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

package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import net.ishchenko.idea.nginx.annotator.NginxElementVisitor;
import net.ishchenko.idea.nginx.psi.NginxDirective;
import net.ishchenko.idea.nginx.psi.NginxDirectiveValue;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 09.07.2009
 * Time: 21:02:29
 */
public class NginxDirectiveValueImpl extends NginxElementImpl implements NginxDirectiveValue {

    public NginxDirectiveValueImpl(ASTNode node) {
        super(node);
    }

    public NginxDirective getDirective() {
        return ((NginxComplexValueImpl) getNode().getTreeParent().getPsi()).getDirective();
    }


    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof NginxElementVisitor) {
            ((NginxElementVisitor) visitor).visitDirectiveValue(this);
        } else {
            visitor.visitElement(this);
        }
    }

}