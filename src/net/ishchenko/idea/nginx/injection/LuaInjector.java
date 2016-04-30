package net.ishchenko.idea.nginx.injection;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import net.ishchenko.idea.nginx.psi.NginxLuaContext;
import org.jetbrains.annotations.NotNull;

public class LuaInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof NginxLuaContext) {
            LanguageFileType maybeLuaType = (LanguageFileType)FileTypeManager.getInstance().getStdFileType("Lua");
            if (maybeLuaType.getLanguage().getID().compareToIgnoreCase("Lua") == 0) {
                int start = host.getText().indexOf("{") + 1;
                injectionPlacesRegistrar.addPlace(maybeLuaType.getLanguage(), new TextRange(start, host.getTextLength()), null, null);
            }
        }
    }
}
