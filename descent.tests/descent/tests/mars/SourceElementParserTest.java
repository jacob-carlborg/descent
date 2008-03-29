package descent.tests.mars;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import descent.internal.compiler.ISourceElementRequestor;
import descent.internal.compiler.SourceElementParser;
import descent.internal.compiler.env.ICompilationUnit;
import descent.internal.compiler.impl.CompilerOptions;

public class SourceElementParserTest extends MockObjectTestCase {
	
	ISourceElementRequestor requestor;
	ICompilationUnit unit;
	
	@Override
	public void setUp() {
		requestor = mock(ISourceElementRequestor.class);
		unit = mock(ICompilationUnit.class, "unit2");
	}
	
	public void testEmpty() {
		withSource("");
		
		checking(new Expectations() {{
			one(requestor).enterCompilationUnit();
			one(requestor).exitCompilationUnit(0);
		}});
		
		doIt();
	}
	
//	public void testVariable() {
//		withSource(" int x;");
//		
//		final FieldInfo info = new FieldInfo();
//		info.declarationStart = 1;
//		info.initializationSource = CharOperation.NO_CHAR;
//		info.modifiers = 0;
//		info.name = "x".toCharArray();
//		
//		checking(new Expectations() {{
//			one(requestor).enterCompilationUnit();
//			one(requestor).enterField(info);
//			one(requestor).exitCompilationUnit(7);
//		}});
//		
//		doIt();
//	}
	
	private void withSource(final String source) {
		checking(new Expectations() {{
			one(unit).getContents(); will(returnValue(source.toCharArray()));
			one(unit).getFileName(); will(returnValue("module.d".toCharArray()));
			one(unit).getFullyQualifiedName(); will(returnValue("some.module"));
		}});
	}
	
	private void doIt() {
		SourceElementParser parser = new SourceElementParser(requestor, new CompilerOptions());
		parser.parseCompilationUnit(unit);
	}

}
