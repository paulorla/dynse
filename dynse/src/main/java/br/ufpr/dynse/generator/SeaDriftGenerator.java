package br.ufpr.dynse.generator;

import java.util.SortedSet;
import java.util.TreeSet;

import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.instance.ConceptInstance;
import moa.streams.generators.SEAGenerator;

public class SeaDriftGenerator extends SEAGenerator implements ConceptBasedDriftGenerator<Integer>{
	
	private static final long serialVersionUID = 1L;
	
	protected int numGeneratedInstances;
	protected Concept<Integer> currentConcept;
	protected SortedSet<Concept<Integer>> generatedConcepts;
	protected int[] functions; 
	
	public SeaDriftGenerator(int seed){
		this(seed, new int[]{1, 2, 3, 4});
	}
	
	public SeaDriftGenerator(int seed, int[] functions){
		super.numInstancesConcept.setValue(25000);//12500 for training and e 12500 for testing
		for(int f : functions){
			if(f < 1 || f > 4)
				throw new RuntimeException("The function values must be between 1 and 4.");
		}
		generatedConcepts = new TreeSet<Concept<Integer>>();
		this.setSeed(seed);
		this.functions = functions;
		super.functionOption.setValue(functions[0]);
	}
	
	public int[] getFunctions() {
		return functions;
	}

	public int getSeed(){
		return super.instanceRandomSeedOption.getValue();
	}
	
	public void setSeed(int seed){
		super.instanceRandomSeedOption.setValue(seed);
	}
	
	protected Concept<Integer> getCurrentConcept() {
		return currentConcept;
	}

	protected void setCurrentConcept(Concept<Integer> currentConcept) {
		this.currentConcept = currentConcept;
	}

	@Override
    public ConceptInstance nextInstance() {
		if(!this.hasMoreInstances())
			throw new RuntimeException("Impossible to create a new instance. The stream ended.");
		
		ConceptInstance retorno = new ConceptInstance(super.nextInstance(), currentConcept);
		numGeneratedInstances++;
		
		this.verifyAndChangeConcept();
		
		return retorno;
	}
	
	protected void verifyAndChangeConcept(){
		if(numGeneratedInstances % super.numInstancesConcept.getValue() == 0 && this.hasMoreInstances()){
			super.functionOption.setValue(functions[numGeneratedInstances/super.numInstancesConcept.getValue()]);
			currentConcept = new Concept<Integer>(super.functionOption.getValue());
			generatedConcepts.add(currentConcept);
		}
	}
	
	protected ConceptInstance nextInstanceNoCount() {
		if(!this.hasMoreInstances())
			throw new RuntimeException("Impossible to create a new instance. The stream ended.");
		
		ConceptInstance retorno = new ConceptInstance(super.nextInstance(), currentConcept);
		return retorno;
	}
	
	@Override
    public void restart() {
    	this.numGeneratedInstances = 0;
    	super.functionOption.setValue(1);
    	currentConcept = new Concept<Integer>(super.functionOption.getValue());
    	generatedConcepts.clear();
    	generatedConcepts.add(currentConcept);
    	super.restart();
    }
    
    @Override
    public long estimatedRemainingInstances() {
        return super.numInstancesConcept.getValue() * functions.length - numGeneratedInstances;
    }

    @Override
    public boolean hasMoreInstances() {
        return super.numInstancesConcept.getValue() * functions.length > numGeneratedInstances;
    }

	public SortedSet<Concept<Integer>> getGeneratedConcepts() {
		return generatedConcepts;
	}
}