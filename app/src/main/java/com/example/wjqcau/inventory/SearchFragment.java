package com.example.wjqcau.inventory;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.wjqcau.inventory.JavaBean.SearchResult;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


/**
 * @author wjqcau
 * Date created: 2019-04-12
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<String> result=new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //Declare the adapter
    private CustomSearchRecyclerAdapter adapter;
    //Decare the arraylist for origin data
    private ArrayList<SearchResult> originLists;
    SearchView searchView;
    public static final int VOICE_REQUEST_CODE=20;
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        MainActivity.addCategoryImage.setVisibility(View.INVISIBLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view=  inflater.inflate(R.layout.fragment_search, container, false);
      // MainActivity.actionBar.hide();
       //Layout floatView=view.findViewById(R.id.floatingView);
         searchView=(SearchView)view.findViewById(R.id.searchAction);
         //set the serchview icon and it's default configuration
        searchView.setIconifiedByDefault(false);
       searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

       //Define the recyclerview
        RecyclerView recyclerView=view.findViewById(R.id.searchProductRecyclerView);
        //Set the recyclerview layout
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        //Get the produt list in the database
        DatabaseHandler db=new DatabaseHandler(getContext());
        originLists=db.getProductSearchResult();
        //set the adapter to the recyclerview
        adapter=new CustomSearchRecyclerAdapter(getContext(),originLists);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
         //set the search view the event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
               //use the adapter to call the fileter
                adapter.getFilter().filter(s);
                return false;
            }
        });
        //get the voice iamgeview
        ImageView voiceActionImage=view.findViewById(R.id.VoiceIcon);
        //Set the click event
        voiceActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Log.d("VoiceRun","hello");
                //Define the intent
                Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                //Put the value to the intent
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
               intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getContext().getPackageName());
               //strart the intent from the activity
                    startActivityForResult(intent,VOICE_REQUEST_CODE);

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  Log.d("VoiceRun","hello2");

        switch (requestCode){
         //if the request is sent by the voice recognizer intent
            case VOICE_REQUEST_CODE:
                if(resultCode==RESULT_OK&&data!=null){
                   // Log.d("VoiceRun","hello2");
             // set the result from the recognizer result
             ArrayList<String> result =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
             //invoke the searchview to call search again
              searchView.setQuery(result.get(0),true);
                }
                break;
        }
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
