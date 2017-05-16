package br.ufpr.dynse.testbed;

import java.util.ArrayList;
import java.util.List;

import br.ufpr.dynse.classifier.factory.AbstractDynseFactory;
import br.ufpr.dynse.classifier.factory.RealConceptDriftDynseFactory;
import br.ufpr.dynse.constant.Constants;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluatePeriodicHeldOutTestUFPR;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.streams.ArffFileStream;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class ForestCoverTypeTestBed implements MultipleExecutionsTestbed{

	private static final String PATH_DATASET = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/Forest/covtypeNorm_MOA.arff";
	
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();
	
	@Override
	public void executeTests(int numExec) throws Exception{
		//this.executeTestsKE(numExec);
		//this.executeTestsLCA(numExec);
		this.executeTestsOLA(numExec);
		//this.executeTestsAPriori(numExec);
		//this.executeTestsAPosteriori(numExec);
		//this.executeTestsKU(numExec);
		//this.executeTestsKUW(numExec);
	}
	
	public void executeTestsKE(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKE(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKU(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKU(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKUW(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKUW(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsLCA(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseLCA(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsOLA(int numberExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numberExecutions);
		for(int i =0;i < numberExecutions; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseOLA(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsAPriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPriori(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsAPosteriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_DATASET);
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPosteriori(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Running " + i + " " + builder);
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_FOREST);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_FOREST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(lc));
			learningCurves.add(lc);
		}
		
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
}