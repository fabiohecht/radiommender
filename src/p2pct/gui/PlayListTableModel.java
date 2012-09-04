package p2pct.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import p2pct.model.PlayListEntry;

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