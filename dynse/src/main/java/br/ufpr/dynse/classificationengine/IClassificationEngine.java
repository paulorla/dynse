package br.ufpr.dynse.classificationengine;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;

public interface IClassificationEngine<U extends IMultipleClassifiersCompetence> extends Serializable{

	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, U> competenceMappings, NearestNeighbourSearch nnSearch) throws Exception;
	
	public NearestNeighbourSearch createNeighborSearchMethod();
	
	public boolean getMapOnlyCorrectClassifiers();
	
	public List<DynseClassifier<DynseClassifierPruningMetrics>> getClassifiersUsedInLastClassification();
	
	public void getClassificationEngineDescription(StringBuilder out);
	
	public void getClassificationEngineShortDescription(StringBuilder out);
	
	public void reset();
}