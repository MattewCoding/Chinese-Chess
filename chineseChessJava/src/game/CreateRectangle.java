package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import outOfGameScreens.ScreenParameters;

public class CreateRectangle {
	private static int strokeWidth = 2;

	/**
	 * Draws a filled square using default board color (i.e. not the palace color)
	 * @param g2 The graphics class that draws on the board
	 * @param topX The top left x-position of the rectangle
	 * @param topY The top left y-position of the rectangle
	 * @param length The amount of pixels the rectangle extends positively over the x and y axis
	 */
	public static void drawFilledRectangle(Graphics2D g2, int topX, int topY, int length) {
		drawFilledRectangle(g2, ScreenParameters.OUTLINECOLOR, ScreenParameters.BOARDCOLOR, topX, topY, length, length);
	}
	
	/**
	 * Draws a filled rectangle using default board color (i.e. not the palace color)
	 * @param g2 The graphics class that draws on the board
	 * @param topX The top left x-position of the rectangle
	 * @param topY The top left y-position of the rectangle
	 * @param length The amount of pixels the rectangle extends positively over the x-axis
	 * @param height The amount of pixels the rectangle extends positively over the y-axis
	 */
	public static void drawFilledRectangle(Graphics2D g2, int topX, int topY, int length, int height) {
		drawFilledRectangle(g2, ScreenParameters.OUTLINECOLOR, ScreenParameters.BOARDCOLOR, topX, topY, length, height);
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
	public static void drawFilledRectangle(Graphics2D g2, Color outlineColor, Color fillColor, int topX, int topY, int length) {
		drawFilledRectangle(g2, outlineColor, fillColor, topX, topY, length, length);
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
	public static void drawFilledRectangle(Graphics2D g2, Color outlineColor, Color fillColor, int topX, int topY, int length, int height) {
		Rectangle rOutline, rFill;
		rOutline = new Rectangle(topX, topY, length, height);
		rFill = new Rectangle(topX+strokeWidth/2,topY+strokeWidth/2, length-strokeWidth, height-strokeWidth);
		g2.setColor(outlineColor);
		g2.draw(rOutline);
		g2.setColor(fillColor);
		g2.fill(rFill);
	}

}
