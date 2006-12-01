package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IQualifiedName;

public abstract class TypeQualified extends Type {
	
	public List<Identifier> idents;
	public QualifiedName qName;
	
	public TypeQualified(TY ty) {
		super(ty, null);
		this.idents = new ArrayList<Identifier>();
	}

	public void addIdent(Identifier identifier) {
		this.idents.add(identifier);
	}
	
	public IQualifiedName getName() {
		if (qName == null) {
			this.qName = new QualifiedName(idents);
		}
		return qName;
	}

}
