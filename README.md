# Micronaut for Spring


[![Build Status](https://travis-ci.org/micronaut-projects/micronaut-spring.svg?branch=master)](https://travis-ci.org/micronaut-projects/micronaut-spring)

Micronaut uses Ahead of Time (AOT) compilation to pre-compute your applications requirements at compile time. The result of this is significantly lower memory requirements, faster startup time, and reflection free framework infrastructure.

This project consists of various components that make it easier to:

* Integrate Spring components into a Micronaut application
* Run Spring applications as Micronaut applications
* Expose Micronaut Beans to a Spring Application

To achieve this the project provides the ability to use a subset of the Spring Annotation-Based programming model to build Micronaut applications. The goal is not necessarily to provide an alternative runtime for Spring, but instead to enable the ability to build libraries that work with both Spring and Micronaut.

[![Micronaut for Spring](https://img.youtube.com/vi/JvzD2SEw0-E/0.jpg)](https://www.youtube.com/watch?v=JvzD2SEw0-E)

See the [User Guide](https://micronaut-projects.github.io/micronaut-spring/latest/guide/index.html) and [Example Project](https://github.com/micronaut-projects/micronaut-spring/tree/master/examples/greeting-service) for more information on how to use this project.
