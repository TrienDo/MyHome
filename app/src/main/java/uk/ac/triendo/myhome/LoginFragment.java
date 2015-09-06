package uk.ac.triendo.myhome;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


/**
 *
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    protected static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private boolean destroyed = false;
    EditText editUsername;
    EditText editPassword;
    CheckBox cbRemember;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View objView = inflater.inflate(R.layout.fragment_login, container, false);
        Button btnLogin = (Button)objView.findViewById(R.id.btnLogin);
        //Get references
        final SharedPreferences settings = getActivity().getSharedPreferences(MyHomeLib.APP_PREFERENCES, 0);
        boolean isRemember = settings.getBoolean("isRemember", false);
        cbRemember = (CheckBox)objView.findViewById(R.id.cbRemember);
        cbRemember.setChecked(isRemember);
        editUsername = (EditText)objView.findViewById(R.id.etUsername);
        editPassword = (EditText)objView.findViewById(R.id.etPassword);
        if(isRemember){
            editUsername.setText(settings.getString("username",""));
            editPassword.setText(settings.getString("password", ""));
        }

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Save references
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("isRemember", cbRemember.isChecked());
                if(cbRemember.isChecked())
                {
                    editor.putString("username",editUsername.getText().toString());
                    editor.putString("password",editPassword.getText().toString());
                }
                editor.commit();

                new FetchSecuredResourceTask().execute();
            }
        });
        return objView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    // ***************************************
    // Private methods
    // ***************************************
    private void displayResponse(ReturnMessage response) {

        if(response.getId() != -1)
            Toast.makeText(getActivity(), "You have sucessfully logged in", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getActivity(), "Something wrong. Pleas log in again", Toast.LENGTH_LONG).show();
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class FetchSecuredResourceTask extends AsyncTask<Void, Void, ReturnMessage> {
        private String username;
        private String password;
        @Override
        protected void onPreExecute() {
            showLoadingProgressDialog();
            this.username = editUsername.getText().toString();
            this.password = editPassword.getText().toString();
        }

        @Override
        protected ReturnMessage doInBackground(Void... params) {
            //final String url = getString(R.string.restful_base_uri) + MyHomeLib.URL_LOGIN;
            /*final String url = getString(R.string.restful_base_uri) + "/addressOne";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            Address result = restTemplate.getForObject(url, Address.class);
            System.out.println(result.toString());*/

            //HttpHeaders headers = new HttpHeaders();
            //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            //HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
            //ResponseEntity<Address> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Populate the HTTP Basic Authentitcation header with the username and password
            final String url = getString(R.string.restful_base_uri) + "/user";
            HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAuthorization(authHeader);
            requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Create a new RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

            try {
                // Make the network request
                Log.d(TAG, url);
                ResponseEntity<ReturnMessage> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(requestHeaders), ReturnMessage.class);
                ReturnMessage msg = response.getBody();
                MyHomeSettings appSettings = (MyHomeSettings) getActivity().getApplicationContext();
                if(msg.getId() != -1)
                {
                    appSettings.setRequestHeaders(requestHeaders);
                    appSettings.setUserID("" + msg.getId());
                    appSettings.setUsername(msg.getSubject());
                    appSettings.setUserEmail(msg.getText());
                }
                else
                    appSettings.setRequestHeaders(null);

                return msg;
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new ReturnMessage(0, e.getClass().getSimpleName(), e.getLocalizedMessage());
            }
        }

        @Override
        protected void onPostExecute(ReturnMessage result) {
            dismissProgressDialog();
            displayResponse(result);
        }

    }
    // ***************************************
    // Public methods
    // ***************************************
    public void showLoadingProgressDialog() {
        this.showProgressDialog("Loading. Please wait...");
    }

    public void showProgressDialog(CharSequence message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
        }

        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && !destroyed) {
            progressDialog.dismiss();
        }
    }
}
