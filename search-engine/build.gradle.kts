plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0" apply false
    id("org.openapi.generator") version "7.1.0"
}

group = "com.memdb"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("io.minio:minio:8.5.9")

    //LOMBOK
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    //OPENAPI
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch:3.2.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    //KAFKA
    implementation("org.springframework.kafka:spring-kafka:3.1.2")
    implementation("org.apache.kafka:kafka-clients:3.6.0")
    
    //TESTS
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.google.code.gson:gson:2.10")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    runtimeOnly("com.h2database:h2:2.2.220")
}

tasks.withType<JavaCompile>() {
    options.compilerArgs.add("-parameters")
}

openApiGenerate {
    generatorName.set("spring")
    validateSpec.set(true)
    inputSpec.set("$projectDir/src/main/resources/search-engine-openapi.yml") // path to spec
    outputDir.set("${layout.buildDirectory.asFile.get()}/generated/sources/annotationProcessor/java/main")
    apiPackage.set("com.memdb.controller")
    modelPackage.set("com.memdb.model.dto")
    generateApiTests.set(false)
    generateModelTests.set(false)
    generateModelDocumentation.set(false)

    globalProperties.set(
        mapOf(
            "generateSupportingFiles" to "false",
            "models" to "", // generate all models
            "apis" to "", // generate all apis
        ),
    )

    configOptions.set(
        mapOf(
            "documentationProvider" to "none",
            "generatedConstructorWithRequiredArgs" to "true",
            "openApiNullable" to "false",
            "useSpringBoot3" to "true",
            "java8" to "false",
            "skipDefaultInterface" to "true",
            "interfaceOnly" to "true",
            "serviceInterface" to "true",
            "useTags" to "true",
            "fullJavaUtil" to "false",
            "hideGenerationTimestamp" to "true",
            "sourceFolder" to "",
            "library" to "spring-boot",
            "serializationLibrary" to "jackson",
        ),
    )
}

sourceSets.main {
    java.srcDirs("${layout.buildDirectory.asFile.get()}/generated/sources/annotationProcessor/java/main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    dependsOn("openApiGenerate")
}