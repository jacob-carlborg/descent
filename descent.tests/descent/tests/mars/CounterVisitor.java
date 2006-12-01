package descent.tests.mars;

import descent.core.dom.IElement;
import descent.core.dom.ASTVisitor;

public class CounterVisitor extends ASTVisitor  {
	
	public int enter;
	public int exit;
	
	@Override
	public void preVisit(IElement node) {
		enter++;
	}
	
	@Override
	public void postVisit(IElement node) {
		exit++;
	}

}
