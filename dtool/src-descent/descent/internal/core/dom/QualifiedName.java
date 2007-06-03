package descent.internal.core.dom;

import java.util.List;

import descent.core.dom.IName;
import descent.core.domX.IASTVisitor;
import descent.core.domX.AbstractElement;

public class QualifiedName extends AbstractElement implements IName  {
	
	public String name;
	
	public QualifiedName(Identifier id) {
		this.name = id.string;
		this.startPos = id.startPos;
		this.length = id.length;
	}
	
	public QualifiedName(List<Identifier> ids) {
		this.startPos = ids.get(0).startPos;
		Identifier last = ids.get(ids.size() - 1);
		this.length = last.startPos + last.length - this.startPos; 
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
		this.startPos = id.startPos;
		StringBuilder sb = new StringBuilder();
		sb.append(id);

		for(Identifier idx : ids) {
			sb.append('.');
			sb.append(idx.string);
		}
		Identifier last = ids.get(ids.size() - 1);
		this.length = last.startPos + last.length - this.startPos;

		name = sb.toString();
	}
	
	public QualifiedName(List<Identifier> packages, Identifier id) {
		StringBuilder sb = new StringBuilder();
		if (packages != null) {
			this.startPos = packages.get(0).startPos;
			for(Identifier idx : packages) {
				sb.append(idx.string);
				sb.append('.');
			}
		} else {
			this.startPos = id.startPos;
		}
		sb.append(id.string);
		name = sb.toString();
		
		this.length = id.startPos + id.length - this.startPos;
	}
	
	public int getElementType() {
		return ElementTypes.QUALIFIED_NAME;
	}

	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toString() {
		return name;
	}

}
