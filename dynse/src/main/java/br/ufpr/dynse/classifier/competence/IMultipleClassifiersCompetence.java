/*    
*    IMultipleClassifiersCompetence.java 
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

import java.util.Set;

import com.yahoo.labs.samoa.instances.Instance;

public interface IMultipleClassifiersCompetence{
	
	public void setInstance(Instance instance);
	public Instance getInstance();
	public void setInstanceCount(int instanceCount);
	public int getInstanceCount();//if there is more instance that is equal to this one
	public Set<IClassifierCompetence> getClassifiersCompetence();
	public void setClassifiersCompetence(Set<IClassifierCompetence> classifiersCompetence);
}