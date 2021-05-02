package qm;

import java.util.Arrays;
import java.util.LinkedList;



public class QTerm {

	private enum QTermType {
		ZERO, ONE, DASH
	}
	
	
	boolean complete = false;
	int minterms[];
	QTermType bits[];
	
	
	private QTerm( )
	{
		complete = false;
		minterms = null;
		bits = null;
	}
	
	public QTerm(int nvars, int minterm)
	{
		minterms = new int[1];
		minterms[0] = minterm;
		
		bits = new QTermType[nvars];
		for (int i = 0; i < nvars; i++)
		{
			bits[i] = (minterm & 1 << i) == 0 ? QTermType.ZERO : QTermType.ONE;
		}
	}
	
	public String getImplicantString() 
	{
		StringBuffer buff = new StringBuffer( );
		for (int i = 0; i < bits.length; i++)
		{
			switch(bits[i]) {
			case ONE:
				buff.append("1");
				break;
			case ZERO:
				buff.append("0");
				break;
			case DASH:
				buff.append("-");
				break;
			}
		}
		
		return buff.reverse().toString();
	}
	
	public int getNumDifferences(QTerm other)
	{
		assert(this.bits.length == other.bits.length);
		
		int numDiff = 0;
		for (int i = 0; i < this.bits.length; i++)
		{
			if (this.bits[i] != other.bits[i]) numDiff++;
		}
		
		return numDiff;
	}

	
	private int findDifference(QTerm other) throws QTermNotMergableException
	{
		for (int i = 0; i < this.bits.length; i++)
		{
			if (this.bits[i] != other.bits[i]) return i;
		}
		
		throw new QTermNotMergableException();
	}
	
	private int[] mergeMinTerms(QTerm other)
	{
		
		LinkedList<Integer> terms = new LinkedList<Integer>( );
		for (int i = 0; i < this.minterms.length; i++) 
			if (!terms.contains(minterms[i]))
				terms.add(minterms[i]);
		for (int i = 0; i < other.minterms.length; i++) 
			if (!terms.contains(other.minterms[i]))
				terms.add(other.minterms[i]);
		
		java.util.Collections.sort(terms);
		int merged[] = new int[terms.size()];
		int count=0;
		for( Integer i : terms)
		{
			merged[count++] = i;
		}
		
		return merged;
	}
	
	
	public QTerm mergeQTerm(QTerm other) throws QTermNotMergableException
	{
		if (this.getNumDifferences(other) != 1) throw new QTermNotMergableException();
		
		QTerm term = new QTerm();
		term.complete = false;
		term.minterms = mergeMinTerms(other);
		

		int diffTerm = findDifference(other);
		term.bits = new QTermType[this.bits.length];
		for (int i = 0; i < this.bits.length; i++)
			term.bits[i] = this.bits[i];
		
		term.bits[diffTerm] = QTermType.DASH;
		
		return term;
	}
	
	
	public int[] getMinTerms() {
		return minterms.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bits);
		result = prime * result + (complete ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QTerm other = (QTerm) obj;
		if (!Arrays.equals(bits, other.bits))
			return false;
		if (complete != other.complete)
			return false;
		return true;
	}

	public void setComplete( ) {
		this.complete = true;
	}

	public boolean isComplete() 
	{
		return complete;
	}

	@Override
	public String toString() {
		
		StringBuffer buff = new StringBuffer();
		buff.append(String.format("m(%s)", Arrays.toString(minterms)));
		buff.append(": (");
		for (int i = bits.length-1; i >= 0; i--) {
			char ch = bits[i] == QTermType.ZERO ? '0' : bits[i] == QTermType.ONE ? '1' : '-';
			buff.append(ch);
		}
		buff.append(")");
		if (complete)
			buff.append("*");

		return buff.toString();
	}

	private String getLetter(int n)
	{
		StringBuffer buff = new StringBuffer( );
		if (n == 0) return "A";
		
		while (n > 0) 
		{
			char ch = (char) ('A' + (char) (n % 26));
			if (!Character.isAlphabetic(ch)) throw new RuntimeException("Not a letter!: " + ch);
			buff.append( ch );
			n = n / 26;
		}
		
		if (buff.length() > 1) return "(" + buff.reverse().toString() + ")";  
		else return buff.toString();
	}
	
	
	public String getTermString()
	{
		StringBuffer buff = new StringBuffer();
		for (int i = bits.length-1; i >= 0; i--) {
			if (bits[i] == QTermType.DASH) continue;
			
			String code = getLetter(bits.length -i - 1);
			if (bits[i] == QTermType.ZERO) {
				code = code.toLowerCase();
				buff.append(code+"'");
			}
			else {
				buff.append(code);
			}
						
		}
		
		return buff.toString();
	}
	
	public void clearComplete() {
		complete = false;
	}
	
	
	
	
}
