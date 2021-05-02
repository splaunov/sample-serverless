locals {
  services = {
    sender    = {}
  }
}

module "lambda_service" {
  for_each = local.services
  source   = "./aws-lambda"
  lambda = {
    name     = "lambda-${each.key}"
    filename = "${path.root}/${each.key}/build/libs/${each.key}-1.0-SNAPSHOT-aws.jar"
  }
  sqs = {
    name = "sqs-${each.key}"
  }
}

resource "aws_sns_topic_subscription" "sns_subscription" {
  for_each             = local.services
  endpoint             = module.lambda_service[each.key].sqs.arn
  protocol             = "sqs"
  raw_message_delivery = true
  topic_arn            = var.snsArn
}

resource "aws_sqs_queue_policy" "sqs_policy" {
  for_each  = local.services
  queue_url = module.lambda_service[each.key].sqs.id

  policy = <<-EOF
{
  "Version": "2012-10-17",
  "Id": "sqspolicy",
  "Statement": [
    {
      "Sid": "First",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "sqs:SendMessage",
      "Resource": "${module.lambda_service[each.key].sqs.arn}",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "${var.snsArn}"
        }
      }
    }
  ]
}
EOF
}

resource "aws_s3_bucket" "s3_bucket" {
  bucket = "sample-serverless"
}
