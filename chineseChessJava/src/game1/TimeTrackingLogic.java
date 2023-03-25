package game1;

import java.util.concurrent.TimeUnit;

public class TimeTrackingLogic {
	  private String player1, player2;
	  private int time;

	  public TimeTrackingLogic(String player1, String player2, int time) {
	    this.player1 = player1;
	    this.player2 = player2;
	    this.time = time;
	  }

	  public void start() {
	    int player1Time = time;
	    int player2Time = time;
	    boolean player1Turn = true;
	    boolean gameOver = false;

	    while (!gameOver) {
	      try {
	        TimeUnit.MINUTES.sleep(1);
	        if (player1Turn) {
	          player1Time--;
	        } else {
	          player2Time--;
	        }
	        if (player1Time == 0 || player2Time == 0) {
	          gameOver = true;
	        }
	        player1Turn = !player1Turn;
	      } catch (InterruptedException e) {
	        e.printStackTrace();
	      }
	    }

	    System.out.println("Game Over!");
	    if (player1Time == 0) {
	      System.out.println(player2 + " wins!");
	    } else {
	      System.out.println(player1 + " wins!");
	    }
	  }
	}