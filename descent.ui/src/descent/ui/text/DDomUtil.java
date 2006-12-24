package descent.ui.text;

import descent.core.dom.ASTNode;
import descent.core.dom.ASTVisitor;
import descent.core.dom.AliasDeclarationFragment;
import descent.core.dom.CompilationUnit;
import descent.core.dom.Declaration;

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
			
			if (isOfInterest(node) && isInBounds(node)) {
					theElement = node;
			}
		}
		
		private boolean isOfInterest(ASTNode element) {
			// TODO fix
			return element instanceof Declaration || element instanceof AliasDeclarationFragment;
		}
		
		private boolean isInBounds(ASTNode element) {
			int off = element.getStartPosition();
			return off <= offset && off + element.getLength() >= offset;
		}
		
	}

}
