package dtool.dom.expressions;

import melnorme.miscutil.Assert;
import descent.internal.compiler.parser.DotTemplateInstanceExp;
import dtool.dom.ast.IASTNeoVisitor;

// TODO not working in descent
public class ExpDotTemplateInstance extends Expression {

	public ExpDotTemplateInstance(DotTemplateInstanceExp element) {
		Assert.failTODO();
		throw new UnsupportedOperationException();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		throw new UnsupportedOperationException();
	}

}
