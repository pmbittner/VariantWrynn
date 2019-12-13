parser grammar CPPParser;

options { tokenVocab=CPPLexer; }

document: code EOF;
code: (macro | codeline)*;

codeline: SRCLINE | LINEBREAK;
macro: (include | define | error | condition);

include: MACRO_INCLUDE INCLUDEPATH MACRO_END;
define: MACRO_DEFINE  params=MDPM_DEFINITION (macrodefinition | MDM_MACRO_END); // body=MACROEXPANSION;
macrodefinition: MACRO_CONTENT ((MDM_MACROLINEBREAK macrodefinition) | MDM_MACRO_END);
error: MACRO_ERROR message=STRING MACRO_END;

condition: (ifMacro | ifdefMacro | ifndefMacro) code ((MACRO_ENDIF MACRO_END) | elseIfCondition | elseCondition);
elseIfCondition: elifMacro code ((MACRO_ENDIF MACRO_END) | elseIfCondition | elseCondition);
elseCondition: MACRO_ELSE MACRO_END code MACRO_ENDIF MACRO_END;

ifMacro: MACRO_IF expression MACRO_END;
ifdefMacro: MACRO_IFDEF IDENTIFIER MACRO_END;
ifndefMacro: MACRO_IFNDEF IDENTIFIER MACRO_END;
elifMacro: MACRO_ELIF expression MACRO_END;

braces: OPENINGBRACKET expression CLOSINGBRACKET;
not: NOT (braces | literal);
binaryOperator : AND | OR;
literal: functionCall | name=IDENTIFIER;
functionCall: IDENTIFIER OPENINGBRACKET (expression (COMMA expression)*)? CLOSINGBRACKET;

expression:
    braces
    | left=expression binaryOperator right=expression
    | not
    | literal;
