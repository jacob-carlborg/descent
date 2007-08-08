package dtool.dom.expressions;

import java.util.List;

import dtool.descentadapter.DescentASTConverter;
import dtool.dom.ast.ASTNeoNode;

public abstract class Initializer extends ASTNeoNode{

	public static Initializer convert(descent.internal.core.dom.Initializer initializer) {
		return (Initializer) DescentASTConverter.convertElem(initializer);
	}

	
	public static Initializer[] convertMany(List<descent.internal.core.dom.Initializer> elements) {
		Initializer[] rets = new Initializer[elements.size()];
		DescentASTConverter.convertMany(elements, rets);
		return rets;
	}
}
