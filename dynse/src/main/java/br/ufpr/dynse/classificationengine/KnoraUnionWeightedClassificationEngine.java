/*    
*    KnoraUnionWeightedClassificationEngine.java 
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.WeightedMajorityVoting;
import br.ufpr.dynse.classifier.competence.IClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.lazy.neighboursearch.LinearNNSearch;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;

public class KnoraUnionWeightedClassificationEngine implements IClassificationEngine<IMultipleClassifiersCompetence>{

	private static final long serialVersionUID = 1L;
	
	private int kNeighbors;
	private WeightedMajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>> classifierCombiner;
	private List<DynseClassifier<DynseClassifierPruningMetrics>> classifiersUsedInLastClassification;
	
	public KnoraUnionWeightedClassificationEngine(int kNeighbors) {
		this.kNeighbors = kNeighbors;
		
		this.classifierCombiner = new WeightedMajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>>();
	}

	@Override
	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, IMultipleClassifiersCompetence> classifiersMapping,
			NearestNeighbourSearch nnSearch) throws Exception{
		
			Instances neighbours = nnSearch.kNearestNeighbours(instance, kNeighbors);
			double[] distances = nnSearch.getDistances();
			
			List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
			List<Double> selectedClassifiersWeights = new ArrayList<Double>(selectedClassifiers.size());
			for(int j =0; j < neighbours.numInstances(); j++){
				IMultipleClassifiersCompetence classifiersCompetence = classifiersMapping.get(neighbours.instance(j));
				
				for(IClassifierCompetence cc:classifiersCompetence.getClassifiersCompetence()){
					selectedClassifiers.add(cc.getClassifier());
					cc.getClassifier().getDynseClassifierMetrics().increaseUseageFactor();
					selectedClassifiersWeights.add(1.0/distances[j]);//The weight as the inverse of the distance
				}
			}
			
			
			double[] result;
			if(selectedClassifiers.size() > 0){
				classifiersUsedInLastClassification = selectedClassifiers;
				this.decreaseFactorFromNotSelectedClassifiers(availableClassifiers, selectedClassifiers);
				result = classifierCombiner.distributionForInstance(instance, selectedClassifiers, selectedClassifiersWeights);
			}else{
				selectedClassifiersWeights = new ArrayList<Double>(availableClassifiers.size());
				
				for(int i =0; i < availableClassifiers.size();i++)
					selectedClassifiersWeights.add(1.0);
				classifiersUsedInLastClassification = availableClassifiers;
				decreaseClassifiersFactor(availableClassifiers);
				
				result = classifierCombiner.distributionForInstance(instance, availableClassifiers, selectedClassifiersWeights);
			}
			
			return result;
	}
	
	private void decreaseFactorFromNotSelectedClassifiers(List<DynseClassifier<DynseClassifierPruningMetrics>> pool, List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers){
		Set<DynseClassifier<DynseClassifierPruningMetrics>> selectedSet = new HashSet<DynseClassifier<DynseClassifierPruningMetrics>>(selectedClassifiers);
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : pool){
			if(!selectedSet.contains(dc))
				dc.getDynseClassifierMetrics().setUseageFactor(dc.getDynseClassifierMetrics().getUseageFactor()-1.0);
		}
	}
	
	private void decreaseClassifiersFactor(List<DynseClassifier<DynseClassifierPruningMetrics>> pool){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : pool)
				dc.getDynseClassifierMetrics().setUseageFactor(dc.getDynseClassifierMetrics().getUseageFactor()-1.0);
	}
	
	@Override
	public NearestNeighbourSearch createNeighborSearchMethod() {
		return new LinearNNSearch();
	}
	
	@Override
	public boolean getMapOnlyCorrectClassifiers() {
		return true;
	}
	
	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> getClassifiersUsedInLastClassification(){
		return classifiersUsedInLastClassification;
	}

	@Override
	public void getClassificationEngineDescription(StringBuilder out) {
		out.append("Knora Union Weighted\n");
		out.append("Neighbors: ");
		out.append(kNeighbors);
		out.append("\n");
		out.append("Combination rule: ");
		out.append("Combination rule: Majority Voting");
	}
	
	@Override
	public void getClassificationEngineShortDescription(StringBuilder out) {
		out.append("UnionW" + kNeighbors);
	}

	@Override
	public void reset() {
		this.classifiersUsedInLastClassification = null;
	}
}