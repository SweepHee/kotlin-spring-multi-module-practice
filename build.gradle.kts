import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    java
    id("org.springframework.boot") version "2.7.14" apply false
    id("io.spring.dependency-management") version "1.1.2" apply false
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21" apply false
    kotlin("plugin.jpa") version "1.8.21"
    kotlin("plugin.noarg") version "1.8.21"
    kotlin("plugin.allopen") version "1.8.21"
    id("com.google.protobuf") version "0.8.14"

}


allprojects{
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<JavaCompile>{
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    repositories {
        mavenCentral()
    }
}

subprojects{

    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "kotlin")




    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }


}


project(":gRPC-core") {

    apply(plugin = "com.google.protobuf")
    apply(plugin = "kotlin-allopen")
    apply(plugin = "kotlin-noarg")


    allOpen {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.Embeddable")
        annotation("jakarta.persistence.MappedSuperclass")
    }

    noArg {
        annotation("jakarta.persistence.Entity")
        annotation("jakarta.persistence.Embeddable")
        annotation("jakarta.persistence.MappedSuperclass")
    }

    val grpcVersion = "3.21.9"
    val grpcKotlinVersion = "1.2.1"
    val grpcProtoVersion = "1.44.1"

    sourceSets{
        getByName("main"){
            java {
                srcDirs(
                    "build/generated/source/proto/main/java",
                    "build/generated/source/proto/main/kotlin"
                )
            }
        }
    }

    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:$grpcVersion"
        }
        plugins {
            id("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:$grpcProtoVersion"
            }
            id("grpckt") {
                artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    id("grpc")
                    id("grpckt")
                }
                it.builtins {
                    id("kotlin")
                }
            }
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-jooq")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        implementation ("io.grpc:grpc-kotlin-stub:${grpcKotlinVersion}")
        implementation ("io.grpc:grpc-protobuf:${grpcProtoVersion}")
        implementation ("com.google.protobuf:protobuf-kotlin:${grpcVersion}")
        implementation ("io.grpc:grpc-stub:1.40.1")

        runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    }

}
