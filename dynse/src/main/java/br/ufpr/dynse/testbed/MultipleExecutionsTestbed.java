package br.ufpr.dynse.testbed;

public interface MultipleExecutionsTestbed{
	
	public abstract void executeTests(int numExec) throws Exception;
}