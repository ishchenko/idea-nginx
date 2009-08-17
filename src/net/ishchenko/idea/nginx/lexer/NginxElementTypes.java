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

import com.intellij.lang.Language;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import net.ishchenko.idea.nginx.NginxLanguage;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 06.07.2009
 * Time: 15:49:41
 */
public interface NginxElementTypes {

    IFileElementType FILE = new IFileElementType(Language.findInstance(NginxLanguage.class));

    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;
    IElementType WHITE_SPACE = TokenType.WHITE_SPACE;

    IElementType COMMENT = new NginxElementType("COMMENT");

    //non-lexed types
    IElementType DIRECTIVE = new NginxElementType("DIRECTIVE");
    IElementType CONTEXT = new NginxElementType("CONTEXT");
    IElementType COMPLEX_VALUE = new NginxElementType("COMPLEX_VALUE");

    //lexed types
    IElementType CONTEXT_NAME = new NginxElementType("CONTEXT_NAME");
    IElementType DIRECTIVE_NAME = new NginxElementType("DIRECTIVE_NAME");
    IElementType DIRECTIVE_VALUE = new NginxElementType("DIRECTIVE_VALUE");
    IElementType DIRECTIVE_STRING_VALUE = new NginxElementType("DIRECTIVE_STRING_VALUE");
    IElementType VALUE_WHITE_SPACE = new NginxElementType("VALUE_WHITE_SPACE");
    IElementType INNER_VARIABLE = new NginxElementType("INNER_VARIABLE");
    IElementType OPENING_BRACE = new NginxElementType("OPENING_BRACE");
    IElementType CLOSING_BRACE = new NginxElementType("CLOSING_BRACE");
    IElementType SEMICOLON = new NginxElementType("SEMICOLON");

    TokenSet WHITE_SPACES = TokenSet.create(WHITE_SPACE);
    TokenSet COMMENTS = TokenSet.create(COMMENT);
    TokenSet STRINGS = TokenSet.create(DIRECTIVE_STRING_VALUE);


}


