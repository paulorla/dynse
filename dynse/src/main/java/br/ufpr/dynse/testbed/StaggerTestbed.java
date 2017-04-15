package br.ufpr.dynse.testbed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.ufpr.dynse.classificationengine.IClassificationEngine;
import br.ufpr.dynse.classificationengine.KnoraEliminateClassificationEngine;
import br.ufpr.dynse.classificationengine.LCAClassificationEngine;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.classifier.factory.AbstractClassifierFactory;
import br.ufpr.dynse.classifier.factory.HoeffdingTreeFactory;
import br.ufpr.dynse.constant.Constants;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluatePeriodicHeldOutTestUFPR;
import br.ufpr.dynse.generator.StaggerDriftGenerator;
import br.ufpr.dynse.pruningengine.AccuracyBasedPruningEngine;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import br.ufpr.dynse.pruningengine.IPruningEngine;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.classifiers.meta.LeveragingBag;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class StaggerTestbed implements MultipleExecutionsTestbed{
	
	private static final int NEIGHBORS_LCA = 5;
	private static final int NEIGHBORS_ELIMINATE = 9;
	private static final int SLACK_VARIABLE_ELIMINATE = 2;
	private static final int ACCURACY_WINDOW_SIZE = 4;
	
	private final AbstractClassifierFactory classifierFactory = new HoeffdingTreeFactory();
	private IPruningEngine<DynseClassifierPruningMetrics> pruningEngine;
	
	private Random random = new Random();
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();
	
	public StaggerTestbed() throws Exception{
		pruningEngine = new AccuracyBasedPruningEngine(25);
	}

	@Override
	public void executeTests(int numeroExecucoes) throws Exception{
		//this.executeTestsLeveragingBag(numeroExecucoes);
		//this.executeTestsDynseKE(numeroExecucoes);
		this.executeTestsDynseLCA(numeroExecucoes);
	}
	
	public void executeTestsLeveragingBag(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		
		for(int i =0;i < numeroExecucoes; i++){
			System.out.println("Executing " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			LeveragingBag leveragingBag = new LeveragingBag();
			leveragingBag.baseLearnerOption.setValueViaCLIString("trees.HoeffdingTree");
			evaluator.learnerOption.setCurrentObject(leveragingBag);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(9000);
			evaluator.testSizeOption.setValue(200);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsDynseKE(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		
		for(int i =0;i < numeroExecucoes; i++){
			System.out.println("Executing StreamDynse KE" + NEIGHBORS_ELIMINATE + "" + SLACK_VARIABLE_ELIMINATE + " - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new KnoraEliminateClassificationEngine(NEIGHBORS_ELIMINATE, SLACK_VARIABLE_ELIMINATE);
			StreamDynse streamKnoraDriftHandler = new StreamDynse(classifierFactory,
					StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER, ACCURACY_WINDOW_SIZE, classificationEngine, pruningEngine);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue((StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER + 
					StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER)*10*4);
			evaluator.testSizeOption.setValue(StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
	
	public void executeTestsDynseLCA(int numeroExecucoes) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numeroExecucoes);
		
		for(int i =0;i < numeroExecucoes; i++){
			System.out.println("Executing StreamDynse LCA" + NEIGHBORS_LCA + " - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new LCAClassificationEngine(NEIGHBORS_LCA);
			StreamDynse streamKnoraDriftHandler = new StreamDynse(classifierFactory,
					StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER, ACCURACY_WINDOW_SIZE, classificationEngine, pruningEngine);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new StaggerDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue((StaggerDriftGenerator.NUM_INST_TRAIN_CLASSIFIER_STAGGER + 
					StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER)*10*4);
			evaluator.testSizeOption.setValue(StaggerDriftGenerator.NUM_INST_TEST_CLASSIFIER_STAGGER);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_CLASSIFIER_STAGGER);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve resultadoMedio = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(resultadoMedio));
	}
}