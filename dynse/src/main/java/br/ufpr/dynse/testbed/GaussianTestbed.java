package br.ufpr.dynse.testbed;

import java.util.ArrayList;
import java.util.List;

import br.ufpr.dynse.classifier.factory.AbstractDynseFactory;
import br.ufpr.dynse.classifier.factory.RealConceptDriftDynseFactory;
import br.ufpr.dynse.constant.Constants;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.dataset.converter.GaussianElwellPolikarConverter;
import br.ufpr.dynse.evaluation.GaussianTestUFPR;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.streams.ArffFileStream;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class GaussianTestbed implements MultipleExecutionsTestbed{
	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();
	
	private final UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();
	
	private static final String PATH_TRAIN_FILE = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/gaussianDataPolikar/gaussianTrain.arff";
	private static final String PATH_TEST_FILE = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/gaussianDataPolikar/gaussianTest.arff";
	private static final String PATH_PRIORS = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/gaussianDataPolikar/Gaussian_testing_priors.csv";
	

	public void executeTests(int numberExec) throws Exception{
		//this.executeTestsKnoraEliminate(numberExec);
		//this.executeTestsLCA(numberExec);
		this.executeTestsOLA(numberExec);
		//this.executeTestsAPriori(numExec);
		//this.executeTestsAPosteriori(numberExec);
		//this.executeTestsKU(numberExec);
		//this.executeTestsKU(numberExec);
	}
	
	public void executeTestsKnoraEliminate(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKE(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsLCA(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseLCA(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsOLA(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseOLA(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKU(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKU(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKUW(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKUW(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
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
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPriori(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
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
			GaussianTestUFPR evaluator = new GaussianTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPosteriori(Constants.NUM_INST_TRAIN_GAUSS_POLIKAR); 
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream trainStream = new ArffFileStream();
			trainStream.arffFileOption.setValue(PATH_TRAIN_FILE);
			trainStream.prepareForUse();
			
			ArffFileStream testStream = new ArffFileStream();
			testStream.arffFileOption.setValue(PATH_TEST_FILE);
			testStream.prepareForUse();
			
			List<Double[]> priors = GaussianElwellPolikarConverter.readPriorsTest(PATH_PRIORS);
			
			evaluator.setTrainStream(trainStream);
			evaluator.setTestStream(testStream);
			evaluator.setPriors(priors);
			
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
}