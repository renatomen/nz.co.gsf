package nz.co.gsf.garagesale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class GarageSaleFilter {

    public static ArrayList<GarageSale> filterGarageSales(Collection<GarageSale> garagesale, float maxDistance, int maxNumGarageSales, CharSequence key) {
        ArrayList<GarageSale> filteredResult = new ArrayList<GarageSale>();
        
        for (GarageSale g : garagesale) {
        	if ((!key.toString().trim().isEmpty()) &&
        		 ( (g.getDescription().toUpperCase().indexOf(key.toString().toUpperCase())>=0) ||
        				(g.getAddress().toUpperCase().indexOf(key.toString().toUpperCase())>=0) ||
        				(g.getSuburb().toUpperCase().indexOf(key.toString().toUpperCase())>=0) ||
        				(g.getRegion().toUpperCase().indexOf(key.toString().toUpperCase())>=0)))
        	{
        		if (g.getRoundedDistance() >= maxDistance) filteredResult.add(g);
        	} else if (key.toString().trim().isEmpty()){
        		if (g.getRoundedDistance() >= maxDistance) filteredResult.add(g);
        	}        
        } 
     

       if (filteredResult.size() > maxNumGarageSales) 
            filteredResult = new ArrayList<GarageSale>(filteredResult.subList(0, maxNumGarageSales));
       
       Collections.sort(filteredResult, new Comparator<GarageSale>() {
    	   @Override
    	   public int compare(GarageSale left, GarageSale right)
    	   {
    		   return (left.getDistance()<right.getDistance()) ? - 1 : (left.getDistance()>right.getDistance())? 1 : 0;
    	   }
       });
        
        return filteredResult;
        
    }
}
