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
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {
	
    public static EditText usernameText;
    public static EditText passwordText;
    public static String hashedname;
    public static String author;
    public static String[] rubyurls =  new String[] {"http://10.0.0.3:3000/user_details/login.json",
    	"http://10.0.0.3:3000/user_details/signup.json",
    	"http://10.0.0.3:3000/blogs/jsonview.json",
    	"http://10.0.0.3:3000/blogs/newpost.json"};
    public static String[] pythonurls = new String[] {"http://10.0.0.3:5000/login/",
    	"http://10.0.0.3:5000/signup/",
    	"http://10.0.0.3:5000/blog/",
    	"http://10.0.0.3:5000/newpost/"} ;
    public static String[] netsqlurls = new String[] {"http://10.0.0.2:49471/api/login/.json",
    	"http://10.0.0.2:49471/api/userdetails/.json",
    	"http://10.0.0.2:49471/api/blog/.json",
    	"http://10.0.0.2:49471/api/blog/.json"};
    public static String[] netmongourls = new String[] {"http://10.0.0.2:55167/api/login/.json",
    	"http://10.0.0.2:55167/api/userdetails/.json",
    	"http://10.0.0.2:55167/api/blog/.json",
    	"http://10.0.0.2:55167/api/blog/.json"};
    public static String[] phpsqlurls = new String[] {"http://10.0.0.3/phpblogsql/logincontroller.php",
    	"http://10.0.0.3/phphblogsql/signupcontroller.php",
    	"http://10.0.0.3/phpblogsql/blogcontroller.php",
    	"http://10.0.0.3/phpblogsql/newpostcontroller.php"};
    public static String[] phpmongourls = new String[] {"http://10.0.0.3/phpblogmongo/logincontroller.php",
    	"http://10.0.0.3/phphblogmongo/signupcontroller.php",
    	"http://10.0.0.3/phpblogmongo/blogcontroller.php",
    	"http://10.0.0.3/phpblogmongo/newpostcontroller.php"};

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
                task.execute(new String[] { pythonurls[0] });

            }
        });

        TextView signupBtn = (TextView) findViewById(R.id.textView3);

        signupBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Signup.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

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
            String line;
            author = usernameText.getText().toString();
            // Open connection for request
            URL url = new URL(url1);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
          
           
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            // write parameters
            StringBuilder sb = new StringBuilder();
            sb.append("&name=").append(username);
            sb.append("&password=").append(password);
            writer.write(sb.toString());
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            

            JSONObject object = (JSONObject) new JSONTokener(answer.toString()).nextValue();
            //System.out.println(object.toString());
            hashedname = object.getString("hashedname");
            //System.out.println("The received hash is " + hashedname);
            reader.close();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return hashedname;
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                response = Login.login(url);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (hashedname.length() >= 64) {

                Toast loginSuccessToast = Toast.makeText(Login.this, R.string.loginsuccessful, Toast.LENGTH_LONG);
                loginSuccessToast.show();
                usernameText.setText("");
                passwordText.setText("");
                startActivity(new Intent(Login.this, BlogActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else {
                Toast loginFailureToast = Toast.makeText(Login.this, R.string.loginunsucessful, Toast.LENGTH_LONG);
                loginFailureToast.show();

            }
        }
    }
}