package game;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import game.GUI;
import game1.TimeTrackingLogic;
import game1.TimeTracker;
import outOfGameScreens.Profile;
import outOfGameScreens.ScreenParameters;

public class TimeTrackerGUI {
  private JFrame frame;
  private JTextField player1Field;
  private JTextField player2Field;
  private JTextField timeField;
  private JLabel resultLabel;

  public TimeTrackerGUI() {
    frame = new JFrame("Time Tracker");
    frame.setSize(ScreenParameters.SCREENWIDTH, ScreenParameters.SCREENHEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new GridLayout(4, 1));

    JPanel player1Panel = new JPanel();
    player1Panel.add(new JLabel("Player 1:"));
    player1Field = new JTextField(10);
    player1Panel.add(player1Field);

    JPanel player2Panel = new JPanel();
    player2Panel.add(new JLabel("Player 2:"));
    player2Field = new JTextField(10);
    player2Panel.add(player2Field);

    JPanel timePanel = new JPanel();
    timePanel.add(new JLabel("Time (minutes):"));
    timeField = new JTextField(10);
    if(timeField.getText().isBlank()){
    }
    timePanel.add(timeField);
    
    JPanel startPanel = new JPanel();
    JButton startButton = new JButton("Start");
    startButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
    	//close the TimeTrackerGUi frame
    	//open the Board GUI
    	GUI gui = new GUI();
        String player1 = player1Field.getText();
        String player2 = player2Field.getText();
        int time = Integer.parseInt(timeField.getText());

        TimeTracker timeTracker = new TimeTracker(player1, player2, time);
        Thread thread = new Thread(timeTracker);
        thread.start();
        frame.setContentPane(gui);
        frame.revalidate();
      }
    });
    
    startPanel.add(startButton);

    JPanel resultPanel = new JPanel();
    resultLabel = new JLabel("");
    resultPanel.add(resultLabel);

    frame.add(player1Panel);
    frame.add(player2Panel);
    frame.add(timePanel);
    frame.add(startPanel);
    frame.add(resultPanel);

    frame.setVisible(true);
  }
  

  public static void main(String[] args) {
    new TimeTrackerGUI();
  }
}
