package br.ufpr.dynse.classifier.competence;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;

public interface IClassifierCompetence {
	
	public DynseClassifier<DynseClassifierPruningMetrics> getClassifier();
	public double[] getCompetenceOnInstance();
	public void setClassifier(DynseClassifier<DynseClassifierPruningMetrics> classifier);
	public void setCompetenceOnInstance(double[] competenceOnInstance);
}