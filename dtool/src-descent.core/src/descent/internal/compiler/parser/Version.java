package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class Version extends Dsymbol {
	
	public char[] value;
	
	public Version(Loc loc, char[] value) {
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
