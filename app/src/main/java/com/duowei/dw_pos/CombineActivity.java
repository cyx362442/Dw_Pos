package com.duowei.dw_pos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.adapter.CombineAdapter;
import com.duowei.dw_pos.adapter.UnCombineAdapter;
import com.duowei.dw_pos.bean.WMLSBJB;
import com.duowei.dw_pos.event.Combine;
import com.duowei.dw_pos.event.UnCombine;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Net;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CombineActivity extends AppCompatActivity implements View.OnClickListener {
    private final String sql="select * from wmlsbjb where isnull(sfyjz,0)<>'1'and wmdbh in(select wmdbh from wmlsb)|";
    private UnCombineAdapter mUnCombineAdapter;
    private List<WMLSBJB> mListWmlsbjb1;
    private List<WMLSBJB>mListWmlsbjb2;
    private CombineAdapter mCombineAdapter;
    private ProgressBar mPb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine);
        mListWmlsbjb1=new ArrayList<>();
        mListWmlsbjb2=new ArrayList<>();
        EventBus.getDefault().register(this);
        initData();

        Http_Wmlsbjb();
    }

    private void initData() {
        mPb = (ProgressBar) findViewById(R.id.progressBar);
        mPb.setVisibility(View.VISIBLE);
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.tv_combine).setOnClickListener(this);
        RecyclerView rv_unCombine = (RecyclerView) findViewById(R.id.rv_unCombine);
        RecyclerView rv_combine = (RecyclerView) findViewById(R.id.rv_combine);
        rv_unCombine.setLayoutManager(new LinearLayoutManager(this));
        rv_combine.setLayoutManager(new LinearLayoutManager(this));
        rv_unCombine.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rv_combine.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mUnCombineAdapter = new UnCombineAdapter(mListWmlsbjb1, this);
        rv_unCombine.setAdapter(mUnCombineAdapter);
        mCombineAdapter = new CombineAdapter(mListWmlsbjb2,this);
        rv_combine.setAdapter(mCombineAdapter);
    }

    @Subscribe
    public void combine(Combine event){
        mListWmlsbjb2.add(mListWmlsbjb1.get(event.position));
        mListWmlsbjb1.remove(event.position);
        mCombineAdapter.setWMLSBJBs(mListWmlsbjb2);
        mUnCombineAdapter.setWMLSBJBs(mListWmlsbjb1);
    }

    @Subscribe
    public void unCombine(UnCombine event){
        mListWmlsbjb1.add(mListWmlsbjb2.get(event.position));
        mListWmlsbjb2.remove(event.position);
        mUnCombineAdapter.setWMLSBJBs(mListWmlsbjb1);
        mCombineAdapter.setWMLSBJBs(mListWmlsbjb2);
    }

    private void Http_Wmlsbjb() {
        DownHTTP.postVolley6(Net.url, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPb.setVisibility(View.GONE);
            }
            @Override
            public void onResponse(String response) {
                mPb.setVisibility(View.GONE);
               if(response.equals("]")){
                   return;
               }
                Gson gson = new Gson();
                Type type = new TypeToken<List<WMLSBJB>>() {
                }.getType();
                mListWmlsbjb1 = gson.fromJson(response, type);
                mUnCombineAdapter.setWMLSBJBs(mListWmlsbjb1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        String sql="";
        String zh="";
        int ys=0;
        if(view.getId()==R.id.tv_combine){
            if(mListWmlsbjb2.size()<=1){
                Toast.makeText(this,"请至少添加两份账单",Toast.LENGTH_SHORT).show();
            }else{
                mPb.setVisibility(View.VISIBLE);
                WMLSBJB wmlsbjb = mListWmlsbjb2.get(0);
                String wmdbh = wmlsbjb.getWMDBH();
                ys+=wmlsbjb.getYS();
                zh+=wmlsbjb.getZH();
                for(int i=1;i<mListWmlsbjb2.size();i++){
                    String wmdbh1 = mListWmlsbjb2.get(i).getWMDBH();
                    ys+=mListWmlsbjb2.get(i).getYS();
                    zh+=mListWmlsbjb2.get(i).getZH();
                    sql+="update wmlsb set wmdbh='"+wmdbh+"' where wmdbh='"+wmdbh1+"'|";
                    sql+="delete from wmlsbjb where wmdbh='"+wmdbh1+"'|";
                }
                sql+="update wmlsbjb set zh='"+zh+"',ys="+ys+" where wmdbh='"+wmdbh+"'|";

                DownHTTP.postVolley7(Net.url, sql, new VolleyResultListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mPb.setVisibility(View.GONE);
                        Toast.makeText(CombineActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(String response) {
                        mPb.setVisibility(View.GONE);
                        if(response.contains("richado")){
                            finish();
                        }
                    }
                });
            }
        }else if(view.getId()==R.id.img_back){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
