package outOfGameScreens;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;

/**
 * This combines the JSlider and JButton together. How far right the slider is represented
 * in the JButton by a percentage between 0 and 100 inclusive. Only works to modify the
 * music and sound volumes of the game.
 * 
 * @author Yang Mattew
 *
 */
public class SliderButtonCombo extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSlider volumeSlider;
	private JTextField volumeAmount;
	
	private String option;
	private int optionValue;

	
	public SliderButtonCombo(String option) {
		this.option = option;
		if(option == "music") {
			optionValue = ScreenParameters.getMusicVolume();
		}
		if(option == "sound") {
			optionValue = ScreenParameters.getSoundVolume();
		}
		volumeSlider =new JSlider(JSlider.HORIZONTAL, 0, 100, optionValue);
		volumeSlider.addChangeListener(new volumeSliderListener());
		volumeSlider.setBackground(ScreenParameters.BOARDCOLOR);
		
		volumeAmount = new JTextField(3);
		volumeAmount.addActionListener(new volumeAmountListener());
		volumeAmount.setText(Integer.toString(optionValue));
		volumeAmount.setFont(new Font(volumeAmount.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE)));
		
		add(BorderLayout.WEST, volumeSlider);
		add(BorderLayout.WEST, volumeAmount);
		setBackground(ScreenParameters.BOARDCOLOR);
	}
	
	public void setVolume(int newValue) {
		if(option == "music") {
			ScreenParameters.setMusicVolume(newValue);
		}
		if(option == "sound") {
			ScreenParameters.setSoundVolume(newValue);
		}
	}
	
	public class volumeSliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
			int sliderValue = volumeSlider.getValue();
			setVolume(sliderValue);
			volumeAmount.setText(Integer.toString(sliderValue));
		}
		
	}
	
	public class volumeAmountListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			int textValueInt;
			try {
				String textValueString = volumeAmount.getText();
				textValueInt = Integer.parseInt(textValueString);
			} catch(NumberFormatException nfe) { // User decided to write letters or symbols
				textValueInt = 0;
			}
			textValueInt = (textValueInt > 100)? textValueInt = 100 : textValueInt;
			textValueInt = (textValueInt < 0)? textValueInt = 0 : textValueInt;

			setVolume(textValueInt);
			volumeAmount.setText(Integer.toString(textValueInt));
			volumeSlider.setValue(textValueInt);
		}
		
	}
}
