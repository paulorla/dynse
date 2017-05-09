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

public class CheckerboardTestbed implements MultipleExecutionsTestbed{
	private static final String PATH_CHECKERBOARD;
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();
	
	static{
		//PATH_CHECKERBOARD = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/checkerboard_data/CBconstant.arff";		
		//PATH_CHECKERBOARD = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/checkerboard_data/CBpulse.arff";
		PATH_CHECKERBOARD = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/checkerboard_data/CBsinusoidal.arff";
	}
	
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();

	@Override
	public void executeTests(int numExecutions) throws Exception{
		//this.executeTestsDynseKE(numExecutions);
		//this.executeTestsLCA(numExecutions);
		this.executeTestsOLA(numExecutions);
	}
	
	public void executeTestsDynseKE(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse KE - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse dynse = dynseFactory.createDefaultDynseKE(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
			evaluator.learnerOption.setCurrentObject(dynse);
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_CHECKERBOARD);
			evaluator.streamOption.setCurrentObject(stream);
			
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_CHECKERBOARD);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsLCA(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse LCA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseLCA(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_CHECKERBOARD);
			evaluator.streamOption.setCurrentObject(stream);
			
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_CHECKERBOARD);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsOLA(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse OLA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseOLA(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			ArffFileStream stream = new ArffFileStream();
			stream.arffFileOption.setValue(PATH_CHECKERBOARD);
			evaluator.streamOption.setCurrentObject(stream);
			
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_CHECKERBOARD);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
}