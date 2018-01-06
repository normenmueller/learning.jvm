#!/bin/bash
sbt package
spark-submit --class "learning.spark.hello.Hello" --master "local" \
  target/scala-2.11/hello_2.11-0.1.jar
