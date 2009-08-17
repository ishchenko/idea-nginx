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

import com.intellij.execution.ExecutionResult;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 21.07.2009
 * Time: 0:11:08
 */
public class NginxExecutionResult implements ExecutionResult {

    public static final String RELOAD_ACTION_ID = "nginx.reload";

    private final ExecutionConsole console;
    private final ProcessHandler handler;
    private AnAction[] actions;

    public NginxExecutionResult(final ExecutionConsole console, ProcessHandler processHandler) {
        this.console = console;
        handler = processHandler;

        ActionManager actionManager = ActionManager.getInstance();
        AnAction reloadAction = actionManager.getAction(RELOAD_ACTION_ID);
        actions = new AnAction[]{reloadAction};

    }

    public ExecutionConsole getExecutionConsole() {
        return console;
    }

    public AnAction[] getActions() {
        return actions;
    }

    public ProcessHandler getProcessHandler() {
        return handler;
    }

}

