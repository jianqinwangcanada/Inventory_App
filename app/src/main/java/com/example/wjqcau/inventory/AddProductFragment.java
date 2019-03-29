package com.example.wjqcau.inventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wjqcau.inventory.JavaBean.Product;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddProductFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {
    FragmentManager fm;
    EditText priceInput;
     EditText amountInput;
      EditText nameInput;
      String ImageURL="";
      Spinner unitSpinner;
    List<String> spinerList;
    //Define variables for take photos
    private  int productMaxId;
    private String encoded_string,image_name;
    private Bitmap bitmap;
    private File file;
    private  Uri file_uri;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE=10;
    ImageView showImage;
    private static  Intent i;
    ImageView showProductImage;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    SharedPreferences settingPref;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
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
        fm=getFragmentManager();
        spinerList = new ArrayList<String>();
        //addUnitSpinner();
        settingPref=PreferenceManager.getDefaultSharedPreferences(getContext());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view =  inflater.inflate(R.layout.fragment_add_product, container, false);
     spinerList.clear();
      //add spinner
       if(settingPref.getString("unitkey","lb")!=null){
           spinerList.add(settingPref.getString("unitkey","lb"));
       }else{
           spinerList.add("lb");
       }

        spinerList.add("PKG");

        unitSpinner = (Spinner) view.findViewById(R.id.seclectUnit);
        unitSpinner.setSelection(0);
//        unitSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(dataAdapter);
        Button addProductButtton=view.findViewById(R.id.addProductButton);
        priceInput=view.findViewById(R.id.addproductPriceInput);
         amountInput=view.findViewById(R.id.addproductAmountInput);
        nameInput=view.findViewById(R.id.addproductNameInput);

        showProductImage=view.findViewById(R.id.addProductImage);
        showProductImage.setImageResource(R.drawable.camera);

        Button takePhotoButton=view.findViewById(R.id.takePhotoButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db=new DatabaseHandler(getContext());
              productMaxId=db.getProductMaxId()+1;
              db.close();
             Log.d("Image_URL",productMaxId+"");
             ImageURL="https://jwang.scweb.ca/PhotoServer/images/"+productMaxId+".jpg";

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                image_name=productMaxId+".jpg";
              //Create local file Uri
                file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+
                        File.separator+image_name);
                file_uri= Uri.fromFile(file);
                 Log.w("FileUri",file_uri.toString());
               i.putExtra(MediaStore.EXTRA_OUTPUT,file_uri);
                startActivityForResult(i,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);



            }
        });



        addProductButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product=new Product(nameInput.getText().toString(),priceInput.getText().toString(),
                        amountInput.getText().toString(),ImageURL,unitSpinner.getSelectedItem().toString(),
                        CategoryProductAdapter.categoryId);
              // Log.d("ChoiceIs",unitSpinner.getSelectedItem().toString());
               DatabaseHandler db=new DatabaseHandler(getContext());
               db.addProduct(product);
               db.close();

               nameInput.setText("");
               priceInput.setText("");
               amountInput.setText("");
                FragmentTransaction transaction=fm.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.content,new HomeFragment());
                transaction.commit();
            }
        });

        return view;
    }

    /**
     * Mainly get permission and execute upload image to server
     * @param requestCode CAMERA_CAPTURE_IMAGE_REQUEST_CODE
     * @param resultCode constant of Result_ok
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode== RESULT_OK) {
            //Toast.makeText(getContext(), "No", Toast.LENGTH_LONG).show();
            Log.d("Result_call","enterResult");
            // new Encode_Image().execute();

            if(ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    !=PackageManager.PERMISSION_GRANTED)
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)){

                }else {
                    Log.d("PermissionFirst","PermissionFirstTime");
                    // new Encode_Image().execute();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            666);
                      }


            }else{
                Log.d("PermissionSecond","PermissionSecondTime");

                new Encode_Image().execute();
                Log.d("LocalURL",file_uri.getPath());
                Picasso.with(getContext())
                        .load("file://"+file_uri.getPath()).memoryPolicy(MemoryPolicy.NO_CACHE).
                        networkPolicy(NetworkPolicy.NO_CACHE).error(R.drawable.chicken).
                        into(showProductImage);

            }

        }else{
            Toast.makeText(getContext(),"Permission denied!",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Inner class used to compressed image and upload to server
     */
    public class Encode_Image extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            bitmap = BitmapFactory.decodeFile(file_uri.getPath());
            Log.d("BitMap",bitmap == null?"Empty":"Good");


            //showImage.setImageBitmap(bitmap);
            // Log.d("showError",file_uri.getPath());
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,1,stream);
            byte[] array=stream.toByteArray();
            encoded_string=Base64.encodeToString(array,0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            makeRequest();

        }
    }
    private  void makeRequest(){
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest request=new StringRequest(Request.Method.POST, Config.FILE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //add method here
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // return super.getParams();
                HashMap<String,String> map=new HashMap<>();
                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);
                return  map;
            }
        };
        requestQueue.add(request);


    }

public void ExecuteImageUpload(){
    new Encode_Image().execute();
    //download picture from server


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
         * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
