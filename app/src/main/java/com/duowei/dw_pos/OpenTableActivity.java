package com.duowei.dw_pos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.duowei.dw_pos.bean.GKLX;
import com.duowei.dw_pos.bean.OpenInfo;
import com.duowei.dw_pos.tools.CartList;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpenTableActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rlTop)
    RelativeLayout mRlTop;
    @BindView(R.id.editText2)
    EditText mEditText2;
    @BindView(R.id.editText3)
    EditText mEditText3;
    @BindView(R.id.btn_cancel)
    Button mBtnCancel;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.spinner_open)
    Spinner mSpinnerOpen;
    String csmc;
    private List<String>gkName=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_table);
        ButterKnife.bind(this);
        initSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        csmc = getIntent().getStringExtra("csmc");
        mTvTitle.setText("开台—" + csmc);
    }

    private void initSpinner() {
        gkName.clear();
        gkName.add("未选择……");
        List<GKLX> gklx = DataSupport.findAll(GKLX.class);
        for(GKLX G:gklx){
            gkName.add(G.getGKLX());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gkName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerOpen.setAdapter(adapter);
    }

    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_confirm:
                String stytle = mSpinnerOpen.getSelectedItem().toString();//顾客类型
                if (stytle.equals("未选择……")) {
                    stytle = "";
                }

                CartList.newInstance(this).setOpenInfo(new OpenInfo(
                        csmc,
                        stytle,
                        mEditText2.getText().toString(),
                        mEditText3.getText().toString()
                ));

                Intent intent = new Intent(this, CashierDeskActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
