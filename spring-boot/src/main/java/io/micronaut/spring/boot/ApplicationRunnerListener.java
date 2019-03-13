/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.spring.boot;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.cli.CommandLine;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.context.event.EventListener;

import javax.inject.Singleton;
import java.util.List;

/**
 * Runs {@link ApplicationRunner} and {@link CommandLineRunner} instances on startup.
 *
 * @author graemerocher
 * @since 1.0
 */
@Singleton
public class ApplicationRunnerListener {

    private final CommandLine commandLine;
    private final List<CommandLineRunner> commandLineRunnerList;
    private final List<ApplicationRunner> applicationRunnerList;

    /**
     * Default constructor.
     * @param commandLine The command line
     * @param commandLineRunnerList The command runner list
     * @param applicationRunnerList The application runner list
     */
    protected ApplicationRunnerListener(
            CommandLine commandLine,
            List<CommandLineRunner> commandLineRunnerList,
            List<ApplicationRunner> applicationRunnerList) {
        this.commandLine = commandLine;
        this.commandLineRunnerList = commandLineRunnerList;
        this.applicationRunnerList = applicationRunnerList;
    }

    /**
     * Invokes on startup and executes the listeners.
     * @param startupEvent The startup event
     */
    @EventListener
    protected void onStartup(StartupEvent startupEvent) {
        for (CommandLineRunner runner : commandLineRunnerList) {
            try {
                runner.run(commandLine.getRawArguments());
            } catch (Exception e) {
                throw new IllegalStateException("Failed to execute ApplicationRunner", e);
            }
        }

        for (ApplicationRunner applicationRunner : applicationRunnerList) {
            try {
                applicationRunner.run(new DefaultApplicationArguments(commandLine.getRawArguments()));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to execute ApplicationRunner", e);
            }
        }
    }
}
