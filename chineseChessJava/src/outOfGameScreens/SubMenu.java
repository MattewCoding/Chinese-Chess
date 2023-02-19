package outOfGameScreens;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SubMenu extends JPanel implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Text file contents
	private String title;
	private ArrayList<String> optionArrayList = new ArrayList<String>();
	private ArrayList<String> contentArrayList = new ArrayList<String>();

	//Swing attributes
	private JTextArea contentLabel;
	private JSplitPane menuSideBar;
	private GameLauncher mainMenuObject;
	private JFrame mainScreen;

	//Listens to the list
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		JList<String> contentList = (JList<String>) e.getSource();
		updateLabel(contentArrayList.get(contentList.getSelectedIndex()));
	}

	//Renders the selected image
	protected void updateLabel (String name) {
		contentLabel.setText(name);
	}

	public JSplitPane getSplitPane() {
		return menuSideBar;
	}

	/**
	 * Creates the sub-menu layout
	 * @param pathName The location of a file named the menu name and containing the various options of that menu
	 * @param menuFrame The screen that shows all of the menus
	 */
	public SubMenu(String pathName, GameLauncher menuScreen) {
		int SCREENWIDTH = ScreenParameters.SCREENWIDTH;
		int SCREENHEIGHT = ScreenParameters.SCREENHEIGHT;
		mainMenuObject = menuScreen;
		mainScreen = menuScreen.getFrame();

		//Extract data from file
		File contentFile = new File(pathName);
		title = contentFile.getName().split("\\.")[0];

		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathName));
			String line;

			//Split the options from its content
			while ((line = reader.readLine()) != null) {
				if(!line.isBlank()) { //Aids with readability in text file
					optionArrayList.add(line);
					line=reader.readLine();
					contentArrayList.add("        " + line); // \t is waaaay too big
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		Color boardColor = ScreenParameters.BOARDCOLOR;
		
		//Menu name is file name
		JLabel menuName = new JLabel(title);
		menuName.setBackground(boardColor);
		menuName.setFont(new Font(menuName.getFont().toString(), Font.BOLD, (int)(55*ScreenParameters.xReduce) ));
		menuName.setHorizontalAlignment(JLabel.CENTER);
		menuName.setOpaque(true);

		//JList doesn't work with ArrayLists, so convert into regular list
		JList<String> optionList = new JList<String>(optionArrayList.toArray(new String[optionArrayList.size()]));
		optionList.setBorder(new EmptyBorder(0, 5, 0, 10)); //order is: top, left, bottom, right
		optionList.setBackground(boardColor);
		optionList.setFont(new Font(optionList.getFont().toString(), Font.ITALIC, (int)(36*ScreenParameters.xReduce) ));
		
		optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionList.setSelectedIndex(0);
		optionList.setSelectionBackground(new Color(226,192,106));
		
		optionList.addListSelectionListener(this);

		//TODO: Figure out wrap around
		// For some reason JLabel doesn't have word wrap so we have
		// To use TextArea, which can word wrap
		contentLabel = new JTextArea(contentArrayList.get(0));
		contentLabel.setAlignmentY(JTextArea.TOP_ALIGNMENT);
		contentLabel.setBorder(new EmptyBorder(20, (int)(50*ScreenParameters.xReduce), 10, 50)); //order is: top, left, bottom, right
		contentLabel.setBackground(boardColor);
		contentLabel.setEditable(false);
		contentLabel.setFont(new Font(contentLabel.getFont().toString(), Font.PLAIN, (int)(28*ScreenParameters.xReduce) ));
		contentLabel.setLineWrap(true);
		contentLabel.setWrapStyleWord(true);

		JButton backButton = new JButton("Back to main menu");
		backButton.addActionListener(new backButtonListener());
		backButton.setFont(new Font(backButton.getFont().toString(), Font.PLAIN, (int)(24*ScreenParameters.xReduce) ));
		backButton.setBackground(boardColor);

		//Create menu parts
		JScrollPane optionScroll = new JScrollPane(optionList);
		JScrollPane contentScroll = new JScrollPane(contentLabel);
		JSplitPane menuNameAndOptionsScroll = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuName, optionScroll);
		menuNameAndOptionsScroll.setDividerLocation(SCREENHEIGHT/8);
		
		JSplitPane leftSideOfMenu = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuNameAndOptionsScroll, backButton);
		int autoAdjust = SCREENHEIGHT - SCREENHEIGHT/7;
		int minSize = SCREENHEIGHT - 100;
		int buttonDivider = autoAdjust > minSize ? minSize : autoAdjust;
		leftSideOfMenu.setDividerLocation(buttonDivider);
		System.out.println(SCREENHEIGHT+"");

		//Regrouping to create the general menu screen
		menuSideBar = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSideOfMenu, contentScroll);
		menuSideBar.setDividerLocation(SCREENWIDTH/4);

		//Prevent menus from being interact-able
		menuNameAndOptionsScroll.setEnabled(false);
		leftSideOfMenu.setEnabled(false);
		menuSideBar.setEnabled(false);

		//Provide a preferred size for the split pane.
		menuSideBar.setPreferredSize(new Dimension(400, 200));
	}

	public class backButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			mainScreen.setContentPane(new MainMenu(mainMenuObject));
			mainScreen.revalidate();
		}

	}

}
