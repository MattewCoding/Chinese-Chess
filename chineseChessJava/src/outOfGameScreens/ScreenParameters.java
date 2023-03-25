package outOfGameScreens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenParameters {
	
	private static Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int SCREENWIDTH = (int)size.getWidth();
	public static final int SCREENHEIGHT = (int)size.getHeight();
	
	public static final Color BOARDCOLOR = new Color(244,227,166);
	
}
