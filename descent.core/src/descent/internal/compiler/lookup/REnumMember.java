package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.ASTNodeEncoder;
import descent.internal.compiler.parser.Dsymbols;
import descent.internal.compiler.parser.Expression;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IEnumMember;
import descent.internal.compiler.parser.IScopeDsymbol;
import descent.internal.compiler.parser.ISignatureConstants;
import descent.internal.compiler.parser.IntegerExp;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.TypeBasic;
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
					value = context.encoder.decodeExpression(encodedValue);
					if (value != null) {
						value.semantic(getScope(), context);
						
						// The expression's type must be my enum's type 
						if (parent.isEnumDeclaration() != null) {
							value.type = parent.getType();
						} else {
							value.type = TypeBasic.tint32;
						}
					}
				}
			} catch (JavaModelException e) {
				Util.log(e);
			}
			
			if (value == null) {
				IDsymbol parent = parent();
				if (parent != null && parent instanceof IScopeDsymbol) {
					Dsymbols children = ((IScopeDsymbol) parent).members();
					for(int i = 0; i < children.size(); i++) {
						if (children.get(i) == this) {
							value = new IntegerExp(i);
							value.semantic(getScope(), context);
							
							if (parent.isEnumDeclaration() != null) {
								value.type = parent.getType();
							} else {
								value.type = TypeBasic.tint32;
							}
							break;
						}
					}
				}
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
	
	public char getSignaturePrefix() {
		return ISignatureConstants.ENUM_MEMBER;
	}
	
	@Override
	public String kind() {
		return "enum member";
	}

}
