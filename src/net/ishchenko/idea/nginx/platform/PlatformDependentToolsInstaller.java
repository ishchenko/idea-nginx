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

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ComponentConfig;
import com.intellij.openapi.components.ex.ComponentManagerEx;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 27.07.2009
 * Time: 1:39:15
 */
public class PlatformDependentToolsInstaller implements ApplicationComponent {

    public PlatformDependentToolsInstaller() {

        //installing a component programmatically

        ComponentConfig config = new ComponentConfig();

        IdeaPluginDescriptor ideaPluginDescriptor = PluginManager.getPlugin(PluginId.getId("ideanginx"));

        config.interfaceClass = PlatformDependentTools.class.getName();
        if (SystemInfo.isLinux) {
            config.implementationClass = LinuxSpecificTools.class.getName();
        } else if (SystemInfo.isWindows) {
            config.implementationClass = WindowsSpecificTools.class.getName();
        } else if (SystemInfo.isMac) {
            config.implementationClass = LinuxSpecificTools.class.getName(); // looks like, it works
        } else {
            throw new RuntimeException("I really am sorry, but your operating system is not known to mankind."); // ;)
        }

        ((ComponentManagerEx) ApplicationManager.getApplication()).registerComponent(config, ideaPluginDescriptor);

    }

    @NotNull
    public String getComponentName() {
        return "nginx.platform.tools.installer";
    }

    public void disposeComponent() {

    }

    public void initComponent() {

    }
}
