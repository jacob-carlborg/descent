package descent.tests.mangling;

import java.text.ParseException;

import descent.core.Mangler;
import junit.framework.TestCase;

public class Demangler_Test extends TestCase {
	
	public void testDemangler1() throws Exception {
		try {
			assertEqualsI( "printf",	"printf" );
			fail();
		} catch (ParseException e) {
			
		}
	}
	
	public void testDemangler2() throws Exception {
		try {
			assertEqualsI(  "_foo",	"_foo" );
			fail();
		} catch (ParseException e) {
			
		}
	}
	
	public void testDemangler3() throws Exception {
		try {
			assertEqualsI(  "_D88",	"_D88" );
			fail();
		} catch (ParseException e) {
			
		}
	}
	
	public void testDemangler4() throws Exception {
		assertEqualsI(  "_D4test3fooAa", "char[] test.foo");
	}
	
	public void testDemangler5() throws Exception {
		assertEqualsI(  "_D8demangle8demangleFAaZAa", "char[] demangle.demangle(char[])" );
	}
	
	public void testDemangler6() throws Exception {
		assertEqualsI(  "_D6object6Object8opEqualsFC6ObjectZi", "int object.Object.opEquals(class Object)" );
	}
	
	public void testDemangler7() throws Exception {
		assertEqualsI(  "_D4test2dgDFiYd", "double delegate(int, ...) test.dg" );
	}
	
	public void testDemangler8() throws Exception {
		assertEqualsI(  "_D4test58__T9factorialVde67666666666666860140VG5aa5_68656c6c6fVPvnZ9factorialf", "float test.factorial!(double 4.2, char[5] \"hello\"c, void* null).factorial" );
	}
	
	public void testDemangler9() throws Exception {
		assertEqualsI(  "_D4test101__T9factorialVde67666666666666860140Vrc9a999999999999d9014000000000000000c00040VG5aa5_68656c6c6fVPvnZ9factorialf", "float test.factorial!(double 4.2, cdouble 6.8+3i, char[5] \"hello\"c, void* null).factorial" );
	}
	
	public void testDemangler10() throws Exception {
		assertEqualsI(  "_D4test34__T3barVG3uw3_616263VG3wd3_646566Z1xi", "int test.bar!(wchar[3] \"abc\"w, dchar[3] \"def\"d).x" );
	}
	
	public void testDemangler11() throws Exception {
		assertEqualsI(  "_D8demangle4testFLC6ObjectLDFLiZiZi", "int demangle.test(lazy class Object, lazy int delegate(lazy int))");
	}
	
	public void testDemangler12() throws Exception {
		assertEqualsI(  "_D8demangle4testFAiXi", "int demangle.test(int[] ...)");
	}
	
	public void testDemangler13() throws Exception {
		assertEqualsI(  "_D8demangle4testFLAiXi", "int demangle.test(lazy int[] ...)");
	}
	
	public void testDemangler14() throws Exception {
		assertEqualsI("_D3dfl5event54__T5EventTC3dfl7control7ControlTC3dfl5event9EventArgsZ5Event50__T10addHandlerTDFC6ObjectC3dfl5event9EventArgsZvZ10addHandlerMFDFC6ObjectC3dfl5event9EventArgsZvZv", "void dfl.event.Event!(class dfl.control.Control, class dfl.event.EventArgs).Event.addHandler!(void delegate(class Object, class dfl.event.EventArgs)).addHandler(void delegate(class Object, class dfl.event.EventArgs))");
	}
	
	public void testDemangler15() throws Exception {
		assertEqualsI("_D8demandoj6Lesson6Lesson7readIniMFAaZHAaAa", "char[][char[]] demandoj.Lesson.Lesson.readIni(char[])");
	}

	private void assertEqualsI(String string, String string2) throws Exception {
		assertEquals(string2, Mangler.demange(string));
	}

}
