# Svinted

## Prerequisites

- PostgreSQL database (14.8)
  - url: `jdbc:postgresql://localhost:5432/vinted`
  - username: `postgres`
  - password: `postgres`
- Java 19

## Build

```bash
git clone https://github.com/Enterprise-Intellijence/EnterpriseIntellijence.git
cd EnterpriseIntellijence
mvn spring-boot:run
```

## Populate database

We've created an endpoint to populate the database with some fake data. 

You can access it at the following url: <http://localhost:8080/api/v1/demo>

If you want to log in with a user created by this endpoint, the credentials are:

username: 'username<user_number>'
password: 'password<user_number>'

## Basepath setup

we're assuming that you're using the default port (8080) and that you're running the application locally.

we've hardcoded the basepath in the android application with in variable called `BASE_PATH` in the `BasePath.kt` file. 

You can find it [here](https://github.com/Enterprise-Intellijence/android-app/blob/main/app/src/main/java/com/enterprise/android_app/controller/BasePath.kt), make sure to change it to your local ip address.

## API documentation

You can find the API documentation prodived by swagger [here](http://localhost:8080/swagger-ui/index.html) once the application is running.

[random link](https://www.youtube.com/watch?v=w7i4amO_zaE)
