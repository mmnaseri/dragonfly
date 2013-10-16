grammar TypeDescriptor;

options
{
	output=AST;
}

WHITESPACE  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        )+
    ;

AND	:	 '&&';

OR	:	'||';

OPENING	:	'(';

CLOSING	:	')';

NOT	:	'!';

SOMETHING	:	'*';

CHILD	:	'+';

SEPARATOR
	:	'.';
	
ANYTHING	: 	'..';

ANNOTATION
	:	'@';

TYPE	:	'TYPE';

SELECT	:	'SELECT';

HAVING	:	'having';

PROPERTY:	'property';

METHOD	:	'method';

PARAMS	:	'PARAMS';

ID  	:	('a'..'z'|'A'..'Z'|'$'|'_') ('a' .. 'z' | 'A' .. 'Z' | '$' | '_' | '0' .. '9'|'?'|'*')*;

COMMA	:	',';

type	:	{root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(TYPE, "TYPE"), root_0);} typeRest;

clazz	:	type (CHILD |);

selector:	annotations WHITESPACE clazz -> ^(SELECT annotations clazz)
	|	clazz -> ^(SELECT clazz)
	|	HAVING! WHITESPACE! PROPERTY^ WHITESPACE!? OPENING! WHITESPACE!? (annotations WHITESPACE!)? clazz WHITESPACE! (ID|SOMETHING) WHITESPACE!? CLOSING!
	|	HAVING! WHITESPACE! METHOD^ WHITESPACE!? OPENING! WHITESPACE!? (annotations WHITESPACE!)? clazz WHITESPACE! (ID|SOMETHING) WHITESPACE!? OPENING! WHITESPACE!? (methodParameters WHITESPACE!? | ANYTHING |) CLOSING! WHITESPACE!? CLOSING!;

methodParameters
	:	{root_0 = (Object)adaptor.becomeRoot((Object)adaptor.create(PARAMS, "PARAMS"), root_0);} (clazz (WHITESPACE!? COMMA! WHITESPACE!? clazz)*)
	;

	
typeRest:	((SOMETHING | ID) SEPARATOR)* (SOMETHING | ID) (ANYTHING ((SOMETHING | ID) SEPARATOR)* (SOMETHING | ID))*;

annotations
	:	ANNOTATION^ type (WHITESPACE! ANNOTATION! type)*;


start	:	WHITESPACE!? orExpression WHITESPACE!? EOF!;

orExpression
	:	andExpression (WHITESPACE! OR^ WHITESPACE! andExpression)*;

andExpression
	:	booleanExpression (WHITESPACE! AND^ WHITESPACE! booleanExpression)*
	;

booleanExpression
	:	expression
	|	notExpression
	;

notExpression
	:	NOT^ WHITESPACE!? expression
	;

expression
	:	selector
	|	OPENING! WHITESPACE!? orExpression WHITESPACE!? CLOSING!
	;

