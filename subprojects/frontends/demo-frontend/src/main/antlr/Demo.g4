grammar Demo;

// parser rules - order does not matter
model: (assignment | statement | return)* assertion;

assignment: VarName ASSIGN expression SCOL;
statement: if_statement | while_statement;
assertion: ASSERT expression SCOL;
command: return | break | continue;

if_statement: IF cond_block (ELSE IF cond_block)* (ELSE block)?;
while_statement: WHILE cond_block;

block: (OBRACE (assignment | statement | command)* CBRACE) | (assignment | statement | command);
cond_block: OPEN expression CLOSE block;

expression : MIN expression
           | NOT expression
           | expression (MULT | DIV | MOD) expression
           | expression (PLUS | MIN) expression
           | expression (LT | GT | GTEQ | LTEQ) expression
           | expression (EQ | NEQ) expression
           | expression (AND | OR) expression
           | OPEN expression CLOSE
           | value;

value: Const | INPUT | TRUE | FALSE | NULL | VarName;

return: RETURN expression? SCOL;
break: BREAK SCOL;
continue: CONTINUE SCOL;

// Lexer rules - order does matter!
// Keywords
INPUT: 'input';
ASSERT: 'assert';
TRUE: 'true';
FALSE: 'false';
NULL: 'null';
IF: 'if';
ELSE: 'else';
WHILE: 'while';
RETURN: 'return';
CONTINUE: 'continue';
BREAK: 'break';

// Symbols
OR: '||';
AND: '&&';
EQ: '==';
NEQ: '!=';
LT: '<';
GT: '>';
GTEQ: '>=';
LTEQ: '<=';
NOT: '!';
ASSIGN: ':=';
PLUS: '+';
MIN: '-';
MULT: '*';
DIV: '/';
MOD: '%';
UNDER: '_';
SCOL: ';';
OPEN: '(';
CLOSE: ')';
OBRACE: '{';
CBRACE: '}';

VarName: Letter(Letter | Digit | UNDER)*;
Const: (MIN)?(Digit)+;

Letter: [a-z] | [A-Z];
Digit: [0-9];

// Ignored
Comment: '#' ~[\r\n]*
    -> skip;

Whitespace: [ \t]+
    -> skip;

Newline: ('\r' '\n'? | '\n')
    -> skip;