package descent.internal.compiler.parser;

public abstract class Declaration extends Dsymbol {
	
	public Type type;
	public int storage_class;
	public LINK linkage;
	public PROT protection;
	
	public Declaration() {
		this(null);
	}
	
	public Declaration(IdentifierExp ident) {
		this.ident = ident;
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
	
	public int size() {
		return type.size();
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

	public boolean isDeprecated() {
		return (storage_class & STC.STCdeprecated) != 0;
	}

	public boolean isOverride()     { return (storage_class & STC.STCoverride) != 0; }

}
