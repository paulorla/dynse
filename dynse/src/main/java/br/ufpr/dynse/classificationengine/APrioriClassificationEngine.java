/*    
*    APrioriClassificationEngine.java 
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
package br.ufpr.dynse.classificationengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.MajorityVoting;
import br.ufpr.dynse.classifier.competence.IClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.lazy.neighboursearch.LinearNNSearch;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;
import moa.core.Utils;

public class APrioriClassificationEngine implements IClassificationEngine<IMultipleClassifiersCompetence>{

	private static final long serialVersionUID = 1L;
	
	private class ProbCorrectClassifierVA{
		private final DynseClassifier<DynseClassifierPruningMetrics> classifier;
		private final double pCorrect;
		
		public ProbCorrectClassifierVA(DynseClassifier<DynseClassifierPruningMetrics> classifier, double pCorrect) {
			this.classifier = classifier;
			this.pCorrect = pCorrect;
		}

		public DynseClassifier<DynseClassifierPruningMetrics> getClassifier() {
			return classifier;
		}

		public double getpCorrect() {
			return pCorrect;
		}
	}
	
	private static final double REJECTION_THRESHOLD = 0.5;
	private static final double DISTANCE_THRESHOULD = 0.1;
	
	private Random random;
	private int kNeighbors;
	private MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>> majorityVoting;
	private List<DynseClassifier<DynseClassifierPruningMetrics>> classifiersUsedInLastClassification;
	
	public APrioriClassificationEngine(int kNeighbors) {
		this.kNeighbors = kNeighbors;
		
		this.majorityVoting = new MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>>();
		random = new Random(); 
	}

	@Override
	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, IMultipleClassifiersCompetence> classifiersMapping,
			NearestNeighbourSearch nnSearch) throws Exception{
		ArrayList<ProbCorrectClassifierVA> classifiersNotRejected = new ArrayList<ProbCorrectClassifierVA>(availableClassifiers.size());
		int idxBestCorrect = -1;
		
		for(DynseClassifier<DynseClassifierPruningMetrics> c:availableClassifiers){
			boolean calculatePCorrectForClassifier = true;
			Instances neighbours = nnSearch.kNearestNeighbours(instance, kNeighbors);
			double[] distances = nnSearch.getDistances();
			double sumDelta = 0.0;
			double sumDividend = 0.0;
			
			for(int i =0; i < neighbours.numInstances(); i++){
				IMultipleClassifiersCompetence classifiersCompetence = classifiersMapping.get(neighbours.instance(i));
				if(distances[i] > 0){
					for(IClassifierCompetence cc : classifiersCompetence.getClassifiersCompetence()){						
						if(cc.getClassifier().equals(c)){
							double posteriori;
							//The MOA framework may not generate the distribution for all possible classes
							if(cc.getCompetenceOnInstance().length > (int)classifiersCompetence.getInstance().classValue())
								posteriori = cc.getCompetenceOnInstance()[(int)classifiersCompetence.getInstance().classValue()];
							else
								posteriori = 0.0;
							double delta = 1.0/distances[i];
							sumDividend += posteriori*delta;
							sumDelta+=delta;
							break;
						}
					}
				}else{//the neighbor is equals to the current instance, we may use any classifier that get it right
					for(IClassifierCompetence cc : classifiersCompetence.getClassifiersCompetence()){
						double [] distribution = cc.getCompetenceOnInstance();
						
						if(Utils.maxIndex(distribution) == classifiersCompetence.getInstance().classValue()){
							classifiersUsedInLastClassification = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>(1);
							classifiersUsedInLastClassification.add(cc.getClassifier());
							return distribution;
						}else{
							calculatePCorrectForClassifier =false;//it does not classify correctly an instance equals to the current one
						}
					}
				}
			}
			if(calculatePCorrectForClassifier){
				Double currentPCorrect = sumDividend/sumDelta;
				
				if(currentPCorrect >= REJECTION_THRESHOLD){
					ProbCorrectClassifierVA probClassifierVA = new ProbCorrectClassifierVA(c, currentPCorrect);
					classifiersNotRejected.add(probClassifierVA);
					
					if(idxBestCorrect ==-1 || currentPCorrect > classifiersNotRejected.get(idxBestCorrect).getpCorrect()){
						idxBestCorrect = classifiersNotRejected.size()-1;
					}
				}
			}
		}
		
		if(idxBestCorrect < 0){//All classifiers rejected by the threshold
			classifiersUsedInLastClassification = availableClassifiers;
			this.decreaseClassifiersFactor(availableClassifiers);
			return this.combineAllClassifiersMajorityVotting(instance, availableClassifiers);
		}
		
		List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
		selectedClassifiers.add(classifiersNotRejected.get(idxBestCorrect).getClassifier());
		
		for(ProbCorrectClassifierVA pc : classifiersNotRejected){
			if(!pc.getClassifier().equals(classifiersNotRejected.get(idxBestCorrect).getClassifier())){
				double distanceProbs = classifiersNotRejected.get(idxBestCorrect).getpCorrect() - pc.getpCorrect();
				if(distanceProbs <= DISTANCE_THRESHOULD)
					selectedClassifiers.add(pc.getClassifier());
			}
		}
		
		DynseClassifier<DynseClassifierPruningMetrics> selectedClassifier;
		if(selectedClassifiers.size() > 1){
			selectedClassifier = selectedClassifiers.get(random.nextInt(selectedClassifiers.size()));
		}else{
			selectedClassifier = selectedClassifiers.get(0);
		}
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : availableClassifiers){
			if(dc != selectedClassifier)
				dc.getDynseClassifierMetrics().decreaseUseageFactor();
		}
		selectedClassifier.getDynseClassifierMetrics().increaseUseageFactor();
		
		classifiersUsedInLastClassification = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>(1);
		classifiersUsedInLastClassification.add(selectedClassifier);
		return selectedClassifier.getVotesForInstance(instance);
	}
	
	private void decreaseClassifiersFactor(List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : classifiers)
			dc.getDynseClassifierMetrics().decreaseUseageFactor();
	}
	
	private double[] combineAllClassifiersMajorityVotting(Instance instance,List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers) 
			throws Exception{
		double[] result = majorityVoting.distributionForInstance(instance, classifiers);
		return result;
	}

	@Override
	public boolean getMapOnlyCorrectClassifiers() {
		return false;
	}
	
	@Override
	public void getClassificationEngineDescription(StringBuilder out) {
		out.append("A Priori\n");
		out.append("Neighbors: ");
		out.append(kNeighbors);
		out.append("\n");
		out.append("Combination rule: ");
		out.append("Combination rule: Majority Voting");
	}
	
	@Override
	public void getClassificationEngineShortDescription(StringBuilder out) {
		out.append("Priori" + kNeighbors);
	}

	@Override
	public void reset() {
		this.classifiersUsedInLastClassification = null;
	}
	
	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> getClassifiersUsedInLastClassification(){
		return classifiersUsedInLastClassification;
	}

	@Override
	public NearestNeighbourSearch createNeighborSearchMethod() {
		return new LinearNNSearch();
	}
}