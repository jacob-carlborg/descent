package descent.internal.core.dom;

import java.util.List;

import util.tree.TreeVisitor;

import descent.core.dom.IDescentElement;
import descent.core.dom.IMixinDeclaration;
import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;
import dtool.dom.ast.ASTNode;

public class TemplateMixin extends Declaration implements IMixinDeclaration {

	public QualifiedName qName;
	public TypeTypeof tqual;
	public AbstractElement[] tiargs;

	public TemplateMixin(Identifier id, TypeTypeof tqual, List<Identifier> idents, List<ASTNode> tiargs) {
		super(id);
		this.tqual = tqual;
		this.qName = new QualifiedName(idents);
		if (tiargs != null) {
			this.tiargs = tiargs.toArray(new AbstractElement[tiargs.size()]);
		}
	}
	
	public int getElementType() {
		return ElementTypes.MIXIN_DECLARATION;
	}
	
	public IName getName() {
		return ident;
	}
	
	public QualifiedName getType() {
		return qName;
	}
	
	public TypeTypeof getTypeofType() {
		return tqual;
	}
	
	public IDescentElement[] getTemplateArguments() {
		if (tiargs == null) return AbstractElement.NO_ELEMENTS; 
		return tiargs;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChild(visitor, tqual);
			TreeVisitor.acceptChild(visitor, ident);
			TreeVisitor.acceptChildren(visitor, tiargs);
		}
		visitor.endVisit(this);
	}

}
