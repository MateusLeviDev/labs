#!/bin/bash
set -x

BUCKET="sales-info"

awslocal s3 mb s3://$BUCKET

awslocal s3 cp /etc/localstack/init/ready.d/2025_sales.csv s3://$BUCKET/
awslocal s3 cp /etc/localstack/init/ready.d/2025_sales_2.csv s3://$BUCKET/
