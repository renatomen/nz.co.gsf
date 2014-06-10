package nz.co.gsf.fragments;

import nz.co.gsf.garagesale.GarageSaleTapListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import nz.co.gsf.garagesale.*;

public class GarageSaleListFragment extends ListFragment {

    
    private GarageSaleTapListener mTapListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

  
    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        if (mTapListener != null) {
            GarageSale garagesale = (GarageSale) getListView().getItemAtPosition(position);
            mTapListener.onGarageSaleTap(garagesale);
        }
    }
    
    public void setOnGarageSaleTapListener(GarageSaleTapListener listener) {
        mTapListener = listener;
    }
}