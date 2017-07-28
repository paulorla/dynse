/*    
*    IPruningEngine.java 
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
package br.ufpr.dynse.pruningengine;

import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;

public interface IPruningEngine<T extends DynseClassifierPruningMetrics> {
	//returns the classifiers that must be removed from the pool
	public List<DynseClassifier<T>> pruneClassifiers(DynseClassifier<T> newClassifier, List<DynseClassifier<T>> currentPool,
			List<Instance> accuracyEstimationInstances) throws Exception;
	
	public void meassureClassifier(DynseClassifier<T> classifier) throws Exception;
	
	public void getPrunningEngineDescription(StringBuilder out);
	
	public void getPrunningEngineShortDescription(StringBuilder out);
}