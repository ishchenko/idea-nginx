package net.ishchenko.idea.nginx.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import net.ishchenko.idea.nginx.annotator.NginxElementVisitor;
import net.ishchenko.idea.nginx.psi.NginxComplexValue;
import net.ishchenko.idea.nginx.psi.NginxDirective;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 15.08.2009
 * Time: 20:01:27
 */
public class NginxComplexValueImpl extends NginxElementImpl implements NginxComplexValue {

    public NginxComplexValueImpl(@NotNull ASTNode node) {
        super(node);
    }


    public NginxDirective getDirective() {
        return (NginxDirective) getNode().getTreeParent().getPsi();
    }

    public boolean isFirstValue() {
        return !(getNode().getTreePrev().getTreePrev().getPsi() instanceof NginxComplexValue);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof NginxElementVisitor) {
            ((NginxElementVisitor) visitor).visitComplexValue(this);
        } else {
            visitor.visitElement(this);
        }
    }
}
