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

First import `sass-embedded-protcol` as dependency into your project. For example using Maven add the following XML to your dependencies section:

```
<dependency>
    <groupId>de.larsgrefer.sass</groupId>
    <artifactId>sass-embedded-host</artifactId>
    <!-- replace x.y.z with the desidered version -->
    <version>x.y.z</version>
</dependency>
```

Now you can instatiate *SassCompiler* to compile your sass file: 

```
SassCompiler sassCompiler = SassCompilerFactory.bundled();
CompileSuccess compileSuccess = sassCompiler.compileFile(new File("src/test/resources/foo/bar.scss"));

//get compiled css
String css = compileSuccess.getCss();
```

## Advanced usage with `WebJars`

WebJars is a fancy project amed to provide client-side libraries distributions as Maven dependency. Using classpath URLs we can read scss files directly from our WebJars dependency. For example let's say we are using the following WebJars dependency for Bootstrap 5.1.3:

```
<dependency>
    <groupId>org.webjars.npm</groupId>
    <artifactId>bootstrap</artifactId>
    <version>5.1.3</version>
</dependency>
```

The following code compiles the main Bootstrap scss file into css:

```
URL resource = getClass().getResource("/META-INF/resources/webjars/bootstrap/5.1.3/scss/bootstrap.scss");
CompileSuccess compileSuccess = sassCompiler.compile(resource);
String css = compileSuccess.getCss(); 
```

