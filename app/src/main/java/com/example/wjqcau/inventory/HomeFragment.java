package com.example.wjqcau.inventory;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.wjqcau.inventory.JavaBean.Product;
import com.example.wjqcau.inventory.JavaBean.ProductCategory;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
   //Declare arraylist to hold categories
    ArrayList<ProductCategory> categories;
    //delcare dialogue to show the add category
    Dialog addCateDialog;
    //declare button in the add category dialogue
    Button addCateButton;
    //declare text input in the add category dialogue
    EditText addCateInput;
    //The "X" image used to colse the dialogue
    ImageView closeImage;
    //Declare the category adapter
     CategoryProductAdapter adapter;
     //Decalre the frgmentmanager object which is reused in other fragment
    public static FragmentManager fm;
    public static FragmentTransaction transaction;

    //public static Dialog editCateDialog;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        //populate the categories from database
        categories=new ArrayList<>();
        //populate the addCategorydialogue
        addCateDialog=new Dialog(getContext());
        //Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_SHORT).show();
        addCateDialog.setContentView(R.layout.addcategory);
        addCateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //populate the framgment manager
        fm=getFragmentManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       //Toggle the addcategoryiamge to visible
        MainActivity.addCategoryImage.setVisibility(View.VISIBLE);
        //show the actionbar
        MainActivity.actionBar.show();
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_home, container, false);
       //Define the recyclevier for category
        final RecyclerView recyclerViewCatetory=view.findViewById(R.id.category_item_recycleView);
        //call the method to populat the category arraylist
         getCategoriesFromDB();
         //populate the adapter
         adapter=new CategoryProductAdapter(getContext(),categories);
         //set the adapter
        recyclerViewCatetory.setAdapter(adapter);
        //set the recyclerview layout
        recyclerViewCatetory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        //recyclerViewCatetory.setHasFixedSize(true);
        /**
         * Declare all the elements in the add Category Dialog
         */
        addCateInput=addCateDialog.findViewById(R.id.add_title);
        addCateButton=addCateDialog.findViewById(R.id.add_category_button);
        closeImage=addCateDialog.findViewById(R.id.close_add_category);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addCateDialog.dismiss();
            }
        });

        /**
         * When user click the add category button
         * step 1:get title and insert into the database
         * step 2: grab the productcategories from database
         * step 3: dismiss the dialog
         * step 4: inform the reccyclerview to reload view
         */
        addCateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // step 1:get title and insert into the database
                DatabaseHandler db=new DatabaseHandler(getContext());
                db.addCatetory(addCateInput.getText().toString());
                db.close();
               // getCategoriesFromDB();
               //refresh the data
                adapter.notifyDataSetChanged();
                //refresh all the framework
                transaction=fm.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.content,new HomeFragment());
                transaction.commit();
                addCateDialog.dismiss();
            }
        });



      //Click the add icon in the toolbar
       MainActivity.addCategoryImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              //show the add category dialogue
               addCateDialog.show();
           }
       });
        return view;
    }



  //method mainly populate the category arraylist from the database
   public void getCategoriesFromDB() {
       DatabaseHandler db=new DatabaseHandler(getContext());
       categories=db.getAllCategories();
       db.close();

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
