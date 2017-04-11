/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @see Producer
 * @see Re
 */ 
public class Resource {
	private Integer type;
	private List<Producer> producers = new ArrayList<Producer>();
	private int nbCopies = 0;
	public Resource(int t) {
		type = t;
	}
	
	public Integer getType() { return type; }
	public List<Producer> getProducers() { return producers;}
	public void addProducer(Producer p) {
		if (!producers.contains(p))
			producers.add(p);
	}
	public void addCopies(int n) { nbCopies+=n; }
	public int getNbCopies() { return nbCopies; }
	
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
