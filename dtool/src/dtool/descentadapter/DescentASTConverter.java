package dtool.descentadapter;

import dtool.dom.base.ASTNode;

public class DescentASTConverter {

	private DefConverter converter;

	public DescentASTConverter() {
		this.converter = new DefConverter();
	}
	
	public ASTNode convert(ASTNode elem) {
		elem.accept(converter);
		return converter.ret;
	}
}
