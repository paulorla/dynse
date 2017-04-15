package br.ufpr.dynse.generator;

import java.util.SortedSet;

import br.ufpr.dynse.concept.Concept;

public interface ConceptBasedDriftGenerator<T extends Comparable<T>> {
	public SortedSet<Concept<T>> getGeneratedConcepts();	
}