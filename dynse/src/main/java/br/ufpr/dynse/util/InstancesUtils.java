package br.ufpr.dynse.util;

import java.util.ArrayList;
import java.util.List;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

public class InstancesUtils {	
	
	public static Instances gerarDataset(List<Instance> instancias, String nomeDataset) throws Exception{
		ArrayList<Attribute> atributos = AttributesUtils.copyAtributes(instancias.get(0));
		
		Instances retorno = new Instances(nomeDataset, atributos,
				instancias.size());
		for(Instance inst : instancias){
			retorno.add(inst);
		}
		
		retorno.setClassIndex(retorno.numAttributes() - 1);
		
		return retorno;
	}
}