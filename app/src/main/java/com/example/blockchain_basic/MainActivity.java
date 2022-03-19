package com.example.blockchain_basic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blockchain_basic.databinding.ActivityMainBinding;
import com.example.blockchain_basic.databinding.ContentMainBinding;
import com.example.blockchain_basic.fragment.PowFragment;
import com.example.blockchain_basic.manager.BlockchainManager;
import com.example.blockchain_basic.manager.SharedPreferencesManager;
import com.example.blockchain_basic.model.BlockModel;
import com.example.blockchain_basic.utils.CipherUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ContentMainBinding viewBindingContent;
    private ProgressDialog progressDialog;
    private SharedPreferencesManager prefs;
    private BlockchainManager blockChain;
    private boolean isEncryptionActivated, isDarkActivated;
    private static final String TAG_POW_DIALOG="proof_of_work_dialog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs=new SharedPreferencesManager(this);
        isDarkActivated=prefs.isDarkTheme();
        PowerManager powerManager=(PowerManager)getSystemService(POWER_SERVICE);
        boolean isPowerSaveMode=false;
        if(powerManager!=null){
            isPowerSaveMode=powerManager.isPowerSaveMode();
        }
        if(isPowerSaveMode){
            isPowerSaveMode=powerManager.isPowerSaveMode();
        }else{
            if(isDarkActivated){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        super.onCreate(savedInstanceState);
        ActivityMainBinding viewBinding=ActivityMainBinding.inflate((getLayoutInflater()));
        viewBindingContent=ContentMainBinding.bind(viewBinding.contentMain.getRoot());
        setContentView(R.layout.activity_main);
        isEncryptionActivated=prefs.getEncryptionStatus();
        viewBindingContent.recyclerView.setHasFixedSize(true);
        viewBindingContent.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        showProgressDialog(getResources().getString(R.string.text_creating_block_chain));

        new Thread(()-> runOnUiThread(()->{
            blockChain=new BlockchainManager(prefs.getPowValue(),this);
            viewBindingContent.recyclerView.setAdapter(blockChain.adapter);
            cancelProgressDialog(progressDialog);
        })).start();
        viewBindingContent.btnSendData.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void startBlockChain(){
        showProgressDialog(getResources().getString(R.string.text_mining_blocks));
        runOnUiThread(()->{
            if(blockChain!=null && viewBindingContent.editMessage.getText()!=null&&viewBindingContent.recyclerView.getAdapter()!=null){
               String message=viewBindingContent.editMessage.getText().toString();
               if(!message.isEmpty()){
                   if(!isEncryptionActivated){
                       blockChain.addBlock(blockChain.newBlock(message));
                   }else{
                       try{
                           blockChain.addBlock(blockChain.newBlock(CipherUtils.encryptIt(message).trim()));
                       }catch(Exception e){
                           e.printStackTrace();
                           Toast.makeText(this,"Something fishy happened",Toast.LENGTH_LONG).show();
                       }
                   }

                   viewBindingContent.recyclerView.scrollToPosition(blockChain.adapter.getItemCount()-1);
                   if(blockChain.isBlockChainValid()){
                       viewBindingContent.recyclerView.getAdapter().notifyDataSetChanged();
                       viewBindingContent.editMessage.setText("");
                   }else{
                       Toast.makeText(this,"BlockChain corrupted",Toast.LENGTH_LONG).show();
                   }
               }else{
                   Toast.makeText(this,"Error empty data",Toast.LENGTH_LONG).show();
               }
               cancelProgressDialog(progressDialog);

            }else{
                Toast.makeText(this,"Something wrong happened",Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send_data) {
            startBlockChain();
        }
    }



        private void showProgressDialog(@NonNull String loadingMessage){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(loadingMessage);
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.show();
        }

        private void cancelProgressDialog(@NonNull ProgressDialog progressDialog){
            if(progressDialog!=null){
                progressDialog.cancel();
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkEncrypt=menu.findItem(R.id.action_encrypt);
        checkEncrypt.setChecked(isEncryptionActivated);
        MenuItem checkTheme=menu.findItem(R.id.action_dark);
        checkTheme.setChecked(isDarkActivated);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_pow:
                PowFragment powFragment=PowFragment.newInstance();
                powFragment.show(this.getSupportFragmentManager(),TAG_POW_DIALOG);
                break;
            case R.id.action_encrypt:
                isEncryptionActivated= !item.isChecked();
                item.setChecked(isEncryptionActivated);
                if(item.isChecked()){
                    Toast.makeText(this,"Message Encryption ON",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Message Encryption OFF",Toast.LENGTH_SHORT).show();
                }
                prefs.setEncryptionStatus(isEncryptionActivated);
                return true;
            case R.id.action_dark:
                isDarkActivated=!item.isChecked();
                item.setChecked(isDarkActivated);
                prefs.setDarkMode(isDarkActivated);
                Intent intent=this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
                startActivity(intent);
                finish();
                return true;
            case R.id.action_exit:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
