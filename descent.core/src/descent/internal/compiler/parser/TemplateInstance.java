package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;


public class TemplateInstance extends ScopeDsymbol {
	
	public List<IdentifierExp> idents;
	public List<ASTNode> tiargs;

	public TemplateInstance(IdentifierExp id) {
		super(null);
		this.idents = new ArrayList<IdentifierExp>(3);
		this.idents.add(id);
	}
	
	@Override
	public int kind() {
		return TEMPLATE_INSTANCE;
	}
	
}