package br.ufpr.dynse.classifier.factory;

import java.io.Serializable;
import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import moa.classifiers.Classifier;
import moa.core.InstanceExample;

public abstract class AbstractClassifierFactory implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public abstract Classifier createClassifier() throws Exception;
	
	public Classifier buildClassifier(Instances trainBatch) throws Exception {
		Classifier classifier = this.createClassifier();
		for(int i = 0; i < trainBatch.size(); i++){
			classifier.trainOnInstance(trainBatch.get(i));
		}
		
		return classifier;
	}
	
	public Classifier buildClassifier(List<Instance> trainBatch) throws Exception {
		Classifier classifier = this.createClassifier();
		for(int i = 0; i < trainBatch.size(); i++){
			classifier.trainOnInstance(trainBatch.get(i));
		}
		
		return classifier;
	}
	
	public Classifier buildClassifierInstanceExamples(List<InstanceExample> trainBatch) throws Exception {
		Classifier classifier = this.createClassifier();
		for(int i = 0; i < trainBatch.size(); i++){
			classifier.trainOnInstance(trainBatch.get(i));
		}
		
		return classifier;
	}
	
	public abstract void getDescription(StringBuilder out);
	
	public abstract void getShortDescription(StringBuilder out);
}