/*    
*    NebraskaWeatherTestBed.java 
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
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.evaluation.EvaluateInterleavedChunksUFPR;
import br.ufpr.dynse.evaluation.EvaluatePrequentialSingleTrainPKLot;
import br.ufpr.dynse.evaluation.EvaluateSingleTrain;
import br.ufpr.dynse.generator.StaggerDriftGenerator;
import br.ufpr.dynse.util.PKLotStreamer;
import br.ufpr.dynse.util.UFPRLearningCurveUtils;
import moa.classifiers.meta.LeveragingBag;
import moa.classifiers.trees.HoeffdingTree;
import moa.streams.ArffFileStream;
import moa.tasks.StandardTaskMonitor;
import moa.tasks.TaskMonitor;

public class PKLotTestbed implements MultipleExecutionsTestbed{
	
	private static final int NUM_SAMPLES_EACH_BATCH = 30;
	private static final int NUM_SAMPLES_TRAIN_CLASSIFIER = NUM_SAMPLES_EACH_BATCH*3;//accumulate 3 bathes (V=3)
	
	private static final String PATH_DATASET = "/home/granza/Granza/PIC/PKLot-ConceptDrift";

	
	private final AbstractDynseFactory dynseFactory = new RealConceptDriftDynseFactory();

	private UFPRLearningCurveUtils ufprLearningCurveUtils = new UFPRLearningCurveUtils();	
	
	@Override
	public void executeTests(int numExec) throws Exception{
	
		this.executeTestsSingleTrainPrequential(numExec);
		this.executeTestsSingleTrainHeldOut(numExec);

	}

	
	public void executeTestsSingleTrainPrequential(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		System.out.println("Testes prequenciais da PKLot");
		for(int i =0;i < numExec; i++){
			System.out.println("Teste "+(i+1)+":");
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluatePrequentialSingleTrainPKLot evaluator = new EvaluatePrequentialSingleTrainPKLot();
			
			ArffFileStream stream = new ArffFileStream();
			PKLotStreamer file = new PKLotStreamer();
			stream.arffFileOption.setValue(file.prepareStream(PATH_DATASET));

			evaluator.learnerOption.setCurrentObject(new HoeffdingTree());
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.trainSizeOption.setValue((int)(300));
			evaluator.sampleFrequencyOption.setValue(100);
			evaluator.widthOption.setValue(1000);
			evaluator.dumpFileOption.setValue("/home/granza/Área de Trabalho/teste.csv");
			evaluator.prepareForUse();
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	public void executeTestsSingleTrainHeldOut(int numExec) throws Exception{
		List<UFPRLearningCurve> learningCurves = new ArrayList<UFPRLearningCurve>(numExec);
		System.out.println("Testes HeldOut da PKLot");
		for(int i =0;i < numExec; i++){
			System.out.println("Teste "+(i+1)+":");
			TaskMonitor monitor = new StandardTaskMonitor();
			EvaluateSingleTrain evaluator = new EvaluateSingleTrain();
			
			ArffFileStream stream = new ArffFileStream();
			PKLotStreamer file = new PKLotStreamer();
			stream.arffFileOption.setValue(file.prepareStream(PATH_DATASET));
			
			evaluator.learnerOption.setCurrentObject(new HoeffdingTree());
			
			evaluator.streamOption.setCurrentObject(stream);
			evaluator.trainSizeOption.setValue((int)(300));
			evaluator.testSizeOption.setValue(0);
			evaluator.sampleFrequencyOption.setValue(100);
			evaluator.dumpFileOption.setValue("/home/granza/Área de Trabalho/teste.csv");
			evaluator.prepareForUse();
			UFPRLearningCurve lc = (UFPRLearningCurve)evaluator.doTask(monitor, null);
			learningCurves.add(lc);
		}
		UFPRLearningCurve avgResult = ufprLearningCurveUtils.averageResults(learningCurves);
		
		System.out.println(ufprLearningCurveUtils.strMainStatisticsMatlab(avgResult));
	}
	
	
}
