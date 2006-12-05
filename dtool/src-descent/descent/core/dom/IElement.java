package descent.core.dom;

import descent.core.domX.ASTVisitor;

/**
 * Represents an element in the D model.
 */
public interface IElement {
	
	/**
	 * Constant representing a compilation unit (a source file).
	 * A D element with this type can be safely cast to <code>ICompilationUnit</code>. 
	 */
	int COMPILATION_UNIT = 1;
	
	/**
	 * Constant representing a module declaration.
	 * A D element with this type can be safely cast to <code>IModuleDeclaration</code>. 
	 */
	int MODULE_DECLARATION = 2;
	
	/**
	 * Constant representing an import declaration.
	 * A D element with this type can be safely cast to <code>IImportDeclaration</code>. 
	 */
	int IMPORT_DECLARATION = 3;
	
	/**
	 * Constant representing an import.
	 * A D element with this type can be safely cast to <code>IImport</code>. 
	 */
	int IMPORT = 4;
	
	/**
	 * Constant representing a selective import.
	 * A D element with this type can be safely cast to <code>ISelectiveImport</code>. 
	 */
	int SELECTIVE_IMPORT = 5;
	
	/**
	 * Constant representing a name.
	 * A D element with this type can be safely cast to <code>IName</code>. 
	 */
	int NAME = 6;
	
	/**
	 * Constant representing a qualified name.
	 * A D element with this type can be safely cast to <code>IQualifiedName</code>. 
	 */
	int QUALIFIED_NAME = 7;
	
	/**
	 * Constant representing an enum declaration.
	 * A D element with this type can be safely cast to <code>IEnumDeclaration</code>. 
	 */
	int ENUM_DECLARATION = 8;
	
	/**
	 * Constant representing an enum member.
	 * A D element with this type can be safely cast to <code>IEnumMember</code>. 
	 */
	int ENUM_MEMBER = 9;
	
	/**
	 * Constant representing an aggregate declaration.
	 * A D element with this type can be safely cast to <code>IAggregateDeclaration</code>. 
	 */
	int AGGREGATE_DECLARATION = 11;
	
	/**
	 * Constant representing a base class.
	 * A D element with this type can be safely cast to <code>IBaseClass</code>. 
	 */
	int BASE_CLASS = 12;
	
	/**
	 * Constant representing a function declaration.
	 * A D element with this type can be safely cast to <code>IFunctionDeclaration</code>. 
	 */
	int FUNCTION_DECLARATION = 14;
	
	/**
	 * Constant representing an invariant declaration.
	 * A D element with this type can be safely cast to <code>IInvariantDeclaration</code>. 
	 */
	int INVARIANT_DECLARATION = 16;
	
	/**
	 * Constant representing a unittest declaration.
	 * A D element with this type can be safely cast to <code>IUnitTestDeclaration</code>. 
	 */
	int UNITTEST_DECLARATION = 17;
	
	/**
	 * Constant representing an argument.
	 * A D element with this type can be safely cast to <code>IArgument</code>. 
	 */
	int ARGUMENT = 19;
	
	/**
	 * Constant representing a variable declaration.
	 * A D element with this type can be safely cast to <code>IVariableDeclaration</code>. 
	 */
	int VARIABLE_DECLARATION = 20;
	
	/**
	 * Constant representing a typedef declaration.
	 * A D element with this type can be safely cast to <code>ITypedefDeclaration</code>. 
	 */
	int TYPEDEF_DECLARATION = 22;
	
	/**
	 * Constant representing a protection declaration.
	 * A D element with this type can be safely cast to <code>IProtectionDeclaration</code>. 
	 */
	int PROTECTION_DECLARATION = 23;
	
	/**
	 * Constant representing a storage class declaration.
	 * A D element with this type can be safely cast to <code>IStorageClassDeclaration</code>. 
	 */
	int STORAGE_CLASS_DECLARATION = 24;
	
	/**
	 * Constant representing an alias declaration.
	 * A D element with this type can be safely cast to <code>IAliasDeclaration</code>. 
	 */
	int ALIAS_DECLARATION = 25;
	
	/**
	 * Constant representing a template declaration.
	 * A D element with this type can be safely cast to <code>ITemplateDeclaration</code>. 
	 */
	int TEMPLATE_DECLARATION = 27;
	
	/**
	 * Constant representing an align declaration.
	 * A D element with this type can be safely cast to <code>IAlignDeclaration</code>. 
	 */
	int ALIGN_DECLARATION = 28;
	
	/**
	 * Constant representing a link declaration.
	 * A D element with this type can be safely cast to <code>ILinkDeclaration</code>. 
	 */
	int LINK_DECLARATION = 29;
	
	/**
	 * Constant representing a condition assignment.
	 * A D element with this type can be safely cast to <code>IConditionAssignment</code>. 
	 */
	int CONDITION_ASSIGNMENT = 30;
	
	/**
	 * Constant representing a pragma declaration.
	 * A D element with this type can be safely cast to <code>IPragmaDeclaration</code>. 
	 */
	int PRAGMA_DECLARATION = 31;
	
	/**
	 * Constant representing a mixin declaration.
	 * A D element with this type can be safely cast to <code>IMixinDeclaration</code>. 
	 */
	int MIXIN_DECLARATION = 32;
	
	/**
	 * Constant representing a catch clause.
	 * A D element with this type can be safely cast to <code>ICatch</code>. 
	 */
	int CATCH_CLAUSE = 33;
	
	/**
	 * Constant representing a static assert.
	 * A D element with this type can be safely cast to <code>IStaticAssert</code>. 
	 */
	int STATIC_ASSERT = 33;
	
	/**
	 * Constant representing a type specialization.
	 * A D element with this type can be safely cast to <code>ITypeSpecialization</code>. 
	 */
	int TYPE_SPECIALIZATION = 34;
	
	/**
	 * Constant representing a this expression.
	 * A D element with this type can be safely cast to <code>IThisExpression</code>.
	 */
	int THIS_EXPRESSION = 35;
	
	/**
	 * Constant representing a super expression.
	 * A D element with this type can be safely cast to <code>ISuperExpression</code>.
	 */
	int SUPER_EXPRESSION = 36;
	
	/**
	 * Constant representing a null expression.
	 * A D element with this type can be safely cast to <code>INullExpression</code>.
	 */
	int NULL_EXPRESSION = 37;
	
	/**
	 * Constant representing a true expression.
	 * A D element with this type can be safely cast to <code>ITrueExpression</code>.
	 */
	int TRUE_EXPRESSION = 38;
	
	/**
	 * Constant representing a false expression.
	 * A D element with this type can be safely cast to <code>IFalseExpression</code>.
	 */
	int FALSE_EXPRESSION = 39;
	
	/**
	 * Constant representing a string expression.
	 * An expression with this type can be safely cast to <code>IStringExpression</code>. 
	 */
	int STRING_EXPRESSION = 40;
	
	/**
	 * Constant representing an integer expression.
	 * An expression with this type can be safely cast to <code>IIntegerExpression</code>. 
	 */
	int INTEGER_EXPRESSION = 41;
	
	/**
	 * Constant representing a real expression.
	 * An expression with this type can be safely cast to <code>IRealExpression</code>. 
	 */
	int REAL_EXPRESSION = 42;
	
	/**
	 * Constant representing an assert expression.
	 * An expression with this type can be safely cast to <code>IAssertExpression</code>. 
	 */
	int ASSERT_EXPRESSION = 43;
	
	/**
	 * Constant representing a parenthesized expression.
	 * An expression with this type can be safely cast to <code>IParenthesizedExpression</code>. 
	 */
	int PARENTHESIZED_EXPRESSION = 44;
	
	/**
	 * Constant representing a binary expression.
	 * An expression with this type can be safely cast to <code>IBinaryExpression</code>. 
	 */
	int BINARY_EXPRESSION = 45;
	
	/**
	 * Constant representing a condition expression.
	 * An expression with this type can be safely cast to <code>IConditionExpression</code>. 
	 */
	int CONDITION_EXPRESSION = 46;
	
	/**
	 * Constant representing an identifier expression.
	 * An expression with this type can be safely cast to <code>IIdentifierExpression</code>.
	 */
	int IDENTIFIER_EXPRESSION = 47;
	
	/**
	 * Constant representing a type dot identifier expression.
	 * An expression with this type can be safely cast to <code>ITypeDotIdentifierExpression</code>. 
	 */
	int TYPE_DOT_IDENTIFIER_EXPRESSION = 48;
	
	/**
	 * Constant representing a delete expression.
	 * An expression with this type can be safely cast to <code>IDeleteExpression</code>. 
	 */
	int DELETE_EXPRESSION = 49;
	
	/**
	 * Constant representing a unary expression.
	 * An expression with this type can be safely cast to <code>IUnaryExpression</code>. 
	 */
	int UNARY_EXPRESSION = 50;
	
	/**
	 * Constant representing a cast expression.
	 * An expression with this type can be safely cast to <code>ICastExpression</code>. 
	 */
	int CAST_EXPRESSION = 51;
	
	/**
	 * Constant representing a scope expression.
	 * An expression with this type can be safely cast to <code>IScopeExpression</code>. 
	 */
	int SCOPE_EXPRESSION = 52;
	
	/**
	 * Constant representing a new expression.
	 * An expression with this type can be safely cast to <code>INewExpression</code>. 
	 */
	int NEW_EXPRESSION = 53;
	
	/**
	 * Constant representing an array expression.
	 * An expression with this type can be safely cast to <code>IArrayExpression</code>. 
	 */
	int ARRAY_EXPRESSION = 54;
	
	/**
	 * Constant representing an array literal expression.
	 * An expression with this type can be safely cast to <code>IArrayLiteralExpression</code>. 
	 */
	int ARRAY_LITERAL_EXPRESSION = 55;
	
	/**
	 * Constant representing a slice expression.
	 * An expression with this type can be safely cast to <code>ISliceExpression</code>. 
	 */
	int SLICE_EXPRESSION = 56;
	
	/**
	 * Constant representing a dollar expression.
	 * An expression with this type can be safely cast to <code>IDollaryExpression</code>.
	 */
	int DOLAR_EXPRESSION = 57;
	
	/**
	 * Constant representing a call expression.
	 * An expression with this type can be safely cast to <code>ICallExpression</code>. 
	 */
	int CALL_EXPRESSION = 58;
	
	/**
	 * Constant representing a type expression.
	 * An expression with this type can be safely cast to <code>ITypeExpression</code>. 
	 */
	int TYPE_EXPRESSION = 59;
	
	/**
	 * Constant representing a dot id expression.
	 * An expression with this type can be safely cast to <code>IDotIdentifierExpression</code>. 
	 */
	int DOT_IDENTIFIER_EXPRESSION = 60;

	/**
	 * Constant representing a typeid expression.
	 * An expression with this type can be safely cast to <code>ITypeidExpression</code>. 
	 */
	int TYPEID_EXPRESSION = 61;

	/**
	 * Constant representing an is expression.
	 * An expression with this type can be safely cast to <code>IIsExpression</code>. 
	 */
	int IS_EXPRESSION = 62;
	
	/**
	 * Constant representing a new anonymous class expression.
	 * An expression with this type can be safely cast to <code>INewAnonymousClassExpression</code>. 
	 */
	int NEW_ANONYMOUS_CLASS_EXPRESSION = 63;
	
	/**
	 * Constant representing a function expression.
	 * An expression with this type can be safely cast to <code>IFunctionExpression</code>. 
	 */
	int FUNCTION_EXPRESSION = 64;
	
	/**
	 * Constant representing a value template parameter.
	 * A template parameter with this type can be safely cast to <code>IValueTemplateParameter</code>.
	 */
	int VALUE_TEMPLATE_PARAMETER = 65;
	
	/**
	 * Constant representing a type template parameter.
	 * A template parameter with this type can be safely cast to <code>ITypeTemplateParameter</code>. 
	 */
	int TYPE_TEMPLATE_PARAMETER = 66;
	
	/**
	 * Constant representing an alias template parameter.
	 * A template parameter with this type can be safely cast to <code>IAliasTemplateParameter</code>. 
	 */
	int ALIAS_TEMPLATE_PARAMETER = 67;
	
	/**
	 * Constant representing a tuple template parameter.
	 * A template parameter with this type can be safely cast to <code>ITupleTemplateParameter</code>. 
	 */
	int TUPLE_TEMPLATE_PARAMETER = 68;
	
	/**
	 * Constant representing an expression statement.
	 * A D element with this type can be safely cast to <code>IExpressionStatement</code>. 
	 */
	int EXPRESSION_STATEMENT = 69;
	
	/**
	 * Constant representing a declaration statement.
	 * A D element with this type can be safely cast to <code>IDeclarationStatement</code>. 
	 */
	int DECLARATION_STATEMENT = 70;
	
	/**
	 * Constant representing a break statement.
	 * A D element with this type can be safely cast to <code>IBreakStatement</code>. 
	 */
	int BREAK_STATEMENT = 71;
	
	/**
	 * Constant representing a continue statement.
	 * A D element with this type can be safely cast to <code>IContinueStatement</code>. 
	 */
	int CONTINUE_STATEMENT = 72;
	
	/**
	 * Constant representing a return statement.
	 * A D element with this type can be safely cast to <code>IReturnStatement</code>. 
	 */
	int RETURN_STATEMENT = 73;
	
	/**
	 * Constant representing a while statement.
	 * A D element with this type can be safely cast to <code>IWhileStatement</code>. 
	 */
	int WHILE_STATEMENT = 74;
	
	/**
	 * Constant representing a do while statement.
	 * A D element with this type can be safely cast to <code>IDoWhileStatement</code>. 
	 */
	int DO_WHILE_STATEMENT = 75;
	
	/**
	 * Constant representing a label statement.
	 * A D element with this type can be safely cast to <code>ILabelStatement</code>. 
	 */
	int LABEL_STATEMENT = 76;
	
	/**
	 * Constant representing a static assert statement.
	 * A D element with this type can be safely cast to <code>IStaticAssertStatement</code>. 
	 */
	int STATIC_ASSERT_STATEMENT = 77;
	
	/**
	 * Constant representing a with statement.
	 * A D element with this type can be safely cast to <code>IWithStatement</code>. 
	 */
	int WITH_STATEMENT = 78;
	
	/**
	 * Constant representing a for statement.
	 * A D element with this type can be safely cast to <code>IForStatement</code>. 
	 */
	int FOR_STATEMENT = 79;
	
	/**
	 * Constant representing a foreach or foreach_reverse statement.
	 * A D element with this type can be safely cast to <code>IForeachStatement</code>. 
	 */
	int FOREACH_STATEMENT = 80;
	
	/**
	 * Constant representing a volatile statement.
	 * A D element with this type can be safely cast to <code>IVolatileStatement</code>. 
	 */
	int VOLATILE_STATEMENT = 81;
	
	/**
	 * Constant representing a switch statement.
	 * A D element with this type can be safely cast to <code>ISwitchStatement</code>. 
	 */
	int SWITCH_STATEMENT = 82;
	
	/**
	 * Constant representing a case statement.
	 * A D element with this type can be safely cast to <code>ICaseStatement</code>. 
	 */
	int CASE_STATEMENT = 83;
	
	/**
	 * Constant representing a scope statement.
	 * A D element with this type can be safely cast to <code>IScopeStatement</code>. 
	 */
	int SCOPE_STATEMENT = 84;
	
	/**
	 * Constant representing a compound statement.
	 * A D element with this type can be safely cast to <code>ICompoundStatement</code>. 
	 */
	int COMPOUND_STATEMENT = 85;
	
	/**
	 * Constant representing a try statement.
	 * A D element with this type can be safely cast to <code>ITryStatement</code>. 
	 */
	int TRY_STATEMENT = 86;
	
	/**
	 * Constant representing a throw statement.
	 * A D element with this type can be safely cast to <code>IThrowStatement</code>. 
	 */
	int THROW_STATEMENT = 87;
	
	/**
	 * Constant representing a synchronized statement.
	 * A D element with this type can be safely cast to <code>ISynchronizedStatement</code>. 
	 */
	int SYNCHRONIZED_STATEMENT = 88;
	
	/**
	 * Constant representing a scope statement.
	 * A D element with this type can be safely cast to <code>IOnScopeStatement</code>. 
	 */
	int ON_SCOPE_STATEMENT = 89;
	
	/**
	 * Constant representing a goto statement.
	 * A D element with this type can be safely cast to <code>IGotoStatement</code>. 
	 */
	int GOTO_STATEMENT = 90;
	
	/**
	 * Constant representing a goto default statement.
	 * A D element with this type can be safely cast to <code>IGotoDefaultStatement</code>.
	 */
	int GOTO_DEFAULT_STATEMENT = 91;
	
	/**
	 * Constant representing a goto case statement.
	 * A D element with this type can be safely cast to <code>IGotoCaseStatement</code>. 
	 */
	int GOTO_CASE_STATEMENT = 92;
	
	/**
	 * Constant representing a default statement.
	 * A D element with this type can be safely cast to <code>IDefaultStatement</code>. 
	 */
	int DEFAULT_STATEMENT = 93;
	
	/**
	 * Constant representing a pragma statement.
	 * A D element with this type can be safely cast to <code>IPragmaStatement</code>. 
	 */
	int PRAGMA_STATEMENT = 94;
	
	/**
	 * Constant representing an if statement.
	 * A D element with this type can be safely cast to <code>IIfStatement</code>. 
	 */
	int IF_STATEMENT = 95;
	
	/**
	 * Constant representing an asm statement.
	 * A D element with this type can be safely cast to <code>IAsmStatement</code>. 
	 */
	int ASM_STATEMENT = 97;
	
	/**
	 * Constant representing a basic type.
	 * A D element with this type can be safely cast to <code>IBasicType</code>. 
	 */
	int BASIC_TYPE = 98;
	
	/**
	 * Constant representing a pointer type.
	 * A D element with this type can be safely cast to <code>IPointerType</code>. 
	 */
	int POINTER_TYPE = 99;
	
	/**
	 * Constant representing a template instance type.
	 * A D element with this type can be safely cast to <code>ITemplateInstanceType</code>. 
	 */
	int TEMPLATE_INSTANCE_TYPE = 101;
	
	/**
	 * Constant representing an identifier type.
	 * A D element with this type can be safely cast to <code>IIdentifierType</code>. 
	 */
	int IDENTIFIER_TYPE = 102;
	
	/**
	 * Constant representing a delegate type.
	 * A D element with this type can be safely cast to <code>IDelegateType</code>. 
	 */
	int DELEGATE_TYPE = 103;
	
	/**
	 * Constant representing a point to function type.
	 * A D element with this type can be safely cast to <code>IDelegateType</code>. 
	 */
	int POINTER_TO_FUNCTION_TYPE = 104;
	
	/**
	 * Constant representing a typeof type.
	 * A D element with this type can be safely cast to <code>ITypeofType</code>. 
	 */
	int TYPEOF_TYPE = 105;
	
	/**
	 * Constant representing a dynamic array.
	 * A D element with this type can be safely cast to <code>IDynamicArrayType</code>.
	 */
	int DYNAMIC_ARRAY_TYPE = 107;
	
	/**
	 * Constant representing an associative array.
	 * A D element with this type can be safely cast to <code>IAssociativeArrayType</code>. 
	 */
	int ASSOCIATIVE_ARRAY_TYPE = 108;
	
	/**
	 * Constant representing a static array.
	 * A D element with this type can be safely cast to <code>IStaticArrayType</code>. 
	 */
	int STATIC_ARRAY_TYPE = 109;
	
	/**
	 * Constant representing an expression intializer.
	 * A D element with this type can be safely cast to <code>IExpressionInitializer</code>. 
	 */
	int EXPRESSION_INITIALIZER = 110;
	
	/**
	 * Constant representing a void intializer.
	 * A D element with this type can be safely cast to <code>IVoidInitializer</code>.
	 */
	int VOID_INITIALIZER = 111;
	
	/**
	 * Constant representing an array intializer.
	 * A D element with this type can be safely cast to <code>IArrayInitializer</code>. 
	 */
	int ARRAY_INITIALIZER = 112;
	
	/**
	 * Constant representing a struct intializer.
	 * A D element with this type can be safely cast to <code>IStructInitializer</code>. 
	 */
	int STRUCT_INITIALIZER = 113;
	
	/**
	 * Constant representing a debug declaration.
	 * A D element with this type can be safely cast to <code>IDebugDeclaration</code>. 
	 */
	int DEBUG_DECLARATION = 114;
	
	/**
	 * Constant representing a version declaration.
	 * A D element with this type can be safely cast to <code>IVersionDeclaration</code>. 
	 */
	int VERSION_DECLARATION = 115;
	
	/**
	 * Constant representing a static if declaration.
	 * A D element with this type can be safely cast to <code>IStaticIfDeclaration</code>. 
	 */
	int STATIC_IF_DECLARATION = 116;
	
	/**
	 * Constant representing the deprecated iftype declaration.
	 * A D element with this type can be safely cast to <code>IIftypeDeclaration</code>. 
	 */
	int IFTYPE_DECLARATION = 117;
	
	/**
	 * Constant representing a debug statement.
	 * A D element with this type can be safely cast to <code>IDebugStatement</code>. 
	 */
	int DEBUG_STATEMENT = 118;
	
	/**
	 * Constant representing a version statement.
	 * A D element with tis type can be safely cast to <code>IVersionStatement</code>. 
	 */
	int VERSION_STATEMENT = 119;
	
	/**
	 * Constant representing a static if statement.
	 * A D element with this type can be safely cast to <code>IStaticIfStatement</code>. 
	 */
	int STATIC_IF_STATEMENT = 120;
	
	/**
	 * Constant representing a slice type.
	 * A D element with this type can be safely cast to <code>ISliceType</code>. 
	 */
	int SLICE_TYPE = 109;
	
}
