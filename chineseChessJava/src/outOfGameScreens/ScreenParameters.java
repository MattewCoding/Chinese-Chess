package outOfGameScreens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import log.LoggerUtility;

public class ScreenParameters {
	
	private static Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int SCREENWIDTH = (int)size.getWidth();
	public static final int SCREENHEIGHT = (int)size.getHeight();

	public static final double XREDUCE = SCREENWIDTH/1600.0;
	public static final double YREDUCE = SCREENHEIGHT/900.0;

	public static final Color BOARDCOLOR = new Color(244,227,166);
	public static final Color PALACECOLOR = new Color(226,192,106);
	public static final Color OUTLINECOLOR = new Color(158,79,34);
	
	public static final int SLEEPAMOUNT = 40; // 25fps
	
}
