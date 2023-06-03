package projects.pdpb;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class ProblemList extends JPanel {
	
	private ProblemFilters filters;
	private JTable problems;
	private JScrollPane scrollPane;
	private JPopupMenu options;
	private JLayeredPane pane;
	private boolean mouseEnabled = true, isFavorites = false;
	
	public ProblemList() throws Exception {
		filters = new ProblemFilters();
		
		this.setBackground(Color.WHITE);
		this.setLayout(null);
		this.setBounds(0, (int)DisplayFrame.size.getHeight()/5, (int)DisplayFrame.size.getWidth(), (int)DisplayFrame.size.getHeight()/5 * 4);
		
		pane = new JLayeredPane();
		pane.setBounds(0, 0, (int)DisplayFrame.size.getWidth(), (int)DisplayFrame.size.getHeight()/5 * 4);
		
		this.add(pane);
		this.add(filters);
		displayProblems();
	}
	
	/**
	 * Displays the problems after applying the selected filters in the desired order
	 * 
	 * @throws Exception
	 */
	public void displayProblems() throws Exception {
		if (scrollPane != null) pane.remove(scrollPane);
		DefaultTableModel model = new DefaultTableModel(new Object[]{"Name", "Points", "Categories", "Complete"}, 0) {
			@Override
         	public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) return String.class;
				else if (columnIndex <= 2) return Integer.class;
				return Boolean.class;
			}
			@Override
			public boolean isCellEditable(int row, int column) { // only fourth column is editable
				return column == 3;
			}
		};
		
		class CustomRenderer extends DefaultTableCellRenderer {
			public CustomRenderer() {}

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				boolean temp = false;
                if (row > -1 && column > -1) {
                	String url = "";
					try {
						url = DisplayFrame.db.getURL(String.valueOf(model.getValueAt(row, 0)).replace("\'", "\'\'"));
						temp = DisplayFrame.db.getFavorite(url) == 1;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
                if (temp) c.setBackground(Color.YELLOW);
                else c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
				return c;
			}
		};
		problems = new JTable(model);
		problems.setDefaultRenderer(Object.class, new CustomRenderer());
		problems.setFocusable(true);
		problems.setCellSelectionEnabled(true);
		scrollPane = new JScrollPane(problems);
		scrollPane.setFocusable(true);
		scrollPane.setBounds(25, 0, (int)DisplayFrame.size.getWidth()/4 * 3 - 40, (int)DisplayFrame.size.getHeight()/4 * 3);
		problems.setFillsViewportHeight(true);

//		problems.getColumnModel().getColumn(1).setMinWidth(100);
//		problems.getColumnModel().getColumn(3).setMinWidth(100);
		problems.getColumnModel().getColumn(1).setMaxWidth(100);
		problems.getColumnModel().getColumn(3).setMaxWidth(100);
		for (int i = 0; i < 4; i++) problems.getColumnModel().getColumn(i).setResizable(false);
		problems.getTableHeader().setReorderingAllowed(false);
		problems.setRowHeight(20);
		
		ResultSet rs = DisplayFrame.db.update(); // retrieves the full list of problems and iterates through it
		while (rs.next()) {
			int minDist = 0x3f3f3f3f;
			if (filters.getStr() != null) {
				// checks every substring with the same length as the string in the text box and applies edit distance algorithm
				for (int i = 0; i + filters.getStr().length() <= rs.getString("NAME").length(); i++) {
					minDist = Math.min(minDist, EditDistance.editDistance(rs.getString("NAME").substring(i, i + filters.getStr().length()), filters.getStr()));
				}
			}
			else minDist = 0;
			// doesn't add current problem if it violates any of the active filters
			if (	isFavorites && rs.getInt("FAVORITE") != 1 ||
					minDist > 1 || 
					rs.getInt("POINTS") < filters.getLow() || 
					rs.getInt("POINTS") > filters.getHigh() || 
					filters.getBitset() != 0 && (rs.getInt("CATEGORIES") & filters.getBitset()) == 0 || 
					filters.getHideCompleted() && rs.getInt("COMPLETE") == 1) continue;
			Object[] temp = new Object[4];
			temp[0] = rs.getString("NAME");
			temp[1] = rs.getInt("POINTS");
			temp[2] = DisplayFrame.db.getCategories(rs.getInt("CATEGORIES"));

			temp[3] = rs.getInt("COMPLETE") == 1;
		    
			model.addRow(temp);
		}
		model.addTableModelListener(new TableModelListener() {
			/**
			 * Checks if the user has modified the check box at any row of the table
			 * If so, performs xor on its complete status
			 * 
			 * @param e any event that occurs within the table model
			 */
			@Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                TableModel model = (TableModel)e.getSource();
                if (row > -1 && column > -1) {
                    Object data = model.getValueAt(row, column);
//	                    System.out.printf("%d %d\n", row, column);
                    if (data instanceof Boolean) { // if the modified cell is in column 4
						try {
							DisplayFrame.db.markComplete(String.valueOf(model.getValueAt(row, 0)).replace("\'", "\'\'"), ((boolean)data ? 1 : 0));
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}  
                }
			}
		});
		problems.addMouseListener(new MouseAdapter() {
			/**
			 * Checks if user has clicked on a cell in the first column at any row
			 * 
			 * @param e any event that occurs within the table
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!mouseEnabled) return;
				// TODO Auto-generated method stub
                JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint(e.getPoint());
                int column = source.columnAtPoint(e.getPoint());
//                System.out.printf("%d %d %d\n", row, column, (boolean)model.getValueAt(row, 3) ? 1 : 0);
				
				if (column != 0) return; // not the first column
				if (SwingUtilities.isLeftMouseButton(e)) { // if left click, opens the url to the dmoj problem in new chrome tab
//					if (row != problems.getSelectedRow() || column != problems.getSelectedColumn()) return;
					try {
						DisplayFrame.db.openWebpage(String.valueOf(model.getValueAt(row, 0)).replace("\'", "\'\'"));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if (SwingUtilities.isRightMouseButton(e)) { // if right click, shows the options to either edit or delete problem
					String url = "";
					try {
						url = DisplayFrame.db.getURL(String.valueOf(model.getValueAt(row, 0)).replace("\'", "\'\'"));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int bitset = 0;
					try {
						bitset = DisplayFrame.db.getBitset(url);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					int isFavorite = 0;
					try {
						isFavorite = DisplayFrame.db.getFavorite(url);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					displayOptions(url, bitset, isFavorite, e.getX(), e.getYOnScreen() - getLocation().y - problems.getTableHeader().getHeight() - (int)DisplayFrame.p.y);
				}
			}
		});
		pane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
//		scrollPane.requestFocus();
		problems.requestFocus();
	}
	
	/**
	 * Shows a popup with the options to either edit or delete the selected problem
	 * 
	 * @param url link to the dmoj problem
	 * @param bitset integer representing the categories of the current problem
	 * @param x integer representing the x coordinate of the mouse click
	 * @param y integer representing the y coordinate of the mouse click
	 */
	public void displayOptions(String url, int bitset, int isFavorite, int x, int y) {
		if (options != null) pane.remove(options); // if previous component still exists in the frame
//		System.out.printf("%d %d\n", x, y);
		options = new JPopupMenu();
		options.setBackground(Color.BLACK);
		options.setFocusable(true);
		options.setBounds(x, y, 200, 160);
		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener((e) -> {
			options.setVisible(false);
			try {
				DisplayFrame.setEditVisible();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			DisplayFrame.editTab.setURLAndCategories(url, bitset);
		});
		options.add(edit);
		JMenuItem favorite = new JMenuItem();
		if (isFavorite == 1) favorite.setText("Unfavorite");
		else favorite.setText("Favorite");
		favorite.addActionListener((e) -> {
			options.setVisible(false);
			try {
				DisplayFrame.db.markFavorite(url, isFavorite ^ 1);
				displayProblems();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		options.add(favorite);
		JMenuItem delete = new JMenuItem("Delete");
		delete.addActionListener((e) -> {
			options.setVisible(false);
			DisplayFrame.setButtonsEnabled(false);
			setMouseEnabled(false);

			JPanel confirm = new JPanel();
			confirm.setLayout(null);
			confirm.setBounds((int)DisplayFrame.size.getWidth()/2 - 135, (int)DisplayFrame.size.getHeight()/2 - 150 - (int)DisplayFrame.size.getHeight()/5, 270, 150);
			confirm.setVisible(true);
			
			JLabel confirmMessage = new JLabel("Confirm deletion?");
			confirmMessage.setBounds(80, 40, 120, 40);
			confirm.add(confirmMessage);
			
			JButton yesButton = new JButton("Yes");
			yesButton.setBounds(25, 90, 100, 40);
			yesButton.addActionListener((e1) -> {
				try {
					DisplayFrame.db.deleteProblem(url);
					DisplayFrame.updateProblems();
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				confirm.setVisible(false);
				DisplayFrame.setButtonsEnabled(true);
				setMouseEnabled(true);
				pane.remove(confirm);
			});
			confirm.add(yesButton);
			
			JButton noButton = new JButton("No");
			noButton.setBounds(145, 90, 100, 40);
			noButton.addActionListener((e1) -> {
				confirm.setVisible(false);
				DisplayFrame.setButtonsEnabled(true);
				setMouseEnabled(true);
				pane.remove(confirm);
			});
			confirm.add(noButton);
			
			pane.add(confirm, JLayeredPane.PALETTE_LAYER);
			
//			try {
//				DisplayFrame.db.deleteProblem(url);
//				DisplayFrame.updateProblems();
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
		});
		options.add(delete);
		pane.add(options);
		options.show(this, x, y);
	}
	
	/**
	 * Disables/enables buttons
	 * 
	 * @param enabled
	 */
	public void setMouseEnabled(boolean enabled) {
		filters.setMouseEnabled(enabled);
		mouseEnabled = enabled;
	}
	
	/**
	 * Determines whether or not only favorite problems will be displayed
	 * 
	 * @param isFavorites
	 */
	public void setIsFavorites(boolean isFavorites) {
		this.isFavorites = isFavorites;
	}
}
