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
	static JButton editButton;
	static JLabel problems;
	static JLabel favorites;
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
        problems.setBounds(244, (int)size.getHeight()/5 - 50, (int)size.getWidth()/3, 40);
//        problems.setVisible(true);
        
        favorites = new JLabel("Favorites");
        favorites.setBounds((int)size.getWidth()/3 + 244, (int)size.getHeight()/5 - 50, (int)size.getWidth()/3, 40);
        favorites.setVisible(false);
        
        edit = new JLabel("+");
        edit.setBounds((int)size.getWidth()/3 * 2 + 269, (int)size.getHeight()/5 - 50, (int)size.getWidth()/3, 40);
        edit.setVisible(false);
        
        problemButton = new JButton("Problems");
        problemButton.setBounds(60, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
        problemButton.setFocusable(true);
        problemButton.setVisible(false);
        problemButton.addActionListener((e) -> {
        	try {
				setProblemsVisible(false);
			} catch (Exception e1) {
//				e1.printStackTrace();
			}
        });
        
        favoritesButton = new JButton("Favorites");
        favoritesButton.setBounds((int)size.getWidth()/3 + 60, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
        favoritesButton.setFocusable(true);
        favoritesButton.setVisible(true);
        favoritesButton.addActionListener((e) -> {
        	try {
				setProblemsVisible(true);
			} catch (Exception e1) {
//				e1.printStackTrace();
			}
        });
        
        editButton = new JButton("+");
        editButton.setBounds((int)size.getWidth()/3 * 2 + 60, (int)size.getHeight()/5 - 50, (int)size.getWidth()/4, 40);
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
        tabs.add(edit);
        tabs.add(problemButton);
        tabs.add(favoritesButton);
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
	public static void setProblemsVisible(boolean isFavorites) throws Exception {
		problemTab.setVisible(true);
		problemTab.setIsFavorites(isFavorites);
		if (isFavorites) {
			favoritesButton.setVisible(false);
			favorites.setVisible(true);
			problemButton.setVisible(true);
	    	problems.setVisible(false);
		}
		else {
			problemButton.setVisible(false);
	    	problems.setVisible(true);
			favoritesButton.setVisible(true);
			favorites.setVisible(false);
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
    	problems.setVisible(false);
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
