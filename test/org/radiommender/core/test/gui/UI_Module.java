/**
 * Copyright 2012 CSG@IFI
 * 
 * This file is part of Radiommender.
 * 
 * Radiommender is free software: you can redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your 
 * option) any later version.
 * 
 * Radiommender is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Radiommender. If not, see 
 * http://www.gnu.org/licenses/.
 * 
 */
package org.radiommender.core.test.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Marium Zeeshan
 *
 */
public class UI_Module extends JFrame {

	Logger logger = LoggerFactory.getLogger(UI_Module.class);
	
    JPanel contentPane= new JPanel (new GridBagLayout());
    GridBagConstraints Grid= new GridBagConstraints();
    private JTextField txtTypeGenrealbumartistName;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    
    
    /**
     * Create the frame.
     * @throws IOException 
     */
    public UI_Module() throws IOException{
	super();
	setting_panel();
    }
    
    
    /**
     * @throws IOException
     */
    public void setting_panel() throws IOException {
	
    	this.setTitle("Music World!!");
    	
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      // the line that reads the image file
//	BufferedImage image = ImageIO.read(new File("icon.jpg"));
	//BufferedImage image = ImageIO.read(new File("C:/Users/Marium Ahmad/work/P2P/GUI/ui/P2P/CT/icon.jpg"));
	this.setBounds(20, 20, 479, 483);
	
//	ImagePanel panel = new ImagePanel(new ImageIcon("C:/Users/Marium Ahmad/work/P2P/GUI/ui/P2P/CT/background_image_music.jpg").getImage());
//	ImagePanel panel = new ImagePanel(new ImageIcon("background_image_music.jpg").getImage());
	
	/******************************************************************************************************************************************************
	 * Menu Bar
	 * ****************************************************************************************************************************************************
	 */
	
	JMenuBar menuBar = new JMenuBar();
	menuBar.setBackground(new Color(240, 240, 240));
	setJMenuBar(menuBar);
	
	JMenu FileMenu = new JMenu("File");
	FileMenu.setMnemonic('F');
	menuBar.add(FileMenu);
	
	JMenuItem OpenMenuItem = new JMenuItem("Open...");
	OpenMenuItem.addActionListener(new ActionListener() {
		
	    public void actionPerformed(ActionEvent e) {
		   JFileChooser chooser = new JFileChooser();
	           chooser.showOpenDialog(null);
	           File file = chooser.getSelectedFile();
	           //file.getName().toLowerCase().endsWith(".mp3");
	           String filename = file.getName();
	         	    
		}
	});
	
	FileMenu.add(OpenMenuItem);
	
	JMenuItem CloseMenu = new JMenuItem("Close");
	FileMenu.add(CloseMenu);
	
	JMenuItem RPIMenu = new JMenuItem("Recently played item");
	FileMenu.add(RPIMenu);
	
	JMenuItem ExitMenu = new JMenuItem("Exit");
	ExitMenu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
		    System.exit(0);
		}
	});
	FileMenu.add(ExitMenu);
	
	JMenu HelpMenu = new JMenu("Help");
	HelpMenu.setMnemonic('H');
	menuBar.add(HelpMenu);
	
	JMenuItem AboutMenu = new JMenuItem("About");
	HelpMenu.add(AboutMenu);
	
	JMenuItem ContentsMenu = new JMenuItem("Help Contents");
	HelpMenu.add(ContentsMenu);
	
	/*
	 * **************************************************************************************************************************************************
	 */
	
	
	contentPane = new JPanel();
	contentPane.setBackground(new Color(240, 240, 240));
	contentPane.setBorder(UIManager.getBorder("MenuBar.border"));
	setContentPane(contentPane);
	
	
	
	/*****************************************************************************************************************************************************
	 * Adding genre list choice
	 * ****************************************************************************************************************************************************
	 */
	/*
	 * Setting Fonts
	 */
	Font SansSerif = new Font ("SansSerif", Font.PLAIN, 14);
	contentPane.setFont(SansSerif);
	
	JPanel panel_2 = new JPanel();
	contentPane.add(panel_2);
	
	textField_2 = new JTextField();
	textField_2.setColumns(10);
	
	JLabel lblAlbum = new JLabel("Album");
	lblAlbum.setFont(new Font("Showcard Gothic", Font.PLAIN, 11));
	GroupLayout gl_panel_2 = new GroupLayout(panel_2);
	gl_panel_2.setHorizontalGroup(
		gl_panel_2.createParallelGroup(Alignment.LEADING)
			.addGroup(gl_panel_2.createSequentialGroup()
				.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_2.createSequentialGroup()
						.addGap(45)
						.addComponent(lblAlbum))
					.addGroup(gl_panel_2.createSequentialGroup()
						.addGap(16)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(17, Short.MAX_VALUE))
	);
	gl_panel_2.setVerticalGroup(
		gl_panel_2.createParallelGroup(Alignment.LEADING)
			.addGroup(gl_panel_2.createSequentialGroup()
				.addGap(5)
				.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lblAlbum))
	);
	panel_2.setLayout(gl_panel_2);
	
	JPanel panel_1 = new JPanel();
	contentPane.add(panel_1);
	
	textField = new JTextField();
	textField.setColumns(10);
	
	JLabel lblGenre = new JLabel("Genre");
	lblGenre.setFont(new Font("Showcard Gothic", Font.PLAIN, 11));
	
	textField_1 = new JTextField();
	textField_1.setColumns(10);
	
	JLabel lblArtist = new JLabel("Artist");
	lblArtist.setFont(new Font("Showcard Gothic", Font.PLAIN, 11));
	GroupLayout gl_panel_1 = new GroupLayout(panel_1);
	gl_panel_1.setHorizontalGroup(
		gl_panel_1.createParallelGroup(Alignment.LEADING)
			.addGroup(gl_panel_1.createSequentialGroup()
				.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(45)
						.addComponent(lblGenre))
					.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(46)
						.addComponent(lblArtist))
					.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(16)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(16)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(17, Short.MAX_VALUE))
	);
	gl_panel_1.setVerticalGroup(
		gl_panel_1.createParallelGroup(Alignment.LEADING)
			.addGroup(gl_panel_1.createSequentialGroup()
				.addGap(5)
				.addComponent(textField, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(lblGenre)
				.addGap(5)
				.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lblArtist))
	);
	panel_1.setLayout(gl_panel_1);
	//String genre = chooser.getSelectedItem();
	//logger.debug(genre);
	//chooser.addItemListener((ItemListener) this);
	
       /* Event.itemStateChanged(ItemEvent e)
	  //  {
	        String result = chooser.getSelectedItem();
	        myTextField.setText("Your Choice: " + result);
	        logger.debug(result);
	    //}
	
	*/
	/*
	 * **************************************************************************************************************************************************
	 */
	
	/****************************************************************************************************************************************************
	 * Adding Button
	 * *****************************************************************************************************************************************
	 */
	
	Component glue_2 = Box.createGlue();
	contentPane.add(glue_2);
	
	Component glue_4 = Box.createGlue();
	contentPane.add(glue_4);
	
	Component glue_5 = Box.createGlue();
	contentPane.add(glue_5);
	
	Component glue_6 = Box.createGlue();
	contentPane.add(glue_6);
	
	Component glue_7 = Box.createGlue();
	contentPane.add(glue_7);
	txtTypeGenrealbumartistName = new JTextField("Enter Song");
	contentPane.add(txtTypeGenrealbumartistName);
	txtTypeGenrealbumartistName.setSelectionColor(SystemColor.controlLtHighlight);
	txtTypeGenrealbumartistName.setPreferredSize(new Dimension(100, 20));
	txtTypeGenrealbumartistName.setBackground(SystemColor.activeCaption);
	txtTypeGenrealbumartistName.setForeground(new Color(102, 0, 204));
	
	JButton Pause= new JButton();
	Pause.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {//**********For pause Button
		}
	});
	//**************This whole is for progress bar***********************************
	//final LongTask task = new LongTask();
	//int length = 0;
	//final JProgressBar progressBar = new JProgressBar(0,task.getLengthOfTask());
	//progressBar.setValue(0);
	//progressBar.setStringPainted(true);
	//*********************************************************************************
	
	JButton Play=new JButton();
	Play.setAlignmentX(Component.RIGHT_ALIGNMENT);
	Play.setAlignmentY(Component.BOTTOM_ALIGNMENT);
	Play.addActionListener(new ActionListener() {
	    
		public void actionPerformed(ActionEvent e) {
		    //To Do from the recommender system
		    
		    if (txtTypeGenrealbumartistName.getText().equals("Enter Song"))
			JOptionPane.showMessageDialog(null,"Enter something in the textbox to play");
		    else
			{String UserInput= txtTypeGenrealbumartistName.getText();
			logger.debug(UserInput);
			//TO DO "UserInput to the recommender system
			
			                               //**********************OBSERVABLE*****************************
			try {
			    Thread.sleep(500);
			} catch (InterruptedException e2) {
			    // TODO Auto-generated catch block
			    e2.printStackTrace();
			} //waiting for the input		
			
			
			ObservableValue Observing_String = new ObservableValue(null);
		        
			TextObserver Update_Value = new TextObserver(Observing_String);
			Observing_String.addObserver(Update_Value);  ///*********************use as many observer as you want....If you find difficulty just email me.
			
		        //Debugging*****************************************************************
		        //Observe observe = new Observe();
		        //**************************************************************************
		        
		        
		        
		        /*
		         * Creating a list for checking
		         * Arrange the progressBar accordingly
		         * 
		         */
		        String[] Check=new String[6];
		        Check[0]="One";
	//	        progressBar.setValue(2);
		        Check[1]="Two";
	//	        progressBar.setValue(4);
		        Check[2]="Three";
	//	        progressBar.setValue(6);
		        Check[3]="Four";
	//	        progressBar.setValue(8);
		        Check[4]="Five";
	//	        progressBar.setValue(10);
		        Check[5]="Six";
		  
		        
		        //logger.debug("here1");
		        for ( int i=0;i< Check.length; i++ ) {
			    //  logger.debug( Iter.next() );
		            
		            Observing_String.setSong(Check[i]);
		            /*
		             * The updated value is shown in the text bars...here you will observe the same value as I took a single observer...rest of the thing is in email.
		             */
		            textField.setText(Update_Value.GetValue());
		            textField_1.setText(Update_Value.GetValue());
		            textField_2.setText(Update_Value.GetValue());
		            
		            try {
				Thread.sleep(2000);
			    } catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			    }
		        
		        
		        
			}
		        
	
		        
		        // DEBUGGING******************************************************************This is used when you run it via Observe.java
		        /*observe.startEngine();
		        
		        try
		        {
		            Thread.sleep(3000);
		        }
		        catch (InterruptedException Check)
		        {
		            Check.printStackTrace();
		        }

		        observe.stopEngine();*/ 
		        //Debugging*******************************************************************
			}
		        }
		});
	
	
	//contentPane.add(progressBar);             THE ADDITION OF PROGRESS BAR!
	
	//Play.setIcon(new ImageIcon("C:\\Users\\Marium Ahmad\\work\\P2P\\GUI\\ui\\P2P\\CT\\Button-Play-icon.png"));
	contentPane.add(Play);
	
	JButton Stop=new JButton();
	Stop.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {//*************For stop button
		}
	});
	
	Component glue = Box.createGlue();
	contentPane.add(glue);
	Stop.setAlignmentX(Component.RIGHT_ALIGNMENT);
	Stop.setAlignmentY(Component.TOP_ALIGNMENT);
	//Stop.setIcon(new ImageIcon("C:\\Users\\Marium Ahmad\\work\\P2P\\GUI\\ui\\P2P\\CT\\Button-Stop-icon.png"));
	contentPane.add(Stop);
	Pause.setAlignmentX(Component.RIGHT_ALIGNMENT);
	Pause.setAlignmentY(Component.TOP_ALIGNMENT);
	//Pause.setIcon(new ImageIcon("C:\\Users\\Marium Ahmad\\work\\P2P\\GUI\\ui\\P2P\\CT\\Button-Pause-icon.png"));
	contentPane.add(Pause);
	
	JButton Skip= new JButton();
	Skip.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {//*****For the Skip button
		}
	});
	
	Component glue_1 = Box.createGlue();
	contentPane.add(glue_1);
	//Skip.setIcon(new ImageIcon("C:\\Users\\Marium Ahmad\\work\\P2P\\GUI\\ui\\P2P\\CT\\button_fast_forward_right_next_skip_button_bak_icon_go_arrow.png"));
	contentPane.add(Skip);
	
	/****************************************************************************************************************************************************
	 * Message display
	 * **************************************************************************************************************************************************
	 */
	ScrollPane scrollPane = new ScrollPane();
	scrollPane.setForeground(Color.WHITE);
	scrollPane.setFont(new Font("Arial", Font.PLAIN, 12));
	scrollPane.setBackground(Color.WHITE);
	
	//panel.add(scrollPane, BorderLayout.CENTER);
	
	
    }

/**
 **************************************************************************************************************************************************************     
 * Creating a background
 **************************************************************************************************************************************************************
 */
    class ImagePanel extends JPanel {

	  private Image img;

	  public ImagePanel(String img) {
	    this(new ImageIcon(img).getImage());
	  }

	  public ImagePanel(Image img) {
	    this.img = img;
	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	  }

	  public void paintComponent(Graphics g) {
	    g.drawImage(img, 0, 0, null);
	  }

	} 
    }
