package io.micronaut.spring.boot;

import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.cli.CommandLine;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.context.event.EventListener;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ApplicationRunnerListener {

    private final CommandLine commandLine;
    private final List<CommandLineRunner> commandLineRunnerList;
    private final List<ApplicationRunner> applicationRunnerList;

    public ApplicationRunnerListener(
            CommandLine commandLine,
            List<CommandLineRunner> commandLineRunnerList,
            List<ApplicationRunner> applicationRunnerList) {
        this.commandLine = commandLine;
        this.commandLineRunnerList = commandLineRunnerList;
        this.applicationRunnerList = applicationRunnerList;
    }

    @EventListener
    public void onStartup(StartupEvent startupEvent) {
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
