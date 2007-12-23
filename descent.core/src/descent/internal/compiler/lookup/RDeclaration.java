package descent.internal.compiler.lookup;

import descent.core.IJavaElement;
import descent.internal.compiler.parser.IDeclaration;
import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Type;

public class RDeclaration extends RDsymbol implements IDeclaration {

	public RDeclaration(IJavaElement element) {
		super(element);
	}
	
	public boolean isAuto() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isConst() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCtorinit() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDataseg(SemanticContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFinal() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOut() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isParameter() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRef() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isScope() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}

	public LINK linkage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void linkage(LINK linkage) {
		// TODO Auto-generated method stub
		
	}

	public PROT protection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void protection(PROT protection) {
		// TODO Auto-generated method stub
		
	}

	public int storage_class() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void storage_class(int storage_class) {
		// TODO Auto-generated method stub
		
	}

	public Type type() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IDeclaration isDeclaration() {
		return this;
	}

}
