package logic;

public class TimeTracker implements Runnable {
  private String player1, player2;
  private int time;

  public TimeTracker(String player1, String player2, int time) {
    this.player1 = player1;
    this.player2 = player2;
    this.time = time;
  }

  public void run() {
    TimeTrackingLogic timeTrackingLogic = new TimeTrackingLogic(player1, player2, time);
    timeTrackingLogic.start();
  }
}

