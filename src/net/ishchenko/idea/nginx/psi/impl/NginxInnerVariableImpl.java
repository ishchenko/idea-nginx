package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import net.ishchenko.idea.nginx.annotator.NginxElementVisitor;
import net.ishchenko.idea.nginx.psi.NginxInnerVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NonNls;

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

    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }
}
