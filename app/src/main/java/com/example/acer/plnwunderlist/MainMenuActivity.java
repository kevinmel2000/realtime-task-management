package com.example.acer.plnwunderlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Ryan Fadholi on 20/07/2017.
 */

public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "MainMenuActivity";
    private ArrayList<String> todoLists = new ArrayList<>();
    HashMap<String, String> userData;

    String endpoint = "http://pudinglab.id/puding-master/PLN/endpoint.php";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Loading data...");
        showDialog();

        //the scale. Refer to:
        //https://stackoverflow.com/questions/4275797/view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp
        final float scale = getResources().getDisplayMetrics().density;

        ListView todoListsList = (ListView) findViewById(R.id.todolistslist);
        todoListsList.setDivider(null);
        todoListsList.setDividerHeight(0);
        todoLists.add("Kerjaan");
        todoLists.add("Gawean");
        todoLists.add("Lokak");
        todoLists.add("Belajar");

        SessionManager sessionManager = new SessionManager(this);
        userData = sessionManager.getUserDetails();

        final todoListAdapter adapter = new todoListAdapter(this, todoLists);
        todoListsList.setAdapter(adapter);

        String reqUrl = endpoint + "?action=get_list&user_id=" + userData.get("user_id");
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, reqUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("JSON", response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                adapter.add(jsonObject.getString("LIST_NAME"));
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        hideDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }

        );

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getRequest, "retrieve_list");


        View layout = getLayoutInflater().inflate(R.layout.main_menu_create_list_btn,null);
        todoListsList.addFooterView(layout);


        Button newListBtn = (Button) findViewById(R.id.add_list_btn);
        newListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainMenuActivity.this, "Add a new list", Toast.LENGTH_SHORT).show();
            }
        });
        //FINISH (CREATE NEW LIST) BUTTON LOGIC
        //---------------------------------------------------------------------------------

        TextView emptyListText = new TextView(this);
        emptyListText.setText(R.string.emptyList_Text);
        emptyListText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        if(todoLists.size() == 0){
            emptyListText.setGravity(Gravity.CENTER_HORIZONTAL);
            todoListsList.addHeaderView(emptyListText);
        }

    }

    private void showAddListDialog(Context context){
        final AlertDialog.Builder addListBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View addListDialogView = inflater.inflate(R.layout.main_menu_create_list_dialog, null);
        addListBuilder.setView(addListDialogView);

        final String newListName;
        final EditText newListText = (EditText) findViewById(R.id.newListTitleText);

        addListBuilder.setTitle("Create New To-Do List");
        addListBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO: GET THE USER INPUTTED TEXT
            }
        });
        addListBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
