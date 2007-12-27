package descent.internal.compiler.parser;

import descent.core.compiler.IProblem;

import static descent.internal.compiler.parser.STC.STCin;

// DMD 1.020
public abstract class Declaration extends Dsymbol implements IDeclaration {

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
		return SemanticMixin.isCtorinit(this);
	}

	public boolean isFinal() {
		return SemanticMixin.isFinal(this);
	}

	public boolean isAbstract() {
		return SemanticMixin.isAbstract(this);
	}

	public boolean isConst() {
		return SemanticMixin.isConst(this);
	}

	public boolean isAuto() {
		return SemanticMixin.isAuto(this);
	}

	public boolean isScope() {
		return SemanticMixin.isScope(this);
	}

	public boolean isStatic() {
		return SemanticMixin.isStatic(this);
	}

	public boolean isSynchronized() {
		return (storage_class & STC.STCsynchronized) != 0;
	}

	public boolean isParameter() {
		return SemanticMixin.isParameter(this);
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
		return SemanticMixin.isOut(this);
	}

	public boolean isRef() {
		return SemanticMixin.isRef(this);
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
						IProblem.ForwardDeclaration, this));
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
	
	public Type type() {
		return type;
	}
	
	public void type(Type type) {
		this.type = type;
	}
	
	public int storage_class() {
		return storage_class;
	}
	
	public void storage_class(int storage_class) {
		this.storage_class = storage_class;
	}
	
	public LINK linkage() {
		return linkage;
	}
	
	public void linkage(LINK linkage) {
		this.linkage = linkage;
	}
	
	public PROT protection() {
		return protection;
	}
	
	public void protection(PROT protection) {
		this.protection = protection;
	}

}
