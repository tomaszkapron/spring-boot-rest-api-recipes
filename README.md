# spring-boot-rest-api-recipes
A Simple RESTful API using Spring Boot, Gradle, Hibernate, H2 database, Spring Data JPA.

An implementaion of [Hyperskill](https://hyperskill.org) Recipes project.

## Overview

This project built using **Java** and the following tools:
- [Spring Boot](https://spring.io/projects/spring-boot) as server side framework
- [Maven](https://maven.apache.org/) as build automation tool
- [Hibernate](https://hibernate.org/) as ORM / JPA implementation
- [H2](https://h2database.com/) as database implementation
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) as the top layer over Hibernate

## Installation 
Clone repository
```
  git clone https://github.com/tomaszkapron/spring-boot-rest-api-recipes
```

Build and run (If working on Windows use gradlew.bat scripts instead)
```
  gradlew build
  gradlew run
```
The app will start running at http://localhost:8881

## H2 database console
H2 database console is available under http://localhost:8881/h2

## Endpoints

| Method | Url | Decription |
| ------ | --- | ---------- |
| POST   |/api/register      | registers an user |
| GET    |/api/recipe/{id}   | returns recipe with given id |
| GET    |/api/recipe/search | returns recipes with given param name and/or category |
| POST   |/api/recipe/new    | saves given recipe; returns assigned id |
| PUT    |/api/recipe/{id}   | updates recipe with given id |
| DELETE |/api/recipe/{id}   | deletes recipe with given id|
