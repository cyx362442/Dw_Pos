package com.duowei.dw_pos;

import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.duowei.dw_pos.adapter.ListAdapter;
import com.duowei.dw_pos.bean.JYCSSZ;
import org.litepal.crud.DataSupport;
import java.util.List;

public class DinningActivity extends AppCompatActivity implements  View.OnClickListener {
    private List<JYCSSZ> mJycssz;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinning);
        mGridView = (GridView) findViewById(R.id.gridView);
        findViewById(R.id.imageButton).setOnClickListener(this);
        mJycssz = DataSupport.select("CSMC").where("FCSBH=?", "").order("CSBH ASC").find(JYCSSZ.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frameList,new ListFragment());
                ft.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageButton:
                break;
        }
    }
}
