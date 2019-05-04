package com.nml.bt;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 简介：1.蓝牙概述
 *      2.蓝牙功能
 *      3.如何获取本地蓝牙信息
 *      4.如何绑定蓝牙
 *      5.如何使用蓝牙进行数据传输
 * @author niumenglin
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    /**
     * 打开蓝牙请求码
     */
    public static final int REQUEST_OPEN_BLUE_TOOTH = 0X01;

    private TextView mTvStatus;
    private Button mBtnOpenBlueTooth;
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 蓝牙是否开启
     */
    private boolean mIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        init();
        addListener();

    }

    private void findViews() {
        mTvStatus = findViewById(R.id.tv_status);
        mBtnOpenBlueTooth =findViewById(R.id.btn_open_bluetooth);
    }

    private void init() {
        //获取本地蓝牙的适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断蓝牙功能是否存在
        if (mBluetoothAdapter == null){
            showToast(getResources().getString(R.string.main_no_support));
            return;
        }

        //获取名称与mac地址
        String name = mBluetoothAdapter.getName();
        String address = mBluetoothAdapter.getAddress();
        Log.d(TAG,"name:"+name+"\n address:"+address);

        //蓝牙状态：STATE_ON=已打开；STATE_TURNING_ON=正在打开；STATE_TURNING_OFF=正在关闭；STATE_OFF=已经关闭
        int state = mBluetoothAdapter.getState();
        switch (state){
            case BluetoothAdapter.STATE_ON:
                showToast("蓝牙已经打开");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                showToast("蓝牙正在打开...");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                showToast("蓝牙正在关闭...");
                break;
            case BluetoothAdapter.STATE_OFF:
                showToast("蓝牙已经关闭");
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlueToothStatus();
    }

    private void addListener() {

        //打开蓝牙设备(前提是蓝牙是关闭状态)
        mBtnOpenBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断蓝牙是否已经打开
                mIsOpen = mBluetoothAdapter.isEnabled();
                if (mIsOpen){
                    //关闭蓝牙
                    boolean isClose = mBluetoothAdapter.disable();
                    Log.e(TAG, "蓝牙是否关闭:" + isClose);
                    mTvStatus.setText(String.format(getString(R.string.main_bluetooth_status_string),"已关闭"));
                    mBtnOpenBlueTooth.setText(getResources().getString(R.string.main_open_bluetooth));
                }else{
                    //蓝牙关闭状态-->打开蓝牙
//                    boolean isOpen = mBluetoothAdapter.enable();
//                    String str = String.format(getString(R.string.main_bluetooth_status),isOpen);
//                    Log.e(TAG, "str-->" + str);
//                    showToast(str);

                    //调用系统API打开蓝牙
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent,REQUEST_OPEN_BLUE_TOOTH);
                }

            }
        });
    }

    /**
     * 回调函数
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_BLUE_TOOTH){
            if (resultCode == RESULT_CANCELED){
                //失败
                showToast(getResources().getString(R.string.main_request_fail));
            }else{
                //成功
                showToast(getResources().getString(R.string.main_request_success));
                mTvStatus.setText(String.format(getString(R.string.main_bluetooth_status_string),"已开启"));
                mBtnOpenBlueTooth.setText(getResources().getString(R.string.main_close_bluetooth));
            }
        }
    }

    /**
     * 提示
     * @param msg
     */
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void getBlueToothStatus(){
        //判断蓝牙状态(开启或关闭)
        mIsOpen = mBluetoothAdapter.isEnabled();
        String statusStr;
        String btnDesc;
        if (mIsOpen){
            statusStr = String.format(getString(R.string.main_bluetooth_status_string),"已开启");
            btnDesc = getResources().getString(R.string.main_close_bluetooth);
        }else{
            statusStr = String.format(getString(R.string.main_bluetooth_status_string),"已关闭");
            btnDesc = getResources().getString(R.string.main_open_bluetooth);
        }
        mTvStatus.setText(statusStr);
        mBtnOpenBlueTooth.setText(btnDesc);
    }


}
