package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.IQualifiedName;

public abstract class TypeQualified extends DmdType {
	
	public List<Identifier> idents;
	public QualifiedNameBak qName;
	
	public TypeQualified(TY ty) {
		super(ty, null);
		this.idents = new ArrayList<Identifier>();
	}

	public void addIdent(Identifier identifier) {
		this.idents.add(identifier);
	}
	
	public IQualifiedName getName() {
		if (qName == null) {
			this.qName = new QualifiedNameBak(idents);
		}
		return qName;
	}

}
