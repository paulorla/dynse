/*    
*    NebraskaWeatherTestBed.java 
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
package br.ufpr.dynse.testbed;

import java.util.ArrayList;
import java.util.List;

import br.ufpr.dynse.classifier.factory.AbstractDynseFactory;
import br.ufpr.dynse.classifier.factory.RealConceptDriftDynseFactory;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluateInterleavedChunksUFPR;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.classifiers.meta.LeveragingBag;
import moa.streams.ArffFileStream;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class NebraskaWeatherTestBed implements MultipleExecutionsTestbed{
	
	private static final int NUM_SAMPLES_EACH_BATCH = 30;
	private static final int NUM_SAMPLES_TRAIN_CLASSIFIER = NUM_SAMPLES_EACH_BATCH*3;//accumulate 3 bathes (V=3)
	
	private static final String PATH_DATASET = "PATH_NEBRASKA_HERE/nebraskaWeather.arff";
	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();

	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();	
	
	@Override
	public void executeTests(int numExec) throws Exception{
		//this.executeTestsLCA(numExec);
		//this.executeTestsKE(numExec);
		//this.executeTestsLeveragingBag(numExec);
		this.executeTestsOLA(numExec);
		//this.executeTestsKUW(numExec);
		//this.executeTestsAPriori(numExec);
		//this.executeTestsAPosteriori(numExec);
		//this.executeTestsKU(numExec);
	}
	
	public void executeTestsLCA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseLCA(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsOLA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseOLA(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKUW(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseKUW(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsAPriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseAPriori(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}

	public void executeTestsAPosteriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseAPosteriori(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKU(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseKU(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKE(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse dynse = dynseFactory.createDefaultDynseKE(NUM_SAMPLES_TRAIN_CLASSIFIER);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsLeveragingBag(int numExec) throws Exception{
		System.out.println("Executing Leveraging Bag in the Nebraska Dataset");
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateInterleavedChunksUFPR evaluator = new EvaluateInterleavedChunksUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			LeveragingBag levBag = new LeveragingBag();
			evaluator.learnerOption.setCurrentObject(levBag);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.chunkSizeOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.sampleFrequencyOption.setValue(NUM_SAMPLES_EACH_BATCH);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
}