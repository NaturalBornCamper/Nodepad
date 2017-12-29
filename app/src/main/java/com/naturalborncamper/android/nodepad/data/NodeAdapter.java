package com.naturalborncamper.android.nodepad.data;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naturalborncamper.android.nodepad.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 2017-12-15.
 */

//public class NodeAdapter extends RecyclerView.Adapter {
public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.NodeAdapterViewHolder> {

    List<Node> mFileLines;

    public static void setLinesToDisplay(List<Integer> linesToDisplay) {
        mLinesToDisplay = linesToDisplay;
    }

    static List<Integer> mLinesToDisplay;
    Map<Integer, String> visibleItems = new LinkedHashMap();
    /*
    node array has "visible" for each node
    OR node array has position in adapter array, -1 if not visible


    */
    private final NodeAdapterOnClickHandler mClickHandler;

    int mItem;

    public interface NodeAdapterOnClickHandler {
        void onClick(Node node);
    }

    public NodeAdapter(NodeAdapterOnClickHandler clickHandler, int item) {
        mItem = item;
        mClickHandler = clickHandler;
    }


    @Override
    public NodeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.d("bob4", "onCreateViewHolder");
        View v = LayoutInflater.from(parent.getContext()).inflate(mItem, parent, false);
        return new NodeAdapterViewHolder(v);
    }


    @Override
    public void onBindViewHolder(NodeAdapterViewHolder holder, int position) {
//        Log.d("bob", "bind position #" + position);
//        Log.d("bob", "bind line #" + mLinesToDisplay.get(position));
//        Log.d("bob", "bind Node " + mFileLines.get(mLinesToDisplay.get(position)));
        holder.bind(mLinesToDisplay.get(position));
//        holder.bind(visibleItems);
    }

    @Override
    public int getItemCount() {
        if (mLinesToDisplay == null) {
//            Log.d("bob", "size marche po");
            return 0;
        }

        return mLinesToDisplay.size();
    }

    public void setData(List<Node> data) {
//        int index = 0;
//        for (Node item: data) {
//            if (item.show)
//                visibleItems.put(index, item.mDisplayString);
//            ++index;
//        }
        mFileLines = data;
        notifyDataSetChanged();
    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public class NodeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mNodeTextView;
        public final TextView mNodeCollapseTextView;

        public NodeAdapterViewHolder(View view) {
            super(view);
//            Log.d("bob4", "NodeAdapterViewHolder");
            mNodeTextView = view.findViewById(R.id.tv_node);
            mNodeCollapseTextView = view.findViewById(R.id.tv_node_collapse);
            view.setOnClickListener(this);
        }

        public void bind(int lineNumber) {
            Node node = null;
            try {
                node = mFileLines.get(lineNumber);
            } catch (Exception e) {
                Log.d("bob", e.getMessage());
                e.printStackTrace();
            }
            mNodeTextView.setText(node.mDisplayString);
            mNodeCollapseTextView.setText(node.mExpanded ? "-" : "+");
//            Log.d("bob", "line first: " + lineNumber);
//            Log.d("bob", "line second: " + node.mLine);
            mNodeTextView.setTag(R.string.line, lineNumber);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Node clickedNode = mFileLines.get(mLinesToDisplay.get(adapterPosition));
            int parentLevel = clickedNode.level;
//            int targetLevel = clickedNode.level+1;
//            Log.d("bob", "Adapter position: " + adapterPosition);
//            Log.d("bob", "Clicked node line: " + String.valueOf(mNodeTextView.getTag(R.string.line)));
//            Log.d("bob", "Clicked node: " + mFileLines.get((int) mNodeTextView.getTag(R.string.line)));
//            Log.d("bob", "Clicked node line with get: " + mFileLines.get((int) mNodeTextView.getTag(R.string.line)).mLine);

            // Set expanded or not
            if (clickedNode.mExpanded) {
                mNodeCollapseTextView.setText("+");
            } else {
                mNodeCollapseTextView.setText("-");
            }
            clickedNode.mExpanded = !clickedNode.mExpanded;


//            if (clickedNode.mExpanded) {
            if (true) {
                for (int i = clickedNode.mLine + 1; i < mFileLines.size(); ++i) {
                    Node currentNode = mFileLines.get(i);

//                    Log.d("bob", "Current node Line:" + currentNode.mLine);
//                    Log.d("bob", "Current node:" + currentNode);
//                    Log.d("bob", "Current node Level:" + currentNode.level);
//                    Log.d("bob", "Clicked node Level:" + clickedNode.level);

                    // Ignore this node, level = -1 so it's an invalid node that failed parsing
                    if (currentNode.level < 0) continue;

                    // XXXXXXXXXXXXBreak the loop, we're done once we get to a node of the same level or below. Also makes sure it's not an invalid node (level = -1)
                    // We got to a node with a higher hierarchy level or same level that we clicked, stop everything
                    if (currentNode.level <= clickedNode.level) break;
                    if (currentNode.level - 1 <= parentLevel) {
//                    if (currentNode.level - 1 == parentLevel) {
//                    currentNode.mIsVisible = true;

//                        Log.d("bob", "mLinesToDisplay: " + mLinesToDisplay);
//                        Log.d("bob", "mLinesToDisplay: " + mLinesToDisplay);
                        if (clickedNode.mExpanded) {
                            mLinesToDisplay.add(++adapterPosition, currentNode.mLine);
                            notifyItemInserted(adapterPosition);
                        } else {
                            mLinesToDisplay.remove(adapterPosition + 1);
                            notifyItemRemoved(adapterPosition + 1);
                        }
//                        Log.d("bob", "Expand to display line #" + (currentNode.mLine));
//                        Log.d("bob", "Expand to display node: " + currentNode);

                        if (currentNode.mExpanded)
                            ++parentLevel;
                    }
//                else if (currentNode.level >= parentLevel){
//                    mLinesToDisplay.add(++adapterPosition, currentNode.mLine);
//                    Log.d("bob", "mLinesToDisplay: " + mLinesToDisplay);
//                    notifyItemInserted(adapterPosition);
//                }
                    if (currentNode.level < parentLevel)
                        parentLevel = currentNode.level;
                    // Else if already visible child of child node (Maybe not be better way to do this, instead increase the parent level
//                else if (currentNode.mIsVisible) {
//                    ++adapterPosition;
//                    mLinesToDisplay.add(adapterPosition, currentNode.mLine+1);
//                    notifyItemInserted(adapterPosition);
//                    Log.d("bob", "Automatically display line #" + (currentNode.mLine+1));
//                    Log.d("bob", "Automatically display node: " + currentNode);
//                }
                }

            } else {
                /*
                solutions:
                -chercher dans visibleArray si le numéro de ligne de la y est, si oui scrap
                 ma propre loop p-e meilleure
                -Setter isVisible en haut, puis filtrer les isVisible seulement
                 probleme si yen a qui on isVisible mais le sont pas vraiment... c'est possble?
                -Refaire le même cheminement que en haut pour tout retirer
                 permet peut-être de faire seulement un loop avec uniquement "add->remove" et "notifyAdded->notifyRemoved"
                 */
/*
                ++adapterPosition;
                for (int i = clickedNode.mLine + 1; i < mFileLines.size(); ++i) {
                    Node currentNode = mFileLines.get(i);

                    Log.d("bob", "Current node Line:" + currentNode.mLine);
                    Log.d("bob", "Current node:" + currentNode);
                    Log.d("bob", "Current node Level:" + currentNode.level);
                    Log.d("bob", "Clicked node Level:" + clickedNode.level);

                    // Ignore this node, level = -1 so it's an invalid node that failed parsing
                    if (currentNode.level < 0) continue;

                    // XXXXXXXXXXXXBreak the loop, we're done once we get to a node of the same level or below. Also makes sure it's not an invalid node (level = -1)
                    // We got to a node with a higher hierarchy level or same level that we clicked, stop everything
                    if (currentNode.level <= clickedNode.level) break;

                    // hide child nodes
                    Log.d("bob", "remove position: " + adapterPosition);
                    try {
                        mLinesToDisplay.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    } catch (Exception e) {
                        Log.d("bob", "Exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
*/


            }

//            mFileLines.add(adapterPosition, new Node("BOB:", 0));
//            notifyItemInserted(adapterPosition);
//            mClickHandler.onClick(node);
        }
    }
}

// TODO Prefs: Default view (word-wrap, multi-line), file list, 
// TODO Save in Bundle which file was opened
// TODO Save in bundle all the nodes of opened file (possible?)
// TODO refresh file button
// TODO Add preference for file locations
// TODO add/remove files from list
// TODO put list of files in left pane
// TODO open file of left pane when clicking
// TODO Add indentation on different levels with lines to better see hierarchy
// TODO Place to see parsing errors (-1 nodes), so it can be corrected (In console at least)
// TODO Add images for +/- buttons
// TODO don't show +/- button if no child, somehow..
// TODO Option "word wrap" pour afficher tout sur une ligne ou po (facile d'accès, pas dans une section séparée options... setté par document, pas global?)
// TODO Check if still need isVisible for when re-launching the app
// TODO Rename level to hierarchy or a synonym since level could be confusing?
// TODO Long press node: "add inside", "add after", "add before"
// TODO If possible: Add "targetLevel" to avoid doing parentLevel+1 all the time
// TODO NOTE: if a line skips a level (straight from CAPITAL to a space), they will never show... ok since bad formatting or should fix?
// TODO Add line
// TODO Save file (Is it possible on dropbox?) Or just locally (Save in different file and compare)
// TODO Save button
// TODO Remember opened files expanded/collapsed
// TODO Expand all
// TODO Collapse all
// TODO Rename node (edit text)
// TODO Re-open last opened file
// TODO Find way to save userdata (shared prefs or db?)
// TODO When opening file, check if modif date is newer than when opened in app, offer to refresh
// TODO Add loading animation when parsing/loading and hide RecyclerView
// TODO If only one sub-node, show it always? Maybe a pref
// TODO Option to view raw in case need to correct stuff, especially parsing errors
// TODO Copy/cut and paste nodes instead of drag, to be able to move nodes around and in different files
// ! TODO Drag and drop nodes in same level of hierarchy
// ! TODO Drag and drop nodes in a different level
// ! TODO Drag and drop nodes in different files