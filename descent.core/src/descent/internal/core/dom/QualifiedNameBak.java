package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.ASTVisitor;
import descent.core.dom.IQualifiedName;

public class QualifiedNameBak extends ASTNode implements IQualifiedName  {
	
	private String name;
	
	public QualifiedNameBak(Identifier id) {
		this.name = id.string;
		this.startPosition = id.startPosition;
		this.length = id.length;
	}
	
	public QualifiedNameBak(List<Identifier> ids) {
		this.startPosition = ids.get(0).startPosition;
		Identifier last = ids.get(ids.size() - 1);
		this.length = last.startPosition + last.length - this.startPosition; 
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < ids.size(); i++) {
			sb.append(ids.get(i).string);
			if (i != ids.size() - 1) {
				sb.append('.');
			}
		}
		name = sb.toString();
	}
	
	public QualifiedNameBak(Identifier id, List<Identifier> ids) {
		this.startPosition = id.startPosition;
		StringBuilder sb = new StringBuilder();
		sb.append(id);

		for(Identifier idx : ids) {
			sb.append('.');
			sb.append(idx.string);
		}
		Identifier last = ids.get(ids.size() - 1);
		this.length = last.startPosition + last.length - this.startPosition;

		name = sb.toString();
	}
	
	public QualifiedNameBak(List<Identifier> packages, Identifier id) {
		StringBuilder sb = new StringBuilder();
		if (packages != null) {
			this.startPosition = packages.get(0).startPosition;
			for(Identifier idx : packages) {
				sb.append(idx.string);
				sb.append('.');
			}
		} else {
			this.startPosition = id.startPosition;
		}
		sb.append(id.string);
		name = sb.toString();
		
		this.length = id.startPosition + id.length - this.startPosition;
	}
	
	public int getElementType() {
		return QUALIFIED_NAME;
	}

	public void accept0(ASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	public String getIdentifier() {
		return name;
	}
	
	// TODO Descent remove
	@Override
	public String toString() {
		return name;
	}

}
