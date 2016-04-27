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

package net.ishchenko.idea.nginx.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import net.ishchenko.idea.nginx.NginxBundle;
import net.ishchenko.idea.nginx.lexer.NginxElementTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 07.07.2009
 * Time: 17:37:49
 */
public class NginxParser implements PsiParser {

    public NginxParser(Project project) {
    }

    @NotNull
    public ASTNode parse(IElementType root, PsiBuilder builder) {

        final PsiBuilder.Marker rootMarker = builder.mark();
        parseFile(builder);
        rootMarker.done(root);
        return builder.getTreeBuilt();

    }

    private void parseFile(PsiBuilder builder) {

        while (!builder.eof()) {

            IElementType token = builder.getTokenType();
            if (token == NginxElementTypes.CONTEXT_NAME || token == NginxElementTypes.DIRECTIVE_NAME) {
                parseDirective(builder);
            } else if (token == NginxElementTypes.CLOSING_BRACE) {
                builder.advanceLexer();
                builder.error(NginxBundle.message("parser.unexpected", "}"));
            } else if (token == NginxElementTypes.OPENING_BRACE) {
                builder.advanceLexer();
                builder.error(NginxBundle.message("parser.unexpected", "{"));
            } else if (token == NginxElementTypes.SEMICOLON) {
                builder.advanceLexer();
                builder.error(NginxBundle.message("parser.unexpected", ";"));
            } else {
                builder.advanceLexer();
            }

        }

    }

    private void parseDirective(PsiBuilder builder) {

        IElementType directiveNameType = builder.getTokenType(); //CONTEXT_NAME or DIRECTIVE_NAME
        PsiBuilder.Marker directiveNameMark = builder.mark();
        PsiBuilder.Marker directiveMark = directiveNameMark.precede();

        builder.advanceLexer();
        directiveNameMark.done(directiveNameType);

        //return on eof
        if (parseDirectiveValues(builder)) {
            directiveMark.done(NginxElementTypes.DIRECTIVE);
            builder.error(NginxBundle.message("parser.eof"));
            return;
        }

        if (builder.getTokenType() == NginxElementTypes.SEMICOLON) {

            builder.advanceLexer();
            directiveMark.done(NginxElementTypes.DIRECTIVE);

        } else if (builder.getTokenType() == NginxElementTypes.OPENING_BRACE) {

            parseContext(builder, directiveMark);

        } else {

            builder.error(NginxBundle.message("parser.orexpected", ';', '{'));
            directiveMark.done(NginxElementTypes.DIRECTIVE);

        }

    }

    private boolean parseDirectiveValues(PsiBuilder builder) {

        while (builder.getTokenType() == NginxElementTypes.DIRECTIVE_VALUE ||
                builder.getTokenType() == NginxElementTypes.DIRECTIVE_STRING_VALUE ||
                builder.getTokenType() == NginxElementTypes.INNER_VARIABLE ||
                builder.getTokenType() == NginxElementTypes.VALUE_WHITE_SPACE) {
            if (builder.eof()) {
                return true;
            }
            if (builder.getTokenType() == NginxElementTypes.DIRECTIVE_VALUE || builder.getTokenType() == NginxElementTypes.INNER_VARIABLE) {
                PsiBuilder.Marker valueMarker = builder.mark();
                //returns true on eof
                if (parseDirectiveValue(builder)) {
                    valueMarker.error("valueMarker error");
                    return true;
                } else {
                    valueMarker.done(NginxElementTypes.COMPLEX_VALUE);
                }
            } else if (builder.getTokenType() == NginxElementTypes.VALUE_WHITE_SPACE) {
                builder.advanceLexer();
            } else if (builder.getTokenType() == NginxElementTypes.DIRECTIVE_STRING_VALUE) {
                PsiBuilder.Marker complexValueMark = builder.mark();
                PsiBuilder.Marker mark = builder.mark();
                builder.advanceLexer();
                mark.done(NginxElementTypes.DIRECTIVE_STRING_VALUE);
                complexValueMark.done(NginxElementTypes.COMPLEX_VALUE);
            }
        }
        return false;
    }

    private boolean parseDirectiveValue(PsiBuilder builder) {
        while (builder.getTokenType() == NginxElementTypes.DIRECTIVE_VALUE || builder.getTokenType() == NginxElementTypes.INNER_VARIABLE) {
            if (builder.eof()) {
                return true;
            }
            PsiBuilder.Marker marker = builder.mark();
            IElementType tokenType = builder.getTokenType();
            builder.advanceLexer();
            marker.done(tokenType);
        }
        return false;
    }

    private void parseContext(PsiBuilder builder, PsiBuilder.Marker directiveMark) {

        PsiBuilder.Marker contextMarker = builder.mark();

        builder.advanceLexer(); // leaving { behind

        while (builder.getTokenType() != NginxElementTypes.CLOSING_BRACE) {

            if (builder.eof()) {
                builder.error(NginxBundle.message("parser.expected", '}'));
                contextMarker.done(NginxElementTypes.CONTEXT);
                directiveMark.done(NginxElementTypes.DIRECTIVE);
                return;
            }

            IElementType token = builder.getTokenType();
            if (token == NginxElementTypes.CONTEXT_NAME || token == NginxElementTypes.DIRECTIVE_NAME) {

                parseDirective(builder);

            } else if (token == NginxElementTypes.LUA_CONTEXT) {

                parseLuaContext(builder);

            } else if (token == NginxElementTypes.CLOSING_BRACE) {

                contextMarker.done(NginxElementTypes.CONTEXT);
                directiveMark.done(NginxElementTypes.DIRECTIVE);
                builder.advanceLexer();
                return;

            } else if (token == NginxElementTypes.OPENING_BRACE) {

                builder.advanceLexer();
                builder.error(NginxBundle.message("parser.unexpected", '{'));

            } else if (token == NginxElementTypes.SEMICOLON) {

                builder.advanceLexer();
                builder.error(NginxBundle.message("parser.unexpected", ';'));

            } else {

                builder.advanceLexer();

            }

        }

        //closing brace found.
        builder.advanceLexer();
        contextMarker.done(NginxElementTypes.CONTEXT);
        directiveMark.done(NginxElementTypes.DIRECTIVE);

    }

    private void parseLuaContext(PsiBuilder builder) {
        IElementType token = builder.getTokenType();
        PsiBuilder.Marker contextMarker = builder.mark();

        while (token != NginxElementTypes.CLOSING_BRACE) {
            builder.advanceLexer();
            token = builder.getTokenType();
        }

        contextMarker.done(NginxElementTypes.LUA_CONTEXT);
        builder.advanceLexer();
    }

}


