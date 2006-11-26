package descent.ui.text;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IElement;
import descent.core.dom.IDElementVisitor;

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
	
	private static class FindOutlineElementVisitor implements IDElementVisitor {
		
		public IElement theElement = null;
		
		private int offset;
		
		public FindOutlineElementVisitor(int offset) {
			this.offset = offset;
		}

		public boolean visit(IElement element) {
			return theElement == null && isInBounds(element);
		}
		
		public void endVisit(IElement element) {
			if (theElement != null) return;
			
			if (isOfInterest(element) && isInBounds(element)) {
					theElement = element;
			}
		}
		
		private boolean isOfInterest(IElement element) {
			switch(element.getElementType()) {
			case IElement.MODULE_DECLARATION:
			case IElement.IMPORT_DECLARATION:
			case IElement.AGGREGATE_DECLARATION:
			case IElement.FUNCTION_DECLARATION:
			case IElement.ENUM_DECLARATION:
			case IElement.ENUM_MEMBER:
			case IElement.INVARIANT_DECLARATION:
			case IElement.UNITTEST_DECLARATION:
			case IElement.VARIABLE_DECLARATION:
			case IElement.TYPEDEF_DECLARATION:
			case IElement.ALIAS_DECLARATION:
			case IElement.TEMPLATE_DECLARATION:
			case IElement.VERSION_DECLARATION:
			case IElement.DEBUG_DECLARATION:
			case IElement.STATIC_IF_DECLARATION:
			case IElement.LINK_DECLARATION:
			case IElement.CONDITION_ASSIGNMENT:
			case IElement.PRAGMA_DECLARATION:
			case IElement.MIXIN_DECLARATION:
				return true;
			}
			return false;
		}
		
		private boolean isInBounds(IElement element) {
			int off = element.getStartPosition();
			return off <= offset && off + element.getLength() >= offset;
		}
		
	}

}
