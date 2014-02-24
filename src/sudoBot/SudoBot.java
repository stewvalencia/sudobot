package sudoBot;

import org.pircbotx.hooks.*;
import org.pircbotx.hooks.events.*;

/**
 * The Class SudoBot contains the !help command to list general commands available to chat users.
 * Also, defines join message of the irc bot.
 */
@SuppressWarnings("rawtypes")
public class SudoBot extends ListenerAdapter implements Listener {
    
    /** The username of irc bot. */
    private String username;

	/**
	 * Instantiates a new sudo bot.
	 *
	 * @param username the username
	 */
	public SudoBot(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onJoin(org.pircbotx.hooks.events.JoinEvent)
	 */
	@Override
    public void onJoin(JoinEvent event) throws Exception {
    	if(event.getUser().getNick().equals(username))
    		event.getChannel().send().message(username+ " is here! B)");
    	
    }
    
    /* (non-Javadoc)
     * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
     */
    public void onMessage(MessageEvent event) {
    	String message = event.getMessage();
    	if (message.equals("!help")) {
    		event.respond("Yo, the available score check commands are: !score, !topScores");
    		}
    }
    
}