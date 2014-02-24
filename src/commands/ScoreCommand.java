package commands;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import bettingManager.Bank;

/**
 * The ScoreCommand class adds the irc command !score for users to use
 * to get their current bank score.
 */
@SuppressWarnings("rawtypes")
public class ScoreCommand extends ListenerAdapter {

	/** The bank. */
	private Bank bank;
	
	/**
	 * Instantiates a new score command.
	 *
	 * @param bank - the bank
	 */
	public ScoreCommand(Bank bank) {
		this.bank = bank;
	}
	
	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
	 */
	public void onMessage(MessageEvent event) {
    	String message = event.getMessage();
    	String name = event.getUser().getNick();
    	if (message.equals("!score")) {
            if(!bank.inBank(name))
            	bank.addScore(name);
            event.respond("Your total is " + bank.getScore(name));
            }
    	else if(message.equals("!topScores")) {
    		event.respond(bank.getTopScores());
    	}
    }
}
