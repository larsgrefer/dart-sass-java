# dart-sass-java

[![Java CI with Gradle](https://github.com/larsgrefer/dart-sass-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/larsgrefer/dart-sass-java/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/larsgrefer/dart-sass-java/branch/3.x/graph/badge.svg?token=WPUF2AWJVF)](https://codecov.io/gh/larsgrefer/dart-sass-java)

This is a Java library that implements the host side of the [Embedded Sass
protocol](https://github.com/sass/sass/blob/main/spec/embedded-protocol.md)

Releases can be found on [Maven Central](https://mvnrepository.com/artifact/de.larsgrefer.sass).

## Modules

### `sass-embedded-protcol`

This module contains the Java DTO-classes for the [Embedded Sass protocol](https://github.com/sass/sass/blob/main/spec/embedded-protocol.md) as produced by protoc.

### `sass-embedded-host`

This module contains the actual host-implementation.

### `sass-embedded-bundled`

This module contains the embedded dart-sass executables.

### `sass-embedded-spring5`

Spring Framework 5, JavaEE specific classes.

### `sass-embedded-spring6`

Spring Framework 6, JakartaEE specific classes.

## Basic usage

First import `sass-embedded-host` as dependency into your project. For example using Maven add the following XML to your dependencies section:

```xml
<dependency>
    <groupId>de.larsgrefer.sass</groupId>
    <artifactId>sass-embedded-host</artifactId>
    <!-- replace x.y.z with the desidered version -->
    <version>x.y.z</version>
</dependency>
```

Now you can instantiate *SassCompiler* to compile your sass file: 

```java
try (SassCompiler sassCompiler = SassCompilerFactory.bundled()) {
    CompileSuccess compileSuccess = sassCompiler.compileFile(new File("src/main/resources/foo/bar.scss"));

    //get compiled css
    String css = compileSuccess.getCss();
}
```

## Advanced usage with `WebJars`

WebJars is a project aimed to provide client-side libraries distributions as Maven dependency. Using classpath URLs we can read SCSS files directly from our WebJars dependency. For example let's say we are using the WebJars dependency for Bootstrap 5.1.3:

```xml
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>bootstrap</artifactId>
    <version>5.1.3</version>
</dependency>
```

The following code compiles the main Bootstrap SCSS file into css:

```java
URL resource = getClass().getResource("/META-INF/resources/webjars/bootstrap/5.3.0/scss/bootstrap.scss");
CompileSuccess compileSuccess = sassCompiler.compile(resource);
String css = compileSuccess.getCss(); 
```

Files form WebJars can be also imported directly from SCSS files. Let's say for example that we would like to customize Bootstrap with our favorite colors. We could create a custom SCSS file (for example: src/main/resources/custom-bootstrap.scss) with this content:

```scss
//VARIABLE OVERRIDING
$primary: #712cf9;
$secondary: #f19027;

//INCLUDING MAIN BOOTSTRAP SCSSS
@import "META-INF/resources/webjars/bootstrap/5.3.0/scss/bootstrap.scss";
```

To compile the file above we need to register a *WebjarsImporter* into our compiler:

```java
try (SassCompiler sassCompiler = SassCompilerFactory.bundled()) {
    sassCompiler.registerImporter(new WebjarsImporter().autoCanonicalize());

    URL resource = getClass().getResource("/custom-bootstrap.scss");
    CompileSuccess compileSuccess = sassCompiler.compile(resource);

    //custom Bootstrap css
    String css = compileSuccess.getCss();
}
```

## Performance and Thread safety

By default - using `SassCompilerFactory.bundled()` - each created `SassCompiler` gets a fresh `CompilerConnection` based on a newly spawned subprocess of the embedded dart-sass binary.

This has two consequences:

1. Creating a new `SassCompiler` is a rather expensive operation, so try to re-use one (or few) instances instead of creating a new one for each compilation.
2. Make sure to `close()` the `SassCompiler` when you`re done with it, so the subprocess can be stopped and the allocated memory can be freed.

The important parts of the communication with the actual compiler are `synchronized` on the `CompilerConnection` object, so it should be safe to use a single `SassCompiler` instance in a multithreaded environment. For better performance you might want to create multiple instances in order to avoid threads blocking each other, but keep in mind that creating new instances is still an expensive operation.
