package projects.pdpb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ProblemFilters extends JPanel {
	
	private int low = 0, high = 50, bitset = 0, sort = 0;
	private boolean reverse = false, hideCompleted = false;
	private String str;
	private JButton apply;
	private JCheckBox reverseBox, hideBox;
	private JCheckBox[] categories = new JCheckBox[7];
	private JComboBox<String> sortBy;
	private JTextField lowBound, highBound, search;
	private JLabel searchLabel, sortLabel, boundsLabel, categoriesLabel, to, validBounds;

	private Font f = new Font("Arial", Font.BOLD, 12);
	
	public ProblemFilters() throws Exception {
		
		this.setBackground(Color.WHITE);
		this.setLayout(null);
		this.setBounds((int)DisplayFrame.size.getWidth()/4 * 3, 0, (int)DisplayFrame.size.getWidth()/4, (int)DisplayFrame.size.getHeight()/5 * 4);
		
		reverseBox = new JCheckBox("Reverse");
		reverseBox.setBackground(Color.WHITE);
		reverseBox.setBounds(10, (int)DisplayFrame.size.getHeight()/3 * 2 - 70, 200, 20);
		
		hideBox = new JCheckBox("Hide completed");
		hideBox.setBackground(Color.WHITE);
		hideBox.setBounds(10, (int)DisplayFrame.size.getHeight()/3 * 2 - 40, 200, 20);
		
		sortBy = new JComboBox<String>();
		sortBy.setBounds(10, (int)DisplayFrame.size.getHeight()/3 * 2 - 155, 100, 40);
		sortBy.addItem("Name");
		sortBy.addItem("Points");
		
		sortLabel = new JLabel("Sort by: ");
		sortLabel.setBounds(10, (int)DisplayFrame.size.getHeight()/3 * 2 - 195, 100, 40);
		
		categoriesLabel = new JLabel("Filter by category:");
		categoriesLabel.setBounds(10, (int)DisplayFrame.size.getHeight()/4 - 20, (int)DisplayFrame.size.getWidth()/7, 30);
		
		for (int i = 0; i < 7; i++) {
			categories[i] = new JCheckBox(DisplayFrame.db.getCategories(1 << i));
			categories[i].setBackground(Color.WHITE);
			categories[i].setFocusable(true);
			categories[i].setBounds(10, (int)DisplayFrame.size.getHeight()/4 + i * 30 + 10, (int)DisplayFrame.size.getWidth()/7, 30);
			int bruh = i; // because lambda expressions need variables to be effectively constant
			categories[i].addActionListener((e) -> {
				bitset ^= (1 << bruh);
			});
			this.add(categories[i]);
		}
		
		searchLabel = new JLabel("Search problems:");
		searchLabel.setBounds(10, 10, (int)DisplayFrame.size.getWidth()/4, 40);
		
		search = new JTextField();
		search.setFocusable(true);
		search.setBounds(10, 50, (int)DisplayFrame.size.getWidth()/5, 40);
		search.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (search.getText().length() >= 200) {
					search.setText(search.getText().substring(0, 200));
					e.consume();
				}
			}
		});
		
		boundsLabel = new JLabel("Filter by points:");
		boundsLabel.setBounds(10, (int)DisplayFrame.size.getHeight()/6 - 40, (int)DisplayFrame.size.getWidth()/5, 20);
		
		lowBound = new JTextField();
		lowBound.setFocusable(true);
		lowBound.setBounds(10, (int)DisplayFrame.size.getHeight()/6 - 10, 50, 40);
		lowBound.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (lowBound.getText().length() >= 3) {
					lowBound.setText(lowBound.getText().substring(0, 3));
					e.consume();
				}
			}
		});

		highBound = new JTextField();
		highBound.setFocusable(true);
		highBound.setBounds(90, (int)DisplayFrame.size.getHeight()/6 - 10, 50, 40);
		highBound.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (highBound.getText().length() >= 3) {
					highBound.setText(highBound.getText().substring(0, 3));
					e.consume();
				}
			}
		});
		
		to = new JLabel("-");
		to.setBounds(72, (int)DisplayFrame.size.getHeight()/6 - 10, 10, 40);
		
		validBounds = new JLabel();
		validBounds.setBounds(10, (int)DisplayFrame.size.getHeight()/6 + 30, 500, 40);
		validBounds.setFont(f);
		validBounds.setForeground(Color.RED);
		validBounds.setVisible(false);
		
		apply = new JButton("Apply");
        apply.setBounds((int)DisplayFrame.size.getWidth()/10, (int)DisplayFrame.size.getHeight()/3 * 2 + 20, 80, 40);
        apply.setFocusable(true);
        apply.addActionListener((e) -> {
    		validBounds.setVisible(false);
    		if (lowBound.getText().length() > 0 || highBound.getText().length() > 0) { // if either bound is set by user
	        	try { // checks if both bounds are set
	        		Integer.parseInt(lowBound.getText());
	        		Integer.parseInt(highBound.getText());
	        	} catch (Exception e1) {
	        		validBounds.setText("Please enter two integers between 0 and 50 inclusive");
	        		validBounds.setVisible(true);
	        		return;
	        	}
	        	low = Integer.parseInt(lowBound.getText());
	        	high = Integer.parseInt(highBound.getText());
	        	if (high < low || low < 0 || high > 50) { // checks if bounds are valid and correspond to the range of points on dmoj
	        		validBounds.setText("Please enter valid bounds");
	        		validBounds.setVisible(true);
	        		return;
	        	}
    		}
    		else { // sets lower bound to 0 and higher bound to 50 by default
    			low = 0;
    			high = 50;
    		}
        	sort = sortBy.getSelectedIndex(); // 0 for name, 1 for points
        	reverse = reverseBox.isSelected();
        	DisplayFrame.db.sort(sort, reverse);
        	hideCompleted = hideBox.isSelected();
        	if (search.getText().length() > 0) str = search.getText(); // if text box for searching isn't empty
        	else str = "";
        	try {
				DisplayFrame.updateProblems(); // updates the displayed problems
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
		
		this.add(apply);
		this.add(reverseBox);
		this.add(hideBox);
		this.add(sortBy);
		this.add(lowBound);
		this.add(highBound);
		this.add(search);
		this.add(searchLabel);
		this.add(sortLabel);
		this.add(validBounds);
		this.add(boundsLabel);
		this.add(to);
		this.add(categoriesLabel);
	}
	
	/**
	 * Disables/enables buttons
	 * 
	 * @param enabled
	 */
	public void setMouseEnabled(boolean enabled) {
		apply.setEnabled(enabled);
		reverseBox.setEnabled(enabled); 
		hideBox.setEnabled(enabled);
		for (int i = 0; i < 7; i++) categories[i].setEnabled(enabled);
		sortBy.setEnabled(enabled);
		lowBound.setEnabled(enabled);
		highBound.setEnabled(enabled);
		search.setEnabled(enabled);
	}
	
	public int getLow() {
		return this.low;
	}
	
	public int getHigh() {
		return this.high;
	}
	
	public int getBitset() {
		return this.bitset;
	}
	
	public String getStr() {
		return this.str;
	}
	
	public boolean getHideCompleted() {
		return this.hideCompleted;
	}
}
