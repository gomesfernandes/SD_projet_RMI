// doit être accessible à tout le monde...

public class Agent {
	public static final char PRODUCER = 'p';
	public static final char PLAYER = 'j';
	private String host;
	private String port;
	private char type;
	
	public Agent(String h, String p, char t) {
		host = h;
		port = p;
		type = t;
	}
	
	public String getHost() { return host; }
	public String getPort() { return port; }
	
	
	public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof Agent) {
			Agent a = (Agent) object;
            if (this.host == a.host 
					&& this.port == a.port
					&& this.type == a.type)
				same = true;
        }

        return same;
    }
}
