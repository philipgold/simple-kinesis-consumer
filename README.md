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
$ java -jar simple-kinesis-consumer.jar -help
 usage: simple.kinesis.consumer.SimpleConsumer
  -help               print this help message
  -profile            the profile name of the CredentialsProvider for
                      conection to AWS KDS
  -streamName <arg>   the name of the kinesis stream the events are sent to
  -streamRegion       the region of the Kinesis stream

```

Example
```shell script
$ java -jar simple-kinesis-consumer.jar -streamName my-events -streamRegion us-east-1 -profile default
``` 

Default arguments: 
* region=us-east-1 
* AWS credentials profile = default 


### Get aws kinesis list-streams
```shell script
$ aws kinesis list-streams --region us-east-1 --profile default
```