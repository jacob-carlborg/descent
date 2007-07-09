package descent.internal.core.dom;

import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;


import descent.core.dom.IDeclaration;
import descent.core.dom.IExpression;
import descent.core.dom.IName;
import descent.core.dom.IPragmaDeclaration;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;

public class PragmaDeclaration extends Dsymbol implements IPragmaDeclaration {
	
	public Expression[] expressions;
	public IDeclaration[] declDefs;

	public PragmaDeclaration(Identifier ident, List<Expression> args, List<IDeclaration> a) {
		this.ident = ident;
		if (args != null) {
			expressions = args.toArray(new Expression[args.size()]);
		}
		if (a != null) {
			a.toArray(new IDeclaration[a.size()]);
		}
	}

	public IExpression[] getArguments() {
		return expressions == null ? new IExpression[0] : expressions;
	}

	public IDeclaration[] getDeclarationDefinitions() {
		return declDefs == null ? AbstractElement.NO_DECLARATIONS: declDefs;
	}

	public IName getIdentifier() {
		return ident;
	}
	
	public int getElementType() {
		return ElementTypes.PRAGMA_DECLARATION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			TreeVisitor.acceptChildren(visitor, (AbstractElement[])declDefs);
		}
		visitor.endVisit(this);
	}

}
