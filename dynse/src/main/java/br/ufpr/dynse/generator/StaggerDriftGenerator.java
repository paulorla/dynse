/*    
*    StaggerDriftGenerator.java 
*    Copyright (C) 2017 Universidade Federal do Paraná, Curitiba, Paraná, Brasil
*    @Author Paulo Ricardo Lisboa de Almeida (prlalmeida@inf.ufpr.br)
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*    
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*    
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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