package outOfGameScreens;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class SubMenu extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	private ArrayList<String[]> optionWithContent = new ArrayList<String[]>();

	/**
	 * Creates the sub-menu layout
	 * @param pathName the location of a file named the menu name and containing the various options of that menu
	 */
	public SubMenu(String pathName) {
		
		//Extract data from file
		File contentFile = new File(pathName);
		title = contentFile.getName();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathName));
			String line;
			
			//Split the options from its content
			while ((line = reader.readLine()) != null) {
				String[] option = line.split("-");
				optionWithContent.add(option);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		//Create components for left side of menu
		JTextField menuName = new JTextField(title);
		JList<String> optionList = new JList<String>();

		//JList is funky with ArrayLists, so manually convert the ArrayList into a regular list
		for(int i=0; i<optionWithContent.size(); i++) {
			JTextField option = new JTextField(optionWithContent.get(i)[0]);
			optionList.add(option);
		}

		//Create left side of menu
		JScrollPane optionScroll = new JScrollPane(optionList);
		JSplitPane menuSideBar = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuName, optionScroll);
		menuSideBar.setOneTouchExpandable(true);
		menuSideBar.setDividerLocation(150);
 
        //Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(100, 50);
        menuName.setMinimumSize(minimumSize);
        optionScroll.setMinimumSize(minimumSize);
 
        //Provide a preferred size for the split pane.
        menuSideBar.setPreferredSize(new Dimension(400, 200));
	}

}
