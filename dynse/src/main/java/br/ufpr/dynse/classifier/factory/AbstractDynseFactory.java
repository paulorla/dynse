package br.ufpr.dynse.classifier.factory;

import br.ufpr.dynse.classificationengine.APrioriClassificationEngine;
import br.ufpr.dynse.classificationengine.IClassificationEngine;
import br.ufpr.dynse.classificationengine.KnoraEliminateClassificationEngine;
import br.ufpr.dynse.classificationengine.KnoraUnionClassificationEngine;
import br.ufpr.dynse.classificationengine.LCAClassificationEngine;
import br.ufpr.dynse.classificationengine.OLAClassificationEngine;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.core.StreamDynse;
import br.ufpr.dynse.pruningengine.AgeBasedPruningEngine;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import br.ufpr.dynse.pruningengine.IPruningEngine;

public abstract class AbstractDynseFactory {
	
		public static final int DEFAULT_NEIGHBORS_KE = 9;
		public static final int DEFAULT_SLACK_KE = 2;
		public static final int DEFAULT_NEIGHBORS_KU = 5;
		
		public static final int DEFAULT_NEIGHBORS_LCA = 5;
		public static final int DEFAULT_NEIGHBORS_OLA = 5;
		
		public static final int DEFAULT_NEIGHBORS_APRIORI = 5;
		public static final int DEFAULT_NEIGHBORS_APOSTERIORI = 5;
		
		public final AbstractClassifierFactory classifierFactory = new NaiveBayesFactory();
		//public final AbstractClassifierFactory classifierFactory = new HoeffdingTreeFactory();
		
		public final int DEFAULT_POOL_SIZE = 25;
		public final IPruningEngine<DynseClassifierPruningMetrics> DEFAULT_PRUNING_ENGINE;
	
		public AbstractDynseFactory(){
			try{
				DEFAULT_PRUNING_ENGINE = new AgeBasedPruningEngine(DEFAULT_POOL_SIZE);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		
		public StreamDynse createDefaultDynseKE(int numInstancesTrainEachClassifierV) throws Exception{
				IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
						new KnoraEliminateClassificationEngine(DEFAULT_NEIGHBORS_KE, DEFAULT_SLACK_KE);
				StreamDynse dynse = new StreamDynse(classifierFactory,
						numInstancesTrainEachClassifierV, getDefaultAccuracyEstimationWindowSize(),
						classificationEngine, DEFAULT_PRUNING_ENGINE);
				
				return dynse;
		}
		
		public StreamDynse createDefaultDynseKU(int numInstancesTrainEachClassifierV) throws Exception{
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new KnoraUnionClassificationEngine(DEFAULT_NEIGHBORS_KU);
			StreamDynse dynse = new StreamDynse(classifierFactory,
					numInstancesTrainEachClassifierV, getDefaultAccuracyEstimationWindowSize(),
					classificationEngine, DEFAULT_PRUNING_ENGINE);
			
			return dynse;
		}
		
		public StreamDynse createDefaultDynseLCA(int numInstancesTrainEachClassifierV) throws Exception{
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new LCAClassificationEngine(DEFAULT_NEIGHBORS_LCA);
			StreamDynse dynse = new StreamDynse(classifierFactory,
					numInstancesTrainEachClassifierV, getDefaultAccuracyEstimationWindowSize(),
					classificationEngine, DEFAULT_PRUNING_ENGINE);
			
			return dynse;
		}
		
		public StreamDynse createDefaultDynseOLA(int numInstancesTrainEachClassifierV) throws Exception{
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new OLAClassificationEngine(DEFAULT_NEIGHBORS_OLA);
			StreamDynse dynse = new StreamDynse(classifierFactory,
					numInstancesTrainEachClassifierV, getDefaultAccuracyEstimationWindowSize(),
					classificationEngine, DEFAULT_PRUNING_ENGINE);
			
			return dynse;
		}
		
		public StreamDynse createDefaultDynseAPriori(int numInstancesTrainEachClassifierV) throws Exception{
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new APrioriClassificationEngine(DEFAULT_NEIGHBORS_APRIORI);
			StreamDynse dynse = new StreamDynse(classifierFactory,
					numInstancesTrainEachClassifierV, getDefaultAccuracyEstimationWindowSize(),
					classificationEngine, DEFAULT_PRUNING_ENGINE);
			
			return dynse;
		}
		
		public StreamDynse createDefaultDynseAPosteriori(int numInstancesTrainEachClassifierV) throws Exception{
			IClassificationEngine<IMultipleClassifiersCompetence> classificationEngine = 
					new APrioriClassificationEngine(DEFAULT_NEIGHBORS_APOSTERIORI);
			StreamDynse dynse = new StreamDynse(classifierFactory,
					numInstancesTrainEachClassifierV, getDefaultAccuracyEstimationWindowSize(),
					classificationEngine, DEFAULT_PRUNING_ENGINE);
			
			return dynse;
		}
		
		public abstract Integer getDefaultAccuracyEstimationWindowSize();
}