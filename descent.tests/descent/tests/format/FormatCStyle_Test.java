package descent.tests.format;

import java.util.HashMap;
import java.util.Map;

public class FormatCStyle_Test extends AbstractFormatter_Test {

	@Override
	protected Map getDefaultOptions() {
		return new HashMap();
	}
	
	public void testOne() throws Exception {
		assertFormat(
				"alias void* (*alloc_func)(void* opaque, uint items, uint size);", 
				"alias void* (*alloc_func)(void* opaque, uint items, uint size);");
	}
	
	public void testTwo() throws Exception {
		assertFormat(
				"alias void (*GC_FINALIZER)(void* p, void* dummy);",
				"alias void (*GC_FINALIZER)(void* p, void* dummy);");
	}
	
	public void testThree() throws Exception {
		assertFormat(
				"alias void (*GC_FINALIZER)();",
				"alias void (*GC_FINALIZER)();");
	}

}
