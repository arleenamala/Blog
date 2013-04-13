package com.blog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Newpost extends Activity {
    
    public static EditText titleText;
    public static EditText contentText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final RelativeLayout llLogin = (RelativeLayout) findViewById(R.id.newPostView);
        llLogin.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)((Context)getApplicationContext())
                        .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
        
        Button newpostBtn = (Button) findViewById(R.id.button1);
        
        newpostBtn.setOnClickListener(new OnClickListener() {
        
            @Override
            public void onClick(View v) {
                titleText = (EditText) findViewById(R.id.editText1);
                contentText = (EditText) findViewById(R.id.editText2); 
                NewpostTask task = new NewpostTask();
                task.execute(new String[] { Login.pythonurls[3] });
                
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_newpost, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch(item.getItemId()){
        case R.id.menu_settings:
            System.out.println("Newpost Selected");
            startActivity(new Intent(Newpost.this, BlogActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            break;
        }
        return true;
    }

    
    public static String newpost(String url1) {
        String success = "";
        try {
            String title = titleText.getText().toString();
            String content = contentText.getText().toString();
            String name = Login.author;
            // Open connection for request
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            
            // write parameters
            StringBuilder sb = new StringBuilder();
            sb.append("&name=").append(name);
            sb.append("&title=").append(title);
            sb.append("&content=").append(content);
            sb.append("&created=");
            writer.write(sb.toString());
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();

            JSONObject object = (JSONObject) new JSONTokener(answer.toString())
                    .nextValue();
            System.out.println(object.toString());
            success = object.getString("success");
            System.out.println("The received hash is "+ success);
           conn.disconnect();
           
        } catch (MalformedURLException ex) {
            System.out.println("url error");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("io error");
            ex.printStackTrace();
        } catch (JSONException e) {
            System.out.println("json error");
            e.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return success;
    }



private class NewpostTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      String response = "";
      for (String url : urls) {
          response = Newpost.newpost(url);
      }
      return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.length() == 3){
            Newpost.titleText.setText("");
            Newpost.contentText.setText("");
            startActivity(new Intent(Newpost.this, BlogActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
        else
        {
            Toast newpostFailureToast = Toast.makeText(Newpost.this, R.string.posterror, Toast.LENGTH_LONG);
            newpostFailureToast.show();
           
        }
    }
  }
}
