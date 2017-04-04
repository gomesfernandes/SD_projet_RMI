import java.util.List;
import java.util.ArrayList;

public class Ressource {
	private Integer type;
	private List<Producer> producers = new ArrayList<Producer>();
	
	public Ressource(int t) {
		type = t;
	}
	
	public Integer getType() { return type; }
	public List<Producer> getProducers() { return producers;}
	public void addProducer(Producer p) {
		if (!producers.contains(p))
			producers.add(p);
	}
	
	public boolean equals(Object object) {
        boolean same = false;
        if (object != null && object instanceof Ressource) {
			Ressource r = (Ressource) object;
            if (this.type == r.type )
				same = true;
        }
        return same;
    }
}
