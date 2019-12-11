 /*
 *    EvaluatePrequential.java
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
 *    @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 *    @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package br.ufpr.dynse.evaluation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.core.ConceptMeasuredAbstractClassifier;
import br.ufpr.dynse.core.UFPRLearningCurve;
import moa.core.Example;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.core.TimingUtils;
import moa.core.Utils;
import moa.evaluation.EWMAClassificationPerformanceEvaluator;
import moa.evaluation.FadingFactorClassificationPerformanceEvaluator;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.evaluation.WindowClassificationPerformanceEvaluator;
import moa.learners.Learner;
import moa.streams.ExampleStream;
import moa.tasks.EvaluatePrequential;
import moa.tasks.TaskMonitor;

/**
 * Task for evaluating a classifier on a stream by testing then training with
 * each example in sequence.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class EvaluatePrequentialSingleTrainPKLot extends EvaluatePrequential {

	private static final String STR_MEDICAO_CLASSIFICATIONS_CORRECT = "classifications correct (percent)";
	private static final String STR_MEDICAO_INST_CLASSIFICADAS = "classified instances";

	public IntOption trainSizeOption = new IntOption("trainSize", 'b', "Number of training examples, <1 = unlimited.",
			30, 1, Integer.MAX_VALUE);

	@Override
	protected Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
		int totalCorrect = 0;
		int totalIncorrect = 0;

		Learner learner = (Learner) getPreparedClassOption(this.learnerOption);
		ExampleStream stream = (ExampleStream) getPreparedClassOption(this.streamOption);
		LearningPerformanceEvaluator evaluator = (LearningPerformanceEvaluator) getPreparedClassOption(
				this.evaluatorOption);
		UFPRLearningCurve learningCurve = new UFPRLearningCurve("learning evaluation instances");

		// New for prequential methods
		if (evaluator instanceof WindowClassificationPerformanceEvaluator) {
			// ((WindowClassificationPerformanceEvaluator)
			// evaluator).setWindowWidth(widthOption.getValue());
			if (widthOption.getValue() != 1000) {
				System.out
						.println("DEPRECATED! Use EvaluatePrequential -e (WindowClassificationPerformanceEvaluator -w "
								+ widthOption.getValue() + ")");
				return learningCurve;
			}
		}
		if (evaluator instanceof EWMAClassificationPerformanceEvaluator) {
			// ((EWMAClassificationPerformanceEvaluator)
			// evaluator).setalpha(alphaOption.getValue());
			if (alphaOption.getValue() != .01) {
				System.out.println("DEPRECATED! Use EvaluatePrequential -e (EWMAClassificationPerformanceEvaluator -a "
						+ alphaOption.getValue() + ")");
				return learningCurve;
			}
		}
		if (evaluator instanceof FadingFactorClassificationPerformanceEvaluator) {
			// ((FadingFactorClassificationPerformanceEvaluator)
			// evaluator).setalpha(alphaOption.getValue());
			if (alphaOption.getValue() != .01) {
				System.out.println(
						"DEPRECATED! Use EvaluatePrequential -e (FadingFactorClassificationPerformanceEvaluator -a "
								+ alphaOption.getValue() + ")");
				return learningCurve;
			}
		}
		// End New for prequential methods

		learner.setModelContext(stream.getHeader());
		int maxInstances = this.instanceLimitOption.getValue();
		long instancesProcessed = 0;
		int maxSeconds = this.timeLimitOption.getValue();
		int secondsElapsed = 0;

		monitor.setCurrentActivityDescription("Training...");

		
		int numTrainedInstances = 0;
		while (numTrainedInstances < this.trainSizeOption.getValue()) {
			if (!stream.hasMoreInstances())
				throw new RuntimeException("Stream does not has enough training instances");
			learner.trainOnInstance((Example) stream.nextInstance());
			instancesProcessed++;
			if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
				if (monitor.taskShouldAbort()) {
					return null;
				}
				monitor.setCurrentActivityFractionComplete(
						(double) (instancesProcessed) / (double) (this.trainSizeOption.getValue()));
			}
			numTrainedInstances++;
		}

		monitor.setCurrentActivity("Evaluating learner...", -1.0);

		File dumpFile = this.dumpFileOption.getFile();
		PrintStream immediateResultStream = null;
		if (dumpFile != null) {
			try {
				if (dumpFile.exists()) {
					immediateResultStream = new PrintStream(new FileOutputStream(dumpFile, true), true);
				} else {
					immediateResultStream = new PrintStream(new FileOutputStream(dumpFile), true);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Unable to open immediate result file: " + dumpFile, ex);
			}
		}
		// File for output predictions
		File outputPredictionFile = this.outputPredictionFileOption.getFile();
		PrintStream outputPredictionResultStream = null;
		if (outputPredictionFile != null) {
			try {
				if (outputPredictionFile.exists()) {
					outputPredictionResultStream = new PrintStream(new FileOutputStream(outputPredictionFile, true),
							true);
				} else {
					outputPredictionResultStream = new PrintStream(new FileOutputStream(outputPredictionFile), true);
				}
			} catch (Exception ex) {
				throw new RuntimeException("Unable to open prediction result file: " + outputPredictionFile, ex);
			}
		}
		boolean firstDump = true;
		boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
		long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
		long lastEvaluateStartTime = evaluateStartTime;
		double RAMHours = 0.0;
		while (stream.hasMoreInstances() && ((maxInstances < 0) || (instancesProcessed < maxInstances))
				&& ((maxSeconds < 0) || (secondsElapsed < maxSeconds))) {
			Example testInst = stream.nextInstance();
			// testInst.setClassMissing();
			double[] prediction = learner.getVotesForInstance(testInst);
			// Output prediction
			if (outputPredictionFile != null) {
				int trueClass = (int) ((Instance) testInst.getData()).classValue();
				outputPredictionResultStream.println(Utils.maxIndex(prediction) + ","
						+ (((Instance) testInst.getData()).classIsMissing() == true ? " ? " : trueClass));
			}

			if ((int) ((Instance) testInst.getData()).classValue() == Utils.maxIndex(prediction))
				totalCorrect++;
			else
				totalIncorrect++;

			// evaluator.addClassificationAttempt(trueClass, prediction, testInst.weight());
			evaluator.addResult(testInst, prediction);
			instancesProcessed++;
			if (instancesProcessed % this.sampleFrequencyOption.getValue() == 0 || stream.hasMoreInstances() == false) {
				long evaluateTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
				double time = TimingUtils.nanoTimeToSeconds(evaluateTime - evaluateStartTime);
				double timeIncrement = TimingUtils.nanoTimeToSeconds(evaluateTime - lastEvaluateStartTime);
				double RAMHoursIncrement = learner.measureByteSize() / (1024.0 * 1024.0 * 1024.0); // GBs
				RAMHoursIncrement *= (timeIncrement / 3600.0); // Hours
				RAMHours += RAMHoursIncrement;
				lastEvaluateStartTime = evaluateTime;

				// BEGIN OF UFPRLEARNINGCURVE
				List<Measurement> measurements = new ArrayList<Measurement>();
				measurements.add(new Measurement("learning evaluation instances", instancesProcessed));

				Measurement[] performanceMeasurements = evaluator.getPerformanceMeasurements();

				Measurement instanciasClassificadas = performanceMeasurements[0];
				Measurement taxaAcertos = performanceMeasurements[1];

				if (!taxaAcertos.getName().equals(STR_MEDICAO_CLASSIFICATIONS_CORRECT))
					throw new RuntimeException(
							"O nome da medida de taxa de acertos não está igual a \"classified instances\","
									+ " o que pode indicar que a medida mudou de posição.");
				if (!instanciasClassificadas.getName().equals(STR_MEDICAO_INST_CLASSIFICADAS))
					throw new RuntimeException(
							"O nome da medida de taxa de acertos não está igual a \"classified instances\","
									+ " o que pode indicar que a medida mudou de posição.");

				for (int i = 1; i < performanceMeasurements.length; i++) {// começando em 1 para replicar a acuracia
																			// prequencial na lista completa de medições
					Measurement measurement = performanceMeasurements[i];
					measurements.add(measurement);
				}

				Measurement[] modelMeasurements = learner.getModelMeasurements();
				for (Measurement measurement : modelMeasurements) {
					measurements.add(measurement);

				}

				if (learner instanceof ConceptMeasuredAbstractClassifier) {
					LinkedHashMap<Concept<?>, Measurement> conceptMeasurements = ((ConceptMeasuredAbstractClassifier) learner)
							.getConceptMeasurementsUpToLastCheck();
					for (Map.Entry<Concept<?>, Measurement> entry : conceptMeasurements.entrySet()) {
						measurements.add(entry.getValue());
					}
				}

				learningCurve.insertEntry(taxaAcertos, instanciasClassificadas, measurements);
				// END OF UFPRLEARNINGCURVE

				if (immediateResultStream != null) {
					if (firstDump) {
						immediateResultStream.println(learningCurve.headerToString());
						firstDump = false;
					}
					immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
					immediateResultStream.flush();
				}
			}
			if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
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
				monitor.setCurrentActivityFractionComplete(estimatedRemainingInstances < 0 ? -1.0
						: (double) instancesProcessed / (double) (instancesProcessed + estimatedRemainingInstances));
				if (monitor.resultPreviewRequested()) {
					monitor.setLatestResultPreview(learningCurve.copy());
				}
				secondsElapsed = (int) TimingUtils
						.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
			}
		}
		if (immediateResultStream != null) {
			immediateResultStream.close();
		}
		if (outputPredictionResultStream != null) {
			outputPredictionResultStream.close();
		}

		learningCurve.setTotalCorrectlyClassifiedInstances(totalCorrect);
		learningCurve.setTotalInstances(totalCorrect + totalIncorrect);
		return learningCurve;
	}
}