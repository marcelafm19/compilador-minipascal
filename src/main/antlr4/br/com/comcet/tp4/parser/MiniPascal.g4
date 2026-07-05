grammar MiniPascal;

// Regra inicial
program: 'program' ID ';' varDecl? functionDecl* block '.' EOF;

varDecl: 'var' (ID (',' ID)* ':' type ';')+;

// Mantido como extensão opcional caso deseje suportar funções
functionDecl: 'function' ID '(' paramList? ')' ':' type ';' varDecl? block ';';
paramList: ID ':' type (',' ID ':' type)*;

type: 'integer' | 'boolean' | 'string';

// Uma lista de comandos dentro de um bloco begin ... end
block: 'begin' commandList 'end';

commandList: command (';' command)* ';'? ;

command
    : assignment
    | ifCmd
    | whileCmd
    | repeatCmd
    | readlnCmd
    | writelnCmd
    | blockCmd
    ;

assignment: ID ':=' expression;
ifCmd: 'if' expression 'then' command ('else' command)?;
whileCmd: 'while' expression 'do' command;
repeatCmd: 'repeat' commandList 'until' expression;
readlnCmd: 'readln' '(' ID ')';
writelnCmd: 'writeln' '(' expression ')';
blockCmd: block;

expression
    : 'not' expression                                                  # notExpr
    | left=expression op=('*'|'/') right=expression                     # multiplicativeExpr
    | left=expression op=('+'|'-') right=expression                     # additiveExpr
    | left=expression op=('='|'<>'|'>'|'<'|'>='|'<=') right=expression  # relationalExpr
    | left=expression 'and' right=expression                            # andExpr
    | left=expression 'or' right=expression                             # orExpr
    | ID '(' argList? ')'                                               # functionCallExpr
    | NUMBER                                                            # numberExpr
    | TRUE                                                              # booleanExpr
    | FALSE                                                             # booleanExpr
    | STRING_LITERAL                                                    # stringExpr
    | ID                                                                # idExpr
    | '(' expression ')'                                                # parenExpr
    ;

argList: expression (',' expression)*;

// --- Regras Léxicas ---

TRUE: 'true';
FALSE: 'false';

ID: [a-zA-Z] [a-zA-Z0-9]*;
NUMBER: [0-9]+;

// A especificação pede strings delimitadas por aspas simples
STRING_LITERAL: '\'' ~['\r\n]* '\'';

// Comentários: delimitados por { ... } ou (* ... *)
COMMENT_BRACES: '{' .*? '}' -> skip;
COMMENT_PARENS: '(*' .*? '*)' -> skip;

WS: [ \t\r\n]+ -> skip;