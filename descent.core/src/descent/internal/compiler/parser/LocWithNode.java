package descent.internal.compiler.parser;

public class LocWithNode extends Loc {
	
	public final ASTDmdNode node;

	public LocWithNode(Loc loc, ASTDmdNode node) {
		this.linnum = loc.linnum;
		this.filename = loc.filename;
		this.node = node;
	}

}
