package nz.co.gsf.garagesale;

import java.util.ArrayList;

import nz.co.gsf.activities.GSPreferenceActivity;
import nz.co.gsf.preferences.*;
import nz.co.gsf.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GarageSaleArrayAdapter extends ArrayAdapter<GarageSale> {

    private ArrayList<GarageSale> mGarageSales;
    private int mMaxHighlight;
    private LayoutInflater mInflater;
    private static SharedPreferences prefs;


    public GarageSaleArrayAdapter(Context context, int textViewResourceId,
                                  ArrayList<GarageSale> garagesales) {
        super(context, textViewResourceId, garagesales);
        this.mGarageSales = garagesales;

        // Setup preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mMaxHighlight = prefs.getInt(GSPreferenceActivity.KEY_PREF_MAX_HIGHLIGHT_DISTANCE, DefaultPrefs.MAX_HIGHLIGHT_DISTANCE);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row, parent, false);
        }

        GarageSale GarageSaleItem = mGarageSales.get(position);
        if (GarageSaleItem != null) {
            TextView itemView = (TextView) convertView
                    .findViewById(R.id.distance);
            if (itemView != null) {
            	
            	itemView.setText(GarageSaleItem.getFormattedDistance());
                if (GarageSaleItem.getRoundedDistance() <= mMaxHighlight)
                    itemView.setTextColor(Color.RED);
                else
                    itemView.setTextColor(Color.BLACK);
            }

            itemView = (TextView) convertView
                    .findViewById(R.id.km);
            if (itemView != null) {
            	
            	if (GarageSaleItem.getRoundedDistance() <= mMaxHighlight)
                    itemView.setTextColor(Color.RED);
                else
                    itemView.setTextColor(Color.BLACK);
            }
                     
            itemView = (TextView) convertView.findViewById(R.id.location);
            if (itemView != null) {
            	itemView.setText(GarageSaleItem.getSuburb()+" / "+GarageSaleItem.getRegion());
            }
            
            String date = GarageSaleItem.getDate() + " / "
          			 +GarageSaleItem.getTime();
            itemView = (TextView) convertView.findViewById(R.id.date);
            if (itemView != null) itemView.setText(date);
               
            
            itemView = (TextView) convertView.findViewById(R.id.address);
            if (itemView != null) {
            	itemView.setText(GarageSaleItem.getAddress());
            }
			
	
        }
        return convertView;
    }
}
