package view;

import java.awt.Dimension;

import javax.swing.JButton;

public class BetButton extends JButton {
   /* BetButton is a JButton class used to stylised the bet button. */
	public BetButton(String id, String name, int points){
		this.setPreferredSize(new Dimension(50,50));
		this.setText("<html> <p style=\"text-align:center\"> Player " 
		      + id 
		      + " <br/> " 
		      + name 
		      + " <br/>Points : " 
		      + points 
		      + " </html>");
	}
}
