package com.blog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class BlogActivity extends Activity {

    public TextView blogText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        blogText = (TextView) findViewById(R.id.textView1);
        GetBlogsTask task = new GetBlogsTask();
        task.execute(new String[] { "http://192.168.1.8:3000/blogs/jsonview.json" });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_blog, menu);
        return true;
    }
    
    @Override
    protected void onRestart()
    {
        super.onRestart();
        GetBlogsTask task = new GetBlogsTask();
        task.execute(new String[] { "http://192.168.1.8:3000/blogs/jsonview.json" });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch(item.getItemId()){
        case R.id.menu_settings:
            System.out.println("Newpost Selected");
            startActivity(new Intent(BlogActivity.this, Newpost.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            break;
        case R.id.logout:
            System.out.println("Logout Selected");
            startActivity(new Intent(BlogActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            break;
            
        }
        return true;
    }

    private static String getBlogs(String url1) {
        String blogtext = "";
        try {
            // Open connection for request
            URL url = new URL(url1);
            System.out.println("the url is set to " + url1);
            System.out.println("going to set it to get");
            HttpURLConnection blogsGetConn = (HttpURLConnection) url.openConnection();
            blogsGetConn.setRequestMethod("GET");
            blogsGetConn.connect();

            System.out.println(" the request method set is " + blogsGetConn.getRequestMethod());

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(blogsGetConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            // writer.close();
            reader.close();

            JSONObject object = (JSONObject) new JSONTokener(answer.toString()).nextValue();
            System.out.println(object.toString());
            JSONArray blogList = object.getJSONArray("blogs");
            for (int i = 0; i < blogList.length(); i++) {
                JSONObject j = (JSONObject) blogList.get(i);
                blogtext += j.getString("subject") + "\n";
                blogtext += j.getString("created") + "\n";
                blogtext += j.getString("name") + "\n";
                blogtext += j.getString("content") + "\n\n\n";

            }
            System.out.println("the blog text is " + blogtext);
        } catch (MalformedURLException ex) {
            System.out.println("url error");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("io error");
            ex.printStackTrace();
        } catch (JSONException e) {
            System.out.println("json error");
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return blogtext;
    }

    private class GetBlogsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                response = getBlogs(url);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
                blogText.setText(result);
            } else {
                Toast loginFailureToast = Toast.makeText(BlogActivity.this, R.string.loginunsucessful,
                        Toast.LENGTH_LONG);
                loginFailureToast.show();

            }
        }
    }

}
