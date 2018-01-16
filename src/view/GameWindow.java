package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.CasinoWheelController;
import model.interfaces.Player;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class GameWindow extends JFrame
{
	JFrame GameWindow = this;
	CasinoWheelController controller;

	private JMenuBar menuBar;
	private JToolBar toolbar;
	private JPanel infoPanel, wheelPanel;
	private JLabel wheelSizeLabel;
	private JButton spinButton;
	private JTable betTable;
	private DefaultTableModel model;
	private JLabel numberLabel;

	public GameWindow(CasinoWheelController controller) {
		this.controller = controller;
		
		setTitle("Casino Roulette");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		this.setResizable(false);
        
		addMenuBar();
        
		toolbar = new JToolBar();  
		addToolBar();
		getContentPane().add(toolbar, BorderLayout.PAGE_START);
 
		populateWheelPanel();
        
		getContentPane().add(wheelPanel, BorderLayout.CENTER);

		
	}

	/* addMenuBar function is used to add the menu bar to the window. The menu
    * bar will contain important actions for the game. */
   private void addMenuBar(){
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu actionMenu = new JMenu("Actions");
       
        JMenuItem addPlayerAction = new JMenuItem("Add Player"); 
        JMenuItem refreshPlayerAction = new JMenuItem("Refresh Player"); 
        JMenuItem displayHistoryAction = new JMenuItem("Display Result"); 
        JMenuItem changeWheelAction = new JMenuItem("Change Wheel Size"); 
        JMenuItem removePlayerAction = new JMenuItem("Remove Player"); 
        actionMenu.add(addPlayerAction);
        actionMenu.add(removePlayerAction);
        actionMenu.add(refreshPlayerAction);
        actionMenu.add(changeWheelAction);
        actionMenu.add(displayHistoryAction);
        
        menuBar.add(actionMenu);
        
        addPlayerAction.addActionListener(new addPlayerActionListener());
        changeWheelAction.addActionListener(new changeWheelActionListener());
        
        
        refreshPlayerAction.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
              controller.getGameEngine().requestRefresh();
              try
              {
                 Thread.sleep(100);
              }
              catch (InterruptedException e1)
              {
                 e1.printStackTrace();
              }
              addToolBar();
              populateBetTable();
           }
        });
        
        removePlayerAction.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
              removePlayerDialog();
           }
        });
        
        displayHistoryAction.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            /* TO BE IMPLEMENTED */
         }
        });
   }
   
   /* addToolBar function is a recursive function that will be called to add
    * a toolbar in the window. The toolbar is populated with player bet button
    * and the player information to the GUI. */
   private void addToolBar() {
      toolbar.removeAll();
      toolbar.repaint();
      ArrayList<Player> players = controller.getAllPlayers();
      for(int i = 0; i<players.size(); i++){
         JButton playerBetButton = new BetButton(players.get(i).getPlayerId(),
                                                 players.get(i).getPlayerName(),
                                                 players.get(i).getPoints());
         playerBetButton.setEnabled(false);
         if(controller.getMainPlayer() != null){
            if(players.get(i).getPlayerId().equals(controller.getMainPlayer().getPlayerId())){
               playerBetButton.setEnabled(true);      
            }
         }
         playerBetButton.addActionListener(new betButtonListener());
         toolbar.add(playerBetButton);
         toolbar.setFloatable(false);
      }
   }
   
   public JLabel getNumberLabel(){
      return numberLabel;
   }
   
   /* openResultDialog is a function to open up a dialog after the spin is
    * finished. It will tell the result number to the players and the winner. */
   public void openResultDialog(){
      int result = Integer.parseInt(numberLabel.getText());
      String message = "Result is number " + result + ". ";
      ArrayList<Player> players = controller.getAllPlayers();
      ArrayList<Player> winners = new ArrayList<Player>();
      for(int i = 0; i < players.size(); i++){
         if(players.get(i).getNumber() == result){
            winners.add(players.get(i));
            message += players.get(i).getPlayerName() 
                  + " win " 
                  + players.get(i).getBet() * 2 
                  + " points. ";
         }
      }
      if(winners.isEmpty())
         message += "No winner this round.";

      JOptionPane.showMessageDialog(GameWindow,
                                    message,
                                    "Result",
                                    JOptionPane.PLAIN_MESSAGE);
   }

	/* populateBetTable is a recursive function that when it is called, it will
	 * remove the table content and start filling it from zero. */
   private void populateBetTable()
   {
      model.setRowCount(0);
      ArrayList<Player> players = controller.getAllPlayers();
      for(int i = 0; i< players.size(); i++){
         if(players.get(i).getBet() > 0)
            model.addRow(new Object[]{players.get(i).getPlayerName(),
                  players.get(i).getNumber(),
                  players.get(i).getBet()});      
      }
   }
		
   /* populateWheelPanel is used to populate the wheel panel.
    * populateWheelPanel contain populateBetTable function as an initialiser. */
   private void populateWheelPanel() {
      wheelPanel = new JPanel();
      wheelPanel.setLayout(new BorderLayout(0, 0));

      wheelSizeLabel = new JLabel("Wheel size " + controller.getWheelSize());
      wheelSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
      wheelSizeLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
      wheelPanel.add(wheelSizeLabel, BorderLayout.NORTH);
        
      numberLabel = new JLabel("0");
      numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
      numberLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
      wheelPanel.add(numberLabel, BorderLayout.CENTER);
        
      spinButton = new SpinButton("Spin");
      spinButton.addActionListener(new SpinButtonListener());
      wheelPanel.add(spinButton, BorderLayout.SOUTH);
      
      infoPanel = new JPanel();
      infoPanel.setLayout(new BorderLayout());
      infoPanel.setPreferredSize(new Dimension(150,50));
      
      wheelPanel.add(infoPanel, BorderLayout.WEST);
      
      Object header[] = new String[]{"Player", "Number", "Bet"};
      model = new DefaultTableModel();
      model.setColumnIdentifiers(header);
      model.setColumnCount(3);

      populateBetTable();
      
      betTable = new JTable();
      betTable.setRowSelectionAllowed(false);
      betTable.setShowGrid(false);
      betTable.setShowVerticalLines(false);
      betTable.setEnabled(false);
      betTable.setModel(model);
      
      JTableHeader header2 = betTable.getTableHeader();
      header2.setReorderingAllowed(false);
      
      infoPanel.add(header2, BorderLayout.NORTH);
      infoPanel.add(betTable, BorderLayout.CENTER);
   }
   
   /* removePlayer is a function to open up a dialog for removing the player.
    * The dialog will gives the available players to be removed. */
   private void removePlayerDialog(){
      JComboBox<String> playerField= new JComboBox<String>();
      ArrayList<Player> players = new ArrayList<Player>(controller.getAllPlayers());
      if(players.isEmpty())
         return;
      for(int i = 0 ; i < players.size(); i++){
         playerField.addItem("Player " + players.get(i).getPlayerId());
      }     
      JPanel myPanel = new JPanel();
      myPanel.setLayout(new GridLayout(2,2));
      myPanel.add(new JLabel("Select player to be removed:"));
      myPanel.add(playerField);
      int result = JOptionPane.showConfirmDialog(GameWindow, myPanel, 
               "Please your bet and number", JOptionPane.OK_CANCEL_OPTION);

      if (result == JOptionPane.OK_OPTION) {
         String nameBoard = playerField.getSelectedItem().toString().substring(7);
         controller.removePlayer(nameBoard);
         addToolBar();
         return;
      }
   }
	
	/* changeWheelActionListener is an ActionListener used for change wheel
	 * action. It will open up a dialog that give the user a choice of wheel
	 * sizes. It will refresh the bet every time wheel change. */
	private class changeWheelActionListener implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent e) {
         Object[] possibilities = {"30", "36", "37", "38", "40"};
         String s = (String)JOptionPane.showInputDialog(
                             GameWindow,
                             "Please select preferred size of the wheel",
                             "Wheel size",
                             JOptionPane.PLAIN_MESSAGE,
                             null,
                             possibilities, "");
         if ((s != null) && (s.length() > 0)) {
            controller.changeWheelSize(Integer.parseInt(s));
            wheelSizeLabel.setText("Wheel size " + controller.getWheelSize());
            model.setRowCount(0);
            controller.refreshBet();
            return;
         }
      }
	}

	/* addPlayerActionListener is an ActionListener used to add a player.
	 * It will open up a dialog that ask the user to input the name and the
	 * amount of points to the table. It will also check that the points
	 * inserted is a numeric value. */
	private class addPlayerActionListener implements ActionListener{
	   @Override
      public void actionPerformed(ActionEvent e) {
         JTextField name = new JTextField();
         JTextField points = new JTextField();
         Object[] message = {
             "Name :", name,
             "Points :", points
         };

         int option = JOptionPane.showConfirmDialog(GameWindow, 
                                                    message, 
                                                    "Add Player", 
                                                    JOptionPane.OK_CANCEL_OPTION);
         if (option == JOptionPane.OK_OPTION) {
            if (points.getText().matches("^[0-9]+$")) {
               String nameCap = name.getText().substring(0, 1).toUpperCase() 
                     + name.getText().substring(1);
               int point = Integer.parseInt(points.getText());
               controller.addPlayer(nameCap, point);
               addToolBar();
            } else {
               System.out.println("Please enter the amount of points to be set.");
            }
         }
	   }
	}

	/* betButtonListener is an ActionListener used to bet for the player.
	 * As the bet button is written with the id, First, it will trim the string
	 * from the button and grab the id. Using the id, it will call the controller
	 * to place bet for the player and populate the bet table. */
	private class betButtonListener implements ActionListener{
      @Override
      public void actionPerformed(ActionEvent e) {
            JComboBox<Integer> numberField= new JComboBox<Integer>();
            SpinnerModel x = new SpinnerNumberModel(1,1,500,1);
            JSpinner spin = new JSpinner(x);
            
            for(int i = 1 ; i <= controller.getWheelSize(); i++){
               numberField.addItem(i);
            }
            
            JPanel myPanel = new JPanel();
            myPanel.setLayout(new GridLayout(2,2));
            myPanel.add(new JLabel("Select number:"));
            myPanel.add(numberField);
            myPanel.add(new JLabel("Set your bet:"));
            myPanel.add(spin);

            String nameBoard = e.getActionCommand().replaceAll("\\<.*?>","");
            nameBoard.trim();
            String id = nameBoard.substring(9,11);
            id = id.trim();
            int result = JOptionPane.showConfirmDialog(GameWindow, myPanel, 
                     "Please your bet and number", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
               controller.placeBet(id,
                                   (Integer)numberField.getSelectedItem(),
                                   (Integer) spin.getValue());
               populateBetTable();
            }
      }
	}
	
	/* spinButtonListener is an ActionListener used to start spinning the wheel.
	 * It will create a new thread and change the numberLabel at the end.
	 * It will also open up the result dialog, and after it finished, it will 
	 * refresh the bet, setting the bet and number of players to 0, and 
	 * refresh the toolbar to update the points. */
	private class SpinButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread()
			{
			       @Override
			       public void run()
			       {
		              controller.getGameEngine().requestSpin();

			         addToolBar();
			       }
			}.start();
		}
	}
}
