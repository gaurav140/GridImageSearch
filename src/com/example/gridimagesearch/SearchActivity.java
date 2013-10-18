package com.example.gridimagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class SearchActivity extends Activity implements OnScrollListener{
	
	//protected static final int REQUEST_CODE = 0;
	EditText etQuery;
	Button btnSearch;
	GridView gvResults;
	int start = 0;
	String size = "icon";
    String type = "face";
    boolean isLoading = false;
    int totalItemCount = 0;
    int firstVisibleItem = 0;
    int visibleItemCount = 0;
    
    ArrayList<ImageResult>imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				Intent i = new Intent(getApplicationContext(),ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("url", imageResult.getFullUrl());
				startActivity(i);
			}
			
		});
		gvResults.setOnScrollListener(this);
	}

		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	
	public void setupViews() {
		// TODO Auto-generated method stub
		etQuery = (EditText) findViewById(R.id.etQuery);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		gvResults = (GridView) findViewById(R.id.gvResults);
		
					
	}
	
	public void setSettings(MenuItem mi){
		Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
		startActivityForResult(i, 100);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(requestCode == 100) {
                if(resultCode==RESULT_OK) {
                        size = result.getExtras().getString("size");
                        type = result.getExtras().getString("type");
                        String color = result.getExtras().getString("Color");
                        String filter = result.getExtras().getString("filter");
                        Toast.makeText(this, result.getExtras().getString("filter"), Toast.LENGTH_SHORT).show();
                }
        }
}
		
	public void onImageSearch(View v) {
		String query = etQuery.getText().toString();
		Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
		start = 0;
		AsyncHttpClient client = new AsyncHttpClient();
		//https://ajax.googleapis.com/ajax/services/search/images?
		client.get("https://ajax.googleapis.com/ajax/services/search/images?rsz=8&" + 
				   "start=" + start + "&v=1.0&q=" + Uri.encode(query)+"&imgsz="+size+"imgtype="+type, new JsonHttpResponseHandler(){
			
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults = null;
				try{
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageResults.clear();
					imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					Log.d("DEBUG", imageResults.toString());
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		//gvResults.setOnScrollListener(this);
	}
	
	public void loadMore (View v){
		isLoading = true;
	    String query = etQuery.getText().toString();
	    start = start+8;
	    String api = "http://ajax.googleapis.com/ajax/services/search/images?rsz=8&" +
	                    "start=" + start + "&v=1.0&q=" + Uri.encode(query) + "&imgsz=" + size + "&imgtype=" + type;
	    search(api);
	}
		
	
	private void search(String api) {
        isLoading = true;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(api, new JsonHttpResponseHandler(){
			
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults = null;
				try{
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					//imageResults.clear();
					imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					Log.d("DEBUG", imageResults.toString());
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			public void onFailure(Throwable e, JSONObject errorResponse) {
                Log.d("DEBUG", "5");
	        }
	        public void onFailure(Throwable e, JSONArray errorResponse) {
	                Log.d("DEBUG", "6");
	        }
		});
		isLoading = false;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
	    Log.d("DEBUG", "firstVisibleItem:"+firstVisibleItem+"visibleItemCount:"+visibleItemCount+"totalItemCount:"+totalItemCount);
	    //loadMore(view);
	    if(isLoading && totalItemCount==0)
	            return;
	    String query = etQuery.getText().toString();
	    start = totalItemCount;
	    String api = "http://ajax.googleapis.com/ajax/services/search/images?rsz=8&" +
	                    "start="+start+"&v=1.0&q="+Uri.encode(query)+"&imgsz="+size+"&imgtype="+type;
	    Log.d("DEBUG", api);
	    
	
	    if(firstVisibleItem==0 && totalItemCount==visibleItemCount)
	            search(api);
	    else if((totalItemCount - visibleItemCount) <= (firstVisibleItem + 8))
	            search(api);
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d("DEBUG", "onScrollStateChanged");
//		String query = etQuery.getText().toString();
//	    start = totalItemCount;
//	    String api = "http://ajax.googleapis.com/ajax/services/search/images?rsz=8&" +
//	                    "start="+start+"&v=1.0&q="+Uri.encode(query)+"&imgsz="+size+"&imgtype="+type;
//	    Log.d("DEBUG", api);
//	    
//	
//	    if(firstVisibleItem==0 && totalItemCount==visibleItemCount)
//	            search(api);
//	    else if((totalItemCount - visibleItemCount) <= (firstVisibleItem + 8))
//	            search(api);
		    	
	}
	
	
	
}
