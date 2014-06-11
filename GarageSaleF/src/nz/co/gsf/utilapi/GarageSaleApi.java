package nz.co.gsf.utilapi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;



import com.google.android.gms.maps.model.LatLng;

import nz.co.gsf.activities.MainActivity;
import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.garagesale.GarageSaleList;
import nz.co.gsf.garagesale.GarageSaleListHandler;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import nz.co.gsf.activities.GSPreferenceActivity;
import nz.co.gsf.preferences.DefaultPrefs;

public class GarageSaleApi {

	private static GarageSaleList mList = null;
    private static Handler progresshandler;
    private static SharedPreferences prefs;
    private static LatLng currentLocation;
	private static List<String> providersList;
	private static String providersListText;
	private static String providersViewText;
    private static String bestProvider;
    private static Geocoder geocoder;

		public static ArrayList<GarageSale> getGarageSalesFromServer(Context context) {
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		Looper.prepare();
		//myprogress = ProgressDialog.show(context, "Refreshing Sale List", "Please Wait", true, false);
		
		// set up our message - used to convey progress information
        Message msg = new Message();
        msg.what = 0;
        
        // install handler for processing gui update messages
        GarageSaleApi.progresshandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                switch (msg.what) {
                    case 0:
                        // update progress bar
                        //myprogress.setMessage("" + (String) msg.obj);
                        break;
                    case 1:
                        //myprogress.cancel();
                        break;
                    case 2: // error occurred
                        //myprogress.cancel();
                        break;
                }
            }
        };
		InputSource is = null;
			
		// instantiate our handler
        GarageSaleListHandler gslHandler = new GarageSaleListHandler(context, progresshandler);
		
		
		try {
            
			URL url = new URL(prefs.getString(GSPreferenceActivity.KEY_PREF_API_SERVER_URL, DefaultPrefs.API_SERVER_URL));
			
            // get our data via the url class
            is = new InputSource(url.openStream());

            // create the factory
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // create a parser
            SAXParser parser = factory.newSAXParser();

            // create the reader (scanner)
            XMLReader xmlreader = parser.getXMLReader();

            // assign our handler
            xmlreader.setContentHandler(gslHandler);
            
            //Update the Progress Handler Message
            msg = new Message();
            msg.what = 0;
            msg.obj = ("Parsing ...");
            GarageSaleApi.progresshandler.sendMessage(msg);
            
            // perform the synchronous parse
            xmlreader.parse(is);     
            
            //Update the Progress Handler Message
            msg = new Message();
            msg.what = 0;
            msg.obj = ("Parsing Complete");
            GarageSaleApi.progresshandler.sendMessage(msg);

            //Update the Progress Handler Message
            msg = new Message();
            msg.what = 0;
            msg.obj = ("Storing Garage-Sales on Device...");
            GarageSaleApi.progresshandler.sendMessage(msg);
            
            gslHandler.getList().persist();
            mList = gslHandler.getList();
            
            //Update the Progress Handler Message
            msg = new Message();
            msg.what = 0;
            msg.obj = ("Garage Sales Stored Successfully");
            GarageSaleApi.progresshandler.sendMessage(msg);
            
            //Cancel Progress Handler
            msg = new Message();
            msg.what = 1;
            GarageSaleApi.progresshandler.sendMessage(msg);
            
            return GarageSaleApi.mList.getAll();

        } catch (Exception e) {
            Log.d("GFS", "Exception: " + e.getMessage());
            msg = new Message();
            msg.what = 2; // error occured
            msg.obj = ("Caught an error retrieving Sale data: " + e.getMessage());
            GarageSaleApi.progresshandler.sendMessage(msg);
        } finally {
        	
        }
		
		return null;
		
	}
	
	public static ArrayList<GarageSale> getGarageSalesFromFile(Context context) {
		try {
			GarageSaleApi.mList = GarageSaleList.parse(context);
	        if (GarageSaleApi.mList == null) {
	            Log.d("GFS", "garageSaleList is null");

	            // we need to do this to allow the garageSaleList to have something to display!
	            // even though it is empty!
	            GarageSaleApi.mList = new GarageSaleList(context);
	        }

	        if (GarageSaleApi.mList.getCount() == 0) {
	            //tv.setText("There are No Sales Available");
	        } else {
	            //tv.setText("There are " + this.garageSaleList.getCount() + " sales.");
	        }
	       
	        return GarageSaleApi.mList.getAll();
	       
		} catch (Exception e) {
            Log.d("GFS", "Exception: " + e.getMessage());
        } finally {
        	
        }
	       
		return null;
	}
	
	public static LatLng getCurrentLocation(Context context){

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        bestProvider = locationManager.getBestProvider(criteria, true);
        providersListText="List of location providers:\n";
        providersList = locationManager.getAllProviders();
        for (int i = 0; i < providersList.size(); i++) {
            String provider = providersList.get(i);
            providersListText += provider.toUpperCase() + " is " + locationManager.isProviderEnabled(provider) + "\n";
        }
        providersViewText=bestProvider.toUpperCase();
        geocoder = new Geocoder(context);
        currentLocation = new LatLng(MainActivity.location.getLatitude(),
                MainActivity.location.getLongitude());
		return currentLocation;
	}
	
	public static String getCurrentAddress() {
        String address=null;
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(MainActivity.location.getLatitude(),
                    MainActivity.location.getLongitude(), 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses != null && addresses.size() > 0) {
            
            address=addresses.get(0).toString();
        }
        return address;
    }
	
	public static List<String> getProvidersList() {
		return providersList;
	}
	public static String getProvidersListText() {
		return providersListText;
	}
	public static String getProvidersViewText() {
		return providersViewText;
	}
	public static String getBestProvider() {
		return bestProvider;
	}


}
