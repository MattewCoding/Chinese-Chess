package outOfGameScreens;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

import outOfGameScreens.menus.AbstractMenu;
import outOfGameScreens.menus.MainMenu;
import outOfGameScreens.menus.AbstractMenu.backButtonListener;

/*
 * @author Rona Nasro
 */

public class EndGame extends JFrame {
    
	private Profile winner;
	private Profile loser;
	
	
	private JLabel winnerTextLabel;
    private JLabel winnerLabel;
    private JLabel loserLabel;
    private JLabel winnerScore;
    private JLabel loserScore;
    private JLabel winnerScoreText;
    private JLabel loserScoreText;
    private JLabel timer1Label;
    private JLabel timer2Label;
    private JLabel timer1LabelText;
    private JLabel timer2LabelText;
    
    private Container c ;
    
    int WİDTH = 220;
	int winnerTextX = 100;
	int loserTextX = ScreenParameters.SCREENWIDTH/(winnerTextX+WİDTH)*30;
	int winnerScoreX = 65;
	int loserScoreX = ScreenParameters.SCREENWIDTH/(winnerScoreX+WİDTH)*20;
	int winnerScoreTextX = 140;
	int loserScoreTextX= ScreenParameters.SCREENWIDTH/(winnerScoreTextX+WİDTH)*50;
	int winnerTimerLabelX = 110;
	int loserTimerLabelX = ScreenParameters.SCREENWIDTH/(winnerTimerLabelX+WİDTH)*30;
	int winnerTimerLabelTextX = 118;
	int loserTimerLabelTextX = ScreenParameters.SCREENWIDTH/(winnerTimerLabelTextX+WİDTH)*32;
    
    protected Color boardColor = ScreenParameters.BOARDCOLOR;
    
    public EndGame(int winnerState,Profile player1, Profile player2){
    	
    	if(winnerState== 1) {
    		winner = player1;
    		loser = player2;
    	}else if(winnerState== 2){
    		winner = player2;
    		loser = player1;
    	}else {
    		winner = player2;
    		loser = player1;
    	}
        
        setSize(645,490);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        
        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        //panel.setLayout(new GridLayout(4,1));
        panel.setBackground(boardColor);
        panel.setLayout(null);
        panel2.setBackground(boardColor);
        panel2.setLayout(null);
        panel3.setBackground(boardColor);
        panel3.setLayout(null);
        panel.setBounds(0,ScreenParameters.SCREENHEIGHT/10,ScreenParameters.SCREENWIDTH/2,ScreenParameters.SCREENHEIGHT);
        panel2.setBounds(ScreenParameters.SCREENWIDTH/4-20,ScreenParameters.SCREENHEIGHT/10,ScreenParameters.SCREENWIDTH/2,ScreenParameters.SCREENHEIGHT);
        panel3.setBounds(0,0,ScreenParameters.SCREENWIDTH,ScreenParameters.SCREENHEIGHT/10);

        Border br = BorderFactory.createLineBorder(Color.black);
        
        if(winnerState == 0) {
        	winnerTextLabel = new JLabel("İt is a draw");
        }else {
        winnerTextLabel = new JLabel("Congragulations " + winner.getId() +" you won!");
        }
        winnerLabel = new JLabel(winner.getId());
        loserLabel = new JLabel(loser.getId());
        winnerScoreText = new JLabel("Your Score :");
        loserScoreText = new JLabel("Your Score :");
        winnerScore = new JLabel(String.valueOf(winner.getScore()));
        loserScore = new JLabel(String.valueOf(loser.getScore()));
        timer1Label = new JLabel(winner.getTimer().getElapsedTime());
        timer2Label = new JLabel(loser.getTimer().getElapsedTime());
        timer1LabelText = new JLabel("Timing :");
        timer2LabelText = new JLabel("Timing :");
        
        //styling
        winnerTextLabel.setFont(new Font("Monsserat", Font.BOLD, 25));
        winnerTextLabel.setForeground(Color.BLACK);
        winnerLabel.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 30));
        winnerLabel.setForeground(Color.GREEN);
        loserLabel.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 30));
        loserLabel.setForeground(Color.RED);
        winnerScore.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 30));
        loserScore.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 30));
        winnerScoreText.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.CENTER_BASELINE, 30));
        loserScoreText.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.CENTER_BASELINE, 30));
        timer1Label.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 20));
        timer2Label.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 20));
        timer1LabelText.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 20));
        timer2LabelText.setFont(new Font(winnerTextLabel.getFont().getFontName(), Font.BOLD, 20));
        
        
        
        //settings Bounds 
        winnerTextLabel.setBounds(ScreenParameters.SCREENHEIGHT/10,ScreenParameters.SCREENWIDTH/200,ScreenParameters.SCREENWIDTH,ScreenParameters.SCREENHEIGHT/10);
        winnerLabel.setBounds(winnerTextX,50, WİDTH,ScreenParameters.SCREENHEIGHT/20);
        loserLabel.setBounds(loserTextX,50, WİDTH,50);
        winnerScoreText.setBounds(winnerScoreX,100, WİDTH,130);
        loserScoreText.setBounds(loserScoreX,100, WİDTH,130);
        winnerScore.setBounds(winnerScoreTextX,150, WİDTH,130);
        loserScore.setBounds(loserScoreTextX,150, WİDTH,130);
        timer1LabelText.setBounds(winnerTimerLabelX,200, WİDTH,170);
        timer2LabelText.setBounds(loserTimerLabelX,200, WİDTH,170);
        timer1Label.setBounds(winnerTimerLabelTextX,250, WİDTH,170);
        timer2Label.setBounds(loserTimerLabelTextX,250, WİDTH,170);

        //adding the labels
        panel.add(winnerLabel);
        panel2.add(loserLabel);
        panel.add(winnerScoreText);
        panel2.add(loserScoreText);
        panel.add(winnerScore);
        panel2.add(loserScore);
        panel.add(timer1Label);
        panel2.add(timer2Label);
        panel.add(timer1LabelText);
        panel2.add(timer2LabelText);
        panel3.add(winnerTextLabel);
        panel.setBorder(br);
        panel2.setBorder(br);

        //adding the panels to the frame
        //super.add(c);
        c=getContentPane();
        c.add(panel2);
        c.add(panel);
        c.add(panel3);
        
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        
        
        
    }
    public static void main(String[] args) {
    	Profile player1 = new Profile("Rona",0,false);
		Profile player2 = new Profile("Mattew",0,true);
		new EndGame(1, player2, player2);
	}
  
}