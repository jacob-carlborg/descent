package descent.internal.compiler.parser;

import descent.internal.compiler.parser.ast.IASTVisitor;

public class StructLiteralExp extends Expression {	

	public StructDeclaration sd;		// which aggregate this is for
	public Expressions elements;	// parallels sd->fields[] with
				// NULL entries for fields to skip

    // Symbol *sym;		// back end symbol to initialize with literal
	public int soffset;		// offset from start of s
	public int fillHoles;		// fill alignment 'holes' with zero
	
	public StructLiteralExp(Loc loc, StructDeclaration sd, Expressions elements) {
		super(loc, TOK.TOKstructliteral);
		this.sd = sd;
	    this.elements = elements;
	    // this.sym = null;
	    this.soffset = 0;
	    this.fillHoles = 1;
	}

	@Override
	public int getNodeType() {
		return 0;
	}

	@Override
	protected void accept0(IASTVisitor visitor) {
	}

}
