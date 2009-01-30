package descent.tests.mars;

import java.math.BigDecimal;

import descent.internal.compiler.parser.real_t;

import junit.framework.TestCase;

public class real_t_Test extends TestCase {
	
	public void testStrtold1() throws Exception {
		BigDecimal decimal = real_t.strtold("1");
		assertEquals(1.0, decimal.doubleValue());
	}
	
	public void testStrtold0x1() throws Exception {
		BigDecimal decimal = real_t.strtold("0x1");
		assertEquals(1.0, decimal.doubleValue());
	}
	
	public void testStrtold0xF() throws Exception {
		BigDecimal decimal = real_t.strtold("0xF");
		assertEquals(15.0, decimal.doubleValue());
	}
	
	public void testStrtold_1() throws Exception {
		BigDecimal decimal = real_t.strtold("-1");
		assertEquals(-1.0, decimal.doubleValue());
	}
	
	public void testStrtold_0x1() throws Exception {
		BigDecimal decimal = real_t.strtold("-0x1");
		assertEquals(-1.0, decimal.doubleValue());
	}
	
	public void testStrtold_0xF() throws Exception {
		BigDecimal decimal = real_t.strtold("-0xF");
		assertEquals(-15.0, decimal.doubleValue());
	}
	
	public void testStrtold1e1() throws Exception {
		BigDecimal decimal = real_t.strtold("1e1");
		assertEquals(10.0, decimal.doubleValue());
	}
	
	public void testStrtold1e2() throws Exception {
		BigDecimal decimal = real_t.strtold("1e2");
		assertEquals(100.0, decimal.doubleValue());
	}
	
	public void testStrtold1e_2() throws Exception {
		BigDecimal decimal = real_t.strtold("1e-2");
		assertEquals(0.01, decimal.doubleValue());
	}
	
	public void testStrtold_1e_2() throws Exception {
		BigDecimal decimal = real_t.strtold("-1e-2");
		assertEquals(-0.01, decimal.doubleValue());
	}
	
	public void testStrtold1p2() throws Exception {
		BigDecimal decimal = real_t.strtold("0x1p2");
		assertEquals(4.0, decimal.doubleValue());
	}
	
	public void testStrtold1p_2() throws Exception {
		BigDecimal decimal = real_t.strtold("0x1p-2");
		assertEquals(0.25, decimal.doubleValue());
	}
	
	public void testStrtold_1p_2() throws Exception {
		BigDecimal decimal = real_t.strtold("-0x1p-2");
		assertEquals(-0.25, decimal.doubleValue());
	}
	
	public void testStrtold1_2() throws Exception {
		BigDecimal decimal = real_t.strtold("1.2");
		assertEquals(1.2, decimal.doubleValue());
	}
	
	public void testStrtold0x1_2() throws Exception {
		BigDecimal decimal = real_t.strtold("0x1.2");
		assertEquals(1.125, decimal.doubleValue());
	}
	
	public static void main(String[] args) {
		System.out.println(Double.parseDouble("1.0124e+07"));
	}

}
