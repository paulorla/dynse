/*    
*    LettersTestbed.java 
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

public class LettersTestbed implements MultipleExecutionsTestbed{
	
	private static final String PATH_DATASET = "PATH_LETTERS_HERE/letters.arff";
	
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();
	private final AbstractDynseFactory dynseFactory = new VirtualConceptDriftDynseFactory();
	
	public void executeTests(int numExec) throws Exception{
		//this.executeTestsKnoraEliminate(numExec);
		this.executeTestsOLA(numExec);
		//this.executeTestsKUW(numExec);
		//this.executeTestsAPriori(numExec);
		//this.executeTestsAPosteriori(numExec);
		//this.executeTestsLeveragingBag(numExec);
		//this.executeTestsAccuracyUpdatedEnsemble(numExec);
		//this.executeTestsOzaAdwin(numExec);
		//this.executeTestsKU(numExec);
	}
	
	public void executeTestsKnoraEliminate(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsOLA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseOLA(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKU(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKU(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsKUW(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKUW(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsAPriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPriori(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsAPosteriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPosteriori(Constants.NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST);
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsLeveragingBag(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsAccuracyUpdatedEnsemble(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsOzaAdwin(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		for(int i =0;i < numExec; i++){
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
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
}