package net.ishchenko.idea.nginx.injection;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import net.ishchenko.idea.nginx.psi.NginxLuaContext;
import org.jetbrains.annotations.NotNull;

public class LuaTextEscaper extends LiteralTextEscaper<NginxLuaContext> {
    public LuaTextEscaper(@NotNull NginxLuaContext host) {
        super(host);
    }

    @Override
    public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
        outChars.append(myHost.getText(), rangeInsideHost.getStartOffset(), rangeInsideHost.getEndOffset());
        return true;
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
        int j = offsetInDecoded + rangeInsideHost.getStartOffset();
        if (j < rangeInsideHost.getStartOffset()) {
            j = rangeInsideHost.getStartOffset();
        }
        if (j > rangeInsideHost.getEndOffset()) {
            j = rangeInsideHost.getEndOffset();
        }
        return j;
    }

    @Override
    public boolean isOneLine() {
        return false;
    }
}
