package br.ufpr.dynse.pruningengine;

import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;

public interface IPruningEngine<T extends DynseClassifierPruningMetrics> {
	//returns the classifiers that must be removed from the pool
	public List<DynseClassifier<T>> pruneClassifiers(DynseClassifier<T> newClassifier, List<DynseClassifier<T>> currentPool,
			List<Instance> accuracyEstimationInstances) throws Exception;
	
	public void meassureClassifier(DynseClassifier<T> classifier) throws Exception;
	
	public void getPrunningEngineDescription(StringBuilder out);
	
	public void getPrunningEngineShortDescription(StringBuilder out);
}