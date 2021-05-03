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