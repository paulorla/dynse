/*    
*    LCAClassificationEngine.java 
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
import br.ufpr.dynse.classifier.MajorityVoting;
import br.ufpr.dynse.classifier.competence.IClassifierCompetence;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.neighborsearch.LinearClassNNSearch;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;
import moa.core.Utils;

public class LCAClassificationEngine implements IClassificationEngine<IMultipleClassifiersCompetence>{

	private static final long serialVersionUID = 1L;
	
	private int kneighbors;
	private MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>> classifierCombiner;
	private List<DynseClassifier<DynseClassifierPruningMetrics>> classifiersUsedInLastClassification;
	
	public LCAClassificationEngine(int kNeighbors) {
		this.kneighbors = kNeighbors;
		
		this.classifierCombiner = new MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>>();
	}
	
	@Override
	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, IMultipleClassifiersCompetence> competenceMapping, NearestNeighbourSearch nnSearch) throws Exception{
		List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
		int bestAccuracy = 0;
		boolean classifiersAgree = true;
		double agreedClass = Double.NEGATIVE_INFINITY;
		double[] distribution = null;
		
		for(DynseClassifier<DynseClassifierPruningMetrics> c: availableClassifiers){
			distribution = c.getVotesForInstance(instance);
			double classGivenForInstance = Utils.maxIndex(distribution);
			if(classGivenForInstance != agreedClass){
				if(agreedClass == Double.NEGATIVE_INFINITY)
					agreedClass = classGivenForInstance;
				else
					classifiersAgree = false;
			}
			
			Instances neighbors = ((LinearClassNNSearch)nnSearch).kNearestNeighbours(instance, kneighbors, classGivenForInstance);
			int currentAccuracy = 0;
			
			for(int j =0; j < neighbors.numInstances(); j++){
				IMultipleClassifiersCompetence classifiersCompetence = competenceMapping.get(neighbors.instance(j));
				for(IClassifierCompetence cc : classifiersCompetence.getClassifiersCompetence()){
					if(cc.getClassifier().equals(c)){
						currentAccuracy++;
						break;
					}
				}
			}
			if(currentAccuracy > bestAccuracy){
				bestAccuracy = currentAccuracy;
				selectedClassifiers.clear();
				selectedClassifiers.add(c);
			}else{
				if(currentAccuracy > 0 && currentAccuracy == bestAccuracy)
					selectedClassifiers.add(c);
			}
		}
		if(classifiersAgree == true){//se todos concordam, pode-se retornar a resposta de qualquer um
			classifiersUsedInLastClassification = availableClassifiers;
			this.increaseClassifiersFactor(availableClassifiers);
			//return distribution;
			return availableClassifiers.get(0).getVotesForInstance(instance);
		}
		
		double[] result;
		if(selectedClassifiers.size() > 0){
			classifiersUsedInLastClassification = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>(selectedClassifiers);
			result = classifierCombiner.distributionForInstance(instance, selectedClassifiers);
			this.decreaseFactorFromNotSelectedClassifiers(availableClassifiers, selectedClassifiers);
		}else{
			classifiersUsedInLastClassification = availableClassifiers;
			result = classifierCombiner.distributionForInstance(instance, selectedClassifiers);
			this.decreaseClassifiersFactor(availableClassifiers);
		}
		
		return result;
	}
	
	private void decreaseFactorFromNotSelectedClassifiers(List<DynseClassifier<DynseClassifierPruningMetrics>> pool,
			List<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers){
		Set<DynseClassifier<DynseClassifierPruningMetrics>> selectedSet = 
				new HashSet<DynseClassifier<DynseClassifierPruningMetrics>>(selectedClassifiers);
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : pool){
			if(!selectedSet.contains(dc))
				dc.getDynseClassifierMetrics().setUseageFactor(dc.getDynseClassifierMetrics().getUseageFactor()-1.0);
		}
	}
	
	private void decreaseClassifiersFactor(List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : classifiers)
			dc.getDynseClassifierMetrics().decreaseUseageFactor();
	}
	
	private void increaseClassifiersFactor(List<DynseClassifier<DynseClassifierPruningMetrics>> classifiers){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : classifiers)
			dc.getDynseClassifierMetrics().increaseUseageFactor();
	}

	@Override
	public NearestNeighbourSearch createNeighborSearchMethod() {
		return new LinearClassNNSearch();
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
		out.append("LCA\n");
		out.append("Neighbors: ");
		out.append(kneighbors);
		out.append("\n");
		out.append("Combination rule: Majority Voting");
	}
	
	@Override
	public void getClassificationEngineShortDescription(StringBuilder out) {
		out.append("LCA" + kneighbors);
	}

	@Override
	public void reset() {
		this.classifiersUsedInLastClassification = null;		
	}
}