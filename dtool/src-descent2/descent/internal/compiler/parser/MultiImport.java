package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

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

}
