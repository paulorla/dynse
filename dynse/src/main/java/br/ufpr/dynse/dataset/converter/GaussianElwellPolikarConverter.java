package br.ufpr.dynse.dataset.converter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//converts the original Elwell and Polikar dataset to a format that can be used in the MOA
public class GaussianElwellPolikarConverter {
	
	private static final String header = "@relation RotatingCheckerboard\n\n"
			+ "@attribute gridX numeric\n"
			+ "@attribute gridY numeric\n"
			+ "@attribute class {1,2,3,4}\n\n"
			+ "@data\n";
	
	public static void parseTrainFile(String csvInstances, String csvLabels,String outFilePath){
		BufferedReader readerInstances = null;
		BufferedReader readerLabels = null;
		FileWriter writer = null;
		try {
			readerInstances = Files.newBufferedReader(Paths.get(csvInstances));
			readerLabels = Files.newBufferedReader(Paths.get(csvLabels));
			writer = new FileWriter(outFilePath);
			
			writer.write(header);
		
		    String line = null;
		    
		    while ((line = readerInstances.readLine()) != null) {
		        writer.write(line);
		        writer.write("," + readerLabels.readLine());
		        writer.write("\n");
		    }
		} catch (IOException e) {
			try{
				if(readerInstances != null)
					readerInstances.close();
				if(readerLabels != null)
					readerLabels.close();
				if(writer != null)
					writer.close();
			}catch(IOException ioe){
				throw new RuntimeException(ioe);
			}
			throw new RuntimeException(e);
		}finally {
			try{
			if(readerInstances != null)
				readerInstances.close();
			if(readerLabels != null)
				readerLabels.close();
			if(writer != null)
				writer.close();
			}catch(IOException ioe){
				throw new RuntimeException(ioe);
			}
		}
	}
	
	//The original dataset does not contain the labels, just the priors for each dataset
	public static void parseTestFile(String csvInstances, String outFilePath){
		BufferedReader readerInstances = null;
		FileWriter writer = null;
		try {
			readerInstances = Files.newBufferedReader(Paths.get(csvInstances));
			writer = new FileWriter(outFilePath);
			
			writer.write(header);
		
		    String line = null;
		    
		    while ((line = readerInstances.readLine()) != null) {
		        writer.write(line);
		        writer.write(",?\n");
		    }
		    
		} catch (IOException e) {
			try{
				if(readerInstances != null)
					readerInstances.close();
				if(writer != null)
					writer.close();
			}catch(IOException ioe){
				throw new RuntimeException(ioe);
			}
			throw new RuntimeException(e);
		}finally {
			try{
			if(readerInstances != null)
				readerInstances.close();
			if(writer != null)
				writer.close();
			}catch(IOException ioe){
				throw new RuntimeException(ioe);
			}
		}
	}
	
	public static List<Double[]> readPriorsTest(String csvPriors) throws ParseException{
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		DecimalFormat formataNumeros = (DecimalFormat)nf;
		
		BufferedReader readerPriors = null;
		List<Double[]> retorno = new ArrayList<Double[]>(300);
		
		try {
			readerPriors = Files.newBufferedReader(Paths.get(csvPriors));		
		    String line = null;
		    
		    while ((line = readerPriors.readLine()) != null) {
		        String[] split = line.split(",");
		        Double[] valores = new Double[split.length];
		        for(int i =0; i < split.length; i++){
		        	valores[i] = formataNumeros.parse(split[i]).doubleValue();
		        }
		        retorno.add(valores);
		    }
		}catch (IOException e) {
			throw new RuntimeException(e);
		}finally {
			try{
			if(readerPriors != null)
				readerPriors.close();
			}catch(IOException ioe){
				throw new RuntimeException(ioe);
			}
		}
		
		return retorno;
	}
}