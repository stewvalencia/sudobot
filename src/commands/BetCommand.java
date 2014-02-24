package commands;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import bettingManager.Bank;
import bettingManager.Bet;

/**
 * BetCommand provides the !bet command for chat users to define their wager in the current
 * bet.
 */
@SuppressWarnings("rawtypes")
public class BetCommand extends ListenerAdapter {
		
		/** The current bet. */
		private Bet bet;
		
		/** The bank totals for the chatroom. */
		private Bank bank;
		
		/**
		 * Instantiates a new bet command.
		 *
		 * @param bet the bet
		 * @param bank the bank
		 */
		public BetCommand(Bet bet, Bank bank) {
			this.bet = bet;
			this.bank = bank;
		}
        
        /* (non-Javadoc)
         * @see org.pircbotx.hooks.ListenerAdapter#onMessage(org.pircbotx.hooks.events.MessageEvent)
         */
        public void onMessage(MessageEvent event) {
        	String message = event.getMessage();
        	String name = event.getUser().getNick();
        	int bet = 0;
        	if (message.startsWith("!bet ")) {
        		try {
        			bet = Integer.parseInt(message.substring(5));
        		} catch (NumberFormatException e) {
        			event.respond("You done fucked up!");
        		}
                String betName = this.bet.getName(bet-1);
                if(!betName.equals("")) {
                	this.bet.addBettor(betName, name);
                	if(!bank.inBank(name))
                		bank.addScore(name);
                	}
                }
        }
}
