package bettingManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The Class Bank will store all the current users totals. Have future plans
 * to save this table.
 */
public class Bank {
	
	/** The bank totals. */
	private HashMap<String, Integer> bankTotals;

	/**
	 * Instantiates a new bank.
	 */
	public Bank() {
		bankTotals = new HashMap<String, Integer>();
	}
	
	/**
	 * Adds the score.
	 *
	 * @param name - the name of the user
	 */
	public void addScore(String name) {
		if (bankTotals.containsKey(name))
			bankTotals.put(name, bankTotals.get(name)+1);
		else
			bankTotals.put(name, 1);
	}
	
	/**
	 * Checks if user is in the bank table.
	 *
	 * @param name - the username
	 * @return true, if successful
	 */
	public boolean inBank(String name) {
		return bankTotals.containsKey(name);
	}

	/**
	 * Gets the score total of the user.
	 *
	 * @param name - the user
	 * @return the score
	 */
	public int getScore(String name) {
		return bankTotals.get(name);
	}
	
	/**
	 * Gets the top 5 scores.
	 *
	 * @return the top scores
	 */
	public String getTopScores() {
		if(this.bankTotals.isEmpty())
			return "Yo, no totals here. Please register with a !score.";
		ArrayList<String> keys = new ArrayList<String>(bankTotals.keySet());
		ArrayList<Integer> values = new ArrayList<Integer>(bankTotals.values());
		Collections.sort(values);
		Collections.reverse(values);
		Collections.sort(keys);
		String rank = "Current Leaderboard: ";
		Iterator<Integer> help = values.iterator();
		for(int i=0; ((i<5) && help.hasNext());) {
			int value = help.next();
			for(String name: keys) {
				if(value == bankTotals.get(name)) {
					rank+=(i+1)+". "+name+"("+value+") ";
					i++;
				}
			}
		}
		return rank;
	}

	/**
	 * Adds the +1 to all users' score in specified bettors list
	 *
	 * @param bettors - the bettors where their scores should be updated
	 */
	public void addScores(ArrayList<String> bettors) {
		for(String name: bettors)
			this.addScore(name);
		
	}
	
}
