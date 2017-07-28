/*    
*    UFPRLearningCurveUtils.java 
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
package br.ufpr.dynse.util;

import java.util.List;

import br.ufpr.dynse.core.UFPRLearningCurve;
import br.ufpr.dynse.exception.StatisticDoesNotExistException;
import moa.core.Measurement;

public class UFPRLearningCurveUtils {

	private final String STR_RESULTADO;
	private final String STR_MIN_ACERTOS;
	private final String STR_MED_ACERTOS;
	private final String STR_TOT_INST;
	
	private static final Double VALOR_AUSENTE_DEFAULT = 0.0; 

	// private DecimalFormat formataNumeros = new DecimalFormat("#,###.00");

	public UFPRLearningCurveUtils() {
		this("res", "min", "med", "totInst");
	}

	public UFPRLearningCurveUtils(String strResultado, String strMinAcertos, String strMediaAcertos,
			String strTotInst) {
		STR_RESULTADO = strResultado;
		STR_MIN_ACERTOS = strMinAcertos;
		STR_MED_ACERTOS = strMediaAcertos;
		STR_TOT_INST = strTotInst;
	}

	// formato para odt
	// protected void imprimirAcuraciaLearningCurve(UFPRLearningCurve lc){
	// System.out.println("batch\tInstancias Classificadas\tTaxa Acertos em %");
	// double numTotalInstancias = 0.0;
	// double numTotalAcertos = 0.0;
	// double taxaMinimaAcertos = Double.MAX_VALUE;
	//
	// for(int i =0; i < lc.numEntries(); i ++){
	// System.out.println(i + "\t" + lc.getInstanciasClassificadas(i) + "\t" +
	// formataNumeros.format(lc.getTaxaAcertos(i)));
	// if(lc.getTaxaAcertos(i) < taxaMinimaAcertos)
	// taxaMinimaAcertos = lc.getTaxaAcertos(i);
	//
	// numTotalInstancias += lc.getInstanciasClassificadas(i);
	// numTotalAcertos += lc.getInstanciasClassificadas(i) *
	// (lc.getTaxaAcertos(i)/100.0);
	// }
	//
	// System.out.println("Taxa média de acertos: " +
	// (numTotalAcertos/numTotalInstancias)*100);
	// System.out.println("Taxa mínima de acertos: " + taxaMinimaAcertos);
	// }
	public String strMainStatisticsMatlab(UFPRLearningCurve lc) {
		return this.strMainStatisticsMatlabFormat(lc, "");
	}

	// formato para Matlab
	public String strMainStatisticsMatlabFormat(UFPRLearningCurve lc, String posfixo) {
		double numTotalInstancias = 0.0;
		double numTotalAcertos = 0.0;
		double taxaMinimaAcertos = Double.MAX_VALUE;

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append(STR_RESULTADO + posfixo + "=[");
		for (int i = 0; i < lc.numEntries(); i++) {
			if (i == 0)
				strBuilder.append(i + "," + lc.getAccuracy(i));
			else
				strBuilder.append(";" + i + "," + lc.getAccuracy(i));
			if (lc.getAccuracy(i) < taxaMinimaAcertos)
				taxaMinimaAcertos = lc.getAccuracy(i);

			numTotalInstancias += lc.getNumClassifierInstances(i);
			numTotalAcertos += lc.getNumClassifierInstances(i) * (lc.getAccuracy(i) / 100.0);
		}
		strBuilder.append("];\n");

		strBuilder.append(STR_MED_ACERTOS + posfixo + "= " + (numTotalAcertos / numTotalInstancias) * 100 + ";\n");
		strBuilder.append(STR_MIN_ACERTOS + posfixo + "= " + taxaMinimaAcertos + ";\n");
		strBuilder.append(STR_TOT_INST + posfixo + "= " + numTotalInstancias + ";\n");

		return strBuilder.toString();
	}
	
	public String strStatisticsMatlabFormat(String statisticID, UFPRLearningCurve lc) throws StatisticDoesNotExistException{
		return this.strStatisticsMatlabFormat(statisticID, lc, "");
	}
	
	// formato para Matlab
	public String strStatisticsMatlabFormat(String statisticID, UFPRLearningCurve lc, String posfixo) throws StatisticDoesNotExistException{
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append(statisticID + posfixo + "=[");
		if(lc.numEntries() < 1){
			strBuilder.append("];");
			return strBuilder.toString();
		}
		
		int index =0;
		while(!lc.getMeasurementName(index).equals(statisticID)){
			index++;
			if(index >= lc.getNumMeasurements())
				throw new StatisticDoesNotExistException(statisticID, statisticID + " does not exist in the learningCurve");
		}
		
		if(lc.getNumMeasurements(0) > index)
			strBuilder.append(0 + "," + lc.getMeasurement(0, index));
		else
			strBuilder.append(0 + "," + VALOR_AUSENTE_DEFAULT);
		for (int i = 1; i < lc.numEntries(); i++) {
			if(lc.getNumMeasurements(i) > index)
				strBuilder.append(";" + i + "," + lc.getMeasurement(i, index));
			else
				strBuilder.append(";" + i + "," + VALOR_AUSENTE_DEFAULT);
		}
		strBuilder.append("];");

		return strBuilder.toString();
	}

	public UFPRLearningCurve averageResults(List<UFPRLearningCurve> learningCurves) throws Exception {
		UFPRLearningCurve curvaMedia = new UFPRLearningCurve(learningCurves.get(0).getOrderingMeasurementName());
		
		for (int i = 0; i < learningCurves.get(0).numEntries(); i++) {
			UFPRLearningCurve lc = learningCurves.get(0);

			double taxaAcertosMedia = lc.getAccuracy(i);
			double instanciasClassificadas = lc.getNumClassifierInstances(i);
			double demaisMedicoes[] = new double[lc.getNumMeasurements()];
			Measurement measurements[] = new Measurement[lc.getNumMeasurements()];

			for (int idxMedicao = 0; idxMedicao < lc.getNumMeasurements(); idxMedicao++) {
				if(lc.getNumMeasurements(i) > idxMedicao)
					demaisMedicoes[idxMedicao] = lc.getMeasurement(i, idxMedicao);
				else
					demaisMedicoes[idxMedicao] = VALOR_AUSENTE_DEFAULT;
			}

			for (int idxLC = 1; idxLC < learningCurves.size(); idxLC++) {
				lc = learningCurves.get(idxLC);
				if (lc.getNumClassifierInstances(i) != instanciasClassificadas)
					throw new Exception("A média dos resultados não pode ser feita. "
							+ "Os batches de teste para as diferentes execuções possuem diferentes números de instâncias.");
				taxaAcertosMedia += lc.getAccuracy(i);
				for (int idxMedicao = 0; idxMedicao < lc.getNumMeasurements()
						&& idxMedicao < demaisMedicoes.length; idxMedicao++) {
					if(lc.getNumMeasurements(i) > idxMedicao)
						demaisMedicoes[idxMedicao] += lc.getMeasurement(i, idxMedicao);
					else
						demaisMedicoes[idxMedicao] += VALOR_AUSENTE_DEFAULT;
				}
			}

			for (int idxMedicao = 0; idxMedicao < lc.getNumMeasurements()
					&& idxMedicao < demaisMedicoes.length; idxMedicao++) {
				measurements[idxMedicao] = new Measurement(lc.getMeasurementName(idxMedicao),
						demaisMedicoes[idxMedicao] / learningCurves.size());
			}
			Measurement medicaoTaxaAcertosMedia = new Measurement("Taxa de acertos média",
					taxaAcertosMedia / learningCurves.size());
			Measurement numInstanciasClassificadas = new Measurement("Instâncias Classificadas",
					instanciasClassificadas);

			curvaMedia.insertEntry(medicaoTaxaAcertosMedia, numInstanciasClassificadas, measurements);
		}
		return curvaMedia;
	}
}