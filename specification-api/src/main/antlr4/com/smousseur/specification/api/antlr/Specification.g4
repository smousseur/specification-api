grammar Specification;

specification
    : (predicate)*
    ;

predicate
    : predicate AND predicate   #AndPredicateCxt
    | predicate OR predicate   #OrPredicateCxt
    | '(' predicate ')'         #InnerPredicateCxt
    | IDENTIFIER                #IdPredicateCxt
    ;

AND : '&' | '&&' | 'and';
OR : '|' | '||' | 'or';

// Les tokens pour les mots-clés, les identifiants et les caractères spéciaux
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]* ;
WS : [ \t\r\n]+ -> skip ;
