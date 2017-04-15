package br.ufpr.dynse.pruningengine;

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;

public class NoPrunePruningEngine extends AbstractDefaultPruningEngine{
	
	private final List<DynseClassifier<DynseClassifierPruningMetrics>> defaultReturn;
	
	public NoPrunePruningEngine() {
		defaultReturn = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
	}
	
	@Override
	public void getPrunningEngineDescription(StringBuilder out) {
		out.append("No Prunning\n");
	}
	
	@Override
	public void getPrunningEngineShortDescription(StringBuilder out) {
		out.append("NoPrun");
	}

	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> pruneClassifiers(DynseClassifier<DynseClassifierPruningMetrics> newClassifier,
			List<DynseClassifier<DynseClassifierPruningMetrics>> currentPool, List<Instance> accuracyEstimationInstances)
			throws Exception {
		return defaultReturn;
	}
}