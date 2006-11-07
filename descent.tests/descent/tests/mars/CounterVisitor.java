package descent.tests.mars;

import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;

public class CounterVisitor implements IDElementVisitor {
	
	public int enter;
	public int exit;
	
	public boolean visit(IDElement element) {
		//System.out.println(element);
		enter++;
		return true;
	}

	public void endVisit(IDElement element) {
		exit++;
	}

}
