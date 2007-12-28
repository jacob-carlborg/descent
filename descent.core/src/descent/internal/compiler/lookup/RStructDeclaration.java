package descent.internal.compiler.lookup;

import descent.core.IType;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IStructDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.Type;
import descent.internal.compiler.parser.TypeStruct;

public class RStructDeclaration extends RAggregateDeclaration implements IStructDeclaration {
	
	private TypeStruct type;
	private boolean zeroInitCalculated;
	private boolean zeroInit;

	public RStructDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}
	
	@Override
	public PROT getAccess(IDsymbol smember) {
		return SemanticMixin.getAccess(this, smember);
	}
	
	@Override
	public Type handle() {
		return type().pointerTo(context);
	}
	
	@Override
	public Type getType() {
		return type();
	}
	
	@Override
	public Type type() {
		if (type == null) {
			type = new TypeStruct(this);
			if (type != null) {
				type.deco = "S" + getTypeDeco();
			}
		}
		return type;
	}
	
	@Override
	public IStructDeclaration isStructDeclaration() {
		return this;
	}
	
	public boolean zeroInit() {
		if (!zeroInitCalculated) {
			zeroInit = SemanticMixin.isZeroInit(this, context);
			zeroInitCalculated = true;
		}
		return zeroInit;
	}

}