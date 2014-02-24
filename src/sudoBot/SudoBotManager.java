package sudoBot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import javax.swing.*;

import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.MultiBotManager;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.managers.ListenerManager;

import bettingManager.Bank;
import bettingManager.Bet;
import commands.BetCommand;
import commands.ScoreCommand;


/**
 * The SudoBotManager class builds the GUI of the IRC bot using Swing, AWT, and JavaFX. It also create and manages
 * the IRC bot using the PircBotX library. The IRC bot can track users bet and bank totals.
 * 
 * @author Stewart Valencia
 *
 */
public class SudoBotManager extends Application {

	/** The manager - Manages all the chat bots, 1 for now */
	private MultiBotManager<PircBotX> manager; 

	/** The sudo - The current irc bot */
	private PircBotX sudo;
	
	/** The current channel */
	private Channel currentChan;
	
	/** The custom chat command listeners. */
	private ListenerManager<PircBotX> listeners;

	/** The current bet */
	private Bet currentBet;
	
	/** The current listener */
	private BetCommand currentListener; 
	
	/** The current bank totals */
	private Bank currentBank;

	/** The status of bot running. */
	private boolean botRunning; 

	/** The Main UI frame */
	private JFrame frame; 

	//Panels storing and displaying buttons and text UI components
	private JPanel panelButton; 
	private JPanel panelText; 
	private JPanel panelMessage;
	private JPanel panelLogin; 

	/** The current error message. */
	private JLabel message;

	/** Name of bet 1 */
	private JTextField betName1;
	
	/** Name of bet 2 */
	private JTextField betName2;

	//Buttons for setting channel, betting system, and bot control
	private JButton buttonChan;
	private JButton buttonStart;
	private JButton buttonClose; 
	
	/** End bet, grants bet 1 winner button*/
	private JButton buttonEnd1;
	
	/** End bet, grants bet 2 winner button */
	private JButton buttonEnd2; 
	
	/** Stops the bot from running button */
	private JButton buttonStop; 

	/** Displays login status, usually if error is detected */
	private JLabel loginMessage; 

	//UI components on where to enter bot login information
	private JTextField nameField;
	private JPasswordField passField;
	private JTextField addressField;
	private JTextField portField;
	private JTextField channelField;

	//Login buttons
	private JButton buttonLogin;
	private JButton buttonLoad;

	//Will store file locations of audio cues for betting system
	private String[] startFile;
	private String[] closeFile;
	
	
	/** MediaPlayer that will play the audio file */
	private MediaPlayer player;

	//Actual storage of bot login information
	private String username;
	private String password;
	private String ircAddress;
	private int port;
	private String channelName;

	/**
	 * Sets the bot manager and starts the irc bot.
	 * Initializes bot instance variables.
	 * Saves the configuration.
	 *
	 * @return true, if successful
	 */
	public boolean setUpBots() {
		manager = new MultiBotManager<PircBotX>();
		currentBank = new Bank();
		Configuration<PircBotX> config = createConfig(); // Create an immutable
															// configuration
															// from this builder
		manager.addBot(config);

		manager.start();
		botRunning = true;
		sudo = manager.getBotById(0);
		listeners = sudo.getConfiguration().getListenerManager();
		currentBet = null;
		currentListener = null;
		currentChan = null;

		if (sudo.isConnected())
			loginMessage.setText("");
		else
			loginMessage.setText("Error in login fields");

		saveConfig(config);
		readConfig();

		return sudo.isConnected();

	}

	/**
	 * Save the login configuration.
	 *
	 * @param config the current configuration
	 */
	private void saveConfig(Configuration<PircBotX> config) {
		try {
			FileOutputStream fs = new FileOutputStream("config.ser");
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(username);
			os.writeObject(ircAddress);
			os.writeObject(password);
			os.writeObject(port);
			os.writeObject(channelName);
			os.writeObject(loginMessage.getText());
			os.close();
		} catch (Exception ex) {
			System.out.println("Save config error");
		}

	}

	/**
	 * Read the login configuration.
	 */
	private void readConfig() {
		try {
			FileInputStream fs = new FileInputStream("config.ser");
			ObjectInputStream os = new ObjectInputStream(fs);

			username = (String) os.readObject();
			ircAddress = (String) os.readObject();
			password = (String) os.readObject();
			port = (int) os.readObject();
			channelName = (String) os.readObject();
			String warning = (String) os.readObject();
			nameField.setText(username);
			passField.setText(password);
			addressField.setText(ircAddress);
			portField.setText("" + port);
			channelField.setText(channelName);
			loginMessage.setText(warning);

			os.close();
		} catch (Exception ex) {
			System.out.println("Read config error");
		}
	}

	/**
	 * Creates the configuration from login bot instance variables.
	 *
	 * @return the configuration
	 */
	@SuppressWarnings("rawtypes")
	private Configuration<PircBotX> createConfig() {
		@SuppressWarnings("unchecked")
		Configuration<PircBotX> config = new Configuration.Builder()
				.setName(username)
				// Nick of the bot. CHANGE IN YOUR CODE
				.setServer(ircAddress, port, password)
				// The server were connecting to
				.addAutoJoinChannel(channelName)
				// Join #pircbotx channel on connect
				.addListener(new SudoBot(username))
				.addListener(new ScoreCommand(currentBank))
				.setAutoReconnect(false).setMessageDelay(2000)
				.buildConfiguration();

		return config;

	}

	/**
	 * Attach shut down hook. Hook makes sure the irc bot stops running.
	 */
	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (botRunning) {
					try {
						manager.stopAndWait();
						botRunning = false;
					} catch (InterruptedException e1) {
						System.out.println("Shutdown Error");
					}
				}
			}
		});
	}

	/**
	 * Builds the gui.
	 */
	public void buildGUI() {
		
		//File locations of startbet and closebet sounds
		startFile = new String[3];
		startFile[0] = (new File("startBet1.mp3")).toURI().toString();
		startFile[1] = (new File("startBet2.mp3")).toURI().toString();
		startFile[2] = (new File("startBet3.mp3")).toURI().toString();

		closeFile = new String[3];
		closeFile[0] = (new File("closeBet1.mp3")).toURI().toString();
		closeFile[1] = (new File("closeBet2.mp3")).toURI().toString();
		closeFile[2] = (new File("closeBet3.mp3")).toURI().toString();

		player = null;

		frame = new JFrame();
		panelButton = new JPanel();
		panelText = new JPanel();
		panelMessage = new JPanel();
		panelLogin = new JPanel();

		frame.setSize(470, 200);

		setUpButtonPanel();
		setUpTextPanel();
		setUpMessagePanel();
		setUpLoginPanel();

		frame.setTitle("SudoBot");
		frame.setIconImage((new ImageIcon("lobster.gif")).getImage());

		frame.getContentPane().add(BorderLayout.SOUTH, panelButton);
		frame.getContentPane().add(BorderLayout.NORTH, panelText);
		frame.getContentPane().add(BorderLayout.CENTER, panelLogin);

		frame.setVisible(true);
		panelText.setVisible(false);
		panelButton.setVisible(true);
		panelLogin.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	/**
	 * Setups the button panel.
	 */
	public void setUpButtonPanel() {
		panelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelButton.setBackground(Color.DARK_GRAY);

		buttonChan = new JButton("Set Channel");
		buttonChan.addActionListener(new SetChannelListener());
		buttonStart = new JButton("Start Bets");
		buttonStart.addActionListener(new StartBetListener());
		buttonClose = new JButton("Close Bets");
		buttonClose.addActionListener(new CloseBetListener());
		buttonEnd1 = new JButton("Bet 1 Wins");
		buttonEnd1.addActionListener(new EndBetListener1());
		buttonEnd2 = new JButton("Bet 2 Wins");
		buttonEnd2.addActionListener(new EndBetListener2());
		buttonStop = new JButton("Stop Bot");
		buttonStop.addActionListener(new StopListener());

		buttonLogin = new JButton("Login");
		buttonLogin.addActionListener(new LoginListener());
		buttonLoad = new JButton("Load Previous Settings");
		buttonLoad.addActionListener(new LoadListener());

		buttonClose.setVisible(false);
		buttonEnd1.setVisible(false);
		buttonEnd2.setVisible(false);
		buttonStop.setVisible(false);
		buttonChan.setVisible(false);
		buttonStart.setVisible(false);
		buttonLoad.setVisible(false);

		panelButton.add(buttonChan);
		panelButton.add(buttonStart);
		panelButton.add(buttonClose);
		panelButton.add(buttonEnd1);
		panelButton.add(buttonEnd2);
		panelButton.add(buttonStop);
		panelButton.add(buttonLogin);
		panelButton.add(buttonLoad);
	}

	/**
	 * Setups text panel components.
	 */
	public void setUpTextPanel() {
		panelText.setLayout(new BoxLayout(panelText, BoxLayout.Y_AXIS));

		JLabel betLabel1 = new JLabel("Bet 1 Name:");
		betName1 = new JTextField(20);
		JLabel betLabel2 = new JLabel("Bet 2 Name:");
		betName2 = new JTextField(20);

		betName1.setText("Name of Bet 1");
		betName2.setText("Name of Bet 2");

		betLabel1.setLabelFor(betName1);
		betLabel2.setLabelFor(betName2);

		panelText.add(betLabel1);
		panelText.add(betName1);
		panelText.add(betLabel2);
		panelText.add(betName2);

	}

	/**
	 * Setups message panel components.
	 */
	public void setUpMessagePanel() {
		message = new JLabel();

		message.setText("Wait for sudobot to enter chat and then press setChan");
		message.setForeground(Color.RED);

		panelMessage.add(message);
	}

	/**
	 * Setup login panel components.
	 */
	public void setUpLoginPanel() {
		JLabel nameLabel = new JLabel("Bot name:");
		JLabel passLabel = new JLabel("Password:");
		JLabel addressLabel = new JLabel("IRC address:");
		JLabel portLabel = new JLabel("Port:");
		JLabel channelLabel = new JLabel("Channel:");
		loginMessage = new JLabel();

		nameField = new JTextField(10);
		passField = new JPasswordField(10);
		addressField = new JTextField(10);
		portField = new JTextField(10);
		channelField = new JTextField(10);

		portField.setInputVerifier(new PortVerifier());
		nameField.setInputVerifier(new EmptyTextVerifier());
		addressField.setInputVerifier(new EmptyTextVerifier());
		channelField.setInputVerifier(new EmptyTextVerifier());
		passField.setInputVerifier(new EmptyPassVerifier());

		loginMessage.setForeground(Color.RED);
		loginMessage.setText("");

		readConfig();

		nameLabel.setLabelFor(nameField);
		passLabel.setLabelFor(passField);
		addressLabel.setLabelFor(addressField);
		portLabel.setLabelFor(portField);
		channelLabel.setLabelFor(channelField);

		panelLogin.add(nameLabel);
		panelLogin.add(nameField);
		panelLogin.add(passLabel);
		panelLogin.add(passField);
		panelLogin.add(addressLabel);
		panelLogin.add(addressField);
		panelLogin.add(portLabel);
		panelLogin.add(portField);
		panelLogin.add(channelLabel);
		panelLogin.add(channelField);
		panelLogin.add(loginMessage);

		panelLogin.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 * Creates instance of SudoBotManager, attaches shutdown hooks, and builds GUI.
	 */
	@Override
	public void start(Stage arg0) throws Exception {
		SudoBotManager gui = new SudoBotManager();
		gui.attachShutDownHook();
		gui.buildGUI();
	}

	public static void main(String[] args) throws InterruptedException {
		Application.launch();
	}

	/**
	 * The listener interface for receiving setChannel events.
	 * Will set the current channel instance variable. IRC bot 
	 * needs to be connected first.
	 *
	 * @see SetChannelEvent
	 */
	public class SetChannelListener implements ActionListener {
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			currentChan = sudo.getUserBot().getChannels().first();
			message.setText("Channel initialized " + currentChan.getName());
			buttonChan.setVisible(false);
		}
	}

	/**
	 * Reads the betName text fields and make the corresponding bet object.
	 * Sends a StartBet message to irc channel and plays a random startbet song.
	 * Attaches a betcommand listener to the bot.
	 *
	 * @see StartBetEvent
	 */
	public class StartBetListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentChan != null) {
				currentChan.send().message(
						"Betting Start: " + betName1.getText() + "(1) vs. "
								+ betName2.getText() + "(2) "
								+ "Type !bet 1 for " + betName1.getText()
								+ ". Type !bet 2 for " + betName2.getText());
				currentBet = new Bet(betName1.getText(), betName2.getText());
				listeners.addListener(currentListener = new BetCommand(
						currentBet, currentBank));
				message.setText("Bets Set");
				betName1.setEditable(false);
				betName2.setEditable(false);
				buttonStart.setVisible(false);
				buttonClose.setVisible(true);
				Random generator = new Random();
				int i = generator.nextInt(3);
				Media startSong = new Media(startFile[i]);
				player = new MediaPlayer(startSong);
				player.setCycleCount(5);
				player.play();
			} else {
				message.setText("Initialize channel");
			}
		}
	}

	/**
	 * Detaches BetCommand listener to prevent further bets.
	 * Reports betting odds to irc channel, ends the startbet song,
	 * and plays a closebet sound.
	 *
	 * @see CloseBetEvent
	 */
	public class CloseBetListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentChan != null) {
				currentChan.send().message(
						"Betting Stop! " + currentBet.toString());
				listeners.removeListener(currentListener);
				buttonClose.setVisible(false);
				buttonEnd1.setVisible(true);
				buttonEnd2.setVisible(true);
				player.stop();
				player = null;
				Random generator = new Random();
				int i = generator.nextInt(3);
				Media closeSong = new Media(closeFile[i]);
				player = new MediaPlayer(closeSong);
				player.play();
			} else {
				message.setText("Initialize channel");
			}
		}
	}

	/**
	 * Awards and reports the winner of the bet is betName1.
	 */
	public class EndBetListener1 implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentChan != null) {
				String winner = betName1.getText();
				if (!winner.equals(null)) {
					currentChan.send().message(
							"Betting Results: " + winner
									+ " wins! Payout to the winners!");
					currentBank.addScores(currentBet.getBettors(winner));
					buttonStart.setVisible(true);
					buttonEnd1.setVisible(false);
					buttonEnd2.setVisible(false);
					betName1.setEditable(true);
					betName2.setEditable(true);
				}
			} else {
				message.setText("Initialize channel");
			}
		}
	}

	/**
	 * Awards and reports the winner of the bet is betName2.
	 */
	public class EndBetListener2 implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentChan != null) {
				String winner = betName2.getText();
				if (!winner.equals(null)) {
					currentChan.send().message(
							"Betting Results: " + winner
									+ " wins! Payout to the winners!");
					currentBank.addScores(currentBet.getBettors(winner));
					buttonStart.setVisible(true);
					buttonEnd1.setVisible(false);
					buttonEnd2.setVisible(false);
					betName1.setEditable(true);
					betName2.setEditable(true);
				}
			} else {
				message.setText("Initialize channel");
			}
		}
	}

	/**
	 * Stops the bot from running. Notifies channel (if channel is intialized).
	 *
	 * @see StopEvent
	 */
	public class StopListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentChan != null) {
				currentChan.send().message(
						"I have to go now. My planet needs me. FrankerZ");
				currentChan
						.send()
						.message(
								"Note: Sudostew died on his way back to his home Planet");
			}
			message.setText("Stopping bots");
			try {
				manager.stopAndWait();
				botRunning = false;
				buttonChan.setVisible(false);
				buttonStart.setVisible(false);
				buttonClose.setVisible(false);
				buttonEnd1.setVisible(false);
				buttonEnd2.setVisible(false);
				buttonStop.setVisible(false);
			} catch (InterruptedException e1) {
				System.out.println("Stop bot Error");
			}
		}
	}

	/**
	 * Reads text fields of login page and tries to setup the bot for it.
	 * Goes to betting UI if successful. Shutdowns program if login info is wrong.
	 * An error message will appear the next time the program launch if login info is
	 * wrong.
	 * 
	 * @see LoginEvent
	 */
	public class LoginListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {

			username = nameField.getText();
			password = new String(passField.getPassword());
			ircAddress = addressField.getText();
			port = Integer.parseInt(portField.getText());
			channelName = channelField.getText();

			if (setUpBots()) {

				panelText.setVisible(true);
				panelMessage.setVisible(true);
				panelLogin.setVisible(false);

				buttonStop.setVisible(true);
				buttonChan.setVisible(true);
				buttonStart.setVisible(true);

				buttonLogin.setVisible(false);
				buttonLoad.setVisible(false);

				frame.getContentPane().add(BorderLayout.CENTER, panelMessage);
			} else {
				frame.dispose();
				System.exit(0);
			}

		}
	}

	/**
	 * LoadListener may be expanded on in the future.
	 *
	 * @see LoadEvent
	 */
	public class LoadListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	/**
	 * The PortVerifier makes sure port field is a valid port number.
	 */
	public class PortVerifier extends InputVerifier {
		
		@Override
		public boolean verify(JComponent input) {
			String text = ((JTextField) input).getText();
			try {
				int i = Integer.parseInt(text);
				if (i < 0 || i > 65535) {
					loginMessage.setText("Please enter valid port number");
					return false;
				}
			} catch (NumberFormatException e) {
				loginMessage.setText("Please enter valid port number");
				return false;
			}
			loginMessage.setText("");
			return true;
		}
	}

	/**
	 * The EmptyTextVerifier class makes sure text fields aren't empty by returning
	 * a boolean value.
	 */
	public class EmptyTextVerifier extends InputVerifier {

		@Override
		public boolean verify(JComponent input) {
			String text = ((JTextField) input).getText();
			boolean result = !text.equals("");
			if (result) {
				loginMessage.setText("");
			} else {
				loginMessage.setText("Empty Field detected");
			}
			return result;
		}
	}

	/**
	 * EmptyPassVerifier class makes sure password isn't empty.
	 * May be depreciated in later versions for no pass logins.
	 */
	public class EmptyPassVerifier extends InputVerifier {
		
		@Override
		public boolean verify(JComponent input) {
			String text = new String(((JPasswordField) input).getPassword());
			boolean result = !text.equals("");
			if (result) {
				loginMessage.setText("");
			} else {
				loginMessage.setText("Empty Field detected");
			}
			return result;
		}
	}

}
