/*    
*    SeaConceptsTestbed.java 
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
import java.util.Random;

import br.ufpr.dynse.classifier.factory.AbstractDynseFactory;
import br.ufpr.dynse.classifier.factory.RealConceptDriftDynseFactory;
import br.ufpr.dynse.constant.Constants;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluatePeriodicHeldOutTestUFPR;
import br.ufpr.dynse.generator.SeaDriftGenerator;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.classifiers.meta.LeveragingBag;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class SeaConceptsTestbed implements MultipleExecutionsTestbed{
	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();
	
	private Random random = new Random();
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();

	@Override
	public void executeTests(int numExec) throws Exception{
		//this.executeTestsLeveragingBag(numExec);
		//this.executeTestsDynseKE(numExec);
		//this.executeTestsDynseLCA(numExec);
		this.executeTestsDynseOLA(numExec);
		//this.executeTestsDynseKUW(numExec);
		//this.executeTestsDynseAPriori(numExec);
		//this.executeTestsDynseAPosteriori(numExec);
		//this.executeTestsDynseKU(numExec);
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
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseKE(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse KE - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKE(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseKU(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse KU - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKU(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseKUW(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse KUW - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKUW(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseLCA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse LCA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseLCA(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseOLA(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse OLA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseOLA(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseAPriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse A Priori - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPriori(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	public void executeTestsDynseAPosteriori(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		
		for(int i =0;i < numExec; i++){
			System.out.println("Executing StreamDynse A Posteriori - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPosteriori(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.learnerOption.setCurrentObject(streamKnoraDriftHandler);
			
			evaluator.streamOption.setCurrentObject(new SeaDriftGenerator(random.nextInt()));
			evaluator.trainSizeOption.setValue(50000);
			evaluator.testSizeOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.sampleFrequencyOption.setValue(Constants.NUM_INST_TRAIN_TEST_SEA);
			evaluator.prepareForUse();
			
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
}