parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

program
    : cool_class+ EOF
    ;

cool_class
    : CLASS type=TYPE (INHERITS superclass=TYPE)? LBRACE (features+=feature SEMICOLON)* RBRACE SEMICOLON
    ;

feature
    : name=ID LPAREN (formals+=formal (COMMA formals+=formal)*)?
        RPAREN COLON returnType=TYPE LBRACE body+=expr RBRACE           # methodDef
    | name=ID COLON type=TYPE (ASSIGN init=expr)?                       # varDef
    ;

formal
    : name=ID COLON type=TYPE
    ;

inited_formal
    : notInitFormal=formal (ASSIGN initExpr=expr)?
    ;

expr
    : caller=expr (AT imposedType=TYPE)? DOT methodName=ID LPAREN
        (args+=expr (COMMA args+=expr)*)? RPAREN                                                # methodCall
    | name=ID LPAREN (args+=expr (COMMA args+=expr)*)? RPAREN                                   # staticMethodCall
    | IF cond=expr THEN then=expr ELSE elseOutcome=expr FI                                      # if
    | WHILE cond=expr LOOP body=expr POOL                                                       # while
    | LBRACE (body+=expr SEMICOLON)+ RBRACE                                                     # instructionBlock
    | LET formals+=inited_formal (COMMA formals+=inited_formal)* IN body=expr                   # letIn
    | CASE cond=expr OF (formals+=formal CASE_THEN then+=expr SEMICOLON)+ ESAC                  # case
    | ID                                                                                        # id
    | INT                                                                                       # int
    | STRING                                                                                    # coolString
    | BOOL                                                                                      # bool
    | NEW type=TYPE                                                                             # new
    | TILDE e=expr                                                                              # negation
    | ISVOID e=expr                                                                             # isVoid
    | left=expr op=(MULT | DIV) right=expr                                                      # multDiv
    | left=expr op=(PLUS | MINUS) right=expr                                                    # plusMinus
    | left=expr op=(LE | LT | EQUAL) right=expr                                                 # relational
    | name=ID ASSIGN e=expr                                                                     # assign
    | NOT e=expr                                                                                # not
    | LPAREN e=expr RPAREN                                                                      # paren
    ;