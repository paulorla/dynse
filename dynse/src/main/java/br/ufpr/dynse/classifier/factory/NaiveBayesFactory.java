/*    
*    NaiveBayesFactory.java 
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
package br.ufpr.dynse.classifier.factory;

import moa.classifiers.Classifier;
import moa.classifiers.bayes.NaiveBayes;

public class NaiveBayesFactory extends AbstractClassifierFactory {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public Classifier createClassifier() throws Exception {
		NaiveBayes classifier = new NaiveBayes();
		classifier.prepareForUse();
		return classifier;
	}
	
	@Override
	public void getDescription(StringBuilder out) {
		out.append("Naive Bayes Factory");
	}
	
	@Override
	public void getShortDescription(StringBuilder out) {
		out.append("NB");
	}
}
