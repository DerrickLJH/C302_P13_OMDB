
package sg.edu.rp.webservices.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etLoginID, etPassword;
    private Button btnSubmit;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginID = (EditText)findViewById(R.id.editTextLoginID);
        etPassword = (EditText)findViewById(R.id.editTextPassword);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);
        client = new AsyncHttpClient();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginID.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter username.", Toast.LENGTH_LONG).show();

                } else if (password.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter password.", Toast.LENGTH_LONG).show();

                } else {
                    

					// TODO: call doLogin web service to authenticate user
                    RequestParams params = new RequestParams();
                    params.put("username",etLoginID.getText().toString());
                    params.put("password", etPassword.getText().toString());
                    client.post("http://10.0.2.2/C302_P13/doLogin.php", params ,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try{
                                Log.i("JSON Response", response.toString());
                                Boolean authenticated = response.getBoolean("authenticated");
                                if (authenticated == true){
                                    String apikey = response.getString("apikey");
                                    String id = response.getString("id");

                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("loginID", id);
                                    editor.putString("apiKey", apikey);
                                    editor.commit();

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
					//save the apikey into SharedPreference
                }
            }
        });
    }
}


