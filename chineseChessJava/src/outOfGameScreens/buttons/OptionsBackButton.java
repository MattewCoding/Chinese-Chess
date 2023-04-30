package outOfGameScreens.buttons;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class OptionsBackButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OptionsBackButton(JFrame frame) {
		this(frame, "Cancel");
	}

	public OptionsBackButton(JFrame frame, String text) {
		super(text);
		setFont(new Font("Arial", Font.BOLD, 20));
		setForeground(Color.BLACK);
		setFocusPainted(false);
		setContentAreaFilled(false);
		setOpaque(true);
		setBackground(new Color(133, 150, 234));
		addActionListener(new BackButtonListener(frame));
	}
	
	public class BackButtonListener implements ActionListener{
		private JFrame frameToClose;
		
		public BackButtonListener(JFrame frame) {
			frameToClose = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			frameToClose.dispose();
		}
		
	}
}
