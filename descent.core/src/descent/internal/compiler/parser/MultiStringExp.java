package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

// DMD 1.020
public class MultiStringExp extends StringExp {
	
	public List<StringExp> strings = new ArrayList<StringExp>(1);
	
	public MultiStringExp(Loc loc) {
		super(loc, null);
	}
	
	public void doneParsing() {
		StringBuilder sb = new StringBuilder();
		if (strings != null) {
			this.postfix = strings.get(0).postfix;
			for(StringExp se : strings) {
				sb.append(se.string);
			}
		}
		this.string = sb.toString().toCharArray();
	}
	
	@Override
	public int getNodeType() {
		return MULTI_STRING_EXP;
	}

}
