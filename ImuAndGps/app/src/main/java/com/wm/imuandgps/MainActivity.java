package com.wm.imuandgps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

import presenter.TransferData;
import presenter.viewImpl;
import sensor.data.GpsData;
import sensor.data.ImuData;
import sensor.data.MessageToPC;

public class MainActivity extends AppCompatActivity implements viewImpl {
    private static final String TAG = "MainActivity";

    private final int REQUEST_CODE_SETTING=1001;
    String [] permissions={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private boolean flag_btn=false;
    private Context context;

    private TextView tv_state;
    private TextView tv_latitude;
    private TextView tv_longitude;
    private TextView tv_acc;
    private TextView tv_gyro;
    private TextView tv_mag;

    private TransferData transferData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        initUI();


        if (AndPermission.hasPermission(context, permissions)) {
           // initGps();
        } else
            AndPermission.with(context)
                    .requestCode(100)
                    .permission(permissions)
                    .callback(permissionListener)
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                    // 这样避免用户勾选不再提示，导致以后无法申请权限。
                    // 你也可以不设置。
                    .rationale(rationaleListener)
                    .start();
    }
    private void initUI(){
        tv_state=(TextView)findViewById(R.id.tv_state);
        tv_acc=(TextView)findViewById(R.id.tv_acc);
        tv_gyro=(TextView)findViewById(R.id.tv_gyro);
        tv_mag=(TextView)findViewById(R.id.tv_mag);

        tv_latitude=(TextView)findViewById(R.id.tv_latitude);
        tv_longitude=(TextView)findViewById(R.id.tv_longitude);

        final Button btn_sendImu=(Button) findViewById(R.id.btn_getImu);
        btn_sendImu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag_btn){
                    btn_sendImu.setText("停止发送");
                    tv_state.setVisibility(View.VISIBLE);
                    tv_state.setText("等待客户端连接");
                    startSend();
                    flag_btn=true;
                }else{
                    btn_sendImu.setText("开始发送");
                    tv_state.setVisibility(View.GONE);
                    stopSend();
                    flag_btn=false;
                }
            }
        });
    }

    private void startSend(){
        transferData=new TransferData(context,this);
        transferData.start();
    }
    private  void stopSend(){
        if(transferData!=null)
            transferData.stop();
    }

    @Override
    public void showMessage(String messageToPC) {
        if(!TextUtils.isEmpty(messageToPC)){
            Gson gson=new Gson();
            MessageToPC msg= gson.fromJson(messageToPC,MessageToPC.class);
            GpsData gpsData=msg.getGps();
            ImuData acc=msg.getAcc();
            ImuData gyr=msg.getGyr();
            ImuData mag=msg.getMag();
            if(gpsData!=null) {
                tv_latitude.setText( "latitude  : " + gpsData.getLatitude());
                tv_longitude.setText("longitude : " +gpsData.getLongitude());
            }
            tv_acc.setText(          "acc       :\n "+acc.getX()+", "+acc.getY()+", "+acc.getZ());
            tv_gyro.setText(         "gyr       :\n "+gyr.getX()+", "+gyr.getY()+", "+gyr.getZ());
            tv_mag.setText(          "mag       :\n "+mag.getX()+", "+mag.getY()+", "+mag.getZ());

        }

    }

    @Override
    public void showState(String state) {
        if(!TextUtils.isEmpty(state))
            tv_state.setText(state);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopSend();
        super.onDestroy();
    }

    /**
     * 回调监听。
     */
    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case 100: {
                    //initGps();
                    break;
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case 100: {

                    break;
                }
            }

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(context, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this,REQUEST_CODE_SETTING).show();

                // 第二种：用自定义的提示语。
//             AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
//                     .setTitle("权限申请失败")
//                     .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
//                     .setPositiveButton("好，去设置")
//                     .show();

//            第三种：自定义dialog样式。
//            SettingService settingHandle = AndPermission.defineSettingDialog(this, REQUEST_CODE_SETTING);
//            你的dialog点击了确定调用：
//            settingHandle.execute();
//            你的dialog点击了取消调用：
//            settingHandle.cancel();
            }
        }
    };

    /**
     * Rationale支持，这里自定义对话框。
     */
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            // 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
            // AndPermission.rationaleDialog(Context, Rationale).show();

            // 自定义对话框。
            AlertDialog.newBuilder(context)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_rationale)
                    .setPositiveButton(R.string.btn_dialog_yes_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })
                    .setNegativeButton(R.string.btn_dialog_no_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    }).show();
        }
    };
}
