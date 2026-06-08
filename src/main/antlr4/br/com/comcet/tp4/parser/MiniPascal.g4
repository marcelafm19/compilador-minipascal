grammar MiniPascal;

// Regra inicial
program: 'program' ID ';' varDecl? functionDecl* block '.' EOF;

varDecl: 'var' (ID (',' ID)* ':' type ';')+;

// Simplificação: Cada parâmetro declara seu próprio tipo (ex: x: integer, y: integer)
functionDecl: 'function' ID '(' paramList? ')' ':' type ';' varDecl? block ';';
paramList: ID ':' type (',' ID ':' type)*;

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
    : left=expression op=('*'|'/') right=expression                   # binaryExpr
    | left=expression op=('+'|'-') right=expression                   # binaryExpr
    | left=expression op=('=='|'!='|'>'|'<'|'>='|'<=') right=expression # relationalExpr
    | ID '(' argList? ')'                                             # functionCallExpr
    | NUMBER                                                          # numberExpr
    | TRUE                                                            # booleanExpr
    | FALSE                                                           # booleanExpr
    | STRING_LITERAL                                                  # stringExpr
    | ID                                                              # idExpr
    | '(' expression ')'                                              # parenExpr
    ;

argList: expression (',' expression)*;

// Lexer Rules
TRUE: 'true';
FALSE: 'false';
ID: [a-zA-Z][a-zA-Z0-9]*;
NUMBER: [0-9]+;
STRING_LITERAL: '"' ~["]* '"'; // Captura tudo entre aspas duplas
WS: [ \t\r\n]+ -> skip;