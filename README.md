# wildfireBack

wildfire simulation server.
Simulation setup is made in src/main/resources/application.properties.

## Prerequisites

 * [java > 21](https://www.oracle.com/ca-fr/java/technologies/downloads/)
 * [maven > 3](https://maven.apache.org/download.cgi)

## How to run

```sh
mvn clean install
mvn spring-boot:run
```
To check that the server correctly run, the [/simulation/configuration](http://localhost:8080/simulation/configuration) endpoint should print simulation's initial configuration.
Something like :
```JSON
{
  "forestWidth": 20,
  "forestHeight": 10,
  "firePropagationRate": 50,
  "simDuration": 8,
  "firstBurningTrees": [
    [5, 5],
    [4, 5],
    [8, 9],
    [5, 6]
  ]
}
```