package descent.tests.mars;

import junit.framework.TestCase;
import descent.core.dom.IElement;

public abstract class Parser_Test extends TestCase {
	
	protected void assertPosition(IElement elem, int start, int length) {
		assertEquals(start, elem.getStartPosition());
		assertEquals(length, elem.getLength());
	}
	
	protected void assertVisitor(IElement elem, int expectedChildren) {
		CounterVisitor visitor = new CounterVisitor();
		elem.accept(visitor);
		assertEquals(expectedChildren, visitor.enter);
		assertEquals(expectedChildren, visitor.exit);
	}

}
