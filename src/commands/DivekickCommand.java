package commands;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * The Class DivekickCommand was designed as a social experiment where viewers try to control a streamed
 * game of divekick. It reads their inputs and outputs a keyboard action for the Divekick game to read.
 */
@SuppressWarnings("rawtypes")
public class DivekickCommand extends ListenerAdapter{
	
	public void onMessage(MessageEvent event) {
    	String message = event.getMessage();
    	try {
			Robot robot = new Robot();
			robot.setAutoDelay(50);
			int duration = 500;
			long start = System.currentTimeMillis();
			switch((message.toLowerCase())) {
			case "d":
			case "dive1":
			case "d1":
			case "a":
				while (System.currentTimeMillis() - start < duration) {
				    robot.keyPress(KeyEvent.VK_D);
				}
				robot.keyRelease(KeyEvent.VK_D);
				break;
			case "k":
			case "kick1":
			case "k1":
			case "b":
				while (System.currentTimeMillis() - start < duration) {
				    robot.keyPress(KeyEvent.VK_K);
				}
				robot.keyRelease(KeyEvent.VK_K);
				break;
			case "divekick1":
			case "dk1":
			case "dk":
			case "democracy":
				while (System.currentTimeMillis() - start < duration) {
				    robot.keyPress(KeyEvent.VK_D);
				    robot.keyPress(KeyEvent.VK_K);
				}
				robot.keyRelease(KeyEvent.VK_D);
				robot.keyRelease(KeyEvent.VK_K);
				break;
			case "dive2":
			case "d2":
			case "x":
			case "up":
				while (System.currentTimeMillis() - start < duration) {
				    robot.keyPress(KeyEvent.VK_X);
				}
				robot.keyRelease(KeyEvent.VK_X);
				break;
			case "kick2":
			case "k2":
			case "m":
			case "down":
				while (System.currentTimeMillis() - start < duration) {
				    robot.keyPress(KeyEvent.VK_M);
				}
				robot.keyRelease(KeyEvent.VK_M);
				break;
			case "divekick2":
			case "dk2":
			case "xm":
			case "anarchy":
				while (System.currentTimeMillis() - start < duration) {
				    robot.keyPress(KeyEvent.VK_X);
				    robot.keyPress(KeyEvent.VK_M);
				}
				robot.keyRelease(KeyEvent.VK_X);
				robot.keyRelease(KeyEvent.VK_M);
				break;
			case "s1":
			case "special1":
			case "left":
				robot.setAutoDelay(0);
				robot.keyPress(KeyEvent.VK_D);
				robot.keyPress(KeyEvent.VK_K);
				while (System.currentTimeMillis() - start < duration) {
				}
				robot.keyRelease(KeyEvent.VK_D);
				robot.keyRelease(KeyEvent.VK_K);
				robot.setAutoDelay(50);
				break;
			case "s2":
			case "special2":
			case "right":
				robot.setAutoDelay(0);
				robot.keyPress(KeyEvent.VK_X);
				robot.keyPress(KeyEvent.VK_M);
				while (System.currentTimeMillis() - start < duration) {
				}
				robot.keyRelease(KeyEvent.VK_X);
				robot.keyRelease(KeyEvent.VK_M);
				robot.setAutoDelay(50);
				break;
			}
    	} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
