/*
 * Gomes Fernandes Caty
 * Université de Strasbourg
 * Licence 3 Informatique, S6 Printemps, 2017
 */
import java.io.Serializable;

/**
 * This class allows the representation of either a producer or a player
 * of the game by keeping its host and port address, but without 
 * directing accessing the distant object.  
 * The class is Serializable so it can be passed by a call to a remote
 * object.
 */ 
public class Agent implements Serializable {
	public static final char PRODUCER = 'p';
	public static final char PLAYER = 'j';
	private String host;
	private String port;
	private char type;
	
	/**
	 * An agent is a remote RMI object, hence is accessible through a 
	 * host address and a port. It can either be a producer or player.
	 * @param h		the address of the agent's machine
	 * @param p		the agent's RMI port as a String
	 * @param t		the type of the agent, either PRODUCER or PLAYER
	 */ 
	public Agent(String h, String p, char t) {
		host = h;
		port = p;
		type = t;
	}
	
	/** @return	host address of the agent */ 
	public String getHost() { return host; }
	
	/** @return	port address of the agent as a String */ 
	public String getPort() { return port; }
	
	/**
	 * Two Agents are identical if they have the same host, port and type.
	 * @param object	the object to compare to	
	 * @return		true if both are identical Agents, false if not
	 */ 
	public boolean equals(Object object) {
        boolean same = false;
        if (object != null && object instanceof Agent) {
			Agent a = (Agent) object;
            if (this.host.equals(a.host) 
					&& this.port.equals(a.port)
					&& this.type == a.type)
				same = true;
        }
        return same;
    }
}
