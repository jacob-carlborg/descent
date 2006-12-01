package descent.ui.text;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDeclaration;
import descent.core.dom.IElement;
import descent.core.dom.ASTVisitor;

public class DDomUtil {
	
	/**
	 * <p>Finds the "outline" element present at the line and column specified,
	 * or null if no element was found.</p>
	 */
	public static IElement getOutlineElementAt(ICompilationUnit unit, int offset) {
		assert(unit != null);
		
		FindOutlineElementVisitor visitor = new FindOutlineElementVisitor(offset);
		unit.accept(visitor);
		
		return visitor.theElement;
	}
	
	// TODO: fix to make it a real visitor
	private static class FindOutlineElementVisitor extends ASTVisitor {
		
		public IElement theElement = null;
		
		private int offset;
		
		public FindOutlineElementVisitor(int offset) {
			this.offset = offset;
		}
		
		@Override
		public void postVisit(IElement node) {
			if (theElement != null) return;
			
			if (isOfInterest(node) && isInBounds(node)) {
					theElement = node;
			}
		}
		
		private boolean isOfInterest(IElement element) {
			return element instanceof IDeclaration;
		}
		
		private boolean isInBounds(IElement element) {
			int off = element.getStartPosition();
			return off <= offset && off + element.getLength() >= offset;
		}
		
	}

}
