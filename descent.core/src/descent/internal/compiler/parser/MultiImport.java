package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class MultiImport extends Dsymbol {
	
	public List<Import> imports;
	public boolean isstatic;
	
	public void addImport(Import imp) {
		if (imports == null) {
			imports = new ArrayList<Import>();
		}
		imports.add(imp);
	}
	
	@Override
	public int kind() {
		return MULTI_IMPORT;
	}

}
