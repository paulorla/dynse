package br.ufpr.dynse.instance;

import com.yahoo.labs.samoa.instances.Instance;

import br.ufpr.dynse.concept.Concept;
import moa.core.InstanceExample;
import moa.core.Utils;

public class ConceptInstance extends InstanceExample implements Comparable<ConceptInstance> {

	private final Concept<?> concept;

	public ConceptInstance(InstanceExample instance, Concept<?> concept) {
		this(instance.getData(), concept);
	}

	public ConceptInstance(Instance instance, Concept<?> concept) {
		super(instance);
		this.concept = concept;
	}

	public Instance getInstance() {
		return instance;
	}

	public Concept<?> getConcept() {
		return concept;
	}

	@Override
	public int compareTo(ConceptInstance other) {
		int result;
		Instance inst1;
		Instance inst2;
		int i;

		inst1 = this.getData();
		inst2 = other.getData();

		result = 0;
		for (i = 0; i < inst1.numAttributes(); i++) {
			// comparing attribute values
			// 1. special handling if missing value (NaN) is involved:
			if (inst1.isMissing(i) || inst2.isMissing(i)) {
				if (inst1.isMissing(i) && inst2.isMissing(i)) {
					continue;
				} else {
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
				} else {
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