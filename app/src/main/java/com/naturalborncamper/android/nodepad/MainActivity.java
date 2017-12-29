package com.naturalborncamper.android.nodepad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.URLUtil;

import com.naturalborncamper.android.nodepad.data.FileLoader;
import com.naturalborncamper.android.nodepad.data.Node;
import com.naturalborncamper.android.nodepad.data.NodeAdapter;

import java.util.HashMap;
import java.util.List;

/*
        // TODO (5) Refactor the refresh functionality to work with our AsyncTaskLoader
        if (id == R.id.action_refresh) {
            mForecastAdapter.setWeatherData(null);

            LoaderManager lm = getSupportLoaderManager();
            if (lm.getLoader(LOADER) == null)
                lm.initLoader(LOADER, null, this);
            else
                lm.restartLoader(LOADER, null, this);
            return true;
        }
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NodeAdapter.NodeAdapterOnClickHandler, LoaderManager.LoaderCallbacks<List<Node>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mNodesRecyclerView;
//    private FileLoader mParser;

    private NodeAdapter mNodeAdapter;
    private FileLoader mFileLoader;
    private HashMap<Integer, String> mFileMenuIds;
    public static String CURRENT_FILE_URL = "https://www.dropbox.com/s/cpt7zap40y69yvq/_Acheter.txt?dl=1";;
    SubMenu mFilesMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mFilesMenu = navigationView.getMenu().addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Files");
        generateFilesMenu();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        mNodesRecyclerView = (RecyclerView) findViewById(R.id.rv_nodes);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mNodesRecyclerView.setLayoutManager(layoutManager);

        mNodesRecyclerView.setHasFixedSize(false);
        mNodeAdapter = new NodeAdapter(this, R.layout.node_item);
        mNodesRecyclerView.setAdapter(mNodeAdapter);

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(5, null, this);
//        fileLoader.forceLoad();

//        mFilePreview.setText("test");
//        Toast.makeText(this, mFilePreview.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void generateFilesMenu() {
        mFilesMenu.clear();

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String[] rawFileList = prefs.getString("file_list", "").split("\\r?\\n");
            int menuId = Menu.FIRST;
            mFileMenuIds = new HashMap(rawFileList.length);
            for (String fileUrl : rawFileList) {
                fileUrl = fileUrl.trim();
                if (fileUrl.isEmpty()) continue;

                mFileMenuIds.put(menuId, fileUrl);
                mFilesMenu.add(Menu.NONE, menuId++, Menu.NONE, URLUtil.guessFileName(fileUrl, null, null)).setIcon(R.drawable.ic_menu_gallery);
            }
        } catch (Exception e) {
            Log.d("bob", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d("bob", "clicked: "+id);

        String bob;
        if ((bob = mFileMenuIds.get(id)) != null){
//            mFileLoader.setFileUrl(bob);
//            getSupportLoaderManager().restartLoader(5, null, this);
//            mFileLoader.forceLoad();
//            mFileLoader.startLoading();
            CURRENT_FILE_URL = bob;
            LoaderManager lm = getSupportLoaderManager();
            if (lm.getLoader(5) != null)
                lm.restartLoader(5, null, this);
            else
                lm.initLoader(5, null, this);
            Log.d("bob", bob);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<List<Node>> onCreateLoader(int id, Bundle args) {
//        Log.d("bob", "onCreateLoader");
        return new FileLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Node>> loader, List<Node> data) {
//        Log.d("bob", "onLoadFinished");
//        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNodeAdapter.setData(data);

    }


    @Override
    public void onLoaderReset(Loader loader) {
        Log.d("bob", "onLoaderReset");

    }

    @Override
    public void onClick(Node node) {
        Log.d("bob", node.toString());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("bob", "pref changed");
        if (key.equals("file_list"))
            generateFilesMenu();
    }
}
