package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.duowei.dw_pos.adapter.MyGridAdapter;
import com.duowei.dw_pos.bean.JYCSSZ;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class DinningActivity extends AppCompatActivity implements  View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private List<String>listName=new ArrayList<>();
    private Spinner mSp;
    private GridView mGv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinning);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        mSp = (Spinner) findViewById(R.id.spinnner);
        mGv = (GridView) findViewById(R.id.gridView);
        mSp.setOnItemSelectedListener(this);
        mGv.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSpinner();
        initGridView();
    }

    private void initGridView() {
        List<JYCSSZ> jycxxz = DataSupport.select("CSMC").where("FCSBH!=?", "").order("CSBH ASC").find(JYCSSZ.class);
        MyGridAdapter adapter = new MyGridAdapter(this, jycxxz);
        mGv.setAdapter(adapter);
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
        Log.e("=====",position+"号");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
