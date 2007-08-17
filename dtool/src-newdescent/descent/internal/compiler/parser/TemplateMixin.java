package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.core.domX.IASTVisitor;

public class TemplateMixin extends TemplateInstance {

	public Type tqual;
	public List<ASTDmdNode> tiargs;
	public int typeStart;
	public int typeLength;

	public TemplateMixin(IdentifierExp ident, Type tqual, List<IdentifierExp> idents, List<ASTDmdNode> tiargs) {
		super(idents.get(idents.size() - 1));
		this.ident = ident;
		this.tqual = tqual;
		this.idents = idents;
		this.tiargs = tiargs != null ? tiargs : new ArrayList<ASTDmdNode>(0);
	}
	
	public void setTypeSourceRange(int start, int length) {
		this.typeStart = start;
		this.typeLength = length;
	}

	
	@Override
	public int getNodeType() {
		return TEMPLATE_MIXIN;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, idents);
			TreeVisitor.acceptChildren(visitor, tiargs);
			TreeVisitor.acceptChildren(visitor, ident);
		}
		visitor.endVisit(this);
	}

	
	@Override
	public TemplateMixin isTemplateMixin() {
		return this;
	}
}
