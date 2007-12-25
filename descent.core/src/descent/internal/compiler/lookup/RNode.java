package descent.internal.compiler.lookup;

import java.util.List;

import melnorme.miscutil.tree.IElement;
import descent.core.IJavaElement;
import descent.internal.compiler.parser.DYNCAST;
import descent.internal.compiler.parser.INode;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class RNode implements INode {
	
	protected IJavaElement element;
	protected SemanticContext context;
	
	public RNode(IJavaElement element, SemanticContext context) {
		this.element = element;
		this.context = context;
	}

	public DYNCAST dyncast() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Modifier> extraModifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	public void extraModifiers(List<Modifier> extraModifiers) {
		// TODO Auto-generated method stub
		
	}

	public int getErrorLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getErrorStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNodeType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Modifier> modifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifiers(List<Modifier> modifiers) {
		// TODO Auto-generated method stub
		
	}

	public String toChars(SemanticContext context) {
		throw new IllegalStateException(
			"This is an abstract method in DMD an should be implemented");
	}

	public IElement[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getElementType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IElement getParentBruno() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	public void accept(IASTVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
	public void setStart(int start) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLength(int length) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLineNumber(int lineNumber) {
		// TODO Auto-generated method stub
		
	}

}
