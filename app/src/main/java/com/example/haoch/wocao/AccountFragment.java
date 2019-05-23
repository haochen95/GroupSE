package com.example.haoch.wocao;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.haoch.wocao.file_api.Fileview_experiment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private ImageView docuemnt_login, account_setting, management_login, gps_location;
    private Button logout;
    private TextView tv_name;
    private String m_Text = "";
    private String USER_NAME;
    private Thread thread;

    private SharedPreferences getPassName;
    private SharedPreferences.Editor getPassNameEditor;
    private static final String PREF_NAME = "prefs";




    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        docuemnt_login = (ImageView)getActivity().findViewById(R.id.docuemnt_login);
        account_setting = (ImageView)getActivity().findViewById(R.id.account_setting);
        management_login = (ImageView)getActivity().findViewById(R.id.management_system);
        gps_location = (ImageView)getActivity().findViewById(R.id.gps_location);
        logout = (Button)getActivity().findViewById(R.id.appLogout);
        tv_name = (TextView)getActivity().findViewById(R.id.TV_NAE123);


        getPassName = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        getPassNameEditor = getPassName.edit();

        tv_name.setText(getPassName.getString("account", ""));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPassNameEditor.putBoolean("remember_password", false);
                getPassNameEditor.commit();
                startActivity(new Intent().setClass(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });



        management_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterPass();
            }
        });


        docuemnt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent().setClass(getActivity(), Fileview_experiment.class));
                    }
                });
                thread.start();

            }
        });

    }

    private void enterPass(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Management password");

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.text_input_password, null);
          // Set up the input

           //        final EditText input666 = new EditText(getActivity());
        final EditText input666 = (EditText)viewInflated.findViewById(R.id.input_pass);
        input666.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(viewInflated);
          // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

         // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //successful
                if (input666.getText().toString().trim().equals("666")){
                    // input正确
                               startActivity(new Intent().setClass(getActivity(), userManageActivity.class));
                } else {
                    enterPass();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }





}
