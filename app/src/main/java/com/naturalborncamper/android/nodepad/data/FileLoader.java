package com.naturalborncamper.android.nodepad.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.naturalborncamper.android.nodepad.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 2017-12-12.
 */

public class FileLoader extends AsyncTaskLoader<List<Node>> {

    // https://developer.android.com/reference/java/util/List.html
    public List<Node> mFileLines = new ArrayList<>();

    private static int mLastLevel = 0;
    private String mFileUrl = "";
    public static List<Integer> mLevelParentLines = new ArrayList<>();

    public FileLoader(Context context) {
        super(context);
    }

    public FileLoader(Context context, String fileUrl) {
        super(context);
        mFileUrl = fileUrl;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

//            mLoadingIndicator.setVisibility(View.VISIBLE);
//            if (mCachedWeatherData != null)
//                deliverResult(mCachedWeatherData);
//            else
                forceLoad();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Node> loadInBackground() {

        URL url = null;
        BufferedReader in = null;
        List<Integer> linesToDisplay = new ArrayList();
        try {
//            url = new URL("https://www.dropbox.com/s/4ewwm8cx1yh2a6j/_Internet.txt?dl=1");
            url = new URL(MainActivity.CURRENT_FILE_URL);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            in = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String inputLine;
            int currrentLevel = 0;


//            if (!in.ready()) {
//                Log.d("bob", "Not ready before loop");
//            }
            Node newNode;
            int currentLine = 0;
            int currentParentLine = 0;
            setLastLevel(0);
            while ((inputLine = in.readLine()) != null) {
//                if (!in.ready()) {
//                    Log.d("bob", "Not ready inside loop");
//                }
                newNode = new Node(currentParentLine, currentLine, inputLine);
                if (newNode.mIsVisible)
                    linesToDisplay.add(newNode.mLine);
                currrentLevel = newNode.level;
                mFileLines.add(newNode);
                ++currentLine;
            }
        } catch (MalformedURLException e) {
            Log.d("bob", "url malformed");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("bob", "IO Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                Log.d("bob", "IO Exception when closing: " + e.getMessage());
                e.printStackTrace();
            }
        }

        NodeAdapter.setLinesToDisplay(linesToDisplay);
        return mFileLines;

    }

    public void setFileUrl(String fileUrl){
        mFileUrl = fileUrl;
    }

    public static int getLastLevel() {
        return mLastLevel;
    }

    public static void setLastLevel(int lastLevel) {
        mLastLevel = lastLevel;
    }
}
