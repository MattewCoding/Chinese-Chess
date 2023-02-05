package outOfGameScreens;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SubMenu extends JPanel implements ListSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Screen size
	private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
	private final int SCREENWIDTH = (int)size.getWidth();
	private final int SCREENHEIGHT = (int)size.getHeight();

	private String title;
	private ArrayList<String> optionArrayList = new ArrayList<String>();
	private ArrayList<String> contentArrayList = new ArrayList<String>();
	
	//Swing attributes
	private JTextArea contentLabel;
	private JSplitPane menuSideBar;
     
    //Listens to the list
    public void valueChanged(ListSelectionEvent e) {
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
	 * @param pathName the location of a file named the menu name and containing the various options of that menu
	 */
	public SubMenu(String pathName) {
        
		//Extract data from file
		File contentFile = new File(pathName);
		title = contentFile.getName().split("\\.")[0];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathName));
			String line;
			
			//Split the options from its content
			while ((line = reader.readLine()) != null) {
				String[] option = line.split("-");
				optionArrayList.add(option[0]);
				contentArrayList.add(option[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		//Menu name is file name
		JLabel menuName = new JLabel(title);
		menuName.setHorizontalAlignment(JLabel.CENTER);
		menuName.setFont(new Font(menuName.getFont().toString(), Font.BOLD, 60));
		
		//JList doesn't work with ArrayLists, so convert into regular list
		JList<String> optionList = new JList<String>(optionArrayList.toArray(new String[optionArrayList.size()]));
		optionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionList.setSelectedIndex(0);
        optionList.addListSelectionListener(this);
        optionList.setFont(new Font(optionList.getFont().toString(), Font.BOLD, 36));
        
        // For some reason JLabel doesn't have word wrap so we have
        // To use TextArea, which can word wrap
        contentLabel = new JTextArea(contentArrayList.get(0));
        contentLabel.setAlignmentY(JTextArea.TOP_ALIGNMENT);
        contentLabel.setEditable(false);
        contentLabel.setLineWrap(true);
        contentLabel.setFont(new Font(contentLabel.getFont().toString(), Font.PLAIN, 36));
        contentLabel.setBorder(new EmptyBorder(20, 50, 10, 10)); //order is: top, left, bottom, right

		//Create menu parts
		JScrollPane optionScroll = new JScrollPane(optionList);
		JScrollPane contentScroll = new JScrollPane(contentLabel);
		JSplitPane menuNameAndOptionsScroll = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuName, optionScroll);
		menuNameAndOptionsScroll.setDividerLocation(SCREENHEIGHT/8);
 
        //Create a split pane with the two scroll panes in it.
		menuSideBar = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuNameAndOptionsScroll, contentScroll);
		menuSideBar.setOneTouchExpandable(true);
		menuSideBar.setDividerLocation(SCREENWIDTH/4);
		
		//Prevent menu from being interactable
		menuNameAndOptionsScroll.setEnabled(false);
		menuSideBar.setEnabled(false);
 
        //Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(100, 50);
        menuName.setMinimumSize(minimumSize);
        optionScroll.setMinimumSize(minimumSize);
 
        //Provide a preferred size for the split pane.
        menuSideBar.setPreferredSize(new Dimension(400, 200));
	}

}
