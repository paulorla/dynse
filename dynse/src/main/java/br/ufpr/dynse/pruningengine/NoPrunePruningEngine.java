/*    
*    NoPrunePruningEngine.java 
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

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.classifier.DynseClassifier;

public class NoPrunePruningEngine extends AbstractDefaultPruningEngine{
	
	private final List<DynseClassifier<DynseClassifierPruningMetrics>> defaultReturn;
	
	public NoPrunePruningEngine() {
		defaultReturn = new ArrayList<DynseClassifier<DynseClassifierPruningMetrics>>();
	}
	
	@Override
	public void getPrunningEngineDescription(StringBuilder out) {
		out.append("No Prunning\n");
	}
	
	@Override
	public void getPrunningEngineShortDescription(StringBuilder out) {
		out.append("NoPrun");
	}

	@Override
	public List<DynseClassifier<DynseClassifierPruningMetrics>> pruneClassifiers(DynseClassifier<DynseClassifierPruningMetrics> newClassifier,
			List<DynseClassifier<DynseClassifierPruningMetrics>> currentPool, List<Instance> accuracyEstimationInstances)
			throws Exception {
		return defaultReturn;
	}
}