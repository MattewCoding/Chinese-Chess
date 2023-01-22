package onlineTesting;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class SwingDemo {
	public static void main(String[] args) {
		GraphicsEnvironment graphics =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = graphics.getDefaultScreenDevice();
		JFrame frame = new JFrame("Fullscreen");
		JPanel panel = new JPanel();
		JLabel label = new JLabel("", JLabel.CENTER);
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				
			}
		});
		label.setText("This is in fullscreen mode!");
		label.setOpaque(true);
		frame.add(panel);
		frame.add(label);
		frame.add(quit);
		frame.setUndecorated(false);
		frame.setResizable(true);
		//frame.setMinimumSize(new Dimension(300,300));
		frame.setVisible(true);
		device.setFullScreenWindow(frame);
	}
	
	
}
