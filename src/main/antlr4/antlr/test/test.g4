grammar test;

start: 'hello' EOF;

WS: [ \r\n\t\u000C]+ -> skip;
SINGLELINE_COMMENT: '//' (~'\n')* -> skip;
MULTILINE_COMMENT: '/*' .*? '*/' -> skip;