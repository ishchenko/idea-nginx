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
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import net.ishchenko.idea.nginx.NginxBundle;
import net.ishchenko.idea.nginx.configurator.NginxServerDescriptor;
import net.ishchenko.idea.nginx.configurator.NginxServersConfiguration;
import net.ishchenko.idea.nginx.platform.PlatformDependentTools;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 24.07.2009
 * Time: 2:42:59
 */
public class NginxProcessHandler extends OSProcessHandler {

    public static final Logger LOG = Logger.getInstance("#net.ishchenko.idea.nginx.run.NginxProcessHandler");

    private NginxServerDescriptor descriptorCopy;
    private ConsoleView console;

    private NginxProcessHandler(Process process, String commandLine, NginxServerDescriptor descriptorCopy) {

        super(process, commandLine);

        this.descriptorCopy = descriptorCopy;

    }

    public static NginxProcessHandler create(NginxRunConfiguration config) throws ExecutionException {

        String descriptorId = config.getServerDescriptorId();

        NginxServerDescriptor descriptor = NginxServersConfiguration.getInstance().getDescriptorById(descriptorId);
        if (descriptor == null) {
            throw new ExecutionException(NginxBundle.message("run.error.servernotfound"));
        }

        NginxServerDescriptor descriptorCopy = descriptor.clone();

        VirtualFile executableVirtualFile = LocalFileSystem.getInstance().findFileByPath(descriptorCopy.getExecutablePath());
        if (executableVirtualFile == null || executableVirtualFile.isDirectory()) {
            throw new ExecutionException(NginxBundle.message("run.error.badpath", descriptorCopy.getExecutablePath()));
        }

        PlatformDependentTools pdt = ServiceManager.getService(PlatformDependentTools.class);

        ProcessBuilder builder = new ProcessBuilder(pdt.getStartCommand(descriptorCopy));
        builder.directory(new File(executableVirtualFile.getParent().getPath()));
        try {
            return new NginxProcessHandler(builder.start(), StringUtil.join(pdt.getStartCommand(descriptorCopy), " "), descriptorCopy.clone());
        } catch (IOException e) {
            throw new ExecutionException(e.getMessage(), e);
        }

    }


    @Override
    public void destroyProcess() {
        if (tryToStop()) {
            super.destroyProcess();
        } else {

            console.print("Could not stop process.\n", ConsoleViewContentType.ERROR_OUTPUT);

        }
    }

    private boolean tryToStop() {

        PlatformDependentTools pdt = ServiceManager.getService(PlatformDependentTools.class);
        VirtualFile executableVirtualFile = LocalFileSystem.getInstance().findFileByPath(descriptorCopy.getExecutablePath());
        String[] stopCommand = pdt.getStopCommand(descriptorCopy);
        ProcessBuilder builder = new ProcessBuilder(stopCommand);
        builder.directory(new File(executableVirtualFile.getParent().getPath()));
        boolean successfullyStopped = false;
        try {
            OSProcessHandler osph = new OSProcessHandler(builder.start(), StringUtil.join(stopCommand, " "));
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
            successfullyStopped = osph.getProcess().exitValue() == 0;

        } catch (IOException e) {
            LOG.error(e);
        }

        return successfullyStopped;

    }

    public void setConsole(ConsoleView console) {
        this.console = console;
    }

    public NginxServerDescriptor getDescriptor() {
        return descriptorCopy;
    }
}
