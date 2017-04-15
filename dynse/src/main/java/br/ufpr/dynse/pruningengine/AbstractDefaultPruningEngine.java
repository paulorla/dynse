package br.ufpr.dynse.pruningengine;

import br.ufpr.dynse.classifier.DynseClassifier;

public abstract class AbstractDefaultPruningEngine implements IPruningEngine<DynseClassifierPruningMetrics>{
	
	@Override
	public void meassureClassifier(DynseClassifier<DynseClassifierPruningMetrics> classifier)  throws Exception{
		DynseClassifierPruningMetrics metrics = new DynseClassifierPruningMetrics(classifier);
		classifier.setDynseClassifierMetrics(metrics);
	}
}