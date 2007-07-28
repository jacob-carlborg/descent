package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class TemplateMixin extends TemplateInstance {

	public Type tqual;
	public List<ASTNode> tiargs;
	public int typeStart;
	public int typeLength;

	public TemplateMixin(IdentifierExp ident, Type tqual, List<IdentifierExp> idents, List<ASTNode> tiargs) {
		super(idents.get(idents.size() - 1));
		this.ident = ident;
		this.tqual = tqual;
		this.idents = idents;
		this.tiargs = tiargs != null ? tiargs : new ArrayList<ASTNode>(0);
	}
	
	public void setTypeSourceRange(int start, int length) {
		this.typeStart = start;
		this.typeLength = length;
	}
	
	@Override
	public TemplateMixin isTemplateMixin() {
		return this;
	}
	
	@Override
	public int getNodeType() {
		return TEMPLATE_MIXIN;
	}

}
