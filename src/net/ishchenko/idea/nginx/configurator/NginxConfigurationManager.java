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

package net.ishchenko.idea.nginx.configurator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;
import net.ishchenko.idea.nginx.NginxBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 21.07.2009
 * Time: 15:10:16
 */
public class NginxConfigurationManager extends BaseConfigurable implements ApplicationComponent {

    private NginxServersConfiguration configuration;
    private NginxConfigurationPanel panel;

    public NginxConfigurationManager(NginxServersConfiguration configuration) {
        this.configuration = configuration;
    }

    public NginxServersConfiguration getConfiguration() {
        return configuration;
    }

    public static NginxConfigurationManager getInstance() {
        return ApplicationManager.getApplication().getComponent(NginxConfigurationManager.class);
    }

    @NotNull
    public String getComponentName() {
        return "nginx.configuration.manager";
    }

    public void disposeComponent() {

    }

    public void initComponent() {

    }

    @Nls
    public String getDisplayName() {
        return NginxBundle.message("config.title");
    }

    public String getHelpTopic() {
        return null;
    }

    public Icon getIcon() {
        return IconLoader.getIcon("/net/ishchenko/idea/nginx/nginx.png");
    }

    public void reset() {
        panel.reset();
    }

    public void apply() throws ConfigurationException {
        panel.apply();
    }

    public boolean isModified() {
        return panel.isModified();
    }

    public JComponent createComponent() {
        if (panel == null) {
            panel = new NginxConfigurationPanel(configuration);
        }
        return panel.getPanel();
    }

    public void disposeUIResources() {
        panel = null;
    }

}