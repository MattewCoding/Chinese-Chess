package game;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TimeTrackerInBoardGui extends JFrame {
  private static final long serialVersionUID = 1L;
private JLabel timeLabel;
  private JButton player1Button;
  private JButton player2Button;
  private Calendar calendar;
  private SimpleDateFormat timeFormat;
  private Timer timer;
  private boolean player1Turn;

  public TimeTrackerInBoardGui() {
    timeLabel = new JLabel();
    player1Button = new JButton("Player 1");
    player2Button = new JButton("Player 2");
    calendar = Calendar.getInstance();
    timeFormat = new SimpleDateFormat("HH:mm:ss");
    timeLabel.setText(timeFormat.format(calendar.getTime()));
    timeLabel.setPreferredSize(new Dimension(100,50));
    player1Turn = true;

    JPanel panel = new JPanel(new FlowLayout());
    panel.add(timeLabel);
    panel.add(player1Button);
    panel.add(player2Button);
    add(panel);

    player1Button.addActionListener(new Player1Listener());
    player2Button.addActionListener(new Player2Listener());
    player2Button.setEnabled(false);

    timer = new Timer(1000, new TimerListener());
    timer.start();
  }

  class TimerListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      calendar = Calendar.getInstance();
      timeLabel.setText(timeFormat.format(calendar.getTime()));
    }
  }

  class Player1Listener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      player1Turn = false;
      player1Button.setEnabled(false);
      player2Button.setEnabled(true);
    }
  }

  class Player2Listener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      player1Turn = true;
      player2Button.setEnabled(false);
      player1Button.setEnabled(true);
    }
  }

  public static void main(String[] args) {
    TimeTrackerInBoardGui TimeTrackerInBoardGui = new TimeTrackerInBoardGui();
    TimeTrackerInBoardGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    TimeTrackerInBoardGui.setSize(200,100);
    TimeTrackerInBoardGui.setVisible(true);
  }
}