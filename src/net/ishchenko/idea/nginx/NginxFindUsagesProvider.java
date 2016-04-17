package net.ishchenko.idea.nginx;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import net.ishchenko.idea.nginx.psi.NginxInnerVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NginxFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new NginxWordsScanner();
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof NginxInnerVariable;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement psiElement) {
        if (psiElement instanceof NginxInnerVariable) return "variable";
        return "";
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement psiElement) {
        if (psiElement instanceof NginxInnerVariable) {
            NginxInnerVariable var = (NginxInnerVariable)psiElement;
            return var.getName();
        }
        return "";
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement psiElement, boolean b) {
        if (psiElement instanceof NginxInnerVariable) {
            NginxInnerVariable var = (NginxInnerVariable)psiElement;
            return var.getName();
        }
        return "";
    }

}
