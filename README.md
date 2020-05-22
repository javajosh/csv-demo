# CSV Demo

Josh Rehman, Jacksonville FL, 5/22/2020

## Problem statement

"For the files in CSV format, write a program that will read the content of the file and separate enrollees by insurance company in its own file. Additionally, sort the contents of each file by last and first name (ascending).  Lastly, if there are duplicate User Ids for the same Insurance Company, then only the record with the highest version should be included. The following data points are included in the file:

  - User Id (string)
  - First and Last Name (string)
  - Version (integer)
  - Insurance Company (string)"

## Implementation notes

Java is an okay pick for this kind of problem, even though it is quite awkward with file handling. Python is prbably best (or Groovy if you really want to run on the JVM). But Java has been around forever and it has good CSV libraries. For the purpose of a demo I'm going to write a one-shot command line program using relatively low-level Java, based on Dropwizard to minimize my project setup pain. It's also an opportunity to try a new library (to me), 

I am going to make some assumptions about the problem:

  - the output should include all fields, even if redundant, 
  - we should ignore (or log) invalid data. 
  - if two rows have the same version, we'll take the last one (and log the problem.)



## How to start the JavaJosh CSV Demo application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/csv-demo-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

----------------------------------------------------------------------------------------------------------------

Enrollees example:

### Input:

input.csv

| userId  | lastName | firstName |version | company |
| ------- |:--------:| ---------:|-------:|--------:|
| a321    | smith    | john		 | 1      | acme    |
| a321    | smith    | john		 | 2      | acme    |
| a321    | smith    | john		 | 3      | acme    |
| b131    | jones    | alice	 | 1      | aetna   |
| b131    | jones    | alice	 | 2      | aetna   |
| b131    | jones    | alice	 | 3      | aetna   |
| e131    | mann     | hugh  	 | 1      | acme    |


### Output: 

acme.csv

| userId  | lastName | firstName |version | company |
| ------- |:--------:| ---------:|-------:|--------:|
| e131    | mann     | hugh  	 | 1      | acme    |
| a321    | smith    | john		 | 3      | acme    |


aetna.csv

| userId  | lastName | firstName |version | company |
| ------- |:--------:| ---------:|-------:|--------:|
| b131    | jones    | alice	 | 3      | aetna   |
