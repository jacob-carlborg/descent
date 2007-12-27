package descent.internal.compiler.lookup;

import java.util.ArrayList;
import java.util.List;

import descent.core.IType;
import descent.core.JavaModelException;
import descent.internal.compiler.parser.IAggregateDeclaration;
import descent.internal.compiler.parser.IDeleteDeclaration;
import descent.internal.compiler.parser.IDsymbol;
import descent.internal.compiler.parser.IInvariantDeclaration;
import descent.internal.compiler.parser.INewDeclaration;
import descent.internal.compiler.parser.INode;
import descent.internal.compiler.parser.IVarDeclaration;
import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.Scope;
import descent.internal.compiler.parser.SemanticContext;
import descent.internal.compiler.parser.SemanticMixin;
import descent.internal.compiler.parser.Symbol;
import descent.internal.compiler.parser.Type;
import descent.internal.core.SourceType;
import descent.internal.core.SourceTypeElementInfo;
import descent.internal.core.util.Util;

public class RAggregateDeclaration extends RScopeDsymbol implements IAggregateDeclaration {
	
	private Symbol sinit;
	private List<IVarDeclaration> fields;
	private int storage_class = -1;
	private int alignof = -1;
	private int sizeof = -1;

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
		if (alignof < 0) {
			// TODO expose property via interface
			SourceType type = (SourceType) element;
			try {
				SourceTypeElementInfo info = (SourceTypeElementInfo) type.getElementInfo();
				alignof = info.getAlignof();
			} catch (JavaModelException e) {
				Util.log(e);
				alignof = 0;
			}
		}
		return alignof;
	}

	public void alignsize(int alignsize) {
		throw new IllegalStateException("Should not be called");
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
		throw new IllegalStateException("Should not be called");
	}

	public int structsize() {
		if (sizeof < 0) {
			// TODO expose property via interface
			SourceType type = (SourceType) element;
			try {
				SourceTypeElementInfo info = (SourceTypeElementInfo) type.getElementInfo();
				sizeof = info.getSizeof();
			} catch (JavaModelException e) {
				Util.log(e);
				sizeof = 0;
			}
		}
		return sizeof;
	}

	public void structsize(int structsize) {
		throw new IllegalStateException("Should not be called");
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
		if (storage_class < 0) {
			storage_class = getStorageClass();
		}
		return storage_class;
	}
	
	public int sizeok() {
		return 1; // contains valid data
	}
	
	public int size(SemanticContext context) {
		return structsize();
	}
	
	public Scope scope() {
		return null; // semantic already done
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
		return alignsize();
	}

}
