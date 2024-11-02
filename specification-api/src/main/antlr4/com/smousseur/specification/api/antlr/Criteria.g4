grammar Criteria;

criteria
    : (join '->')* path operator VALUE
    | (join '->')* path unaryOperator
    ;

operator
    : '='
    | '!='
    | 'like'
    | '>'
    | '>='
    | '<'
    | '<='
    | 'contains'
    | 'in'
    ;

unaryOperator
    : 'isnull'
    | 'isnotnull'
    ;

join
    : 'join(' IDENTIFIER (',' joinProperty)? ')'
    ;

joinProperty
    : 'fetch'
    | 'right'
    | 'left'
    ;

path
    : property
    | jsonProperty
    ;

property
    : 'property(' IDENTIFIER ')'
    | 'prop(' IDENTIFIER ')'
    ;

jsonProperty
    : 'json_property(' IDENTIFIER ',' IDENTIFIER (',' jsonOption)? ')'
    | 'json(' IDENTIFIER ',' IDENTIFIER (',' jsonOption)? ')'
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
    : QUESTION_MARK
    ;

WS
    : [ \t\n\r]+ -> skip
    ;
