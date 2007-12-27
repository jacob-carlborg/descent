package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.IType;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IDeleteDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IInvariantDeclaration;
import descent.internal.compiler.parser.INewDeclaration;
import descent.internal.compiler.parser.INode;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.Symbol;
import descent.internal.compiler.parser.Type;

public class RAggregateDeclaration extends RScopeDsymbol implements IAggregateDeclaration {
	
	private Symbol sinit;
	private List<IVarDeclaration> fields;

	public RAggregateDeclaration(IType element, SemanticContext context) {
		super(element, context);
	}

	public void accessCheck(Scope sc, IDsymbol smember, SemanticContext context, INode reference) {
		SemanticMixin.accessCheck(this, sc, smember, context, reference);
	}

	public void addField(Scope sc, IVarDeclaration v, SemanticContext context) {
		throw new IllegalStateException("Should not be called");
	}

	public void alignmember(int salign, int size, int[] poffset) {
		SemanticMixin.alignmember(this, salign, size, poffset);
	}

	public int alignsize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void alignsize(int alignsize) {
		// TODO Auto-generated method stub
		
	}

	public List<IVarDeclaration> fields() {
		if (fields == null) {
			fields = new ArrayList<IVarDeclaration>();
			for(IDsymbol s : members()) {
				IVarDeclaration v = s.isVarDeclaration();
				if (v != null) {
					fields.add(v);
				}
			}
		}
		return fields;
	}
	
	public PROT getAccess(IDsymbol smember) {
		return PROT.PROTpublic;
	}

	public Type handle() {
		return null;
	}

	public boolean hasPrivateAccess(IDsymbol smember) {
		return SemanticMixin.hasPrivateAccess(this, smember);
	}

	public int hasUnions() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void hasUnions(int hasUnions) {
		// TODO Auto-generated method stub
		
	}

	public IInvariantDeclaration inv() {
		// TODO Auto-generated method stub
		return null;
	}

	public void inv(IInvariantDeclaration inv) {
		// TODO Auto-generated method stub
		
	}

	public boolean isFriendOf(IAggregateDeclaration cd) {
		return SemanticMixin.isFriendOf(this, cd);
	}

	public void sizeok(int sizeok) {
		// TODO Auto-generated method stub
		
	}

	public int structsize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void structsize(int structsize) {
		// TODO Auto-generated method stub
		
	}

	public Symbol toInitializer() {
		// TODO semantic back-end
		if (null == sinit) {
			sinit = new Symbol();
		}
		return sinit;
	}

	public Type type() {
		return null;
	}

	@Override
	public IAggregateDeclaration isAggregateDeclaration() {
		return this;
	}
	
	public int storage_class() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int sizeok() {
		return 1;
	}
	
	public int size(SemanticContext context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Scope scope() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public INewDeclaration aggNew() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IDeleteDeclaration aggDelete() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int structalign() {
		// TODO Auto-generated method stub
		return 0;
	}

}
