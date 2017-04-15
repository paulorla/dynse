package br.ufpr.dynse.generator;

import java.util.SortedSet;
import java.util.TreeSet;

import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.instance.ConceptInstance;
import moa.streams.generators.STAGGERGenerator;

public class StaggerDriftGenerator extends STAGGERGenerator implements ConceptBasedDriftGenerator<Integer>{
	
	private static final long serialVersionUID = 1L;
	
	public static final int NUM_INST_TRAIN_CLASSIFIER_STAGGER = 20;
	public static final int NUM_INST_TEST_CLASSIFIER_STAGGER = 200;

	private static final int ordemFuncoes[] = {1,2,3,1};
	private static final int NUM_INSTANCES_EACH_CONCEPT = (NUM_INST_TRAIN_CLASSIFIER_STAGGER+
			NUM_INST_TEST_CLASSIFIER_STAGGER)*10;//10 batches per concept
	
	private int numGeneratedInstances;
	private int idxCurrentFunction;
	private int seed;
	private Concept<Integer> currentConcept;
	private SortedSet<Concept<Integer>> generatedConcepts;
	
	public StaggerDriftGenerator(int seed) {
		super();
		this.seed = seed;
		generatedConcepts = new TreeSet<Concept<Integer>>();
	}

	@Override
    public ConceptInstance nextInstance() {
		if(!this.hasMoreInstances())
			throw new RuntimeException("Impossible to create a new instance. The stream ended.");
		
		ConceptInstance conceptInstance = new ConceptInstance(super.nextInstance(), currentConcept);
		
		numGeneratedInstances++;
		if(numGeneratedInstances % NUM_INSTANCES_EACH_CONCEPT == 0 && this.idxCurrentFunction < ordemFuncoes.length-1){
			this.idxCurrentFunction++;
			currentConcept = new Concept<Integer>(ordemFuncoes[this.idxCurrentFunction]);
			generatedConcepts.add(currentConcept);
			super.functionOption.setValue(ordemFuncoes[this.idxCurrentFunction]);
		}
		return conceptInstance;
	}
	
    public void restart() {
    	this.idxCurrentFunction = 0;
    	this.generatedConcepts.clear();
    	currentConcept = new Concept<Integer>(ordemFuncoes[this.idxCurrentFunction]);
    	this.numGeneratedInstances = 0;
    	super.functionOption.setValue(ordemFuncoes[this.idxCurrentFunction]);
    	super.instanceRandomSeedOption.setValue(seed);
    	generatedConcepts.add(currentConcept);
    	super.restart();
    }
    
    @Override
    public long estimatedRemainingInstances() {
        return NUM_INSTANCES_EACH_CONCEPT * ordemFuncoes.length - numGeneratedInstances;
    }

    @Override
    public boolean hasMoreInstances() {
        return NUM_INSTANCES_EACH_CONCEPT * ordemFuncoes.length > numGeneratedInstances;
    }

	@Override
	public SortedSet<Concept<Integer>> getGeneratedConcepts() {
		return generatedConcepts;
	}
}