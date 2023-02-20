package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import logic.Move;
import logic.Moving;
import outOfGameScreens.ScreenParameters;

/**
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GUI extends JPanel implements MouseListener, Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Looks visually pleasing if the board is centered
	// and tall enough to fit most of the screen
	// but not too big to take up all of the screen
	int center = ScreenParameters.SCREENWIDTH/2;
	int margin = ScreenParameters.SCREENHEIGHT/18;
	int squareLength = (ScreenParameters.SCREENHEIGHT-margin*3)/11; // x3 to leave more space at bottom of screen
	
	int leftMostPixel = (int) (center-squareLength*(5.5));
	int chessboardEdge = leftMostPixel + 11 * squareLength;

	int fontMarginY = 75;
	
	// Board creation
	private int strokeWidth = 1;
	private Board board;
	private Graphics2D g2;
	
	// Notation history
	private int notationMargin = (int) (15 * ScreenParameters.XREDUCE);
	private NotationHistory pastMoves;
	private JTextArea pastMovesTextArea;
	private int leftNotationBoxX = chessboardEdge + notationMargin;
	private int rightMostX = ScreenParameters.SCREENWIDTH - notationMargin;
	private int topNotationY = 2*ScreenParameters.SCREENHEIGHT/5;
	
	// Mouse stuff
	private int mouseX = 0,mouseY = 0, pieceX = 0, pieceY = 0;
	
	//Piece moving
	private Piece movingPiece = null;
	private boolean mouseClickedPiece = false;
	private boolean mouseMovingPiece = false;
	
	// Timer
	private long startTime;
    private String elapsedTime = "00:00:00";
	
	private boolean run = false;
	
	public GUI(){
		board = new Board();
		pastMoves = new NotationHistory();
		
		pastMovesTextArea = new JTextArea("");
		
		addMouseListener(this);
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//Image background
		try {
			String imgLocation = "images/wood-background.jpg";
			Image background = ImageIO.read(new File(imgLocation));
			g.drawImage(background,0,0,null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Drawing board
		// Graphics class doesn't have method to thicken stroke width
		// Small screen sizes do not need thick lines to distinguish squares
		g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(ScreenParameters.SCREENWIDTH >= 1920) {
			strokeWidth = 4;
		} else {
			if(ScreenParameters.SCREENWIDTH >= 1080){
				strokeWidth = 2;
			}
		}
		g2.setStroke(new BasicStroke(strokeWidth));
		for(int row = 0; row<11; row++) {
			for(int col = 0; col<11; col++) {
				// Fill creates rectangle overlapping outline
				// So it needs to be smaller to avoid overlapping
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				
				if(row == 5) {
					// River needs different draw method
					drawFilledRectangle(topLeftX, topLeftY, squareLength*11, squareLength);
					row++; col--;
				}
				else {
					if((row<3 || row > 7) && (col>=3 && col<= 7)) { // Palace
						drawFilledRectangle(ScreenParameters.OUTLINECOLOR, ScreenParameters.PALACECOLOR, topLeftX, topLeftY, squareLength);
					}
					else {
						drawFilledRectangle(topLeftX, topLeftY, squareLength);
					}
				}

			}
			
		}
		
		setPieceImages();
		setNotation();
		drawTurnTimer();

		if(!run) {
			turnTimerPanel();
			Thread chronoThread = new Thread(this);
			chronoThread.start();
			run = true;
		}
		
		//Testing code
		g2.drawLine(30, 30, 30, 3000);
		g2.drawLine(30, 400, 500, 3000);
	}

	public void turnTimerPanel(){

		startTime = System.currentTimeMillis();

		Timer timer = new Timer(1000, new TimerActionListener());
		timer.start();

	}

	public void drawTurnTimer(){
		
		Rectangle.Float ellipse = new Rectangle.Float();
		Rectangle.Float ellipse2 = new Rectangle.Float();
		
        
        ellipse.setFrame(50, 50, 270, 90);
	    ellipse2.setFrame(1050, 50, 270, 90);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(244,227,166));
	    g2.draw(ellipse);
	    g2.fill(ellipse);
	    g2.setColor(new Color(226,192,106));
	    g2.draw(ellipse2);
	    g2.fill(ellipse2);
	    g2.setFont(new Font("Arial", Font.BOLD, 30));
	    g2.setColor(Color.black);
	    g2.drawString(elapsedTime, 115, 90);
	    g2.drawString(elapsedTime, 1115, 90);

	}

	public class TimerActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			long elapsed = System.currentTimeMillis() - startTime;
			int hours = (int) (elapsed / 3600000);
			int minutes = (int) ((elapsed - hours * 3600000) / 60000);
			int seconds = (int) ((elapsed - hours * 3600000 - minutes * 60000) / 1000);
			elapsedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			repaint();
		}
	}

	/**
	 * Places the different pieces on the board
	 */
	public void setPieceImages() {
		for(int col = 0; col<11; col++) {
			for(int row = 0; row<11; row++) {
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				Piece[][] pieceList = board.getCoords();
				Piece piece = pieceList[col][row];
				if (piece != null) {
					String fileName = piece.getImageName();
					Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
					g2.drawImage(scaledImage, topLeftX+5, topLeftY+5, squareLength-10, squareLength-10, null);

				}
			}
		}
	}
	
	/**
	 * Places the notation history onto the board on the left
	 */
	public void setNotation() {
		drawFilledRectangle(leftNotationBoxX, topNotationY, rightMostX - chessboardEdge - 2*notationMargin, ScreenParameters.SCREENHEIGHT/2);
		
		g2.setFont(new Font(getFont().getFontName(), Font.PLAIN, (int)(36*ScreenParameters.XREDUCE)));
		g2.setColor(ScreenParameters.OUTLINECOLOR);
		drawString(pastMovesTextArea.getText(), leftNotationBoxX + notationMargin, topNotationY);
	}

    private void drawString(String text, int x, int y) {
        for (String line : text.split("\n")) {
            g2.drawString(line, x, y += g2.getFontMetrics().getHeight());
        }
    }
	
	/**
	 * Sets the timer in the upper left
	 */
	public void setTimer2() {
		drawFilledRectangle(50, 50, 270, 90);
		drawFilledRectangle(1050, 50, 270, 90);
		
		g2.setColor(Color.black);
		//g2.drawString(Float.toString(t2.getTime()), 55, 55);
		
	}
	
	/**
	 * Updates the notation history list with the most recenely played move
	 * @param mostRecentMove The squares from which the piece was and moved to
	 * @param pieceMoved The piece in question that moved
	 */
	public void updateNotation(Move mostRecentMove, Piece pieceMoved) {
		pastMoves.updateNotation(mostRecentMove, pieceMoved);
		String recentMove = pastMoves.getPastMoves().get(pastMoves.getPastMovesSize()-1);
		pastMovesTextArea.setText(pastMovesTextArea.getText() + "\n" + recentMove);
	}
	
	@Override
	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		
		//TODO: Create separate class for displaying NotationHistory
		JFrame frame = new JFrame();
		JScrollPane scrollPane = new JScrollPane(this);
		frame.add(scrollPane);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		
		// Every 40ms, check if anything has been clicked
		while(run) {
			try {
				Thread.sleep(ScreenParameters.SLEEPAMOUNT);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			//System.out.println(mouseClickedPiece + " " + mouseMovingPiece);
			
			// Ensure that the game is not stopped during the iteration.
			if (run) {
				if(mouseClickedPiece && mouseMovingPiece) {
						Move move = new Move(pieceX, pieceY, mouseX, mouseY);
						Moving moving = new Moving(board,move);
						if(moving.isLegal()) {
							board.doMove(move);
							updateNotation(move, movingPiece);
							mouseClickedPiece = false;
						}
						mouseMovingPiece = false;
				}
				repaint();

			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {

		// floorDiv makes sure that negative numbers between -1 and 0 get rounded down to -1 and not 0
		mouseX = Math.floorDiv((e.getX()-leftMostPixel), squareLength);
		mouseY = Math.floorDiv((e.getY()-margin), squareLength);

		boolean xInRange = (mouseX >= 0) && (mouseX <= 10);
		boolean yInRange = (mouseY >= 0) && (mouseY <= 10);

		if(xInRange && yInRange) {
			if(mouseClickedPiece) {
				Piece newPiece = board.getCoords(mouseX,mouseY);
				boolean switchedPiece = false;
				if(newPiece != null) {
					switchedPiece = newPiece.getPlace() == movingPiece.getPlace();
					
					//Update current piece and location
					if(switchedPiece) {
						movingPiece = newPiece;
						pieceX = mouseX;
						pieceY = mouseY;
					}
				}
				mouseMovingPiece = !switchedPiece;
			}
			else {
				movingPiece = board.getCoords(mouseX,mouseY);
				pieceX = mouseX;
				pieceY = mouseY;
				
				mouseClickedPiece = movingPiece != null;
			}
		}
		// We want to click off the piece
		else {
			mouseClickedPiece = false;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Draws a filled square using default board color (i.e. not the palace color)
	 * @param g2 The graphics class that draws on the board
	 * @param topX The top left x-position of the rectangle
	 * @param topY The top left y-position of the rectangle
	 * @param length The amount of pixels the rectangle extends positively over the x and y axis
	 */
	public void drawFilledRectangle(int topX, int topY, int length) {
		drawFilledRectangle(ScreenParameters.OUTLINECOLOR, ScreenParameters.BOARDCOLOR, topX, topY, length, length);
	}
	
	/**
	 * Draws a filled rectangle using default board color (i.e. not the palace color)
	 * @param g2 The graphics class that draws on the board
	 * @param topX The top left x-position of the rectangle
	 * @param topY The top left y-position of the rectangle
	 * @param length The amount of pixels the rectangle extends positively over the x-axis
	 * @param height The amount of pixels the rectangle extends positively over the y-axis
	 */
	public void drawFilledRectangle(int topX, int topY, int length, int height) {
		drawFilledRectangle(ScreenParameters.OUTLINECOLOR, ScreenParameters.BOARDCOLOR, topX, topY, length, height);
	}
	
	/**
	 * Draws a filled square, where the outline does not overlap with the fill region
	 * @param g2 The graphics class that draws on the board
	 * @param outlineColor The color of the rectangle's outline
	 * @param fillColor The color of the rectangle's interior
	 * @param topX The top left x-position of the rectangle
	 * @param topY The top left y-position of the rectangle
	 * @param length The amount of pixels the rectangle extends positively over the x and y axis
	 */
	public void drawFilledRectangle(Color outlineColor, Color fillColor, int topX, int topY, int length) {
		drawFilledRectangle(outlineColor, fillColor, topX, topY, length, length);
	}
	
	/**
	 * Draws a filled rectangle, where the outline does not overlap with the fill region
	 * @param g2 The graphics class that draws on the board
	 * @param outlineColor The color of the rectangle's outline
	 * @param fillColor The color of the rectangle's interior
	 * @param topX The top left x-position of the rectangle
	 * @param topY The top left y-position of the rectangle
	 * @param length The amount of pixels the rectangle extends positively over the x-axis
	 * @param height The amount of pixels the rectangle extends positively over the y-axis
	 */
	public void drawFilledRectangle(Color outlineColor, Color fillColor, int topX, int topY, int length, int height) {
		Rectangle rOutline, rFill;
		rOutline = new Rectangle(topX, topY, length, height);
		rFill = new Rectangle(topX+strokeWidth/2,topY+strokeWidth/2, length-strokeWidth, height-strokeWidth);
		g2.setColor(outlineColor);
		g2.draw(rOutline);
		g2.setColor(fillColor);
		g2.fill(rFill);
	}

}