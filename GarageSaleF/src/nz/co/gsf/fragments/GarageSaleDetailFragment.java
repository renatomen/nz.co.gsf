package nz.co.gsf.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.utilapi.GarageSaleApi;
import nz.co.gsf.utilapi.Route;
import nz.co.gsf.R;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;


public class GarageSaleDetailFragment extends Fragment {

    private GarageSale mGarageSale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_garagesale_details, container, false);
    }

    public void setGarageSale(GarageSale garagesale) {
        mGarageSale = garagesale;
        displayGarageSaleDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayGarageSaleDetails();
    }

    private void displayGarageSaleDetails() {
        if (mGarageSale == null) return;
        if (getActivity() == null) return;
        View root = getView();
        if (root == null) return;
        
        TextView v = (TextView) root.findViewById(R.id.date_detail_field);
        if (null != v) {
            String date = mGarageSale.getDate()+" / "+mGarageSale.getTime();
            if (null != date) {
               v.setText(date);
            }
        }

            
        v = (TextView) root.findViewById(R.id.region_detail_field);
        if (null != v){
            v.setText(mGarageSale.getRegion());
    	} else {
    		v.setText(" ");
    	}
            
        v = (TextView) root.findViewById(R.id.address_detail_field);
        if (null != v)
            v.setText(mGarageSale.getAddress());
        
        v = (TextView) root.findViewById(R.id.suburb_detail_field);
        if (null != v)
            v.setText(mGarageSale.getSuburb());

            
        v = (TextView) root.findViewById(R.id.distance_detail_field);
        if (null != v)
            v.setText(mGarageSale.getFormattedDistance() + " km");

        v = (TextView) root.findViewById(R.id.description_detail_field);
        if (null != v) {
            v.setMovementMethod(new ScrollingMovementMethod());
        	v.setText(mGarageSale.getDescription());
        }
        
        ImageView iv = (ImageView) root.findViewById(R.id.sendMail_button);
        if (null != iv) {
                iv.setOnClickListener(new View.OnClickListener() {
            		@Override
            		public void onClick(View v) {
            			String address = mGarageSale.getAddress() + " " + mGarageSale.getSuburb() + " "
                                + mGarageSale.getRegion();
            			String mailBody = getResources().getString(R.string.mailBody_intro) + "\n\n"+
                                		  "http://www.garage-sales.co.nz/view/"+mGarageSale.getId() +"\n\n"+
                                		  getResources().getString(R.string.mailBody_where) + " "+
                                		  address + "\n\n " +
            							  getResources().getString(R.string.mailBody_when) +" "+
            							  mGarageSale.getDate() +".\n\n" +
            						      getResources().getString(R.string.mailBody_close);
                        Intent emailListing = new Intent(Intent.ACTION_SEND);
                        emailListing.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                        emailListing.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.mailSubject));
                        emailListing.putExtra(Intent.EXTRA_TEXT, mailBody);
                        emailListing.setType("message/rfc822");
                        startActivity(Intent.createChooser(emailListing, "Choose an Email Client :"));

            		}
            	});
            }
        	
        iv = (ImageView) root.findViewById(R.id.goToWebsite_button);
        if (null != iv) {
            iv.setOnClickListener(new View.OnClickListener() {
        		@Override
        		public void onClick(View v) {
                    String url = "http://www.garage-sales.co.nz/view/"+mGarageSale.getId();

                    try {
                        Intent webIntent = new Intent("android.intent.action.VIEW",
                                android.net.Uri.parse(url));

                        startActivity(webIntent);
                    } catch (Exception ee) {
                        Log.d("GFS", "error launching map? " + ee.getMessage());
                    }

        		}
        	});
        }
        
        iv = (ImageView) root.findViewById(R.id.externalNavigation_button);
        if (null != iv) {
            iv.setOnClickListener(new View.OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			// clean up data for use in GEO query
                    String address = mGarageSale.getAddress() + " " + mGarageSale.getSuburb() + " "
                            + mGarageSale.getRegion();
                    String cleanAddress = address.replace(",", "");
                    cleanAddress = cleanAddress.replace(' ', '+');
                    String locationStr = "geo:" + mGarageSale.getGeocode();

                    try {
                        Intent geoIntent = new Intent("android.intent.action.VIEW",
                                android.net.Uri.parse(locationStr + "?q=" + cleanAddress));

                        startActivity(geoIntent);
                    } catch (Exception ee) {
                        Log.d("GFS", "error launching map? " + ee.getMessage());
                    }

        		}
        	});
        }

        iv = (ImageView) root.findViewById(R.id.directions_button);
        if (null != iv) {
        	iv.setOnClickListener(new View.OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			ArrayList<LatLng> routePoints = new ArrayList<LatLng>();
        			routePoints.add(GarageSaleApi.getCurrentLocation(getActivity().getApplicationContext()));
        			routePoints.add(mGarageSale.getLatLng());
        			GMapFragment mMap = new GMapFragment();
        			mMap = (GMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.garagesale_map);
        			if (!(mMap == null)) { 
        				Route destRoute = new Route();
        				destRoute.drawRoute(mMap.getMap(), getActivity(), routePoints,true, Route.LANGUAGE_ENGLISH, true);
        			} 
        		}
        	});
        }
        
    }
 
    public GarageSale getGarageSale() {
        return mGarageSale;
    }
}
