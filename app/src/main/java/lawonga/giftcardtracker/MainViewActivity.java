package lawonga.giftcardtracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.parse.ParseUser;

public class MainViewActivity extends AppCompatActivity {
    // Register things
    public static boolean active = false;
    private String[] mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout centerFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization
        setContentView(R.layout.activity_main);
        mTitle = getResources().getStringArray(R.array.titles);
        centerFrame = (FrameLayout)findViewById(R.id.container);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        if (!active) getSupportFragmentManager().beginTransaction().add(R.id.container, new CardListCreator()).commit();
        active = true;

        // Set the Adapter for list View
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mTitle));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        getSupportActionBar().setTitle(mTitle[0]);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    // action_add = create the add card fragment view
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add){
            showEditDialog();
            CardListCreator.notifychangeddata();
        }
        if(id == R.id.logout){
            CardListCreator.clearadapter();
            CardListAdapter.queryList();
            ParseUser.logOut();
            this.finish();
            Intent intent = new Intent(this, LogonActivity.class);
            startActivity(intent);
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
            Fragment fragment = new posFragment();
            Bundle args = new Bundle();
            args.putInt(posFragment.ARG_FRAG_NO, position);
            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            setTitle(mTitle[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }

    }
    public static class posFragment extends android.app.Fragment{
        public static final String ARG_FRAG_NO = "frag_no";

        public posFragment() {
            //EMPTY AS REQUIRED
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.card_list, container, false);
            int i = getArguments().getInt(ARG_FRAG_NO);
            String title = getResources().getStringArray(R.array.titles)[i];
            getActivity().setTitle(title);
            return rootView;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        CardListCreator.clearadapter();
        active = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!active) getSupportFragmentManager().beginTransaction().add(R.id.container, new CardListCreator()).commit();
        active = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        active = true;
    }


}
