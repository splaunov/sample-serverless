terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.32"
    }
  }
}

provider "aws" {
  region = "eu-west-1"
}


module "main" {
  source = "./iac"
}