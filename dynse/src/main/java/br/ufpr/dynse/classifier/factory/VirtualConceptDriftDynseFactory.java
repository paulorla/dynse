package br.ufpr.dynse.classifier.factory;

public class VirtualConceptDriftDynseFactory extends AbstractDynseFactory{

	public static final int DEFAULT_ACC_WINDOW_SIZE_REAL_DRIFT = 32;
	
	@Override
	public Integer getDefaultAccuracyEstimationWindowSize() {
		return DEFAULT_ACC_WINDOW_SIZE_REAL_DRIFT;
	}
}
