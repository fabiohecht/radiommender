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
package org.radiommender.ui.gui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.radiommender.core.Application;
import org.radiommender.model.AffinityEntry;
import org.radiommender.model.PlayListEntry;
import org.radiommender.model.SearchTermRankingEntry;
import org.radiommender.model.Song;
import org.radiommender.model.SongVoteEntry;
import org.radiommender.player.Player;
import org.radiommender.ui.Ui;
import org.radiommender.utils.ConfigurationFactory;
import org.radiommender.utils.ExecutorPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.miginfocom.swing.MigLayout;

public class Gui implements Ui
{
    Logger logger = LoggerFactory.getLogger(Gui.class);
    	
    final private JLabel nowPlaying = new JLabel( " " );
    final private PlayListTableModel playListTableModel = new PlayListTableModel();
    final private VoteTableModel voteTableModel = new VoteTableModel();
    final private RatingTableModel ratingTableModel = new RatingTableModel();
    final private AffinityTableModel actualAffinityTableModel = new AffinityTableModel();
    final private AffinityTableModel estimatedAffinityTableModel = new AffinityTableModel();
    
    // module vars
    private Application application;
    private JFrame frame;
	private String peerId;
    JDialog detailsDialog;

    public Gui(Application application, boolean askBootstrap, boolean askMusicPath, int position)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {    	
    	this.application = application;
    	
    	
        String nativeLF = UIManager.getSystemLookAndFeelClassName();
        // Install the look and feel
        UIManager.setLookAndFeel( nativeLF );

        MigLayout layout = new MigLayout();
        frame = new JFrame( "Radiommender" );
        frame.setLayout( layout );

        // ask for bootstrap peer
        String bootstrapTo;
        if(askBootstrap){
		    bootstrapTo = (String) JOptionPane.showInputDialog( frame, "Connect to network", "Bootstrap peer", JOptionPane.QUESTION_MESSAGE, null, null, ConfigurationFactory.getProperty("overlay.network.bootstrap.ip") + ":" + ConfigurationFactory.getProperty("overlay.network.bootstrap.port") );
		    if ( bootstrapTo == null || bootstrapTo.isEmpty() )
		    {
		        JOptionPane.showMessageDialog( frame, "bootstrap not found" );
		        System.exit( 1 );
		    } else{
		    	String[] ip = bootstrapTo.split(":");
		    	ConfigurationFactory.setProperty("overlay.network.bootstrap.ip", ip[0]);
		    	if(ip.length > 1){
		    		ConfigurationFactory.setProperty("overlay.network.bootstrap.port", ip[1]);
		    	}
		    }
        } else{
        	bootstrapTo = ConfigurationFactory.getProperty("overlay.network.bootstrap.ip");
        }
        
        // ask for music library
        if(askMusicPath)
        {
            if(!share( frame ))
            {
                JOptionPane.showMessageDialog( frame, "music library required" );
                System.exit( 2 );
            }
        }
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();
        int width = (int) (bounds.getWidth()/2), height = (int) (bounds.getHeight()/2);

        frame.setSize( width, height );
        int x = (int) (position%2==0?bounds.getMinX()+width:bounds.getMinX());
		int y = (int) (position>2?bounds.getMaxY()-height:bounds.getMinY());
		//System.out.println("x:"+x+" y:"+y+" w:"+width+" h:"+height);
        frame.setLocation(x,y);
        
        initLayout( frame );
        //frame.setVisible( true );
        //init( bootstrapTo );
    }
    
    public void showFrame(){
    	frame.setVisible(true);
    }

    private void initLayout( final JFrame frame )
    {
        Icon iconPlay = new ImageIcon( Gui.class.getResource( "/play.png" ) );
        Icon iconSkip = new ImageIcon( Gui.class.getResource( "/skip.png" ) );
        Icon iconStop = new ImageIcon( Gui.class.getResource( "/stop.png" ) );
        JButton play = new JButton( iconPlay );
        final JTextField searchTerm = new JTextField();
        searchTerm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//enter key on serchTerm field
				play( searchTerm.getText() );
			}
		});
        
        play.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
            	//mouse click on play button
                play( searchTerm.getText() );
            }
        } );
        frame.add( new JLabel( "Search term: " ), "split, pushx" );
        frame.add( searchTerm, "growx, span 2" );
        frame.add( play, "wrap" );
        frame.add( new JLabel( "Now playing:" ), "split, pushx" );
        frame.add( nowPlaying, "growx" );
        JButton skip = new JButton( iconSkip );
        skip.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                skip();
            }
        } );
        frame.add( skip, "" );
        JButton stop = new JButton( iconStop );
        stop.addActionListener( new ActionListener()
        {

            @Override
            public void actionPerformed( ActionEvent e )
            {
                stop();
            }
        } );
        frame.add( stop, "wrap" );
        frame.add( new JLabel( "Play List" ), "split, span, gaptop 10" );
        frame.add( new JSeparator(), "growx, wrap, gaptop 10" );

        JTable playListTable = new JTable( playListTableModel );
        playListTable.setFocusable(false);
        playListTable.setRowSelectionAllowed(false);
        JScrollPane scrollPane = new JScrollPane( playListTable );
        playListTableModel.setWidths(playListTable.getColumnModel());
        frame.add( scrollPane, "span, grow, push 100 100" );
        
        JButton share = new JButton( "Share song..." );
        share.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                share(frame);
            }
        } );
        frame.add( share, "split" );
        
        JButton details = new JButton( "Details..." );
        details.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                details(frame);
            }
        } );
        frame.add( details, "" );
        
        JButton test = new JButton( "Test" );
        test.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                updateCurrentSong(new Song("genre", "artist artist artist", "album album album album album", "title title title title title title"), "AT");
                
                List<PlayListEntry> songs = new ArrayList<PlayListEntry>(10);
                for (int i=0; i<=10; i++) {
                	songs.add(new PlayListEntry(new Song(i+" genre",i+ " artist artist artist", i+" album album album album album", i+" title title title title title title"),i%5==4?"AN":"ST", i));
                }
				updatePlayList(songs);
				
				List<SongVoteEntry> songVotes = new ArrayList<SongVoteEntry>(10);
                for (int i=0; i<=10; i++) {
                	songVotes.add(new SongVoteEntry(new Song(i+" genre",i+ " artist artist artist", i+" album album album album album", i+" title title title title title title"), "searchTerm", i%2==0?true:false));
                }
				updateVotes(songVotes);
				
				List<SearchTermRankingEntry> ratings = new ArrayList<SearchTermRankingEntry>(10);
				for (int i=0; i<=10; i++) {
					ratings.add(new SearchTermRankingEntry("search term "+i, new Song(i+" genre",i+ " artist artist artist", i+" album album album album album", i+" title title title title title title"), 1F*10-(i)));
				}
				updateSearchTermRanking(ratings);
				
				List<AffinityEntry> affinityEntries = new ArrayList<AffinityEntry>(10);
				for (int i=0; i<=10; i++) {
					affinityEntries.add(new AffinityEntry(i+ " actual user user", .1F*10-(i)));
				}
				updateActualAffinity(affinityEntries);
				
				List<AffinityEntry> affinityEntries2 = new ArrayList<AffinityEntry>(10);
                for (int i=0; i<=10; i++) {
                	affinityEntries2.add(new AffinityEntry(i+ " estimated user user", .1F*10-(i)));
                }
				updateEstimatedAffinity(affinityEntries2);
            }
        } );
        //frame.add( test, "" );
        
        //Details dialog
        
        detailsDialog = new JDialog( frame , "Details for "+peerId);
        detailsDialog.getContentPane().setLayout( new MigLayout() );
        detailsDialog.setSize( 1000, 400 );
        
        
        JPanel serchTermPanel = new JPanel(new MigLayout());
        serchTermPanel.add(new JLabel( "SearchTerm" ), "wrap");
        serchTermPanel.add( new JLabel("Votes"), "" );
        serchTermPanel.add( new JLabel("Ratings"), "wrap" );
        JTable voteTable = new JTable( voteTableModel );
        voteTable.setFocusable(false);
        voteTable.setRowSelectionAllowed(false);
        voteTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane1 = new JScrollPane( voteTable );
        voteTableModel.setWidths(voteTable.getColumnModel());
        serchTermPanel.add( scrollPane1, "w 300" );
        JTable ratingTable = new JTable( ratingTableModel );
        ratingTable.setFocusable(false);
        ratingTable.setRowSelectionAllowed(false);
        ratingTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane2 = new JScrollPane( ratingTable );
        ratingTableModel.setWidths(ratingTable.getColumnModel());
        serchTermPanel.add( scrollPane2, "w 400" );
        detailsDialog.getContentPane().add(serchTermPanel, "w 700");
        
        JPanel affinityNetworkPanel = new JPanel(new MigLayout()); 
        affinityNetworkPanel.add(new JLabel( "Affinity Network" ), "wrap");
        affinityNetworkPanel.add( new JLabel("Actual Affinity"), "" );
        affinityNetworkPanel.add( new JLabel("Estimated Affinity"), "wrap" );
        JTable actualAffinityTable = new JTable( actualAffinityTableModel );
        actualAffinityTable.setFocusable(false);
        actualAffinityTable.setRowSelectionAllowed(false);
        actualAffinityTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane3 = new JScrollPane( actualAffinityTable );
        actualAffinityTableModel.setWidths(actualAffinityTable.getColumnModel());
        affinityNetworkPanel.add( scrollPane3, "" );
        JTable estimatedAffinityTable = new JTable( estimatedAffinityTableModel );
        estimatedAffinityTable.setFocusable(false);
        estimatedAffinityTable.setRowSelectionAllowed(false);
        estimatedAffinityTable.setAutoCreateRowSorter(true);
        estimatedAffinityTableModel.setWidths(estimatedAffinityTable.getColumnModel());
        JScrollPane scrollPane4 = new JScrollPane( estimatedAffinityTable );
        affinityNetworkPanel.add( scrollPane4, "" );
        detailsDialog.getContentPane().add(affinityNetworkPanel, "w 300");
        
        
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e )
            {
                shutdown();
                System.exit( 0 ); // calling the method is a must
            }
        } );
        
    }
    
    private boolean share(JFrame frame)
    {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        int returnVal = fc.showOpenDialog( frame );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File file = fc.getSelectedFile();
            ConfigurationFactory.setProperty( "file.store.path", file.toString() );
            shareFolder(file);
            return true;
        }
        else
        {
            // log to screen
            logger.debug( "cancel choice" );
            return false;
        }
    }

    private void shareFolder( File file )
    {
    	logger.info("will share folder '"+file.toString()+"'");
        //this.application.shareFolder(file);
    }

    private void play(final String searchTerm )
    {
    	Runnable runner = new Runnable() {
			@Override
			public void run() {
				try{
			    	logger.info("will play '"+searchTerm+"'");
			        application.play(searchTerm);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		ExecutorPool.getGeneralExecutorService().execute(runner);    	
    }

    private void details(JFrame frame)
    {
        detailsDialog.setVisible( true );
    }

    private void stop()
    {
        this.application.stop();
    }

    private void skip()
    {
        this.application.skip();
    }

    private void shutdown()
    {
    	this.application.shutdownApplication();
    }
    
    
    @Override
	public void updateCurrentSong(Song song, String origin) {
    	if (song==null) {
    		nowPlaying.setText("-");
    	}
    	else {
    		nowPlaying.setText(song.toString()+" ["+origin+"]");
    	}
	}
    
    @Override
    public void updatePlayList(List<PlayListEntry> songs) {
    	logger.debug("updatePlayList: "+songs.size());
    	this.playListTableModel.update(songs);
    }

	@Override
	public void updateVotes(List<SongVoteEntry> songVotes) {
		this.voteTableModel.update(songVotes);
	}

	@Override
	public void updateSearchTermRanking(List<SearchTermRankingEntry> ratings) {
		this.ratingTableModel.update(ratings);
	}

	@Override
	public void updateActualAffinity(List<AffinityEntry> affinityEntries) {
		this.actualAffinityTableModel.update(affinityEntries);
	}

	@Override
	public void updateEstimatedAffinity(List<AffinityEntry> affinityEntries) {
		this.estimatedAffinityTableModel.update(affinityEntries);
	}

	@Override
	public void updatePeerId(String peerId) {
		this.peerId = peerId;
		this.frame.setTitle("Radiommender: "+peerId);
        detailsDialog.setTitle("Details for "+peerId);
	}

}
