/*    
*    ClassifierCompetence.java 
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
package br.ufpr.dynse.classifier.competence;

import java.util.Arrays;

import br.ufpr.dynse.classifier.DynseClassifier;
import br.ufpr.dynse.pruningengine.DynseClassifierPruningMetrics;

public class ClassifierCompetence implements IClassifierCompetence{
	
	private DynseClassifier<DynseClassifierPruningMetrics> classifier;
	private double[] competenceOnInstance;
	
	public ClassifierCompetence() {
	}
	
	public ClassifierCompetence(DynseClassifier<DynseClassifierPruningMetrics> classifier, double[] competenceOnInstance) {
		this.classifier = classifier;
		this.competenceOnInstance = competenceOnInstance;
	}

	@Override
	public DynseClassifier<DynseClassifierPruningMetrics> getClassifier() {
		return classifier;
	}
	
	@Override
	public void setClassifier(DynseClassifier<DynseClassifierPruningMetrics> classifier) {
		this.classifier = classifier;
	}
	
	@Override
	public double[] getCompetenceOnInstance() {
		return competenceOnInstance;
	}
	
	@Override
	public void setCompetenceOnInstance(double[] competenceOnInstance) {
		this.competenceOnInstance = competenceOnInstance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + Arrays.hashCode(competenceOnInstance);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassifierCompetence other = (ClassifierCompetence) obj;
		if (classifier == null) {
			if (other.classifier != null)
				return false;
		} else if (!classifier.equals(other.classifier))
			return false;
		if (!Arrays.equals(competenceOnInstance, other.competenceOnInstance))
			return false;
		return true;
	}
}