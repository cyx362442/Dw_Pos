package com.duowei.dw_pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.duowei.dw_pos.adapter.MyGridAdapter;
import com.duowei.dw_pos.bean.JYCSSZ;
import com.duowei.dw_pos.bean.TableUse;
import com.duowei.dw_pos.event.ChangeTable;
import com.duowei.dw_pos.event.FinishEvent;
import com.duowei.dw_pos.event.SelectPing;
import com.duowei.dw_pos.fragment.PingFragment;
import com.duowei.dw_pos.httputils.DownHTTP;
import com.duowei.dw_pos.httputils.Post7;
import com.duowei.dw_pos.httputils.VolleyResultListener;
import com.duowei.dw_pos.tools.Users;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

public class DinningActivity extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, PopupMenu.OnMenuItemClickListener {
    private String sqlUse="select datediff(mi,jysj,getdate())scjc,a.csmc,b.* from wmlsbjb b,jycssz a where (charindex('@'+a.csmc+',@',b.zh)>0 or charindex(a.csmc+',',b.zh)>0) and isnull(sfyjz,'0')<>'1' and wmdbh in(select wmdbh from wmlsb)|";

    private List<String>listName=new ArrayList<>();
    private Spinner mSp;
    private GridView mGv;
    private List<JYCSSZ> mJycssz=new ArrayList<>();
    private MyGridAdapter mGv_adapter;
    private TextView mUser;
    private TableUse[] mTableUses=new TableUse[]{};
    private String mUrl;
    private ProgressBar mPb;
    private Handler mHandler;
    private Runnable mRun;
    private String mCsbh;
    private final int REQUESTCODE=100;
    private String mWmdbh;
    private ImageView mImgMore;
    private boolean isPing=false;
    private TextView mTvPing;
    private ArrayAdapter<String> mSpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinning);
        EventBus.getDefault().register(this);
        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mUrl = user.getString("url", "");
        initUi();
    }

    private void initUi() {
        mPb = (ProgressBar) findViewById(R.id.progressBar);
        mUser = (TextView) findViewById(R.id.tv_user);
        mSp = (Spinner) findViewById(R.id.spinnner);
        mTvPing = (TextView) findViewById(R.id.tv_ping);
        mImgMore = (ImageView) findViewById(R.id.img_more);
        mImgMore.setOnClickListener(this);
        mGv = (GridView) findViewById(R.id.gridView);
        mGv_adapter = new MyGridAdapter(this, mJycssz,mTableUses);
        mGv.setAdapter(mGv_adapter);
        mGv.setOnItemClickListener(this);

        mSpAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item2, R.id.tv_spinner,listName);
        mSpAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item2);
        mSp.setAdapter(mSpAdapter);
        mSp.setOnItemSelectedListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==REQUESTCODE&&resultCode==CheckOutActivity.RESURTCODE){
           mWmdbh = data.getStringExtra("wmdbh");
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser.setText(Users.YHMC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Http_TalbeUse();
        //自动刷新餐桌
        if(mHandler!=null){
            mHandler.removeCallbacks(mRun);
        }
        mHandler = new Handler();
        mHandler.postDelayed(mRun = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this,15000);
                brushTable();
            }
        },5000);
    }

//    @Subscribe
//    public void finishActivity(FinishEvent event){
//        finish();
//    }
    /**转台后刷新*/
    @Subscribe
    public void changeTable(ChangeTable event){
        Http_TalbeUse();
        mWmdbh=null;
    }
    //选择拼桌
    @Subscribe
    public void selectPing(SelectPing event){
        toCheckOutActivity(event.wmdbh);
    }

    private void brushTable() {
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
                if(TextUtils.isEmpty(mCsbh)){
                    mJycssz = DataSupport.select("CSMC").where("FCSBH!=?","").order("CSBH ASC").find(JYCSSZ.class);
                }else{
                    mJycssz = DataSupport.select("CSMC").where("FCSBH=?",mCsbh).order("CSBH ASC").find(JYCSSZ.class);
                }
                mGv_adapter.setUsed(mTableUses);
                mGv_adapter.setList(mJycssz);
                mGv_adapter.notifyDataSetChanged();
            }
        });
    }

    private synchronized void Http_TalbeUse() {
        mPb.setVisibility(View.VISIBLE);
        DownHTTP.postVolley6(mUrl, sqlUse, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPb.setVisibility(View.GONE);
                Toast.makeText(DinningActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
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
                if(TextUtils.isEmpty(mCsbh)){
                    initGridView("FCSBH!=?","");
                }else{
                    initGridView("FCSBH=?",mCsbh);
                }
            }
        });
    }

    private void initGridView(String str1,String str2) {
        mJycssz = DataSupport.select("CSMC").where(str1, str2).order("CSBH ASC").find(JYCSSZ.class);
        mGv_adapter.setUsed(mTableUses);
        mGv_adapter.setList(mJycssz);
        mGv_adapter.notifyDataSetChanged();
        mPb.setVisibility(View.GONE);
    }

    private void initSpinner() {
        listName.clear();
        List<JYCSSZ> jycxxz = DataSupport.select("CSMC").where("FCSBH=?", "").order("CSBH ASC").find(JYCSSZ.class);
        listName.add("全部");
        for(JYCSSZ J:jycxxz){
            listName.add(J.getCSMC());
        }
        mSpAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_more:
                PopupMenu popupMenu = new PopupMenu(this, mImgMore);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        final String csmc = mJycssz.get(position).getCSMC();
        String sql="select * from wmlsbjb where zh like '%"+csmc+"%' and isnull(sfyjz,'0')<>'1' and wmdbh in(select wmdbh from wmlsb)|";
        DownHTTP.postVolley6(mUrl, sql, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DinningActivity.this,"餐桌数据获取失败",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(String response) {
                if(response.equals("]")){//餐桌未占用
                    if(mWmdbh!=null){//转台
                        Post7.getInstance().ChangeTable(csmc+",",mWmdbh);
                    }else{
                        toOpenTableActivity(csmc);
                    }
                }else{//餐桌己被占用，获取相关信息
                    if(mWmdbh!=null){//转台
                        Post7.getInstance().ChangeTable(csmc+",",mWmdbh);
                    }else{
                        if(isPing==true){//拼桌
                            toOpenTableActivity(csmc);
                            return;
                        }
                        Gson gson = new Gson();
                        TableUse[] tableUses = gson.fromJson(response, TableUse[].class);
                        if(tableUses.length==1){
                            String wmdbh = tableUses[0].getWMDBH();
                            toCheckOutActivity(wmdbh);
                        }else if(tableUses.length>1){//拼桌，选择账单
                            PingFragment pingFragment = PingFragment.newInstance(tableUses);
                            pingFragment.show(getFragmentManager(),getString(R.string.ping));
                        }
                    }
                }
                isPing=false;
                mTvPing.setVisibility(View.GONE);
            }
        });
    }

    private void toOpenTableActivity(String csmc) {
        Intent intent = new Intent(DinningActivity.this, OpenTableActivity.class);
        intent.putExtra("csmc",csmc);
        startActivity(intent);
    }

    private void toCheckOutActivity(String wmdbh) {
        Intent intent = new Intent(DinningActivity.this, CheckOutActivity.class);
        intent.putExtra("WMDBH",wmdbh);
        startActivityForResult(intent,REQUESTCODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(position==0){
            initGridView("FCSBH!=?","");
            mCsbh="";
        }else{
            String csmc = listName.get(position);
            List<JYCSSZ> jycssz = DataSupport.select("CSBH").where("CSMC=?", csmc).find(JYCSSZ.class);
            mCsbh = jycssz.get(0).CSBH;
            initGridView("FCSBH=?", mCsbh);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mHandler!=null){
            mHandler.removeCallbacks(mRun);
        }
       mWmdbh=null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId()==R.id.ping){
            isPing=true;
            mTvPing.setVisibility(View.VISIBLE);
        }else if(item.getItemId()==R.id.combine){
            Intent intent = new Intent(this, CombineActivity.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.exit){
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
