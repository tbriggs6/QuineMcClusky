package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import qm.QTerm;
import qm.QTermNotMergableException;

public class TestQTerm {

	@Test
	public void testQTermConstructor() {
		
		String expected[] = {
				"0000", "0001", "0010", "0011", 
				"0100", "0101", "0110", "0111", 
				"1000", "1001", "1010", "1011", 
				"1100", "1101", "1110", "1111" 
		};
		
		for (int i = 0; i < expected.length; i++)
		{
			QTerm q = new QTerm(4, i);
			int minterms[] = q.getMinTerms();
			assertEquals(1, minterms.length);
			assertEquals(i, minterms[0]);
			assertEquals(expected[i], q.getImplicantString());
		}
	}

	@Test
	public void testQTermDifferences() {
		
		int expected[] = { 
				0, 1, 1, 2,
				1, 2, 2, 3,
				1, 2, 2, 3,
				2, 3, 3, 4
		};
		
		QTerm terms[] = new QTerm[16];
		
		for (int i = 0; i < 16; i++)
		{
			terms[i] = new QTerm(4, i);
		}
		
		for (int i = 0; i < 16; i++)
		{
			assertEquals(expected[i], terms[0].getNumDifferences(terms[i]));
			assertEquals(expected[i], terms[i].getNumDifferences(terms[0]));
		}
	}

	
	@Test
	public void testQTermMergeOK() throws QTermNotMergableException {
		QTerm A = new QTerm(4, 4);
		QTerm B = new QTerm(4, 12);
		
		QTerm C = A.mergeQTerm(B);
		assertEquals("-100", C.getImplicantString());
	}
	
	
	@Test
	public void testQTermMergeNOK() {
		QTerm A = new QTerm(4, 4);
		QTerm B = new QTerm(4, 8);
		
		try {
			QTerm C = A.mergeQTerm(B);
			fail("This should have thrown an exception");
		}
		catch(QTermNotMergableException E)
		{
			;
		}
	}
	
}
