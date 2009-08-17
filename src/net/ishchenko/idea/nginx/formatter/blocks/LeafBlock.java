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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 20.07.2009
 * Time: 1:15:50
 */
public class LeafBlock implements ASTBlock {

    private static final List<Block> EMPTY_SUB_BLOCKS = new ArrayList<Block>();
    private ASTNode node;
    private Indent indent;

    public LeafBlock(ASTNode node) {
        this.node = node;
        this.indent = Indent.getNormalIndent();
    }

    public ASTNode getNode() {
        return node;
    }

    @NotNull
    public TextRange getTextRange() {
        return node.getTextRange();
    }

    @NotNull
    public List<Block> getSubBlocks() {
        return EMPTY_SUB_BLOCKS;
    }

    public Wrap getWrap() {
        return null;
    }

    public Indent getIndent() {
        return indent;
    }

    public Alignment getAlignment() {
        return null;
    }

    public Spacing getSpacing(Block child1, Block child2) {
        return null;
    }

    @NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(getIndent(), null);
    }

    public boolean isIncomplete() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }


}
