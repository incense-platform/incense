package edu.incense.android.ui;

import java.util.List;


import edu.incense.android.R;
import edu.incense.android.results.JsonResults;
import edu.incense.android.results.ResultFile;
import edu.incense.android.results.ResultsUploader;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsListActivity extends ListActivity {
    private List<ResultFile> fileList;

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.results);

        ResultsUploader resultsUploader = new ResultsUploader(this);
        fileList = resultsUploader.getQueueList();

        setListAdapter(new ArrayAdapter<ResultFile>(this,
                R.layout.results_item, fileList));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                showDialogForItem(position);
            }
        });
    }

    private void showDialogForItem(int position) {
        ResultFile resultFile = fileList.get(position);
        String content = getFileContent(resultFile.getFileName());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content)
                .setTitle(resultFile.getFileType().toString())
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getFileContent(String fileName) {
        JsonResults jsonResults = new JsonResults();
        return jsonResults.toString(fileName);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /*** MENU ***/

    // This inflates/populates your menu resource (convert the XML resource into
    // a programmable object)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    // This method passes the MenuItem that the user selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {
        case R.id.new_recording:
            Intent mainIntent = new Intent(this, RecordActivity.class);
            startActivity(mainIntent);
            return true;
        case R.id.settings:
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            // startActivityForResult(settingsIntent, SHOW_PREFERENCES);
            return true;
        case R.id.results:
            Intent resultsIntent = new Intent(this, ResultsListActivity.class);
            startActivity(resultsIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
