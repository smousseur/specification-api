grammar Expression;

expression
    : basicExpression ('->' basicExpression)*
    ;

basicExpression
    : join | value
    ;

join
    : 'join(' IDENTIFIER ')'
    | 'join(' IDENTIFIER ',' IDENTIFIER ')'
    ;

value
    : objectValue
    | jsonValue
    ;

objectValue
    : 'value(' IDENTIFIER ',' valueOperation '(' IDENTIFIER '))'
    | 'value(' IDENTIFIER ',' valueOperation '(' IDENTIFIER '),' valueType ')'
    ;

jsonValue
    : 'json_value(' IDENTIFIER ',' IDENTIFIER ',' valueOperation '(' IDENTIFIER '))'
    | 'json_value(' IDENTIFIER ',' IDENTIFIER ',' valueOperation '(' IDENTIFIER '),' valueType (',' jsonOption)? ')'
    ;

valueType
    : 'string'
    | 'int'
    | 'float'
    | 'double'
    | 'bool'
    | 'long'
    ;

jsonOption
    : 'json'
    | 'jsonb'
    ;

valueOperation
    : 'eq'
    | 'like'
    ;

IDENTIFIER
    : ALPHANUM+
    | (ALPHANUM+ SPACE ALPHANUM+)
    ;

fragment DIGIT: [0-9];
fragment LETTER: [a-zA-Z_$-'];
fragment DOT: '.';
fragment SPACE: ' ';
ALPHANUM: LETTER | DIGIT | DOT;

WS
    : [ \t\n\r]+ -> skip
    ;
