package descent.ui.text;

import descent.core.dom.ICompilationUnit;
import descent.core.dom.IDElement;
import descent.core.dom.IDElementVisitor;

public class DDomUtil {
	
	/**
	 * <p>Finds the "outline" element present at the line and column specified,
	 * or null if no element was found.</p>
	 */
	public static IDElement getOutlineElementAt(ICompilationUnit unit, int offset) {
		assert(unit != null);
		
		FindOutlineElementVisitor visitor = new FindOutlineElementVisitor(offset);
		unit.accept(visitor);
		
		return visitor.theElement;
	}
	
	private static class FindOutlineElementVisitor implements IDElementVisitor {
		
		public IDElement theElement = null;
		
		private int offset;
		
		public FindOutlineElementVisitor(int offset) {
			this.offset = offset;
		}

		public boolean visit(IDElement element) {
			return theElement == null && isInBounds(element);
		}
		
		public void endVisit(IDElement element) {
			if (theElement != null) return;
			
			if (isOfInterest(element) && isInBounds(element)) {
					theElement = element;
			}
		}
		
		private boolean isOfInterest(IDElement element) {
			switch(element.getElementType()) {
			case IDElement.MODULE_DECLARATION:
			case IDElement.IMPORT_DECLARATION:
			case IDElement.AGGREGATE_DECLARATION:
			case IDElement.FUNCTION_DECLARATION:
			case IDElement.ENUM_DECLARATION:
			case IDElement.ENUM_MEMBER:
			case IDElement.INVARIANT_DECLARATION:
			case IDElement.UNITTEST_DECLARATION:
			case IDElement.VARIABLE_DECLARATION:
			case IDElement.TYPEDEF_DECLARATION:
			case IDElement.ALIAS_DECLARATION:
			case IDElement.TEMPLATE_DECLARATION:
			case IDElement.CONDITIONAL_DECLARATION:
			case IDElement.LINK_DECLARATION:
			case IDElement.CONDITION_ASSIGNMENT:
			case IDElement.PRAGMA_DECLARATION:
			case IDElement.MIXIN_DECLARATION:
				return true;
			}
			return false;
		}
		
		private boolean isInBounds(IDElement element) {
			int off = element.getOffset();
			return off <= offset && off + element.getLength() >= offset;
		}
		
	}

}
