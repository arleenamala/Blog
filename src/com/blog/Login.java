package com.blog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

    public static EditText usernameText;
    public static EditText passwordText;
    public static String hashedname;
    public static String author;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginBtn = (Button) findViewById(R.id.button1);
        
        loginBtn.setOnClickListener(new OnClickListener() {
        
            @Override
            public void onClick(View v) {
                usernameText = (EditText) findViewById(R.id.editText1);
                passwordText = (EditText) findViewById(R.id.editText2); 
                LoginTask task = new LoginTask();
                task.execute(new String[] { "http://192.168.1.8:3000/user_details/login.json" });
                
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    
    public static String login(String url1) {
        try {
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            author = usernameText.getText().toString();
            // Open connection for request
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            
            // write parameters
            StringBuilder sb = new StringBuilder();
            sb.append("&name=").append(username);
            sb.append("&password=").append(password);
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
            hashedname = object.getString("hashedname");
            System.out.println("The received hash is "+ hashedname);
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
        return hashedname;
    }


private class LoginTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
      String response = "";
      for (String url : urls) {
          response = Login.login( url );
      }
      return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (hashedname.length() == 64){
            
            Toast loginSuccessToast = Toast.makeText(Login.this, R.string.loginsuccessful, Toast.LENGTH_LONG);
            loginSuccessToast.show();
            usernameText.setText("");
            passwordText.setText("");
            startActivity(new Intent(Login.this, BlogActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
        else
        {
            Toast loginFailureToast = Toast.makeText(Login.this, R.string.loginunsucessful, Toast.LENGTH_LONG);
            loginFailureToast.show();
           
        }
    }
  }
}