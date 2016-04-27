package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import net.ishchenko.idea.nginx.psi.NginxVariableReference;
import net.ishchenko.idea.nginx.annotator.NginxElementVisitor;
import net.ishchenko.idea.nginx.psi.NginxContext;
import net.ishchenko.idea.nginx.psi.NginxInnerVariable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 14.08.2009
 * Time: 14:31:26
 */
public class NginxInnerVariableImpl extends NginxElementImpl implements NginxInnerVariable {

    public NginxInnerVariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof NginxElementVisitor) {
            ((NginxElementVisitor) visitor).visitInnerVariable(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public String getName() {
        return getText().substring(1);
    }

    @Override
    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public PsiReference getReference() {
        PsiReference[] references = getReferences();
        return references.length == 0 ? null : references[0];
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        Collection<NginxInnerVariable> children = PsiTreeUtil.findChildrenOfType(
                PsiTreeUtil.findFirstParent(this, Conditions.instanceOf(NginxContext.class)),
                NginxInnerVariable.class);
        return children.stream()
                .filter(child -> child.getName().contains(this.getName()))
                .map(child -> new NginxVariableReference(this, new TextRange(0, child.getName().length() + 1)))
                .toArray(PsiReference[]::new);
    }

}
