package descent.internal.compiler.lookup;

import descent.core.IField;
import descent.internal.compiler.parser.ITypedefDeclaration;

public class RTypedefDeclaration extends RDeclaration implements ITypedefDeclaration {

	public RTypedefDeclaration(IField element) {
		super(element);
	}

}
