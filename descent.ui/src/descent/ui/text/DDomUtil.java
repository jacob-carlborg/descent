package descent.ui.text;

import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;
import descent.core.dom.ModuleDeclaration;

public class DDomUtil {
	
	/**
	 * <p>Finds the "outline" element present at the line and column specified,
	 * or null if no element was found.</p>
	 */
	public static ASTNode getOutlineElementAt(CompilationUnit unit, int offset) {
		assert(unit != null);
		
		FindOutlineElementVisitor visitor = new FindOutlineElementVisitor(offset);
		unit.accept(visitor);
		
		return visitor.theElement;
	}
	
	// TODO: fix to make it a real visitor
	private static class FindOutlineElementVisitor extends ASTVisitor {
		
		public ASTNode theElement = null;
		
		private int offset;
		
		public FindOutlineElementVisitor(int offset) {
			this.offset = offset;
		}
		
		@Override
		public void postVisit(ASTNode node) {
			if (theElement != null) return;
			
			if (!isInBounds(node)) return;
			
			if (isOfInterest(node)) {
				theElement = node;
			}
		}
		
		private boolean isOfInterest(ASTNode element) {
			// TODO fix
			return element instanceof Declaration || element instanceof ModuleDeclaration;
		}
		
		private boolean isInBounds(ASTNode element) {
			int off = element.getStartPosition();
			return off <= offset && off + element.getLength() >= offset;
		}
		
	}

}
