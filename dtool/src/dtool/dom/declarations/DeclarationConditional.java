package dtool.dom.declarations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.ArrayUtil;
import melnorme.miscutil.tree.TreeVisitor;

import descent.core.dom.IConditionalDeclaration;
import descent.core.dom.IDebugDeclaration;
import descent.core.dom.IDeclaration;
import descent.core.dom.IDescentElement;
import descent.core.dom.IIftypeDeclaration;
import descent.core.dom.IVersionDeclaration;
import descent.internal.core.dom.CompoundStatement;
import descent.internal.core.dom.Condition;
import descent.internal.core.dom.ConditionalDeclaration;
import descent.internal.core.dom.ConditionalStatement;
import descent.internal.core.dom.StaticIfDeclaration;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.ASTNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.statements.IStatement;
import dtool.dom.statements.MultiNodes;
import dtool.dom.statements.MultiStatement;
import dtool.dom.statements.Statement;
import dtool.refmodel.INonScopedBlock;

public class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public Condition condition; // TODO convert Condition
	public ASTNode thendecls;
	public ASTNode elsedecls;


	public DeclarationConditional(IDebugDeclaration elem) {
		convertConditional(elem, Condition.DEBUG);
	}

	public DeclarationConditional(IVersionDeclaration elem) {
		convertConditional(elem, Condition.VERSION);
	}

	public DeclarationConditional(StaticIfDeclaration elem) {
		convertConditional(elem, Condition.STATIC_IF);
	}
	
	public DeclarationConditional(IIftypeDeclaration elem) {
		// TODO: here be a definition?
		convertConditional(elem, Condition.IFTYPE);
	}
	
	
	private void convertConditional(IConditionalDeclaration elem, int debug) {
		convertNode((ASTNode)elem);
		thendecls = createMultiNodes(elem, elem.getIfTrueDeclarationDefinitions()); 
		elsedecls = createMultiNodes(elem, elem.getIfFalseDeclarationDefinitions());
	}

	private MultiNodes createMultiNodes(IDescentElement elem, IDeclaration[] olddecls) {
		if(olddecls == null || olddecls.length == 0)
			return null;
		return new MultiNodes(elem, olddecls);
	}

	public DeclarationConditional(ConditionalDeclaration elem) {
		convertNode(elem);
		this.condition = elem.condition;
		thendecls = createMultiNodes(elem, elem.a); 
		elsedecls = createMultiNodes(elem, elem.aelse);
	}
	
	private MultiNodes createMultiNodes(IDescentElement elem, List<IDeclaration> olddecls) {
		if(olddecls == null || olddecls.size() == 0)
			return null;
		return new MultiNodes(elem, olddecls);
	}
	
	public DeclarationConditional(ConditionalStatement elem) {
		setSourceRange(elem);
		this.condition = elem.condition;
		
		convertThenElseBodies(elem.ifbody, elem.elsebody);
	}

	private void convertThenElseBodies(descent.internal.core.dom.Statement thenbody, 
			descent.internal.core.dom.Statement elsebody) {
		if(thenbody instanceof CompoundStatement) {
			thendecls = new MultiStatement((CompoundStatement)thenbody); 
		} else {
			thendecls = (ASTNode) Statement.convert(thenbody);
		}
		if(elsebody instanceof CompoundStatement) {
			elsedecls = new MultiStatement((CompoundStatement)elsebody); 
		} else {
			elsedecls = (ASTNode) Statement.convert(elsebody);
		}
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
		if(thendecls instanceof MultiStatement) 
			return ((MultiStatement)thendecls).statements.toArray(new ASTNode[0]);
		return new ASTNode[] { thendecls };
	}

	private ASTNode[] getElseMembers() {
		if(elsedecls instanceof MultiNodes) 
			return ((MultiNodes)elsedecls).decls;
		if(elsedecls instanceof MultiStatement) 
			return ((MultiStatement)elsedecls).statements.toArray(new ASTNode[0]);
		return new ASTNode[] { elsedecls };
	}

	public Iterator<ASTNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}

}
