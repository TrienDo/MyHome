package uk.ac.triendo.myhome;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
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

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View objView = inflater.inflate(R.layout.fragment_login, container, false);
        Button btnLogin = (Button)objView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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

    public void login(View v)
    {
        new FetchSecuredResourceTask().execute();
    }

    // ***************************************
    // Private methods
    // ***************************************
    private void displayResponse(Address response) {
        Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class FetchSecuredResourceTask extends AsyncTask<Void, Void, Address> {

        private String username;

        private String password;

        @Override
        protected void onPreExecute() {
            showLoadingProgressDialog();

            // build the message object
            EditText editUsername = (EditText)getView().findViewById(R.id.etUsername);
            this.username = editUsername.getText().toString();

            EditText editPasword = (EditText)getView().findViewById(R.id.etPassword);
            this.password = editPasword.getText().toString();
        }

        @Override
        protected Address doInBackground(Void... params) {
            //final String url = getString(R.string.restful_base_uri) + MyHomeLib.URL_LOGIN;
            final String url = getString(R.string.restful_base_uri) + "/addressOne/";
            // Populate the HTTP Basic Authentitcation header with the username and password
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
                ResponseEntity<Address> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(requestHeaders), Address.class);
                return response.getBody();
            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Address("This", "is", "a", "wrong", "address" + e.getMessage());
            } catch (ResourceAccessException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                return new Address("This", "is", "a", "wrong", "address" + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(Address result) {
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
