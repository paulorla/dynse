package br.ufpr.dynse.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classificationengine.IClassificationEngine;
import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.competence.ClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.classifier.competence.MultipleClassifiersCompetence;
import br.ufpr.dynse.classifier.factory.AbstractClassifierFactory;
import br.ufpr.dynse.concept.Concept;
import br.ufpr.dynse.instance.ConceptInstance;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import br.ufpr.dynse.pruningengine.IPruningEngine;
import br.ufpr.dynse.util.InstancesUtils;
import moa.core.Utils;

public class StreamDynse extends AbstractDynse<LinkedList<Instance>, IMultipleClassifiersCompetence>
		implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean updateNNSearch;
	private DynseClassifier<DynseClassifierPruningMetrics> clasifierTrainedIncompleteBatch = null;
	private boolean classifierTrainedIncompleteBatchIsUpdated = false;
	private Set<Concept<?>> trainingConcepts;

	public StreamDynse(AbstractClassifierFactory classifierFactory, int trainBatchSize,
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine) throws Exception {
		this(classifierFactory, trainBatchSize, 1, classificationEngine, null);
	}

	public StreamDynse(AbstractClassifierFactory classifierFactory, int trainBatchSize,
			int accuracyEstimationWindowSizeInBatches,
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine) throws Exception {
		this(classifierFactory, trainBatchSize, accuracyEstimationWindowSizeInBatches, classificationEngine, null);
	}

	public StreamDynse(AbstractClassifierFactory classifierFactory, int trainBatchSize,
			int accuracyEstimationWindowSizeInBatches, IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine,
			IPruningEngine<DynseClassifierPruningMetrics> pruningEngine) throws Exception {
		super(classifierFactory, trainBatchSize, accuracyEstimationWindowSizeInBatches * trainBatchSize,
				classificationEngine, pruningEngine);
		super.createNNSearch();
		trainingConcepts = new HashSet<Concept<?>>();

		updateNNSearch = false;
		super.resetClassifiersMapping();
	}

	@Override
	protected void trainClassifierIncompleteBatch() throws Exception {
		Set<Concept<?>> conceitos = new HashSet<Concept<?>>(trainingConcepts);
		DynseClassifier<DynseClassifierPruningMetrics> classifier = 
				this.addNewClassifierAccumulatedInstances(super.getTrainInstancesAccumulator(),
				super.getAccuracyEstimationInstances(), conceitos);
		if (classifier != null)
			this.mapClassifierCompetence(classifier);
		clasifierTrainedIncompleteBatch = classifier;
		classifierTrainedIncompleteBatchIsUpdated = true;
	}

	@Override
	public void trainOnInstanceImpl(Instance instance) {
		try {
			if (instance instanceof ConceptInstance){
				trainingConcepts.add(((ConceptInstance) instance).getConcept());
			}

			Instance removedInstance = null;
			super.getTrainInstancesAccumulator().addLast(instance);
			super.getAccuracyEstimationInstances().addLast(instance);
			
			if (super.getAccuracyEstimationInstances().size() > super.getAccuracyEstimationWindowSize()) {
				removedInstance = super.getAccuracyEstimationInstances().getFirst();
				super.getAccuracyEstimationInstances().removeFirst();
			}

			this.updateClassifiersMap(instance, removedInstance);
			if (super.getTrainInstancesAccumulator().size() >= super.getNumMinInstancesTrainClassifier())
				this.atualizarClassificadoresComAcumulador();
			else
				classifierTrainedIncompleteBatchIsUpdated = false;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void updateClassifiersMap(Instance instance, Instance removedInstance) throws Exception {
		Set<IClassifierCompetence> classifiersInstance = new HashSet<IClassifierCompetence>();
		for (DynseClassifier<DynseClassifierPruningMetrics> classifier : super.getClassifiers()) {
			double[] distribution = classifier.getVotesForInstance(instance);

			if (super.getClassificationEngine().getMapOnlyCorrectClassifiers() == false
					|| Utils.maxIndex(distribution) == instance.classValue()) {
				ClassifierCompetence competence = new ClassifierCompetence(classifier, distribution);
				classifiersInstance.add(competence);
			}
		}
		IMultipleClassifiersCompetence multipleClassifiersCompetence = super.getClassifiersMapping().get(instance);
		if (multipleClassifiersCompetence != null) {// the instance is a duplicate
			multipleClassifiersCompetence.setInstanceCount(multipleClassifiersCompetence.getInstanceCount() + 1);
			multipleClassifiersCompetence.getClassifiersCompetence().addAll(classifiersInstance);
		} else {
			multipleClassifiersCompetence = new MultipleClassifiersCompetence();
			multipleClassifiersCompetence.setInstance(instance);
			multipleClassifiersCompetence.setClassifiersCompetence(classifiersInstance);
			super.getClassifiersMapping().put(instance, multipleClassifiersCompetence);
		}

		if (removedInstance != null) {
			IMultipleClassifiersCompetence competenceBeingRemoved = super.getClassifiersMapping()
					.get(removedInstance);
			if (competenceBeingRemoved.getInstanceCount() > 1)
				competenceBeingRemoved.setInstanceCount(competenceBeingRemoved.getInstanceCount() - 1);
			else
				super.getClassifiersMapping().remove(removedInstance);
		}

		updateNNSearch = true;
	}

	private void updateNNSearch() {
		try {
			super.setNNSearchInstances(InstancesUtils.gerarDataset(
					super.getAccuracyEstimationInstances(), "Validation Instances"));
			updateNNSearch = false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public double[] getVotesForInstance(Instance instance) {
		if (updateNNSearch == true){
			this.updateNNSearch();
		}else{
			if(super.getNumClassifiersPool() < 1
					&& super.getTrainInstancesAccumulator().size() < 1 && super.getAccuracyEstimationInstances().size() < 1)
				return new double[0];//No classifier trained
		}
		if (classifierTrainedIncompleteBatchIsUpdated == false && super.getTrainInstancesAccumulator().size() > 0) {
			if (clasifierTrainedIncompleteBatch != null) {
				super.pruneClassifier(clasifierTrainedIncompleteBatch);
				try {
					this.trainClassifierIncompleteBatch();
				} catch (Exception e) {
					StringBuilder builder = new StringBuilder();
					getModelDescription(builder, 0);
					throw new RuntimeException(e);
				}
			}
		}
		return super.getVotesForInstance(instance);
	}

	@Override
	public void resetLearningImpl() {
		super.resetClassifiersMapping();
		super.resetLearningImpl();
	}

	@Override
	protected LinkedList<Instance> createEmptyTrainingInstancesAccumulator() {
		return new LinkedList<Instance>();
	}

	@Override
	protected LinkedList<Instance> createEmptyAccuracyEstimationAccumulator() {
		return new LinkedList<Instance>();
	}

	private void atualizarClassificadoresComAcumulador() throws Exception {
		if (clasifierTrainedIncompleteBatch != null) {
			super.pruneClassifier(clasifierTrainedIncompleteBatch);
			clasifierTrainedIncompleteBatch = null;
		}

		Set<Concept<?>> concepts = new HashSet<Concept<?>>(trainingConcepts);
		DynseClassifier<DynseClassifierPruningMetrics> classifier = this.addNewClassifierAccumulatedInstances(super.getTrainInstancesAccumulator(),
				super.getAccuracyEstimationInstances(), concepts);
		if (classifier != null)
			this.mapClassifierCompetence(classifier);
		super.getTrainInstancesAccumulator().clear();

		trainingConcepts.clear();
	}

	protected void mapClassifierCompetence(DynseClassifier<DynseClassifierPruningMetrics> classifier) throws Exception {
		for (IMultipleClassifiersCompetence mcc : super.getClassifiersMapping().values()) {
			double[] distribution = classifier.getVotesForInstance(mcc.getInstance());
			if (super.getClassificationEngine().getMapOnlyCorrectClassifiers() == false 
					|| Utils.maxIndex(distribution) == mcc.getInstance().classValue()) {
				ClassifierCompetence competence = new ClassifierCompetence(classifier, distribution);
				mcc.getClassifiersCompetence().add(competence);
			}
		}
	}

	@Override
	public void getDescription(StringBuilder out, int ident) {
		out.append("Stream Dynse\n");
		out.append("Num. Training Instances each classifier: ");
		out.append(super.getNumMinInstancesTrainClassifier());
		out.append("\n");
		out.append("Accuracy Est. Window Size: ");
		out.append(super.getAccuracyEstimationWindowSize());
		out.append("\n");
		out.append("Classifier Factory: ");
		super.getClassifierFactory().getDescription(out);
		out.append("\n");
		out.append("Classification Engine");
		super.getClassificationEngine().getClassificationEngineDescription(out);
		out.append("\n");
		out.append("Prunning Engine");
		super.getPruningEngine().getPrunningEngineDescription(out);
	}

	@Override
	public void getShortDescription(StringBuilder out, int ident) {
		out.append("SDynseT" + super.getNumMinInstancesTrainClassifier() + "W"
				+ super.getAccuracyEstimationWindowSize());
		super.getClassifierFactory().getShortDescription(out);
		super.getClassificationEngine().getClassificationEngineShortDescription(out);
		super.getPruningEngine().getPrunningEngineShortDescription(out);
	}

	@Override
	public void getModelDescription(StringBuilder out, int ident) {
		this.getDescription(out, ident);
	}
}