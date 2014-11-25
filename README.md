SparkJ: A Java library for interfacing with the SparkCloud
=======

## Usage

SparkJ is a maven project is available from the Maven Central repository. To use SparkJ add this to your pom.xml:

```xml
<dependency>
  <groupId>com.github.grantwest.sparkj</groupId>
  <artifactId>sparkj</artifactId>
  <version>0.0.0</version>
</dependency>
```

Example of calling functions and reading variables:
```java
ISparkDevice dev = new SparkDevice("50df6b0651675496402a02b7", "username", "password");
int funcResult = dev.callFunction("functionName", "argsHere");
String varValue = dev.readVariable("varName");
```

Yup, it's that easy. Right now those are the only 2 features that it supports, but more are on the way.

Lastly, if you are instantiating multiple devices, this method will result in fewer HTTP requests:

```java
  SparkSession session = new SparkSession("username", "password");
  ISparkDevice dev1 = new SparkDevice("50df6b0651675496402a02b7", session);
  ISparkDevice dev2 = new SparkDevice("50df6b0651675496402a02b7", session);
```


## Documentation

Full SparkCloud documentation and examples on how to write your Spark device code can be found here:
http://docs.spark.io/api/

