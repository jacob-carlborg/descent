package dtool.dom.declarations;

import descent.internal.core.dom.Identifier;
import dtool.dom.ast.IASTNeoVisitor;
import dtool.model.IScope;

public abstract class TemplateParameter extends DefUnit {

	public TemplateParameter(Identifier id) {
		super(id);
	}

	@Override
	public EArcheType getArcheType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	public static TemplateParameter[] convert(descent.internal.core.dom.TemplateParameter[] elems) {
		// TODO Auto-generated method stub
		//return (TemplateParameter[]) DescentASTConverter.convertMany(elems);
		return null;
	}

}
