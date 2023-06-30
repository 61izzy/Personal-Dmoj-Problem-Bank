package projects.pdpb;

import javax.swing.*;
import java.awt.*;

public class EditProblem extends JPanel {
	
	private String url;
	private int bitset = 0;
	private JTextField urlField;
	private JButton submit, okButton;
	private JLabel invalid, duplicate, urlPrompt;
	private JCheckBox[] categories = new JCheckBox[7];
	private Font f = new Font("Arial", Font.BOLD, 12);
	
	public EditProblem() {
		
		this.setBackground(Color.WHITE);
		this.setLayout(null);
		this.setBounds(0, (int)DisplayFrame.size.getHeight()/5, (int)DisplayFrame.size.getWidth(), (int)DisplayFrame.size.getHeight()/5 * 4);
		
		urlPrompt = new JLabel("URL:");
		urlPrompt.setBounds((int)DisplayFrame.size.getWidth()/4, (int)DisplayFrame.size.getHeight()/4 - 30, (int)DisplayFrame.size.getWidth()/2, 40);
		
		invalid = new JLabel("Invalid URL!");
		invalid.setFont(f);
		invalid.setForeground(Color.RED);
		invalid.setBounds((int)DisplayFrame.size.getWidth()/4, (int)DisplayFrame.size.getHeight()/4 + 30, (int)DisplayFrame.size.getWidth()/2, 40);
		
		duplicate = new JLabel("URL already exists!");
		duplicate.setFont(f);
		duplicate.setForeground(Color.RED);
		duplicate.setBounds((int)DisplayFrame.size.getWidth()/4, (int)DisplayFrame.size.getHeight()/4 + 30, (int)DisplayFrame.size.getWidth()/2, 40);
		
		submit = new JButton("Submit");
		submit.setFocusable(true);
		submit.setBounds((int)DisplayFrame.size.getWidth()/4 * 3 + 10, (int)DisplayFrame.size.getHeight()/4, 100, 40);
		submit.addActionListener((e) -> {
			
			int result = DisplayFrame.db.checkValidURL(urlField.getText());
			
			if (result == -1) { // url already exists in database
				duplicate.setVisible(true);
				invalid.setVisible(false);
			}
			else if (result == 0) { // url is invalid
				invalid.setVisible(true);
				duplicate.setVisible(false);
			}
			else { // url is valid
				try {
					setURLAndCategories(DisplayFrame.db.addProblem(urlField.getText()), 0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		okButton = new JButton("OK");
		okButton.setFocusable(true);
		okButton.setBounds((int)DisplayFrame.size.getWidth()/2 - 20, (int)DisplayFrame.size.getHeight()/5 * 4 + 30, 80, 40);
		okButton.addActionListener((e) -> {
			if (url != null) {
				try { // updates the categories of the dmoj problem provided by the url
					DisplayFrame.db.updateCategories(url, bitset);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			try { // returns to problem list
				DisplayFrame.setProblemsVisible(false, false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		
		urlField = new JTextField();
		urlField.setFocusable(true);
		urlField.setBounds((int)DisplayFrame.size.getWidth()/4, (int)DisplayFrame.size.getHeight()/4, (int)DisplayFrame.size.getWidth()/2, 40);
//		urlField.addActionListener((e) -> {
//			duplicate.setVisible(false);
//			invalid.setVisible(false);
//		});
		
		for (int i = 0; i < 7; i++) {
			categories[i] = new JCheckBox(DisplayFrame.db.getCategories(1 << i));
			categories[i].setBackground(Color.WHITE);
			categories[i].setFocusable(true);
			categories[i].setVisible(false);
			categories[i].setBounds((int)DisplayFrame.size.getWidth()/3 * (i%3) + (int)DisplayFrame.size.getWidth()/7, (int)DisplayFrame.size.getHeight()/8 * (3 + i/3), (int)DisplayFrame.size.getWidth()/7, 40);
			int bruh = i; // because lambda expressions need variables to be effectively constant
			categories[i].addActionListener((e) -> {
				bitset ^= (1 << bruh);
			});
			this.add(categories[i]);
		}
		
		this.add(invalid);
		this.add(duplicate);
		this.add(submit);
		this.add(urlField);
		this.add(okButton);
		this.add(urlPrompt);
		
		this.setVisible(false);
	}
	
	/**
	 * Sets the url and categories of the dmoj problem provided by the link
	 * 
	 * @param url link to the dmoj problem
	 * @param bitset integer representing the categories of the current dmoj problem
	 */
	public void setURLAndCategories(String url, int bitset) {
		this.url = url;
		this.bitset = bitset;
		urlField.setText(url);
		urlField.setEditable(false);
		submit.setVisible(false);
		duplicate.setVisible(false);
		invalid.setVisible(false);
		for (int i = 0; i < 7; i++) {
			categories[i].setVisible(true);
			categories[i].setSelected((bitset & (1 << i)) > 0);
		}
	}
	
	/**
	 * Resets the edit problem tab to default state with nothing selected and an empty text field
	 */
	public void reset() {
		url = null;
		bitset = 0;
		urlField.setText("");
		urlField.setEditable(true);
		submit.setVisible(true);
		duplicate.setVisible(false);
		invalid.setVisible(false);
		for (int i = 0; i < 7; i++) {
			categories[i].setVisible(false);
			categories[i].setSelected(false);
		}
	}
}
