# Contributing Code or Documentation to Micronaut

Sign the [Contributor License Agreement (CLA)](https://cla-assistant.io/micronaut-projects/micronaut-project-template). This is required before any of your code or pull-requests are accepted.

## Finding Issues to Work on

If you are interested in contributing to Micronaut and are looking for issues to work on, take a look at the issues tagged with [help wanted](https://github.com/micronaut-projects/micronaut-spring/issues?q=is%3Aopen+is%3Aissue+label%3A%22status%3A+help+wanted%22).

## JDK Setup

Micronaut Spring currently requires JDK 8.

## IDE Setup

Micronaut Spring can be imported into IntelliJ IDEA by opening the `build.gradle` file.

## Docker Setup

Micronaut Spring tests currently require Docker to be installed.

## Running Tests

To run the tests, use `./gradlew check`.

## Building Documentation

The documentation sources are located at `src/main/docs/guide`.

To build the documentation, run `./gradlew publishGuide` (or `./gradlew pG`), then open `build/docs/index.html`

To also build the Javadocs, run `./gradlew docs`.

## Working on the code base

If you use IntelliJ IDEA, you can import the project using the Intellij Gradle Tooling ("File / Import Project" and selecting the "settings.gradle" file).

To get a local development version of Micronaut Spring working, first run the `publishToMavenLocal` task.

```
./gradlew pTML
```

You can then reference the version specified with `projectVersion` in `gradle.properties` in a test project's `build.gradle` or `pom.xml`. If you use Gradle, add the `mavenLocal` repository (Maven automatically does this):

```
repositories {
    mavenLocal()
    mavenCentral()
}
```

## Creating a pull request

Once you are satisfied with your changes:

- Commit your changes in your local branch
- Push your changes to your remote branch on GitHub
- Send us a [pull request](https://help.github.com/articles/creating-a-pull-request)

## Checkstyle

We want to keep the code clean, following good practices about organization, Javadoc, and style as much as possible.

Micronaut Spring uses [Checkstyle](https://checkstyle.sourceforge.io/) to make sure that the code follows those standards. The configuration is defined in `config/checkstyle/checkstyle.xml`. To execute Checkstyle, run:

```
./gradlew <module-name>:checkstyleMain
```

Before starting to contribute new code we recommended that you install the IntelliJ [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin and configure it to use Micronaut's checkstyle configuration file.

IntelliJ will mark in red the issues Checkstyle finds. For example:

![](https://github.com/micronaut-projects/micronaut-core/raw/master/src/main/docs/resources/img/checkstyle-issue.png)

In this case, to fix the issues, we need to:

- Add one empty line before `package` in line 16
- Add the Javadoc for the constructor in line 27
- Add an space after `if` in line 34

The plugin also adds a new tab in the bottom of the IDE to run Checkstyle and show errors and warnings. We recommend that you run the report and fix all issues before submitting a pull request.
