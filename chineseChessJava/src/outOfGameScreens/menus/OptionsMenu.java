package outOfGameScreens.menus;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import outOfGameScreens.SliderButtonCombo;

/**
 * The option menu. Differs from the SubMenu class in that the content here is interactable
 * @author Yang Mattew
 *
 */
public class OptionsMenu extends AbstractMenu {

	private static final long serialVersionUID = 1L;

	//Text file contents
	private String title = "Options";
	private String[] optionArrayList = {"Sound", "Music", "Timer"};

	//Swing attributes
	private JPanel timeAmount = new JPanel();
	private SliderButtonCombo soundSlider = new SliderButtonCombo("sound");
	private SliderButtonCombo musicSlider = new SliderButtonCombo("music");
	private JComponent[] optionSliders = {soundSlider, musicSlider, timeAmount};

	/**
	 * Creates the option menu. Differs from the SubMenu class in that the content here is interactable
	 * @param menuScreen The JFrame
	 */
	public OptionsMenu(JFrame menuScreen) {
		mainScreen = menuScreen;

		// Menu
		setTitle(title);

		// Options
		optionList = setOptionListOptions(optionArrayList);
		optionList.addListSelectionListener(new optionListListener());

		contentScroll = new JScrollPane(soundSlider);
		arrangeMenuComponents();
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
