package outOfGameScreens.menus;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import outOfGameScreens.SliderButtonCombo;
import outOfGameScreens.menus.OptionsMenu.optionListListener;

public class SetNamesMenu extends AbstractMenu {

	private static final long serialVersionUID = 1L;
	
	//Text file contents
	private String title = "Set Names";
	private String[] optionArrayList = {"Player 1's name", "Player 2's name"};

	//Swing attributes
	private JPanel player1name = new JPanel();
	private JPanel player2name = new JPanel();
	private JTextField soundSlider = new JTextField();
	private JTextField musicSlider = new JTextField();

	private JPanel[] optionSliders = {player1name, player2name};
	
	public SetNamesMenu(JFrame menuScreen) {
		mainScreen = menuScreen;

		// Menu
		setTitle(title);

		// Options
		optionList = setOptionListOptions(optionArrayList);
		optionList.addListSelectionListener(new optionListListener());
	}


	public class optionListListener implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent evt) {
			@SuppressWarnings("unchecked")
			JList<String> contentList = (JList<String>) evt.getSource();
			contentScroll.setViewportView(optionSliders[contentList.getSelectedIndex()]);
		}
	}
}
