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
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.logging.log4j.Logger;

import bot.Bot;
import game.pieces.Piece;
import log.LoggerUtility;
import logic.moveChecking.PointVisitor;
import logic.boardChecking.BoardManager;
import logic.moveChecking.Move;
import logic.moveChecking.Moving;
import outOfGameScreens.EndGame;
import outOfGameScreens.Profile;
import outOfGameScreens.ScreenParam;
import outOfGameScreens.menus.SubMenu;

/**
 * This class is the panel in which the chess pieces are printed
 * 
 * @author Yang Mattew, Nasro Rona
 */
public class GUI extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;

	// Looks visually pleasing if the board is centered
	// and tall enough to fit most of the screen
	// but not too big to take up all of the screen
	private int center = ScreenParam.SCREENWIDTH/2;
	private int margin = ScreenParam.SCREENHEIGHT/18;
	private int squareLength = (ScreenParam.SCREENHEIGHT-margin*3)/11; // x3 to leave more space at bottom of screen

	private int leftMostPixel = (int) (center-squareLength*(5.5));
	private int chessboardEdge = leftMostPixel + 11 * squareLength;

	private int fontMarginY = 75;

	// Mouse stuff
	private int mouseX = 0,mouseY = 0, pieceX = 0, pieceY = 0;

	//Piece moving
	private Piece movingPiece = null;
	private boolean mouseClickedPiece = false;
	private boolean mouseMovingPiece = false;

	// Players
	private Profile player1, player2;
	private boolean redTurn = true;

	// Board creation
	private int strokeWidth = 1;
	private Board board;
	private Graphics2D g2;
	
	// Captured pieces 
	private ArrayList<Piece> capturedPieceRed;
	private ArrayList<Piece> capturedPieceBlack;
	private int capturedMargin = (int) (15 * ScreenParam.XREDUCE);
	private int deplacepiecesX = capturedMargin;
	
	// Music and Audio
	private int imageWidth = 30;
	private int imageHeight = 30;

	private int musicBoxX = center - imageWidth - 10;
	private int soundBoxX = center + 10;

	private int boxY = margin - imageHeight - 15;

	// Notation history
	private JTextArea pastMovesTextArea;
	private ArrayList<String> pastMovesArrayList;
	private NotationHistory pastMoves;
	private int notationMargin = (int) (15 * ScreenParam.XREDUCE);
	private int leftNotationBoxX = chessboardEdge + notationMargin;
	private int rightMostX = ScreenParam.SCREENWIDTH - notationMargin;
	private int topNotationY = 2*ScreenParam.SCREENHEIGHT/5;
	
	private boolean soundOn = true;
	private boolean musicOn = true;
	private String soundFilename = "src/music-sounds/sound.wav";
	private String musicFilename = "src/music-sounds/music.wav";
	private Clip clip;
	private Clip musicClip;

	// Surrender and Turn timer
	private int widthTurnTimer = 270;
	private int topButtonLeftX = 50;
	private int surrBoxHeight = 35;
	private int topButtonTopY = 150;

	// Points
	private PointVisitor searchValidMoves;
	
	private Bot bot;

	private static Logger logDataGUI = LoggerUtility.getLogger(SubMenu.class, "html");

	private String theme;
	
	/**
	 * Creates the main elements for the game board
	 * @param player1name Player 1's name
	 * @param player2name Player 2's name
	 * @param time1 Player 1's amount of starting time in minutes
	 * @param time2 Player 2's amount of starting time in minutes
	 * @param theme The chosen theme for the Xiangqi pieces
	 */
	public GUI(String player1name, String player2name, String time1, String time2, String theme) {
		board = new Board();
		
		this.theme=theme;

		pastMoves = new NotationHistory();
		pastMovesTextArea = new JTextArea("");
		pastMovesArrayList = new ArrayList<String>();
		capturedPieceRed = new ArrayList<Piece>();
		capturedPieceBlack = new ArrayList<Piece>();

		player1 = new Profile(player1name, 0, false, time1);
		player2 = new Profile(player2name, 0, true, time2);
		player2.getTimer().stop();

		bot = new Bot(player2, true, 4);
		searchValidMoves = new PointVisitor(board);
		
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File(musicFilename).getAbsoluteFile());
	        musicClip = AudioSystem.getClip();
	        musicClip.open(audioInputStream);
	        musicClip.start();
	    } catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

		addMouseListener(this);
		
		logCreationData();
	}
	
	public void logCreationData() {
		logDataGUI.info(board.toString());
		logDataGUI.info("Players created: Player 1 is " + player1.getId() + " and Player 2 is " + player2.getId());
	}

	/**
	 * Updates the state of the game
	 */
	public void checkPieces() {
		// Player1's turn (or the not-computer)
		if(redTurn || !player2.getId().equals("Computer Enemy")) {
			if(mouseClickedPiece && mouseMovingPiece) {

				// Check if move is legal
				Move move = new Move(pieceX, pieceY, mouseX, mouseY);
				Moving moving = new Moving(board, move);

				// This name is just as long as writing the right side of this equation
				// But it's much clearer to understand why the boolean is true or false
				boolean thePieceClickedOnIsRed = !board.getPiece(pieceX, pieceY).isBlack();
				//logDataGUI.info( "\n(redTurn && board.tryMove(move, player1) = " + (redTurn && board.tryMove(move, player1)) + " (!redTurn && board.tryMove(move, player2)) = " + (!redTurn && board.tryMove(move, player2)) );

				if(moving.isLegal() && (redTurn && BoardManager.tryMove(board, moving, player1)) || (!redTurn && BoardManager.tryMove(board, moving, player2))) {
					if(thePieceClickedOnIsRed == true) { // Player 1's turn is over
						player1.stopTurnTimer();
						player2.startTurnTimer();
					} else {
						player2.stopTurnTimer();
						player1.startTurnTimer();
					}
					redTurn = !redTurn;

					updateCaptured();
					updateNotation(move, movingPiece);
					playSound(soundFilename, !soundOn ,soundOn);
					mouseClickedPiece = false;
					
				}
				mouseMovingPiece = false;
			}
		} else {
			bot.updateBoard(board);
			Move move = bot.generateIdealMove();
			Moving moving = new Moving(board, move);
			BoardManager.tryMove(board, moving, player2);
			playSound(soundFilename, !soundOn ,soundOn);

			player2.stopTurnTimer();
			player1.startTurnTimer();
			redTurn = !redTurn;
			updateCaptured();
			updateNotation(move, move.getPiece());
		}
	}

	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//Image background
		String imgLocation = "src/images"+ScreenParam.PATHSEP+"wood-background.jpg";
		try {
			Image background = ImageIO.read(new File(imgLocation));
			g.drawImage(background,0,0,null);
		} catch (IOException e) {
			//logDataGUI.error("Image at " + imgLocation + " was not found.");
		}

		// Drawing board
		// Graphics class doesn't have method to thicken stroke width
		// Small screen sizes do not need thick lines to distinguish squares
		g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(ScreenParam.SCREENWIDTH >= 1920) {
			strokeWidth = 4;
		} else {
			if(ScreenParam.SCREENWIDTH >= 1080){
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
						CreateRectangle.drawFilledRectangle(g2, ScreenParam.OUTLINECOLOR, ScreenParam.PALACECOLOR, topLeftX, topLeftY, squareLength);
					}
					else {
						CreateRectangle.drawFilledRectangle(g2, topLeftX, topLeftY, squareLength);
					}
				}
			}
		}

		drawCapturedBox();
		drawMusicImages();
		drawNotation();
		drawPieceImages(theme);
		drawSurrender();
		drawTurnTimer();

		if(mouseClickedPiece) {
			drawPoints();
		}
	}
	
	/**
	 * Check whether the game has ended because the general is in checkmate, or time has run out
	 * @return boolean Whether the game has ended
	 */
	public boolean hasEnded() {
		if(Board.getWinner()!=-1) {
			player1.calculateScore();
			player2.calculateScore();
			player2.stopTurnTimer();
			player1.stopTurnTimer();
		}
		
		String timeLeft = player1.getTimer().getElapsedTime();
		String timeRight = player2.getTimer().getElapsedTime();
		boolean player1Out = timeLeft.equals("00:00");
		boolean player2Out = timeRight.equals("00:00");
		
		return Board.getWinner() != -1 || player1Out || player2Out;
	}

	/**
	 * Places the captured pieces onto the board on the right
	 */
	public void drawCapturedBox() {
		CreateRectangle.drawFilledRectangle(g2, capturedMargin, topNotationY, leftMostPixel - 2*capturedMargin, ScreenParam.SCREENHEIGHT/2);

		drawCapturedPieces(capturedPieceBlack, deplacepiecesX+10, topNotationY+10,deplacepiecesX+10);
		drawCapturedPieces(capturedPieceRed, deplacepiecesX+10, 2*topNotationY-10, deplacepiecesX+10);
	}

	/**
	 * Draw all the captured pieces
	 * @param capturedPiece The list of the captured pieces
	 * @param x The horizontal position of the initial drawing
	 * @param y The vertical position of the initial drawing
	 * @param xInitial The initial x-coordinate that will be compared to add the x coordinate to the next line
	 */
	public void drawCapturedPieces(ArrayList<Piece> capturedPiece, int x, int y, int xInitial) {
		for(Piece captured : capturedPiece) {
			String fileName = captured.getImageName();
			Image scaledImage = new ImageIcon("./src/images/pieces/chinese_"+fileName).getImage();
			
			g2.drawImage(scaledImage, x, y,squareLength-10, squareLength-10, null);
			if(x<7*xInitial) {
				x  += 35;
			}else {
				y += 50;
				x = xInitial;
			}
		}
	}

	/**
	 * Draw the icons that you can click on to toggle the music and sound
	 */
	public void drawMusicImages() {
		Image musicImage = null;
		Image soundImage = null;
		try {
			if(musicOn) {
				musicImage = ImageIO.read(new File("src/images/logo/musicon.png"));
			}else {
				musicImage = ImageIO.read(new File("src/images/logo/musicoff.png"));
			}
			
			if(soundOn) {
				soundImage = ImageIO.read(new File("src/images/logo/soundon.png"));
			}else {
				soundImage = ImageIO.read(new File("src/images/logo/soundoff.png"));
			}
			
		} catch (IOException e) {
		    e.printStackTrace();
		}
		// draw music box
		CreateRectangle.drawFilledRectangle(g2, ScreenParam.OUTLINECOLOR, ScreenParam.BOARDCOLOR, musicBoxX, boxY, imageHeight, imageWidth);
		g2.drawImage(musicImage, musicBoxX + 2, boxY + 2, imageWidth - 4, imageHeight - 4, null);

		// draw sound box
		CreateRectangle.drawFilledRectangle(g2, ScreenParam.OUTLINECOLOR, ScreenParam.BOARDCOLOR, soundBoxX, boxY, imageHeight, imageWidth);
		g2.drawImage(soundImage, soundBoxX + 2, boxY + 2, imageWidth - 4, imageHeight - 4, null);
	}

	/**
	 * Draws the notation history onto the board on the left
	 */
	public void drawNotation() {
		CreateRectangle.drawFilledRectangle(g2, leftNotationBoxX, topNotationY, rightMostX - chessboardEdge - 2*notationMargin, ScreenParam.SCREENHEIGHT/2);

		g2.setFont(new Font(getFont().getFontName(), Font.PLAIN, (int)(36*ScreenParam.XREDUCE)));
		g2.setColor(ScreenParam.OUTLINECOLOR);
		drawString(pastMovesTextArea.getText(), leftNotationBoxX + notationMargin, topNotationY);
	}

	/**
	 * Draws the moves made using grpahic2D's drawString inside of the notation box
	 * @param text The most recent moves
	 * @param x The left x-position of the text
	 * @param y The top y-position of the text
	 */
	public void drawString(String text, int x, int y) {
		for (String line : pastMovesArrayList) {
			g2.drawString(line, x, y += g2.getFontMetrics().getHeight());
		}
	}
	
	/**
	 * Places the different pieces on the board
	 * @param theme The theme of the board
	 */
	public void drawPieceImages(String theme) {
		for(int col = 0; col<11; col++) {
			for(int row = 0; row<11; row++) {
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				Piece[][] pieceList = board.getCoords();
				Piece piece = pieceList[col][row];
				if (piece != null) {
					String fileName = piece.getImageName();
					Image scaledImage = new ImageIcon(ScreenParam.IMAGESELECT+theme+"_"+fileName).getImage();
					g2.drawImage(scaledImage, topLeftX+5, topLeftY+5, squareLength-10, squareLength-10, null);
				}
			}
		}
	}
	
	/**
	 * Draws the button the player uses to surrender
	 */
	public void drawSurrender(){
		//Rectangle calculation
		int surrBlackLeftX = ScreenParam.SCREENWIDTH -(topButtonLeftX + widthTurnTimer);

		g2.setFont(new Font(getFont().getFontName(), Font.BOLD, 25));
		FontMetrics metrics = g2.getFontMetrics(getFont());
		String surr = "Surrender";
		int offset = 7*metrics.stringWidth(surr)/6; // 7/6 is weird but it works...
		
		int surrRedPlayerX = topButtonLeftX + widthTurnTimer/2 - offset;
		CreateRectangle.drawFilledRectangle(g2, topButtonLeftX, topButtonTopY, widthTurnTimer, surrBoxHeight);
		g2.setColor(Color.black);
		g2.drawString(surr, surrRedPlayerX, 177);
		
		if(!player2.getId().equals("Computer Enemy")) {
			int surrBlackPlayerX = surrBlackLeftX + widthTurnTimer/2 - offset;
			CreateRectangle.drawFilledRectangle(g2, surrBlackLeftX, topButtonTopY, widthTurnTimer, surrBoxHeight);
			g2.setColor(Color.black);
			g2.drawString(surr, surrBlackPlayerX, 177);
		}

	}
	
	/**
	 * Draws the turn timer for both players
	 */
	public void drawTurnTimer(){
		//Rectangle calculation
		int timerBlackLeftX = ScreenParam.SCREENWIDTH -(topButtonLeftX + widthTurnTimer);

		String p1ID = player1.getId();
		String p2ID = player2.getId();
		String timeLeft = player1.getTimer().getElapsedTime();
		String timeRight = player2.getTimer().getElapsedTime();

		CreateRectangle.drawFilledRectangle(g2, topButtonLeftX, 50, widthTurnTimer, 90);
		CreateRectangle.drawFilledRectangle(g2, timerBlackLeftX, 50, widthTurnTimer, 90);

		g2.setFont(new Font(getFont().getFontName(), Font.BOLD, 25));
		FontMetrics metrics = g2.getFontMetrics(getFont());

		// Determine the X coordinate for the text
		// I'm not actually sure why we need to multiply the string width
		// By 9/8, (it should be 1), but if it works...
		int timerRedPlayerX = topButtonLeftX + widthTurnTimer/2 - 9*metrics.stringWidth(p1ID)/8;
		int timerBlackPlayerX = timerBlackLeftX + widthTurnTimer/2 - 9*metrics.stringWidth(p2ID)/8;

		g2.setColor(Color.black);
		g2.drawString(p1ID, timerRedPlayerX, 75);
		g2.drawString(p2ID, timerBlackPlayerX, 75);

		g2.setFont(new Font(getFont().getFontName(), Font.BOLD, 40));
		metrics = g2.getFontMetrics(getFont());
		int timerRedCountX = topButtonLeftX + widthTurnTimer/2 - 5*metrics.stringWidth(timeLeft)/3;
		int timerBlackCountX = timerBlackLeftX + widthTurnTimer/2 - 5*metrics.stringWidth(timeRight)/3;
		
		g2.drawString(timeLeft, timerRedCountX, 118);
		g2.drawString(timeRight, timerBlackCountX, 118);

	}

	/**
	 * Draw the points that the piece can move to on the board.
	 */
	public void drawPoints() {
		ArrayList<Integer[]> legalMoves = movingPiece.accept(searchValidMoves);

		int circleSize = squareLength*2/5;
		int displacement = squareLength - circleSize;

		for(Integer[] legalCoord : legalMoves) {
			int col = legalCoord[0], row = legalCoord[1];

			int topLeftX = leftMostPixel + col * squareLength + displacement/2;
			int topLeftY = margin + row * squareLength + displacement/2;

			drawCircle(new Color(0f,0f,0f,.5f), topLeftX, topLeftY, circleSize);
		}
	}

	/**
	 * Draw a circle
	 * @param fillColor The color of the circle's interior
	 * @param leftX The leftmost x-coordinate of the circle
	 * @param topY The topmost y-coordinate of the circle
	 * @param size The diameter of the circle in pixels
	 */
	public void drawCircle(Color fillColor, int leftX, int topY, int size) {
		Ellipse2D c1 = new Ellipse2D.Double(leftX, topY, size, size);
		g2.setColor(fillColor);
		g2.fill(c1);
	}

	/**
	 * Updates the notation history list with the most recenely played move
	 * @param mostRecentMove The squares from which the piece was and moved to
	 * @param pieceMoved The piece in question that moved
	 */
	public void updateNotation(Move mostRecentMove, Piece pieceMoved) {
		pastMoves.updateNotation(mostRecentMove, pieceMoved);

		if(redTurn) {
			String recentMove = pastMoves.getPastMoves().get(pastMoves.getPastMovesSize()-1);
			pastMovesTextArea.setText(recentMove + "\n" + pastMovesTextArea.getText());

			pastMovesArrayList.add(recentMove);
		}

		int notationBoxSize = ScreenParam.SCREENHEIGHT/2;
		int turnsFittableInBox = (int) (notationBoxSize/g2.getFontMetrics().getHeight());
		if(pastMovesArrayList.size() > turnsFittableInBox) {
			pastMovesArrayList.remove(0);
		}
	}

	/*
	 * Updates all the captured pieces
	 */
	public void updateCaptured() {
		capturedPieceBlack = player1.getPiecesCaptured();
		capturedPieceRed = player2.getPiecesCaptured();
	}
	
	/**
	 * Play the music chosen for this game
	 * @param play Whether to start or stop the music
	 */
	public void playMusic(boolean play) {
		if(play) {
	        AudioInputStream audioInputStream;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(new File(musicFilename).getAbsoluteFile());
		        musicClip = AudioSystem.getClip();
		        musicClip.open(audioInputStream);
		        musicClip.start();
		    } catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		} else {
			musicClip.stop();
        }
		
	}
	
	/**
	 * Play the sound to move or capture a piece
	 * @param filename The location of the sound file
	 * @param stop Whether to stop the music
	 * @param play Whether to play the music
	 */
	public void playSound(String filename, boolean stop, boolean play) {
		if(play) {
		    try {
		    	if (stop) {
		            clip.stop();
		            return;
		        }
		        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename).getAbsoluteFile());
		        Clip clip = AudioSystem.getClip();
		        clip.open(audioInputStream);
		        clip.start();
		    } catch (UnsupportedAudioFileException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (LineUnavailableException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public Profile getPlayer1() {
		return player1;
	}
	
	public Profile getPlayer2() {
		return player2;
	}

	@Override
	public void mousePressed(MouseEvent e) {

		/** Mouse detecting pieces **/

		// floorDiv makes sure that negative numbers between -1 and 0 get rounded down to -1 and not 0
		// (casting directly to int does that for some reason)
		mouseX = Math.floorDiv((e.getX()-leftMostPixel), squareLength);
		mouseY = Math.floorDiv((e.getY()-margin), squareLength);

		boolean xInRange = (mouseX >= 0) && (mouseX <= 10);
		boolean yInRange = (mouseY >= 0) && (mouseY <= 10);

		if(xInRange && yInRange) {
			if(mouseClickedPiece) {
				Piece newPiece = board.getPiece(mouseX,mouseY);
				boolean canSwitchPlace = false;
				if(newPiece != null) {
					canSwitchPlace = newPiece.isBlack() == movingPiece.isBlack();

					//Update current piece and location
					if(canSwitchPlace) {
						movingPiece = newPiece;
						pieceX = mouseX;
						pieceY = mouseY;
					}
				}
				mouseMovingPiece = !canSwitchPlace;
			} else {
				movingPiece = board.getPiece(mouseX,mouseY);
				pieceX = mouseX;
				pieceY = mouseY;

				if(movingPiece != null) {
					mouseClickedPiece = redTurn != movingPiece.isBlack();
				}
			}
		}
		// We want to click off the piece
		else {
			mouseClickedPiece = false;
		}
		
		/** Mouse detecting audio change **/
		
		boolean xSoundRange = (e.getX() >= soundBoxX) && (e.getX() <= imageWidth+soundBoxX);
		boolean ySoundRange = (e.getY() >= boxY) && (e.getY() <= imageHeight+boxY);
		
		boolean xMusicRange = (e.getX() >= musicBoxX) && (e.getX() <= imageWidth+musicBoxX);
		boolean yMusicRange = (e.getY() >= boxY) && (e.getY() <= imageHeight+boxY);
		
		if(xSoundRange && ySoundRange) {
			soundOn = !soundOn;
	    }
		if(xMusicRange && yMusicRange) {
	        musicOn = !musicOn;
            playMusic(musicOn);
	    }
		
		/** Mouse detecting surrendering **/
		
		boolean xBlackSurrRange = e.getX() >= topButtonLeftX && e.getX() <= topButtonLeftX + widthTurnTimer;
		boolean yBlackSurrRange = e.getY() >= topButtonTopY && e.getY() <= topButtonTopY + surrBoxHeight;
		if(xBlackSurrRange && yBlackSurrRange) {
			Board.setWinner(BoardManager.PLAYER1_WINS);
		}

		if(!player2.getId().equals("Computer Enemy")) {
			int topButtonRightX = ScreenParam.SCREENWIDTH -(topButtonLeftX + widthTurnTimer);
			boolean xRedSurrRange = e.getX() >= topButtonRightX && e.getX() <= topButtonRightX + widthTurnTimer;
			boolean yRedSurrRange = e.getY() >= topButtonTopY && e.getY() <= topButtonTopY + surrBoxHeight;
			if(xRedSurrRange && yRedSurrRange) {
				Board.setWinner(BoardManager.PLAYER2_WINS);
			}
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
