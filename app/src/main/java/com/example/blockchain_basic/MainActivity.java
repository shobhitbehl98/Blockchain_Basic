package com.example.blockchain_basic;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blockchain_basic.adapter.BlockAdapter;
import com.example.blockchain_basic.manager.SharedPreferencesManager;
import com.example.blockchain_basic.model.BlockModel;
import com.example.blockchain_basic.utils.CipherUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<BlockModel> list;
    BlockAdapter adapter;
    SharedPreferencesManager prefs;
    ImageButton btn;
    TextInputEditText edt;
    int difficulty;
    private boolean isEncryptionActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs=new SharedPreferencesManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          btn=findViewById(R.id.btn_send_data);
          edt=findViewById(R.id.edit_message);
          difficulty=prefs.getPowValue();
          btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  startBlockChain(edt);
              }
          });

        initdata();
        initrecycler();

    }

    private void initdata() {
        list=new ArrayList<>();
        list.add(new BlockModel(0,System.currentTimeMillis(),null,"Genesis Block"));

    }

    public BlockModel newBlock(String data){
        BlockModel latestBlock=lastestBlock();

        String prev=latestBlock.getHash();
        Log.d("cheetos", "newBlock: "+data+" "+prev);
        return new BlockModel(latestBlock.getIndex()+1,System.currentTimeMillis(),prev,data);
    }
    private BlockModel lastestBlock(){
        return list.get(list.size()-1);
    }

    public void addBlock(BlockModel block){
        if(block!=null){
             block.mineBlock((difficulty));
            list.add(block);
            Log.d("cheetos", "addBlock: "+block.getData());
        }
    }

    private boolean isFirstBlockValid(){
        BlockModel firstBlock=list.get(0);
        if(firstBlock.getIndex()!=0){
            return false;
        }

        if(firstBlock.getPreviousHash()!=null){
            return false;
        }

        return firstBlock.getHash()!=null && BlockModel.calculateHash_detail(firstBlock).equals(firstBlock.getHash());
    }

    private boolean isValidNewBlock(@Nullable BlockModel newBlock,@Nullable BlockModel previousBlock) {
        if (newBlock != null && previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
                return false;
            }


            if (newBlock.getPreviousHash() == null || !newBlock.getPreviousHash().equals(newBlock.getData())) {
                return false;
            }
            return newBlock.getHash() != null && BlockModel.calculateHash_detail(newBlock).equals(newBlock.getHash());
        }
        return false;
    }

    public boolean isBlockChainValid(){
        if(!isFirstBlockValid()){
            return false;
        }

        for(int i=1;i<list.size();i++){
            BlockModel currentBlock=list.get(i);
            BlockModel previousBlock=list.get(i-1);
            if(!isValidNewBlock(currentBlock,previousBlock)){
                return false;
            }
        }
        return true;
    }

    private void initrecycler() {
        recyclerView=findViewById(R.id.recycler_view);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new BlockAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void startBlockChain(TextInputEditText m){
         if(m.getText()!=null&&recyclerView.getAdapter()!=null){
        String message=m.getText().toString();
        if(!message.isEmpty()){
                   if(!isEncryptionActivated){
                       Log.d("cheetos", "startBlockChain: "+message);
                       addBlock(newBlock(message));
                   }else{
                       try{
                           addBlock(newBlock(CipherUtils.encryptIt(message).trim()));
                       }catch(Exception e){
                           e.printStackTrace();
                           Toast.makeText(this,"Something fishy happened",Toast.LENGTH_LONG).show();
                       }
                   }

                   recyclerView.scrollToPosition(adapter.getItemCount()-1);
                   for(int h=0;h<list.size();h++){
                       Log.d("cheetos", list.get(h).getData()+"\n");
                   }
                   if(isBlockChainValid()){
                       Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                       edt.setText("");
                   }else{
                       Toast.makeText(this,"BlockChain corrupted",Toast.LENGTH_LONG).show();
                   }
               }else{
                   Toast.makeText(this,"Error empty data",Toast.LENGTH_LONG).show();
               }

            }else{
                Toast.makeText(this,"Something wrong happened",Toast.LENGTH_LONG).show();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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

            case R.id.action_exit:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
