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
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.PsiErrorElement;
import net.ishchenko.idea.nginx.lexer.NginxElementTypes;
import net.ishchenko.idea.nginx.psi.NginxComplexValue;
import net.ishchenko.idea.nginx.psi.NginxContext;
import net.ishchenko.idea.nginx.psi.NginxDirective;
import net.ishchenko.idea.nginx.psi.NginxPsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 13.07.2009
 * Time: 23:00:41
 */
public class NginxBlock implements ASTBlock {

    private static final Spacing ONE_LINE_BREAK_SPACING = Spacing.createSpacing(0, 0, 1, true, 50);

    private ASTNode node;
    private Indent indent;
    private Alignment alignment;
    private List<Block> blocks;

    public NginxBlock(ASTNode node, Indent indent, Alignment alignment) {
        this.node = node;
        this.indent = indent;
        this.alignment = alignment;
    }

    @NotNull
    public List<Block> getSubBlocks() {

        if (blocks == null) {

            blocks = new ArrayList<Block>();

            boolean isFileNode = getNode().getPsi() instanceof NginxPsiFile;

            for (ASTNode childNode : getNode().getChildren(null)) {

                if (!(childNode.getPsi() instanceof PsiWhiteSpace) && !(childNode.getPsi() instanceof PsiErrorElement)) { //just omitting whitespace tokens

                    Indent childIndent = Indent.getNoneIndent();

                    if (!isFileNode && (childNode.getPsi() instanceof NginxDirective || childNode.getPsi() instanceof PsiComment)) {
                        childIndent = Indent.getNormalIndent();
                    }

                    blocks.add(new NginxBlock(childNode, childIndent, null));

                }
            }
        }
        return blocks;
    }

    public Spacing getSpacing(Block genericLeftBlock, Block genericRightBlock) {

        NginxBlock leftBlock = (NginxBlock) genericLeftBlock;
        NginxBlock rightBlock = (NginxBlock) genericRightBlock;

        PsiElement leftPsi = leftBlock.getNode().getPsi();
        PsiElement rightPsi = rightBlock.getNode().getPsi();

        if (rightPsi instanceof NginxDirective) {
            return ONE_LINE_BREAK_SPACING;
        }

        if (rightBlock.getNode().getElementType() == NginxElementTypes.CLOSING_BRACE) {
            return ONE_LINE_BREAK_SPACING;
        }

        if (leftBlock.getNode().getElementType() == NginxElementTypes.OPENING_BRACE) {
            return ONE_LINE_BREAK_SPACING;
        }

        return null;
    }

    public ASTNode getNode() {
        return node;
    }

    @NotNull
    public TextRange getTextRange() {
        return node.getTextRange();
    }

    public Wrap getWrap() {
        return null;
    }

    public Indent getIndent() {
        return indent;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public boolean isIncomplete() {
        return false;
    }

    public boolean isLeaf() {
        return getNode().getFirstChildNode() == null || getNode().getPsi() instanceof NginxComplexValue;
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {

        if (getNode().getPsi() instanceof NginxContext) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }
}
