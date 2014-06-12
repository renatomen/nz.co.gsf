package nz.co.gsf.fragments;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.garagesale.GarageSaleTapListener;
import nz.co.gsf.utilapi.GarageSaleApi;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import nz.co.gsf.R;

import java.util.ArrayList;
import java.util.HashMap;

public class GMapFragment extends SupportMapFragment implements GoogleMap.OnMarkerClickListener {
    private ArrayList<GarageSale> mGarageSale;
    private static final double NZ_CENTRE_LATITUDE = -41;
    private static final double NZ_CENTRE_LONGITUDE = 173;
    private CameraPosition mDefaultCameraPosition;

    private GarageSaleTapListener mListener;
    private HashMap<String, BitmapDescriptor> mMarkerImageContainer;
    private ArrayList<Marker> mMarkers;
    private HashMap<String, GarageSale> mMarkerIdToGarageSale;

    /**
     
     * 
     * AMONG OTHERS.. GREATLY INSPIRED IN
     * https://code.google.com/p/iosched/source/browse/android/src/main/java/com/google/android/apps/iosched/ui/MapFragment.java
     * 
     */
    public GMapFragment() {
        mMarkerIdToGarageSale = new HashMap<String, GarageSale>();
        mMarkers = new ArrayList<Marker>();
        mMarkerImageContainer = new HashMap<String, BitmapDescriptor>();
        mDefaultCameraPosition = new CameraPosition(new LatLng(NZ_CENTRE_LATITUDE, NZ_CENTRE_LONGITUDE), // target
                // TODO Zoom should probably change with device...
                5, // zoom
                0, // tilt
                0); // bearing
      /*  mDefaultCameraPosition = new CameraPosition(GarageSaleApi.getCurrentLocation(), // target
                // TODO Zoom should probably change with device...
                9, // zoom
                0, // tilt
                0); // bearing 
*/    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);  
       
        setInitialCameraPosition(savedInstanceState);
        updateOverlayItems();
      
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        GoogleMap map = getMap();
        if (map != null) {
            CameraPosition position = map.getCameraPosition();
            outState.putParcelable("mapPosition", position);
        }
    }

    private void setInitialCameraPosition(Bundle savedInstanceState) {
        GoogleMap map = getMap();
        if (map == null) return;
        CameraPosition camPosition = null;
        if (savedInstanceState != null) {
            camPosition = savedInstanceState.getParcelable("mapPosition");
        }

        if (camPosition == null) {
            Bundle args = getArguments();
            GoogleMapOptions googleMapOptions = null;

            if(args != null)
                googleMapOptions = (GoogleMapOptions)getArguments().get("MapOptions");

            if (googleMapOptions != null) {
                camPosition = googleMapOptions.getCamera();
            } else {
                camPosition = mDefaultCameraPosition;
            }
        }

        map.moveCamera(CameraUpdateFactory.newCameraPosition(camPosition));
    }

    public void setGarageSale(GarageSale garagesale, boolean centerOnGaragesale) {
        ArrayList<GarageSale> garagesales = new ArrayList<GarageSale>();
        garagesales.add(garagesale);
        setGarageSales(garagesales);
        if (centerOnGaragesale) {
            mDefaultCameraPosition = new CameraPosition(garagesale.getLatLng(), // target
                    getDefaultZoomForDevice() + 2, // zoom
                    0, // tilt
                    0); // bearing
            setInitialCameraPosition(null);
        }
    }

    public void setGarageSales(ArrayList<GarageSale> garagesales) {
        mGarageSale = garagesales;
        updateOverlayItems();
    }

    private void updateOverlayItems() {
        GoogleMap map = getMap();
        if (map == null || mGarageSale == null)
            return;

        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               if(mListener != null) mListener.onGarageSaleLostFocus(null);
            }
        });
        removeAllMarkers();
        for (GarageSale q : mGarageSale) {
            addGarageSaleMarker(map, q);
        }

    }

    private void addGarageSaleMarker(GoogleMap map, GarageSale q) {
        if (map == null || q == null) return;
        MarkerOptions markerOptions = getMarkerForGarageSale(q);
        Marker marker = map.addMarker(markerOptions);
        mMarkerIdToGarageSale.put(marker.getId(), q);
        mMarkers.add(marker);
    }

    private MarkerOptions getMarkerForGarageSale(GarageSale q) {
        MarkerOptions m = new MarkerOptions()
                .position(q.getLatLng())
                .icon(getIconForGarageSale(q));
        return m;
    }

    private BitmapDescriptor getIconForGarageSale(GarageSale q) {
       
        // Set up the colour for our marker image
        Drawable marker = getResources().getDrawable(R.drawable.mapmarker);
        int markerWidth = marker.getIntrinsicWidth();
        int markerHeight = marker.getIntrinsicHeight();
        int markerColor = Color.HSVToColor(0xC8, new float[]{q.getHue(), 1, 1});
        PorterDuffColorFilter filter = new PorterDuffColorFilter(markerColor, PorterDuff.Mode.MULTIPLY);
        Paint markerPaint = new Paint();
        markerPaint.setColorFilter(filter);

        // Set up the text paint & location (bounds) for painting onto our marker
        TextPaint textPaint = new TextPaint();
        Rect textBounds = new Rect();
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
        float textMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        textPaint.setTextSize(fontSize);
        textPaint.getTextBounds(q.getFormattedDistance(), 0, q.getFormattedDistance().length(), textBounds);
        textBounds.inset((int) -textMargin, (int) -textMargin);
        textBounds.offsetTo(markerWidth / 2 - textBounds.width() / 2, // text will be centered horizontally
                markerHeight - markerHeight / 2 - textBounds.height()); // this aligns it in top 1/3 of our marker
        textPaint.setARGB(255, 0, 0, 0);

        // Now lets paint our marker image & text
        Bitmap bmp = Bitmap.createBitmap(markerWidth, markerHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mapmarker), 0, 0, markerPaint);
        canvas.drawText(q.getFormattedDistance(), textBounds.left,
                textBounds.bottom - textMargin, textPaint);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bmp);
        // Cache our marker image so we don't have to create new ones that are the same.
        mMarkerImageContainer.put(q.getFormattedDistance(), icon);
        return icon;
    }

    /**
     * Returns the default zoom level for the device size. Different screen
     * sizes require different default zoom levels.
     *
     * @return The default zoom level to show the whole NZ map, for the current
     * device.
     */
    private int getDefaultZoomForDevice() {
        // TODO Broken with new maps.
        int defaultZoom = 5;
        View v = getView();
        if (v != null) {
            int width = v.getWidth();

            if (width < 480)
                defaultZoom = 5;
            else if (width < 720)
                defaultZoom = 6;
            else
                defaultZoom = 7;
        }
        return defaultZoom;
    }

    public void highlightGarageSale(GarageSale q) {
        removeAllMarkers();
        addGarageSaleMarker(getMap(), q);
    }

    private void removeAllMarkers() {
        for(Marker marker : mMarkers) {
            marker.remove();
            mMarkerIdToGarageSale.remove(marker.getId());
        }
        mMarkers.clear();
    }

    public void clearHighlight() {
        removeAllMarkers();
        updateOverlayItems();
    }

    public void setOnGarageSaleTapListener(GarageSaleTapListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mGarageSale.size() > 1) {
            GarageSale garagesale = mMarkerIdToGarageSale.get(marker.getId());
            if(garagesale != null && mListener != null) {
                mListener.onGarageSaleTap(garagesale);
                return true;
            }
        }
        return false;
    }
}
