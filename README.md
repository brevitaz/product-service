# Product Search

Product service is an application that uses relevance search capability of Elasticsearch to search and list out products from database.  

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites


- Java v8+

- Elasticsearch v6.6.1

- Gradle build-tool

### Installing

These are the Required installation steps to get a development environment running. follow them step by step. 

##### Upload synonym.txt file.

Upload ``additional-files/synonym.txt`` file into ``config`` directory of Elasticsearch.
  
  
Or, If you're using Elasticsearch Docker container, then run bellow command from additional-files directory in the project. 
    
```
docker cp synonym.txt {container-id}:/usr/share/elasticsearch/config/synonym.txt
```

##### Setup Elasticsearch index.

```
sh setup-index.sh {Elasticsearch-host-ip} {index-name}
```

(Here the first argument is Elasticsearch host IP and second one is index name.By default these will be "http://localhost:9200" and "products" respectively.)
    
##### Setup demo data into Elasticsearch index. 

```
sh setup-data.sh {Elasticsearch-host-ip} {index-name}
```

(Here the first argument is Elasticsearch host IP and second one is index name.By default these will be "http://localhost:9200" and "products" respectively.)
    
##### Set index name in application. 
   
In ``main/resources/application.yml`` file, set value of "*index-name*" according your index name. 
   (Make sure the value of *index-name* and the index name you have set while Setting up index and demo data match.)

##### Set test index name in application. 
In ``test/resources/application.yml`` file, set value of "*index-name*" according your test index name. 
 
##### Download dependency jars.
```
gradle build -x test
```
    
##### Running application. 
    
Run ``ProductSearchDemoApplication.java`` to run the application.  

## Running the tests

For running test, An instance of the Elasticseach should be running. And then run below command to run the test. 

```
gradle test
```

## API Docs 

Api Documents is configured with Swagger and, can be access by http://localhost:8080/swagger/index.html, while application is running.  

## Built With

* [Spring](https://spring.io/docs) - The web framework used
* [Gradle](https://gradle.org/) - Dependency Management
* [Elasticsearch](https://www.elastic.co/guide/index.html) - Search engine/ database 