package br.ufpr.dynse;

import br.ufpr.dynse.testbed.MultipleExecutionsTestbed;
import br.ufpr.dynse.testbed.NebraskaWeatherTestBed;

public class Main {
    public static void main( String[] args ) {
    	long startTime = System.currentTimeMillis();
		
		try{
			MultipleExecutionsTestbed testBed = new NebraskaWeatherTestBed();
			testBed.executeTests(1);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Execution Time: " + estimatedTime/1000);
		System.out.println("Done!");
    }
}