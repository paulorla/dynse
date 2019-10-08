/*    
*    EvaluateInterleavedChunksUFPR.java 
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
package br.ufpr.dynse.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import br.ufpr.dynse.core.UFPRLearningCurve;
import moa.classifiers.Classifier;
import moa.core.Example;
import moa.core.InstanceExample;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.core.TimingUtils;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.evaluation.preview.LearningCurve;
import moa.streams.InstanceStream;
import moa.tasks.EvaluateInterleavedChunks;
import moa.tasks.TaskMonitor;

public class EvaluateInterleavedChunksUFPR extends EvaluateInterleavedChunks {
	
	private static final long serialVersionUID = 1L;
	
	private static final String STR_MEDICAO_CLASSIFICATIONS_CORRECT = "classifications correct (percent)";
	private static final String STR_MEDICAO_INST_CLASSIFICADAS = "classified instances";

	public Class<?> getTaskResultType() {
		return LearningCurve.class;
	}

	@Override
	protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
		Classifier learner = (Classifier) getPreparedClassOption(this.learnerOption);
		InstanceStream stream = (InstanceStream) getPreparedClassOption(this.streamOption);
		LearningPerformanceEvaluator evaluator = (LearningPerformanceEvaluator) getPreparedClassOption(this.evaluatorOption);
		learner.setModelContext(stream.getHeader());
		int maxInstances = this.instanceLimitOption.getValue();
		int chunkSize = this.chunkSizeOption.getValue();
		long instancesProcessed = 0;
		int maxSeconds = this.timeLimitOption.getValue();
		int secondsElapsed = 0;
		
		monitor.setCurrentActivity("Evaluating learner...", -1.0);
		
		UFPRLearningCurve learningCurve = new UFPRLearningCurve("learning evaluation instances");
		
		File dumpFile = this.dumpFileOption.getFile();
		PrintStream immediateResultStream = null;
		if (dumpFile != null) {
			try {
				if (dumpFile.exists()) {
					immediateResultStream = new PrintStream(
							new FileOutputStream(dumpFile, true), true);
				} else {
					immediateResultStream = new PrintStream(
							new FileOutputStream(dumpFile), true);
				}
			} catch (Exception ex) {
				throw new RuntimeException(
						"Unable to open immediate result file: " + dumpFile, ex);
			}
		}
		boolean firstDump = true;
		boolean firstChunk = true;
		boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
		long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
		long sampleTestTime =0, sampleTrainTime = 0;
		double RAMHours = 0.0;
		
		while (stream.hasMoreInstances()
				&& ((maxInstances < 0) || (instancesProcessed < maxInstances))
				&& ((maxSeconds < 0) || (secondsElapsed < maxSeconds))) {
			
			Instances chunkInstances = new Instances(stream.getHeader(), chunkSize);
			
			while (stream.hasMoreInstances() && chunkInstances.numInstances() < chunkSize) {
				chunkInstances.add(stream.nextInstance().getData());
				if (chunkInstances.numInstances()
						% INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
					if (monitor.taskShouldAbort()) {
						return null;
					}
					
					long estimatedRemainingInstances = stream.estimatedRemainingInstances();
			
					if (maxInstances > 0) {
						long maxRemaining = maxInstances - instancesProcessed;
						if ((estimatedRemainingInstances < 0) || (maxRemaining < estimatedRemainingInstances)) {
							estimatedRemainingInstances = maxRemaining;
						}
					}
					
					monitor.setCurrentActivityFractionComplete((double) instancesProcessed/ (double) (instancesProcessed + estimatedRemainingInstances));
				}
			}
			
			////Testing
			long testStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
			boolean testeExecutado =false;
			if(!firstChunk)
			{
				for (int i=0; i< chunkInstances.numInstances(); i++) {
					Example testInst = new InstanceExample((Instance) chunkInstances.instance(i));
					double[] prediction = learner.getVotesForInstance(testInst);
					evaluator.addResult(testInst, prediction);
			    }
				testeExecutado = true;
			}
			else
			{
				firstChunk = false;
			}
			
			sampleTestTime += TimingUtils.getNanoCPUTimeOfCurrentThread() - testStartTime;
			
			////Training
			long trainStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
			
			for (int i=0; i< chunkInstances.numInstances(); i++) {
				learner.trainOnInstance(chunkInstances.instance(i));
				instancesProcessed++;
		    }
			
			sampleTrainTime += TimingUtils.getNanoCPUTimeOfCurrentThread() - trainStartTime;
			
			////Result output
			if (instancesProcessed % this.sampleFrequencyOption.getValue() == 0 && testeExecutado) {
				double RAMHoursIncrement = learner.measureByteSize() / (1024.0 * 1024.0 * 1024.0); //GBs
                RAMHoursIncrement *= (TimingUtils.nanoTimeToSeconds(sampleTrainTime + sampleTestTime) / 3600.0); //Hours
                RAMHours += RAMHoursIncrement;
				
				double avgTrainTime = TimingUtils.nanoTimeToSeconds(sampleTrainTime)/((double)this.sampleFrequencyOption.getValue()/chunkInstances.numInstances());
				double avgTestTime = TimingUtils.nanoTimeToSeconds(sampleTestTime)/((double)this.sampleFrequencyOption.getValue()/chunkInstances.numInstances());
				
				sampleTestTime = 0;
				sampleTrainTime = 0;
				
				List<Measurement> measurements = new ArrayList<Measurement>();
				measurements.add(new Measurement("learning evaluation instances", instancesProcessed));
				measurements.add(new Measurement(("evaluation time ("+ (preciseCPUTiming ? "cpu " : "") + "seconds)"),TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime)));
				measurements.add(new Measurement("average chunk train time", avgTrainTime));
				measurements.add(new Measurement("average chunk train speed", chunkInstances.numInstances() / avgTrainTime));
				measurements.add(new Measurement("average chunk test time", avgTestTime));
				measurements.add(new Measurement("average chunk test speed", chunkInstances.numInstances()/ avgTestTime));
				measurements.add(new Measurement( "model cost (RAM-Hours)", RAMHours));
				
				Measurement[] performanceMeasurements = evaluator.getPerformanceMeasurements();
	            Measurement instanciasClassificadas = new Measurement(STR_MEDICAO_INST_CLASSIFICADAS, chunkInstances.numInstances());
	            Measurement taxaAcertos = performanceMeasurements[1];
	            
	            if(!taxaAcertos.getName().equals(STR_MEDICAO_CLASSIFICATIONS_CORRECT))
	            	throw new RuntimeException("O nome da medida de taxa de acertos não está igual a \"classified instances\","
	            			+ " o que pode indicar que a medida mudou de posição.");
	            
	            for (int i = 0; i < performanceMeasurements.length; i++) {
	            	if(i != 1){
	            		Measurement measurement = performanceMeasurements[i];
	            		measurements.add(measurement);
	            	}
	            }
	            
	            Measurement[] modelMeasurements = learner.getModelMeasurements();
	            for (Measurement measurement : modelMeasurements) {
	                measurements.add(measurement);
	                
	            }
	            learningCurve.insertEntry(taxaAcertos, instanciasClassificadas,measurements);
				
				if (immediateResultStream != null) {
					if (firstDump) {
						immediateResultStream.println(learningCurve
								.headerToString());
						firstDump = false;
					}
					immediateResultStream.println(learningCurve
							.entryToString(learningCurve.numEntries() - 1));
					immediateResultStream.flush();
				}
			}
			
			////Memory testing
			if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
				if (monitor.taskShouldAbort()) {
					return null;
				}
				long estimatedRemainingInstances = stream
						.estimatedRemainingInstances();
				if (maxInstances > 0) {
					long maxRemaining = maxInstances - instancesProcessed;
					if ((estimatedRemainingInstances < 0)
							|| (maxRemaining < estimatedRemainingInstances)) {
						estimatedRemainingInstances = maxRemaining;
					}
				}
				monitor
						.setCurrentActivityFractionComplete(estimatedRemainingInstances < 0 ? -1.0
								: (double) instancesProcessed
										/ (double) (instancesProcessed + estimatedRemainingInstances));
				if (monitor.resultPreviewRequested()) {
					monitor.setLatestResultPreview(learningCurve.copy());
				}
				secondsElapsed = (int) TimingUtils
						.nanoTimeToSeconds(TimingUtils
								.getNanoCPUTimeOfCurrentThread()
								- evaluateStartTime);
			}
		}
		if (immediateResultStream != null) {
			immediateResultStream.close();
		}
		return learningCurve;
	}
}