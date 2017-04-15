package br.ufpr.dynse.testbed;

import java.util.ArrayList;
import java.util.List;

import br.ufpr.dynse.classifier.factory.AbstractDynseFactory;
import br.ufpr.dynse.classifier.factory.VirtualConceptDriftDynseFactory;
import br.ufpr.dynse.constant.Constants;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluatePeriodicHeldOutTestUFPR;
import br.ufpr.dynse.generator.PxDriftGenerator;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.classifiers.meta.AccuracyUpdatedEnsemble;
import moa.classifiers.meta.LeveragingBag;
import moa.classifiers.meta.OzaBagAdwin;

public class NistTestbed implements MultipleExecutionsTestbed{
	
	private static final String PATH_DATASET = "/home/paulo/Projetos/experimentos-doutorado/DriftGeneratorsAndData/nist/nist.arff";
	
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();
	private final AbstractDynseFactory dynseFactory = new VirtualConceptDriftDynseFactory();
	
	public void executeTests(int numeroExecucoes) throws Exception{
		this.executeTestsKnoraEliminate(numeroExecucoes);
		//this.executeTestsLeveragingBag(numeroExecucoes);
		//this.executeTestsAccuracyUpdatedEnsemble(numeroExecucoes);
		//this.executeTestsOzaAdwin(numeroExecucoes);
	}
	
	public void executeTestsKnoraEliminate(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		for(int i =0;i < numeroExecucoes; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKE(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			StringBuilder builder = new StringBuilder();
			streamKnoraDriftHandler.getShortDescription(builder, 0);
			System.out.println("Executing " + i + ": "+ builder);
			
			PxDriftGenerator pxDriftGenerator = new PxDriftGenerator(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST, 
					Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST, PATH_DATASET);
			
			evaluator.streamOption.setCurrentObject(pxDriftGenerator);
			evaluator.trainSizeOption.setValue(0);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask();
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsLeveragingBag(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		for(int i =0;i < numeroExecucoes; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			LeveragingBag levBag = new LeveragingBag();
			evaluator.learnerOption.setCurrentObject(levBag);
			
			System.out.println("Executing " + i);
			
			PxDriftGenerator pxDriftGenerator = new PxDriftGenerator(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST, 
					Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST, PATH_DATASET);
			
			evaluator.streamOption.setCurrentObject(pxDriftGenerator);
			evaluator.trainSizeOption.setValue(0);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask();
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsAccuracyUpdatedEnsemble(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		for(int i =0;i < numeroExecucoes; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			AccuracyUpdatedEnsemble accuracyUpdatedEnsemble = new AccuracyUpdatedEnsemble();
			accuracyUpdatedEnsemble.chunkSizeOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
			evaluator.learnerOption.setCurrentObject(accuracyUpdatedEnsemble);
			
			System.out.println("Executing " + i);
			
			PxDriftGenerator pxDriftGenerator = new PxDriftGenerator(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST, 
					Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST, PATH_DATASET);
			
			evaluator.streamOption.setCurrentObject(pxDriftGenerator);
			evaluator.trainSizeOption.setValue(0);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask();
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsOzaAdwin(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		for(int i =0;i < numeroExecucoes; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			OzaBagAdwin ozaBagAdwin = new OzaBagAdwin();
			ozaBagAdwin.baseLearnerOption.setValueViaCLIString("trees.HoeffdingTree");
			
			evaluator.learnerOption.setCurrentObject(ozaBagAdwin);
			
			System.out.println("Executing " + i);
			
			PxDriftGenerator pxDriftGenerator = new PxDriftGenerator(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST, 
					Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST, PATH_DATASET);
			
			evaluator.streamOption.setCurrentObject(pxDriftGenerator);
			evaluator.trainSizeOption.setValue(0);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask();
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
}