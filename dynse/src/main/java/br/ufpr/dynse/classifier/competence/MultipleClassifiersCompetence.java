package br.ufpr.dynse.classifier.competence;

import java.util.Set;

import com.yahoo.labs.samoa.instances.Instance;

public class MultipleClassifiersCompetence implements IMultipleClassifiersCompetence{
	
	private Instance instance;
	private int instanceCount;
	private Set<IClassifierCompetence> classifiersCompetence;
	
	public MultipleClassifiersCompetence() {
		instanceCount = 1;
	}

	@Override
	public Instance getInstance() {
		return instance;
	}
	
	@Override
	public void setInstance(Instance instance) {
		this.instance = instance;
	}
	
	@Override
	public Set<IClassifierCompetence> getClassifiersCompetence(){
		return classifiersCompetence;
	}

	@Override
	public int getInstanceCount() {
		return instanceCount;
	}

	@Override
	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

	@Override
	public void setClassifiersCompetence(Set<IClassifierCompetence> classifiersCompetence) {
		this.classifiersCompetence = classifiersCompetence;
	}
}