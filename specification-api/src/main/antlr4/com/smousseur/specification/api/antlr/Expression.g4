grammar Expression;

expression
    : (join '->')* path operator VALUE
    ;

operator
    : '='
    | 'like'
    | '>'
    | '>='
    | '<'
    | '<='
    | 'contains'
    | 'in'
    ;

join
    : 'join(' IDENTIFIER ')'
    ;

path
    : property
    | jsonProperty
    ;

property
    : 'property(' IDENTIFIER ')'
    ;

jsonProperty
    : 'json_property(' IDENTIFIER ',' IDENTIFIER (',' jsonOption)? ')'
    ;

jsonOption
    : 'json'
    | 'jsonb'
    ;

fragment DIGIT: [0-9];
fragment LETTER: [a-zA-Z_'];
fragment DOT: '.';
fragment QUESTION_MARK: '?';

ALPHANUM: LETTER | DIGIT | DOT;

IDENTIFIER
    : ALPHANUM+
    ;

VALUE
    : '"' ~[\u007F]+ '"'
    | QUESTION_MARK
    ;

WS
    : [ \t\n\r]+ -> skip
    ;
