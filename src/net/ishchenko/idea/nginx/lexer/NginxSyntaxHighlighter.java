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

package net.ishchenko.idea.nginx.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 06.07.2009
 * Time: 16:40:05
 */
public class NginxSyntaxHighlighter extends SyntaxHighlighterBase {

    private Lexer lexer;

    private final TextAttributesKey[] BAD_CHARACTER_KEYS = new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};
    private final Map<IElementType, TextAttributesKey> colors = new HashMap<IElementType, TextAttributesKey>();

    public NginxSyntaxHighlighter() {

        lexer = new FlexAdapter(new _NginxLexer((java.io.Reader) null));

        colors.put(NginxElementTypes.BAD_CHARACTER, HighlighterColors.BAD_CHARACTER);
        colors.put(NginxElementTypes.COMMENT, DefaultLanguageHighlighterColors.BLOCK_COMMENT);

        colors.put(NginxElementTypes.CONTEXT_NAME, DefaultLanguageHighlighterColors.KEYWORD);
        colors.put(NginxElementTypes.DIRECTIVE_STRING_VALUE, DefaultLanguageHighlighterColors.STRING);
        colors.put(NginxElementTypes.INNER_VARIABLE, DefaultLanguageHighlighterColors.NUMBER);

    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return lexer;
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {

        TextAttributesKey[] textAttributesKeys = {colors.get(iElementType)};
        if (textAttributesKeys == null) {
            textAttributesKeys = BAD_CHARACTER_KEYS;
        }
        return textAttributesKeys;

    }

}
