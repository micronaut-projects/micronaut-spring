# Micronaut for Spring


[![Maven Central](https://img.shields.io/maven-central/v/io.micronaut.spring/micronaut-spring-annotation.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.micronaut.spring%22%20AND%20a:%22micronaut-spring-annotation%22)
[![Build Status](https://github.com/micronaut-projects/micronaut-spring/workflows/Java%20CI/badge.svg)](https://github.com/micronaut-projects/micronaut-spring/actions)

Micronaut uses Ahead of Time (AOT) compilation to pre-compute your applications requirements at compile time. The result of this is significantly lower memory requirements, faster startup time, and reflection free framework infrastructure.

This project consists of various components that make it easier to:

* Integrate Spring components into a Micronaut application
* Run Spring applications as Micronaut applications
* Expose Micronaut Beans to a Spring Application

To achieve this the project provides the ability to use a subset of the Spring Annotation-Based programming model to build Micronaut applications. The goal is not necessarily to provide an alternative runtime for Spring, but instead to enable the ability to build libraries that work with both Spring and Micronaut.

[![Micronaut for Spring](https://img.youtube.com/vi/JvzD2SEw0-E/0.jpg)](https://www.youtube.com/watch?v=JvzD2SEw0-E)

See the [User Guide](https://micronaut-projects.github.io/micronaut-spring/latest/guide/index.html) and [Example Project](https://github.com/micronaut-projects/micronaut-spring/tree/master/examples/greeting-service) for more information on how to use this project.

## Snapshots and Releases

Snaphots are automatically published to [JFrog OSS](https://oss.jfrog.org/artifactory/oss-snapshot-local/) using [Github Actions](https://github.com/micronaut-projects/micronaut-spring/actions).

See the documentation in the [Micronaut Docs](https://docs.micronaut.io/latest/guide/index.html#usingsnapshots) for how to configure your build to use snapshots.

Releases are published to JCenter and Maven Central via [Github Actions](https://github.com/micronaut-projects/micronaut-spring/actions).

A release is performed with the following steps:

* [Edit the version](https://github.com/micronaut-projects/micronaut-spring/edit/master/gradle.properties) specified by `projectVersion` in `gradle.properties` to a semantic, unreleased version. Example `1.0.0`
* [Create a new release](https://github.com/micronaut-projects/micronaut-spring/releases/new). The Git Tag should start with `v`. For example `v1.0.0`.
* [Monitor the Workflow](https://github.com/micronaut-projects/micronaut-spring/actions?query=workflow%3ARelease) to check it passed successfully.
* Celebrate!
