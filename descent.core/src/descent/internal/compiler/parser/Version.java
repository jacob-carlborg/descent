package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class Version extends Dsymbol {
	
	public String value;
	
	public Version(Loc loc, String value) {
		super(loc);
		this.value = value;
	}
	
	@Override
	public int getNodeType() {
		return VERSION;
	}
	
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}

}
