package descent.tests.mars;

import descent.core.dom.IElement;
import descent.core.dom.ElementVisitor;

public class CounterVisitor extends ElementVisitor  {
	
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
