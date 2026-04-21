grammar MiniPascal;

// Regra inicial
program: 'program' ID ';' varDecl? block '.' EOF;

varDecl: 'var' (ID (',' ID)* ':' type ';')+;

type: 'integer' | 'boolean' | 'string';

block: 'begin' (command ';')* 'end';

command
    : assignment
    | ifCmd
    | whileCmd
    | printCmd
    | blockCmd
    ;

assignment: ID ':=' expression;
ifCmd: 'if' expression 'then' command ('else' command)?;
whileCmd: 'while' expression 'do' command;
printCmd: 'print' expression;
blockCmd: block;

expression
    : left=expression op=('*'|'/') right=expression  # binaryExpr
    | left=expression op=('+'|'-') right=expression  # binaryExpr
    | NUMBER                                         # numberExpr
    | ID                                             # idExpr
    | '(' expression ')'                             # parenExpr
    ;

// Lexer Rules
ID: [a-zA-Z][a-zA-Z0-9]*;
NUMBER: [0-9]+;
WS: [ \t\r\n]+ -> skip;