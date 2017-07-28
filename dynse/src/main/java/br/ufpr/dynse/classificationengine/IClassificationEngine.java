/*    
*    IClassificationEngine.java 
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.classifier.competence.IMultipleClassifiersCompetence;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;
import moa.classifiers.lazy.neighboursearch.NearestNeighbourSearch;

public interface IClassificationEngine<U extends IMultipleClassifiersCompetence> extends Serializable{

	public double[] classify(Instance instance, List<DynseClassifier<DynseClassifierPruningMetrics>> availableClassifiers,
			Map<Instance, U> competenceMappings, NearestNeighbourSearch nnSearch) throws Exception;
	
	public NearestNeighbourSearch createNeighborSearchMethod();
	
	public boolean getMapOnlyCorrectClassifiers();
	
	public List<DynseClassifier<DynseClassifierPruningMetrics>> getClassifiersUsedInLastClassification();
	
	public void getClassificationEngineDescription(StringBuilder out);
	
	public void getClassificationEngineShortDescription(StringBuilder out);
	
	public void reset();
}