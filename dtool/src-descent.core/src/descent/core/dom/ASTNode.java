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
	 
	 
	 	public int getStartPosition() {
		return startPosition;
	}
	 
	 
	 public final int getFlags() {
		return this.typeAndFlags & 0xFFFF;
	}
	 
	 public final void setFlags(int flags) {
		//this.ast.modifying();
		int old = this.typeAndFlags & 0xFFFF0000;
		this.typeAndFlags = old | (flags & 0xFFFF);
	}


	public int getLength() {
		return length;
	}
	
}
