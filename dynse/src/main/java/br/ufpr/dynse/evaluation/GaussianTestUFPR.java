/*    
*    GaussianTestUFPR.java 
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

import br.ufpr.dynse.core.UFPRLearningCurve;
import moa.classifiers.Classifier;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.core.StringUtils;
import moa.core.TimingUtils;
import moa.evaluation.preview.LearningCurve;
import moa.options.ClassOption;
import moa.streams.InstanceStream;
import moa.tasks.EvaluatePeriodicHeldOutTest;
import moa.tasks.TaskMonitor;

public class GaussianTestUFPR extends EvaluatePeriodicHeldOutTest {
	
	private static final int TRAIN_BATCH_SIZE = 20;
	private static final int TEST_BATCH_SIZE = 1024;

    private static final long serialVersionUID = 1L;

    private InstanceStream trainStream;
    private InstanceStream testStream;
    private List<Double[]> priors;

    public InstanceStream getTrainStream() {
		return trainStream;
	}

    public void setTrainStream(InstanceStream trainStream) {
		this.trainStream = trainStream;
	}

    public InstanceStream getTestStream() {
		return testStream;
	}

    public void setTestStream(InstanceStream testStream) {
		this.testStream = testStream;
	}

    public List<Double[]> getPriors() {
		return priors;
	}

    public void setPriors(List<Double[]> priors) {
		this.priors = priors;
	}

    @Override
    protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
        Classifier learner = (Classifier) getPreparedClassOption(this.learnerOption);
        learner.setModelContext(trainStream.getHeader());
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
        
        instancesProcessed = 0;
        TimingUtils.enablePreciseTiming();
        double totalTrainTime = 0.0;
        int idxPriors = 0;
        while (trainStream.hasMoreInstances() == true) {
            monitor.setCurrentActivityDescription("Training...");
            long instancesTarget = instancesProcessed
                    + TRAIN_BATCH_SIZE;
            long trainStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            while (instancesProcessed < instancesTarget && trainStream.hasMoreInstances() == true) {
                learner.trainOnInstance(trainStream.nextInstance());
                instancesProcessed++;
                if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                    if (monitor.taskShouldAbort()) {
                        return null;
                    }
                    monitor.setCurrentActivityFractionComplete((double)instancesProcessed/(double)TRAIN_BATCH_SIZE);
                }
            }
            double lastTrainTime = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()
                    - trainStartTime);
            totalTrainTime += lastTrainTime;
            long testInstancesProcessed = 0;
            monitor.setCurrentActivityDescription("Testing (after "
                    + StringUtils.doubleToString(
                    ((double) (instancesProcessed)
                    / (double) (TRAIN_BATCH_SIZE) * 100.0), 2)
                    + "% training)...");
            long testStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            int instCount = 0 ;
            double[] totalPrediction = new double[4];
            for(int i =0; i < 4;i++)
            	totalPrediction[i] = 0.0;
            
            double predictionsSum = 0.0;
            for (instCount = 0; instCount < TEST_BATCH_SIZE && testStream.hasMoreInstances(); instCount++){
                Instance testInst = (Instance)testStream.nextInstance().getData();
                double[] prediction = learner.getVotesForInstance(testInst);
                for(int i =0; i < prediction.length;i++){
                	totalPrediction[i] += prediction[i];
                	predictionsSum+=prediction[i];
                }
                
                
                testInstancesProcessed++;
                if (testInstancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                    if (monitor.taskShouldAbort()) {
                        return null;
                    }
                    monitor.setCurrentActivityFractionComplete((double) testInstancesProcessed
                            / (double) (TEST_BATCH_SIZE));
                }
            }
            instancesProcessed+=testInstancesProcessed;
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
                    TRAIN_BATCH_SIZE / lastTrainTime));
            measurements.add(new Measurement("test time", testTime));
            measurements.add(new Measurement("test speed", TEST_BATCH_SIZE/ testTime));
            measurements.add(new Measurement("test speed", TEST_BATCH_SIZE/ testTime));
            
            Measurement instanciasClassificadas = new Measurement("Processed Instances", testInstancesProcessed);
            measurements.add(instanciasClassificadas);
            
            for(int i =0; i < 4;i++){
            	totalPrediction[i] /= predictionsSum;
            }
            
            Measurement[] modelMeasurements = learner.getModelMeasurements();
            for (Measurement measurement : modelMeasurements) {
                measurements.add(measurement);
            }
            
            double diff = 0.0;
            for(int i =0; i < 4;i++){
            	diff +=  Math.abs(totalPrediction[i] - priors.get(idxPriors)[i]);
            }
            diff = diff/2;// Dividir por 2 devido aos priors. Cada erro no prior é computado 2x (a instância deixou de computar para a classe real, e ainda computou para a classe incorreta)
            
            Measurement taxaAcertos = new Measurement("Accuracy", (TEST_BATCH_SIZE - (diff*TEST_BATCH_SIZE))/(double)TEST_BATCH_SIZE);
            measurements.add(taxaAcertos);
            learningCurve.insertEntry(taxaAcertos, instanciasClassificadas,measurements);
            idxPriors++;
            
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

    @Override
    public Class<?> getTaskResultType() {
        return LearningCurve.class;
    }
}