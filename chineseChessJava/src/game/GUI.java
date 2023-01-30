package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * 
 * @author YANG Mattew, Nasro Rona
 *
 */
public class GUI extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	private final int SCREENWIDTH = (int)size.getWidth();
	private final int SCREENHEIGHT = (int)size.getHeight();
	private int strokeWidth = 1;
	private Board board;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println(SCREENHEIGHT);

		//Image background
		try {
			String imgLocation = "images\\test.png";
			Image background = ImageIO.read(new File(imgLocation));
			int bgHeight = background.getHeight(null);

			if(bgHeight != SCREENHEIGHT) {
				// Resize image if necessaryk
				String imgOut = "images\\test1.png";
				ImageResizer.resize(imgLocation, imgOut, ((double)SCREENHEIGHT) / ((double)bgHeight));
				File fileOut = new File(imgOut);
				background = ImageIO.read(fileOut);
				g.drawImage(background,0,0,null);
				fileOut.delete();
			}

			else {
				g.drawImage(background,0,0,null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		Graphics2D g2 = (Graphics2D) g;
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
			
			setPieceImages(g2, board);
		}
	
	}
	
	void setPieceImages(Graphics2D g2, Board board) {
		int margin = SCREENHEIGHT/18;
		int squareLength = (SCREENHEIGHT-margin*3)/11; // x3 to leave more space at bottom of screen
		int center = SCREENWIDTH/2;
		int leftMostPixel = (int) (center-squareLength*(5.5));
		try {
			for(int row = 0; row<11; row++) {
				for(int col = 0; col<11; col++) {
					int topLeftX = leftMostPixel + col * squareLength;
					int topLeftY = margin + row * squareLength;
					Piece[][] pieceList = board.getCoords();
					Piece piece = pieceList[col][row];
					if (piece != null) {
						String fileName = piece.getImageName();
						Image scaledImage = new ImageIcon("pictures/chinese_"+fileName).getImage();
						// figure out why is the y instead of x (i think it's because of the board's order)
						g2.drawImage(scaledImage, topLeftX, topLeftY, 55, 55, null);
						
					}
				}
			}
		} catch (Exception e) {
			System.out.print(e);
		}
	}
}



