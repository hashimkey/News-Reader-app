package project.min.school.schoolapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Abdulqani on 5/11/2017.
 */

public class News extends Fragment {


    // PLauDev implementing least recently used cache instead of passing large url content
    //   via intent to avoid crashing (failed binder transaction)
    //   crash & solution by WISHY http://stackoverflow.com/a/31133766/1827488
    //   docs https://developer.android.com/reference/android/util/LruCache.html
    //   string size in bytes http://stackoverflow.com/a/4385653/1827488
    //   intent size limit https://www.neotechsoftware.com/blog/android-intent-size-limit
    //   shared preference size limit http://stackoverflow.com/a/30638875/1827488
    static LruCache<String, String> mMemoryCache;
    static final String cacheKey = "cacheKey";
    final int kiloByte = 1024;


    //array-list of strings for titles and contents
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> content = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    SQLiteDatabase articlesDb;
    ListView listView;
    Context context; //Declare the variable context
    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_SEARCH_QUERY = "searchQuery";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1news, container, false);

        setHasOptionsMenu(true);

        // WISHY Get max available VM memory, exceeding this amount will throw an
        //   OutOfMemory exception. Stored in kilobytes as LruCache takes an
        //   int in its constructor.
        final int maxMemoryKB = (int) (Runtime.getRuntime().maxMemory() / kiloByte);
        // WISHY Use 1/8th of the available memory for this memory cache.
        final int cacheSizeKB = maxMemoryKB / 8;
        Toast.makeText(getActivity().getApplicationContext(),
                String.format("memory = %dMB, cache = %dMB",
                        maxMemoryKB / kiloByte, cacheSizeKB / kiloByte),
                Toast.LENGTH_LONG).show();

        mMemoryCache = new LruCache<String, String>(cacheSizeKB) {
            //@Override
            protected int sizeOf(String key, String value) {
                // WISHY The cache size will be measured in kilobytes rather than
                //   number of items.
                try {
                    byte[] bytesUtf8 = value.getBytes(NewsActivity.codingScheme);
                    return bytesUtf8.length / kiloByte;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        };

        ListView listView = (ListView)rootView.findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,titles);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String contentString = content.get(position);
                int contentSizeKB;
                try {
                    byte[] contentBytes = contentString.getBytes(NewsActivity.codingScheme);
                    contentSizeKB = contentBytes.length / kiloByte;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "content byte count failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (contentSizeKB > cacheSizeKB) {
                    Toast.makeText(getActivity().getApplicationContext(), "content too large", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity().getApplicationContext(),
                        String.format("page size = %dKB", contentSizeKB),
                        Toast.LENGTH_LONG).show();
                mMemoryCache.put(cacheKey, content.get(position));
                Intent intent = new Intent(getActivity().getApplicationContext(), NewsActivity.class);
                intent.putExtra("content", content.get(position));
                News.this.startActivity(intent);

            }
        });

        articlesDb = getActivity().openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        articlesDb.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, title VARCHAR, content VARCHAR)");

        updateListView();

        DownloadTask task = new DownloadTask();

        try {
            // PLauDev comment out task.execute() to prevent loading live data

             //task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return rootView;
    }


    public void updateListView()
    {
        //get data from database and display to user

        Cursor c = articlesDb.rawQuery("SELECT * FROM articles",null);

        int contentIndex = c.getColumnIndex("content");
        int titleIndex  = c.getColumnIndex("title");

        if (c.moveToFirst()) {

            titles.clear(); //if database don't return anythings clear arraylist Title
            content.clear();//if database don't return anythings clear arraylist content

            do {

                titles.add(c.getString(titleIndex)); //add current title to titles Arraylists
                content.add(c.getString(contentIndex));
            }while (c.moveToNext()); // keep going and go to next item

            arrayAdapter.notifyDataSetChanged(); // Update Array Adapter

        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            Log.i("doInBackground", params[0]);

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }


                JSONArray jsonArray = new JSONArray(result);

                int numberOfItems = Math.min(20, jsonArray.length());

                /*
                if (jsonArray.length() < numberOfItems) {
                    numberOfItems = jsonArray.length();
                }
                */

                articlesDb.execSQL("DELETE FROM articles"); //Clear articleDB before add new data because we don't want add same thing over over again

                for (int i = 0; i < numberOfItems; i++) {
                    String articleId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json?print=pretty"); //ger id for details
                    urlConnection = (HttpURLConnection) url.openConnection();
                    in = urlConnection.getInputStream();
                    reader = new InputStreamReader(in);
                    data = reader.read();
                    String articleInfo = "";
                    while (data != -1) {
                        char current = (char) data;
                        articleInfo += current;

                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(articleInfo);

                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url"))    //if title & URL is exists and is not null
                    {
                        String articleTitle = jsonObject.getString("title"); // get Title
                        String articleURL = jsonObject.getString("url"); // get uRL
                        Log.i("info", articleTitle + " " + articleURL);

                        url = new URL(articleURL);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        in = urlConnection.getInputStream();

                        /*
                        reader = new InputStreamReader(in);
                        data = reader.read();
                        String articleContent = "";
                        while (data != -1) {
                            char current = (char) data;
                            articleInfo += current;

                            data = reader.read();
                        }
                        */

                        // PLauDev more efficient http://stackoverflow.com/a/2549222/1827488
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder resultBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            resultBuilder.append(line).append("\n");
                        }

                        String articleContent = resultBuilder.toString();
                        Log.i("articleContent", "(length=" + articleContent.length() + ") ");
                        //Log.i("articleContent", articleContent);


                        String sql = "INSERT INTO articles (articleId, title, content) VALUES (? , ? , ?)";

                        SQLiteStatement statement = articlesDb.compileStatement(sql);

                        statement.bindString(1, articleId);
                        statement.bindString(2, articleTitle);
                        statement.bindString(3, articleContent);

                        statement.execute();
                    }

                }

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // when the Process Download Task is complete it  we use onPostExecute
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            updateListView();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflate)
    {
        super.onCreateOptionsMenu(menu,inflate);
        inflate.inflate(R.menu.menu_main, menu);


        MenuItem searchItem = menu.findItem(R.id.menu_item_search);


        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}