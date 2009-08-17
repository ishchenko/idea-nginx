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

package net.ishchenko.idea.nginx;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import net.ishchenko.idea.nginx.configurator.NginxServersConfiguration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 04.07.2009
 * Time: 1:02:46
 */
public final class NginxFileTypeManager implements ApplicationComponent {

    private NginxFileType fileType;

    public static NginxFileTypeManager getInstance() {
        return ApplicationManager.getApplication().getComponent(NginxFileTypeManager.class);
    }

    @NotNull
    @NonNls
    public String getComponentName() {
        return "NginxFileTypeManager";
    }

    public void initComponent() {
        fileType = new NginxFileType(NginxServersConfiguration.getInstance());
        FileTypeManager.getInstance().registerFileType(fileType);
    }

    public void disposeComponent() {
    }

    public NginxFileType getFileType() {
        return fileType;
    }

}

