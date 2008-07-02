package descent.internal.codeassist;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import descent.core.compiler.CharOperation;
import descent.core.dom.ASTConverter;
import descent.core.dom.ASTNode;
import descent.core.dom.Declaration;
import descent.core.dom.Expression;
import descent.core.dom.FunctionDeclaration;
import descent.core.dom.Type;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.AliasDeclaration;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.FuncDeclaration;
import descent.internal.compiler.parser.Id;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.ProtDeclaration;
import descent.internal.compiler.parser.Statement;
import descent.internal.compiler.parser.StorageClassDeclaration;
import descent.internal.compiler.parser.TypeClass;
import descent.internal.compiler.parser.TypeIdentifier;
import descent.internal.compiler.parser.TypeInstance;
import descent.internal.compiler.parser.TypeStruct;
import descent.internal.compiler.parser.TypeTypedef;

/**
 * Specialized ASTConverter that doesn't set source range and sometimes
 * replaces source information for resolved information, needed in
 * template instantiations.
 * 
 * It also removes conditional declarations and statements, keeping only
 * the branches that evaluate to true.
 */
public class EvaluationASTConverter extends ASTConverter {

	public EvaluationASTConverter(boolean resolveBindings, IProgressMonitor monitor) {
		super(resolveBindings, monitor);
	}
	
	@Override
	public descent.core.dom.ASTNode convert(ASTDmdNode symbol) {
		if (symbol == null) {
			return null;
		}
		
		switch(symbol.getNodeType()) {
		case ASTDmdNode.TYPE_CLASS:
			return ast.newSimpleType(ast.newSimpleName(new String(((TypeClass) symbol).sym.ident.ident)));
		case ASTDmdNode.TYPE_STRUCT:
			return ast.newSimpleType(ast.newSimpleName(new String(((TypeStruct) symbol).sym.ident.ident)));
		case ASTDmdNode.TYPE_TYPEDEF:
			return ast.newSimpleType(ast.newSimpleName(new String(((TypeTypedef) symbol).sym.ident.ident)));
		default:
			return super.convert(symbol);
		}
	}
	
	@Override
	public FunctionDeclaration convert(FuncDeclaration a) {
		if (a.getJavaElement() != null) {
			a = a.materialize();
		}
		return super.convert(a);
	}
	
	@Override
	protected int convertOneOfManyDeclarations(List<Declaration> destination, List<Dsymbol> source, int i, Dsymbol symbol) {
		switch(symbol.getNodeType()) {
		case ASTDmdNode.CONDITIONAL_DECLARATION:
			ConditionalDeclaration decl = (ConditionalDeclaration) symbol;
			switch(decl.condition.inc) {
			case 0:
				destination.add((Declaration) convert(symbol));
				break;
			case 1:
				convertDeclarations(destination, decl.decl);
				break;
			case 2:
				convertDeclarations(destination, decl.elsedecl);
				break;
			}
			return i;
		default:
			return super.convertOneOfManyDeclarations(destination, source, i, symbol);
		}
	}
	
	@Override
	protected Statement convertOneOfManyStatements(List<descent.core.dom.Statement> destination, Statement stm) {
		switch(stm.getNodeType()) {
		case ASTDmdNode.CONDITIONAL_STATEMENT:
			ConditionalStatement condStm = (ConditionalStatement) stm;
			switch(condStm.condition.inc) {
			case 1:
				stm = condStm.ifbody;
				stm = extractSingleCompoundStatement(stm);
				break;
			case 2:
				stm = condStm.elsebody;
				stm = extractSingleCompoundStatement(stm);
				break;
			}
		}
		descent.core.dom.Statement convertStm = convert(stm);
		if (convertStm != null) {
			destination.add(convertStm);
		}
		return stm;
	}
	
	@Override
	public Type convert(TypeIdentifier a) {
		if (a.ident != null && a.ident.resolvedSymbol instanceof AliasDeclaration) {
			AliasDeclaration alias = (AliasDeclaration) a.ident.resolvedSymbol;
			if (alias.sourceType != null) {
				return convert(alias.sourceType);
			}
		}
		return super.convert(a);
	}
	
	@Override
	public Expression convert(IdentifierExp a) {
		if (a == null || a.ident == null || CharOperation.equals(a.ident, Id.empty)) return null;
		
		char[] id;
		if (a.resolvedSymbol instanceof AliasDeclaration) {
			AliasDeclaration alias = (AliasDeclaration) a.resolvedSymbol;
			if (alias.aliassym != null && alias.aliassym.ident != null && alias.aliassym.ident.ident != null) {
				id = alias.aliassym.ident.ident;
			} else {
				id = a.ident;
			}
		} else if (a.resolvedExpression != null) {
			return convert(a.resolvedExpression);
		} else {
			id = a.ident;
		}
		
		descent.core.dom.SimpleName b = newSimpleName();
		internalSetIdentifier(b, id);
		setSourceRange(b, a.start, a.length);
		
		if (resolveBindings) {
			recordNodes(b, a);
		}
		
		return convertParenthesizedExpression(a, b);
	}
	
	@Override
	public void convert(ProtDeclaration a, List<Declaration> toAdd) {
		if (a.decl != null) {
			convertDeclarations(toAdd, a.decl);
		}
	}
	
	@Override
	public void convert(StorageClassDeclaration a, List<Declaration> toAdd) {
		if (a.decl != null) {
			convertDeclarations(toAdd, a.decl);
		}
	}
	
	@Override
	public Type convert(TypeInstance a) {
		if (a.tempinst.sourceTiargs == null && a.tempinst.tiargs != null) {
			a.tempinst.sourceTiargs = a.tempinst.tiargs;
		}
		return super.convert(a);
	}
	
	@Override
	protected void setSourceRange(ASTNode node, int start, int length) {
		// Do nothing
	}
	
	private Statement extractSingleCompoundStatement(Statement stm) {
		if (stm instanceof CompoundStatement) {
			CompoundStatement cs = (CompoundStatement) stm;
			if (cs.statements != null && cs.statements.size() == 1) {
				stm = cs.statements.get(0);
			}
		}
		return stm;
	}

}
