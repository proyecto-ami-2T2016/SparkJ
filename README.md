SparkJ: Control the ParticleCloud w/ Java
=======

## Usage

SparkJ is a maven project and is available from the Maven Central repository. To use SparkJ add this to your pom.xml:

```xml
<dependency>
  <groupId>com.github.grantwest.sparkj</groupId>
  <artifactId>sparkj</artifactId>
  <version>0.0.2</version>
</dependency>
```

Example of calling functions and reading variables:
```java
SparkDevice device = new SparkDevice("50df6b0651675496402a02b7", "username", "password");
int funcResult = device.callFunction("functionName", "argsHere");
String varValue = device.readVariable("varName");
```

Example of subscribing to a device's event stream:
```java
SparkDevice device = new SparkDevice("50df6b0651675496402a02b7", "username", "password");
SparkEventStream stream = device.eventStream((event) -> System.out.println(event.toString()));
//You will need to wait here because the event stream is asynchronous
```
It is also possible to get other event streams by using the myEvents() and publicEvents() methods of the SparkEventStream class.

Lastly, if you are instantiating many devices, this method will result in fewer HTTP requests:

```java
  SparkSession session = new SparkSession("username", "password");
  SparkDevice dev1 = new SparkDevice("50df6b0651675496402a02b7", session);
  SparkDevice dev2 = new SparkDevice("5496402a02b000df6b06516c", session);
```


## Documentation

Full ParticleCloud documentation and examples on how to write your Particle device code can be found here:
https://docs.particle.io/reference/api/

