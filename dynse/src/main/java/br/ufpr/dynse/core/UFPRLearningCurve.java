/*    
*    UFPRLearningCurve.java 
*    Copyright (C) 2017 Universidade Federal do Paraná, Curitiba, Paraná, Brasil
*    @Author Paulo Ricardo Lisboa de Almeida (prlalmeida@inf.ufpr.br)
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*    
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*    
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package br.ufpr.dynse.core;

import java.util.ArrayList;
import java.util.List;

import moa.core.Measurement;
import moa.evaluation.preview.LearningCurve;
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