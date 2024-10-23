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
    | 'isnull'
    | 'in'
    ;

join
    : 'join(' IDENTIFIER ')'
    ;

path
    : objectPath
    | jsonPath
    ;

objectPath
    : 'path(' IDENTIFIER (',' valueType)?')'
    ;

jsonPath
    : 'json_path(' IDENTIFIER ',' IDENTIFIER (',' valueType)? (',' jsonOption)? ')'
    ;

jsonOption
    : 'json'
    | 'jsonb'
    ;

valueType
    : 'string'
    | 'int'
    | 'float'
    | 'double'
    | 'bool'
    | 'long'
    | 'date'
    | 'datetime'
    ;


IDENTIFIER
    : ALPHANUM+
    ;

fragment DIGIT: [0-9];
fragment LETTER: [a-zA-Z_'];
fragment DOT: '.';
fragment QUESTION_MARK: '?';

ALPHANUM: LETTER | DIGIT | DOT;

VALUE
    : '"' ~["]+ '"'
    | QUESTION_MARK
    ;

WS
    : [ \t\n\r]+ -> skip
    ;
