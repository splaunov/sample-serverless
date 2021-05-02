# Sample project demonstrating AWS serverless services usage

This simple project showcases the task of generating welcome messages to the
new users of some Internet service.
New user registration events are coming through an SNS topic.
Welcome messages are generated according to the following template:  
`Hi {user} and welcome. {user1}, {user2} and {user3} also joined recently.`  
Generated welcome messages are then sent to the REST API of some external
notification service.

The implemented service comprises one SQS queue to store incoming messages
while processing, and AWS Lambda doing all the rest.
A little complication to the design is the necessity to keep the state for the recent user's list.
To manage the state a simple JSON file on an S3 bucket is used. This is the simplest and adequate
solution for the task as there are no requirements for the accuracy of the recent users' list.
So it is acceptable to just overwrite the list without any synchronisation overhead.

A simple optimisation technique was used to minimise the cost of S3 calls. The JSON file is
being read and updated only if it becomes stale. This optimisation logic is covered in this test:
`RecentUsersCollectorTest.OptimizeStoreCommunications`.

The following technologies were used in the project:

Technologies used:

1. Kotlin + Spring Boot.
2. AWS SQS - to store messages while processing them.
3. AWS Lambda - for running the service logic.
4. AWS S3 - to store state between the service invocations.
5. AWS CloudWatch - to store and analyse the services’ logs.
6. Terraform - as a universal IaC tool.
7. Gradle - as a build tool.
8. Testcontainers and LocalStack - to run integration tests locally.
9. Spring Cloud - to abstract AWS services.
10. Spring WebClient - to call external service in non-blocking way.
11. Kotlin Coroutines - to make asynchronous code simpler.

## How to build

Check the parameters' values in `sender/src/main/resources/application.yml`.  
Set the external service endpoint URL in `pushServiceUri`.

Run this in the project's root directory:

`./gradlew build`

When running tests with IntelliJ IDEA instead of Gradle,
add the following VM options to run/debug configuration or 
configuration's template.
```
-ea  
-XX:TieredStopAtLevel=1  
-Djunit.jupiter.testinstance.lifecycle.default=per_class  
-Djava.util.logging.config.file=$ProjectFileDir$/test-configs/java-util-logging-test.properties  
-Dlogback.configurationFile=$ProjectFileDir$/test-configs/logback-test.xml  
-Dspring.config.location=$ProjectFileDir$/test-configs/
```
## How to deploy and configure

Set inbound messages SNS ARN into `snsArn` variable in the file `iac/variables.tf`.

Run the deployment scripts:
```
terraform init
terraform apply
```
Go to AWS Console, check if everything deployed right.
Activate SQS-Lambda trigger for the sqs-sender queue.