package p2pct.tests.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TextObserver implements Observer {
    
	Logger logger = LoggerFactory.getLogger(TextObserver.class);
	
    private ObservableValue user_input;
   
    private Object tmpo;
    
    public TextObserver(ObservableValue user_input)
    {
    
	this.user_input=user_input;
	
    }
    public void update(Observable obs, Object obj)
    {     
    
       //for ( Iterator Iter = user_input.iterator(); Iter.hasNext(); ) {
	    //  logger.debug( Iter.next() );
       //logger.debug(user_input);
      // }
       
        ObservableValue tmp = (ObservableValue) obs;
        //logger.debug("++" + tmp.getSong() + "++");
        this.tmpo=tmp.getSong()+"Change";
        
       logger.debug("+++"+this.tmpo+"+++");
      }

    /**
     * @return
     */
    public String GetValue(){
	return (String) this.tmpo;
    }
    
    
    }

