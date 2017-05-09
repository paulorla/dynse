package br.ufpr.dynse.classificationengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.MajorityVoting;
import br.ufpr.dynse.classifier.competence.IClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.Classifier;
import moa.classifiers.lazy.neighboursearch.LinearNNSearch;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;
import moa.core.Utils;

public class OLAClassificationEngine implements IClassificationEngine<IMultipleClassifiersCompetence>{

	private static final long serialVersionUID = 1L;
	
	private int kNeighbors;
	private MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>> classifierCombiner;
	private List<DynseClassifier<DynseClassifierPruningMetrics>> classifiersUsedInLastClassification;
	
	public OLAClassificationEngine(int kNeighbors) {
		this.kNeighbors = kNeighbors;
		
		this.classifierCombiner = new MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>>();
	}

	@Override
	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, IMultipleClassifiersCompetence> classifiersMapping,
			NearestNeighbourSearch nnSearch) throws Exception{
		
		double agreedClass = -1.0;
		boolean allClassifiersAgree = true;
		double[] distribution = null;
		for(Classifier c :availableClassifiers){
			distribution = c.getVotesForInstance(instance);
			double classGiven = Utils.maxIndex(distribution);
			if(classGiven != agreedClass){
				if(agreedClass == -1.0){
					agreedClass = classGiven;
				}else{
					allClassifiersAgree = false;
					break;
				}
			}
		}
		if(allClassifiersAgree == true){//If all agree, we may return any of the results
			classifiersUsedInLastClassification = availableClassifiers;
			this.increaseClassifiersFactor(availableClassifiers);
			return availableClassifiers.get(0).getVotesForInstance(instance);
		}
		
		Map<DynseClassifier<DynseClassifierPruningMetrics>, Integer> hitMap = new HashMap<DynseClassifier<DynseClassifierPruningMetrics>, Integer>();
		List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
		
		Instances neighbours = nnSearch.kNearestNeighbours(instance, kNeighbors);
		
		for(int j =0; j < neighbours.numInstances(); j++){
			IMultipleClassifiersCompetence classifiersCompetence = classifiersMapping.get(neighbours.instance(j));
			for(IClassifierCompetence cc : classifiersCompetence.getClassifiersCompetence()){
				Integer numHits = hitMap.get(cc.getClassifier());
				if(numHits == null)
					numHits = 1;
				else
					numHits++;
				hitMap.put(cc.getClassifier(), numHits);
			}
		}
		
		double bestAccuracy = -1;
		for (Map.Entry<DynseClassifier<DynseClassifierPruningMetrics>, Integer> entry : hitMap.entrySet())
		{
		    if(entry.getValue() > bestAccuracy){
		    	selectedClassifiers.clear();
		    	selectedClassifiers.add(entry.getKey());
		    	bestAccuracy = entry.getValue(); 
		    }else{
		    	if(entry.getValue() == bestAccuracy)
		    		selectedClassifiers.add(entry.getKey());
		    }
		}
		
		double[] result;
		if(selectedClassifiers.size() > 0){
			classifiersUsedInLastClassification = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>(selectedClassifiers);
			result = classifierCombiner.distributionForInstance(instance, selectedClassifiers);
			this.decreaseFactorFromNotSelectedClassifiers(availableClassifiers, selectedClassifiers);
		}else{
			classifiersUsedInLastClassification = availableClassifiers;
			result = classifierCombiner.distributionForInstance(instance, selectedClassifiers);
			this.decreaseClassifiersFactor(availableClassifiers);
		}
		return result;
	}
	
	private void decreaseFactorFromNotSelectedClassifiers(List<DynseClassifier<DynseClassifierPruningMetrics>> pool,
			List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers){
		Set<DynseClassifier<DynseClassifierPruningMetrics>> selectedSet = 
				new HashSet<DynseClassifier<DynseClassifierPruningMetrics>>(selectedClassifiers);
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : pool){
			if(!selectedSet.contains(dc))
				dc.getDynseClassifierMetrics().setUseageFactor(dc.getDynseClassifierMetrics().getUseageFactor()-1.0);
		}
	}
	
	private void decreaseClassifiersFactor(List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : classifiers)
			dc.getDynseClassifierMetrics().decreaseUseageFactor();
	}
	
	private void increaseClassifiersFactor(List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : classifiers)
			dc.getDynseClassifierMetrics().increaseUseageFactor();
	}

	@Override
	public NearestNeighbourSearch createNeighborSearchMethod() {
		return new LinearNNSearch();
	}
	
	@Override
	public boolean getMapOnlyCorrectClassifiers() {
		return true;
	}
	
	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> getClassifiersUsedInLastClassification(){
		return classifiersUsedInLastClassification;
	}
	
	@Override
	public void getClassificationEngineDescription(StringBuilder out) {
		out.append("OLA\n");
		out.append("Neighbors: ");
		out.append(kNeighbors);
		out.append("\n");
		out.append("Combination rule: ");
		out.append("Combination rule: Majority Voting");
	}
	
	@Override
	public void getClassificationEngineShortDescription(StringBuilder out) {
		out.append("OLA" + kNeighbors);
	}

	@Override
	public void reset() {
		this.classifiersUsedInLastClassification = null;
	}
}