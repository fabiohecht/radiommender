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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.radiommender.model.PlayListEntry;


public class PlayListTableModel
    implements TableModel
{
	private String[] columns = new String[]{"Artist", "Album", "Title", "Genre", "Origin"};
	private int[] columnWidths = new int[]{100,100,200,50,20};
    private List<List<String>> data = new ArrayList<List<String>>();
    private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

    @Override
    public int getRowCount()
    {
    	synchronized (this.data) {
    		return data.size();
    	}
    }

    @Override
    public int getColumnCount()
    {
        return this.columns.length;
    }

    @Override
    public String getColumnName( int columnIndex )
    {
    	return this.columns[columnIndex];
    }

    @Override
    public Class<?> getColumnClass( int columnIndex )
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex )
    {
        return false;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
    	synchronized (this.data) {
    		try {
    			return data.get( rowIndex ).get( columnIndex );
    		}
    		catch (IndexOutOfBoundsException e) {
    			e.printStackTrace();
    			return null;
    		}
    	}
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex )
    {
        throw new RuntimeException( "cannot edit here" );
    }

    private void addRow(String genre, String artist, String album, String title, String origin)
    {
    	synchronized (this.data) {
	        List<String> tmp=new ArrayList<String>();            
	        tmp.add( artist );
	        tmp.add( album );
	        tmp.add( title );
	        tmp.add( genre );
	        tmp.add( origin );
	        data.add( tmp );
    	}
        notifyListeners();
    }

    @Override
    public void addTableModelListener( TableModelListener l )
    {
        listeners.add( l );
    }

    @Override
    public void removeTableModelListener( TableModelListener l )
    {
        listeners.remove( l );
    }

    private void notifyListeners()
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                for ( TableModelListener l : listeners )
                {
                    l.tableChanged( new TableModelEvent( PlayListTableModel.this ) );
                }
            }
        } );
    }

	public void update(List<PlayListEntry> songs) {
    	synchronized (this.data) {	
			this.data.clear();
			for (PlayListEntry song : songs) {
				this.addRow(song.getSong().getGenre(), song.getSong().getArtist(), song.getSong().getAlbum(), song.getSong().getTitle(), song.getOrigin());
			}
    	}
        notifyListeners();
	}

	public void setWidths(TableColumnModel columnModel) {
		for (int i=0; i<columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
			columnModel.getColumn(i).setWidth(columnWidths[i]);
		}
	}
}