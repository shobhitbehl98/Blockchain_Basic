package com.example.blockchain_basic.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.example.blockchain_basic.R;
import com.example.blockchain_basic.databinding.FragmentPowBinding;
import com.example.blockchain_basic.manager.SharedPreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PowFragment extends DialogFragment implements View.OnClickListener {

    private FragmentPowBinding viewBinding;
    private Context mcontext;
    private SharedPreferencesManager prefs;



    public PowFragment() {
        // Required empty public constructor
    }


    public static PowFragment newInstance() {

        return new PowFragment();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        mcontext=context.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding =FragmentPowBinding.inflate(getLayoutInflater(),container,false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs=new SharedPreferencesManager(mcontext);
        viewBinding.edtSetPow.setText(String.valueOf((prefs.getPowValue())));
        viewBinding.btnClose.setOnClickListener(this);
        viewBinding.btnContinue.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog=super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(dialog.getWindow()!=null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return dialog;
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()) {
           case R.id.btn_close:
               dismiss();
               break;
           case R.id.btn_continue:
               if(viewBinding.edtSetPow.getText()!=null){
                   String pow=viewBinding.edtSetPow.getText().toString();
                   prefs.setPowValue(Integer.parseInt(pow));
                   if(getActivity()!=null){
                       Intent intent=mcontext.getPackageManager().getLaunchIntentForPackage(mcontext.getPackageName());
                       startActivity(intent);
                       getActivity().finish();
                   }else{
                       dismiss();
                   }
                   break;
               }
       }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        viewBinding=null;
        mcontext=null;
    }
}