/*
 * ISCG7424 – Mobile Software Development
 * Assignment 3: Garage Sales Finder
 * Parts: All
 * Student: Paul Shalley		(ID:1402195)
 * Student: Renato De Mendonca	(ID:1422497)
 * Student: Sergey Seriakov 	(ID:1405156)
 * Teacher: Dr. John Casey
 * 2014.
 */

package nz.co.gsf.garagesale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

import nz.co.gsf.activities.GSPreferenceActivity;
import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.preferences.DefaultPrefs;
import nz.co.gsf.utilapi.DistanceTool;
import nz.co.gsf.utilapi.GarageSaleApi;

import com.google.android.gms.maps.model.LatLng;


public class GarageSale implements Parcelable{
    String id;
    String address;
    String suburb;
    String geocode;
    String description;
    String date;
    String time;
    String region;
    Double distance,mRoundedDistance;
    private static Context context;
    private static SharedPreferences prefs;

    private LatLng mLatLng;
    public static final DecimalFormat distanceFormat = new DecimalFormat("#.0");
    


    public GarageSale() {
    }
    
    public GarageSale(String vID, 
    				  String vAddress, 
    				  String vSuburb, 
    				  String vGeocode, 
    				  String vDescription,
    				  String vDate,
    				  String vTime,
    				  String vRegion) {
    	setId(vID);
    	setAddress(vAddress);
    	setSuburb(vSuburb);
    	setGeocode(vGeocode);
    	setDescription(vDescription);
    	setDate(vDate);
    	setTime(vTime);
    	setRegion(vRegion);
    }
    
    private GarageSale(Parcel in) {
    	this(in.readString(),
    		 in.readString(),
    		 in.readString(),
    		 in.readString(),
    		 in.readString(),
    		 in.readString(),
    		 in.readString(),
    		 in.readString());	
    }
    
    public static void setContext(Context c) {
    	context = c;
    }
    
    // this is used to regenerate your object. All Parcelables must have a
    // CREATOR that implements these two methods
    public static final Parcelable.Creator<GarageSale> CREATOR = new Parcelable.Creator<GarageSale>() {
        public GarageSale createFromParcel(Parcel in) {
            return new GarageSale(in);
        }

        public GarageSale[] newArray(int size) {
            return new GarageSale[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(address);
        dest.writeString(suburb);
        dest.writeString(geocode);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(region);
    }


    public String toString() {
        if (distance != null)
            return address + "\n" + suburb + ", " + region + "\n\t"
                    + String.format("Dist: %5.0f km. ", distance);
        else
            return address + "\n" + suburb + ", " + region;
    }

    public String toXMLString() {
        StringBuilder xs = new StringBuilder("");
        xs.append("<garagesale>");
        xs.append("<id>" + id + "</id>");
        xs.append("<address>" + address + "</address>");
        xs.append("<suburb>" + suburb + "</suburb>");
        xs.append("<geocode>" + geocode + "</geocode>");
        xs.append("<description>" + description + "</description>");
        xs.append("<date>" + date + "</date>");
        xs.append("<time>" + time + "</time>");
        xs.append("<region>" + region + "</region>");
        xs.append("</garagesale>");
        return xs.toString() + "/n";
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuburb() {
        return this.suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getGeocode() {
        return this.geocode;
    }

    public void setGeocode(String geocode) {
        this.geocode = geocode;
        String[] coords = geocode.split(",");

        if ((coords != null) && (coords.length == 2)) {
            LatLng dest = new LatLng(Double.parseDouble(coords[0]), Double
                    .parseDouble(coords[1]));
            if (GarageSaleApi.getCurrentLocation(context) != null) {
                LatLng curr = new LatLng(GarageSaleApi.getCurrentLocation(context).latitude,
                		GarageSaleApi.getCurrentLocation(context).longitude);
                this.distance = DistanceTool.distanceBetweenPlaces(curr, dest);
            }
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("id", this.id);
        b.putString("address", this.address);
        b.putString("suburb", this.suburb);
        b.putString("geocode", this.geocode);
        b.putString("description", this.description);
        b.putString("date", this.date);
        b.putString("time", this.time);
        b.putString("region", this.region);
        b.putString("Distance", this.distance.toString());

        return b;
    }

    public static GarageSale fromBundle(Bundle b) {
        GarageSale garageSale = new GarageSale();
        garageSale.setId(b.getString("id"));
        garageSale.setAddress(b.getString("address"));
        garageSale.setSuburb(b.getString("suburb"));
        garageSale.setGeocode(b.getString("geocode"));
        garageSale.setDescription(b.getString("description"));
        garageSale.setDate(b.getString("date"));
        garageSale.setTime(b.getString("time"));
        garageSale.setRegion(b.getString("region"));
        garageSale.setDistance(Double.parseDouble(b.getString("distance")));
        return garageSale;
    }
    private void setDistance(Double m) {
        this.distance = m;
        this.mRoundedDistance = (double) Math.round(distance * 10) / 10;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public String getFormattedDistance() {
        return distanceFormat.format(distance);
    }
    
    public Double getRoundedDistance() {
    	this.mRoundedDistance = (double) Math.round(distance * 10) / 10;
    	return mRoundedDistance;
    }
    
    public LatLng getLatLng() {
    	//Double lat = Double.parseDouble(geocode.substring(0,geocode.indexOf(",",0)));
    	//Double lng = Double.parseDouble(geocode.substring(geocode.indexOf(",",0)+1,geocode.length()));
    	 String[] coords = geocode.split(",");
         if ((coords != null) && (coords.length == 2)) {
             mLatLng = new LatLng(Double.parseDouble(coords[0]), Double
                     .parseDouble(coords[1]));
         }
    	    
    	return mLatLng;
    }
    private void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }
    
    public float getHue() {
        // https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/BitmapDescriptorFactory
        // Red is 0
        // Orange is 30
        // Yellow is 60
        // Green is 120

        // TODO Apply an appropriate non-linear scale to these - 4.0 not much redder than a 3.0, currently.
    	prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int maxHighlightDistance = prefs.getInt(GSPreferenceActivity.KEY_PREF_MAX_HIGHLIGHT_DISTANCE, DefaultPrefs.MAX_HIGHLIGHT_DISTANCE);
        float hue;
        if (getDistance() <= maxHighlightDistance) {
        	hue = 120;
        } else {
        	hue = 60;
        }
        /*float percentage = (float) (getDistance() / maxHighlightDistance);
        hue = 90 - (percentage * 90);

        if (hue < 0)
            hue = 0;
        else if (hue > 90)
            hue = 90;
        */
        return hue;
}

}
