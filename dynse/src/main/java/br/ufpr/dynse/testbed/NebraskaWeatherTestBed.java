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
	
	private static final String PATH_DATASET = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/NebraskaWeather/nebraskaWeather.arff";
	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();

	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();	
	
	@Override
	public void executeTests(int numExec) throws Exception{
		//this.executeTestsLCA(numExec);
		//this.executeTestsKE(numExec);
		//this.executeTestsLeveragingBag(numExec);
		//this.executeTestsOLA(numExec);
		//this.executeTestsAPriori(numExec);
		this.executeTestsAPosteriori(numExec);
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
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
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
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
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
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
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
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
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
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
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
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
}