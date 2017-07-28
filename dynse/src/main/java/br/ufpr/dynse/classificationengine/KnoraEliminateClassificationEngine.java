/*    
*    KnoraEliminateClassificationEngine.java 
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
import java.util.HashMap;
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
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.lazy.neighboursearch.LinearNNSearch;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;

public class KnoraEliminateClassificationEngine implements IClassificationEngine<IMultipleClassifiersCompetence>{

	private static final long serialVersionUID = 1L;
	
	private int kneighbors;
	private int slackVariable;
	private MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>> classifierCombiner;
	private List<DynseClassifier<DynseClassifierPruningMetrics>> classifiersUsedInLastClassification;
	
	public KnoraEliminateClassificationEngine(int kVizinhos, int slackVariable) {
		this.kneighbors = kVizinhos;
		this.slackVariable = slackVariable;
		
		this.classifierCombiner = new MajorityVoting<DynseClassifier<DynseClassifierPruningMetrics>>();
	}

	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, IMultipleClassifiersCompetence> classifiersMapping,
			NearestNeighbourSearch nnSearch) throws Exception{
		
		Map<DynseClassifier<DynseClassifierPruningMetrics>, Integer> hitMapping = new HashMap<DynseClassifier<DynseClassifierPruningMetrics>, Integer>();
		Set<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers = new HashSet<DynseClassifier<DynseClassifierPruningMetrics>>();
		
		Instances neighbours = nnSearch.kNearestNeighbours(instance, kneighbors);
		
		if(neighbours.size() < kneighbors && nnSearch.getInstances().numInstances() != neighbours.size())
			throw new Exception("There was " + neighbours.size() + " instances found (less than the specified). It indicates an error in the algorithm.");
		
		int numNeighborsCorrect = neighbours.size() - slackVariable;
		
		for(int j =0; j < neighbours.size(); j++){
			IMultipleClassifiersCompetence competences = classifiersMapping.get(neighbours.instance(j));
			for(IClassifierCompetence competence : competences.getClassifiersCompetence()){
				Integer numHits = hitMapping.get(competence.getClassifier());
				if(numHits == null)
					numHits = 1;
				else
					numHits++;
				hitMapping.put(competence.getClassifier(), numHits);
				if(numHits >= numNeighborsCorrect){
					if(!selectedClassifiers.contains(competence.getClassifier())){
						selectedClassifiers.add(competence.getClassifier());
						competence.getClassifier().getDynseClassifierMetrics().setUseageFactor(
								competence.getClassifier().getDynseClassifierMetrics().getUseageFactor() + 1.0);
					}
				}
			}
		}
		
		if(selectedClassifiers.size() < 1){
			numNeighborsCorrect--;
			while(numNeighborsCorrect > 0){
				for (Map.Entry<DynseClassifier<DynseClassifierPruningMetrics>, Integer> entry : hitMapping.entrySet())
				{
				    if(entry.getValue() >= numNeighborsCorrect){
				    	if(!selectedClassifiers.contains(entry.getKey())){
							selectedClassifiers.add(entry.getKey());
							entry.getKey().getDynseClassifierMetrics().setUseageFactor(
									entry.getKey().getDynseClassifierMetrics().getUseageFactor() + 1.0);
						}
				    	
				    	selectedClassifiers.add(entry.getKey());
				    }
				}
				if(selectedClassifiers.size() > 0)
					break;
				numNeighborsCorrect--;
			}
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
			Set<DynseClassifier<DynseClassifierPruningMetrics>> selectedClassifiers){
		for(DynseClassifier<DynseClassifierPruningMetrics> dc : pool){
			if(!selectedClassifiers.contains(dc))
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
		out.append("KNORA Eliminate\n");
		out.append("Neighbors: ");
		out.append(kneighbors);
		out.append("\n");
		out.append("Slack: ");
		out.append(slackVariable);
		out.append("\n");
		out.append("Combination rule: Majority Voting");
	}
	
	@Override
	public void getClassificationEngineShortDescription(StringBuilder out) {
		out.append("Elim" + kneighbors + "_"+ slackVariable);
	}

	@Override
	public void reset() {
		classifiersUsedInLastClassification  = null;
	}
}