package descent.internal.core.resolved;

import java.util.List;

import melnorme.miscutil.tree.IElement;
import descent.internal.compiler.parser.DYNCAST;
import descent.internal.compiler.parser.INode;
import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.ast.IASTVisitor;

public class RNode implements INode {

	public DYNCAST dyncast() {
		return null;
	}

	public List<Modifier> extraModifiers() {
		return null;
	}

	public void extraModifiers(List<Modifier> extraModifiers) {
	}

	public int getErrorLength() {
		return 0;
	}

	public int getErrorStart() {
		return 0;
	}

	public int getLength() {
		return 0;
	}

	public int getLineNumber() {
		return 0;
	}

	public int getNodeType() {
		return 0;
	}

	public int getStart() {
		return 0;
	}

	public List<Modifier> modifiers() {
		return null;
	}

	public void modifiers(List<Modifier> modifiers) {
	}

	public String toChars(SemanticContext context) {
		return null;
	}

	public IElement[] getChildren() {
		return null;
	}

	public int getElementType() {
		return 0;
	}

	public IElement getParentBruno() {
		return null;
	}

	public boolean hasChildren() {
		return false;
	}

	public void accept(IASTVisitor visitor) {
	}

}
