# dart-sass-java

[![Java CI with Gradle](https://github.com/larsgrefer/dart-sass-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/larsgrefer/dart-sass-java/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/larsgrefer/dart-sass-java/branch/master/graph/badge.svg?token=WPUF2AWJVF)](https://codecov.io/gh/larsgrefer/dart-sass-java)

This is a Java library that implements the host side of the [Embedded Sass
protocol](https://github.com/sass/embedded-protocol)

Releases can be found on [Maven Central](https://mvnrepository.com/artifact/de.larsgrefer.sass).

## `sass-embedded-protcol`

This module contains the Java DTO-classes for the [Embedded Sass protocol](https://github.com/sass/embedded-protocol) as produced by protoc.

## `sass-embedded-host`

This module contains the actual host-implementation including the embedded compiler https://github.com/sass/dart-sass-embedded.

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
URL resource = getClass().getResource("/META-INF/resources/webjars/bootstrap/5.1.3/scss/bootstrap.scss");
CompileSuccess compileSuccess = sassCompiler.compile(resource);
String css = compileSuccess.getCss(); 
```

Files form WebJars can be also imported directly from SCSS files. Let's say for example that we would like to customize Bootstrap with our favorite colors. We could create a custom SCSS file (for example: src/main/resources/custom-bootstrap.scss) with this content:

```scss
//VARIABLE OVERRIDING
$primary: #712cf9;
$secondary: #f19027;

//INCLUDING MAIN BOOTSTRAP SCSSS
@import "META-INF/resources/webjars/bootstrap/5.1.3/scss/bootstrap.scss";
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
