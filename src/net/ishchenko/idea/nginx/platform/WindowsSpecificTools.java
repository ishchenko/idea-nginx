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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import net.ishchenko.idea.nginx.configurator.NginxServerDescriptor;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 27.07.2009
 * Time: 1:32:19
 */
public class WindowsSpecificTools implements PlatformDependentTools {

    public static final String DEFAULT_CONF_PATH = "/conf/nginx.conf";
    public static final String DEFAULT_PID_PATH = "/logs/nginx.pid";
    public static final String DEFAULT_HTTP_LOG_PATH = "/logs/access.log";
    public static final String DEFAULT_ERROR_LOG_PATH = "/logs/error.log";

    public static final String[] STOP_COMMAND = new String[]{"-s", "stop"};
    public static final String[] RELOAD_COMMAND = new String[]{"-s", "reload"};
    public static final String[] TEST_COMMAND = new String[]{"-t"};

    public boolean checkExecutable(VirtualFile file) {
        return file != null && !file.isDirectory() && StringUtil.endsWithIgnoreCase(file.getName(), ".exe");
    }

    public boolean checkExecutable(String path) {
        return checkExecutable(LocalFileSystem.getInstance().findFileByPath(path));
    }

    public String[] getStartCommand(NginxServerDescriptor descriptor) {
        String[] commandWithoutGlobals = new String[]{descriptor.getExecutablePath(), "-c", descriptor.getConfigPath()};
        String[] globals = getGlobals(descriptor);
        return ArrayUtil.mergeArrays(commandWithoutGlobals, globals);
    }

    public String[] getStopCommand(NginxServerDescriptor descriptor) {
        return ArrayUtil.mergeArrays(getStartCommand(descriptor), STOP_COMMAND);
    }

    public String[] getReloadCommand(NginxServerDescriptor descriptor) {
        return ArrayUtil.mergeArrays(getStartCommand(descriptor), RELOAD_COMMAND);
    }

    public String[] getTestCommand(NginxServerDescriptor descriptor) {
        return ArrayUtil.mergeArrays(getStartCommand(descriptor), TEST_COMMAND);
    }

    public NginxServerDescriptor createDescriptorFromFile(VirtualFile file) throws ThisIsNotNginxExecutableException {

        NginxCompileParameters compileParameters = NginxCompileParametersExtractor.extract(file);

        NginxServerDescriptor descriptor = getDefaultDescriptorFromFile(file);
        descriptor.setName("nginx/Windows [" + compileParameters.getVersion() + "]");

        String prefix;
        if (compileParameters.getPrefix() != null) {
            prefix = compileParameters.getPrefix();
            if (prefix.equals("")) {
                prefix = file.getParent().getPath();
            }
        } else {
            prefix = file.getParent().getPath(); // There is no default prefix for windows, so let it be current dir
        }

        descriptor.setConfigPath(getPrefixDependentSettings(compileParameters.getConfigurationPath(), prefix, DEFAULT_CONF_PATH));
        descriptor.setPidPath(getPrefixDependentSettings(compileParameters.getPidPath(), prefix, DEFAULT_PID_PATH));

        descriptor.setHttpLogPath(getPrefixDependentSettings(compileParameters.getHttpLogPath(), prefix, DEFAULT_HTTP_LOG_PATH));
        descriptor.setErrorLogPath(getPrefixDependentSettings(compileParameters.getErrorLogPath(), prefix, DEFAULT_ERROR_LOG_PATH));

        return descriptor;

    }

    public NginxServerDescriptor getDefaultDescriptorFromFile(VirtualFile virtualFile) {

        NginxServerDescriptor result = new NginxServerDescriptor();
        result.setName("nginx/Windows [unknown version]");
        result.setExecutablePath(virtualFile.getPath());
        result.setConfigPath(virtualFile.getParent().getPath() + DEFAULT_CONF_PATH);
        result.setPidPath(virtualFile.getParent().getPath() + DEFAULT_PID_PATH);
        result.setHttpLogPath(virtualFile.getParent().getPath() + DEFAULT_HTTP_LOG_PATH);
        result.setErrorLogPath(virtualFile.getParent().getPath() + DEFAULT_ERROR_LOG_PATH);
        return result;

    }

    private String getPrefixDependentSettings(String configurationPath, String prefix, String defaultValue) {

        //this logic is not needed for linux as default prefix exists there
        if (configurationPath != null) {
            if (new File(configurationPath).isAbsolute()) {
                return configurationPath;
            } else {
                String slash = "/";
                if (prefix.endsWith(slash) || configurationPath.startsWith(slash)) {
                    slash = "";
                }
                return prefix + slash + configurationPath;
            }
        } else {
            return prefix + defaultValue;
        }
    }


    private String[] getGlobals(NginxServerDescriptor descriptor) {
        String globals = "pid '" + descriptor.getPidPath() + "';"; //we don't want nginx run as daemon process
        if (descriptor.getGlobals().length() > 0) {
            globals = globals + " " + descriptor.getGlobals();
        }
        return new String[]{"-g", globals};
    }

}
