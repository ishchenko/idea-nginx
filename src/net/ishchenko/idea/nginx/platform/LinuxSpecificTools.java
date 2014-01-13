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

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import net.ishchenko.idea.nginx.configurator.NginxServerDescriptor;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 27.07.2009
 * Time: 1:43:04
 */
public class LinuxSpecificTools implements PlatformDependentTools {

    private static String DEFAULT_PREFIX = "/usr/local/nginx";
    private static String DEFAULT_CONF_PATH = "/conf/nginx.conf";
    private static String DEFAULT_PID_PATH = "/logs/nginx.pid";
    private static String DEFAULT_HTTP_LOG_PATH = "/logs/access.log";
    private static String DEFAULT_ERROR_LOG_PATH = "/logs/error.log";


    public boolean checkExecutable(VirtualFile file) {
        return file != null && !file.isDirectory();
    }

    public boolean checkExecutable(String path) {
        return checkExecutable(LocalFileSystem.getInstance().findFileByPath(path));
    }

    public String[] getStartCommand(NginxServerDescriptor descriptor) {
        String[] commandWithoutGlobals = {descriptor.getExecutablePath(), "-c", descriptor.getConfigPath()};
        String[] globals = getGlobals(descriptor);
        return ArrayUtil.mergeArrays(commandWithoutGlobals, globals);
    }

    public String[] getStopCommand(NginxServerDescriptor descriptor) {
        return new String[]{"/bin/sh", "-c", "kill -TERM `cat " + descriptor.getPidPath() + "`"};
    }

    public String[] getReloadCommand(NginxServerDescriptor descriptor) {
        return new String[]{"/bin/sh", "-c", "kill -HUP `cat " + descriptor.getPidPath() + "`"};
    }

    public String[] getTestCommand(NginxServerDescriptor descriptor) {
        return ArrayUtil.mergeArrays(getStartCommand(descriptor), new String[]{"-t"});
    }

    public NginxServerDescriptor createDescriptorFromFile(VirtualFile file) throws ThisIsNotNginxExecutableException {

        NginxCompileParameters compileParameters = NginxCompileParametersExtractor.extract(file);

        NginxServerDescriptor descriptor = getDefaultDescriptorFromFile(file);
        descriptor.setName("nginx/Unix [" + compileParameters.getVersion() + "]");

        String prefix;
        if (compileParameters.getPrefix() != null) {
            prefix = compileParameters.getPrefix();
        } else {
            prefix = DEFAULT_PREFIX;
        }

        if (compileParameters.getConfigurationPath() != null) {
            descriptor.setConfigPath(compileParameters.getConfigurationPath());
        } else {
            descriptor.setConfigPath(prefix + DEFAULT_CONF_PATH);
        }

        if (compileParameters.getPidPath() != null) {
            descriptor.setPidPath(compileParameters.getPidPath());
        } else {
            descriptor.setPidPath(prefix + DEFAULT_PID_PATH);
        }

        if (compileParameters.getHttpLogPath() != null) {
            descriptor.setHttpLogPath(compileParameters.getHttpLogPath());
        } else {
            descriptor.setHttpLogPath(prefix + DEFAULT_HTTP_LOG_PATH);
        }

        if (compileParameters.getErrorLogPath() != null) {
            descriptor.setErrorLogPath(compileParameters.getErrorLogPath());
        } else {
            descriptor.setErrorLogPath(prefix + DEFAULT_ERROR_LOG_PATH);
        }

        return descriptor;

    }

    public NginxServerDescriptor getDefaultDescriptorFromFile(VirtualFile virtualFile) {
        NginxServerDescriptor result = new NginxServerDescriptor();
        result.setName("nginx/Unix [unknown version]");
        result.setExecutablePath(virtualFile.getPath());
        result.setConfigPath(DEFAULT_PREFIX + DEFAULT_CONF_PATH);
        result.setPidPath(DEFAULT_PREFIX + DEFAULT_PID_PATH);
        result.setHttpLogPath(DEFAULT_PREFIX + DEFAULT_HTTP_LOG_PATH);
        result.setErrorLogPath(DEFAULT_PREFIX + DEFAULT_ERROR_LOG_PATH);
        return result;
    }

    private String[] getGlobals(NginxServerDescriptor descriptor) {
        String globals = "daemon off; pid " + descriptor.getPidPath() + ";"; //we don't want nginx run as daemon process
        if (descriptor.getGlobals().length() > 0) {
            globals = globals + " " + descriptor.getGlobals();
        }
        return new String[]{"-g", globals};
    }
}
