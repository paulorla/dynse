package br.ufpr.dynse;

import br.ufpr.dynse.testbed.MultipleExecutionsTestbed;
import br.ufpr.dynse.testbed.StaggerTestbed;

public class Main {
    public static void main( String[] args ) {
    	long startTime = System.currentTimeMillis();
		
		try{
			MultipleExecutionsTestbed testBed = new StaggerTestbed();
			testBed.executeTests(10);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Execution Time: " + estimatedTime/1000);
		System.out.println("Done!");
    }
}