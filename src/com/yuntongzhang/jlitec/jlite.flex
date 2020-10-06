package com.yuntongzhang.jlitec;

import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;

%%

%public
%class Lexer
%unicode
%cup
%implements sym
%line
%column
%yylexthrow Exception

%{
    StringBuffer string = new StringBuffer();
    ComplexSymbolFactory symbolFactory;

    public Lexer(java.io.Reader in, ComplexSymbolFactory sf) {
        this(in);
        symbolFactory = sf;
    }

    private Symbol symbol(String name, int sym) {
        Location left = new Location(yyline + 1, yycolumn + 1);
        Location right = new Location(yyline + 1, yycolumn + yylength());
        return symbolFactory.newSymbol(name, sym, left, right);
    }

    private Symbol symbol(String name, int sym, Object val) {
        Location left = new Location(yyline + 1, yycolumn + 1);
        Location right = new Location(yyline + 1, yycolumn + yylength());
        return symbolFactory.newSymbol(name, sym, left, right, val);
    }

    private Symbol symbol(String name, int sym, Object val, int buflength) {
        Location left = new Location(yyline + 1, yycolumn + yylength() - buflength);
        Location right = new Location(yyline + 1, yycolumn + yylength());
        return symbolFactory.newSymbol(name, sym, left, right, val);
    }

    private void error(String message) throws Exception {
        System.out.println("Lexical error at <Line " + (yyline + 1) +
            ", Column " + (yycolumn + 1) + "> : " + message);
        throw new Exception("A Lexical error occurred!");
    }
%}

%eofval{
     return symbolFactory.newSymbol("EOF", EOF,
        new Location(yyline + 1, yycolumn + 1),
        new Location(yyline + 1, yycolumn + 1));
%eofval}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?

Identifier = [a-z][a-zA-Z0-9_]*
ClassName = [A-Z][a-zA-Z0-9_]*
IntegerLiteral = 0 | [1-9][0-9]*
BooleanLiteral = true | false

/* ascii charater in decimal and hex */
AsciiDec = [0-9]{1,3}
AsciiHex = [0-9a-fA-F]{1,2}

%state STRING

%%

<YYINITIAL> {
    /* keywords */
    "class"                 { return symbol("class", CLASS); }
    "this"                  { return symbol("this", THIS); }
    "new"                   { return symbol("new", NEW); }
    "null"                  { return symbol("null", NULL); }
    "if"                    { return symbol("if", IF); }
    "else"                  { return symbol("else", ELSE); }
    "while"                 { return symbol("while", WHILE); }
    "main"                  { return symbol("main", MAIN); }
    "readln"                { return symbol("readln", READLN); }
    "println"               { return symbol("println", PRINTLN); }
    "return"                { return symbol("return", RETURN); }

    /* primitive types */
    "Int"                   { return symbol("int", TYPE_INT); }
    "Bool"                  { return symbol("bool", TYPE_BOOL); }
    "String"                { return symbol("string", TYPE_STRING); }
    "Void"                  { return symbol("void", TYPE_VOID); }

    /* literals */
    {IntegerLiteral}        { return symbol("integerLiteral", INTEGER_LITERAL, new Integer(Integer.parseInt(yytext()))); }
    {BooleanLiteral}        { return symbol("booleanLiteral", BOOLEAN_LITERAL, new Boolean(Boolean.parseBoolean(yytext()))); }
    \"                      { string.setLength(0); yybegin(STRING); }

    /* identifiers */
    {Identifier}            { return symbol("identifier", IDENTIFIER, yytext()); }

    /* class names */
    {ClassName}             { return symbol("cname", CNAME, yytext()); }

    /* operators */
    "="                     { return symbol("assign", ASSIGN); }
    "."                     { return symbol("dot", DOT); }
    "||"                    { return symbol("or", OR); }
    "&&"                    { return symbol("and", AND); }
    "<"                     { return symbol("lt", LT); }
    ">"                     { return symbol("gt", GT); }
    "<="                    { return symbol("leq", LEQ); }
    ">="                    { return symbol("geq", GEQ); }
    "=="                    { return symbol("eq", EQ); }
    "!="                    { return symbol("neq", NEQ); }
    "+"                     { return symbol("plus", PLUS); }
    "-"                     { return symbol("minus", MINUS); }
    "*"                     { return symbol("times", TIMES); }
    "/"                     { return symbol("div", DIV); }
    "!"                     { return symbol("not", NOT); }

    /* separators */
    ";"                     { return symbol("semicolon", SEMICOLON); }
    ","                     { return symbol("comma", COMMA); }
    "("                     { return symbol("(", LPAR); }
    ")"                     { return symbol(")", RPAR); }
    "{"                     { return symbol("{", LBRACE); }
    "}"                     { return symbol("}", RBRACE); }

    /* comments */
    {Comment}               { /* ignore */ }

    /* whitespace */
    {WhiteSpace}            { /* ignore */ }
}


<STRING> {
    \"                      { yybegin(YYINITIAL);
                              return symbol("stringLiteral", STRING_LITERAL, string.toString(), string.length()); }

    [^\n\r\t\b\"\\]+        { string.append(yytext()); }

    \\\"                    { string.append('\"'); }
    \\\\                    { string.append('\\'); }
    \\n                     { string.append('\n'); }
    \\r                     { string.append('\r'); }
    \\t                     { string.append('\t'); }
    \\b                     { string.append('\b'); }

    \\{AsciiDec}            { int val = Integer.parseInt(yytext().substring(1), 10);
                              if (val < 0 || val > 255) error("Decimal <" + yytext() + "> is not a valid ascii value.");
                              string.append((char) val); }
    \\x{AsciiHex}           { int val = Integer.parseInt(yytext().substring(2), 16);
                              string.append((char) val); }
}


/* error fallback */
[^]                         { error("Illegal character <" + yytext() + ">"); }
