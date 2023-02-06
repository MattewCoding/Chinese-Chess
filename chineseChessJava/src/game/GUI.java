package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import outOfGameScreens.MainMenu;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
/**
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GUI extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Screen size
	private final int SCREENWIDTH, SCREENHEIGHT;

	//Graphic attributes
	private int strokeWidth = 1;
	private Board board;
	
	//Execution attributes
	private boolean run = false;
	
	public GUI(MainMenu menuInstance) {
		SCREENWIDTH = menuInstance.getScreenWidth();
		SCREENHEIGHT = menuInstance.getScreenHeight();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g; // 2D version allows for adjustable stroke width
		
		setBackground(g);
		setBoardDrawing(g2);
		setPieceImages(g2);
	}
	
	/**
	 * Creates the background image for the chess game
	 * @param g The graphics instance that will show the background image
	 */
	private void setBackground(Graphics g) {
		try {
			String imgLocation = "images/wood-background.jpg";
			Image background = ImageIO.read(new File(imgLocation));
			int bgWidth = background.getWidth(null);

			if(bgWidth != SCREENWIDTH) {
				// Resize image if necessary
				String imgOut = "images/test1.png";
				ImageResizer.resize(imgLocation, imgOut, ((double)SCREENWIDTH) / ((double)bgWidth));
				File fileOut = new File(imgOut);
				background = ImageIO.read(fileOut);
				g.drawImage(background,0,0,null);
				fileOut.delete();
			}

			else {
				g.drawImage(background,0,0,null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param g2 The graphics instance that will show the background image
	 */
	private void setBoardDrawing(Graphics2D g2) {
		// Looks visually pleasing if the board is centered
		// and tall enough to fit most of the screen
		// but not too big to take up all of the screen
		int margin = SCREENHEIGHT/18;
		int squareLength = (SCREENHEIGHT-margin*3)/11; // x3 to leave more space at bottom of screen
		int center = SCREENWIDTH/2;
		int leftMostPixel = (int) (center-squareLength*(5.5)); // 11 wide therefore 5.5 squares on each side

		// Drawing board
		// Graphics class doesn't have method to thicken stroke width
		// Small screen sizes do not need thick lines to distinguish squares
		if(SCREENWIDTH >= 1920) {
			strokeWidth = 4;
		} else {
			if(SCREENWIDTH >= 1080){
				strokeWidth = 2;
			}
		}
		g2.setStroke(new BasicStroke(strokeWidth));
		Rectangle rOutline, rFill;
		for(int row = 0; row<11; row++) {
			for(int col = 0; col<11; col++) {
				// Fill creates rectangle overlapping outline, so it needs to be smaller
				// to avoid overlapping
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				rOutline = new Rectangle(topLeftX,topLeftY,squareLength, squareLength);
				rFill = new Rectangle(topLeftX+strokeWidth/2,topLeftY+strokeWidth/2, squareLength-strokeWidth, squareLength-strokeWidth);
				if(row == 5) {
					// River needs different draw method
					int fullLength = squareLength*11;
					rOutline = new Rectangle(topLeftX,topLeftY,fullLength,squareLength);
					rFill = new Rectangle(topLeftX+strokeWidth/2,topLeftY+strokeWidth/2, fullLength-strokeWidth, squareLength-strokeWidth);
					row++; col--;
				}

				//TODO: This will cause problems for variants
				//Make the square appear on the screen
				g2.setColor(new Color(158,79,34));
				g2.draw(rOutline);
				if((row<3 || row > 7) && (col>=3 && col<= 7)) {
					g2.setColor(new Color(226,192,106));
				}
				else {
					g2.setColor(new Color(244,227,166));
				}
				g2.fill(rFill);

			}
			board= new Board();
		}
	}

	private void setPieceImages(Graphics2D g2) {
		int margin = SCREENHEIGHT/18;
		int squareLength = (SCREENHEIGHT-margin*3)/11; // x3 to leave more space at bottom of screen
		int center = SCREENWIDTH/2;
		int leftMostPixel = (int) (center-squareLength*(5.5));

		ComponentMove listener = new ComponentMove(this);
		for(int col = 0; col<11; col++) {
			for(int row = 0; row<11; row++) {
				int topLeftX = leftMostPixel + col * squareLength;
				int topLeftY = margin + row * squareLength;
				Piece[][] pieceList = board.getCoords();
				Piece piece = pieceList[row][col]; //This breaks the order in Board but it works so...
				if (piece != null) {
					String fileName = piece.getImageName();

					Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
					// figure out why is the y instead of x (i think it's because of the board's order)
					g2.drawImage(scaledImage, topLeftX+5, topLeftY+5, squareLength-10, squareLength-10, null);


				}
			}
		}
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	private static class ComponentMove extends MouseAdapter {

		private boolean move;
		private int relx;
		private JComponent component;
		private int rely;
		private Container container;

		public ComponentMove(Container container) {
			this.container=container;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if ( move ) {
				move=false; // arrêt du mouvement
				component.setBorder(null); // on  supprime la bordure noire
				//component=null;
			}
			else {
				component = getComponent(e.getX(),e.getY()); // on mémorise le composant en déplacement
				if ( component!=null ) {
					container.setComponentZOrder(component,0); // place le composant le plus haut possible
					relx = e.getX()-component.getX(); // on mémorise la position relative
					rely = e.getY()-component.getY(); // on mémorise la position relative
					move=true; // démarrage du mouvement
					component.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // on indique le composant sélectionné par une bordure noire
				}
			}

			if (e.getButton() == MouseEvent.BUTTON3) {
				// si on déplace
				component.setLocation(e.getX()-relx, e.getY()-rely);
			}
		}

		private JComponent getComponent(int x, int y) {
			// on recherche le premier composant qui correspond aux coordonnées de la souris
			for(Component component : container.getComponents()) {
				if ( component instanceof JComponent && component.getBounds().contains(x, y) ) {
					return (JComponent)component;
				}
			}
			return null;
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}



