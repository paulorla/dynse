package br.ufpr.dynse.core;

import java.util.ArrayList;
import java.util.List;

import moa.core.Measurement;
import moa.evaluation.LearningCurve;
import moa.evaluation.LearningEvaluation;

public class UFPRLearningCurve extends LearningCurve{
	
	private static final long serialVersionUID = 1L;
	
	protected List<Measurement> accuracy;
	protected List<Measurement> numClassifiedInstances;
	protected double totalInstances = 0.0;
	protected double totalHits = 0.0;
	protected Double minimunAccuracyAchieved = null;
	
	public String getNames(){
		return super.measurementNames.toString();
	}
	
	public UFPRLearningCurve(String orderingMeasurementName) {
		super(orderingMeasurementName);
		
		accuracy = new ArrayList<Measurement>();
		numClassifiedInstances = new ArrayList<Measurement>();
	}
	
	public void insertEntry(Measurement accuracy, Measurement numClassifiedInstances, List<Measurement> otherMeasurements){
		Measurement[] medicoes = new Measurement[otherMeasurements.size()];
		medicoes = otherMeasurements.toArray(medicoes);
		
		this.accuracy.add(accuracy);
		this.numClassifiedInstances.add(numClassifiedInstances);
		
		totalInstances += numClassifiedInstances.getValue();
		totalHits += numClassifiedInstances.getValue() * (accuracy.getValue()/100.0);
		
		if(minimunAccuracyAchieved == null || accuracy.getValue() < minimunAccuracyAchieved)
			minimunAccuracyAchieved = accuracy.getValue();
		
		super.insertEntry(new LearningEvaluation(medicoes));
	}
	
	public void insertEntry(Measurement accuracy, Measurement numClassifiedInstances, Measurement[] otherMeasurements){
		this.accuracy.add(accuracy);
		this.numClassifiedInstances.add(numClassifiedInstances);
		
		totalInstances += numClassifiedInstances.getValue();
		totalHits += numClassifiedInstances.getValue() * (accuracy.getValue()/100.0);
		
		if(minimunAccuracyAchieved == null || accuracy.getValue() < minimunAccuracyAchieved)
			minimunAccuracyAchieved = accuracy.getValue();
		
		super.insertEntry(new LearningEvaluation(otherMeasurements));
	}

	public double getAccuracy(int index) {
		return accuracy.get(index).getValue();
	}

	public double getNumClassifierInstances(int index) {
		return numClassifiedInstances.get(index).getValue();
	}
	
	public int getNumMeasurements(){
		return super.measurementNames.size();
	}
	
	public int getNumMeasurements(int row){
		return super.measurementValues.get(row).length;
	}

	public List<Measurement> getNumClassifiedInstances() {
		return numClassifiedInstances;
	}

	public double getTotalInstances() {
		return totalInstances;
	}
	
	public Double getMinimunAccuracyAchieved() {
		return minimunAccuracyAchieved;
	}

	public double getAverageAccuracy(){
		return (totalHits/totalInstances)*100;
	}
}