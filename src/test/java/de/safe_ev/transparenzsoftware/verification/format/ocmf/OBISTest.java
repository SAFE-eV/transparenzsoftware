package de.safe_ev.transparenzsoftware.verification.format.ocmf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.safe_ev.transparenzsoftware.verification.format.ocmf.OBISCode;

public class OBISTest {

	@Test
	public void testSimple()
	{
		OBISCode code1 = new OBISCode("1-b:1.8.0");
		assertEquals(1,code1.getA());
		assertEquals(0xb,code1.getB());
		assertEquals(1,code1.getC());
		assertEquals(8,code1.getD());
		assertEquals(0,code1.getE());
		System.out.println("Code1 : "+code1);
		
		OBISCode code2 = new OBISCode("01-00:01.08.00.FF");
		assertEquals(1,code2.getA());
		assertEquals(0,code2.getB());
		assertEquals(1,code2.getC());
		assertEquals(8,code2.getD());
		assertEquals(0,code2.getE());
		assertEquals(255,code2.getF());
		System.out.println("Code2 : "+code2);
		
		
	}
}
