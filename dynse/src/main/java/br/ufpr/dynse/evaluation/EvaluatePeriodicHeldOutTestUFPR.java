/*    
*    EvaluatePeriodicHeldOutTestUFPR.java 
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.core.ConceptMeasuredAbstractClassifier;
import br.ufpr.dynse.core.UFPRLearningCurve;
import moa.classifiers.Classifier;
import moa.core.Example;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.core.StringUtils;
import moa.core.TimingUtils;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.streams.CachedInstancesStream;
import moa.streams.ExampleStream;
import moa.tasks.EvaluatePeriodicHeldOutTest;
import moa.tasks.TaskMonitor;

/**
 * Task for evaluating a classifier on a stream by periodically testing on a heldout set.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 7 $
 */
public class EvaluatePeriodicHeldOutTestUFPR extends EvaluatePeriodicHeldOutTest {
	
	private static final long serialVersionUID = 1L;
	
	private static final String STR_MEDICAO_CLASSIFICATIONS_CORRECT = "classifications correct (percent)";
	private static final String STR_MEDICAO_INST_CLASSIFICADAS = "classified instances";
    
    @Override
    protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
        Classifier learner = (Classifier) getPreparedClassOption(this.learnerOption);
        ExampleStream stream = (ExampleStream) getPreparedClassOption(this.streamOption);
        LearningPerformanceEvaluator evaluator = (LearningPerformanceEvaluator) getPreparedClassOption(this.evaluatorOption);
        learner.setModelContext(stream.getHeader());
        long instancesProcessed = 0;
        UFPRLearningCurve learningCurve = new UFPRLearningCurve("evaluation instances");
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
            }catch (Exception ex) {
                throw new RuntimeException(
                        "Unable to open immediate result file: " + dumpFile, ex);
            }
        }
        boolean firstDump = true;
        ExampleStream testStream = null;
        int testSize = this.testSizeOption.getValue();
        if (this.cacheTestOption.isSet()) {
            monitor.setCurrentActivity("Caching test examples...", -1.0);
            Instances testInstances = new Instances(stream.getHeader(),
                    this.testSizeOption.getValue());
            while (testInstances.numInstances() < testSize) {
            	testInstances.add((Instance)stream.nextInstance().getData());
                if (testInstances.numInstances()
                        % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                    if (monitor.taskShouldAbort()) {
                        return null;
                    }
                    monitor.setCurrentActivityFractionComplete((double) testInstances.numInstances()
                            / (double) (this.testSizeOption.getValue()));
                }
            }
            testStream = new CachedInstancesStream(testInstances);
        } else {
            //testStream = (InstanceStream) stream.copy();
            testStream = stream;
            /*monitor.setCurrentActivity("Skipping test examples...", -1.0);
            for (int i = 0; i < testSize; i++) {
            stream.nextInstance();
            }*/
        }
        instancesProcessed = 0;
        TimingUtils.enablePreciseTiming();
        double totalTrainTime = 0.0;
        while ((this.trainSizeOption.getValue() < 1
                || instancesProcessed < this.trainSizeOption.getValue())
                && stream.hasMoreInstances() == true) {
            monitor.setCurrentActivityDescription("Training...");
            long instancesTarget = instancesProcessed
                    + this.sampleFrequencyOption.getValue();
            long trainStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            while (instancesProcessed < instancesTarget && stream.hasMoreInstances() == true) {
                learner.trainOnInstance(stream.nextInstance());
                instancesProcessed++;
                if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                    if (monitor.taskShouldAbort()) {
                        return null;
                    }
                    monitor.setCurrentActivityFractionComplete((double) (instancesProcessed)
                            / (double) (this.trainSizeOption.getValue()));
                }
            }
            double lastTrainTime = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()
                    - trainStartTime);
            totalTrainTime += lastTrainTime;
            if (totalTrainTime > this.trainTimeOption.getValue()) {
                break;
            }
	    if (this.cacheTestOption.isSet()) {
                testStream.restart();
            } 
            evaluator.reset();
            long testInstancesProcessed = 0;
            monitor.setCurrentActivityDescription("Testing (after "
                    + StringUtils.doubleToString(
                    ((double) (instancesProcessed)
                    / (double) (this.trainSizeOption.getValue()) * 100.0), 2)
                    + "% training)...");
            long testStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            int instCount = 0 ;
            for (instCount = 0; instCount < testSize; instCount++) {
				if (stream.hasMoreInstances() == false) {
					break;
				}
                Example testInst = (Example) testStream.nextInstance();
                double[] prediction = learner.getVotesForInstance(testInst);
                //testInst.setClassValue(trueClass);
                evaluator.addResult(testInst, prediction);
                testInstancesProcessed++;
                
                if (testInstancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                    if (monitor.taskShouldAbort()) {
                        return null;
                    }
                    monitor.setCurrentActivityFractionComplete((double) testInstancesProcessed
                            / (double) (testSize));
                }
            }
            
        	if ( instCount != testSize) {
				break;
			}
            double testTime = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()
                    - testStartTime);
            List<Measurement> measurements = new ArrayList<Measurement>();
            measurements.add(new Measurement("evaluation instances",            		
                    instancesProcessed));
            measurements.add(new Measurement("total train time", totalTrainTime));
            measurements.add(new Measurement("total train speed",
                    instancesProcessed / totalTrainTime));
            measurements.add(new Measurement("last train time", lastTrainTime));
            measurements.add(new Measurement("last train speed",
                    this.sampleFrequencyOption.getValue() / lastTrainTime));
            measurements.add(new Measurement("test time", testTime));
            measurements.add(new Measurement("test speed", this.testSizeOption.getValue()
                    / testTime));
            
            Measurement[] performanceMeasurements = evaluator.getPerformanceMeasurements();
            
            Measurement instanciasClassificadas = performanceMeasurements[0];
            Measurement taxaAcertos = performanceMeasurements[1];
            
            if(!taxaAcertos.getName().equals(STR_MEDICAO_CLASSIFICATIONS_CORRECT))
            	throw new RuntimeException("O nome da medida de taxa de acertos não está igual a \"classified instances\","
            			+ " o que pode indicar que a medida mudou de posição.");
            if(!instanciasClassificadas.getName().equals(STR_MEDICAO_INST_CLASSIFICADAS))
            	throw new RuntimeException("O nome da medida de taxa de acertos não está igual a \"classified instances\","
            			+ " o que pode indicar que a medida mudou de posição.");
            
            for (int i = 2; i < performanceMeasurements.length; i++) {
            	Measurement measurement = performanceMeasurements[i];
                measurements.add(measurement);
            }
            
            Measurement[] modelMeasurements = learner.getModelMeasurements();
            for (Measurement measurement : modelMeasurements) {
                measurements.add(measurement);
                
            }
            
            if(learner instanceof ConceptMeasuredAbstractClassifier) {
            	LinkedHashMap<Concept<?>, Measurement> conceptMeasurements = ((ConceptMeasuredAbstractClassifier)learner).getConceptMeasurementsUpToLastCheck();
            	for (Map.Entry<Concept<?>, Measurement> entry : conceptMeasurements.entrySet()){
            		measurements.add(entry.getValue());
            	}
			}
            
            learningCurve.insertEntry(taxaAcertos, instanciasClassificadas,measurements);
            if (immediateResultStream != null) {
                if (firstDump) {
                    immediateResultStream.println(learningCurve.headerToString());
                    firstDump = false;
                }
                immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
                immediateResultStream.flush();
            }
            if (monitor.resultPreviewRequested()) {
                monitor.setLatestResultPreview(learningCurve.copy());
            }
        }
        if (immediateResultStream != null) {
            immediateResultStream.close();
        }
        return learningCurve;
    }
}