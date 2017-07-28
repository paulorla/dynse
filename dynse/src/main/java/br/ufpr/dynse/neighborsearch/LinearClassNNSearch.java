/*    
*    LinearClassNNSearch.java 
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
package br.ufpr.dynse.neighborsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import moa.classifiers.lazy.neighboursearch.LinearNNSearch;

public class LinearClassNNSearch extends LinearNNSearch {

	private static final long serialVersionUID = 1L;

	protected double[] m_Distances;

	protected boolean m_SkipIdentical = false;
	private double neighborsClass = 0.0;
	private Map<Integer, Instances> classMap;

	public LinearClassNNSearch() {
		super();
	}

	public LinearClassNNSearch(Instances insts) {
		super(insts);
		this.rebuildClassMap();
	}

	private void rebuildClassMap(){
		classMap = new HashMap<Integer, Instances>();
		Map<Integer, List<Instance>> instancesPerClassMap = new HashMap<Integer, List<Instance>>();
		for (int i = 0; i < super.getInstances().numInstances(); i++) {
			Instance instance = super.getInstances().get(i);
			Integer classInstance = (int)instance.classValue();
			List<Instance> mapValue = instancesPerClassMap.get(classInstance);
			if(mapValue == null){
				mapValue = new ArrayList<Instance>();
				instancesPerClassMap.put(classInstance, mapValue);
			}
			mapValue.add(instance);
		}
		for(Map.Entry<Integer, List<Instance>> entry : instancesPerClassMap.entrySet()){
			Instances instances = new Instances(m_Instances, entry.getValue().size());
			for(int i =0; i < entry.getValue().size();i++)
				instances.add(entry.getValue().get(i));
			classMap.put(entry.getKey(), instances);
		}
	}
	
	public double getNeighborsClass() {
		return neighborsClass;
	}

	public void setNeighborsClass(double neighborsClass) {
		this.neighborsClass = neighborsClass;
	}
	
	public Instances kNearestNeighbours(Instance target, int kNN, double neighborsClass) throws Exception {
		this.setNeighborsClass(neighborsClass);
		return this.kNearestNeighbours(target, kNN);
	}
	
	@Override
	public Instances kNearestNeighbours(Instance target, int kNN) throws Exception {
		Instances instances = classMap.get((int)this.neighborsClass);
		
		if(instances == null || instances.size() < 1){
			return new Instances(classMap.values().iterator().next(), 0);
		}

	    MyHeap heap = new MyHeap(kNN);
	    double distance; int firstkNN=0;
	    for(int i=0; i<instances.numInstances(); i++) {
	      if(target == instances.instance(i)) //for hold-one-out cross-validation
	        continue;
	      if(firstkNN<kNN) {
	        distance = m_DistanceFunction.distance(target, instances.instance(i), Double.POSITIVE_INFINITY);
	        if(distance == 0.0 && m_SkipIdentical)
	          if(i<instances.numInstances()-1)
	            continue;
	          else
	            heap.put(i, distance);
	        heap.put(i, distance);
	        firstkNN++;
	      }
	      else {
	        MyHeapElement temp = heap.peek();
	        distance = m_DistanceFunction.distance(target, instances.instance(i), temp.distance);
	        if(distance == 0.0 && m_SkipIdentical)
	          continue;
	        if(distance < temp.distance) {
	          heap.putBySubstitute(i, distance);
	        }
	        else if(distance == temp.distance) {
	          heap.putKthNearest(i, distance);
	        }
	      }
	    }
	    
	    Instances neighbours = new Instances(instances, (heap.size()+heap.noOfKthNearest()));
	    m_Distances = new double[heap.size()+heap.noOfKthNearest()];
	    int [] indices = new int[heap.size()+heap.noOfKthNearest()];
	    int i=1; MyHeapElement h;
	    while(heap.noOfKthNearest()>0) {
	      h = heap.getKthNearest();
	      indices[indices.length-i] = h.index;
	      m_Distances[indices.length-i] = h.distance;
	      i++;
	    }
	    while(heap.size()>0) {
	      h = heap.get();
	      indices[indices.length-i] = h.index;
	      m_Distances[indices.length-i] = h.distance;
	      i++;
	    }
	    
	    m_DistanceFunction.postProcessDistances(m_Distances);
	    
	    for(int k=0; k<indices.length; k++) {
	      neighbours.add(instances.instance(indices[k]));
	    }
	    
	    return neighbours;  
	  }

	@Override
	public void update(Instance ins) throws Exception {
		super.update(ins);
		this.rebuildClassMap();
	}
	
	@Override
	public void setInstances(Instances insts) throws Exception {
		super.setInstances(insts);
		this.rebuildClassMap();
	}
}