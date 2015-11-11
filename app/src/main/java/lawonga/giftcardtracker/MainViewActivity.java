package lawonga.giftcardtracker;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toolbar;

import com.parse.ParseUser;

public class MainViewActivity extends AppCompatActivity {
    // Register things
    private String[] mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout centerLayout;
    private FloatingActionButton fab;
    public static SwipeRefreshLayout swipeRefreshLayout;
    // Register global network connectivity
    public static boolean networkStatus, initialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Class", "Create");
        // Check network status
        networkStatus = isNetworkConnected();

        // Initialization
        setContentView(R.layout.activity_main);
        mTitle = getResources().getStringArray(R.array.titles);
        centerLayout = (FrameLayout)findViewById(R.id.container);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.main_swipeContainer);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        // Set the Adapter for list View
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mTitle));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setItemChecked(0, true);

        // Nav drawer code
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_drawer);
        mDrawerToggle.syncState();

        // Floating action button, add card action
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
                CardListCreator.notifychangeddata();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Class", "Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Class", "Resume");
        Log.e("Boolean", String.valueOf(initialized));
        // This code CREATES the entire list with CHECKS on network state
        final CardListCreator cardListCreatorFragment = new CardListCreator();
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, cardListCreatorFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container));
        }
        // Implement swipe to refresh
        swipeToRefresh(cardListCreatorFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        // Hamburger menu on the drawer
        mDrawerToggle.syncState();
    }

    // After if settings menu came back after logging out
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                finish();
            }
        }
    }

    // action_add = create the add card fragment view
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){
            showEditDialog();
            CardListCreator.notifychangeddata();
        }
        if (id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1);
        }
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    // SHOW DIALOG from NEWCARD
    public void showEditDialog(){
        FragmentManager fm = getFragmentManager();
        NewCardFragment newcard = new NewCardFragment();
        newcard.show(fm, "new_card");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // Click listener for the drawer
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        // Swaps fragments in the main content view
        private void selectItem(int position){
            // Create a new fragment and specify what view to show
            Fragment fragment = new grabCard();
            // args required to set the title and fragment; send position clicked
            Bundle args = new Bundle();
            args.putInt(grabCard.ARG_FRAG_NO, position);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            setTitle(mTitle[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }
    public class grabCard extends android.app.Fragment{
        public static final String ARG_FRAG_NO = "frag_no";

        public grabCard() {
            //EMPTY AS REQUIRED
        }

        // If 0, access DataBase parse class. If 1, access Archive parse class. If 2, remove & access settings
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.reuseable_fragment_layout, container, false);
            int i = getArguments().getInt(ARG_FRAG_NO);
            String title = getResources().getStringArray(R.array.titles)[i];
            getActivity().setTitle(title);
            if (i==0) {
                LogonActivity.currentcard = 0;
            } else if (i==1){
                LogonActivity.currentcard = 1;
            }
            CardListCreator.clearadapter();
            final CardListCreator cardListCreatorFragment = new CardListCreator();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, cardListCreatorFragment).commit();
            swipeToRefresh(cardListCreatorFragment);
            return rootView;
        }
    }

    // What happens when back button is pressed: open a new exit dialog fragment
    @Override
    public void onBackPressed() {
        DialogFragment exitDialog = new ExitDialogFragment();
        exitDialog.show(getFragmentManager(), "Exit_Dialog");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Class", "Restart");
    }

    // On pause
    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Class", "Stop");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // CardListCreator.clearadapter();
        // getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
        Log.e("Class", "Pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Class", "Destroy");
    }

    // Network state check
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    // Swipe to refresh
    public void swipeToRefresh(final CardListCreator cardListCreatorFragment){
        // Code for swipe to top to refresh
        centerLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeRefreshLayout.setEnabled(false);
                try {
                    if (cardListCreatorFragment.getListView().getFirstVisiblePosition() == 0 && cardListCreatorFragment.getListView().getChildAt(0).getTop() == 0) {
                        swipeRefreshLayout.setEnabled(true);
                        // Swipe to refresh code
                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                networkStatus = isNetworkConnected();
                                CardListCreator.clearadapter();
                                CardListAdapter.queryList();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                } catch (Exception ignored) {
                }

            }
        });
    }
}
