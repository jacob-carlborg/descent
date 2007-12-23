package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IEnumMember;

public class REnumMember extends RDsymbol implements IEnumMember {

	public REnumMember(IField element) {
		super(element);
	}

	public Expression value() {
		// TODO Auto-generated method stub
		return null;
	}

	public void value(Expression value) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public IEnumMember isEnumMember() {
		return this;
	}

}
