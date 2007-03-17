package descent.internal.compiler.parser;

import java.util.ArrayList;
import java.util.List;

public class MultiStringExp extends StringExp {
	
	public List<StringExp> strings = new ArrayList<StringExp>(1);
	
	public MultiStringExp() {
		super(null);
	}
	
	public void doneParsing() {
		StringBuilder sb = new StringBuilder();
		if (strings != null) {
			this.postfix = strings.get(0).postfix;
			for(StringExp se : strings) {
				sb.append(se.string);
			}
		}
		this.string = sb.toString();
	}
	
	@Override
	public int kind() {
		return MULTI_STRING_EXP;
	}

}
