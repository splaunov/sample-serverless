variable "lambda" {
  type = object({
    name : string
    filename : string
  })
}

variable "sqs" {
  type = object({
    name : string
  })
}

output "sqs" {
  value = {
    id  = aws_sqs_queue.sqs.id
    arn = aws_sqs_queue.sqs.arn
  }
}
