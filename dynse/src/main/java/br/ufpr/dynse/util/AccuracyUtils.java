package br.ufpr.dynse.util;

import java.util.Collection;

import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.Classifier;
import moa.core.Utils;

public class AccuracyUtils {
	
	public static Double computeAccuracy(Classifier classifier, Collection<Instance> instances) throws Exception{
		int correct = 0;
		for(Instance currentInstance : instances){
			double[] distribution = classifier.getVotesForInstance(currentInstance);
			double classGivenForInstance = Utils.maxIndex(distribution);
			
			if(classGivenForInstance == currentInstance.classValue())
				correct++;
		}
		return ((double)correct)/(double)instances.size();
	}
}