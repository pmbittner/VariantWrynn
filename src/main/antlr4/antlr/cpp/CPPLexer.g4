lexer grammar CPPLexer;

WS: [ \t]+ -> skip;
SINGLELINE_COMMENT: [ \t\n\r]* '//' (~('\r' | '\n'))* -> skip;
MULTILINE_COMMENT: [ \t\n\r]* '/*' ([ \t\n\r]|.)*? '*/' -> skip;
//EMPTYLINE: [\n\r] WS*? [\n\r]+ -> skip;


//SRC: ~('#' | '\r' | '\n')+;

LINEBREAK: [\n\r];

MACRO_INCLUDE: '#' 'include' -> pushMode(MACRO_MODE);
MACRO_IFDEF: '#' 'ifdef' -> pushMode(MACRO_MODE);
MACRO_IFNDEF: '#' 'ifndef' -> pushMode(MACRO_MODE);
MACRO_IF: '#' 'if' -> pushMode(MACRO_MODE);
MACRO_ELSE: '#' 'else' -> pushMode(MACRO_MODE);
MACRO_ELIF: '#' 'elif' -> pushMode(MACRO_MODE);
MACRO_ENDIF: '#' 'endif' -> pushMode(MACRO_MODE);
MACRO_ERROR: '#' 'error' -> pushMode(MACRO_MODE);

MACRO_DEFINE: '#' 'define' -> pushMode(MACRO_DEFINITION_PARAMS_MODE);

SRCLINE: ~('#' | '\r' | '\n')+;


mode MACRO_MODE;
MM_WS: WS -> skip;
MM_SINGLELINE_COMMENT: SINGLELINE_COMMENT -> skip;
MM_MULTILINE_COMMENT: MULTILINE_COMMENT -> skip;

INCLUDEPATH: ('<'[a-zA-Z_0-9./]*'>') | ('"'[a-zA-Z_0-9./]*'"');
IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]*;
STRING: '"' ~('\r' | '\n' | '"')* '"';
AND : '&&';
OR  : '||';
NOT : '!';
OPENINGBRACKET: '(';
CLOSINGBRACKET: ')';
COMMA: ',';

MACRO_END: (LINEBREAK | EOF) -> popMode;


mode MACRO_DEFINITION_PARAMS_MODE;
MDPM_WS: WS -> skip;
//MDM_SINGLELINE_COMMENT: SINGLELINE_COMMENT -> skip;
MDPM_MULTILINE_COMMENT: MULTILINE_COMMENT -> skip;

VARARGS: '...';

//MDPM_PARAMS: MDPM_OPENINGBRACKET WS? (MDPM_IDENTIFIER WS? (MDPM_COMMA WS? MDPM_IDENTIFIER WS?)* (MDPM_COMMA WS? VARARGS)?)? WS? MDPM_CLOSINGBRACKET;
//MDPM_DEFINITION: MDPM_IDENTIFIER WS? MDPM_PARAMS? -> pushMode(MACRO_DEFINITION_MODE);

MDPM_PARAMS: OPENINGBRACKET WS? (IDENTIFIER WS? (COMMA WS? IDENTIFIER WS?)* (COMMA WS? VARARGS)?)? WS? CLOSINGBRACKET;
MDPM_DEFINITION: IDENTIFIER WS? MDPM_PARAMS? -> pushMode(MACRO_DEFINITION_MODE);

mode MACRO_DEFINITION_MODE;
MDM_WS: WS -> skip;
//MDM_SINGLELINE_COMMENT: SINGLELINE_COMMENT -> skip;
MDM_MULTILINE_COMMENT: MULTILINE_COMMENT -> skip;

MACRO_CONTENT: ~('\\' | '#' | '\r' | '\n')+;
MDM_MACROLINEBREAK: '\\' WS* LINEBREAK;
MDM_MACRO_END: (LINEBREAK | EOF) -> popMode, popMode;