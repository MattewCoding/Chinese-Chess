package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import logic.Move;
import logic.Moving;
import logic.TimerListener;
import outOfGameScreens.Profile;
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
	private JTextArea pastMovesTextArea;
	private ArrayList<String> pastMovesArrayList;
	private NotationHistory pastMoves;
	private int notationMargin = (int) (15 * ScreenParameters.XREDUCE);
	private int leftNotationBoxX = chessboardEdge + notationMargin;
	private int rightMostX = ScreenParameters.SCREENWIDTH - notationMargin;
	private int topNotationY = 2*ScreenParameters.SCREENHEIGHT/5;
	
	// Captured pieces 
	private ArrayList<Piece> capturedPieceRed;
	private ArrayList<Piece> capturedPieceBlack;
	private int capturedMargin = (int) (15 * ScreenParameters.XREDUCE);
	private int rightCapturedBoxX = chessboardEdge - 81*capturedMargin;
	private int deplacepiecesX = rightCapturedBoxX;
	
	// Mouse stuff
	private int mouseX = 0,mouseY = 0, pieceX = 0, pieceY = 0;
	
	//Piece moving
	private Piece movingPiece = null;
	private boolean mouseClickedPiece = false;
	private boolean mouseMovingPiece = false;

	// players
	private Profile player1, player2;
	private boolean blackTurn = true;

	
	private boolean run = false;
	
	public GUI(){
		board = new Board();
		
		pastMoves = new NotationHistory();
		pastMovesTextArea = new JTextArea("");
		pastMovesArrayList = new ArrayList<String>();
		capturedPieceRed = new ArrayList<Piece>();
		capturedPieceBlack = new ArrayList<Piece>();
		
		//timerListener = new TimerListener();
		player1 = new Profile("Rona",0,true);
		player2 = new Profile("Mattew",0,false);
		player2.getTimer().stop();
		
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
					CreateRectangle.drawFilledRectangle(g2, topLeftX, topLeftY, squareLength*11, squareLength);
					row++; col--;
				}
				else {
					if((row<3 || row > 7) && (col>=3 && col<= 7)) { // Palace
						CreateRectangle.drawFilledRectangle(g2, ScreenParameters.OUTLINECOLOR, ScreenParameters.PALACECOLOR, topLeftX, topLeftY, squareLength);
					}
					else {
						CreateRectangle.drawFilledRectangle(g2, topLeftX, topLeftY, squareLength);
					}
				}

			}
			
		}
		
		setPieceImages();
		drawTurnTimer();
		setNotation();
		setCapturedPieces();

		if(!run) {
			
			Thread chronoThread = new Thread(this);
			chronoThread.start();
			run = true;
		}
	}

	public void drawTurnTimer(){
		//Rectangle calculations
		int widthTurnTimer = 270;
		int timerRedLeftX = 50;
		int timerBlackLeftX = ScreenParameters.SCREENWIDTH-(timerRedLeftX+widthTurnTimer);

		String p1ID = player1.getId();
		String p2ID = player2.getId();
		String timeLeft = player1.getTimer().getElapsedTime();
		String timeRight = player2.getTimer().getElapsedTime();

		g2.setFont(new Font(getFont().getFontName(), Font.BOLD, 30));
	    FontMetrics metrics = g2.getFontMetrics(getFont());
	    
	    // Determine the X coordinate for the text
	    // You know, I'm not actually sure why we need to multiply the string width
	    // By 4/3, (it should be 1/2), but if it works...
	    int timerRedPlayerX = timerRedLeftX + widthTurnTimer/2 - 4*metrics.stringWidth(p1ID)/3;
	    int timerBlackPlayerX = timerBlackLeftX + widthTurnTimer/2 - 4*metrics.stringWidth(p2ID)/3;
	    
	    int timerRedCountX = timerRedLeftX + widthTurnTimer/2 - 4*metrics.stringWidth(timeLeft)/3;
	    int timerBlackCountX = timerBlackLeftX + widthTurnTimer/2 - 4*metrics.stringWidth(timeRight)/3;

		CreateRectangle.drawFilledRectangle(g2, timerRedLeftX, 50, widthTurnTimer, 90);
		CreateRectangle.drawFilledRectangle(g2, timerBlackLeftX, 50, widthTurnTimer, 90);
		
		g2.setColor(Color.black);
		g2.drawString(p1ID, timerRedPlayerX, 75);
		g2.drawString(p2ID, timerBlackPlayerX, 75);
		g2.drawString(timeLeft, timerRedCountX, 118);
		g2.drawString(timeRight, timerBlackCountX, 118);

	}
	
	/**
	 * Places the notation history onto the board on the left
	 */
	public void setNotation() {
		CreateRectangle.drawFilledRectangle(g2, leftNotationBoxX, topNotationY, rightMostX - chessboardEdge - 2*notationMargin, ScreenParameters.SCREENHEIGHT/2);
		
		g2.setFont(new Font(getFont().getFontName(), Font.PLAIN, (int)(36*ScreenParameters.XREDUCE)));
		g2.setColor(ScreenParameters.OUTLINECOLOR);
		drawString(pastMovesTextArea.getText(), leftNotationBoxX + notationMargin, topNotationY);
	}

    private void drawString(String text, int x, int y) {
        for (String line : pastMovesArrayList) {
            g2.drawString(line, x, y += g2.getFontMetrics().getHeight());
        }
    }
	
	/**
	 * Places the captured pieces onto the board on the right
	 */
	public void setCapturedPieces() {
		CreateRectangle.drawFilledRectangle(g2, rightCapturedBoxX, topNotationY, rightMostX - chessboardEdge - 2*notationMargin, ScreenParameters.SCREENHEIGHT/2);

			drawİmage(capturedPieceBlack, deplacepiecesX+10, topNotationY+10,deplacepiecesX+10);
			drawİmage(capturedPieceRed, deplacepiecesX+10, 2*topNotationY-5,deplacepiecesX+10);
		}
    
    
    /*
     * to draw all the captured pieces
     * @param the list of the captured pieces, the x and y are the initial coordinates and the xİnitial is also the initial x-coordinate that will be compared to add the x coordinate to the next line
     * @return draws the pieces
     */
    private void drawİmage(ArrayList<Piece> capturedPiece, int x, int y, int xİnitial) {
    	for(Piece captured : capturedPiece) {
			String fileName = captured.getImageName();
			Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
			g2.drawImage(scaledImage, x, y,squareLength-10, squareLength-10, null);
			if(x<7*xİnitial) {
				x  += 35;
			}else {
				y += 50;
				x = xİnitial;
			}
		}
    }
	
	/**
	 * Updates the notation history list with the most recenely played move
	 * @param mostRecentMove The squares from which the piece was and moved to
	 * @param pieceMoved The piece in question that moved
	 */
	public void updateNotation(Move mostRecentMove, Piece pieceMoved) {
		pastMoves.updateNotation(mostRecentMove, pieceMoved);
		String recentMove = pastMoves.getPastMoves().get(pastMoves.getPastMovesSize()-1);
		pastMovesTextArea.setText(recentMove + "\n" + pastMovesTextArea.getText());
		
		pastMovesArrayList.add(recentMove);
		
    	int notationBoxSize = ScreenParameters.SCREENHEIGHT/2;
    	int turnsFittableInBox = (int) (notationBoxSize/g2.getFontMetrics().getHeight());
    	if(pastMovesArrayList.size() > turnsFittableInBox) {
    		pastMovesArrayList.remove(0);
    	}
	}
	
	/*
	 * updates all the captured pieces
	 */
	public void updateCaptured() {
		capturedPieceBlack = player1.getPiecesCaptured();
		capturedPieceRed = player2.getPiecesCaptured();
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
	 * Sets the timer in the upper left
	 */
	public void setTimer2() {
		CreateRectangle.drawFilledRectangle(g2, 50, 50, 270, 90);
		CreateRectangle.drawFilledRectangle(g2, 1050, 50, 270, 90);
		
		g2.setColor(Color.black);
		//g2.drawString(Float.toString(t2.getTime()), 55, 55);
		
	}
	
	@Override
	public void run() {
		
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
					
						// Check if move is legal
						Move move = new Move(pieceX, pieceY, mouseX, mouseY);
						Moving moving = new Moving(board,move);
						
						// This name is just as long as writing the right side of this equation
						// But it's much clearer to understand why the boolean is true or false
						boolean thePieceClickedOnIsBlack = board.getCoords(pieceX, pieceY).isBlack();
						Piece thePieceBeingAttacked = board.getCoords(mouseX, mouseY);
						
						if(moving.isLegal() && blackTurn != thePieceClickedOnIsBlack) {
							if(thePieceClickedOnIsBlack != true) { // Player 1's turn is over
								player1.stopTurnTimer();
								player2.startTurnTimer();
							}else{
								player2.stopTurnTimer();
								player1.startTurnTimer();
							}
							blackTurn = !blackTurn;
							
							board.doMove(move);
							
							if(thePieceBeingAttacked != null) {
								if(thePieceBeingAttacked.isBlack()) {
									player1.addPieceCaptured(thePieceBeingAttacked);
								}
								else {
									player2.addPieceCaptured(thePieceBeingAttacked);
								}
								updateCaptured();
							}
							
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
		// (casting directly to int does that for some reason)
		mouseX = Math.floorDiv((e.getX()-leftMostPixel), squareLength);
		mouseY = Math.floorDiv((e.getY()-margin), squareLength);

		boolean xInRange = (mouseX >= 0) && (mouseX <= 10);
		boolean yInRange = (mouseY >= 0) && (mouseY <= 10);

		if(xInRange && yInRange) {
			if(mouseClickedPiece) {
				Piece newPiece = board.getCoords(mouseX,mouseY);
				boolean switchedPiece = false;
				if(newPiece != null) {
					switchedPiece = newPiece.isBlack() == movingPiece.isBlack();
					
					//Update current piece and location
					if(switchedPiece) {
						movingPiece = newPiece;
						pieceX = mouseX;
						pieceY = mouseY;
					} else {
						
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
}