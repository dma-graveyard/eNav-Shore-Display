package dk.frv.enav.esd.service.ais;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("hiding")
public class RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> extends HashMap<RouteSuggestionKey, RouteSuggestionData>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean containsKey( Object key){
		
		@SuppressWarnings("unchecked")
		RouteSuggestionKey suggestionKey = (RouteSuggestionKey) key;
		
		 Iterator<RouteSuggestionKey> iterator = this.keySet().iterator();
		    while (iterator.hasNext()) {
		    	RouteSuggestionKey currentKey = (RouteSuggestionKey) iterator.next();
		      if (currentKey.equals(suggestionKey)){
		    	  return true;
		      }
		    }
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RouteSuggestionData get(Object key){
		RouteSuggestionKey suggestionKey = (RouteSuggestionKey) key;
		
	    for (Iterator<Entry<RouteSuggestionKey, RouteSuggestionData>> it = this.entrySet().iterator(); it.hasNext();) {
	        @SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();
	        RouteSuggestionKey currentKey = (RouteSuggestionKey) entry.getKey();
	        RouteSuggestionData value = (RouteSuggestionData) entry.getValue();
	        if (currentKey.equals(suggestionKey)){
	        	return value;
	        }
	      }
		return null;
	}
	

}
