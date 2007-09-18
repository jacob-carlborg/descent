package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import melnorme.miscutil.tree.TreeVisitor;
import descent.internal.compiler.parser.ast.IASTVisitor;

// DMD 1.020
public class MultiImport extends Dsymbol {
	
	public List<Import> imports;
	public boolean isstatic;
	
	public MultiImport(Loc loc) {
		super(loc);
	}
	
	public void addImport(Import imp) {
		if (imports == null) {
			imports = new ArrayList<Import>();
		}
		imports.add(imp);
	}
	
	@Override
	public int getNodeType() {
		return MULTI_IMPORT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, imports);
		}
		visitor.endVisit(this);
	}

}
