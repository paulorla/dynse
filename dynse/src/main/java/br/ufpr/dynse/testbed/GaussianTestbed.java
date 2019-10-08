/*    
*    GaussianTestbed.java 
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
import br.ufpr.dynse.dataset.converter.GaussianElwellPolikarConverter;
import br.ufpr.dynse.evaluation.GaussianTestUFPR;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.streams.ArffFileStream;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class GaussianTestbed implements MultipleExecutionsTestbed{
	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();
	
	private final UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();
	
	private static final String PATH_TRAIN_FILE = "PATH_TRAIN_HERE/gaussianTrain.arff";
	private static final String PATH_TEST_FILE = "PATH_TEST_HERE/gaussianTest.arff";
	private static final String PATH_PRIORS = "PATH_PRIORS_HERE/Gaussian_testing_priors.csv";
	

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