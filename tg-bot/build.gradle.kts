plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"

    id("org.springframework.boot") version "2.7.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

repositories {
    mavenCentral()
    maven(uri("https://jitpack.io"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val jaicfVersion = "1.3.6"

dependencies {

    implementation("com.just-ai.jaicf:core:$jaicfVersion")
    implementation("com.just-ai.jaicf:jaicp:$jaicfVersion")
    implementation("com.just-ai.jaicf:telegram:$jaicfVersion")
    implementation("com.just-ai.jaicf:mongo:$jaicfVersion")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("io.ktor:ktor-client-serialization:1.5.1")
    implementation("io.ktor:ktor-client-gson:1.5.1")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    jvmToolchain(17)
}
