package nz.co.gsf.activities;

import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.fragments.GarageSaleDetailFragment;
import nz.co.gsf.fragments.GMapFragment;
import nz.co.gsf.R;
import android.content.Intent;
import android.os.Bundle;

public class GarageSaleActivity extends GarageSaleFinderActivity {
    public static String GARAGESALE_KEY = "nz.co.gsf.SingleGarageSale";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garagesale);

        Intent sender = getIntent();
        final GarageSale garageSaleItem = sender.getParcelableExtra(GARAGESALE_KEY);
        GMapFragment map = (GMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.garagesale_map);
        
        // If we have a saved state, we're being restored or rotated.
        // In this case, we don't want to overwrite the saved map zoom/center location.
        boolean centerOnGarageSale = savedInstanceState == null;
        map.setGarageSale(garageSaleItem, centerOnGarageSale);
        GarageSaleDetailFragment detail = (GarageSaleDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.garagesale_detail);
        detail.setGarageSale(garageSaleItem);
    }
}
