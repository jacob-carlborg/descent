package descent.internal.compiler.parser;

import java.util.List;

import melnorme.miscutil.tree.IElement;
import melnorme.miscutil.tree.IVisitable;
import descent.internal.compiler.parser.ast.IASTVisitor;

public interface INode extends IElement, IVisitable<IASTVisitor> {
	
	int getStart();
	
	void setStart(int start);
	
	int getLength();
	
	void setLength(int length);
	
	int getErrorStart();
	
	int getErrorLength();
	
	int getLineNumber();
	
	void setLineNumber(int lineNumber);
	
	DYNCAST dyncast();
	
	String toChars(SemanticContext context);
	
	List<Modifier> modifiers();
	
	void modifiers(List<Modifier> modifiers);	
	
	List<Modifier> extraModifiers();
	
	void extraModifiers(List<Modifier> extraModifiers);
	
	int getNodeType();

}