package br.ufpr.dynse.pruningengine;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;

public class AgeBasedPruningEngine extends AbstractDefaultPruningEngine{
	
	private int maxPoolSize;

	public AgeBasedPruningEngine(int maxPoolSize) throws Exception{
		this.maxPoolSize = maxPoolSize;
		if(maxPoolSize < 1)
			throw new Exception("The max pool size must be greater than 0.");
	}

	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> pruneClassifiers(DynseClassifier<DynseClassifierPruningMetrics> newClassifier,
			List<DynseClassifier<DynseClassifierPruningMetrics>> currentPool, List<Instance> accuracyEstimationInstances) {
		if(currentPool.size() + 1 <= maxPoolSize)//sum 1, since a new classifier (newClassifier) will be added in the pool
			return new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
		int numClassifiers = currentPool.size() + 1 - maxPoolSize;
		List<DynseClassifier<DynseClassifierPruningMetrics>> classifiesToPrune = 
				new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>(numClassifiers);
		SortedSet<Long> agesSet = new TreeSet<Long>();
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : currentPool)
			agesSet.add(dc.getDynseClassifierMetrics().getCreationTime());
		
		Long prunningAge = -1L;
		int agesChecked = 0;
		for(Long age : agesSet){
			prunningAge = age;
			agesChecked++;
			if(agesChecked == numClassifiers)
				break;
		}
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : currentPool){
			if(dc.getDynseClassifierMetrics().getCreationTime() <= prunningAge){
				classifiesToPrune.add(dc);
				if(classifiesToPrune.size() == numClassifiers)
					break;
			}
		}
		return classifiesToPrune;
	}
	
	@Override
	public void getPrunningEngineDescription(StringBuilder out) {
		out.append("Age Based Prunning Engine\n");
		out.append("Max Pool Size: ");
		out.append(maxPoolSize);
		out.append("\n");
	}
	
	@Override
	public void getPrunningEngineShortDescription(StringBuilder out) {
		out.append("AgePrun" + maxPoolSize);
	}
}