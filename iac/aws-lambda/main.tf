resource "aws_lambda_function" "lambda" {
  function_name    = var.lambda.name
  handler          = "org.springframework.cloud.function.adapter.aws.FunctionInvoker"
  role             = aws_iam_role.lambda_role.arn
  runtime          = "java11"
  filename         = var.lambda.filename
  source_code_hash = filebase64sha256(var.lambda.filename)
  memory_size      = 1024
  timeout          = 60

  environment {
    variables = {
      JAVA_TOOL_OPTIONS = "-Dlogging.level.me.splaunov.sample.serverless=DEBUG"
    }
  }
}

resource "aws_sqs_queue" "sqs" {
  name                       = var.sqs.name
  visibility_timeout_seconds = 360
}

resource "aws_lambda_event_source_mapping" "event_source_mapping" {
  event_source_arn = aws_sqs_queue.sqs.arn
  enabled          = false
  function_name    = aws_lambda_function.lambda.function_name
  batch_size       = 1

}

resource "aws_iam_role" "lambda_role" {
  name               = "${var.lambda.name}-role"
  assume_role_policy = <<-EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
        "Action": "sts:AssumeRole",
        "Effect": "Allow",
        "Principal": {
            "Service": "lambda.amazonaws.com"
        }
    }
  ]
}
EOF
}

resource "aws_cloudwatch_log_group" "lambda_logging" {
  name              = "/aws/lambda/${var.lambda.name}"
  retention_in_days = 1
}

resource "aws_iam_role_policy" "lambda_role_policy" {
  name   = "${var.lambda.name}-role-policy"
  policy = data.aws_iam_policy_document.policy_document_lambda_permissions.json
  role   = aws_iam_role.lambda_role.id
}

data "aws_iam_policy_document" "policy_document_lambda_permissions" {
  statement {
    effect = "Allow"

    actions = [
      "logs:CreateLogStream",
      "logs:CreateLogGroup",
      "logs:PutLogEvents",
    ]
    resources = [
    "arn:aws:logs:*:*:*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "sqs:GetQueueAttributes",
      "sqs:ChangeMessageVisibility",
      "sqs:ReceiveMessage",
      "sqs:DeleteMessage",
    ]
    resources = [
    "arn:aws:sqs:*:*:*"]
  }

  statement {
    effect = "Allow"

    actions = [
      "s3:*",
    ]
    resources = [
    "arn:aws:s3:::*"]
  }

}
