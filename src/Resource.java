/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a type of resource, whose type is distinguished
 * by an integer. It is mainly used by the Players. Each Resource lists 
 * the producers that create it, as well as the number of copies a 
 * producer has already obtained.
 * 
 * @see Producer
 */ 
public class Resource {
	private Integer type;
	private List<Producer> producers = new ArrayList<Producer>();
	private int nbCopies = 0;
	
	/** @param t	the type of the resource (an integer) */
	public Resource(int t) {
		type = t;
	}
	
	/** @return the type of this resource */ 
	public Integer getType() { return type; }
	
	/** @return the list of Producers that create this resource */
	public List<Producer> getProducers() { return producers;}
	
	/** @param p a producer that creates this resource to add to the list */
	public void addProducer(Producer p) {
		if (!producers.contains(p))
			producers.add(p);
	}
	/** @param n number of new copies to add */
	public void addCopies(int n) { nbCopies+=n; }
	
	/** @return number of copies already obtained */
	public int getNbCopies() { return nbCopies; }
	
	/**
	 * @param object	the object to compare to	
	 * @return	true if both Resources have the same type, false if not
	 */ 
	public boolean equals(Object object) {
        boolean same = false;
        if (object != null && object instanceof Resource) {
			Resource r = (Resource) object;
            if (this.type == r.type )
				same = true;
        }
        return same;
    }
}
