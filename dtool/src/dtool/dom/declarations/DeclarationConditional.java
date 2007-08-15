package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.ASTNode;
import descent.internal.compiler.parser.Condition;
import descent.internal.compiler.parser.ConditionalDeclaration;
import descent.internal.compiler.parser.ConditionalStatement;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.IStatement;
import dtool.dom.statements.MultiNodes;
import dtool.refmodel.INonScopedBlock;

public class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public Condition condition; // TODO convert Condition
	public ASTNode thendecls;
	public ASTNode elsedecls;

	public DeclarationConditional(ConditionalDeclaration elem) {
		convertNode(elem);
		this.condition = elem.condition;
		thendecls = MultiNodes.createNodeBlock(elem, elem.decl); 
		elsedecls = MultiNodes.createNodeBlock(elem, elem.elsedecl);
	}
	

	public DeclarationConditional(ConditionalStatement elem) {
		convertNode(elem);
		this.condition = elem.condition;
		thendecls = MultiNodes.createNodeBlock(elem.ifbody); 
		elsedecls = MultiNodes.createNodeBlock(elem.elsebody);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			//TreeVisitor.acceptChildren(visitor, condition);
			TreeVisitor.acceptChildren(visitor, thendecls);
			TreeVisitor.acceptChildren(visitor, elsedecls);
		}
		visitor.endVisit(this);
	}

	public ASTNode[] getMembers() {
		if(thendecls == null)
			return getElseMembers();
		if(elsedecls == null)
			return getThenMembers();
		
		return ArrayUtil.concat(getThenMembers(), getElseMembers());
	}

	private ASTNode[] getThenMembers() {
		if(thendecls instanceof MultiNodes) 
			return ((MultiNodes)thendecls).decls;
		return new ASTNode[] { thendecls };
	}

	private ASTNode[] getElseMembers() {
		if(elsedecls instanceof MultiNodes) 
			return ((MultiNodes)elsedecls).decls;
		return new ASTNode[] { elsedecls };
	}

	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}

}
