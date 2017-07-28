/*    
*    Concept.java 
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
package br.ufpr.dynse.concept;

public class Concept<T extends Comparable<T>> implements Comparable<Concept <T>>, Cloneable{

	private T conceptID;
	
	public Concept(T conceptID) {
		super();
		this.conceptID = conceptID;
	}
	

	public Concept(Concept<T> copiedObject) {
		super();
		this.conceptID = copiedObject.getConceptID();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Concept<T> clone(){//canonical implementation
		try{
			return (Concept<T>)super.clone();
		}catch(CloneNotSupportedException e){
			throw new AssertionError(e);
		}
	}

	public Concept() {
		super();
	}

	public T getConceptID() {
		return conceptID;
	}
	
	public void setConceptID(T conceptID) {
		this.conceptID = conceptID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conceptID == null) ? 0 : conceptID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept<?> other = (Concept<?>) obj;
		if (conceptID == null) {
			if (other.conceptID != null)
				return false;
		} else if (!conceptID.equals(other.conceptID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Concept " + conceptID.toString();
	}
	
	public int compareTo(Concept<T> o) {
		return conceptID.compareTo(o.getConceptID());
	}
}