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
import com.intellij.psi.tree.TokenSet;
import net.ishchenko.idea.nginx.NginxKeywordsManager;
import net.ishchenko.idea.nginx.annotator.NginxElementVisitor;
import net.ishchenko.idea.nginx.lexer.NginxElementTypes;
import net.ishchenko.idea.nginx.psi.NginxComplexValue;
import net.ishchenko.idea.nginx.psi.NginxContext;
import net.ishchenko.idea.nginx.psi.NginxDirective;
import net.ishchenko.idea.nginx.psi.NginxDirectiveName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 09.07.2009
 * Time: 21:02:29
 */
public class NginxDirectiveImpl extends NginxElementImpl implements NginxDirective {

    private final TokenSet DIRECTIVE_VALUE_TOKENS = TokenSet.create(NginxElementTypes.COMPLEX_VALUE);

    public NginxDirectiveImpl(ASTNode node) {
        super(node);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof NginxElementVisitor) {
            ((NginxElementVisitor) visitor).visitDirective(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @NotNull
    public String getNameString() {
        return getDirectiveName().getText();
    }

    @NotNull
    public NginxDirectiveName getDirectiveName() {
        ASTNode nameNode = getNode().findChildByType(NginxElementTypes.CONTEXT_NAME);
        if (nameNode == null) {
            nameNode = getNode().findChildByType(NginxElementTypes.DIRECTIVE_NAME);
        }
        //is npe really probable here?
        return (NginxDirectiveName) nameNode.getPsi();
    }

    @Nullable
    public NginxContext getDirectiveContext() {
        ASTNode contextNode = getNode().findChildByType(NginxElementTypes.CONTEXT);
        return contextNode != null ? (NginxContext) contextNode.getPsi() : null;
    }

    @Nullable
    public NginxContext getParentContext() {
        ASTNode parentNode = getNode().getTreeParent();
        if (parentNode.getPsi() instanceof NginxContext) {
            return (NginxContext) parentNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    public List<NginxComplexValue> getValues() {
        ArrayList<NginxComplexValue> result = new ArrayList<NginxComplexValue>();
        for (ASTNode value : getNode().getChildren(DIRECTIVE_VALUE_TOKENS)) {
            result.add((NginxComplexValue) value.getPsi());
        }
        return result;
    }

    public boolean isInChaosContext() {
        return getParentContext() != null && NginxKeywordsManager.CHAOS_DIRECTIVES.contains(getParentContext().getDirective().getNameString());
    }

    public boolean hasContext() {
        return getDirectiveContext() != null;
    }
}

