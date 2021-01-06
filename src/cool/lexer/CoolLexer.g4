lexer grammar CoolLexer;

tokens { ERROR } 

@header{
    package cool.lexer;	
}

@members{
    int stringLen = 0;
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }

    /**
     * Strips the string of quotation marks, manages all escaped sequences
     * replaces escaped characters such as newlines and tabs to their conterpart character,
     * manages effective newlines, as well as strips any 'unnecessary' escaped character.
     * @param str String to format and parse
     * @return String the managed and formatted string
     */
    private String formatString(String str) {
        return str.substring(1, getText().length()-1).replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t").replaceAll("\\\\([^bf])", "$1");
    }
}

/*
 * Keywords
 */
CLASS : 'class';
INHERITS : 'inherits';

IF : 'if';
THEN : 'then';
ELSE : 'else';
FI : 'fi';

LET : 'let';
IN  : 'in';

WHILE : 'while';
LOOP : 'loop';
POOL : 'pool';

CASE : 'case';
ESAC : 'esac';
OF : 'of';

NEW : 'new';
ISVOID : 'isvoid';
NOT : 'not';
BOOL : 'true' | 'false';


/*
 * Operators and miscellaneous
 */

SEMICOLON : ';';
COLON : ':';
COMMA : ',';

LPAREN : '(';
RPAREN : ')';

LBRACE : '{';
RBRACE : '}';

PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';

EQUAL : '=';
LT : '<';
LE : '<=';
ASSIGN : '<-';

AT : '@';
DOT : '.';

CASE_THEN : '=>';
TILDE : '~';

/*
 * Strings
 */
fragment NEWLINE : '\r'? '\n';
fragment ESCAPED
  : '\\b'
  | '\\t'
  | '\\' NEWLINE
  | '\\f'
  | '\\n'
  ;
fragment QUOTE : '"';
STRING
  : QUOTE
    ( ESCAPED { ++stringLen; }                                       // Known escaped sequences count as one character
    | '\\'                                                           // Don't count escaping backslash
    | '\u0000' { raiseError("\"String contains null character\""); } // Additional quotes as the message will be stripped
    | . { ++stringLen; }
    )*?
    ( EOF { raiseError("EOF in string constant"); stringLen = 0; }
    | NEWLINE { raiseError("Unterminated string constant"); }
    | QUOTE { setText(formatString(getText())); if (stringLen > 1024) raiseError("String constant too long"); stringLen = 0; })
  ;

/*
 * Comments
 */
LINE_COMMENT
    : '--' .*? (NEWLINE | EOF) -> skip
    ;

UNMATCHED_COMMENT_BLOCK : '*)' { raiseError("Unmatched *)"); };

BLOCK_COMMENT
    : '(*'
      (BLOCK_COMMENT | .)*?
      ('*)' { skip(); } | EOF { raiseError("EOF in comment"); })
    ;

/*
 * Type identifiers
 */
fragment UPPERCASE : [A-Z];
fragment LOWERCASE : [a-z];
fragment LETTER : [a-zA-Z];
fragment DIGIT : [0-9];

TYPE
  : (UPPERCASE (LETTER | DIGIT | '_')*)
  ;

/*
 * Object identifiers
 */
ID
  : LOWERCASE (LETTER | DIGIT | '_')*
  ;

/*
 * Integer values
 */
INT
  : DIGIT+
  ;

/*
 * White spaces
 */
WS
  : [ \n\f\r\t]+ -> skip
  ;

/*
 * Anything else counts as a syntactic error
 */
UNKNOWN_CHAR
  : . { raiseError("Invalid character: " + getText()); }
  ;