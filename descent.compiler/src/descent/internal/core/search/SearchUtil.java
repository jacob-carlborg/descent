package descent.internal.core.search;

import descent.core.IType;
import descent.core.ITypeParameter;
import descent.core.JavaModelException;
import descent.core.compiler.CharOperation;

public class SearchUtil {
	
	public static char[] getTemplateParametersSignature(IType type) throws JavaModelException {
		char[] templateParametersSignature;
		ITypeParameter[] tps = type.getTypeParameters();
		if (tps.length == 0) {
			templateParametersSignature = CharOperation.NO_CHAR;
		} else {
			StringBuilder sb = new StringBuilder();
			for(ITypeParameter tp : type.getTypeParameters()) {
				sb.append(tp.getSignature());
			}
			templateParametersSignature = new char[sb.length()];
			sb.getChars(0, sb.length(), templateParametersSignature, 0);
		}
		return templateParametersSignature;
	}

}
