package br.ufpr.dynse.classifier.factory;

import moa.classifiers.Classifier;
import moa.classifiers.bayes.NaiveBayes;

public class NaiveBayesFactory extends AbstractClassifierFactory {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public Classifier createClassifier() throws Exception {
		NaiveBayes classifier = new NaiveBayes();
		classifier.prepareForUse();
		return classifier;
	}
	
	@Override
	public void getDescription(StringBuilder out) {
		out.append("Naive Bayes Factory");
	}
	
	@Override
	public void getShortDescription(StringBuilder out) {
		out.append("NB");
	}
}
