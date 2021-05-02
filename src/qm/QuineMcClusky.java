package qm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class QuineMcClusky {

	ArrayList<Integer> minterms;
	ArrayList<QTerm> terms;
	int nvars;
	
	public QuineMcClusky(int nvars )
	{
		this.minterms = new ArrayList<Integer>();
		this.nvars = nvars;
		terms = new ArrayList<QTerm>( );
	}
	
	public void addTerm(int n)
	{
		assert(n < (1 << nvars));
		
		
		QTerm term = new QTerm(nvars, n);
		if (!terms.contains(term)) {
			minterms.add(n);
			terms.add(term);
		}
	}
	
	
	public void addDNCTerm(int n)
	{
		assert(n < (1 << nvars));
		
		
		QTerm term = new QTerm(nvars, n);
		if (!terms.contains(term)) {
			terms.add(term);
		}
	}
	
	public void simplify( ) throws QTermNotMergableException
	{
	
		int iteration = 0;
		while (! isSetComplete()) 
		{
			ArrayList<QTerm> workingSet = new ArrayList<QTerm>( );
			
			for (QTerm A : terms)
				A.setComplete();
			
			for (int i = 0; i < terms.size(); i++) 
			{
				QTerm A = terms.get(i);
				
				for (int j = i; j < terms.size(); j++)
				{
					QTerm B = terms.get(j);
					if (A.getNumDifferences(B) == 1)
					{
						A.clearComplete();
						B.clearComplete();
						
						QTerm C = A.mergeQTerm(B);
						
						if (!workingSet.contains(C))
							workingSet.add(C);
					}
				}
			}
			
			// look for any term that wasn't used
			for (QTerm A : terms)
				if (A.isComplete())
					workingSet.add(A);
			
			terms = workingSet;
			
			System.out.println("**********************************\n");
			System.out.print("Iteration " + iteration + " ");
			System.out.println(this.toString());
			System.out.println("**********************************\n");
			
			iteration++;
			
		}
	}

	
	public boolean[][] computeCoverTable( )
	{
		// construct a 2D table, cols = the original min terms, rows = the prime implicants
		boolean cover[][] = new boolean[terms.size()][minterms.size()];
		for (int i = 0; i < terms.size(); i++)
			for (int j = 0; j < minterms.size(); j++)
				cover[i][j] = false;
		
		// construct a mapping of minterm to column, used to make it easier to compile table
		int mincols[] = new int[1 << nvars];
		for (int i = 0; i < mincols.length; i++) mincols[i] = -1;
		for (int i = 0; i < minterms.size(); i++)
		{
			int minterm = minterms.get(i);
			mincols[minterm] = i;
		}
		
		//at this point, supposed we had nvars=3, minterms={0,3,7}
		// then mincols = { 0, -1, -1, 1,  -1, -1, -1, 2 }
		
		for (int row = 0; row < terms.size(); row++)
		{
			QTerm A = terms.get(row);
			
			int qmterms[] = A.getMinTerms();
			for (int j = 0; j < qmterms.length; j++)
			{
				int qmterm = qmterms[j];
				int col = mincols[qmterm];
				if (col != -1)
					cover[row][col] = true;
			}
		}
		
		return cover;
	}
	
	
	boolean computeCoverageForPermuation(int perm, boolean coverSet[][])
	{
		// make an arry of vars and set to not covered
		boolean vars[] = new boolean[coverSet[0].length];
		for (int i = 0; i < vars.length; i++) vars[i] = false;
		
		for (int i = 0; i < coverSet.length; i++)
		{
			// if this row is not in this permuation, skip it
			if ((perm & (1 << i))  == 0) continue;
			
			// for all of the variables covered by this row, set the 
			// covered flag for this permutation
			for (int j = 0; j < coverSet[i].length; j++)
			{
				if (coverSet[i][j])
					vars[j] = true;
			}
		}
		
		// if any of the variables aren't covered, then this set
		// isn't a cover
		for (int i = 0; i < vars.length; i++)
			if (vars[i] == false) return false;
		
		// all of the variables are covered, so this is a cover
		return true;
		
	}
	
	
	int numBits(int perm)
	{
		return Integer.bitCount(perm);
	}
	
	public void computeCover()
	{
		boolean cover[][] = computeCoverTable();
		
		int npermutations = 1 << terms.size();
		int min = Integer.MAX_VALUE;
		int bestPermutation = -1;
		
		for (int i = 0; i < npermutations; i++)
		{
			if (computeCoverageForPermuation(i, cover))
			{
				if (numBits(i) < min)
				{
					min = numBits(i);
					bestPermutation = i;
				}
			}
		}
		
		System.out.println("The best permutation of variables has " + min + " terms.");
		
		System.out.print("F() = ");
		boolean first = true;
		for (int i = 0; i < terms.size(); i++)
		{
			if ((bestPermutation & (1 << i)) == 0) continue;
			
			if (!first) System.out.print(" + ");
			first = false;
			System.out.print(terms.get(i).getTermString());
		}
		System.out.println("");
		
	}
	
	@Override
	public String toString() {
		return "QuineMcClusky [terms=" + terms + "]";
	}

	public boolean isSetComplete( )
	{
		for (QTerm A : terms)
		{
			if (!A.isComplete()) return false;
		}
		
		return true;
	}
	
	
//	public static void main(String args[])
//	{
//		int minterms[] = { 4,8,10,11,12,15 };
//		int dnc[] = { 9,14 };
//		
//		QuineMcClusky Q = new QuineMcClusky(4);
//		for (int i = 0; i < minterms.length; i++)
//			Q.addTerm(minterms[i]);
//		for (int j = 0; j < dnc.length; j++)
//			Q.addDNCTerm(dnc[j]);
//		
//		try {
//			Q.simplify();
//			Q.computeCover();
//		} catch (QTermNotMergableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	

	public static void main(String args[])
	{
		int N = 10;
		
		double cover = 0.80;
		double dncrate = 0.01;
		
		int M = (int)(Math.pow(2, N) * cover);
		
		HashSet<Integer> minSet = new HashSet<Integer>( );
		HashSet<Integer> dncSet = new HashSet<Integer>( );
		
		for (int i = 0; i < M; i++)
		{
			int j; 
			do {
				j = (int) (Math.random() * Math.pow(2, N));
			} while (minSet.contains(j) || (dncSet.contains(j)));
			
			if (Math.random() > dncrate) 
				dncSet.add(j);
			else
				minSet.add(j);
		}
		
		long start = System.currentTimeMillis( );
		
		System.out.format("%d min terms and %d dnc terms\n", dncSet.size(), minSet.size());
		QuineMcClusky Q = new QuineMcClusky(N);
		for (int i  : minSet)
			Q.addTerm(i);
		for (int i : dncSet)
			Q.addDNCTerm(i);
		
		try {
			Q.simplify();
			Q.computeCover();
		} catch (QTermNotMergableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis( );
		
		System.out.format("Time: %d ms", (end-start));
	}

	
	
}
