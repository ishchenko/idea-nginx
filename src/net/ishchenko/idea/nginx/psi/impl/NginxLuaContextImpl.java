package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import net.ishchenko.idea.nginx.NginxKeywordsManager;
import net.ishchenko.idea.nginx.injection.LuaTextEscaper;
import net.ishchenko.idea.nginx.lexer.NginxElementTypes;
import net.ishchenko.idea.nginx.psi.NginxComplexValue;
import net.ishchenko.idea.nginx.psi.NginxContext;
import net.ishchenko.idea.nginx.psi.NginxDirectiveName;
import net.ishchenko.idea.nginx.psi.NginxLuaContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.ishchenko.idea.nginx.psi.impl.NginxDirectiveImpl.DIRECTIVE_VALUE_TOKENS;

public class NginxLuaContextImpl extends NginxElementImpl implements NginxLuaContext {
    public NginxLuaContextImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isValidHost() {
        return true;
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        return null;
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new LuaTextEscaper(this);
    }

    @NotNull
    @Override
    public String getNameString() {
        return getText();
    }

    @NotNull
    @Override
    public NginxDirectiveName getDirectiveName() {
        // TODO don't extend NginxDirectiveName
        return (NginxDirectiveName) this;
    }

    @Nullable
    @Override
    public NginxContext getDirectiveContext() {
        ASTNode contextNode = getNode().findChildByType(NginxElementTypes.CONTEXT);
        return contextNode != null ? (NginxContext) contextNode.getPsi() : null;
    }

    @Nullable
    @Override
    public NginxContext getParentContext() {
        ASTNode parentNode = getNode().getTreeParent();
        if (parentNode.getPsi() instanceof NginxContext) {
            return (NginxContext) parentNode.getPsi();
        } else {
            return null;
        }
    }

    @NotNull
    @Override
    public List<NginxComplexValue> getValues() {
        ArrayList<NginxComplexValue> result = new ArrayList<>();
        for (ASTNode value : getNode().getChildren(DIRECTIVE_VALUE_TOKENS)) {
            result.add((NginxComplexValue) value.getPsi());
        }
        return result;
    }

    @Override
    public boolean isInChaosContext() {
        return getParentContext() != null && NginxKeywordsManager.CHAOS_DIRECTIVES.contains(getParentContext().getDirective().getNameString());
    }

    @Override
    public boolean hasContext() {
        return getDirectiveContext() != null;
    }
}
