package descent.internal.compiler.parser;

import static descent.internal.compiler.parser.STC.STCin;
import static descent.internal.compiler.parser.STC.STCout;
import static descent.internal.compiler.parser.STC.STCref;

import descent.core.compiler.IProblem;

// DMD 1.020
public abstract class Declaration extends Dsymbol {

	public Type type;
	public Type sourceType;
	public int storage_class;
	public LINK linkage;
	public PROT protection;

	public Declaration(IdentifierExp ident) {
		super(ident);
		this.type = null;
		this.storage_class = STC.STCundefined;
		this.protection = PROT.PROTundefined;
		this.linkage = LINK.LINKdefault;
	}

	@Override
	public void semantic(Scope sc, SemanticContext context) {

	}

	@Override
	public String kind() {
		return "declaration";
	}

	@Override
	public int size(SemanticContext context) {
		return type.size(loc, context);
	}

	public boolean isStaticConstructor() {
		return false;
	}

	public boolean isStaticDestructor() {
		return false;
	}

	@Override
	public Declaration isDeclaration() {
		return this;
	}

	public boolean isDelete() {
		return false;
	}

	public boolean isDataseg(SemanticContext context) {
		return false;
	}

	public boolean isCodepseg() {
		return false;
	}

	@Override
	public PROT prot() {
		return protection;
	}

	public boolean isCtorinit() {
		return (storage_class & STC.STCctorinit) != 0;
	}

	public boolean isFinal() {
		return (storage_class & STC.STCfinal) != 0;
	}

	public boolean isAbstract() {
		return (storage_class & STC.STCabstract) != 0;
	}

	public boolean isConst() {
		return (storage_class & STC.STCconst) != 0;
	}

	public boolean isAuto() {
		return (storage_class & STC.STCauto) != 0;
	}

	public boolean isScope() {
		return (storage_class & (STC.STCscope | STC.STCauto)) != 0;
	}

	public boolean isStatic() {
		return (storage_class & STC.STCstatic) != 0;
	}

	public boolean isSynchronized() {
		return (storage_class & STC.STCsynchronized) != 0;
	}

	public boolean isParameter() {
		return (storage_class & STC.STCparameter) != 0;
	}

	@Override
	public boolean isDeprecated() {
		return (storage_class & STC.STCdeprecated) != 0;
	}

	public boolean isOverride() {
		return (storage_class & STC.STCoverride) != 0;
	}
	
	public boolean isIn() {
		return (storage_class & STCin) != 0;
	}

	public boolean isOut() {
		return (storage_class & STCout) != 0;
	}

	public boolean isRef() {
		return (storage_class & STCref) != 0;
	}

	@Override
	public String mangle(SemanticContext context) {
		if (null == parent || parent.isModule() != null) { 
			// if at global scope
			// If it's not a D declaration, no mangling
			switch (linkage) {
			case LINKd:
				break;

			case LINKc:
			case LINKwindows:
			case LINKpascal:
			case LINKcpp:
				return ident.toChars();

			case LINKdefault:
				context.acceptProblem(Problem.newSemanticTypeError(
						IProblem.ForwardDeclaration, 0, start,
						length));
				return ident.toChars();

			default:
				throw new IllegalStateException("assert(0);");
			}
		}
		String p = mangle(this);
		OutBuffer buf = new OutBuffer();
		buf.writestring("_D");
		buf.writestring(p);
		p = buf.toChars();
		buf.data = null;
		return p;
	}

}
