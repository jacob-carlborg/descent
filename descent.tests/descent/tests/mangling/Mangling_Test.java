package descent.tests.mangling;

import java.text.ParseException;

import descent.core.Mangler;
import junit.framework.TestCase;

public class Mangling_Test extends TestCase {
	
	public void testMangling1() throws Exception {
		try {
			assertEqualsI( "printf",	"printf" );
			fail();
		} catch (ParseException e) {
			
		}
	}
	
	public void testMangling2() throws Exception {
		try {
			assertEqualsI(  "_foo",	"_foo" );
			fail();
		} catch (ParseException e) {
			
		}
	}
	
	public void testMangling3() throws Exception {
		try {
			assertEqualsI(  "_D88",	"_D88" );
			fail();
		} catch (ParseException e) {
			
		}
	}
	
	public void testMangling4() throws Exception {
		assertEqualsI(  "_D4test3fooAa", "char[] test.foo");
	}
	
	public void testMangling5() throws Exception {
		assertEqualsI(  "_D8demangle8demangleFAaZAa", "char[] demangle.demangle(char[])" );
	}
	
	public void testMangling6() throws Exception {
		assertEqualsI(  "_D6object6Object8opEqualsFC6ObjectZi", "int object.Object.opEquals(class Object)" );
	}
	
	public void testMangling7() throws Exception {
		assertEqualsI(  "_D4test2dgDFiYd", "double delegate(int, ...) test.dg" );
	}
	
	public void testMangling8() throws Exception {
		assertEqualsI(  "_D4test58__T9factorialVde67666666666666860140VG5aa5_68656c6c6fVPvnZ9factorialf", "float test.factorial!(double 4.2, char[5] \"hello\"c, void* null).factorial" );
	}
	
	public void testMangling9() throws Exception {
		assertEqualsI(  "_D4test101__T9factorialVde67666666666666860140Vrc9a999999999999d9014000000000000000c00040VG5aa5_68656c6c6fVPvnZ9factorialf", "float test.factorial!(double 4.2, cdouble 6.8+3i, char[5] \"hello\"c, void* null).factorial" );
	}
	
	public void testMangling10() throws Exception {
		assertEqualsI(  "_D4test34__T3barVG3uw3_616263VG3wd3_646566Z1xi", "int test.bar!(wchar[3] \"abc\"w, dchar[3] \"def\"d).x" );
	}
	
	public void testMangling11() throws Exception {
		assertEqualsI(  "_D8demangle4testFLC6ObjectLDFLiZiZi", "int demangle.test(lazy class Object, lazy int delegate(lazy int))");
	}
	
	public void testMangling12() throws Exception {
		assertEqualsI(  "_D8demangle4testFAiXi", "int demangle.test(int[] ...)");
	}
	
	public void testMangling13() throws Exception {
		assertEqualsI(  "_D8demangle4testFLAiXi", "int demangle.test(lazy int[] ...)");
	}
	
	public void testMangling14() throws Exception {
		assertEqualsI("_D3dfl5event54__T5EventTC3dfl7control7ControlTC3dfl5event9EventArgsZ5Event50__T10addHandlerTDFC6ObjectC3dfl5event9EventArgsZvZ10addHandlerMFDFC6ObjectC3dfl5event9EventArgsZvZv", "void dfl.event.Event!(class dfl.control.Control, class dfl.event.EventArgs).Event.addHandler!(void delegate(class Object, class dfl.event.EventArgs)).addHandler(void delegate(class Object, class dfl.event.EventArgs))");
	}

	private void assertEqualsI(String string, String string2) throws Exception {
		assertEquals(string2, Mangler.demange(string));
	}

}
