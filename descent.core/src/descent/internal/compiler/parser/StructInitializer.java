package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class StructInitializer extends Initializer {
	
	public List<IdentifierExp> field;
	public List<Initializer> value;
	
	public void addInit(IdentifierExp field, Initializer value) {
		if (this.field == null) {
			this.field = new ArrayList<IdentifierExp>();
			this.value = new ArrayList<Initializer>();
		}
		this.field.add(field);
		this.value.add(value);
	}
	
	@Override
	public int kind() {
		return STRUCT_INITIALIZER;
	}

}
