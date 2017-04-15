package br.ufpr.dynse.core;

import java.io.Serializable;
import java.util.LinkedHashMap;

import br.ufpr.dynse.concept.Concept;
import moa.classifiers.AbstractClassifier;
import moa.core.Measurement;

public abstract class ConceptMeasuredAbstractClassifier extends AbstractClassifier implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public abstract LinkedHashMap<Concept<?>, Measurement> getConceptMeasurementsUpToLastCheck();
	
	public abstract LinkedHashMap<Concept<?>, Integer> getTotalConceptsUpToLastCheck();
}
