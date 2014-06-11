package nz.co.gsf.activities;

import java.util.ArrayList;

import nz.co.gsf.activities.AboutActivity;
import nz.co.gsf.activities.GSPreferenceActivity;
import nz.co.gsf.preferences.DefaultPrefs;
import nz.co.gsf.utilapi.GarageSaleApi;
import nz.co.gsf.R;
import nz.co.gsf.fragments.GarageSaleListFragment;
import nz.co.gsf.fragments.NZMapFragment;
import nz.co.gsf.garagesale.GarageSale;
import nz.co.gsf.garagesale.GarageSaleArrayAdapter;
import nz.co.gsf.garagesale.GarageSaleFilter;
import nz.co.gsf.garagesale.GarageSaleTapListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;

/**
 *
 */
public class MainActivity extends ActionBarActivity implements 
		OnSharedPreferenceChangeListener, ActionBar.TabListener, GarageSaleTapListener,
		LoaderManager.LoaderCallbacks<ArrayList<GarageSale>>{

	
	private NZMapFragment mMapFragment;
	private GarageSaleListFragment mListFragment;
	private SearchView search;

	private boolean mPreferencesUpdated;

	private int mMaxDistance, mMaxNumberOfGarageSales;
	
	GarageSaleArrayAdapter adapter;
    
	final String tag = "GSF:Main";

   
    /**
     * True if the system is currently downloading garage sales in the background.
     */
    private boolean mDownloading = false;

    /**
     * Holds the Refresh menu item, used to set visibility when processing.
     */
    private MenuItem mRefreshMenuItem;

    /**
     * A list of the latest {@link GarageSale}s downloaded from Garage-Sale.co.nz.
     */
    private ArrayList<GarageSale> mGarageSales;

    /**
     * The currently selected tab.
     */
    private String mSelectedTab;

  
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//LoaderManager.enableDebugLogging(true);
		
		// Set defaults before we do anything else.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Log.d("XXX", "OnCreate");
        // Request Feature must be called before adding content.
        // Note this turns it on by default (so only on 2.x devices).
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		search = (SearchView) findViewById(R.id.searchView);
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				 
			     updateGarageSalesDisplay();
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				 
			     updateGarageSalesDisplay();
				return true;
			}
		});
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.tab_title_list))
					.setTabListener(this));
			actionBar.addTab(actionBar.newTab()
					.setText(getString(R.string.tab_title_map))
					.setTabListener(this));
			
			mListFragment = new GarageSaleListFragment();
			mMapFragment  = new NZMapFragment();
	
			
			
			mListFragment.setOnGarageSaleTapListener(this);
	        mMapFragment.setOnGarageSaleTapListener(this);

	        // First start-up
	        if (null == savedInstanceState) {
	             // Set this to true, so that garage sales are downloaded and preference items
	             // are updated in the onResume call.
	            mPreferencesUpdated = true;
	        }
	        // App was killed by the OS
	        else {
	            mDownloading = savedInstanceState.getBoolean("mDownloading");
	            mGarageSales = savedInstanceState.getParcelableArrayList("mGarageSales");
	            if (mDownloading || mGarageSales == null) {
	                mPreferencesUpdated = true;
	            } else {
	                hideProgress();
	            }
	            
	        }
	       
	        
	        updateItemsFromPreferences();
	        updateGarageSalesDisplay();
	}

	 @Override
	 protected void onSaveInstanceState(Bundle outState) {
	     super.onSaveInstanceState(outState);
	     Log.d("XXX", "onSaveInstanceState");
	     outState.putString("mSelectedTab", mSelectedTab);
	     outState.putParcelableArrayList("mGarageSales", mGarageSales);
	     outState.putBoolean("mDownloading", mDownloading);
	 }
	 

	 @Override
	 protected void onResume() {
	     super.onResume();
	     Log.d("XXX", "onResume");
	     PreferenceManager.getDefaultSharedPreferences(this)
	            .registerOnSharedPreferenceChangeListener(this);
	     if (mPreferencesUpdated) {
	         mPreferencesUpdated = false;
	         updateItemsFromPreferences();
	         downloadGarageSales();
	         
		        
	     }
	     
	 }

	 private void updateItemsFromPreferences() {
	     Log.d("XXX", "updateItemsFromPreferences");

	      SharedPreferences prefs = PreferenceManager
	              .getDefaultSharedPreferences(this);
	      mMaxNumberOfGarageSales = prefs.getInt(
	              GSPreferenceActivity.KEY_PREF_NUM_GARAGESALES_TO_SHOW, DefaultPrefs.NUM_GARAGESALES_TO_DISPLAY);
	      mMaxDistance = prefs.getInt(GSPreferenceActivity.KEY_PREF_MAX_DISPLAY_DISTANCE,
	              DefaultPrefs.MAX_DISPLAY_DISTANCE);
	 }
	    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	     Log.d("XXX", "onCreateOptionsMenu");

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		 mRefreshMenuItem = menu.findItem(R.id.menu_refresh);
	     if(mDownloading) mRefreshMenuItem.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
	     Log.d("XXX", "onOptionsItemSelected");

		if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
		} else if (item.getItemId() == R.id.menu_preferences)
        {
            startActivity(new Intent(this, GSPreferenceActivity.class));
        } else if (item.getItemId() == R.id.menu_refresh) {
            downloadGarageSales();
        }
    
	
		return super.onOptionsItemSelected(item);
	}
	
	 @Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	                                          String key) {
	     Log.d("XXX", "onSharedPreferenceChanged");

	        if (key.equals(GSPreferenceActivity.KEY_PREF_MAX_DISPLAY_DISTANCE)
	                || key.equals(GSPreferenceActivity.KEY_PREF_MAX_HIGHLIGHT_DISTANCE)
	                || key.equals(GSPreferenceActivity.KEY_PREF_NUM_GARAGESALES_TO_SHOW)) {
	            mPreferencesUpdated = true;
	        }
	        updateItemsFromPreferences();
	        updateGarageSalesDisplay();	    }

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
	     Log.d("XXX", "onTabSelected");

		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
		     Log.d("XXX", "Fragment getItem");

			switch (position) {
			case 0:
				return mListFragment;
			case 1:
				return mMapFragment;
			default:
				return mListFragment;
			}			
			
		}
		

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		
	}
	
	  /**
     * Called to update the garagesales display. Will update whichever view is
     * selected at the moment. If you have new garagesales to display, be sure to set
     * {@link #mgaragesales} first, then call this method.
     */
    private void updateGarageSalesDisplay() {
	     Log.d("XXX", "updateGarageSalesDisplay ");

    	updateGarageSalesList();
        updateGarageSalesMap();
        
    }
    
    /**
     * Updates the map view. Please use {@link #updategaragesalesDisplay} instead.
     */
    private void updateGarageSalesMap() {
	     Log.d("XXX", "updateGarageSalesMap ");

    	if (null == mGarageSales || null == mMapFragment)
            return;
        mMapFragment.setGarageSales(GarageSaleFilter.filterGarageSales(mGarageSales, ((float) mMaxDistance) , mMaxNumberOfGarageSales,search.getQuery()));
    }

    /**
     * Updates the list view. Please use {@link #updategaragesalesDisplay} instead.
     */
    private void updateGarageSalesList() {
	     Log.d("XXX", "updateGarageSalesList ");

    	if (null == mGarageSales || null == mListFragment)
            return;
        ArrayList<GarageSale> filteredList = GarageSaleFilter.filterGarageSales(mGarageSales, ((float) mMaxDistance), mMaxNumberOfGarageSales,search.getQuery());
       
        adapter = new GarageSaleArrayAdapter(this
                , R.layout.row, filteredList);
         mListFragment.setListAdapter(adapter);
         
         TextView gsCount = (TextView) this.findViewById(R.id.gscount);
         if (filteredList.size() == 0) {
             gsCount.setText("No \n sales");
         } else {
             gsCount.setText(filteredList.size() + "\n sales");
         }
    }
    
    private void downloadGarageSales() {
	     Log.d("XXX", "downloadGarageSales ");

    	mDownloading = true;
        showProgress();
        
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void hideProgress() {
	     Log.d("XXX", "hideProgress ");

    	if (null != mRefreshMenuItem)
            mRefreshMenuItem.setVisible(true);
    	//setProgress(Window.PROGRESS_END);
        setProgressBarIndeterminateVisibility(false);
        
    }

    private void showProgress() {
	     Log.d("XXX", "showProgress ");

    	if (null != mRefreshMenuItem)
            mRefreshMenuItem.setVisible(false);
        
        setProgressBarIndeterminateVisibility(true);
        
    }
    
    @Override
    public void onGarageSaleTap(GarageSale garagesale) {
	     Log.d("XXX", "onGarageSaleTap ");

    	if (garagesale == null) return;
            Intent intent = new Intent(this, GarageSaleActivity.class);
            intent.putExtra(GarageSaleActivity.GARAGESALE_KEY, garagesale);
            startActivity(intent);
      
    }
    
    @Override
    public Loader<ArrayList<GarageSale>> onCreateLoader(int i, Bundle bundle) {
	     Log.d("XXX", "OnCreateLoader ");

    	AsyncTaskLoader<ArrayList<GarageSale>> loader = new AsyncTaskLoader<ArrayList<GarageSale>>(this) {
            @Override
            public ArrayList<GarageSale> loadInBackground() {
            	return GarageSaleApi.getGarageSalesFromServer(this.getContext());	
            }
        };
        loader.forceLoad();
        return loader;
    }
    
    @Override
    public void onLoadFinished(Loader<ArrayList<GarageSale>> objectLoader, ArrayList<GarageSale> results) {
	     Log.d("XXX", "onLoadFinished ");
	     
    	//mDownloading = false;
        //getSupportLoaderManager().destroyLoader(0); 
        hideProgress();
        AlertDialog.Builder builderNoConnection = new AlertDialog.Builder(MainActivity.this);
        builderNoConnection.setTitle("No Connection")
        .setMessage(
                "There appears to be a problem "
                        + "with the connection. Last downloaded "
                        + "data will be used.")
        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ArrayList<GarageSale> resultsLocal;
				
				resultsLocal=GarageSaleApi.getGarageSalesFromFile(getApplicationContext());
				
				AlertDialog.Builder builderNoFile = new AlertDialog.Builder(MainActivity.this);
				builderNoFile.setTitle("No Stored Data")
							.setMessage(
											"There are no Garage Sales "
											+ "Stored locally. Connect to "
											+ "Internet to download from server.")
							.setNeutralButton("Close", null);
				
				 	if (null == resultsLocal || (resultsLocal.size() == 0)) {
		                    AlertDialog dialogNoFile = builderNoFile.create();
							dialogNoFile.show();
		                    resultsLocal = new ArrayList<GarageSale>();
		            }
		            
		            mGarageSales = resultsLocal;
		            updateGarageSalesDisplay();
			}  
			});
        AlertDialog dialogNoConnection = builderNoConnection.create();
        if (null == results) {
            
        	dialogNoConnection.show();
        }
       
        mGarageSales = results;
        updateGarageSalesDisplay();
    }
    
    @Override
    public void onLoaderReset(Loader<ArrayList<GarageSale>> objectLoader) {
	     Log.d("XXX", "onLoaderReset ");

    }

	@Override
	public void onGarageSaleLostFocus(GarageSale GarageSaleItem) {
		// TODO Auto-generated method stub
	     Log.d("XXX", "onGarageSaleLostFocus ");

	}



}
