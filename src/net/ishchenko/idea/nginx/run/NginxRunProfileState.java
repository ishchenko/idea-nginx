/*
 * Copyright 2009 Max Ishchenko
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

package net.ishchenko.idea.nginx.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunnableState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 14.07.2009
 * Time: 19:19:25
 */
public class NginxRunProfileState implements RunnableState {

    private ExecutionEnvironment environment;
    private Project project;

    public NginxRunProfileState(ExecutionEnvironment environment, Project project) {
        this.environment = environment;
        this.project = project;
    }

    public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {

        NginxProcessHandler handler = NginxProcessHandler.create((NginxRunConfiguration) environment.getRunProfile());

        TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        final ConsoleView console = builder != null ? builder.getConsole() : null;
        if (console != null) {
            console.addMessageFilter(new NginxConsoleFilter(project));
            console.attachToProcess(handler);
            handler.setConsole(console);
        }

        return new NginxExecutionResult(console, handler);

    }

    public ConfigurationPerRunnerSettings getConfigurationSettings() {
        return environment.getConfigurationSettings();
    }

    public RunnerSettings getRunnerSettings() {
        return environment.getRunnerSettings();
    }

}
