package outOfGameScreens.menus;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import log.LoggerUtility;
import outOfGameScreens.ScreenParameters;
import outOfGameScreens.SliderButtonCombo;

/**
 * The option menu. Differs from the SubMenu class in that the content here is interactable
 * @author Yang Mattew
 *
 */
public class OptionsMenu extends AbstractMenu {

	private static final long serialVersionUID = 1L;

	//Text file contents
	private String title = "Options";
	private String[] optionArrayList = {"Sound", "Music", "Timer"};

	//Swing attributes
	private JPanel timePanel = new JPanel();
	private JPanel soundPanel = new JPanel();
	private JPanel musicPanel = new JPanel();
	private SliderButtonCombo soundSlider = new SliderButtonCombo("sound");
	private SliderButtonCombo musicSlider = new SliderButtonCombo("music");

	private JPanel[] optionSliders = {soundPanel, musicPanel, timePanel};
	ArrayList<JTextField> blackTime;
	ArrayList<JTextField> redTime;

	private static Logger logData = LoggerUtility.getLogger(SubMenu.class, "html");

	/**
	 * Creates the option menu. Differs from the SubMenu class in that the content here is interactable
	 * @param menuScreen The JFrame
	 */
	public OptionsMenu(JFrame menuScreen) {
		mainScreen = menuScreen;

		// Menu
		setTitle(title);

		// Options
		optionList = setOptionListOptions(optionArrayList);
		optionList.addListSelectionListener(new optionListListener());

		// Volume controls
		soundPanel.add(soundSlider, JPanel.CENTER_ALIGNMENT);
		musicPanel.add(musicSlider, JPanel.CENTER_ALIGNMENT);

		// Timers
		JTextArea blackTimerText = new JTextArea("Black's Timer:");
		//JTextField blackHour = new JTextField(2);
		JTextField blackMinute = new JTextField(2);
		JTextField blackSecond = new JTextField(2);
		blackMinute.setText("15");
		blackSecond.setText("00");

		JTextArea redTimerText = new JTextArea("Red's Timer:");
		//JTextField redHour = new JTextField(2);
		JTextField redMinute = new JTextField(2);
		JTextField redSecond = new JTextField(2);
		redMinute.setText("15");
		redSecond.setText("00");

		blackTimerText.setEditable(false);
		blackTimerText.setBackground(boardColor);
		blackTimerText.setFont(new Font(blackTimerText.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE)));

		redTimerText.setEditable(false);
		redTimerText.setBackground(boardColor);
		redTimerText.setFont(new Font(redTimerText.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE)));

		// I'd make a HashMap associating a JTextArea to their JTextFields but it doesn't want to work for some reason
		blackTime = new ArrayList<JTextField>(Arrays.asList(blackMinute, blackSecond));
		redTime = new ArrayList<JTextField>(Arrays.asList(redMinute, redSecond));

		timePanel.add(blackTimerText, JPanel.CENTER_ALIGNMENT);

		int timerPos = 0;
		Boolean isBlack = true;

		for(JTextField timerComponent : blackTime) {
			timerComponent.setFont(new Font(timerComponent.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE)));
			timerComponent.addActionListener(new timeListener(timerPos, isBlack));
			timePanel.add(timerComponent, JPanel.CENTER_ALIGNMENT);
			timerPos++;
		}
		
		timerPos = 0;
		isBlack = !isBlack;
		timePanel.add(redTimerText, JPanel.CENTER_ALIGNMENT);

		for(JTextField timerComponent : redTime) {
			timerComponent.setFont(new Font(timerComponent.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE)));
			timerComponent.addActionListener(new timeListener(timerPos, isBlack));
			timePanel.add(timerComponent, JPanel.CENTER_ALIGNMENT);
			timerPos++;
		}

		for(JPanel rightSideContent : optionSliders) {
			rightSideContent.setLayout(new GridBagLayout());
			rightSideContent.setBackground(boardColor);
		}

		contentScroll = new JScrollPane(soundPanel);
		arrangeMenuComponents();
	}

	public class optionListListener implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent evt) {
			@SuppressWarnings("unchecked")
			JList<String> contentList = (JList<String>) evt.getSource();
			contentScroll.setViewportView(optionSliders[contentList.getSelectedIndex()]);
		}
	}

	public class timeListener implements ActionListener {

		private int timerPosition;
		private Boolean isBlack;
		public timeListener(int timerP, Boolean isB) {
			timerPosition = timerP;
			isBlack = isB;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField jt = (JTextField) e.getSource();
			String amountString;
			if(isBlack) {
				amountString = blackTime.get(timerPosition).getText();
			} else {
				amountString = redTime.get(timerPosition).getText();
			}

			logData.info(timerPosition + " " + amountString + " " + isBlack);

			int amountInt;
			try {
				amountInt = Integer.parseInt(amountString);
				amountString = Integer.toString(amountInt); // Strips leading zeros
			} catch(NumberFormatException nse) {
				amountString = "00";
				amountInt = 0;
			}

			if(amountInt > 59) {
				amountInt = 59;
				amountString = "59";
			}
			if(amountInt < 0) {
				amountInt = 0;
				amountString = "00";
			}
			
			jt.setText(amountString);

			//Based on which timer was modified, change the timer accordingly (ex. timer 2 is the minute amount for Red)
			String overwriteTime="";
			int decalage = isBlack? 0 : 2;
			switch(decalage + timerPosition) {
			case 0:
				overwriteTime = amountString + ":" + ScreenParameters.getBlackTime().substring(3,5);
				break;
			case 1:
				overwriteTime = ScreenParameters.getBlackTime().substring(0,2) + ":" + amountString;
				break;
			case 2:
				overwriteTime = amountString + ":" + ScreenParameters.getRedTime().substring(3,5);
				break;
			case 3:
				overwriteTime = ScreenParameters.getRedTime().substring(0,2) + ":" + amountString;
				break;
			}

			overwriteTime += ":000";
			if(isBlack) { //First two timers therefore black's timers
				ScreenParameters.setBlackTime(overwriteTime);
			}
			else { //Red's timers
				ScreenParameters.setRedTime(overwriteTime);
			}
			logData.info(amountString+" "+overwriteTime);
		}

	}
}
