package nz.co.gsf.activities;

import nz.co.gsf.R;
import nz.co.gsf.utilapi.GarageSaleApi;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class AboutActivity extends GarageSaleFinderActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    
        TextView text = (TextView) this.findViewById(R.id.text);
        text.setText(GarageSaleApi.getProvidersListText());
       
        
        TextView prvdView = (TextView) this.findViewById(R.id.prvdView);
        prvdView.setText(GarageSaleApi.getBestProvider());
        
        TextView cView = (TextView) this.findViewById(R.id.cView);
        cView.setText("Latitude,Longitude: " + GarageSaleApi.getCurrentLocation(this));
        
        EditText editText = (EditText) this.findViewById(R.id.editDistance);
        editText.setText("Best Provider: "+GarageSaleApi.getCurrentAddress()); 
        
      
       
    }
    

}
