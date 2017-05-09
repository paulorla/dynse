package br.ufpr.dynse;

import br.ufpr.dynse.testbed.CheckerboardTestbed;
import br.ufpr.dynse.testbed.MultipleExecutionsTestbed;

public class Main {
    public static void main( String[] args ) {
    	long startTime = System.currentTimeMillis();
		
		try{
			MultipleExecutionsTestbed testBed = new CheckerboardTestbed();
			testBed.executeTests(3);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Execution Time: " + estimatedTime/1000);
		System.out.println("Done!");
    }
}