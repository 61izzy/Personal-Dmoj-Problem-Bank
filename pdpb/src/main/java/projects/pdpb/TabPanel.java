package projects.pdpb;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class TabPanel extends JPanel {
	
	private ImageIcon banner;
    private Image bannerImage;
    
	public TabPanel() {
        banner = new ImageIcon(getClass().getResource("/Images/IA Banner.png"));
        bannerImage = banner.getImage().getScaledInstance(1600, 160, Image.SCALE_DEFAULT);

        this.setLayout(null);
        this.setBounds(0, 0, (int)DisplayFrame.size.getWidth(), (int)DisplayFrame.size.getHeight()/5);
        this.setBackground(Color.WHITE);
	}
	
	/**
	 * Displays the banner
	 */
	public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D)g;
        g2D.setColor(Color.WHITE);
        g2D.drawImage(bannerImage, 43, 0, this);
	}
}
