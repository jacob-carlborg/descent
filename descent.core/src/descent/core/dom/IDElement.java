package descent.core.dom;

/**
 * Represents an element in the D model.
 */
public interface IDElement {
	
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
	 * Constant representing an expression.
	 * A D element with this type can be safely cast to <code>IExpression</code>. 
	 */
	int EXPRESSION = 10;
	
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
	 * Constant representing a template parameter.
	 * A D element with this type can be safely cast to <code>ITemplateParameter</code>. 
	 */
	int TEMPLATE_PARAMETER = 13;
	
	/**
	 * Constant representing a function declaration.
	 * A D element with this type can be safely cast to <code>IFunctionDeclaration</code>. 
	 */
	int FUNCTION_DECLARATION = 14;
	
	/**
	 * Constant representing a statement.
	 * A D element with this type can be safely cast to <code>IStatement</code>. 
	 */
	int STATEMENT = 15;
	
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
	 * Constant representing a type.
	 * A D element with this type can be safely cast to <code>IType</code>. 
	 */
	int TYPE = 18;
	
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
	 * Constant representing an initializer.
	 * A D element with this type can be safely cast to <code>IInitializer</code>. 
	 */
	int INITIALIZER = 21;
	
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
	 * Constant representing a conditional declaration.
	 * A D element with this type can be safely cast to <code>IConditionalDeclaration</code>. 
	 */
	int CONDITIONAL_DECLARATION = 26;
	
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
	int CATCH = 33;
	
	/**
	 * Returns the offset in the source code where this element
	 * is located.
	 */
	int getOffset();
	
	/**
	 * Returns the length in the source code of this element.
	 */
	int getLength();
	
	/**
	 * Returns one of this interface constants, telling to
	 * which class one can cast safely.
	 */
	int getElementType();
	
	/**
	 * Accepts a visitor down the element hierarchy.
	 * @param visitor a visitor
	 */
	void accept(IDElementVisitor visitor);

}
