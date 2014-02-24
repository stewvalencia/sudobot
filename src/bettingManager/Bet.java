package bettingManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Class Bet defines a bet as a string list of outcomes and 
 * a table that associate a user with their predicted outcome.
 */
public class Bet {
	
	/** The name. */
	private ArrayList<String> name;
	
	/** The bet table. */
	private HashMap<String, ArrayList<String>> betTable;
	
	
	/**
	 * Instantiates a new bet.
	 *
	 * @param bet1 the bet1
	 * @param bet2 the bet2
	 */
	public Bet(String bet1, String bet2) {
		this.name = new ArrayList<String>();
		this.betTable = new HashMap<String, ArrayList<String>>();
		this.addName(bet1);
		this.addName(bet2);
	}


	/**
	 * Gets the name of a bet outcome.
	 *
	 * @param pos - the pos
	 * @return the name
	 */
	public String getName(int pos) {
		if(pos < 0 || pos > name.size())
			return "";
		else
			return name.get(pos);
	}

	/**
	 * Gets the list of all betting outcomes.
	 *
	 * @return the names
	 */
	public ArrayList<String> getNames() {
		return name;
	}
	
	/**
	 * Gets the bettors.
	 *
	 * @param betName - the bet name
	 * @return the bettors
	 */
	public ArrayList<String> getBettors(String betName) {
		return betTable.get(betName);
	}
	
	/**
	 * Adds the name of the outcome to the list and table.
	 *
	 * @param name - the name of the outcome
	 */
	public void addName(String name) {
			this.name.add(name);
			this.betTable.put(name, new ArrayList<String>());
	}
	
	/**
	 * Adds a user to the betting table where the user is 
	 * associated with their prediction.
	 *
	 * @param bet - the bet
	 * @param bettor - the bettor
	 */
	public void addBettor(String bet, String bettor) {
		for(String name: this.name)
			this.betTable.get(name).remove(bettor);
		this.betTable.get(bet).add(bettor);
	}
	
	/* (non-Javadoc)
	 * toString method will list the names of the outcomes and their odds.
	 * Odds are the number of people who bet on that outcome for now.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String string = "[Name: Odds] || ";
		for(String name: betTable.keySet()) {
			string+="| "+name+":";
			string+=betTable.get(name).size()+" ";
		}
		return string;
	}
}
