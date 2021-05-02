/*
Plugin with common configuration and dependencies for all projects' modules.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    kotlin("jvm")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

repositories {
    mavenLocal()
    mavenCentral()
}

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

val intTest = task<Test>("intTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperties(
        "java.util.logging.config.file" to
                "${project.parent?.projectDir}/test-configs/java-util-logging-test.properties",
        "logback.configurationFile" to
                "${project.parent?.projectDir}/test-configs/logback-test.xml",
        "junit.jupiter.testinstance.lifecycle.default" to "per_class",
        "spring.config.location" to "${project.parent?.projectDir}/test-configs/"
    )

    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
    reports {
        junitXml.isEnabled = false
        html.isEnabled = true
    }
}

tasks.check { dependsOn(intTest) }

val verJUnit = "5.7.1"
val verKotest = "4.4.3"
val verMockk = "1.11.0"

dependencies {
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2020.0.2"))
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation(platform("org.springframework.data:spring-data-releasetrain:Neumann-SR8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework.cloud:spring-cloud-function-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework:spring-webflux")
    implementation("io.projectreactor.netty:reactor-netty-http")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$verJUnit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$verJUnit")
    testImplementation("io.mockk:mockk:$verMockk")
    testImplementation("io.kotest:kotest-assertions-core:$verKotest")

    intTestImplementation(kotlin("test-junit5"))
    intTestImplementation("org.jetbrains.kotlin:kotlin-reflect")
    intTestImplementation("io.mockk:mockk:$verMockk")
    intTestImplementation("io.kotest:kotest-assertions-core:$verKotest")
    intTestImplementation("io.kotest:kotest-assertions-json:$verKotest")
    intTestImplementation("org.springframework.boot:spring-boot-starter-test")
    intTestImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    intTestImplementation("org.slf4j:jcl-over-slf4j:1.7.30")
    intTestImplementation(testFixtures(project(":adapter-aws")))
}
