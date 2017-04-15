package br.ufpr.dynse.pruningengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.util.AccuracyUtils;

public class AccuracyBasedPruningEngine extends AbstractDefaultPruningEngine{
	private int maxPoolSize;

	public AccuracyBasedPruningEngine(int maxPoolSize) throws Exception{
		this.maxPoolSize = maxPoolSize;
		if(maxPoolSize < 1)
			throw new Exception("The max pool size must be greater than 0.");
	}

	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> pruneClassifiers(DynseClassifier<DynseClassifierPruningMetrics> newClassifier,
			List<DynseClassifier<DynseClassifierPruningMetrics>> currentPool,
			List<Instance> accuracyEstimationInstances) throws Exception{
		if(currentPool.size() + 1 <= maxPoolSize)//sum 1, since a new classifier (newClassifier) will be added in the pool
			return new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
		int numClassifiers = currentPool.size() + 1 - maxPoolSize;
		
		SortedSet<Double> accuraciesSet = new TreeSet<Double>();
		Map<Double, List<DynseClassifier<DynseClassifierPruningMetrics>>> accuraciesMap = 
				new HashMap<Double, List<DynseClassifier<DynseClassifierPruningMetrics>>>();
		
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : currentPool){
			Double accuracy = AccuracyUtils.computeAccuracy(dc, accuracyEstimationInstances);
			accuraciesSet.add(accuracy);
			List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers = accuraciesMap.get(accuracy);
			if(classifiers == null){//there is already one or more classifiers with the same accuracy
				classifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
				accuraciesMap.put(accuracy, classifiers);
				
			}
			classifiers.add(dc); 
		}
		Double accuracyNewClassifier = AccuracyUtils.computeAccuracy(newClassifier, accuracyEstimationInstances);
		accuraciesSet.add(accuracyNewClassifier);
		List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers = accuraciesMap.get(accuracyNewClassifier);
		if(classifiers == null){//there is already one or more classifiers with the same accuracy
			classifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
			accuraciesMap.put(accuracyNewClassifier, classifiers);
		}
		
		List<DynseClassifier<DynseClassifierPruningMetrics>> classifiesToPrune = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>(numClassifiers);
		classifiers.add(newClassifier); 
		int classifiersRemoved = 0;
		for(Double accuracy : accuraciesSet){
			List<DynseClassifier<DynseClassifierPruningMetrics>> mapedClassifiers = accuraciesMap.get(accuracy);
			for(DynseClassifier<DynseClassifierPruningMetrics> dc : mapedClassifiers){
				classifiesToPrune.add(dc);
				classifiersRemoved++;
				if(classifiersRemoved == numClassifiers)
					return classifiesToPrune;
			}
		}
		
		return classifiesToPrune;
	}

	@Override
	public void getPrunningEngineDescription(StringBuilder out) {
		out.append("Accuracy Based Prunning Engine\n");
		out.append("Max Pool Size: ");
		out.append(maxPoolSize);
		out.append("\n");
	}
	
	@Override
	public void getPrunningEngineShortDescription(StringBuilder out) {
		out.append("AccPrun" + maxPoolSize);
	}
}