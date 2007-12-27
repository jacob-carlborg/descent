package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.IEnumMember;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.core.SourceField;
import descent.internal.core.SourceFieldElementInfo;
import descent.internal.core.util.Util;

public class REnumMember extends RDsymbol implements IEnumMember {
	
	private Expression value;
	private boolean valueComputed;

	public REnumMember(IField element, SemanticContext context) {
		super(element, context);
	}

	public Expression value() {
		if (!valueComputed) {
			// TODO: expose this value via the IField interface?
			SourceField f = (SourceField) element;
			try {
				SourceFieldElementInfo info = (SourceFieldElementInfo) f.getElementInfo();
				char[] encodedValue = info.getInitializationSource();
				if (encodedValue != null) {
					value = ASTNodeEncoder.decodeExpression(encodedValue);
					// The expression's type must be my enum's type 
					value.type = parent.getType();
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
			valueComputed = true;
		}
		return value;
	}

	public void value(Expression value) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IEnumMember isEnumMember() {
		return this;
	}

}
