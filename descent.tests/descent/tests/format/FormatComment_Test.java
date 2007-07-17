package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

public class FormatComment_Test extends AbstractFormatter_Test {

	@Override
	protected Map getDefaultOptions() {
		return new HashMap();
	}
	
	public void test() throws Exception {
		assertFormat(
				"// comment\r\n" +
				"int x;", 
				
				"// comment\r\n" +
				"int x;"
			);
	}

}
