package br.ufpr.dynse.instance;

import java.io.Serializable;
import java.util.Comparator;

import com.yahoo.labs.samoa.instances.Instance;

import moa.core.Utils;

public class InstanceComparator implements Comparator<Instance>, Serializable {

	private static final long serialVersionUID = 1L;

	protected boolean includeClass;

	public InstanceComparator() {
		this(true);
	}

	public InstanceComparator(boolean includeClass) {
		super();
		this.includeClass = includeClass;
	}

	@Override
	public int compare(Instance inst1, Instance inst2) {
		int         result;
	    int         classindex;
	    int         i;
	    
	    // get class index
	    if (inst1.classIndex() == -1)
	      classindex = inst1.numAttributes() - 1;
	    else
	      classindex = inst1.classIndex();

	    result = 0;
	    for (i = 0; i < inst1.numAttributes(); i++) {
	      // exclude class?
	      if (!includeClass && (i == classindex))
	        continue;
	   
	      // comparing attribute values
	      // 1. special handling if missing value (NaN) is involved:
	      if (inst1.isMissing(i) || inst2.isMissing(i)) {
	        if (inst1.isMissing(i) && inst2.isMissing(i)) {
	          continue;
	        }
	        else {
	          if (inst1.isMissing(i))
	            result = -1;
	          else
	            result = 1;
	          break;
	        }
	      }
	      // 2. regular values:
	      else {
	        if (Utils.eq(inst1.value(i), inst2.value(i))) { 
	          continue;
	        }
	        else {
	          if (inst1.value(i) < inst2.value(i))
	            result = -1;
	          else
	            result = 1;
	          break;
	        }
	      }
	    }
	    
	    return result;
	}
}