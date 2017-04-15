package br.ufpr.dynse.classifier;

import java.util.HashSet;
import java.util.Set;

import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Measurement;

public class DynseClassifier<T extends DynseClassifierPruningMetrics> extends AbstractClassifier{
	
	private static final long serialVersionUID = 1L;

	private Classifier classifier;

	private T dynseClassifierMetrics;
	private Set<Concept<?>> trainingConcepts;

	public DynseClassifier(Classifier classifier) {
		this(classifier, new HashSet<Concept<?>>());
	}
	
	public DynseClassifier(Classifier classifier, Set<Concept<?>> trainingConcepts) {
		super();
		this.classifier = classifier;
		this.trainingConcepts = trainingConcepts;
	}

	public Set<Concept<?>> getTrainingConcepts() {
		return trainingConcepts;
	}

	public void setDynseClassifierMetrics(T dynseClassifierMetrics) {
		this.dynseClassifierMetrics = dynseClassifierMetrics;
	}

	public boolean isRandomizable() {
		return false;
	}

	@Override
	public void getModelDescription(StringBuilder out, int arg1) {
		out.append("Dynse Single Classifier using a " + classifier.getClass());
	}

	@Override
	protected Measurement[] getModelMeasurementsImpl() {
		return classifier.getModelMeasurements();
	}

	@Override
	public double[] getVotesForInstance(com.yahoo.labs.samoa.instances.Instance arg0) {
		return classifier.getVotesForInstance(arg0);
	}

	@Override
	public void resetLearningImpl() {
		classifier.resetLearning();
	}

	@Override
	public void trainOnInstanceImpl(com.yahoo.labs.samoa.instances.Instance arg0) {
		classifier.trainOnInstance(arg0);
	}

	public T getDynseClassifierMetrics() {
		return dynseClassifierMetrics;
	}
}