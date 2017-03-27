package com.duowei.dw_pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.adapter.MyGridAdapter;
import com.duowei.dw_pos.bean.JYCSSZ;
import com.duowei.dw_pos.bean.TableUse;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Net;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DinningActivity extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private String sqlUse="select datediff(mi,jysj,getdate())scjc,a.csmc,b.* from wmlsbjb b,jycssz a where (charindex('@'+a.csmc+',@',b.zh)>0 or charindex(a.csmc+',',b.zh)>0) and isnull(sfyjz,'0')<>'1' and wmdbh in(select wmdbh from wmlsb)|";

    private List<String>listName=new ArrayList<>();
    private Spinner mSp;
    private GridView mGv;
    private List<JYCSSZ> mJycssz;
    private MyGridAdapter mGv_adapter;
    private TextView mUser;
    private TableUse[] mTableUses=new TableUse[]{};
    private Intent mIntent;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinning);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mUrl = user.getString("url", "");

        mUser = (TextView) findViewById(R.id.tv_user);
        mSp = (Spinner) findViewById(R.id.spinnner);
        mGv = (GridView) findViewById(R.id.gridView);
        mGv.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser.setText(Users.YHMC);
        mSp.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Http_TalbeUse();
    }

    private synchronized void Http_TalbeUse() {
        DownHTTP.postVolley6(mUrl, sqlUse, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                if(!response.equals("]")){
                    Gson gson = new Gson();
                    mTableUses = gson.fromJson(response, TableUse[].class);
                }else{
                    mTableUses=new TableUse[0];
                }
                initSpinner();
                initGridView("FCSBH!=?","");
            }
        });
    }

    private void initGridView(String str1,String str2) {
        mJycssz = DataSupport.select("CSMC").where(str1, str2).order("CSBH ASC").find(JYCSSZ.class);
        mGv_adapter = new MyGridAdapter(this, mJycssz,mTableUses);
        mGv.setAdapter(mGv_adapter);
    }

    private void initSpinner() {
        listName.clear();
        List<JYCSSZ> jycxxz = DataSupport.select("CSMC").where("FCSBH=?", "").order("CSBH ASC").find(JYCSSZ.class);
        listName.add("全部");
        for(JYCSSZ J:jycxxz){
            listName.add(J.getCSMC());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item2, R.id.tv_spinner,listName);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final String csmc = mJycssz.get(position).getCSMC();
        String sql="select * from wmlsbjb where '@'+zh like '%@"+csmc+"%' and isnull(sfyjz,'0')<>'1' and wmdbh in(select wmdbh from wmlsb)|";
        DownHTTP.postVolley6(mUrl, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DinningActivity.this,"餐桌数据获取失败",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                if(response.equals("]")){//餐桌未占用
                    mIntent = new Intent(DinningActivity.this, OpenTableActivity.class);
                    mIntent.putExtra("csmc",csmc);
                    startActivity(mIntent);
                }else{//餐桌己被占用，获取相关信息
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String wmdbh = jsonObject.getString("WMDBH");//单据编号
                        Intent intent = new Intent(DinningActivity.this, CheckOutActivity.class);
                        intent.putExtra("WMDBH",wmdbh);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
