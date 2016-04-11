package net.ishchenko.idea.nginx.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

%%

%class _NginxLexer
%implements FlexLexer
%final
%unicode
%function advance
%type IElementType
%eof{ return;
%eof}

LineTerminator = [\r\n]
WhiteSpace = [ \t]
AnySpace = {LineTerminator} | {WhiteSpace} | [\f]
//tokens to be highlighted
SpecialDirectives = "http" | "server" | "location" | "if" | "upstream" | "events" | "types" | "charset_map" | "limit_except" | "geo" | "map" | "mail" | "imap"
LuaDirectives = "init_by_lua_block" | "log_by_lua_block" | "upstream_by_lua_block" | "content_by_lua_block" | "access_by_lua_block" | "rewrite_by_lua_block" | "body_filter_by_lua_block" | "header_filter_by_lua_block" | "init_worker_by_lua_block" | "set_by_lua_block" | "ssl_by_lua_block"
ValueStart = [^{};\n\r \t\f#$']

LineComment = {WhiteSpace}* "\#" (.* | {LineTerminator})

%state DIRECTIVE_VALUE

%%

<YYINITIAL> {
    {SpecialDirectives}                        {yybegin(DIRECTIVE_VALUE); return NginxElementTypes.CONTEXT_NAME; }
    {LuaDirectives}                            {yybegin(DIRECTIVE_VALUE); return NginxElementTypes.LUA_CONTEXT; }
    [^{};\n\r \t\f'#]+                         {yybegin(DIRECTIVE_VALUE); return NginxElementTypes.DIRECTIVE_NAME; }
    "}"                                        {return NginxElementTypes.CLOSING_BRACE; }
}

<DIRECTIVE_VALUE> {
    "$" [_a-z]*              {return NginxElementTypes.INNER_VARIABLE;}
    {ValueStart}+("$"[^_a-z])?     {
                                                //the idea is to distinguish:
                                                // 1) asd$ as value "asd$"
                                                // 2) asd$qwe as value "asd" and variable "$qwe"
                                                //thus, I include dollar and tail and cut the tail
                                                //if you find any easier solution - advise me, please
                                                int indexOfDollar = yytext().toString().indexOf("$");
                                                if (indexOfDollar >= 0) {
                                                    yypushback(yylength() - indexOfDollar - 1);
                                                }

                                                return NginxElementTypes.DIRECTIVE_VALUE;
                                            }
    ' ~'                                    {return NginxElementTypes.DIRECTIVE_STRING_VALUE;}
    {AnySpace}+ {ValueStart} {yypushback(1); return NginxElementTypes.VALUE_WHITE_SPACE;}
    {AnySpace}+ [$]          {yypushback(1); return NginxElementTypes.VALUE_WHITE_SPACE;}
    "{"                      {yybegin(YYINITIAL); return NginxElementTypes.OPENING_BRACE; }
    "}"                      {yybegin(YYINITIAL); return NginxElementTypes.CLOSING_BRACE; }
    ";"                      {yybegin(YYINITIAL); return NginxElementTypes.SEMICOLON;}
}

{AnySpace}+         {return NginxElementTypes.WHITE_SPACE;}
{LineComment}       {return NginxElementTypes.COMMENT;}
.                   {return NginxElementTypes.BAD_CHARACTER;}


