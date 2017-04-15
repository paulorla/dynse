package br.ufpr.dynse.util;

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

public class AttributesUtils {
	public static List<Attribute> copyAtributes(Instances originalDataset){
		List<Attribute> atributos = new ArrayList<Attribute>(originalDataset.numAttributes());
		for(int i =0; i < originalDataset.numAttributes(); i++){
				Attribute atribOriginal = originalDataset.attribute(i);
				atributos.add(atribOriginal);
		}
		return atributos;
	}
	
	public static ArrayList<Attribute> copyAtributes(Instance instance){
		ArrayList<Attribute> atributes = new ArrayList<Attribute>(instance.numAttributes());
		for(int i =0; i < instance.numAttributes(); i++){
				Attribute atribOriginal = instance.attribute(i);
				atributes.add(atribOriginal);
		}
		return atributes;
	}
}