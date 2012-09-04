/**
 * 
 */
package p2pct.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Message is used to call functions on other peers and get a response.
 * The functions are defined as static variables starting with CMD.
 * Along with the function, arguments can be passed.
 * 
 * @author nicolas baer
 */
public class Message implements Serializable{
	// serial
	private static final long serialVersionUID = -395699669022831614L;
	
	// command constants
	public final static String CMD_FILE_REQUEST = "fileRequest";
	public final static String CMD_SONGLIST_REQUEST = "songlistRequest";
	
	//
	public final static String CMD_SONGTAGGER_VOTE = "songtaggerVote";
	
	
	public final static String CMD_SEARCHTERM_REQUEST = "stRequest";
	public final static String CMD_AFFINITY_SONGREQUEST = "affSongRequest";
	
	
	private String command;
	private Object[] arguments;
	
	public Message(){
		
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the arguments
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arguments);
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (!Arrays.equals(arguments, other.arguments))
			return false;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		return true;
	}

	
	
}
