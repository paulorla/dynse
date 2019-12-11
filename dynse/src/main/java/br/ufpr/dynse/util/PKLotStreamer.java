package br.ufpr.dynse.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import weka.core.Instances;

public class PKLotStreamer {
	private static final int MIN_INSTANCES_EACH_CLASS = 50;

	public int randomInstance(List<String> lista) {
		int rnd = new Random().nextInt(lista.size());
		return rnd;
	
	}

	public String prepareStream(String filePath) {
        String line = null;
		File[] foldersEstacionamentos = new File(filePath).listFiles(File::isDirectory);
		
		List<String> dataSet = new ArrayList<String>();
		
		for(File estacionamento : foldersEstacionamentos){
			File[] dias = new File(estacionamento.getAbsolutePath()).listFiles(File::isDirectory);
			for(File dia : dias){
				File[] arff = new File(dia.getAbsolutePath()).listFiles(File::isFile);
				
				if(arff.length != 1)
					throw new RuntimeException("ERRO: " + dia.getAbsolutePath() + "Não existem arquivos de extraçao ou existem mais de um arquivo.");
				String path = arff[0].getAbsolutePath();
				
				
				if(!path.endsWith(".arff"))
					throw new RuntimeException("ERRO: " + dia.getAbsolutePath() + "O arquivo existente não tem a extensão .arff"); 
				
				
				
				
				
				try {
					FileReader fileReader = new FileReader(path);
					
					BufferedReader bufferedReader =  new BufferedReader(fileReader);
					
					//Jogar as instancias em listas
					
					List<String> positivos = new ArrayList<String>();
					List<String> negativos = new ArrayList<String>();
					
					List<String> positivosSample = new ArrayList<String>();
					List<String> negativosSample = new ArrayList<String>();
					
					while((line = bufferedReader.readLine()) != null) {
						if(line.endsWith("-1")) {
							negativos.add(line);
						}
						if(line.endsWith("+1")) {
							positivos.add(line);
						}
			        }   
					if(positivos.size()<MIN_INSTANCES_EACH_CLASS || negativos.size()<MIN_INSTANCES_EACH_CLASS)
						throw new RuntimeException("ERRO: " + dia.getAbsolutePath() + "O arquivo existente não tem o número mínimo de instancias"); 
					
					int rnd;
					for(int i =0;i<MIN_INSTANCES_EACH_CLASS;i++) {
						rnd =randomInstance(positivos);
						positivosSample.add(positivos.get(rnd));
						positivos.remove(rnd);
						
						rnd =randomInstance(negativos);
						negativosSample.add(negativos.get(rnd));
						negativos.remove(rnd);
					}
					
					List<String> entradas = new ArrayList<String>();
					
					//embaralhar as entradas
					entradas.addAll(negativosSample);
					entradas.addAll(positivosSample);
					Collections.shuffle(entradas); 

					dataSet.addAll(entradas);
					
					bufferedReader.close();         
					 
				} catch(FileNotFoundException ex) {
		            System.out.println("ERRO: Não foi possível abrir '" + path + "'");                
		        } catch(IOException ex) {
		            System.out.println("ERRO: Erro lendo o arquivo '" + path + "'");                  
		         }
				
				
			}
			
		}
        String fileName = new String();
        fileName = filePath+"/PKLot.arff";
        
        //escrevendo arquivo de saída
        try {

        	FileWriter fileWriter = new FileWriter(fileName);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            //cabeçalho  
            bufferedWriter.write("@RELATION PKLot");
            bufferedWriter.newLine();
            bufferedWriter.newLine();
            
			for(int i =1;i<=59;i++) {
			    bufferedWriter.write("@ATTRIBUTE "+i+" NUMERIC");
	            bufferedWriter.newLine();
			}
			 bufferedWriter.write("@ATTRIBUTE class {-1,+1}");
	         bufferedWriter.newLine();
	         bufferedWriter.newLine();
	         bufferedWriter.write("@DATA");
	         bufferedWriter.newLine();
	         bufferedWriter.newLine();
           
	        //instancias
            for(String line1 : dataSet){
            	bufferedWriter.write(line1);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            
        }catch(IOException ex) {
            System.out.println("ERRO: Erro escrevendo o arquivo '" + fileName + "'");                  
         }
        return fileName;
	}
}
