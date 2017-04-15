package br.ufpr.dynse.classifier.factory;

import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;

public class HoeffdingTreeFactory extends AbstractClassifierFactory {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Classifier createClassifier() throws Exception {
		HoeffdingTree classifier = new HoeffdingTree();
		classifier.prepareForUse();
		return classifier;
	}

	@Override
	public void getDescription(StringBuilder out) {
		out.append("Hoeffding Tree Factory");
	}
	
	@Override
	public void getShortDescription(StringBuilder out) {
		out.append("HoeffTree");
	}
}