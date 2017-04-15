package br.ufpr.dynse.constant;

import java.math.BigDecimal;

public class Constants {
	public static final int NUM_INST_TRAIN_CLASSIFIER_STAGGER = 20;
	public static final int NUM_INST_TEST_CLASSIFIER_STAGGER = 200;
	public static final int NUM_TOTAL_INST_TEST_STAGGER = (NUM_INST_TRAIN_CLASSIFIER_STAGGER + 
			NUM_INST_TEST_CLASSIFIER_STAGGER)*40;
	
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