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

package net.ishchenko.idea.nginx.formatter.blocks;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
//import com.intellij.psi.PsiComment;
//import com.intellij.psi.PsiErrorElement;
//import com.intellij.psi.PsiWhiteSpace;
//import com.intellij.psi.impl.source.tree.ChameleonElement;
//import com.intellij.psi.tree.IChameleonElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 13.07.2009
 * Time: 23:00:41
 */
public class NginxBlock implements ASTBlock {

    private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    private static final Spacing NO_SPACING_WITH_NEWLINE = Spacing.createSpacing(0, 0, 0, true, 1);
    private static final Spacing COMMON_SPACING = Spacing.createSpacing(1, 1, 0, true, 100);

    private ASTNode node;
    private Indent indent;
    private Wrap wrap;
    private Alignment alignment;
    private List<Block> blocks;

    public NginxBlock(ASTNode node, Wrap wrap, Indent indent, Alignment alignment) {
        this.node = node;
        this.wrap = wrap;
        this.indent = indent;
        this.alignment = alignment;
    }

    @NotNull
    public List<Block> getSubBlocks() {
        return blocks;
    }

    public ASTNode getNode() {
        return node;
    }

    @NotNull
    public TextRange getTextRange() {
        return node.getTextRange();
    }

    public Wrap getWrap() {
        return wrap;
    }

    public Indent getIndent() {
        return this.indent;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public boolean isIncomplete() {
        return isIncomplete(getNode());
    }

    public boolean isIncomplete(@NotNull final ASTNode node) {
//        if (node.getElementType() instanceof IChameleonElementType) return false;
//        ASTNode lastChild = node.getLastChildNode();
//        while (lastChild != null && !(lastChild.getElementType() instanceof IChameleonElementType) &&
//                (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
//            lastChild = lastChild.getTreePrev();
//        }
//        return lastChild != null && !(lastChild instanceof ChameleonElement) &&
//                (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
        //todo: formatter stuff
        return true;
    }

    public boolean isLeaf() {
        return getNode().getFirstChildNode() == null;
    }

    public Spacing getSpacing(Block child1, Block child2) {
        return null;  
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }
}
