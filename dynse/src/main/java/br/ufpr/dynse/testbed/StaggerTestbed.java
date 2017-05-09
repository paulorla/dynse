package br.ufpr.dynse.testbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.ufpr.dynse.classifier.factory.AbstractDynseFactory;
import br.ufpr.dynse.classifier.factory.RealConceptDriftDynseFactory;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluatePeriodicHeldOutTestUFPR;
import br.ufpr.dynse.generator.StaggerDriftGenerator;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.classifiers.meta.LeveragingBag;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class StaggerTestbed implements MultipleExecutionsTestbed{
	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();
	
	private Random random = new Random();
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();

	@Override
	public void executeTests(int numExec) throws Exception{
		//this.executeTestsLeveragingBag(numExec);
		//this.executeTestsDynseKE(numExec);
		//this.executeTestsDynseLCA(numExec);
		this.executeTestsDynseOLA(numExec);
	}
	
	public void executeTestsLeveragingBag(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			LeveragingBag leveragingBag = new LeveragingBag();
			leveragingBag.baseLearnerOption.setValueViaCLIString("trees.HoeffdingTree");
			evaluator.learnerOption.setCurrentObject(leveragingBag);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(9000);
			evaluator.testSizeOption.setValue(200);
			evaluator.sampleFrequencyOption.setValue(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsDynseKE(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse KE - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKE(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue((StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER + 
					StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER)*10*4);
			evaluator.testSizeOption.setValue(StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER);
			evaluator.sampleFrequencyOption.setValue(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsDynseLCA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse LCA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseLCA(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue((StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER + 
					StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER)*10*4);
			evaluator.testSizeOption.setValue(StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER);
			evaluator.sampleFrequencyOption.setValue(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsDynseOLA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse OLA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseOLA(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue((StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER + 
					StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER)*10*4);
			evaluator.testSizeOption.setValue(StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER);
			evaluator.sampleFrequencyOption.setValue(StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
}