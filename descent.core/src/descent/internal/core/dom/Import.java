package descent.internal.core.dom;

import java.util.ArrayList;
import java.util.List;

import descent.core.dom.ElementVisitor;
import descent.core.dom.IImport;
import descent.core.dom.IName;
import descent.core.dom.IQualifiedName;
import descent.core.dom.ISelectiveImport;

public class Import extends Dsymbol implements IImport {
	
	public List<Identifier> packages;
	public List<ISelectiveImport> selectiveImports;
	public QualifiedName qName;
	public Identifier alias;

	public Import(List<Identifier> packages, Identifier id, Identifier aliasid, boolean isstatic) {
		this.ident = id;
		this.packages = packages;
		this.qName = new QualifiedName(packages, id);
		this.alias = aliasid;
	}

	public void addAlias(Identifier name, Identifier alias) {
		if (selectiveImports == null) {
			selectiveImports = new ArrayList<ISelectiveImport>();
		}
		selectiveImports.add(new SelectiveImport(name, alias));
	}

	public IQualifiedName getQualifiedName() {
		return qName;
	}
	
	public IName getAlias() {
		return alias;
	}
	
	public ISelectiveImport[] getSelectiveImports() {
		if (selectiveImports == null) return new ISelectiveImport[0];
		return selectiveImports.toArray(new ISelectiveImport[selectiveImports.size()]);
	}
	
	public int getElementType() {
		return IMPORT;
	}
	
	public void accept0(ElementVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptChild(visitor, alias);
			acceptChild(visitor, qName);
			acceptChildren(visitor, selectiveImports);
		}
		visitor.endVisit(this);
	}

}
