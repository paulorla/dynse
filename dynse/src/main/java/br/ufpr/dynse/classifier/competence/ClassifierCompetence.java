package br.ufpr.dynse.classifier.competence;

import java.util.Arrays;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;

public class ClassifierCompetence implements IClassifierCompetence{
	
	private DynseClassifier<DynseClassifierPruningMetrics> classifier;
	private double[] competenceOnInstance;
	
	public ClassifierCompetence() {
	}
	
	public ClassifierCompetence(DynseClassifier<DynseClassifierPruningMetrics> classifier, double[] competenceOnInstance) {
		this.classifier = classifier;
		this.competenceOnInstance = competenceOnInstance;
	}

	@Override
	public DynseClassifier<DynseClassifierPruningMetrics> getClassifier() {
		return classifier;
	}
	
	@Override
	public void setClassifier(DynseClassifier<DynseClassifierPruningMetrics> classifier) {
		this.classifier = classifier;
	}
	
	@Override
	public double[] getCompetenceOnInstance() {
		return competenceOnInstance;
	}
	
	@Override
	public void setCompetenceOnInstance(double[] competenceOnInstance) {
		this.competenceOnInstance = competenceOnInstance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + Arrays.hashCode(competenceOnInstance);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassifierCompetence other = (ClassifierCompetence) obj;
		if (classifier == null) {
			if (other.classifier != null)
				return false;
		} else if (!classifier.equals(other.classifier))
			return false;
		if (!Arrays.equals(competenceOnInstance, other.competenceOnInstance))
			return false;
		return true;
	}
}