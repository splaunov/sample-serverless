
plugins {
    id("java-test-fixtures")
    id("common-conventions")
}

val verSpring = "5.3.4"
val verTestcontainers = "1.15.2"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-function-adapter-aws")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.7.0")
    implementation("com.amazonaws:aws-lambda-java-serialization:1.0.0")
    runtimeOnly("io.awspring.cloud:spring-cloud-starter-aws:2.3.1")

    testFixturesImplementation("org.springframework:spring-context:$verSpring")
    testFixturesImplementation("org.springframework:spring-test:$verSpring")
    testFixturesImplementation("org.testcontainers:testcontainers:$verTestcontainers")
    testFixturesImplementation( "org.testcontainers:localstack:$verTestcontainers")
    testFixturesImplementation("org.testcontainers:junit-jupiter:$verTestcontainers")

}