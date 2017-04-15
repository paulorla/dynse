package br.ufpr.dynse.pruningengine;

import br.ufpr.dynse.classifier.DynseClassifier;

public class DynseClassifierPruningMetrics {
	
	public static final Double DEFAULT_INCREASE_FACTOR_STEP = 1.0;
	public static final Double DEFAULT_DECREASE_FACTOR_STEP = -1.0;
	
	private Long creationTime;
	private Double useageFactor;
	private DynseClassifier<?> dynseClassifier;

	public DynseClassifierPruningMetrics(DynseClassifier<?> dynseClassifier) {
		this.creationTime = System.currentTimeMillis();
		this.useageFactor = 0.0;
		this.dynseClassifier = dynseClassifier;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public DynseClassifier<?> getDynseClassifier() {
		return dynseClassifier;
	}

	public Double getUseageFactor() {
		return useageFactor;
	}

	public void setUseageFactor(Double useageFactor) {
		this.useageFactor = useageFactor;
	}
	
	public void increaseUseageFactor(){
		this.useageFactor+=DEFAULT_INCREASE_FACTOR_STEP;
	}
	
	public void decreaseUseageFactor(){
		this.useageFactor+=DEFAULT_DECREASE_FACTOR_STEP;
	}

	@Override
	public String toString() {
		return "creationTime: " + creationTime + ", useageFactor: " + useageFactor;
	}
}