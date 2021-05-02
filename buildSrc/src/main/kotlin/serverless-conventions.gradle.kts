/*
Plugin with configuration and dependencies for modules, implementing serverless functions.
 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.PropertiesFileTransformer

plugins {
    id("common-conventions")
    id("org.springframework.boot")
    id("org.springframework.boot.experimental.thin-launcher")
    id("com.github.johnrengelman.shadow")
}

val startClass = "me.splaunov.sample.serverless.AppConfig"
springBoot {
    mainClass.set(startClass)
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("aws")
        dependencies {
            exclude(
                dependency("org.springframework.cloud:spring-cloud-function-web")
            )
        }
        // Required for Spring
        mergeServiceFiles()
        append("META-INF/spring.handlers")
        append("META-INF/spring.schemas")
        append("META-INF/spring.tooling")
        transform(PropertiesFileTransformer::class.java) {
            paths = listOf("META-INF/spring.factories")
            mergeStrategy = "append"
        }
        manifest {
            attributes.putIfAbsent("Main-Class", startClass)
        }
    }
}

tasks {
    assemble {
        dependsOn(shadowJar, thinJar)
    }
}

dependencies {
    implementation(project(":adapter-aws"))

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.4.4")
}
