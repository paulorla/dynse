package br.ufpr.dynse.neighborsearch;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import moa.classifiers.lazy.neighboursearch.LinearNNSearch;

public class LinearIndexNNSearch extends LinearNNSearch{

	private static final long serialVersionUID = 1L;
	
	public LinearIndexNNSearch() {
		super();
	}

	public LinearIndexNNSearch(Instances insts) {
		super(insts);
	}

	public int[] kNearestNeighboursIndexes(Instance target, int kNN) throws Exception {
	 
	    MyHeap heap = new MyHeap(kNN);
	    double distance; int firstkNN=0;
	    for(int i=0; i<m_Instances.numInstances(); i++) {
	      if(target == m_Instances.instance(i)) //for hold-one-out cross-validation
	        continue;
	      if(firstkNN<kNN) {
	        distance = m_DistanceFunction.distance(target, m_Instances.instance(i), Double.POSITIVE_INFINITY);
	        if(distance == 0.0 && m_SkipIdentical)
	          if(i<m_Instances.numInstances()-1)
	            continue;
	          else
	            heap.put(i, distance);
	        heap.put(i, distance);
	        firstkNN++;
	      }
	      else {
	        MyHeapElement temp = heap.peek();
	        distance = m_DistanceFunction.distance(target, m_Instances.instance(i), temp.distance);
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
	    
	    return indices;
	  }
}