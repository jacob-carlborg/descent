package descent.tests.mars;

import descent.core.dom.IElement;
import descent.core.dom.IDElementVisitor;

public class CounterVisitor implements IDElementVisitor {
	
	public int enter;
	public int exit;
	
	public boolean visit(IElement element) {
		//System.out.println(element);
		enter++;
		return true;
	}

	public void endVisit(IElement element) {
		exit++;
	}

}
