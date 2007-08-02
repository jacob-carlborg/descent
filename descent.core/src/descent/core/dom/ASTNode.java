/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package descent.core.dom;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Abstract superclass of all Abstract Syntax Tree (AST) node types.
 * <p>
 * An AST node represents a D source code construct, such
 * as a name, type, expression, statement, or declaration.
 * </p>
 * <p>
 * Each AST node belongs to a unique AST instance, called the owning AST.
 * The children of an AST node always have the same owner as their parent node.
 * If a node from one AST is to be added to a different AST, the subtree must
 * be cloned first to ensure that the added nodes have the correct owning AST.
 * </p>
 * <p>
 * When an AST node is part of an AST, it has a unique parent node.
 * Clients can navigate upwards, from child to parent, as well as downwards,
 * from parent to child. Newly created nodes are unparented. When an 
 * unparented node is set as a child of a node (using a 
 * <code>set<i>CHILD</i></code> method), its parent link is set automatically
 * and the parent link of the former child is set to <code>null</code>.
 * For nodes with properties that include a list of children (for example,
 * <code>Block</code> whose <code>statements</code> property is a list
 * of statements), adding or removing an element to/for the list property
 * automatically updates the parent links. These lists support the 
 * <code>List.set</code> method; however, the constraint that the same
 * node cannot appear more than once means that this method cannot be used
 * to swap elements without first removing the node.
 * </p>
 * <p>
 * ASTs must not contain cycles. All operations that could create a cycle
 * detect this possibility and fail.
 * </p>
 * <p>
 * ASTs do not contain "holes" (missing subtrees). If a node is required to
 * have a certain property, a syntactically plausible initial value is
 * always supplied. 
 * </p>
 * <p>
 * The hierarchy of AST node types has some convenient groupings marked
 * by abstract superclasses:
 * <ul>
 * <li>declarations - <code>Declaration</code></li>
 * <li>expressions - <code>Expression</code></li>
 * <li>names - <code>Name</code> (a sub-kind of expression)</li>
 * <li>initializers - <code>Initializer</code></li>
 * <li>statements - <code>Statement</code></li>
 * <li>template parameters - <code>TemplateParameter</code></li>
 * <li>types - <code>Type</code></li>
 * </ul>
 * </p>
 * <p>
 * Abstract syntax trees may be hand constructed by clients, using the
 * <code>new<i>TYPE</i></code> factory methods (see <code>AST</code>) to
 * create new nodes, and the various <code>set<i>CHILD</i></code> methods
 * to connect them together.
 * </p>
 * <p>
 * The class {@link ASTParser} parses a string
 * containing a D source code and returns an abstract syntax tree
 * for it. The resulting nodes carry source ranges relating the node back to
 * the original source characters. The source range covers the construct
 * as a whole.
 * </p>
 * <p>
 * Each AST node carries bit flags, which may convey additional information about
 * the node. For instance, the parser uses a flag to indicate a syntax error.
 * Newly created nodes have no flags set.
 * </p>
 * <p>
 * Each AST node is capable of carrying an open-ended collection of
 * client-defined properties. Newly created nodes have none. 
 * <code>getProperty</code> and <code>setProperty</code> are used to access
 * these properties.
 * </p>
 * <p>
 * AST nodes are thread-safe for readers provided there are no active writers.
 * If one thread is modifying an AST, including creating new nodes or cloning
 * existing ones, it is <b>not</b> safe for another thread to read, visit,
 * write, create, or clone <em>any</em> of the nodes on the same AST.
 * When synchronization is required, consider using the common AST
 * object that owns the node; that is, use  
 * <code>synchronize (node.getAST()) {...}</code>.
 * </p>
 * <p>
 * ASTs also support the visitor pattern; see the class <code>ASTVisitor</code>
 * for details.
 * </p>
 * <p>
 * Compilation units created by <code>ASTParser</code> from a
 * source document can be serialized after arbitrary modifications
 * with minimal loss of original formatting. See 
 * {@link CompilationUnit#recordModifications()} for details.
 * See also {@link descent.core.dom.rewrite.ASTRewrite} for
 * an alternative way to describe and serialize changes to a
 * read-only AST.
 * </p>
 * 
 * @see ASTParser
 * @see ASTVisitor
 */
public abstract class ASTNode {
	
	/*
	 * INSTRUCTIONS FOR ADDING NEW CONCRETE AST NODE TYPES
	 * 
	 * There are several things that need to be changed when a
	 * new concrete AST node type (call it "FooBar"):
	 * 
	 * 1. Create the FooBar AST node type class.
	 * The most effective way to do this is to copy a similar
	 * existing concrete node class to get a template that
     * includes all the framework methods that must be implemented.
	 * 
	 * 2. Add node type constant ASTNode.FOO_BAR.
	 * Node constants are numbered consecutively. Add the
	 * constant after the existing ones.
	 * 
	 * 3. Add entry to ASTNode.nodeClassForType(int).
	 * 
	 * 4. Add AST.newFooBar() factory method.
	 * 
	 * 5. Add ASTVisitor.visit(FooBar) and endVisit(FooBar) methods.
	 * 
	 * 6. Add ASTMatcher.match(FooBar,Object) method.
	 * 
	 * 7. Ensure that SimpleName.isDeclaration() covers FooBar
	 * nodes if required.
	 * 
	 * 8. Add NaiveASTFlattener.visit(FooBar) method to illustrate
	 * how these nodes should be serialized.
	 * 
	 * 9. Update the AST test suites.
	 * 
	 * The next steps are to update AST.parse* to start generating
	 * the new type of nodes, and ASTRewrite to serialize them back out.
	 */
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>AggregateDeclaration</code>.
	 * @see AggregateDeclaration
	 */
	public static final int AGGREGATE_DECLARATION = 1;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AliasDeclaration</code>.
	 * @see AliasDeclaration
	 */
	public static final int ALIAS_DECLARATION = 2;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AliasDeclarationFragment</code>.
	 * @see AliasDeclarationFragment
	 */
	public static final int ALIAS_DECLARATION_FRAGMENT = 3;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AliasTemplateParameter</code>.
	 * @see AliasTemplateParameter
	 */
	public static final int ALIAS_TEMPLATE_PARAMETER = 4;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AlignDeclaration</code>.
	 * @see AlignDeclaration
	 */
	public static final int ALIGN_DECLARATION = 5;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Argument</code>.
	 * @see Argument
	 */
	public static final int ARGUMENT = 6;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ArrayAccess</code>.
	 * @see ArrayAccess
	 */
	public static final int ARRAY_ACCESS = 7;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ArrayInitializer</code>.
	 * @see ArrayInitializer
	 */
	public static final int ARRAY_INITIALIZER = 8;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ArrayInitializerFragment</code>.
	 * @see ArrayInitializerFragment
	 */
	public static final int ARRAY_INITIALIZER_FRAGMENT = 9;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ArrayLiteral</code>.
	 * @see ArrayLiteral
	 */
	public static final int ARRAY_LITERAL = 10;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AsmBlock</code>.
	 * @see AsmBlock
	 */
	public static final int ASM_BLOCK = 11;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AsmStatement</code>.
	 * @see AsmStatement
	 */
	public static final int ASM_STATEMENT = 12;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AsmToken</code>.
	 * @see AsmToken
	 */
	public static final int ASM_TOKEN = 13;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AssertExpression</code>.
	 * @see AssertExpression
	 */
	public static final int ASSERT_EXPRESSION = 14;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Assignment</code>.
	 * @see Assignment
	 */
	public static final int ASSIGNMENT = 15;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>AssociativeArrayType</code>.
	 * @see AssociativeArrayType
	 */
	public static final int ASSOCIATIVE_ARRAY_TYPE = 16;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>BaseClass</code>.
	 * @see BaseClass
	 */
	public static final int BASE_CLASS = 17;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Block</code>.
	 * @see Block
	 */
	public static final int BLOCK = 18;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>BooleanLiteral</code>.
	 * @see BooleanLiteral
	 */
	public static final int BOOLEAN_LITERAL = 19;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>BreakStatement</code>.
	 * @see BreakStatement
	 */
	public static final int BREAK_STATEMENT = 20;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>CallExpression</code>.
	 * @see CallExpression
	 */
	public static final int CALL_EXPRESSION = 21;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>CastExpression</code>.
	 * @see CastExpression
	 */
	public static final int CAST_EXPRESSION = 22;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>CatchClause</code>.
	 * @see CatchClause
	 */
	public static final int CATCH_CLAUSE = 23;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>CharacterLiteral</code>.
	 * @see CharacterLiteral
	 */
	public static final int CHARACTER_LITERAL = 24;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>CodeComment</code>.
	 * @see CodeComment
	 */
	public static final int CODE_COMMENT = 25;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>CompilationUnit</code>.
	 * @see CompilationUnit
	 */
	public static final int COMPILATION_UNIT = 26;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ConditionalExpression</code>.
	 * @see ConditionalExpression
	 */
	public static final int CONDITIONAL_EXPRESSION = 27;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ConstructorDeclaration</code>.
	 * @see ConstructorDeclaration
	 */
	public static final int CONSTRUCTOR_DECLARATION = 28;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ContinueStatement</code>.
	 * @see ContinueStatement
	 */
	public static final int CONTINUE_STATEMENT = 29;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DDocComment</code>.
	 * @see DDocComment
	 */
	public static final int D_DOC_COMMENT = 30;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DebugAssignment</code>.
	 * @see DebugAssignment
	 */
	public static final int DEBUG_ASSIGNMENT = 31;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DebugDeclaration</code>.
	 * @see DebugDeclaration
	 */
	public static final int DEBUG_DECLARATION = 32;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DebugStatement</code>.
	 * @see DebugStatement
	 */
	public static final int DEBUG_STATEMENT = 33;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DeclarationStatement</code>.
	 * @see DeclarationStatement
	 */
	public static final int DECLARATION_STATEMENT = 34;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DefaultStatement</code>.
	 * @see DefaultStatement
	 */
	public static final int DEFAULT_STATEMENT = 35;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DelegateType</code>.
	 * @see DelegateType
	 */
	public static final int DELEGATE_TYPE = 36;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DeleteExpression</code>.
	 * @see DeleteExpression
	 */
	public static final int DELETE_EXPRESSION = 37;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DollarLiteral</code>.
	 * @see DollarLiteral
	 */
	public static final int DOLLAR_LITERAL = 38;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DoStatement</code>.
	 * @see DoStatement
	 */
	public static final int DO_STATEMENT = 39;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DotIdentifierExpression</code>.
	 * @see DotIdentifierExpression
	 */
	public static final int DOT_IDENTIFIER_EXPRESSION = 40;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DotTemplateTypeExpression</code>.
	 * @see DotTemplateTypeExpression
	 */
	public static final int DOT_TEMPLATE_TYPE_EXPRESSION = 41;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>DynamicArrayType</code>.
	 * @see DynamicArrayType
	 */
	public static final int DYNAMIC_ARRAY_TYPE = 42;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>EnumDeclaration</code>.
	 * @see EnumDeclaration
	 */
	public static final int ENUM_DECLARATION = 43;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>EnumMember</code>.
	 * @see EnumMember
	 */
	public static final int ENUM_MEMBER = 44;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ExpressionInitializer</code>.
	 * @see ExpressionInitializer
	 */
	public static final int EXPRESSION_INITIALIZER = 45;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ExpressionStatement</code>.
	 * @see ExpressionStatement
	 */
	public static final int EXPRESSION_STATEMENT = 46;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ExternDeclaration</code>.
	 * @see ExternDeclaration
	 */
	public static final int EXTERN_DECLARATION = 47;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ForeachStatement</code>.
	 * @see ForeachStatement
	 */
	public static final int FOREACH_STATEMENT = 48;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ForStatement</code>.
	 * @see ForStatement
	 */
	public static final int FOR_STATEMENT = 49;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>FunctionDeclaration</code>.
	 * @see FunctionDeclaration
	 */
	public static final int FUNCTION_DECLARATION = 50;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>FunctionLiteralDeclarationExpression</code>.
	 * @see FunctionLiteralDeclarationExpression
	 */
	public static final int FUNCTION_LITERAL_DECLARATION_EXPRESSION = 51;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>GotoCaseStatement</code>.
	 * @see GotoCaseStatement
	 */
	public static final int GOTO_CASE_STATEMENT = 52;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>GotoDefaultStatement</code>.
	 * @see GotoDefaultStatement
	 */
	public static final int GOTO_DEFAULT_STATEMENT = 53;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>GotoStatement</code>.
	 * @see GotoStatement
	 */
	public static final int GOTO_STATEMENT = 54;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>IfStatement</code>.
	 * @see IfStatement
	 */
	public static final int IF_STATEMENT = 55;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>IftypeDeclaration</code>.
	 * @see IftypeDeclaration
	 */
	public static final int IFTYPE_DECLARATION = 56;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>IftypeStatement</code>.
	 * @see IftypeStatement
	 */
	public static final int IFTYPE_STATEMENT = 57;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Import</code>.
	 * @see Import
	 */
	public static final int IMPORT = 58;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ImportDeclaration</code>.
	 * @see ImportDeclaration
	 */
	public static final int IMPORT_DECLARATION = 59;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>InfixExpression</code>.
	 * @see InfixExpression
	 */
	public static final int INFIX_EXPRESSION = 60;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>InvariantDeclaration</code>.
	 * @see InvariantDeclaration
	 */
	public static final int INVARIANT_DECLARATION = 61;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>IsTypeExpression</code>.
	 * @see IsTypeExpression
	 */
	public static final int IS_TYPE_EXPRESSION = 62;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>IsTypeSpecializationExpression</code>.
	 * @see IsTypeSpecializationExpression
	 */
	public static final int IS_TYPE_SPECIALIZATION_EXPRESSION = 63;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>LabeledStatement</code>.
	 * @see LabeledStatement
	 */
	public static final int LABELED_STATEMENT = 64;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>MixinDeclaration</code>.
	 * @see TemplateMixinDeclaration
	 */
	public static final int TEMPLATE_MIXIN_DECLARATION = 65;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Modifier</code>.
	 * @see Modifier
	 */
	public static final int MODIFIER = 66;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ModifierDeclaration</code>.
	 * @see ModifierDeclaration
	 */
	public static final int MODIFIER_DECLARATION = 67;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ModuleDeclaration</code>.
	 * @see ModuleDeclaration
	 */
	public static final int MODULE_DECLARATION = 68;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>NewAnonymousClassExpression</code>.
	 * @see NewAnonymousClassExpression
	 */
	public static final int NEW_ANONYMOUS_CLASS_EXPRESSION = 69;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>NewExpression</code>.
	 * @see NewExpression
	 */
	public static final int NEW_EXPRESSION = 70;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>NullLiteral</code>.
	 * @see NullLiteral
	 */
	public static final int NULL_LITERAL = 71;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>NumberLiteral</code>.
	 * @see NumberLiteral
	 */
	public static final int NUMBER_LITERAL = 72;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ParenthesizedExpression</code>.
	 * @see ParenthesizedExpression
	 */
	public static final int PARENTHESIZED_EXPRESSION = 73;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>PointerType</code>.
	 * @see PointerType
	 */
	public static final int POINTER_TYPE = 74;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>PostfixExpression</code>.
	 * @see PostfixExpression
	 */
	public static final int POSTFIX_EXPRESSION = 75;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Pragma</code>.
	 * @see Pragma
	 */
	public static final int PRAGMA = 76;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>PragmaDeclaration</code>.
	 * @see PragmaDeclaration
	 */
	public static final int PRAGMA_DECLARATION = 77;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>PragmaStatement</code>.
	 * @see PragmaStatement
	 */
	public static final int PRAGMA_STATEMENT = 78;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>PrefixExpression</code>.
	 * @see PrefixExpression
	 */
	public static final int PREFIX_EXPRESSION = 79;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>PrimitiveType</code>.
	 * @see PrimitiveType
	 */
	public static final int PRIMITIVE_TYPE = 80;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>QualifiedName</code>.
	 * @see QualifiedName
	 */
	public static final int QUALIFIED_NAME = 81;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>QualifiedType</code>.
	 * @see QualifiedType
	 */
	public static final int QUALIFIED_TYPE = 82;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ReturnStatement</code>.
	 * @see ReturnStatement
	 */
	public static final int RETURN_STATEMENT = 83;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ScopeStatement</code>.
	 * @see ScopeStatement
	 */
	public static final int SCOPE_STATEMENT = 84;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SelectiveImport</code>.
	 * @see SelectiveImport
	 */
	public static final int SELECTIVE_IMPORT = 85;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SimpleName</code>.
	 * @see SimpleName
	 */
	public static final int SIMPLE_NAME = 86;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SimpleType</code>.
	 * @see SimpleType
	 */
	public static final int SIMPLE_TYPE = 87;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SliceExpression</code>.
	 * @see SliceExpression
	 */
	public static final int SLICE_EXPRESSION = 88;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SliceType</code>.
	 * @see SliceType
	 */
	public static final int SLICE_TYPE = 89;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StaticArrayType</code>.
	 * @see StaticArrayType
	 */
	public static final int STATIC_ARRAY_TYPE = 90;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StaticAssert</code>.
	 * @see StaticAssert
	 */
	public static final int STATIC_ASSERT = 91;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StaticAssertStatement</code>.
	 * @see StaticAssertStatement
	 */
	public static final int STATIC_ASSERT_STATEMENT = 92;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StaticIfDeclaration</code>.
	 * @see StaticIfDeclaration
	 */
	public static final int STATIC_IF_DECLARATION = 93;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StaticIfStatement</code>.
	 * @see StaticIfStatement
	 */
	public static final int STATIC_IF_STATEMENT = 94;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StringLiteral</code>.
	 * @see StringLiteral
	 */
	public static final int STRING_LITERAL = 95;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StringsExpression</code>.
	 * @see StringsExpression
	 */
	public static final int STRINGS_EXPRESSION = 96;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StructInitializer</code>.
	 * @see StructInitializer
	 */
	public static final int STRUCT_INITIALIZER = 97;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>StructInitializerFragment</code>.
	 * @see StructInitializerFragment
	 */
	public static final int STRUCT_INITIALIZER_FRAGMENT = 98;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SuperLiteral</code>.
	 * @see SuperLiteral
	 */
	public static final int SUPER_LITERAL = 99;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SwitchCase</code>.
	 * @see SwitchCase
	 */
	public static final int SWITCH_CASE = 100;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SwitchStatement</code>.
	 * @see SwitchStatement
	 */
	public static final int SWITCH_STATEMENT = 101;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>SynchronizedStatement</code>.
	 * @see SynchronizedStatement
	 */
	public static final int SYNCHRONIZED_STATEMENT = 102;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TemplateDeclaration</code>.
	 * @see TemplateDeclaration
	 */
	public static final int TEMPLATE_DECLARATION = 103;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TemplateType</code>.
	 * @see TemplateType
	 */
	public static final int TEMPLATE_TYPE = 104;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ThisLiteral</code>.
	 * @see ThisLiteral
	 */
	public static final int THIS_LITERAL = 105;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ThrowStatement</code>.
	 * @see ThrowStatement
	 */
	public static final int THROW_STATEMENT = 106;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TryStatement</code>.
	 * @see TryStatement
	 */
	public static final int TRY_STATEMENT = 107;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TupleTemplateParameter</code>.
	 * @see TupleTemplateParameter
	 */
	public static final int TUPLE_TEMPLATE_PARAMETER = 108;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypedefDeclaration</code>.
	 * @see TypedefDeclaration
	 */
	public static final int TYPEDEF_DECLARATION = 109;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypedefDeclarationFragment</code>.
	 * @see TypedefDeclarationFragment
	 */
	public static final int TYPEDEF_DECLARATION_FRAGMENT = 110;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypeDotIdentifierExpression</code>.
	 * @see TypeDotIdentifierExpression
	 */
	public static final int TYPE_DOT_IDENTIFIER_EXPRESSION = 111;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypeExpression</code>.
	 * @see TypeExpression
	 */
	public static final int TYPE_EXPRESSION = 112;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypeidExpression</code>.
	 * @see TypeidExpression
	 */
	public static final int TYPEID_EXPRESSION = 113;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypeofType</code>.
	 * @see TypeofType
	 */
	public static final int TYPEOF_TYPE = 114;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>TypeTemplateParameter</code>.
	 * @see TypeTemplateParameter
	 */
	public static final int TYPE_TEMPLATE_PARAMETER = 115;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>UnitTestDeclaration</code>.
	 * @see UnitTestDeclaration
	 */
	public static final int UNIT_TEST_DECLARATION = 116;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>ValueTemplateParameter</code>.
	 * @see ValueTemplateParameter
	 */
	public static final int VALUE_TEMPLATE_PARAMETER = 117;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VariableDeclaration</code>.
	 * @see VariableDeclaration
	 */
	public static final int VARIABLE_DECLARATION = 118;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VariableDeclarationFragment</code>.
	 * @see VariableDeclarationFragment
	 */
	public static final int VARIABLE_DECLARATION_FRAGMENT = 119;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>Version</code>.
	 * @see Version
	 */
	public static final int VERSION = 120;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VersionAssignment</code>.
	 * @see VersionAssignment
	 */
	public static final int VERSION_ASSIGNMENT = 121;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VersionDeclaration</code>.
	 * @see VersionDeclaration
	 */
	public static final int VERSION_DECLARATION = 122;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VersionStatement</code>.
	 * @see VersionStatement
	 */
	public static final int VERSION_STATEMENT = 123;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VoidInitializer</code>.
	 * @see VoidInitializer
	 */
	public static final int VOID_INITIALIZER = 124;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>VolatileStatement</code>.
	 * @see VolatileStatement
	 */
	public static final int VOLATILE_STATEMENT = 125;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>WhileStatement</code>.
	 * @see WhileStatement
	 */
	public static final int WHILE_STATEMENT = 126;
	 
	/**
	 * Node type constant indicating a node of type 
	 * <code>WithStatement</code>.
	 * @see WithStatement
	 */
	public static final int WITH_STATEMENT = 127;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>MixinDeclaration</code>.
	 * @see MixinDeclaration
	 */
	public static final int MIXIN_DECLARATION = 128;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>MixinExpression</code>.
	 * @see MixinExpression
	 */
	public static final int MIXIN_EXPRESSION = 129;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>FileImportExpression</code>.
	 * @see FileImportExpression
	 */
	public static final int FILE_IMPORT_EXPRESSION = 130;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>EmptyStatement</code>.
	 * @see EmptyStatement
	 */
	public static final int EMPTY_STATEMENT = 131;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>AssociativeArrayLiteral</code>.
	 * @see AssociativeArrayLiteral
	 */
	public static final int ASSOCIATIVE_ARRAY_LITERAL = 132;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>AssociativeArrayLiteralFragment</code>.
	 * @see AssociativeArrayLiteralFragment
	 */
	public static final int ASSOCIATIVE_ARRAY_LITERAL_FRAGMENT = 133;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>ForeachRangeStatement</code>.
	 * @see ForeachRangeStatement
	 */
	public static final int FOREACH_RANGE_STATEMENT = 134;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>TraitsExpression</code>.
	 * @see TraitsExpression
	 */
	public static final int TRAITS_EXPRESSION = 135;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>CastToModifierExpression</code>.
	 * @see CastToModifierExpression
	 */
	public static final int CAST_TO_MODIFIER_EXPRESSION = 136;
	
	/**
	 * Node type constant indicating a node of type 
	 * <code>ModifiedType</code>.
	 * @see ModifiedType
	 */
	public static final int MODIFIED_TYPE = 137;
	
	/**
	 * Returns the node class for the corresponding node type.
	 * 
	 * @param nodeType AST node type
	 * @return the corresponding <code>ASTNode</code> subclass
	 * @exception IllegalArgumentException if <code>nodeType</code> is 
	 * not a legal AST node type
	 * @see #getNodeType()
	 */
	public static Class nodeClassForType(int nodeType) {
		switch (nodeType) {
		case AGGREGATE_DECLARATION:
			return AggregateDeclaration.class;
		case ALIAS_DECLARATION:
			return AliasDeclaration.class;
		case ALIAS_DECLARATION_FRAGMENT:
			return AliasDeclarationFragment.class;
		case ALIAS_TEMPLATE_PARAMETER:
			return AliasTemplateParameter.class;
		case ALIGN_DECLARATION:
			return AlignDeclaration.class;
		case ARGUMENT:
			return Argument.class;
		case ARRAY_ACCESS:
			return ArrayAccess.class;
		case ARRAY_INITIALIZER:
			return ArrayInitializer.class;
		case ARRAY_INITIALIZER_FRAGMENT:
			return ArrayInitializerFragment.class;
		case ARRAY_LITERAL:
			return ArrayLiteral.class;
		case ASM_BLOCK:
			return AsmBlock.class;
		case ASM_STATEMENT:
			return AsmStatement.class;
		case ASM_TOKEN:
			return AsmToken.class;
		case ASSERT_EXPRESSION:
			return AssertExpression.class;
		case ASSIGNMENT:
			return Assignment.class;
		case ASSOCIATIVE_ARRAY_TYPE:
			return AssociativeArrayType.class;
		case ASSOCIATIVE_ARRAY_LITERAL:
			return AssociativeArrayLiteral.class;
		case ASSOCIATIVE_ARRAY_LITERAL_FRAGMENT:
			return AssociativeArrayLiteralFragment.class;
		case BASE_CLASS:
			return BaseClass.class;
		case BLOCK:
			return Block.class;
		case BOOLEAN_LITERAL:
			return BooleanLiteral.class;
		case BREAK_STATEMENT:
			return BreakStatement.class;
		case CALL_EXPRESSION:
			return CallExpression.class;
		case CAST_EXPRESSION:
			return CastExpression.class;
		case CAST_TO_MODIFIER_EXPRESSION:
			return CastToModifierExpression.class;
		case CATCH_CLAUSE:
			return CatchClause.class;
		case CHARACTER_LITERAL:
			return CharacterLiteral.class;
		case CODE_COMMENT:
			return CodeComment.class;
		case COMPILATION_UNIT:
			return CompilationUnit.class;
		case CONDITIONAL_EXPRESSION:
			return ConditionalExpression.class;
		case CONSTRUCTOR_DECLARATION:
			return ConstructorDeclaration.class;
		case CONTINUE_STATEMENT:
			return ContinueStatement.class;
		case D_DOC_COMMENT:
			return DDocComment.class;
		case DEBUG_ASSIGNMENT:
			return DebugAssignment.class;
		case DEBUG_DECLARATION:
			return DebugDeclaration.class;
		case DEBUG_STATEMENT:
			return DebugStatement.class;
		case DECLARATION_STATEMENT:
			return DeclarationStatement.class;
		case DEFAULT_STATEMENT:
			return DefaultStatement.class;
		case DELEGATE_TYPE:
			return DelegateType.class;
		case DELETE_EXPRESSION:
			return DeleteExpression.class;
		case DOLLAR_LITERAL:
			return DollarLiteral.class;
		case DO_STATEMENT:
			return DoStatement.class;
		case DOT_IDENTIFIER_EXPRESSION:
			return DotIdentifierExpression.class;
		case DOT_TEMPLATE_TYPE_EXPRESSION:
			return DotTemplateTypeExpression.class;
		case DYNAMIC_ARRAY_TYPE:
			return DynamicArrayType.class;
		case ENUM_DECLARATION:
			return EnumDeclaration.class;
		case ENUM_MEMBER:
			return EnumMember.class;
		case EXPRESSION_INITIALIZER:
			return ExpressionInitializer.class;
		case EXPRESSION_STATEMENT:
			return ExpressionStatement.class;
		case EXTERN_DECLARATION:
			return ExternDeclaration.class;
		case FOREACH_RANGE_STATEMENT:
			return ForeachRangeStatement.class;
		case FOREACH_STATEMENT:
			return ForeachStatement.class;
		case FOR_STATEMENT:
			return ForStatement.class;
		case FUNCTION_DECLARATION:
			return FunctionDeclaration.class;
		case FUNCTION_LITERAL_DECLARATION_EXPRESSION:
			return FunctionLiteralDeclarationExpression.class;
		case GOTO_CASE_STATEMENT:
			return GotoCaseStatement.class;
		case GOTO_DEFAULT_STATEMENT:
			return GotoDefaultStatement.class;
		case GOTO_STATEMENT:
			return GotoStatement.class;
		case IF_STATEMENT:
			return IfStatement.class;
		case IFTYPE_DECLARATION:
			return IftypeDeclaration.class;
		case IFTYPE_STATEMENT:
			return IftypeStatement.class;
		case IMPORT:
			return Import.class;
		case IMPORT_DECLARATION:
			return ImportDeclaration.class;
		case INFIX_EXPRESSION:
			return InfixExpression.class;
		case INVARIANT_DECLARATION:
			return InvariantDeclaration.class;
		case IS_TYPE_EXPRESSION:
			return IsTypeExpression.class;
		case IS_TYPE_SPECIALIZATION_EXPRESSION:
			return IsTypeSpecializationExpression.class;
		case LABELED_STATEMENT:
			return LabeledStatement.class;
		case TEMPLATE_MIXIN_DECLARATION:
			return TemplateMixinDeclaration.class;
		case MODIFIER:
			return Modifier.class;
		case MODIFIED_TYPE:
			return ModifiedType.class;
		case MODIFIER_DECLARATION:
			return ModifierDeclaration.class;
		case MODULE_DECLARATION:
			return ModuleDeclaration.class;
		case NEW_ANONYMOUS_CLASS_EXPRESSION:
			return NewAnonymousClassExpression.class;
		case NEW_EXPRESSION:
			return NewExpression.class;
		case NULL_LITERAL:
			return NullLiteral.class;
		case NUMBER_LITERAL:
			return NumberLiteral.class;
		case PARENTHESIZED_EXPRESSION:
			return ParenthesizedExpression.class;
		case POINTER_TYPE:
			return PointerType.class;
		case POSTFIX_EXPRESSION:
			return PostfixExpression.class;
		case PRAGMA:
			return Pragma.class;
		case PRAGMA_DECLARATION:
			return PragmaDeclaration.class;
		case PRAGMA_STATEMENT:
			return PragmaStatement.class;
		case PREFIX_EXPRESSION:
			return PrefixExpression.class;
		case PRIMITIVE_TYPE:
			return PrimitiveType.class;
		case QUALIFIED_NAME:
			return QualifiedName.class;
		case QUALIFIED_TYPE:
			return QualifiedType.class;
		case RETURN_STATEMENT:
			return ReturnStatement.class;
		case SCOPE_STATEMENT:
			return ScopeStatement.class;
		case SELECTIVE_IMPORT:
			return SelectiveImport.class;
		case SIMPLE_NAME:
			return SimpleName.class;
		case SIMPLE_TYPE:
			return SimpleType.class;
		case SLICE_EXPRESSION:
			return SliceExpression.class;
		case SLICE_TYPE:
			return SliceType.class;
		case STATIC_ARRAY_TYPE:
			return StaticArrayType.class;
		case STATIC_ASSERT:
			return StaticAssert.class;
		case STATIC_ASSERT_STATEMENT:
			return StaticAssertStatement.class;
		case STATIC_IF_DECLARATION:
			return StaticIfDeclaration.class;
		case STATIC_IF_STATEMENT:
			return StaticIfStatement.class;
		case STRING_LITERAL:
			return StringLiteral.class;
		case STRINGS_EXPRESSION:
			return StringsExpression.class;
		case STRUCT_INITIALIZER:
			return StructInitializer.class;
		case STRUCT_INITIALIZER_FRAGMENT:
			return StructInitializerFragment.class;
		case SUPER_LITERAL:
			return SuperLiteral.class;
		case SWITCH_CASE:
			return SwitchCase.class;
		case SWITCH_STATEMENT:
			return SwitchStatement.class;
		case SYNCHRONIZED_STATEMENT:
			return SynchronizedStatement.class;
		case TEMPLATE_DECLARATION:
			return TemplateDeclaration.class;
		case TEMPLATE_TYPE:
			return TemplateType.class;
		case THIS_LITERAL:
			return ThisLiteral.class;
		case THROW_STATEMENT:
			return ThrowStatement.class;
		case TRAITS_EXPRESSION:
			return TraitsExpression.class;
		case TRY_STATEMENT:
			return TryStatement.class;
		case TUPLE_TEMPLATE_PARAMETER:
			return TupleTemplateParameter.class;
		case TYPEDEF_DECLARATION:
			return TypedefDeclaration.class;
		case TYPEDEF_DECLARATION_FRAGMENT:
			return TypedefDeclarationFragment.class;
		case TYPE_DOT_IDENTIFIER_EXPRESSION:
			return TypeDotIdentifierExpression.class;
		case TYPE_EXPRESSION:
			return TypeExpression.class;
		case TYPEID_EXPRESSION:
			return TypeidExpression.class;
		case TYPEOF_TYPE:
			return TypeofType.class;
		case TYPE_TEMPLATE_PARAMETER:
			return TypeTemplateParameter.class;
		case UNIT_TEST_DECLARATION:
			return UnitTestDeclaration.class;
		case VALUE_TEMPLATE_PARAMETER:
			return ValueTemplateParameter.class;
		case VARIABLE_DECLARATION:
			return VariableDeclaration.class;
		case VARIABLE_DECLARATION_FRAGMENT:
			return VariableDeclarationFragment.class;
		case VERSION:
			return Version.class;
		case VERSION_ASSIGNMENT:
			return VersionAssignment.class;
		case VERSION_DECLARATION:
			return VersionDeclaration.class;
		case VERSION_STATEMENT:
			return VersionStatement.class;
		case VOID_INITIALIZER:
			return VoidInitializer.class;
		case VOLATILE_STATEMENT:
			return VolatileStatement.class;
		case WHILE_STATEMENT:
			return WhileStatement.class;
		case WITH_STATEMENT:
			return WithStatement.class;
		case MIXIN_DECLARATION:
			return MixinDeclaration.class;
		case MIXIN_EXPRESSION:
			return MixinExpression.class;
		case FILE_IMPORT_EXPRESSION:
			return FileImportExpression.class;
		case EMPTY_STATEMENT:
			return EmptyStatement.class;
		}
		return null;
	}
	
	/**
	 * Owning AST.
     * <p>
     * N.B. This ia a private field, but declared as package-visible
     * for more efficient access from inner classes.
     * </p>
	 */
	final AST ast;
	
	/**
	 * Parent AST node, or <code>null</code> if this node is a root.
	 * Initially <code>null</code>.
	 */
	private ASTNode parent = null;
	
	/**
	 * An unmodifiable empty map (used to implement <code>properties()</code>).
	 */
	private static final Map<Object, Object> UNMODIFIABLE_EMPTY_MAP
		= Collections.unmodifiableMap(new HashMap<Object, Object>(1));
	
	/**
	 * Primary field used in representing node properties efficiently.
	 * If <code>null</code>, this node has no properties.
	 * If a <code>String</code>, this is the name of this node's sole property,
	 * and <code>property2</code> contains its value.
	 * If a <code>HashMap</code>, this is the table of property name-value
	 * mappings; <code>property2</code>, if non-null is its unmodifiable
	 * equivalent.
	 * Initially <code>null</code>.
	 * 
	 * @see #property2
	 */
	private Object property1 = null;
	
	/**
	 * Auxillary field used in representing node properties efficiently.
	 * 
	 * @see #property1
	 */
	private Object property2 = null;
	
	/**
	 * A character index into the original source string, 
	 * or <code>-1</code> if no source position information is available
	 * for this node; <code>-1</code> by default.
	 */
	private int startPosition = -1;
	
	/**
	 * A character length, or <code>0</code> if no source position
	 * information is recorded for this node; <code>0</code> by default.
	 */
	private int length = 0;
	
	/**
	 * Flag constant (bit mask, value 1) indicating that there is something
	 * not quite right with this AST node.
	 * <p>
	 * The standard parser (<code>ASTParser</code>) sets this
	 * flag on a node to indicate a syntax error detected in the vicinity.
	 * </p>
	 */
	public static final int MALFORMED = 1;

	/**
	 * Flag constant (bit mask, value 2) indicating that this is a node
	 * that was created by the parser (as opposed to one created by another
	 * party).
	 * <p>
	 * The standard parser (<code>ASTParser</code>) sets this
	 * flag on the nodes it creates.
	 * </p>
	 * @since 3.0
	 */
	public static final int ORIGINAL = 2;

	/**
	 * Flag constant (bit mask, value 4) indicating that this node
	 * is unmodifiable. When a node is marked unmodifiable, the
	 * following operations result in a runtime exception:
	 * <ul>
	 * <li>Change a simple property of this node.</li>
	 * <li>Add or remove a child node from this node.</li>
	 * <li>Parent (or reparent) this node.</li>
	 * </ul>
	 * <p>
	 * The standard parser (<code>ASTParser</code>) does not set
	 * this flag on the nodes it creates. However, clients may set
	 * this flag on a node to prevent further modification of the
	 * its structural properties.
	 * </p>
	 * @since 3.0
	 */
	public static final int PROTECT = 4;

	/**
	 * Flag constant (bit mask, value 8) indicating that this node
	 * or a part of this node is recovered from source that contains
	 * a syntax error detected in the vicinity.
	 * <p>
	 * The standard parser (<code>ASTParser</code>) sets this
	 * flag on a node to indicate a recovered node.
	 * </p>
	 * @since 3.2
	 */
	public static final int RECOVERED = 8;
	
	/**
	 * int containing the node type in the top 16 bits and
	 * flags in the bottom 16 bits; none set by default.
     * <p>
     * N.B. This is a private field, but declared as package-visible
     * for more efficient access from inner classes.
     * </p>
	 * 
	 * @see #MALFORMED
	 */
	int typeAndFlags = 0;
	
	/**
	 * Property of parent in which this node is a child, or <code>null</code>
	 * if this node is a root. Initially <code>null</code>.
	 * 
	 * @see #getLocationInParent
	 * @since 3.0
	 */
	private StructuralPropertyDescriptor location = null;
	
	/** Internal convenience constant indicating that there is definite risk of cycles.
	 * @since 3.0
	 */ 
	static final boolean CYCLE_RISK = true;
	
	/** Internal convenience constant indicating that there is no risk of cycles.
	 * @since 3.0
	 */ 
	static final boolean NO_CYCLE_RISK = false;
	
	/** Internal convenience constant indicating that a structural property is mandatory.
	 * @since 3.0
	 */ 
	static final boolean MANDATORY = true;
	
	/** Internal convenience constant indicating that a structural property is optional.
	 * @since 3.0
	 */ 
	static final boolean OPTIONAL = false;
	
	/**
	 * A specialized implementation of a list of ASTNodes. The
	 * implementation is based on an ArrayList.
	 */ 
	class NodeList extends AbstractList {
		
		/**
		 * The underlying list in which the nodes of this list are
		 * stored (element type: <code>ASTNode</code>).
		 * <p>
		 * Be stingy on storage - assume that list will be empty.
		 * </p>
		 * <p>
		 * This field declared default visibility (rather than private)
		 * so that accesses from <code>NodeList.Cursor</code> do not require
		 * a synthetic accessor method.
		 * </p>
		 */
		ArrayList store = new ArrayList(0);
		
		/**
		 * The property descriptor for this list.
		 */
		ChildListPropertyDescriptor propertyDescriptor;
		
		/**
		 * A cursor for iterating over the elements of the list.
		 * Does not lose its position if the list is changed during
		 * the iteration.
		 */
		class Cursor implements Iterator {
			/**
			 * The position of the cursor between elements. If the value
			 * is N, then the cursor sits between the element at positions
			 * N-1 and N. Initially just before the first element of the
			 * list.
			 */
			private int position = 0;
			
			/* (non-Javadoc)
			 * Method declared on <code>Iterator</code>.
			 */
			public boolean hasNext() {
				return this.position < NodeList.this.store.size();
			}
			
			/* (non-Javadoc)
			 * Method declared on <code>Iterator</code>.
			 */
			public Object next() {
				Object result = NodeList.this.store.get(this.position);
				this.position++;
				return result;
		    }
			
			/* (non-Javadoc)
			 * Method declared on <code>Iterator</code>.
			 */
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			/**
			 * Adjusts this cursor to accomodate an add/remove at the given
			 * index.
			 * 
			 * @param index the position at which the element was added
			 *    or removed
			 * @param delta +1 for add, and -1 for remove
			 */
			void update(int index, int delta) {
				if (this.position > index) {
					// the cursor has passed the added or removed element
					this.position += delta;
				}
			}
		}

		/**
		 * A list of currently active cursors (element type:
		 * <code>Cursor</code>), or <code>null</code> if there are no
		 * active cursors.
		 * <p>
		 * It is important for storage considerations to maintain the
		 * null-means-empty invariant; otherwise, every NodeList instance
		 * will waste a lot of space. A cursor is needed only for the duration
		 * of a visit to the child nodes. Under normal circumstances, only a 
		 * single cursor is needed; multiple cursors are only required if there
		 * are multiple visits going on at the same time.
		 * </p>
		 */
		private List cursors = null;

		/**
		 * Creates a new empty list of nodes owned by this node.
		 * This node will be the common parent of all nodes added to 
		 * this list.
		 * 
		 * @param property the property descriptor
		 * @since 3.0
		 */
		NodeList(ChildListPropertyDescriptor property) {
			super();
			this.propertyDescriptor = property;
		}
	
		/* (non-javadoc)
		 * @see java.util.AbstractCollection#size()
		 */
		public int size() {
			return this.store.size();
		}
	
		/* (non-javadoc)
		 * @see AbstractList#get(int)
		 */
		public Object get(int index) {
			return this.store.get(index);
		}
	
		/* (non-javadoc)
		 * @see List#set(int, java.lang.Object)
		 */
		public Object set(int index, Object element) {
		    if (element == null) {
		        throw new IllegalArgumentException();
		    }
			if ((ASTNode.this.typeAndFlags & PROTECT) != 0) {
				// this node is protected => cannot gain or lose children
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			// delink old child from parent, and link new child to parent
			ASTNode newChild = (ASTNode) element;
			ASTNode oldChild = (ASTNode) this.store.get(index);
			if (oldChild == newChild) {
				return oldChild;
			}
			if ((oldChild.typeAndFlags & PROTECT) != 0) {
				// old child is protected => cannot be unparented
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			ASTNode.checkNewChild(ASTNode.this, newChild, this.propertyDescriptor.cycleRisk, this.propertyDescriptor.elementType);
			ASTNode.this.ast.preReplaceChildEvent(ASTNode.this, oldChild, newChild, this.propertyDescriptor);
			
			Object result = this.store.set(index, newChild);
			// n.b. setParent will call ast.modifying()
			oldChild.setParent(null, null);
			newChild.setParent(ASTNode.this, this.propertyDescriptor);
			ASTNode.this.ast.postReplaceChildEvent(ASTNode.this, oldChild, newChild, this.propertyDescriptor);
			return result;
		}
		
		/* (non-javadoc)
		 * @see List#add(int, java.lang.Object)
		 */
		public void add(int index, Object element) {
		    if (element == null) {
		        throw new IllegalArgumentException();
		    }
			if ((ASTNode.this.typeAndFlags & PROTECT) != 0) {
				// this node is protected => cannot gain or lose children
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			// link new child to parent
			ASTNode newChild = (ASTNode) element;
			ASTNode.checkNewChild(ASTNode.this, newChild, this.propertyDescriptor.cycleRisk, this.propertyDescriptor.elementType);
			ASTNode.this.ast.preAddChildEvent(ASTNode.this, newChild, this.propertyDescriptor);
			
			
			this.store.add(index, element);
			updateCursors(index, +1);
			// n.b. setParent will call ast.modifying()
			newChild.setParent(ASTNode.this, this.propertyDescriptor);
			ASTNode.this.ast.postAddChildEvent(ASTNode.this, newChild, this.propertyDescriptor);
		}
		
		/* (non-javadoc)
		 * @see List#remove(int)
		 */
		public Object remove(int index) {
			if ((ASTNode.this.typeAndFlags & PROTECT) != 0) {
				// this node is protected => cannot gain or lose children
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			// delink old child from parent
			ASTNode oldChild = (ASTNode) this.store.get(index);
			if ((oldChild.typeAndFlags & PROTECT) != 0) {
				// old child is protected => cannot be unparented
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			
			ASTNode.this.ast.preRemoveChildEvent(ASTNode.this, oldChild, this.propertyDescriptor);
			// n.b. setParent will call ast.modifying()
			oldChild.setParent(null, null);
			Object result = this.store.remove(index);
			updateCursors(index, -1);
			ASTNode.this.ast.postRemoveChildEvent(ASTNode.this, oldChild, this.propertyDescriptor);
			return result;

		}
		
		/**
		 * Allocate a cursor to use for a visit. The client must call
		 * <code>releaseCursor</code> when done.
		 * <p>
		 * This method is internally synchronized on this NodeList.
		 * It is thread-safe to create a cursor.
		 * </p>
		 * 
		 * @return a new cursor positioned before the first element 
		 *    of the list
		 */
		Cursor newCursor() {
			synchronized (this) {
				// serialize cursor management on this NodeList
				if (this.cursors == null) {
					// convert null to empty list
					this.cursors = new ArrayList(1);
				}
				Cursor result = new Cursor();
				this.cursors.add(result);
				return result;
			}
		}
		
		/**
		 * Releases the given cursor at the end of a visit.
		 * <p>
		 * This method is internally synchronized on this NodeList.
		 * It is thread-safe to release a cursor.
		 * </p>
		 * 
		 * @param cursor the cursor
		 */
		void releaseCursor(Cursor cursor) {
			synchronized (this) {
				// serialize cursor management on this NodeList
				this.cursors.remove(cursor);
				if (this.cursors.isEmpty()) {
					// important: convert empty list back to null
					// otherwise the node will hang on to needless junk
					this.cursors = null;
				}
			}
		}

		/**
		 * Adjusts all cursors to accomodate an add/remove at the given
		 * index.
		 * <p>
		 * This method is only used when the list is being modified.
		 * The AST is not thread-safe if any of the clients are modifying it.
		 * </p>
		 * 
		 * @param index the position at which the element was added
		 *    or removed
		 * @param delta +1 for add, and -1 for remove
		 */
		private void updateCursors(int index, int delta) {
			if (this.cursors == null) {
				// there are no cursors to worry about
				return;
			}
			for (Iterator it = this.cursors.iterator(); it.hasNext(); ) {
				Cursor c = (Cursor) it.next();
				c.update(index, delta);
			}
		}
		
		/**
		 * Returns an estimate of the memory footprint of this node list 
		 * instance in bytes.
	     * <ul>
	     * <li>1 object header for the NodeList instance</li>
	     * <li>5 4-byte fields of the NodeList instance</li>
	     * <li>0 for cursors since null unless walk in progress</li>
	     * <li>1 object header for the ArrayList instance</li>
	     * <li>2 4-byte fields of the ArrayList instance</li>
	     * <li>1 object header for an Object[] instance</li>
	     * <li>4 bytes in array for each element</li>
	     * </ul>
	 	 * 
		 * @return the size of this node list in bytes
		 */
		int memSize() {
			int result = HEADERS + 5 * 4;
			result += HEADERS + 2 * 4;
			result += HEADERS + 4 * size();
			return result;
		}

		/**
		 * Returns an estimate of the memory footprint in bytes of this node
		 * list and all its subtrees.
		 * 
		 * @return the size of this list of subtrees in bytes
		 */
		int listSize() {
			int result = memSize();
			for (Iterator it = iterator(); it.hasNext(); ) {
				ASTNode child = (ASTNode) it.next();
				result += child.treeSize();
			}
			return result;
		}
	}
	
	/**
	 * Creates a new AST node owned by the given AST. Once established,
	 * the relationship between an AST node and its owning AST does not change
	 * over the lifetime of the node. The new node has no parent node,
	 * and no properties.
	 * <p>
	 * N.B. This constructor is package-private; all subclasses my be 
	 * declared in the same package; clients are unable to declare 
	 * additional subclasses.
	 * </p>
	 * 
	 * @param ast the AST that is to own this node
	 */
	ASTNode(AST ast) {
		if (ast == null) {
			throw new IllegalArgumentException();
		}
		
		this.ast = ast;
		setNodeType(getNodeType0());
		setFlags(ast.getDefaultNodeFlag());		
		// setFlags calls modifying();
	}
	
	/**
	 * Returns this node's AST.
	 * <p>
	 * Note that the relationship between an AST node and its owing AST does
	 * not change over the lifetime of a node.
	 * </p>
	 * 
	 * @return the AST that owns this node
	 */ 
	public final AST getAST() {
		return this.ast;
	}
	
	/**
	 * Returns this node's parent node, or <code>null</code> if this is the
	 * root node.
	 * <p>
	 * Note that the relationship between an AST node and its parent node
	 * may change over the lifetime of a node.
	 * </p>
	 * 
	 * @return the parent of this node, or <code>null</code> if none
	 */ 
	public final ASTNode getParent() {
		return this.parent;
	}
	
	/**
	 * Returns the location of this node within its parent,
	 * or <code>null</code> if this is a root node.
	 * <p>
	 * <pre>
	 * ASTNode node = ...;
	 * ASTNode parent = node.getParent();
	 * StructuralPropertyDescriptor location = node.getLocationInParent();
	 * assert (parent != null) == (location != null);
	 * if ((location != null) && location.isChildProperty())
	 *    assert parent.getStructuralProperty(location) == node;
	 * if ((location != null) && location.isChildListProperty())
	 *    assert ((List) parent.getStructuralProperty(location)).contains(node);
	 * </pre>
	 * </p>
	 * <p>
	 * Note that the relationship between an AST node and its parent node
	 * may change over the lifetime of a node.
	 * </p>
	 * 
	 * @return the location of this node in its parent, 
	 * or <code>null</code> if this node has no parent
	 */ 
	public final StructuralPropertyDescriptor getLocationInParent() {
		return this.location;
	}
	
	/**
	 * Returns the root node at or above this node; returns this node if 
	 * it is a root.
	 * 
	 * @return the root node at or above this node
	 */ 
	public final ASTNode getRoot() {
		ASTNode candidate = this;
		while (true) {
			ASTNode p = candidate.getParent();
			if (p == null) {
				// candidate has no parent - that's the guy
				return candidate;
			}
			candidate = p;
		}
	}
	
	/**
	 * Returns the value of the given structural property for this node. The value
	 * returned depends on the kind of property:
	 * <ul>
	 * <li>{@link SimplePropertyDescriptor} - the value of the given simple property,
	 * or <code>null</code> if none; primitive values are "boxed"</li>
	 * <li>{@link ChildPropertyDescriptor} - the child node (type <code>ASTNode</code>),
	 * or <code>null</code> if none</li>
	 * <li>{@link ChildListPropertyDescriptor} - the list (element type: {@link ASTNode})</li>
	 * </ul>
	 * 
	 * @param property the property
	 * @return the value, or <code>null</code> if none
	 * @exception RuntimeException if this node does not have the given property
	 */
	public final Object getStructuralProperty(StructuralPropertyDescriptor property) {
		if (property instanceof SimplePropertyDescriptor) {
			SimplePropertyDescriptor p = (SimplePropertyDescriptor) property;
			if (p.getValueType() == int.class) {
				int result = internalGetSetIntProperty(p, true, 0);
				return new Integer(result);
			} else if (p.getValueType() == boolean.class) {
				boolean result = internalGetSetBooleanProperty(p, true, false);
				return Boolean.valueOf(result);
			} else {
				return internalGetSetObjectProperty(p, true, null);
			}
		}
		if (property instanceof ChildPropertyDescriptor) {
			return internalGetSetChildProperty((ChildPropertyDescriptor) property, true, null);
		}
		if (property instanceof ChildListPropertyDescriptor) {
			return internalGetChildListProperty((ChildListPropertyDescriptor) property);
		}
		throw new IllegalArgumentException();
	}
	
	/**
	 * Sets the value of the given structural property for this node. The value
	 * passed depends on the kind of property:
	 * <ul>
	 * <li>{@link SimplePropertyDescriptor} - the new value of the given simple property,
	 * or <code>null</code> if none; primitive values are "boxed"</li>
	 * <li>{@link ChildPropertyDescriptor} - the new child node (type <code>ASTNode</code>),
	 * or <code>null</code> if none</li>
	 * <li>{@link ChildListPropertyDescriptor} - not allowed</li>
	 * </ul>
	 * 
	 * @param property the property
	 * @param value the property value
	 * @exception RuntimeException if this node does not have the
	 * given property, or if the given property cannot be set
	 */
	public final void setStructuralProperty(StructuralPropertyDescriptor property, Object value) {
		if (property instanceof SimplePropertyDescriptor) {
			SimplePropertyDescriptor p = (SimplePropertyDescriptor) property;
			if (p.getValueType() == int.class) {
				int arg = ((Integer) value).intValue();
				internalGetSetIntProperty(p, false, arg);
				return;
			} else if (p.getValueType() == boolean.class) {
				boolean arg = ((Boolean) value).booleanValue();
				internalGetSetBooleanProperty(p, false, arg);
				return;
			} else {
				if (value == null && p.isMandatory()) {
					throw new IllegalArgumentException();
				}
				internalGetSetObjectProperty(p, false, value);
				return;
			}
		}
		if (property instanceof ChildPropertyDescriptor) {
			ChildPropertyDescriptor p = (ChildPropertyDescriptor) property;
			ASTNode child = (ASTNode) value;
			if (child == null && p.isMandatory()) {
				throw new IllegalArgumentException();
			}
			internalGetSetChildProperty(p, false, child);
			return;
		}
		if (property instanceof ChildListPropertyDescriptor) {
			throw new IllegalArgumentException("Cannot set the list of child list property");  //$NON-NLS-1$
		}
	}
	
	/**
	 * Sets the value of the given int-valued property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one simple property whose value
	 * type is int.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param value the new property value; ignored for get operations
	 * @return the value; always returns
	 * <code>0</code> for set operations
	 * @exception RuntimeException if this node does not have the 
	 * given property, or if the given value cannot be set as specified
	 */
	int internalGetSetIntProperty(SimplePropertyDescriptor property, boolean get, int value) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Sets the value of the given boolean-valued property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one simple property whose value
	 * type is boolean.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param value the new property value; ignored for get operations
	 * @return the value; always returns
	 * <code>false</code> for set operations
	 * @exception RuntimeException if this node does not have the 
	 * given property, or if the given value cannot be set as specified
	 */
	boolean internalGetSetBooleanProperty(SimplePropertyDescriptor property, boolean get, boolean value) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Sets the value of the given property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one simple property whose value
	 * type is a reference type.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param value the new property value, or <code>null</code> if none;
	 * ignored for get operations
	 * @return the value, or <code>null</code> if none; always returns
	 * <code>null</code> for set operations
	 * @exception RuntimeException if this node does not have the 
	 * given property, or if the given value cannot be set as specified
	 */
	Object internalGetSetObjectProperty(SimplePropertyDescriptor property, boolean get, Object value) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}

	/**
	 * Sets the child value of the given property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this node does not have such a property. This method should be
	 * extended in subclasses that have at leasy one child property.
	 * 
	 * @param property the property
	 * @param get <code>true</code> for a get operation, and 
	 * <code>false</code> for a set operation
	 * @param child the new child value, or <code>null</code> if none;
	 * always <code>null</code> for get operations
	 * @return the child, or <code>null</code> if none; always returns
	 * <code>null</code> for set operations
	 * @exception RuntimeException if this node does not have the
	 * given property, or if the given child cannot be set as specified
	 */
	ASTNode internalGetSetChildProperty(ChildPropertyDescriptor property, boolean get, ASTNode child) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Returns the list value of the given property for this node.
	 * The default implementation of this method throws an exception explaining
	 * that this noed does not have such a property. This method should be
	 * extended in subclasses that have at leasy one child list property.
	 * 
	 * @param property the property
	 * @return the list (element type: {@link ASTNode})
	 * @exception RuntimeException if the given node does not have the
	 * given property
	 */
	List internalGetChildListProperty(ChildListPropertyDescriptor property) {
		throw new RuntimeException("Node does not have this property");  //$NON-NLS-1$
	}
	
	/**
	 * Returns a list of structural property descriptors for nodes of the
	 * same type as this node. Clients must not modify the result.
	 * <p>
	 * Note that property descriptors are a meta-level mechanism
	 * for manipulating ASTNodes in a generic way. They are
	 * unrelated to <code>get/setProperty</code>.
	 * </p>
	 * 
	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 */
	public final List structuralPropertiesForType() {
		return internalStructuralPropertiesForType(this.ast.apiLevel);
	}
	
	/**
	 * Returns a list of property descriptors for this node type.
	 * Clients must not modify the result. This abstract method
	 * must be implemented in each concrete AST node type.
	 * <p>
	 * N.B. This method is package-private, so that the implementations
	 * of this method in each of the concrete AST node types do not
	 * clutter up the API doc.
	 * </p>
	 * 
	 * @param apiLevel the API level; one of the <code>AST.JLS*</code> constants
	 * @return a list of property descriptors (element type: 
	 * {@link StructuralPropertyDescriptor})
	 */
	abstract List internalStructuralPropertiesForType(int apiLevel);
	
	/**
	 * Internal helper method that starts the building a list of
	 * property descriptors for the given node type.
	 * 
	 * @param nodeClass the class for a concrete node type
	 * @param propertyList empty list
	 */
	static void createPropertyList(Class nodeClass, List propertyList) {
		// stuff nodeClass at head of list for future ref
		propertyList.add(nodeClass);
	}
	
	/**
	 * Internal helper method that adding a property descriptor.
	 * 
	 * @param property the structural property descriptor
	 * @param propertyList list beginning with the AST node class
	 * followed by accumulated structural property descriptors
	 */
	static void addProperty(StructuralPropertyDescriptor property, List propertyList) {
		Class nodeClass = (Class) propertyList.get(0);
		if (property.getNodeClass() != nodeClass) {
			// easily made cut-and-paste mistake
			throw new RuntimeException("Structural property descriptor has wrong node class!");  //$NON-NLS-1$
		}
		propertyList.add(property);
	}
	
	/**
	 * Internal helper method that completes the building of
	 * a node type's structural property descriptor list.
	 * 
	 * @param propertyList list beginning with the AST node class
	 * followed by accumulated structural property descriptors
	 * @return unmodifiable list of structural property descriptors
	 * (element type: <code>StructuralPropertyDescriptor</code>)
	 */
	static List reapPropertyList(List<StructuralPropertyDescriptor> propertyList) {
		propertyList.remove(0); // remove nodeClass
		// compact
		ArrayList a = new ArrayList(propertyList.size());
		a.addAll(propertyList); 
		return Collections.unmodifiableList(a);
	}
	
	/**
	 * Sets or clears this node's parent node and location.
	 * <p>
	 * Note that this method is package-private. The pointer from a node
	 * to its parent is set implicitly as a side effect of inserting or
	 * removing the node as a child of another node. This method calls
	 * <code>ast.modifying()</code>.
	 * </p>
	 * 
	 * @param parent the new parent of this node, or <code>null</code> if none
	 * @param property the location of this node in its parent, 
	 * or <code>null</code> if <code>parent</code> is <code>null</code>
	 * @see #getLocationInParent
	 * @see #getParent
	 */ 
	final void setParent(ASTNode parent, StructuralPropertyDescriptor property) {
		this.ast.modifying();
		this.parent = parent;
		this.location = property;
	}
	
	/**
	 * Removes this node from its parent. Has no effect if this node
	 * is unparented. If this node appears as an element of a child list
	 * property of its parent, then this node is removed from the
	 * list using <code>List.remove</code>.
	 * If this node appears as the value of a child property of its
	 * parent, then this node is detached from its parent 
	 * by passing <code>null</code> to the appropriate setter method;
	 * this operation fails if this node is in a mandatory property.
	 */ 
	public final void delete() {
		StructuralPropertyDescriptor p = getLocationInParent();
		if (p == null) {
			// node is unparented
			return;
		}
		if (p.isChildProperty()) {
			getParent().setStructuralProperty(this.location, null);
			return;
		}
		if (p.isChildListProperty()) {
			List l = (List) getParent().getStructuralProperty(this.location);
			l.remove(this);
		}
	}
	
	/**
	 * Checks whether the given new child node is a node 
	 * in a different AST from its parent-to-be, whether it is
	 * already has a parent, whether adding it to its
	 * parent-to-be would create a cycle, and whether the child is of
	 * the right type. The parent-to-be is the enclosing instance.
	 * 
	 * @param node the parent-to-be node
	 * @param newChild the new child of the parent
	 * @param cycleCheck <code>true</code> if cycles are possible and need 
	 *   to be checked, <code>false</code> if cycles are impossible and do 
	 *   not need to be checked
	 * @param nodeType a type constraint on child nodes, or <code>null</code>
	 *   if no special check is required
	 * @exception IllegalArgumentException if:
	 * <ul>
	 * <li>the child is null</li>
	 * <li>the node belongs to a different AST</li>
	 * <li>the child has the incorrect node type</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * </ul>
	 */ 
	static void checkNewChild(ASTNode node, ASTNode newChild,
			boolean cycleCheck, Class nodeType) {
		if (newChild.ast != node.ast) {
			// new child is from a different AST
			throw new IllegalArgumentException();
		}	
		/* see AST.internalParserMode */
		if (newChild.getParent() != null) {
			// new child currently has a different parent
			throw new IllegalArgumentException();
		}
		if (cycleCheck && newChild == node.getRoot()) {
			// inserting new child would create a cycle
			throw new IllegalArgumentException();
		}
		Class childClass = newChild.getClass();
		if (nodeType != null && !nodeType.isAssignableFrom(childClass)) {
			// new child is not of the right type
			throw new ClassCastException();
		}
		if ((newChild.typeAndFlags & PROTECT) != 0) {
			// new child node is protected => cannot be parented
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
	}
	
	/**
     * Prelude portion of the "3 step program" for replacing the
	 * old child of this node with another node.
     * Here is the code pattern found in all AST node subclasses:
     * <pre>
     * ASTNode oldChild = this.foo;
     * preReplaceChild(oldChild, newFoo, FOO_PROPERTY);
     * this.foo = newFoo;
     * postReplaceChild(oldChild, newFoo, FOO_PROPERTY);
     * </pre>
     * The first part (preReplaceChild) does all the precondition checks,
     * reports pre-delete events, and changes parent links.
	 * The old child is delinked from its parent (making it a root node),
	 * and the new child node is linked to its parent. The new child node
	 * must be a root node in the same AST as its new parent, and must not
	 * be an ancestor of this node. All three nodes must be
     * modifiable (not PROTECTED). The replace operation must fail
     * atomically; so it is crucial that all precondition checks
     * be done before any linking and delinking happens.
     * The final part (postReplaceChild )reports post-add events.
	 * <p>
	 * This method calls <code>ast.modifying()</code> for the nodes affected.
	 * </p>
	 * 
	 * @param oldChild the old child of this node, or <code>null</code> if
	 *   there was no old child to replace
	 * @param newChild the new child of this node, or <code>null</code> if
	 *   there is no replacement child
	 * @param property the property descriptor of this node describing
     * the relationship between node and child
	 * @exception RuntimeException if:
	 * <ul>
	 * <li>the node belongs to a different AST</li>
	 * <li>the node already has a parent</li>
	 * <li>a cycle in would be created</li>
	 * <li>any of the nodes involved are unmodifiable</li>
	 * </ul>
	 */ 
	final void preReplaceChild(ASTNode oldChild, ASTNode newChild, ChildPropertyDescriptor property) {
		if ((this.typeAndFlags & PROTECT) != 0) {
			// this node is protected => cannot gain or lose children
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
		if (newChild != null) {
			checkNewChild(this, newChild, property.cycleRisk, null);
		}
		// delink old child from parent
		if (oldChild != null) {
			if ((oldChild.typeAndFlags & PROTECT) != 0) {
				// old child node is protected => cannot be unparented
				throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
			}
			if (newChild != null) {
				this.ast.preReplaceChildEvent(this, oldChild, newChild, property);
			} else {
				this.ast.preRemoveChildEvent(this, oldChild, property);
			}
			oldChild.setParent(null, null);
		} else {
			if(newChild != null) {
				this.ast.preAddChildEvent(this, newChild, property);
			}
		}
		// link new child to parent
		if (newChild != null) {
			newChild.setParent(this, property);
			// cannot notify postAddChildEvent until parent is linked to child too
		}
	}

	/**
     * Postlude portion of the "3 step program" for replacing the
	 * old child of this node with another node.
     * See {@link #preReplaceChild(ASTNode, ASTNode, ChildPropertyDescriptor)}
     * for details.
	 */ 
	final void postReplaceChild(ASTNode oldChild, ASTNode newChild, ChildPropertyDescriptor property) {
		// link new child to parent
		if (newChild != null) {
			if (oldChild != null) {
				this.ast.postReplaceChildEvent(this, oldChild, newChild, property);
			} else {
				this.ast.postAddChildEvent(this, newChild, property);
			}
		} else {
			this.ast.postRemoveChildEvent(this, oldChild, property);
		}
	}
	
	/**
     * Prelude portion of the "3 step program" for changing the
	 * value of a simple property of this node.
     * Here is the code pattern found in all AST node subclasses:
     * <pre>
     * preValueChange(FOO_PROPERTY);
     * this.foo = newFoo;
     * postValueChange(FOO_PROPERTY);
     * </pre>
     * The first part (preValueChange) does the precondition check
     * to make sure the node is modifiable (not PROTECTED).
     * The change operation must fail atomically; so it is crucial
     * that the precondition checks are done before the field is
     * hammered. The final part (postValueChange)reports post-change
     * events.
	 * <p>
	 * This method calls <code>ast.modifying()</code> for the node affected.
	 * </p>
	 * 
	 * @param property the property descriptor of this node 
	 * @exception RuntimeException if:
	 * <ul>
	 * <li>this node is unmodifiable</li>
	 * </ul>
	 */ 
	final void preValueChange(SimplePropertyDescriptor property) {
		if ((this.typeAndFlags & PROTECT) != 0) {
			// this node is protected => cannot change valure of properties
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
		this.ast.preValueChangeEvent(this, property);
		this.ast.modifying();
	}

	/**
     * Postlude portion of the "3 step program" for replacing the
	 * old child of this node with another node.
     * See {@link #preValueChange(SimplePropertyDescriptor)} for details.
	 */ 
	final void postValueChange(SimplePropertyDescriptor property) {
		this.ast.postValueChangeEvent(this, property);
	}
	
	/**
     * Ensures that this node is modifiable (that is, not marked PROTECTED).
     * If successful, calls ast.modifying().
     * @exception RuntimeException is not modifiable
     */
	final void checkModifiable() {
		if ((this.typeAndFlags & PROTECT) != 0) {
			throw new IllegalArgumentException("AST node cannot be modified"); //$NON-NLS-1$
		}
		this.ast.modifying();
	}
	
	/**
     * Begin lazy initialization of this node.
     * Here is the code pattern found in all AST
     * node subclasses:
     * <pre>
     * if (this.foo == null) {
	 *    // lazy init must be thread-safe for readers
     *    synchronized (this) {
     *       if (this.foo == null) {
     *          preLazyInit();
     *          this.foo = ...; // code to create new node
     *          postLazyInit(this.foo, FOO_PROPERTY);
     *       }
     *    }
     * }
     * </pre>
     */
	final void preLazyInit() {
		// IMPORTANT: this method is called by readers
		// ASTNode.this is locked at this point
		this.ast.disableEvents();
		// will turn events back on in postLasyInit
	}
	
	/**
     * End lazy initialization of this node.
     * 
	 * @param newChild the new child of this node, or <code>null</code> if
	 *   there is no replacement child
	 * @param property the property descriptor of this node describing
     * the relationship between node and child
     */
	final void postLazyInit(ASTNode newChild, ChildPropertyDescriptor property) {
		// IMPORTANT: this method is called by readers
		// ASTNode.this is locked at this point
		// newChild is brand new (so no chance of concurrent access)
		newChild.setParent(this, property);
		// turn events back on (they were turned off in corresponding preLazyInit)
		this.ast.reenableEvents();
	}

	/**
	 * Returns the named property of this node, or <code>null</code> if none.
	 * 
	 * @param propertyName the property name
	 * @return the property value, or <code>null</code> if none
	 * @see #setProperty(String,Object)
	 */
	public final Object getProperty(String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException();
		}
		if (this.property1 == null) {
			// node has no properties at all
			return null;
		}
		if (this.property1 instanceof String) {
			// node has only a single property
			if (propertyName.equals(this.property1)) {
				return this.property2;
			} else {
				return null;
			}
		}
		// otherwise node has table of properties
		Map m = (Map) this.property1;
		return m.get(propertyName);
	}
	
	/**
	 * Sets the named property of this node to the given value,
	 * or to <code>null</code> to clear it.
	 * <p>
	 * Clients should employ property names that are sufficiently unique
	 * to avoid inadvertent conflicts with other clients that might also be
	 * setting properties on the same node.
	 * </p>
	 * <p>
	 * Note that modifying a property is not considered a modification to the 
	 * AST itself. This is to allow clients to decorate existing nodes with 
	 * their own properties without jeopardizing certain things (like the 
	 * validity of bindings), which rely on the underlying tree remaining static.
	 * </p>
	 * 
	 * @param propertyName the property name
	 * @param data the new property value, or <code>null</code> if none
	 * @see #getProperty(String)
	 */
	public final void setProperty(String propertyName, Object data) {
		if (propertyName == null) {
			throw new IllegalArgumentException();
		}
		// N.B. DO NOT CALL ast.modifying();

		if (this.property1 == null) {
			// node has no properties at all
			if (data == null) {
				// we already know this
				return;
			}
			// node gets its fist property
			this.property1 = propertyName;
			this.property2 = data;
			return;
		}

		if (this.property1 instanceof String) {
			// node has only a single property
			if (propertyName.equals(this.property1)) {
				// we're in luck
				this.property2 = data;
				if (data == null) {
					// just deleted last property
					this.property1 = null;
					this.property2 = null;
				}
				return;
			}
			if (data == null) {
				// we already know this
				return;
			}
			// node already has one property - getting its second
			// convert to more flexible representation
			HashMap m = new HashMap(2);
			m.put(this.property1, this.property2);
			m.put(propertyName, data);
			this.property1 = m;
			this.property2 = null;
			return;
		}
			
		// node has two or more properties
		HashMap m = (HashMap) this.property1;
		if (data == null) {
			m.remove(propertyName);
			// check for just one property left
			if (m.size() == 1) {
				// convert to more efficient representation
				Map.Entry[] entries = (Map.Entry[]) m.entrySet().toArray(new Map.Entry[1]);
				this.property1 = entries[0].getKey();
				this.property2 = entries[0].getValue();
			}
			return;
		} else {
			m.put(propertyName, data);
			// still has two or more properties
			return;
		}
	}
	
	/**
	 * Returns an unmodifiable table of the properties of this node with 
	 * non-<code>null</code> values.
	 * 
	 * @return the table of property values keyed by property name
	 *   (key type: <code>String</code>; value type: <code>Object</code>)
	 */
	public final Map properties() {
		if (this.property1 == null) {
			// node has no properties at all
			return UNMODIFIABLE_EMPTY_MAP;
		} 
		if (this.property1 instanceof String) {
			// node has a single property
			return Collections.singletonMap(this.property1, this.property2);
		}
		
		// node has two or more properties
		if (this.property2 == null) {
			this.property2 = Collections.unmodifiableMap((Map) this.property1);
		}
		// property2 is unmodifiable wrapper for map in property1
		return (Map) this.property2;
	}
	
	/**
	 * Returns the flags associated with this node.
	 * <p>
	 * No flags are associated with newly created nodes.
	 * </p>
	 * <p>
	 * The flags are the bitwise-or of individual flags.
	 * The following flags are currently defined:
	 * <ul>
	 * <li>{@link #MALFORMED} - indicates node is syntactically 
	 *   malformed</li>
	 * <li>{@link #ORIGINAL} - indicates original node
	 * created by ASTParser</li>
	 * <li>{@link #PROTECT} - indicates node is protected
	 * from further modification</li>
	 * <li>{@link #RECOVERED} - indicates node or a part of this node
	 *  is recovered from source that contains a syntax error</li>
	 * </ul>
	 * Other bit positions are reserved for future use.
	 * </p>
	 * 
	 * @return the bitwise-or of individual flags
	 * @see #setFlags(int)
	 */
	public final int getFlags() {
		return this.typeAndFlags & 0xFFFF;
	}
	
	/**
	 * Sets the flags associated with this node to the given value.
	 * <p>
	 * The flags are the bitwise-or of individual flags.
	 * The following flags are currently defined:
	 * <ul>
	 * <li>{@link #MALFORMED} - indicates node is syntactically 
	 *   malformed</li>
	 * <li>{@link #ORIGINAL} - indicates original node
	 * created by ASTParser</li>
	 * <li>{@link #PROTECT} - indicates node is protected
	 * from further modification</li>
	 * <li>{@link #RECOVERED} - indicates node or a part of this node
	 *  is recovered from source that contains a syntax error</li>
	 * </ul>
	 * Other bit positions are reserved for future use.
	 * </p>
	 * <p>
	 * Note that the flags are <em>not</em> considered a structural
	 * property of the node, and can be changed even if the
	 * node is marked as protected.
	 * </p>
	 * 
	 * @param flags the bitwise-or of individual flags
	 * @see #getFlags()
	 */
	public final void setFlags(int flags) {
		this.ast.modifying();
		int old = this.typeAndFlags & 0xFFFF0000;
		this.typeAndFlags = old | (flags & 0xFFFF);
	}
	
	/**
	 * Returns an integer value identifying the type of this concrete AST node.
	 * The values are small positive integers, suitable for use in switch statements.
	 * <p>
	 * For each concrete node type there is a unique node type constant (name
	 * and value). The unique node type constant for a concrete node type such as 
	 * <code>CastExpression</code> is <code>ASTNode.CAST_EXPRESSION</code>.
	 * </p>
	 * 
	 * @return one of the node type constants
	 */
	public final int getNodeType() {
		return this.typeAndFlags >>> 16;
	}
	
	/**
	 * Sets the integer value identifying the type of this concrete AST node.
	 * The values are small positive integers, suitable for use in switch statements.
	 * 
	 * @param nodeType one of the node type constants
	 */
	private void setNodeType(int nodeType) {
		int old = this.typeAndFlags & 0xFFFF0000;
		this.typeAndFlags = old | (nodeType << 16);
	}
	
	/**
	 * Returns an integer value identifying the type of this concrete AST node.
	 * <p>
	 * This internal method is implemented in each of the
	 * concrete node subclasses.
	 * </p>
	 * 
	 * @return one of the node type constants
	 */
	abstract int getNodeType0();
	
	/**
	 * The <code>ASTNode</code> implementation of this <code>Object</code>
	 * method uses object identity (==). Use <code>subtreeMatch</code> to
	 * compare two subtrees for equality.
	 * 
	 * @param obj {@inheritDoc}
	 * @return {@inheritDoc}
	 * @see #subtreeMatch(ASTMatcher matcher, Object other)
	 */
	public final boolean equals(Object obj) {
		return this == obj; // equivalent to Object.equals
	}
	
	/**
	 * Returns whether the subtree rooted at the given node matches the
	 * given other object as decided by the given matcher.
	 * 
	 * @param matcher the matcher
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 * <code>false</code> if they do not match
	 */
	public final boolean subtreeMatch(ASTMatcher matcher, Object other) {
		return subtreeMatch0(matcher, other);
	}
	
	/**
	 * Returns whether the subtree rooted at the given node matches the
	 * given other object as decided by the given matcher.
	 * <p>
	 * This internal method is implemented in each of the
	 * concrete node subclasses.
	 * </p>
	 * 
	 * @param matcher the matcher
	 * @param other the other object, or <code>null</code>
	 * @return <code>true</code> if the subtree matches, or 
	 * <code>false</code> if they do not match
	 */
	abstract boolean subtreeMatch0(ASTMatcher matcher, Object other);
	
	/**
	 * Returns a deep copy of the subtree of AST nodes rooted at the
	 * given node. The resulting nodes are owned by the given AST,
	 * which may be different from the ASTs of the given node. 
	 * Even if the given node has a parent, the result node will be unparented.
	 * <p>
	 * Source range information on the original nodes is automatically copied to the new
	 * nodes. Client properties (<code>properties</code>) are not carried over.
	 * </p>
	 * <p>
	 * The node's <code>AST</code> and the target <code>AST</code> must support
     * the same API level.
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @param node the node to copy, or <code>null</code> if none
	 * @return the copied node, or <code>null</code> if <code>node</code>
	 *    is <code>null</code>
	 */
	public static ASTNode copySubtree(AST target, ASTNode node) {
		if (node == null) {
			return null;
		}
		if (target == null) {
			throw new IllegalArgumentException();
		}
		if (target.apiLevel() != node.getAST().apiLevel()) {
			throw new UnsupportedOperationException();
		}
		ASTNode newNode = node.clone(target);
		return newNode;
	}
	
	/**
	 * Returns a deep copy of the subtrees of AST nodes rooted at the
	 * given list of nodes. The resulting nodes are owned by the given AST,
	 * which may be different from the ASTs of the nodes in the list. 
	 * Even if the nodes in the list have parents, the nodes in the result
	 * will be unparented.
	 * <p>
	 * Source range information on the original nodes is automatically copied to the new
	 * nodes. Client properties (<code>properties</code>) are not carried over.
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @param nodes the list of nodes to copy
	 *    (element type: <code>ASTNode</code>)
	 * @return the list of copied subtrees
	 *    (element type: <code>ASTNode</code>)
	 */
	public static List copySubtrees(AST target, List nodes) {
		List result = new ArrayList(nodes.size());
		for (Iterator it = nodes.iterator(); it.hasNext(); ) {
			ASTNode oldNode = (ASTNode) it.next();
			ASTNode newNode = oldNode.clone(target);
			result.add(newNode);
		}
		return result;
	}
	
	/**
	 * Returns a deep copy of the subtree of AST nodes rooted at this node.
	 * The resulting nodes are owned by the given AST, which may be different
	 * from the AST of this node. Even if this node has a parent, the 
	 * result node will be unparented.
	 * <p>
	 * This method reports pre- and post-clone events, and dispatches
	 * to <code>clone0(AST)</code> which is reimplemented in node subclasses.
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @return the root node of the copies subtree
	 */
	final ASTNode clone(AST target) {
		this.ast.preCloneNodeEvent(this);
		ASTNode c = this.clone0(target);
		this.ast.postCloneNodeEvent(this, c);
		return c;
	}
	
	/**
	 * Returns a deep copy of the subtree of AST nodes rooted at this node.
	 * The resulting nodes are owned by the given AST, which may be different
	 * from the AST of this node. Even if this node has a parent, the 
	 * result node will be unparented.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p>
	 * <p>
	 * This method does not report pre- and post-clone events.
	 * All callers should instead call <code>clone(AST)</code>
	 * to ensure that pre- and post-clone events are reported.
	 * </p>
	 * <p>
	 * N.B. This method is package-private, so that the implementations
	 * of this method in each of the concrete AST node types do not
	 * clutter up the API doc. 
	 * </p>
	 * 
	 * @param target the AST that is to own the nodes in the result
	 * @return the root node of the copies subtree
	 */
	abstract ASTNode clone0(AST target);
	
	/**
	 * Accepts the given visitor on a visit of the current node.
	 * 
	 * @param visitor the visitor object
	 * @exception IllegalArgumentException if the visitor is null
	 */
	public final void accept(ASTVisitor visitor) {
		if (visitor == null) {
			throw new IllegalArgumentException();
		}
		// begin with the generic pre-visit
		visitor.preVisit(this);
		// dynamic dispatch to internal method for type-specific visit/endVisit
		accept0(visitor);
		// end with the generic post-visit
		visitor.postVisit(this);
	}
	
	/**
	 * Accepts the given visitor on a type-specific visit of the current node.
	 * This method must be implemented in all concrete AST node types.
	 * <p>
	 * General template for implementation on each concrete IElement class:
	 * <pre>
	 * <code>
	 * boolean visitChildren = visitor.visit(this);
	 * if (visitChildren) {
	 *    // visit children in normal left to right reading order
	 *    acceptChild(visitor, getProperty1());
	 *    acceptChildren(visitor, rawListProperty);
	 *    acceptChild(visitor, getProperty2());
	 * }
	 * visitor.endVisit(this);
	 * </code>
	 * </pre>
	 * Note that the caller (<code>accept</code>) take cares of invoking
	 * <code>visitor.preVisit(this)</code> and <code>visitor.postVisit(this)</code>.
	 * </p>
	 * 
	 * @param visitor the visitor object
	 */
	abstract void accept0(ASTVisitor visitor);
	
	/**
	 * Accepts the visitor on the child. If child is null,
	 * nothing happens.
	 */
	protected void acceptChild(ASTVisitor visitor, ASTNode child) {
		if (child == null)
			return;
		
		child.accept(visitor);
	}
	
	/**
	 * Accepts the given visitor on a visit of the given live list of
	 * child nodes. 
	 * <p>
	 * This method must be used by the concrete implementations of
	 * <code>accept</code> to traverse list-values properties; it
	 * encapsulates the proper handling of on-the-fly changes to the list.
	 * </p>
	 * 
	 * @param visitor the visitor object
	 * @param children the child AST node to dispatch too, or <code>null</code>
	 *    if none
	 */
	final void acceptChildren(ASTVisitor visitor, ASTNode.NodeList children) {
		// use a cursor to keep track of where we are up to
		// (the list may be changing under foot)
		NodeList.Cursor cursor = children.newCursor();
		try {
			while (cursor.hasNext()) {
				ASTNode child = (ASTNode) cursor.next();
				child.accept(visitor);
			}
		} finally {
			children.releaseCursor(cursor);
		}
	}
	
	/**
	 * Returns the character index into the original source file indicating
	 * where the source fragment corresponding to this node begins.
	 * <p>
	 * The parser supplies useful well-defined source ranges to the nodes it creates.
	 * See {@link ASTParser#setKind(int)} for details
	 * on precisely where source ranges begin and end.
	 * </p>
	 * 
	 * @return the 0-based character index, or <code>-1</code>
	 *    if no source position information is recorded for this node
	 * @see #getLength()
	 * @see ASTParser
	 */
	public int getStartPosition() {
		return startPosition;
	}
	
	/**
	 * Returns the length in characters of the original source file indicating
	 * where the source fragment corresponding to this node ends.
	 * <p>
	 * The parser supplies useful well-defined source ranges to the nodes it creates.
	 * See {@link ASTParser#setKind(int)} methods for details
	 * on precisely where source ranges begin and end.
	 * </p>
	 * 
	 * @return a (possibly 0) length, or <code>0</code>
	 *    if no source position information is recorded for this node
	 * @see #getStartPosition()
	 * @see ASTParser
	 */
	public final int getLength() {
		return this.length;
	}
	
	/**
	 * Sets the source range of the original source file where the source
	 * fragment corresponding to this node was found.
	 * <p>
	 * See {@link ASTParser#setKind(int)} for details
	 * on precisely where source ranges are supposed to begin and end.
	 * </p>
	 * 
	 * @param startPosition a 0-based character index, 
	 *    or <code>-1</code> if no source position information is 
	 *    available for this node
	 * @param length a (possibly 0) length, 
	 *    or <code>0</code> if no source position information is recorded 
	 *    for this node
	 * @see #getStartPosition()
	 * @see #getLength()
	 * @see ASTParser
	 */
	public final void setSourceRange(int startPosition, int length) {
		if (startPosition >= 0 && length < 0) {
			throw new IllegalArgumentException(this.toString());
		}
		if (startPosition < 0 && length != 0) {
			throw new IllegalArgumentException(this.toString());
		}
		// source positions are not considered a structural property
		// but we protect them nevertheless
		checkModifiable();
		this.startPosition = startPosition;
		this.length = length;
	}
	
	/**
	 * Returns a string representation of this node suitable for debugging
	 * purposes only.
	 * 
	 * @return a debug string 
	 */
	public final String toString() {
		StringBuffer buffer = new StringBuffer();
		int p = buffer.length();
		try {
			appendDebugString(buffer);
		} catch (RuntimeException e) {
			// since debugger sometimes call toString methods, problems can easily happen when
			// toString is called on an instance that is being initialized
			buffer.setLength(p);
			buffer.append("!"); //$NON-NLS-1$
			buffer.append(standardToString());
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the string representation of this node produced by the standard
	 * <code>Object.toString</code> method.
	 * 
	 * @return a debug string 
	 */
	final String standardToString() {
		return super.toString();
	}
	
	/**
	 * Appends a debug representation of this node to the given string buffer.
	 * <p>
	 * The <code>ASTNode</code> implementation of this method prints out the entire 
	 * subtree. Subclasses may override to provide a more succinct representation.
	 * </p>
	 * 
	 * @param buffer the string buffer to append to
	 */
	void appendDebugString(StringBuffer buffer) {
		// print the subtree by default
		appendPrintString(buffer);
	}
		
	/**
	 * Appends a standard Java source code representation of this subtree to the given
	 * string buffer.
	 * 
	 * @param buffer the string buffer to append to
	 */
	final void appendPrintString(StringBuffer buffer) {
		NaiveASTFlattener printer = new NaiveASTFlattener();
		this.accept(printer);
		buffer.append(printer.getResult());
	}
	
	/**
	 * Estimate of size of an object header in bytes.
	 */
	static final int HEADERS = 12;
	
	/**
	 * Approximate base size of an AST node instance in bytes, 
	 * including object header and instance fields.
	 * That is, HEADERS + (# instance vars in ASTNode)*4.
	 */
	static final int BASE_NODE_SIZE = HEADERS + 7 * 4;
	
	/**
	 * Returns an estimate of the memory footprint, in bytes,
	 * of the given string.
	 * 
	 * @param string the string to measure, or <code>null</code>
	 * @return the size of this string object in bytes, or
	 *   0 if the string is <code>null</code>
     * @since 3.0
	 */
	static int stringSize(String string) {
		int size = 0;
		if (string != null) {
			// Strings usually have 4 instance fields, one of which is a char[]
			size += HEADERS + 4 * 4;
			// char[] has 2 bytes per character
			size += HEADERS + 2 * string.length();
		}
		return size;
	}
	
	/**
	 * Returns an estimate of the memory footprint in bytes of the entire 
	 * subtree rooted at this node.
	 * 
	 * @return the size of this subtree in bytes
	 */
	public final int subtreeBytes() {
		return treeSize();
	}
	
	/**
	 * Returns an estimate of the memory footprint in bytes of the entire 
	 * subtree rooted at this node.
	 * <p>
	 * N.B. This method is package-private, so that the implementations
	 * of this method in each of the concrete AST node types do not
	 * clutter up the API doc.
	 * </p>
	 * 
	 * @return the size of this subtree in bytes
	 */
	abstract int treeSize();
	
	/**
	 * Returns an estimate of the memory footprint of this node in bytes.
	 * The estimate does not include the space occupied by child nodes.
	 * 
	 * @return the size of this node in bytes
	 */
	abstract int memSize();
	
	public final static ASTNode[] NO_ELEMENTS = new ASTNode[0];
	public final static Declaration[] NO_DECLARATIONS = new Declaration[0];

}
