/*    
*    AbstractDynse.java 
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
package br.ufpr.dynse.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import br.ufpr.dynse.classificationengine.IClassificationEngine;
import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.competence.IClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.classifier.factory.AbstractClassifierFactory;
import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.instance.InstanceComparator;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import br.ufpr.dynse.pruningengine.IPruningEngine;
import moa.classifiers.Classifier;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;
import moa.core.Measurement;

public abstract class AbstractDynse<T extends List<Instance>, U extends IMultipleClassifiersCompetence> 
		extends ConceptMeasuredAbstractClassifier implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int numMinInstancesTrainClassifier;
	private int accuracyEstimationWindowSize;
	
	private List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers;
	private NearestNeighbourSearch nnSearch;
	private IClassificationEngine<U> classificationEngine;
	private IPruningEngine<DynseClassifierPruningMetrics> pruningEngine;
	
	private T trainInstancesAccumulator;
	private T accuracyEstimationInstances;
	private Map<Instance, U> classifiersMapping;
	private LinkedHashMap<Concept<?>, Integer> conceptUsage;
	private LinkedHashMap<Concept<?>, Integer> classifiersByConcept;
	private LinkedHashMap<Concept<?>, Integer> totalClassifiersByConcepLastCheck;
	
	private AbstractClassifierFactory classifierFactory;
	private final InstanceComparator instanceComparator = new InstanceComparator();

	public AbstractDynse(AbstractClassifierFactory classifierFactory, int numMinInstancesTrainClassifier,
			IClassificationEngine<U> classificationEngine) throws Exception{
		this(classifierFactory, numMinInstancesTrainClassifier, numMinInstancesTrainClassifier, classificationEngine, null);
	}
	
	public AbstractDynse(AbstractClassifierFactory classifierFactory, int numMinInstancesTrainClassifier,
			int accuracyEstimationWindowSize, IClassificationEngine<U> classificationEngine, 
			IPruningEngine<DynseClassifierPruningMetrics> pruningEngine) throws Exception{
		if(accuracyEstimationWindowSize < numMinInstancesTrainClassifier)
			throw new Exception("It is not allowed to create a Dynse with the number of instances in the accuracyEstimationWindow "
					+ "smaller than the number of instances used to build new classifiers.");
		
		this.numMinInstancesTrainClassifier = numMinInstancesTrainClassifier;
		this.accuracyEstimationWindowSize = accuracyEstimationWindowSize;
		this.classificationEngine = classificationEngine;
		conceptUsage = new LinkedHashMap<Concept<?>, Integer>();
		totalClassifiersByConcepLastCheck = new LinkedHashMap<Concept<?>, Integer>();
		classifiersByConcept = new LinkedHashMap<Concept<?>, Integer>();
		
		this.pruningEngine = pruningEngine;
		this.resetLearning();
		
		this.classifierFactory = classifierFactory;
	}
	
	protected DynseClassifier<DynseClassifierPruningMetrics> addNewClassifierAccumulatedInstances(List<Instance> trainBatch, 
			List<Instance> accuracyEstimationBatch, Set<Concept<?>> trainConcepts) throws Exception{
		Classifier newClassifier = classifierFactory.buildClassifier(trainBatch);
		DynseClassifier<DynseClassifierPruningMetrics> meassuredNewClassifier =  new DynseClassifier<DynseClassifierPruningMetrics>(newClassifier);
		meassuredNewClassifier.setDynseClassifierMetrics(new DynseClassifierPruningMetrics(meassuredNewClassifier));
		
		List<DynseClassifier<DynseClassifierPruningMetrics>> classifiersToPrune = 
				pruningEngine.pruneClassifiers(meassuredNewClassifier, classifiers, accuracyEstimationBatch);
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : classifiersToPrune ){
			if(dc != meassuredNewClassifier){
				for(Concept<?> concept : dc.getTrainingConcepts()){
					Integer val = classifiersByConcept.get(concept);
					val--;
					classifiersByConcept.put(concept, val);
				}
				this.pruneClassifier(dc);
			}else{
				meassuredNewClassifier = null;
			}
		}
		
		if(meassuredNewClassifier != null){
			classifiers.add(meassuredNewClassifier);
			for(Concept<?> concept : meassuredNewClassifier.getTrainingConcepts()){
				Integer val = classifiersByConcept.get(concept);
				if(val != null)
					val++;
				else
					val = 1;
				classifiersByConcept.put(concept, val);
			}
		}
		
		return meassuredNewClassifier;
	}
	
	
	protected void pruneClassifier(DynseClassifier<DynseClassifierPruningMetrics> classifier){
		Collection<U> multipleCompetences = classifiersMapping.values();
		for(U mc: multipleCompetences){
			Set<IClassifierCompetence> competences = mc.getClassifiersCompetence();
			Set<IClassifierCompetence> competencesToRemove = new HashSet<IClassifierCompetence>();
			for(IClassifierCompetence comp : competences){
				if(comp.getClassifier().equals(classifier))
					competencesToRemove.add(comp);
			}
			competences.removeAll(competencesToRemove);
		}
		classifiers.remove(classifier);
	}

	@Override
	public boolean isRandomizable() {
		return false;
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		Measurement[] retorno = {new Measurement("Pool Size", classifiers.size())};
		return retorno;
	}
	
	@Override
	public LinkedHashMap<Concept<?>, Measurement> getConceptMeasurementsUpToLastCheck(){
		LinkedHashMap<Concept<?>, Measurement> retorno = new LinkedHashMap<Concept<?>, Measurement>();
		double total = 0.0;
		for (Map.Entry<Concept<?>, Integer> entry : conceptUsage.entrySet())
		{
			double val = (double)entry.getValue()/totalClassifiersByConcepLastCheck.get(entry.getKey());
			retorno.put(entry.getKey(), new Measurement(entry.getKey().toString(), val));
			total+=val;
		}
		
		for(Concept<?> concept : conceptUsage.keySet()){
			Measurement nonNormalized = retorno.get(concept);
			Measurement m = new Measurement(nonNormalized.getName(), nonNormalized.getValue()/total);
			retorno.put(concept,m);
		}
		
		this.resetConceptUsageStatistic();
		return retorno;
	}
	
	@Override
	public LinkedHashMap<Concept<?>, Integer> getTotalConceptsUpToLastCheck(){
		return totalClassifiersByConcepLastCheck;
	}

	@Override
	public void resetLearningImpl() {
		trainInstancesAccumulator = this.createEmptyTrainingInstancesAccumulator();
		accuracyEstimationInstances = this.createEmptyAccuracyEstimationAccumulator();
		classifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
		this.resetClassifiersMapping();
		this.classificationEngine.reset();
		this.resetConceptUsageStatistic();
	}
	
	public int getNumClassifiersPool(){
		return classifiers.size();
	}
	
	@Override
	public double[] getVotesForInstance(Instance instancia) {
		try{
			if(classifiers.size() < 1){
				this.trainClassifierIncompleteBatch();
			}
			double[] retorno = classificationEngine.classify(instancia, classifiers, classifiersMapping, nnSearch);
			
			for(DynseClassifier<?> dc : classificationEngine.getClassifiersUsedInLastClassification()){
				for(Concept<?> concept : dc.getTrainingConcepts()){
					Integer uso = conceptUsage.get(concept);
					if(uso == null)
						uso = 1;
					else
						uso++;
					conceptUsage.put(concept, uso);
				}
			}
				
			for (Map.Entry<Concept<?>, Integer> entry : classifiersByConcept.entrySet())
			{
			    Integer val = totalClassifiersByConcepLastCheck.get(entry.getKey());
			    if(val!=null)
			    	val+=entry.getValue();
			    else
			    	val = entry.getValue();
			    totalClassifiersByConcepLastCheck.put(entry.getKey(), val);
			}
			
			return retorno;
		}catch(Exception e){
			StringBuilder builder = new StringBuilder();
			getModelDescription(builder, 0);
			throw new RuntimeException(e);
		}
	}
	
	protected IClassificationEngine<U> getClassificationEngine() {
		return classificationEngine;
	}

	public IPruningEngine<DynseClassifierPruningMetrics> getPruningEngine() {
		return pruningEngine;
	}

	protected void setClassificationEngine(IClassificationEngine<U> classificationEngine) {
		this.classificationEngine = classificationEngine;
	}

	protected int getNumMinInstancesTrainClassifier() {
		return numMinInstancesTrainClassifier;
	}

	public List<DynseClassifier<DynseClassifierPruningMetrics>> getClassifiers() {
		return classifiers;
	}

	protected NearestNeighbourSearch getNnSearch() {
		return nnSearch;
	}
	
	protected void createNNSearch() throws Exception{
		this.nnSearch = classificationEngine.createNeighborSearchMethod();
	}
	
	protected void setNNSearchInstances(Instances instances) throws Exception{
		this.nnSearch.setInstances(instances);
	}
	
	protected void createNNSearch(Instances instancias) throws Exception{
		this.createNNSearch();
		this.setNNSearchInstances(instancias);
	}

	protected T getTrainInstancesAccumulator() {
		return trainInstancesAccumulator;
	}

	public int getAccuracyEstimationWindowSize() {
		return accuracyEstimationWindowSize;
	}

	public T getAccuracyEstimationInstances() {
		return accuracyEstimationInstances;
	}

	protected Map<Instance, U> getClassifiersMapping() {
		return classifiersMapping;
	}

	protected AbstractClassifierFactory getClassifierFactory() {
		return classifierFactory;
	}
	
	protected void resetClassifiersMapping(){
		classifiersMapping = new TreeMap<Instance, U>(instanceComparator);
	}
	
	public void printClassifiersData(){
		for(DynseClassifier<?> classifier : classifiers){
			System.out.println(classifier.toString());
		}
	}
	
	protected void resetConceptUsageStatistic(){
		conceptUsage.clear();
		totalClassifiersByConcepLastCheck.clear();
	}
	
	//may be used in scenarios where there are no enought instances to train a new classifier and the pool is empty, but is necessary to classify an instance
	protected abstract void trainClassifierIncompleteBatch() throws Exception;
	
	protected abstract T createEmptyTrainingInstancesAccumulator();
	
	protected abstract T createEmptyAccuracyEstimationAccumulator();
	
	public abstract void getShortDescription(StringBuilder out, int ident);
}