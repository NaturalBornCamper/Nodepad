package com.naturalborncamper.android.nodepad.data;

import android.util.Log;

/**
 * Created by Marco on 2017-12-15.
 */
public class Node {

    public String mRawString;
    public String mDisplayString;
    public int level;
    public int mParentLine;
    public int mLine;
    public boolean mIsVisible = false;
    public boolean mExpanded = false;

    public Node(int parentLine, int line, String rawString) {
        mParentLine = parentLine;
        mLine = line;

        try {
            // Trim trailing spaces
            mRawString = rawString.replaceFirst("\\s++$", "");

            // Trim leading spaces for a clean display
            mDisplayString = mRawString.trim();

            int rawLength = mRawString.length();

            // Check if first character is a dash (level 1)
            if (rawLength <= 0){
                level = -1;
            }
            else if (mRawString.charAt(0) == '-'){
                level = 1;
                // Check if first character is a space (level 2+)
            }
            else if (mRawString.charAt(0) == ' '){
                level = mRawString.indexOf(mDisplayString) + 1;
                // Check if ends with colon and all caps (top level, 0)
            }
            else if (mRawString.charAt(mRawString.length() - 1) == ':') {
                level = 0;
                mParentLine = mLine;
                mIsVisible = true;
    //            try {
                    for (int i = mRawString.length()-1; i > 0; i--) {
                        if (Character.isLowerCase(mRawString.charAt(i))) {
                            level = -1;
                            mParentLine = -1;
                            mIsVisible = false;
                            break;
                        }
                    }
    //            } catch (Exception e) {
    //                Log.d("bob", "Exception in loop: " + e.getMessage());
    //                e.printStackTrace();
    //            }
            }
            else level = -1;

            Log.d("level", String.valueOf(level));
            if (level >= 0){
                if (FileLoader.mLevelParentLines.size() <= level)
                    FileLoader.mLevelParentLines.add(mLine);
                else if (FileLoader.mLevelParentLines.get(level) != mLine){
                    FileLoader.mLevelParentLines.set(level, mLine);
                }
            }
            if (level > 0)
                mParentLine = FileLoader.mLevelParentLines.get(level-1);
            else
                mParentLine = -1;

            FileLoader.setLastLevel(level);
        } catch (Exception e) {
            Log.d("bob", "Node creation exception: " + e.getMessage());
            e.printStackTrace();
        }

//        Log.d("bob", "Node level: " + String.valueOf(level));
//        Log.d("bob", "Node constructor end");
    }

    @Override
    public String toString() {
        return "(Level " + level + ") " + mDisplayString;
    }
}
