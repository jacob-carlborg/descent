package descent.internal.corext.dom;

import java.util.List;

import descent.core.dom.AST;
import descent.core.dom.ASTNode;
import descent.core.dom.ASTParser;
import descent.core.dom.AggregateDeclaration;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Expression;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.GenericVisitor;
import descent.core.dom.Modifier;
import descent.core.dom.PrimitiveType;
import descent.core.dom.Type;

public class ASTNodeFactory {
	
	private static final String STATEMENT_HEADER= "class __X__ { void __x__() { "; //$NON-NLS-1$
	private static final String STATEMENT_FOOTER= "}}"; //$NON-NLS-1$
	
	private static final String TYPE_HEADER= "class __X__ { abstract "; //$NON-NLS-1$
	private static final String TYPE_FOOTER= " __f__(); }}"; //$NON-NLS-1$
	
	private static final String TYPEPARAM_HEADER= "class __X__ { abstract <"; //$NON-NLS-1$
	private static final String TYPEPARAM_FOOTER= "> void __f__(); }}"; //$NON-NLS-1$
	
	private static class PositionClearer extends GenericVisitor {
		
		protected boolean visitNode(ASTNode node) {
			node.setSourceRange(-1, 0);
			return true;
		}
	}
	
	/**
	 * Returns a list of newly created Modifier nodes corresponding to the given modfier flags. 
	 * @param ast The ast to create the nodes for.
	 * @param modifiers The modifier flags describing the modifier nodes to create.
	 * @return Returns a list of nodes of type {@link Modifier}.
	 */
	public static List newModifiers(AST ast, long modifiers) {
		return ast.newModifiers(modifiers);
	}
	
	public static Type newType(AST ast, String content) {
		StringBuffer buffer= new StringBuffer(TYPE_HEADER);
		buffer.append(content);
		buffer.append(TYPE_FOOTER);
		ASTParser p= ASTParser.newParser(ast.apiLevel());
		p.setSource(buffer.toString().toCharArray());
		CompilationUnit root= (CompilationUnit) p.createAST(null);
		List list= root.declarations();
		AggregateDeclaration typeDecl= (AggregateDeclaration) list.get(0);
		FunctionDeclaration methodDecl= (FunctionDeclaration) typeDecl.declarations().get(0);
		ASTNode type= methodDecl.getReturnType();
		ASTNode result= ASTNode.copySubtree(ast, type);
		result.accept(new PositionClearer());
		return (Type)result;
	}
	
	/**
	 * Returns an expression that is assignable to the given type. <code>null</code> is
	 * returned if the type is the 'void' type.
	 * 
	 * @param ast The AST to create the expression for
	 * @param type The type of the returned expression
	 * @param extraDimensions Extra dimensions to the type
	 * @return Returns the Null-literal for reference types, a boolen-literal for a boolean type, a number
	 * literal for primitive types or <code>null</code> if the type is void.
	 */
	public static Expression newDefaultExpression(AST ast, Type type) {
		if (type instanceof PrimitiveType) {
			PrimitiveType primitiveType= (PrimitiveType) type;
			if (primitiveType.getPrimitiveTypeCode() == PrimitiveType.Code.BOOL) {
				return ast.newBooleanLiteral(false);
			} else if (primitiveType.getPrimitiveTypeCode() == PrimitiveType.Code.VOID) {
				return null;				
			} else {
				return ast.newNumberLiteral("0"); //$NON-NLS-1$
			}
		}
		return ast.newNullLiteral();
	}
	
}