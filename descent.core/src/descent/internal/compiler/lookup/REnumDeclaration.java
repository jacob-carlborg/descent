package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.IEnumDeclaration;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeEnum;
import descent.internal.compiler.parser.integer_t;
import descent.internal.core.SourceType;
import descent.internal.core.SourceTypeElementInfo;
import descent.internal.core.util.Util;

public class REnumDeclaration extends RScopeDsymbol implements IEnumDeclaration {
	
	private TypeEnum type;
	private integer_t[] values;
	private Type memtype;

	public REnumDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public IEnumDeclaration isEnumDeclaration() {
		return this;
	}
	
	@Override
	public Type getType() {
		if (type == null) {
			type = new TypeEnum(this);
		}
		return type;
	}

	public integer_t defaultval() {
		return getValue(0);
	}
	
	public integer_t minval() {
		return getValue(1);
	}

	public integer_t maxval() {
		return getValue(2);
	}

	public Type memtype() {
		if (memtype == null) {
			IType t = (IType) element;
			try {
				String signature = t.getSuperclassTypeSignature();
				memtype = getType(signature);
			} catch (JavaModelException e) {
				Util.log(e);
				memtype = Type.tint32;
			}	
		}
		return memtype;
	}	
	
	private integer_t getValue(int n) {
		calculateValues();
		if (values != null && values[n] != null) {
			return values[n];
		} else {
			return integer_t.ZERO;
		}
	}
	
	private void calculateValues() {
		if (values == null) {
			// TODO expose this values via the IType interface
			SourceType t = (SourceType) element;
			SourceTypeElementInfo info;
			try {
				info = (SourceTypeElementInfo) t.getElementInfo();
				values = info.getEnumValues();
			} catch (JavaModelException e) {
				Util.log(e);
			}
		}
	}

}
