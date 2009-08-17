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

package net.ishchenko.idea.nginx.annotator;

import com.intellij.psi.PsiElementVisitor;
import net.ishchenko.idea.nginx.psi.*;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 14.07.2009
 * Time: 16:07:31
 */
public class NginxElementVisitor extends PsiElementVisitor {

    public void visitDirective(final NginxDirective node) {
        visitElement(node);
    }

    public void visitDirectiveName(final NginxDirectiveName node) {
        visitElement(node);
    }

    public void visitDirectiveValue(final NginxDirectiveValue node) {
        visitElement(node);
    }

    public void visitContext(NginxContext node) {
        visitElement(node);
    }

    public void visitComplexValue(final NginxComplexValue node) {
        visitElement(node);
    }

    public void visitInnerVariable(NginxInnerVariable node) {
        visitElement(node);
    }
}
