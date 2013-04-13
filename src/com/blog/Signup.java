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
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity {

    public static EditText usernameText;
    public static EditText passwordText;
    public static EditText confirmpassText;
    public static EditText emailText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button signupBtn = (Button) findViewById(R.id.button1);

        signupBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                usernameText = (EditText) findViewById(R.id.editText1);
                passwordText = (EditText) findViewById(R.id.editText2);
                confirmpassText = (EditText) findViewById(R.id.editText3);
                emailText = (EditText) findViewById(R.id.editText4);
                SignupTask task = new SignupTask();
                task.execute(new String[] { Login.pythonurls[1] });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.menu_settings:
            System.out.println("Newpost Selected");
            startActivity(new Intent(Signup.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            break;
        }
        return true;
    }

    public static String signup(String url1) {
        String hashedname = "";
        try {
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            String confirmpass = confirmpassText.getText().toString();
            String email = emailText.getText().toString();

            if ((username.length() == 0) || (password.length() == 0) || (confirmpass.length() == 0)) {
                return "fieldempty";
            } else if (password.equals(confirmpass)) {
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
                sb.append("&email=").append(email);
                writer.write(sb.toString());
                writer.flush();

                // Get the response
                StringBuffer answer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    answer.append(line);
                }
                writer.close();
                reader.close();

                JSONObject object = (JSONObject) new JSONTokener(answer.toString()).nextValue();
                System.out.println(object.toString());
                hashedname = object.getString("hashedname");
                System.out.println("The received hash is " + hashedname);
                conn.disconnect();
            } else {
                return "passerror";
            }
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

    private class SignupTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                response = Signup.signup(url);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() >= 64) {
                startActivity(new Intent(Signup.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            } else if (result.equalsIgnoreCase("fieldempty")) {
                    Toast signupFailureToast = Toast
                            .makeText(Signup.this, R.string.signupfielderror, Toast.LENGTH_LONG);
                    signupFailureToast.show();
                } else if (result.equalsIgnoreCase("passerror")) {
                    Toast signupFailureToast = Toast.makeText(Signup.this, R.string.signuppasserror, Toast.LENGTH_LONG);
                    signupFailureToast.show();
                }else{
                Toast signupFailureToast = Toast.makeText(Signup.this, R.string.signuperror, Toast.LENGTH_LONG);
                signupFailureToast.show();
                }

            
        }
    }
}
