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

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.*;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import net.ishchenko.idea.nginx.NginxBundle;
import net.ishchenko.idea.nginx.configurator.NginxServerDescriptor;
import net.ishchenko.idea.nginx.platform.PlatformDependentTools;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 25.07.2009
 * Time: 4:15:37
 */
public class NginxReloadAction extends AnAction {

    public void actionPerformed(final AnActionEvent event) {

        Project project = PlatformDataKeys.PROJECT.getData(event.getDataContext());

        FileDocumentManager.getInstance().saveAllDocuments(); //todo: save configs only

        RunContentDescriptor runContentDescriptor = getRunContentDescriptor(event);
        if (runContentDescriptor != null) {
            if (runContentDescriptor.getProcessHandler() == null) {
                return;
            }

            final ConsoleView console = (ConsoleView) runContentDescriptor.getExecutionConsole();

            try {
                //the action will be disabled when there's no process handler, so no npe here. i hope...
                NginxProcessHandler processHandler = (NginxProcessHandler) runContentDescriptor.getProcessHandler();
                NginxServerDescriptor descriptor = processHandler.getDescriptor();

                //evil user may have deleted either server configuration
                validateDescriptor(descriptor);

                //it can be omitted for windows, but in linux we see silence if configuration file is invalid on reload
                //as reload is done by SIGHUP
                testConfig(descriptor, console, project);

                doReload(descriptor, console, project);

            } catch (ReloadException e) {
                console.print(e.getMessage() + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            } catch (Exception e) {
                console.print(e.getClass() + " " + e.getMessage() + "\n", ConsoleViewContentType.ERROR_OUTPUT);
            }

        }


    }

    private void validateDescriptor(NginxServerDescriptor descriptor) throws ReloadException {

        VirtualFile executable = LocalFileSystem.getInstance().findFileByPath(descriptor.getExecutablePath());
        if (executable == null) {
            throw new ReloadException(NginxBundle.message("run.error.badpath"));
        }

    }

    private void testConfig(final NginxServerDescriptor descriptor, final ConsoleView console, Project project) throws ReloadException, IOException {

        VirtualFile executableFile = LocalFileSystem.getInstance().findFileByPath(descriptor.getExecutablePath());
        PlatformDependentTools pdt = ApplicationManager.getApplication().getComponent(PlatformDependentTools.class);
        String[] testCommand = pdt.getTestCommand(descriptor);
        int exitValue = runAndGetExitValue(testCommand, new File(executableFile.getParent().getPath()), console);

        if (exitValue != 0) {
            throw new ReloadException(NginxBundle.message("run.validationfailed"));
        }

    }

    private void doReload(NginxServerDescriptor descriptor, ConsoleView console, Project project) throws ReloadException, IOException {

        VirtualFile executableFile = LocalFileSystem.getInstance().findFileByPath(descriptor.getExecutablePath());
        PlatformDependentTools pdt = ApplicationManager.getApplication().getComponent(PlatformDependentTools.class);
        String[] reloadCommand = pdt.getReloadCommand(descriptor);
        int exitValue = runAndGetExitValue(reloadCommand, new File(executableFile.getParent().getPath()), console);

        if (exitValue != 0) {
            throw new ReloadException(NginxBundle.message("run.validationfailed"));
        }

    }

    private int runAndGetExitValue(String[] testCommand, File dir, final ConsoleView console) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(testCommand);
        builder.directory(dir);
        OSProcessHandler osph = new OSProcessHandler(builder.start(), StringUtil.join(testCommand, " "));
        osph.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(final ProcessEvent event, Key outputType) {
                ConsoleViewContentType contentType = ConsoleViewContentType.SYSTEM_OUTPUT;
                if (outputType == ProcessOutputTypes.STDERR) {
                    contentType = ConsoleViewContentType.ERROR_OUTPUT;
                }
                console.print(event.getText(), contentType);
            }
        });
        osph.startNotify();
        osph.waitFor();
        osph.destroyProcess(); //is that needed if waitFor has returned?
        return osph.getProcess().exitValue();
    }


    @Override
    public void update(AnActionEvent e) {
        e.getPresentation().setEnabled(isEnabled(e));
    }

    private boolean isEnabled(AnActionEvent e) {
        ProcessHandler processHandler = getProcessHandler(e);
        return processHandler != null && !(processHandler.isProcessTerminated() || processHandler.isProcessTerminating());
    }

    @Nullable
    private RunContentDescriptor getRunContentDescriptor(AnActionEvent e) {

        //magic copied from com.intellij.execution.actions.StopAction
        RunContentDescriptor runContentDescriptor = RunContentManager.RUN_CONTENT_DESCRIPTOR.getData(e.getDataContext());
        if (runContentDescriptor == null) {
            Project project = PlatformDataKeys.PROJECT.getData(e.getDataContext());
            if (project != null) {
                runContentDescriptor = ExecutionManager.getInstance(project).getContentManager().getSelectedContent();
            }
        }
        return runContentDescriptor;

    }

    @Nullable
    private ProcessHandler getProcessHandler(AnActionEvent e) {
        RunContentDescriptor rcd = getRunContentDescriptor(e);
        return rcd != null ? rcd.getProcessHandler() : null;
    }

    private static class ReloadException extends Exception {

        private ReloadException(String message) {
            super(message);
        }
    }

}
