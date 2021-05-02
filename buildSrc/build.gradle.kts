plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.4.4")
    implementation("org.springframework.boot.experimental:spring-boot-thin-gradle-plugin:1.0.26.RELEASE")
    implementation("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.4.31")
}