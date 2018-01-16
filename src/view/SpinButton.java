package view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

public class SpinButton extends JButton{  
   /* Spin Button is used at GameWindow to stylised the spin button. */
   SpinButton(String text){
      setPreferredSize(new Dimension(100,100));
      setText(text);
      setFont(new Font("Tahoma", Font.PLAIN, 30));
   }
}