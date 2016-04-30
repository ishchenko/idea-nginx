package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import net.ishchenko.idea.nginx.injection.LuaTextEscaper;
import net.ishchenko.idea.nginx.psi.NginxLuaContext;
import org.jetbrains.annotations.NotNull;

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
}
