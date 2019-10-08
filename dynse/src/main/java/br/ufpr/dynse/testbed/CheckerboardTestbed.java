/*    
*    CheckerboardTestbed.java 
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
		//PATH_CHECKERBOARD = "PATH_HERE/CBconstant.arff";		
		//PATH_CHECKERBOARD = "PATH_HERE/CBpulse.arff";
		PATH_CHECKERBOARD = "PATH_HERE/CBsinusoidal.arff";
	}
	
	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();

	@Override
	public void executeTests(int numExecutions) throws Exception{
		//this.executeTestsDynseKE(numExecutions);
		//this.executeTestsLCA(numExecutions);
		//this.executeTestsOLA(numExecutions);
		//this.executeTestsAPriori(numExecutions);
		//this.executeTestsAPosteriori(numExecutions);
		//this.executeTestsKU(numExecutions);
		//this.executeTestsKUW(numExecutions);
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
	
	public void executeTestsKUW(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse OLA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKUW(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
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
	
	public void executeTestsKU(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse KU - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseKU(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
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
	
	public void executeTestsAPriori(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse OLA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPriori(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
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
	
	public void executeTestsAPosteriori(int numExecutions) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExecutions);
		
		for(int i =0;i < numExecutions; i++){
			System.out.println("Running StreamDynse OLA - Exec.: " + i);
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePeriodicHeldOutTestUFPR evaluator = new EvaluatePeriodicHeldOutTestUFPR();
			
			StreamDynse streamKnoraDriftHandler = dynseFactory.createDefaultDynseAPosteriori(Constants.NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD);
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