ManyWho Twilio Service
======================

This service allows you to integrate your flows with Twilio. Currently the service allows your flow to send and receive
SMS and MMS messages.

## Usage

The latest tagged version of the Twilio service will always be deployed to our shared services platform, which is 
accessible at https://services.manywho.com/api/twilio/2.

If you need to run your own instance of the service (e.g. for compliance reasons), it's easy to spin up following these
instructions:

### Building

To build the service, you will need to have Apache Ant, Maven 3 and a Java 8 implementation installed (OpenJDK and the Oracle Java
SE are both supported).

You will need to generate a configuration file for the service by running the provided `build.xml` script with Ant, and 
passing in a valid URL to a Redis instance:

```bash
$ ant -Dredis.url=redis.company.net
```

Now you can build the runnable shaded JAR:

```bash
$ mvn clean package
```

### Running

The service is a Jersey JAX-RS application, that by default is run under the Grizzly2 server on port 8080 (if you use 
the packaged JAR).

#### Defaults

Running the following command will start the service listening on `0.0.0.0:8080/api/twilio/2`:

```bash
$ java -jar target/twilio-2.0-SNAPSHOT.jar
```

#### Custom Port

You can specify a custom port to run the service on by passing the `server.port` property when running the JAR. The
following command will start the service listening on port 9090 (`0.0.0.0:9090/api/twilio/2`):

```bash
$ java -Dserver.port=9090 -jar target/twilio-2.0-SNAPSHOT.jar
```

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).