package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IDElementVisitor;
import descent.core.dom.IQualifiedName;

public class QualifiedName extends AbstractElement implements IQualifiedName  {
	
	private String name;
	
	public QualifiedName(Identifier id) {
		this.name = id.string;
		this.start = id.start;
		this.length = id.length;
	}
	
	public QualifiedName(List<Identifier> ids) {
		this.start = ids.get(0).start;
		Identifier last = ids.get(ids.size() - 1);
		this.length = last.start + last.length - this.start; 
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < ids.size(); i++) {
			sb.append(ids.get(i).string);
			if (i != ids.size() - 1) {
				sb.append('.');
			}
		}
		name = sb.toString();
	}
	
	public QualifiedName(Identifier id, List<Identifier> ids) {
		this.start = id.start;
		StringBuilder sb = new StringBuilder();
		sb.append(id);

		for(Identifier idx : ids) {
			sb.append('.');
			sb.append(idx.string);
		}
		Identifier last = ids.get(ids.size() - 1);
		this.length = last.start + last.length - this.start;

		name = sb.toString();
	}
	
	public QualifiedName(List<Identifier> packages, Identifier id) {
		StringBuilder sb = new StringBuilder();
		if (packages != null) {
			this.start = packages.get(0).start;
			for(Identifier idx : packages) {
				sb.append(idx.string);
				sb.append('.');
			}
		} else {
			this.start = id.start;
		}
		sb.append(id.string);
		name = sb.toString();
		
		this.length = id.start + id.length - this.start;
	}
	
	public int getElementType() {
		return QUALIFIED_NAME;
	}

	public void accept(IDElementVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return name;
	}

}
