# bionic.js Java runtime

[![javadoc](https://javadoc.io/badge2/srl.forge/bionic-js/javadoc.svg)](https://javadoc.io/doc/srl.forge/bionic-js)

![Build bionic.js Java](https://github.com/Forge-Srl/bionic-js-runtime-java/workflows/Build%20bionic.js%20Java/badge.svg?branch=main)

This is the Java runtime library used by bionic.js (for more information see the [main repository](https://github.com/Forge-Srl/bionic-js))

## Installation

Add this to your pom.xml:
```xml
<dependency>
  <groupId>srl.forge</groupId>
  <artifactId>bionic-js</artifactId>
  <version>0.2.0</version>
</dependency>
```

Also, you must add a [JJBridge engine](https://github.com/Forge-Srl/jjbridge-api#installation) for JavaScript code execution,
for example [JJBridge V8 engine](https://github.com/Forge-Srl/jjbridge-engine-v8).

## Usage
The full javadoc is available at <https://www.javadoc.io/doc/srl.forge/bionic-js/latest/index.html>.

### Setup
To ensure bionic.js runs properly you must set the JavaScript engine. Just add this at the beginning of your program:
```java
BjsProject.setJsEngine(new V8Engine()); // In this case we are using JJBridge V8 engine
```

Also, if you need to enable the JavaScript inspector add:
```java
BjsProject.enableInspector(9876); // Or any other free IP port on your machine
```

## License

See the [LICENSE](LICENSE.md) file for license rights and limitations (MIT).