grammar TypeChefCPP;

macros: ifLine*;

ifLine: '#' (condition | endif);

condition: 'if' expression;
endif: 'endif';

braces: '(' expression ')';
not: NOT (braces | literal);
binaryOperator : AND | OR;
literal: function;
function: IDENTIFIER '(' IDENTIFIER ')';

expression:
    braces
    | left=expression binaryOperator right=expression
    | not
    | function;

AND : '&&';
OR  : '||';
NOT : '!';
IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]* ;
WS  : [ \r\t\u000C\n]+ -> skip;