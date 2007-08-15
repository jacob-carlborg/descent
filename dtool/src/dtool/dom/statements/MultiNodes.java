package dtool.dom.statements;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.CompoundStatement;
import descent.internal.compiler.parser.Dsymbol;
import descent.internal.compiler.parser.Statement;
import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.refmodel.INonScopedBlock;

/**
 * A block of declarations or statements which does not introduces a new Scope.
 */
public class MultiNodes extends ASTNeoNode implements INonScopedBlock {
	
	public ASTNode[] decls;

	private MultiNodes() {
	}

	public static ASTNeoNode createNodeBlock(Statement body) {
		MultiNodes nodes = new MultiNodes();
		if(body instanceof CompoundStatement) {
			CompoundStatement cst = (CompoundStatement) body;
				nodes.decls = DescentASTConverter.convertMany(cst.sourceStatements);
		} else {
			nodes.decls = new ASTNode[] { 
				DescentASTConverter.convertElem(body)
			};
		}
		return nodes;
	}

	public static ASTNeoNode createNodeBlock(Collection<Dsymbol> decl) {
		MultiNodes nodes = new MultiNodes();
		nodes.decls = DescentASTConverter.convertMany(decl);
		return nodes;
	}
	
	public static ASTNeoNode createNodeBlock(ASTDmdNode elem, Collection<Dsymbol> olddecls) {
		if(olddecls == null || olddecls.size() == 0)
			return null;
		ASTNeoNode multiNodes = createNodeBlock(olddecls);
		multiNodes.setSourceRange(elem);
		return multiNodes;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, decls);
		}
		visitor.endVisit(this);
	}

	public Iterator<? extends ASTNode> getMembersIterator() {
		return Arrays.asList(decls).iterator();
	}



}
