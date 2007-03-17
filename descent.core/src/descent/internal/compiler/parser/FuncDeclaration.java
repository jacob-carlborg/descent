package descent.internal.compiler.parser;

import java.util.List;

public class FuncDeclaration extends Declaration {
	
	public Statement fensure;
	public Statement frequire;
	public Statement fbody;
	public IdentifierExp outId;
	public List<Argument> parameters;
	public List<TemplateParameter> templateParameters;
	
	public VarDeclaration vthis;		// 'this' parameter (member and nested)
	
	public FuncDeclaration() {
	}
	
	public FuncDeclaration(IdentifierExp ident, int storage_class, Type type) {
		super(ident);
		this.storage_class = storage_class;
		this.type = type;
	}
	
	@Override
	public FuncDeclaration isFuncDeclaration() {
		return this;
	}
	
	public boolean isNested() {
		return ((storage_class & STC.STCstatic) == 0) &&
		   (toParent2().isFuncDeclaration() != null);
	}
	
	@Override
	public AggregateDeclaration isThis() {
		AggregateDeclaration ad;

		ad = null;
		if ((storage_class & STC.STCstatic) == 0) {
			ad = isMember2();
		}
		return ad;
	}
	
	public AggregateDeclaration isMember2() {
		AggregateDeclaration ad;

		ad = null;
		for (Dsymbol s = this; s != null; s = s.parent) {
			ad = s.isMember();
			if (ad != null) {
				break;
			}
			if (s.parent == null || (s.parent.isTemplateInstance() == null)) {
				break;
			}
		}
		return ad;
	}
	
	@Override
	public int kind() {
		return FUNC_DECLARATION;
	}

}
