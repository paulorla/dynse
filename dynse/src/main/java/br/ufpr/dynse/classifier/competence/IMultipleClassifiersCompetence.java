package br.ufpr.dynse.classifier.competence;

import java.util.Set;

import com.yahoo.labs.samoa.instances.Instance;

public interface IMultipleClassifiersCompetence{
	
	public void setInstance(Instance instance);
	public Instance getInstance();
	public void setInstanceCount(int instanceCount);
	public int getInstanceCount();//if there is more instance that is equal to this one
	public Set<IClassifierCompetence> getClassifiersCompetence();
	public void setClassifiersCompetence(Set<IClassifierCompetence> classifiersCompetence);
}