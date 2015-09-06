package uk.ac.triendo.myhome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AddressFragment extends Fragment implements AbsListView.OnItemClickListener {
    private OnFragmentInteractionListener mListener;
    protected static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private boolean destroyed = false;
    List<String> addressesString = new ArrayList<String>();

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AddressFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        MyHomeSettings appSettings = (MyHomeSettings) getActivity().getApplicationContext();
        if(appSettings.getRequestHeaders() == null)
            Toast.makeText(getActivity(), "You need to log in to see your addresses", Toast.LENGTH_LONG).show();
        else
            new FetchSecuredResourceTask().execute();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyed = true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(addressesString.get(position));
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    // ***************************************
    // Private classes
    // ***************************************
    private class FetchSecuredResourceTask extends AsyncTask<Void, Void, ReturnMessage> {
        @Override
        protected void onPreExecute() {
            showLoadingProgressDialog();
        }

        @Override
        protected ReturnMessage doInBackground(Void... params) {
            MyHomeSettings appSettings = (MyHomeSettings) getActivity().getApplicationContext();
            final String url = getString(R.string.restful_base_uri) + "/users/" + appSettings.getUserID() + "/addresses/";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
            ResponseEntity<Address[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(appSettings.getRequestHeaders()), Address[].class);
            //restTemplate.getForEntity(url, Address[].class);
            Address[] tmp  = responseEntity.getBody();
            if(tmp == null)
            {
                addressesString.add("No addresses found");
            }
            else {
                for (int i = 0 ; i < tmp.length; i++)
                    addressesString.add(tmp[i].toString());
            }
            return new ReturnMessage(1,"Finished","");
        }

        @Override
        protected void onPostExecute(ReturnMessage result) {
            dismissProgressDialog();
            // Set OnItemClickListener so we can be notified on item clicks
            mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, addressesString);
            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        }

    }
    // ***************************************
    // Public methods
    // ***************************************
    public void showLoadingProgressDialog() {
        this.showProgressDialog("Loading your addresses. Please wait...");
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
