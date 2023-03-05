package outOfGameScreens.menus;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.logging.log4j.Logger;

import log.LoggerUtility;
import outOfGameScreens.ScreenParameters;

/**
 * Used to create the submenus that need to show information
 * @author Yang Mattew
 *
 */
public class SubMenu extends AbstractMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Text file contents
	private String title;
	private ArrayList<String> optionArrayList = new ArrayList<String>();
	private ArrayList<String> contentArrayList = new ArrayList<String>();

	private static Logger logData = LoggerUtility.getLogger(SubMenu.class, "html");

	/**
	 * Creates the sub menu layout
	 * @param pathName The location of a file named the menu name and containing the various options of that menu
	 * @param menuFrame The screen that shows all of the menus
	 */
	public SubMenu(String pathName, JFrame menuScreen) {
		mainScreen = menuScreen;

		//Extract data from file
		File contentFile = new File(pathName);
		title = contentFile.getName().split("\\.")[0];

		try {
			BufferedReader reader = new BufferedReader(new FileReader(pathName));
			String line;

			//Split the options from its content
			while ((line = reader.readLine()) != null) {
				if(!line.isBlank()) { //Demarks the end of an option
					optionArrayList.add(line);
					line=reader.readLine();
					contentArrayList.add("        " + line); // \t is waaaay too big

					// For new paragraphs still in the same option
					for(line = reader.readLine(); line != null && !line.isBlank(); line = reader.readLine()){
						String informationString = contentArrayList.get(contentArrayList.size()-1);
						informationString += ("\n        " + line);
						contentArrayList.set(contentArrayList.size()-1, informationString);

						logData.info("Line added: " + line);
						logData.info("End result: " + contentArrayList.get(contentArrayList.size()-1));
					}
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		//Menu name is file name
		setTitle(title);

		//JList doesn't work with ArrayLists, so convert into regular list
		optionList = setOptionListOptions(optionArrayList.toArray(new String[optionArrayList.size()]));
		optionList.addListSelectionListener(new optionListListener());

		// For some reason JLabel doesn't have word wrap so we have
		// To use TextArea, which can word wrap
		contentLabel = new JTextArea(contentArrayList.get(0));
		contentLabel.setAlignmentY(JTextArea.TOP_ALIGNMENT);
		contentLabel.setBorder(new EmptyBorder(20, (int)(50*ScreenParameters.XREDUCE), 10, 50)); //order is: top, left, bottom, right
		contentLabel.setBackground(boardColor);
		contentLabel.setEditable(false);
		contentLabel.setFont(new Font(contentLabel.getFont().toString(), Font.PLAIN, (int)(28*ScreenParameters.XREDUCE) ));
		contentLabel.setLineWrap(true);
		contentLabel.setWrapStyleWord(true);
		
		contentScroll = new JScrollPane(contentLabel);		
		arrangeMenuComponents();
	}


	public class optionListListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			@SuppressWarnings("unchecked")
			JList<String> contentList = (JList<String>) e.getSource();
			updateLabel(contentArrayList.get(contentList.getSelectedIndex()));
		}

		/**
		 * Renders the selected image
		 * @param name The updated text for the content
		 */
		protected void updateLabel (String name) {
			contentLabel.setText(name);
		}
	}

}
