# Micronaut for Spring Example

This example demonstrates how to use Micronaut for Spring.

The example itself uses only Spring annotations to implement the application and is computed to a Micronaut application at compile time.

To run the application simply do:

```bash
./gradlew bootRun
```

Or via Maven:

```bash
./mvnw compile spring-boot:run
```

To build the application into a native image for GraalVM you can checkout the `graal-native-image` branch:

```
$ git checkout graal-native-image
```

Then if you have Graal installed you can use the `./build-native-image.sh` script, alternatively you can build the application into a native image with Docker:

```
$ docker build . -t greeting-service
$ docker run -p 8080:8080 greeting-service
```

