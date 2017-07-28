/*    
*    Constants.java 
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
package br.ufpr.dynse.constant;

import java.math.BigDecimal;

public class Constants {	
	public static final int NUM_INST_TRAIN_CLASSIFIER_VIRTUAL_TEST = 50;
	public static final int NUM_INST_TEST_CLASSIFIER_VIRTUAL_TEST = 50;
	
	public static final int NUM_INST_TRAIN_GAUSS_POLIKAR= 20;
	public static final int NUM_TEST_TRAIN_GAUSS_POLIKAR= 1024;
	
	public static final int NUM_INST_TRAIN_TEST_SEA = 250;
	
	public static final int NUM_INST_TRAIN_CLASSIFIER_ELEC = 240;
	public static final int NUM_INST_TEST_CLASSIFIER_ELEC = 240;//equivalente a 5 dias
	
	public static final int NUM_INST_TRAIN_CLASSIFIER_NEBRASKA = 90;
	public static final int NUM_INST_BATCH_NEBRASKA = 30;
	public static final int NUM_INST_TEST_CLASSIFIER_NEBRASKA = NUM_INST_BATCH_NEBRASKA;
	
	public static final int NUM_INST_TRAIN_CLASSIFIER_FOREST = 200;
	public static final int NUM_INST_TEST_CLASSIFIER_FOREST = NUM_INST_TRAIN_CLASSIFIER_FOREST*10;
	
	public static final int NUM_INST_TRAIN_CLASSIFIER_CHECKERBOARD = 25;
	public static final int NUM_INST_BATCH_CHECKERBOARD = 25;
	public static final int NUM_INST_TEST_CLASSIFIER_CHECKERBOARD = 1024;
	
	public static final int NUM_DIAS_ACUMULADOS_TREINAMENTO_PKLOT = 2;
	public static final int NUM_INSTANCIAS_CADA_CLASSE_TREINA_PKLOT_DIA = 25;
	
	public static final int DEFAULT_ROUNDING = BigDecimal.ROUND_HALF_EVEN;
	public static final BigDecimal BIG_DECIMAL_100 = new BigDecimal(100);
}