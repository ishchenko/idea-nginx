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

package net.ishchenko.idea.nginx.platform;

import com.intellij.openapi.vfs.VirtualFile;
import net.ishchenko.idea.nginx.configurator.NginxServerDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 27.07.2009
 * Time: 1:30:36
 */
public interface PlatformDependentTools {

    boolean checkExecutable(VirtualFile file);

    boolean checkExecutable(String path);

    String[] getStartCommand(NginxServerDescriptor descriptor);

    String[] getStopCommand(NginxServerDescriptor descriptor);

    String[] getReloadCommand(NginxServerDescriptor descriptor);

    String[] getTestCommand(NginxServerDescriptor descriptor);

    /**
     * @param file nginx executable
     * @return new NginxServerDescriptor respecting -V output
     * @throws ThisIsNotNginxExecutableException
     *          - when file.getPath() -V can not be executed, or can not be parsed
     */
    NginxServerDescriptor createDescriptorFromFile(VirtualFile file) throws ThisIsNotNginxExecutableException;

    /**
     * @param virtualFile nginx executable
     * @return new NginxServerDescriptor instance ignoring -V output
     */
    NginxServerDescriptor getDefaultDescriptorFromFile(VirtualFile virtualFile);

    class ThisIsNotNginxExecutableException extends Exception {

        public ThisIsNotNginxExecutableException(String message) {
            super(message);
        }

        public ThisIsNotNginxExecutableException(Throwable e) {
            super(e);
        }
    }
}
