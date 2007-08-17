package dtool.dom.declarations;

import java.util.Iterator;
import java.util.List;

import melnorme.miscutil.IteratorUtil;
import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ASTDmdNode;
import descent.internal.compiler.parser.IdentifierExp;
import descent.internal.compiler.parser.IftypeExp;
import descent.internal.compiler.parser.TOK;
import dtool.dom.ast.ASTNeoNode;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.dom.ast.IASTNode;
import dtool.dom.definitions.DefUnit;
import dtool.dom.references.Reference;
import dtool.refmodel.IScope;
import dtool.refmodel.IScopeNode;

/**
 * Declaration of a static if, with an is type condition, which creates
 * a new DefUnit.
 * In this DeclarationConditional the thendecls are not a direct children
 * of this node. The direct children is an IsTypeScope, which in turn is the 
 * parent of the the thendecls. This is so that the thendecls can see the
 * DefUnit of the node. However, the {@link #getMembersIterator()} will 
 * still return thendecls + elsedecls as normal, bypassing the IsTypeScope.
 */
public class DeclarationStaticIfIsType extends DeclarationConditional {

	public class IsTypeDefUnit extends DefUnit {

		public IsTypeDefUnit(IdentifierExp ident) {
			super(ident);
			setSourceRange(ident);
		}

		@Override
		public EArcheType getArcheType() {
			return EArcheType.Alias;
		}

		@Override
		public IScopeNode getMembersScope() {
			if(specType != null)
				return specType.getTargetScope();
			else
				return arg.getTargetScope();
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			if (visitor.visit(this)) {	
			}
			visitor.endVisit(this);
		}
	}
	
	public class IsTypeScope extends ASTNeoNode implements IScopeNode {

		public NodeList nodelist;
		
		public IsTypeScope(NodeList nodes) {
			this.nodelist = nodes;
			// The range?
		}

		@Override
		public void accept0(IASTNeoVisitor visitor) {
			if (visitor.visit(this)) {
				TreeVisitor.acceptChildren(visitor, nodelist.nodes);
			}
			visitor.endVisit(this);
		}

		public Iterator<? extends IASTNode> getMembersIterator() {
			return IteratorUtil.singletonIterator(defUnit);
		}

		public List<IScope> getSuperScopes() {
			return null;
		}
	}
	
	public IsTypeScope thendeclsScope;

	public Reference arg;
	public IsTypeDefUnit defUnit;
	public TOK tok;
	public Reference specType;
	
	public DeclarationStaticIfIsType(ASTDmdNode  elem,
			IftypeExp iftypeExp, NodeList thendecls, NodeList elsedecls) {
		convertNode(elem);
		this.arg = Reference.convertType(iftypeExp.targ);
		this.defUnit = new IsTypeDefUnit(iftypeExp.ident);
		this.tok = iftypeExp.tok;
		this.specType = Reference.convertType(iftypeExp.tspec);
		this.thendecls = thendecls;
		this.elsedecls = elsedecls;
		this.thendeclsScope = new IsTypeScope(thendecls);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, defUnit);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, thendeclsScope);
			TreeVisitor.acceptChildren(visitor, elsedecls.nodes);
		}
		visitor.endVisit(this);
	}

}
