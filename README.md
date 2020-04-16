# Simple Kinesis Consumer

## Pre-requisite Software Installations

* JAVA 8 
* Gradle
* AWS CLI 
* Acquire temporary AWS credentials via AWS STS. For instance, [gimme-aws-creds.](https://github.com/Nike-Inc/gimme-aws-creds)


## Build
```shell script
$ gradle clean jar
```
 

## Run

```shell script
$ java -jar simple-kinesis-consumer.jar -streamName <STREAM NAME>
``` 


Optional arguments
```shell script
$ java -jar simple-kinesis-consumer.jar -streamName <STREAM NAME> -streamRegion us-east-1 -profile default
``` 

Default arguments: 
* region=us-east-1 
* AWS credentials profile = default 