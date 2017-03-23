package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.adapter.MyGridAdapter;
import com.duowei.dw_pos.bean.JYCSSZ;
import com.duowei.dw_pos.bean.TableUse;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DinningActivity extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private final String sqlUse="select datediff(mi,jysj,getdate())scjc,a.csmc,b.* from wmlsbjb b,jycssz a where (charindex('@'+a.csmc+',@',b.zh)>0 or charindex(a.csmc+',',b.zh)>0) and isnull(sfyjz,'0')<>'1' and wmdbh in(select wmdbh from wmlsb)|";
    private List<String>listName=new ArrayList<>();
    private Spinner mSp;
    private GridView mGv;
    private List<JYCSSZ> mJycssz;
    private MyGridAdapter mGv_adapter;
    private TextView mUser;
    private TableUse[] mTableUses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinning);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        DownHTTP.postVolley6(Net.url, sqlUse, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                mTableUses = gson.fromJson(response, TableUse[].class);
                Log.e("======",mTableUses.length+"号");
                initSpinner();
                initGridView("FCSBH!=?","");
            }
        });

        mUser = (TextView) findViewById(R.id.tv_user);
        mSp = (Spinner) findViewById(R.id.spinnner);
        mGv = (GridView) findViewById(R.id.gridView);
        mGv.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser.setText(Users.YHMC);
//        initSpinner();
//        initGridView("FCSBH!=?","");
        mSp.setOnItemSelectedListener(this);
    }

    private void initGridView(String str1,String str2) {
        mJycssz = DataSupport.select("CSMC").where(str1, str2).order("CSBH ASC").find(JYCSSZ.class);
        mGv_adapter = new MyGridAdapter(this, mJycssz,mTableUses);
        mGv.setAdapter(mGv_adapter);
    }

    private void initSpinner() {
        List<JYCSSZ> jycxxz = DataSupport.select("CSMC").where("FCSBH=?", "").order("CSBH ASC").find(JYCSSZ.class);
        listName.add("全部");
        for(JYCSSZ J:jycxxz){
            listName.add(J.getCSMC());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSp.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_exit:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(position==0){
            initGridView("FCSBH!=?","");
        }else{
            String csmc = listName.get(position);
            List<JYCSSZ> jycssz = DataSupport.select("CSBH").where("CSMC=?", csmc).find(JYCSSZ.class);
            String csbh = jycssz.get(0).CSBH;
            initGridView("FCSBH=?",csbh);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
