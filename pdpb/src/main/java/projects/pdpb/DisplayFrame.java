package projects.pdpb;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import java.awt.*;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.util.Enumeration;

public class DisplayFrame extends JFrame {
	
	static Database db;
	static Dimension size;
	
	static TabPanel tabs;
	static JButton problemButton;
	static JButton favoritesButton;
	static JButton archiveButton;
	static JButton editButton;
	static JLabel problems;
	static JLabel favorites;
	static JLabel archive;
	static JLabel edit;
	static ProblemList problemTab;
	static EditProblem editTab;
	static Point p;
    
	public DisplayFrame() throws Exception {
		super("Personal DMOJ Problem Bank");
//		size = Toolkit.getDefaultToolkit().getScreenSize();
//		System.out.printf("%d %d\n", size.height, size.width);
		size = new Dimension(1707, 1067);
		
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    setUIFont(new FontUIResource("Arial", Font.PLAIN, 14));
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		db = new Database();
		tabs = new TabPanel();
        problemTab = new ProblemList();
        editTab = new EditProblem();
        
        this.setBackground(Color.WHITE);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(size.width, size.height);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        
        p = getLocationOnScreen();
        
        this.setIconImage(new ImageIcon(getClass().getResource("/Images/DMOJ logo.png")).getImage());
        
        problems = new JLabel("Problems");
        problems.setBounds(201, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
//        problems.setVisible(true);
        
        favorites = new JLabel("Favorites");
        favorites.setBounds((int)size.getWidth()/4 + 181, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
        favorites.setVisible(false);

        archive = new JLabel("Archive");
        archive.setBounds((int)size.getWidth()/4 * 2 + 167, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
        archive.setVisible(false);
        
        edit = new JLabel("+");
        edit.setBounds((int)size.getWidth()/4 * 3 + 166, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
        edit.setVisible(false);
        
        problemButton = new JButton("Problems");
        problemButton.setBounds(60, (int)size.getHeight()/5 - 50, (int)size.getWidth()/5, 40);
        problemButton.setFocusable(true);
        problemButton.setVisible(false);
        problemButton.addActionListener((e) -> {
        	try {
				setProblemsVisible(false, false);
			} catch (Exception e1) {
//				e1.printStackTrace();
			}
        });
        
        favoritesButton = new JButton("Favorites");
        favoritesButton.setBounds((int)size.getWidth()/4 + 40, (int)size.getHeight()/5 - 50, (int)size.getWidth()/5, 40);
        favoritesButton.setFocusable(true);
        favoritesButton.setVisible(true);
        favoritesButton.addActionListener((e) -> {
        	try {
				setProblemsVisible(true, false);
			} catch (Exception e1) {
//				e1.printStackTrace();
			}
        });
        
        archiveButton = new JButton("Archive");
        archiveButton.setBounds((int)size.getWidth()/4 * 2 + 20, (int)size.getHeight()/5 - 50, (int)size.getWidth()/5, 40);
        archiveButton.setFocusable(true);
        archiveButton.setVisible(true);
        archiveButton.addActionListener((e) -> {
        	try {
				setProblemsVisible(false, true);
			} catch (Exception e1) {
//				e1.printStackTrace();
			}
        });
        
        editButton = new JButton("+");
        editButton.setBounds((int)size.getWidth()/4 * 3, (int)size.getHeight()/5 - 50, (int)size.getWidth()/5, 40);
        editButton.setFocusable(true);
        editButton.addActionListener((e) -> {
        	try {
				setEditVisible();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        });
        
//        tabs.setLayout(null);
//        tabs.setBounds(0, 0, (int)size.getWidth(), (int)size.getHeight()/5);
//        tabs.setBackground(Color.WHITE);
        tabs.add(problems);
        tabs.add(favorites);
        tabs.add(archive);
        tabs.add(edit);
        tabs.add(problemButton);
        tabs.add(favoritesButton);
        tabs.add(archiveButton);
        tabs.add(editButton);
        
        this.getContentPane().add(tabs);
        this.getContentPane().add(problemTab);
        this.getContentPane().add(editTab);
        
        this.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				// TODO Auto-generated method stub
				p = getLocationOnScreen();
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
	}
	
	/**
	 * Makes the problem tab visible
	 * 
	 * @throws Exception
	 */
	public static void setProblemsVisible(boolean isFavorites, boolean isArchive) throws Exception {
		problemTab.setVisible(true);
		problemTab.setIsFavorites(isFavorites);
		problemTab.setIsArchive(isArchive);
		if (isFavorites) {
			favoritesButton.setVisible(false);
			favorites.setVisible(true);
			problemButton.setVisible(true);
	    	problems.setVisible(false);
			archiveButton.setVisible(true);
	    	archive.setVisible(false);
		}
		else if (isArchive) {
			favoritesButton.setVisible(true);
			favorites.setVisible(false);
			problemButton.setVisible(true);
	    	problems.setVisible(false);
			archiveButton.setVisible(false);
	    	archive.setVisible(true);
		}
		else {
			problemButton.setVisible(false);
	    	problems.setVisible(true);
			favoritesButton.setVisible(true);
			favorites.setVisible(false);
			archiveButton.setVisible(true);
			archive.setVisible(false);
		}
    	editTab.setVisible(false);
    	editButton.setVisible(true);
    	edit.setVisible(false);
    	updateProblems();
	}
	
	/**
	 * Makes the edit table invisible
	 * 
	 * @throws Exception
	 */
	public static void setEditVisible() throws Exception {
    	problemTab.setVisible(false);
    	problemButton.setVisible(true);
    	favoritesButton.setVisible(true);
    	archiveButton.setVisible(true);
    	problems.setVisible(false);
    	favorites.setVisible(false);
    	archive.setVisible(false);
    	editTab.reset();
    	editTab.setVisible(true);
    	editButton.setVisible(false);
    	edit.setVisible(true);
	}
	
	/**
	 * Updates the display
	 * 
	 * @throws Exception
	 */
	public static void updateProblems() throws Exception {
		problemTab.displayProblems();
	}
	
	/**
	 * Sets font
	 * 
	 * @param f
	 */
	public static void setUIFont (FontUIResource f) {
	    Enumeration<Object> keys = UIManager.getDefaults().keys();
	    while (keys.hasMoreElements()) {
	    	Object key = keys.nextElement();
	    	Object value = UIManager.get(key);
	    	if (value instanceof FontUIResource) UIManager.put(key, f);
	    }
	}
	
	/**
	 * Disables/enables buttons on problem page
	 * 
	 * @param enabled
	 */
	public static void setButtonsEnabled(boolean enabled) {
		editButton.setEnabled(enabled);
	}
}
