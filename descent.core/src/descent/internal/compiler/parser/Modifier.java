package descent.internal.compiler.parser;

import descent.core.Flags;
import descent.internal.compiler.parser.ast.IASTVisitor;


public class Modifier extends ASTDmdNode {
	
	public int lineNumber;
	public TOK tok;

	public Modifier(Token token, int lineNumber) {
		this.tok = token.value;
		this.start = token.ptr;
		this.length = token.sourceLen;
		this.lineNumber = lineNumber;
	}
	
	@Override
	public String toString() {
		return tok.toString();
	}
	
	@Override
	public int getNodeType() {
		return MODIFIER;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public char[] toCharArray() {
		return tok.charArrayValue;
	}
	
	@Override
	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Returns the flags of this modifier.
	 * @see Flags
	 */
	public long getFlags() {
		switch(tok) {
		case TOKprivate: return Flags.AccPrivate;
		case TOKpackage: return Flags.AccPackage;
		case TOKprotected: return Flags.AccProtected;
		case TOKpublic: return Flags.AccPublic;
		case TOKexport: return Flags.AccExport;
		case TOKstatic: return Flags.AccStatic;
		case TOKfinal: return Flags.AccFinal;
		case TOKabstract: return Flags.AccAbstract;
		case TOKoverride: return Flags.AccOverride;
		case TOKauto: return Flags.AccAuto;
		case TOKsynchronized: return Flags.AccSynchronized;
		case TOKdeprecated: return Flags.AccDeprecated;
		case TOKextern: return Flags.AccExtern;
		case TOKconst: return Flags.AccConst;
		case TOKscope: return Flags.AccScope;
		case TOKinvariant: return  Flags.AccInvariant;
		case TOKin: return 0;
		case TOKout: return 0;
		case TOKinout: return 0;
		case TOKlazy: return 0;
		case TOKref: return 0;
		default: return 0;
		}
	}

}