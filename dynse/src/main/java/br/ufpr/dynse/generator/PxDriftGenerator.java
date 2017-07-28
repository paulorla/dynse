/*    
*    PxDriftGenerator.java 
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
package br.ufpr.dynse.generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Range;

import br.ufpr.dynse.neighborsearch.LinearIndexNNSearch;
import moa.core.Example;
import moa.core.InstanceExample;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;

public class PxDriftGenerator extends AbstractOptionHandler implements InstanceStream {

	private static final long serialVersionUID = 1L;

	private LinearIndexNNSearch nnSearch;

	protected int trainSize;
	protected int generatedInstances;
	protected List<Integer> idxTrainInstances;
	protected Instances instances;

	private int testSize;
	private int generatedInstancesCurrentStep;
	private String pathArff;
	private Iterator<Integer> trainIterator;

	protected Random rnd = new Random();

	public PxDriftGenerator(int trainSize, int testSize, String pathArff) {
		this.trainSize = trainSize;
		this.testSize = testSize;
		this.pathArff = pathArff;
		idxTrainInstances = new LinkedList<Integer>();
	}

	@Override
	public void restart() {
		try{
			this.idxTrainInstances.clear();
			this.instances = carregarArquivoExtracao(pathArff);
			generatedInstances = 0;
			nnSearch = new LinearIndexNNSearch(this.instances);
			this.beginTrainStage();
		}catch(Exception e){
			throw new RuntimeException("Error Loading extraction File!", e);
		}
	}
	
	private void beginTestStage(){
		generatedInstancesCurrentStep = 0;
		//if we remove i, then j, where j>i, the j index were modifier by i deletion. The reverse order solves this
		Collections.sort(idxTrainInstances, Collections.reverseOrder());
		for(Integer i : idxTrainInstances){
			instances.delete(i);
		}
	}
	
	protected void beginTrainStage(){
		generatedInstancesCurrentStep = 0;
		this.buildTrainSet();
		trainIterator = idxTrainInstances.iterator();
	}
	
	protected void buildTrainSet(){
		int idxInitialInstance = rnd.nextInt(this.instances.size());
		try{
			idxTrainInstances.clear();
			idxTrainInstances.add(idxInitialInstance);
			int[] idxNeighbors = nnSearch.kNearestNeighboursIndexes(instances.instance(idxInitialInstance), trainSize-1);
			for(int idx : idxNeighbors)
				idxTrainInstances.add(idx);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
		this.restart();
	}
	
	private Instances carregarArquivoExtracao(String pathArff) throws Exception{
		BufferedReader reader = null;
		
		try{
			reader = new BufferedReader(new FileReader(pathArff));
			Instances instancias = new Instances(reader,new Range("-1"));
			
			for ( ; instancias.readInstance(null); ); //loop to read all instances from file
			
			instancias.setClassIndex(instancias.numAttributes()-1);
			
			return instancias;
		}finally{
			if(reader!=null)
				reader.close();
		}
	}
	
	@Override
	public Example<Instance> nextInstance(){
		Example<Instance> retorno;
		if(trainIterator.hasNext()){
			retorno = new InstanceExample(instances.instance(trainIterator.next()));
			generatedInstancesCurrentStep++;
			if(!trainIterator.hasNext())
				this.beginTestStage();
		}else{
			int idx = rnd.nextInt(instances.numInstances());
			retorno = new InstanceExample(instances.get(idx));
			instances.delete(idx);
			if(generatedInstancesCurrentStep == testSize && instances.numInstances() > 0)
				this.beginTrainStage();
		}
		generatedInstancesCurrentStep++;
		generatedInstances++;
		return retorno;
	}

	@Override
	public long estimatedRemainingInstances() {
		return instances.numInstances() - generatedInstances;
	}

	@Override
	public InstancesHeader getHeader() {
		return new InstancesHeader(this.instances);
	}

	@Override
	public boolean hasMoreInstances() {
		return instances.numInstances() > 0;
	}

	@Override
	public boolean isRestartable() {
		return true;
	}

	@Override
	public void getDescription(StringBuilder sb, int indent) {
		
	}
}