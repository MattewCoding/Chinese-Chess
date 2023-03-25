package outOfGameScreens.menus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import outOfGameScreens.ScreenParameters;

/**
 * An abstract class that regroups the general functionalities of the menus:
 * The main options screen and its components
 * The creation of the similar components (e.g. the back button)
 * @author Yang Mattew
 *
 */
public abstract class AbstractMenu extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	//Swing attributes
	protected JTextArea contentLabel;
	protected JSplitPane menuSideBar;
	protected JFrame mainScreen;
	
	//Swing attributes created here
	protected JLabel menuName;
	protected JList<String> optionList;
	protected JButton backButton = setBackButtonOptions();
	

	protected JScrollPane contentScroll;
	
	protected Color boardColor = ScreenParameters.BOARDCOLOR;
	
	private int screenWidth = ScreenParameters.SCREENWIDTH;
	private int screenHeight = ScreenParameters.SCREENHEIGHT;
	
	/**
	 * Creates the JComponents necessary for arranging and displaying the information correctly.
	 */
	public void arrangeMenuComponents() {
		//Create menu parts
		JScrollPane optionScroll = new JScrollPane(optionList);
		JSplitPane menuNameAndOptionsScroll = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuName, optionScroll);
		menuNameAndOptionsScroll.setDividerLocation(screenHeight/8);

		JSplitPane leftSideOfMenu = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuNameAndOptionsScroll, backButton);
		int autoAdjust = screenHeight - screenHeight/7;
		int minSize = screenHeight - 100;
		int buttonDivider = autoAdjust > minSize ? minSize : autoAdjust;
		leftSideOfMenu.setDividerLocation(buttonDivider);

		//Regrouping to create the general menu screen
		menuSideBar = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSideOfMenu, contentScroll);
		menuSideBar.setDividerLocation(screenWidth/4);

		//Prevent menus from being interact-able
		menuNameAndOptionsScroll.setEnabled(false);
		leftSideOfMenu.setEnabled(false);
		menuSideBar.setEnabled(false);

		//Provide a preferred size for the split pane.
		menuSideBar.setPreferredSize(new Dimension(400, 200));
	}
	
	/**
	 * Creates the Jlabel for the option selections while setting the options (e.g. the background color).
	 * @param title The name of the menu for the JLabel
	 */
	public void setTitle(String title) {
		menuName = new JLabel(title);
		menuName.setBackground(boardColor);
		menuName.setFont(new Font(menuName.getFont().toString(), Font.BOLD, (int)(55*ScreenParameters.XREDUCE) ));
		menuName.setHorizontalAlignment(JLabel.CENTER);
		menuName.setOpaque(true);
		
	}
	
	/**
	 * Creates the JList for the option selections while setting the options (e.g. the background color).
	 * @param optionArrayList The list of options in string format
	 * @return optionList The list of options bundled up in a JList
	 */
	public JList<String> setOptionListOptions(String[] optionArrayList) {
		optionList = new JList<String>(optionArrayList);
		optionList.setBorder(new EmptyBorder(0, 5, 0, 10)); //order is: top, left, bottom, right
		optionList.setBackground(boardColor);
		optionList.setFont(new Font(optionList.getFont().toString(), Font.ITALIC, (int)(36*ScreenParameters.XREDUCE) ));
		
		optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionList.setSelectedIndex(0);
		optionList.setSelectionBackground(new Color(226,192,106));
		return optionList;
	}
	
	/**
	 * Create the back button with the correct font and background color.
	 * @return backButton A JButton returning the user to the main menu
	 */
	public JButton setBackButtonOptions() {
		backButton = new JButton("Back to main menu");
		backButton.addActionListener(new backButtonListener());
		backButton.setFont(new Font(backButton.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.XREDUCE) ));
		backButton.setBackground(ScreenParameters.BOARDCOLOR);
		return backButton;
	}

	public class backButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			mainScreen.setContentPane(new MainMenu(mainScreen));
			mainScreen.revalidate();
		}

	}

	
	public JButton getBackButton() {
		return backButton;
	}
	/**
	 * Get the menu (that is, the name of the menu, the options available, and its associated content)
	 * @return The menu
	 */
	public JSplitPane getSplitPane() {
		return menuSideBar;
	}
	

}
