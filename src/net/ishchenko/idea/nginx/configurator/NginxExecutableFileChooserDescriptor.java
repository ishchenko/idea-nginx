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
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import net.ishchenko.idea.nginx.platform.PlatformDependentTools;

public class NginxExecutableFileChooserDescriptor extends FileChooserDescriptor {

    PlatformDependentTools pdt;

    public NginxExecutableFileChooserDescriptor() {
        super(true, false, false, false, false, false);
        pdt = ApplicationManager.getApplication().getComponent(PlatformDependentTools.class);
    }

    @Override
    public boolean isFileSelectable(VirtualFile file) {
        boolean superDecision = super.isFileSelectable(file);
        return superDecision && pdt.checkExecutable(file);
    }
}
