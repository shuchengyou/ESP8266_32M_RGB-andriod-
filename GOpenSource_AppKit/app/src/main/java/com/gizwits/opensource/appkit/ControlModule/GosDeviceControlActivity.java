package com.gizwits.opensource.appkit.ControlModule;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ConcurrentHashMap;

import scyou.colorseerbarpicker_master.SeekBarColorPicker;
import com.gizwits.opensource.appkit.R;

public class GosDeviceControlActivity extends GosBaseActivity {
	/** The GizWifiDevice device */
	private GizWifiDevice mDevice;

	/** The ActionBar actionBar */
	ActionBar actionBar;
	//临时存储回调结果
	ConcurrentHashMap<String, Object> mtempDataSend;

	private int rgb_Data;

    private SeekBarColorPicker mSeekBarColorPicker;
    private TextView tvShow;
    private CheckBox cb_pwr;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			//更新UI
			if(message.what == 101){
			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_device_control);
		initDevice();
		setActionBar(true, true, mDevice.getProductName());
		initView();
	}

	private void initView() {
        tvShow = (TextView) findViewById(R.id.tvShow);
        mSeekBarColorPicker = (SeekBarColorPicker) findViewById(R.id.mSeekBarColorPicker);
        mSeekBarColorPicker.setSeekBarColorPickerChangeListener(new SeekBarColorPicker.SeekBarColorPickerChangeListener() {
            @Override
            public void onProgressChange(SeekBarColorPicker seekBarColorPicker, int color, String htmlRgb) {
				mtempDataSend = new ConcurrentHashMap<>();
				findViewById(R.id.mViewResult).setBackgroundColor(color);
				tvShow.setText("采集结果：" + htmlRgb);

				rgb_Data = (Color.red(color) << 16) + (Color.green(color) << 8) + Color.blue(color);
				mtempDataSend.put("RGB_DATA",rgb_Data);
				mDevice.write(mtempDataSend,5);
				Log.i("--w", "下发命令：" + mtempDataSend.toString());
            }
        });

		cb_pwr = (CheckBox) findViewById(R.id.cb_pwr);
		cb_pwr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				mtempDataSend = new ConcurrentHashMap<>();
				if(b){
					mtempDataSend.put("RGB_PWR",false);
				} else{
					mtempDataSend.put("RGB_PWR",true);
				}
				mDevice.write(mtempDataSend,5);
			}
		});

	}

	private void initDevice() {
		Intent intent = getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(mListener);
	}

	private GizWifiDeviceListener mListener = new GizWifiDeviceListener(){
		@Override
		public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
			super.didReceiveData(result, device, dataMap, sn);
			Log.e("==w","显示从云端发送过来的数据：" + dataMap.toString());
			//先判断是否为正确回调
			if(result == GizWifiErrorCode.GIZ_SDK_SUCCESS){
				//首先从回调的数据中判断这个回调设备是否为当前界面的设备，通过唯一的mac地址
				if(device.getMacAddress().equals(mDevice.getMacAddress())){
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.devices_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_getMessage:
            Toast.makeText(GosDeviceControlActivity.this,"设备的PK值：" + mDevice.getProductKey(),Toast.LENGTH_SHORT).show();
			break;

        case R.id.item_Rename:
            View inflate = LayoutInflater.from(GosDeviceControlActivity.this).inflate(R.layout.dialog_rename,null);
            break;
		}
		return super.onOptionsItemSelected(item);
	}

    public void btBlue(View view) {
		mtempDataSend = new ConcurrentHashMap<>();
		rgb_Data = 0x01DDFF;
        //#01DDFF
        mSeekBarColorPicker.setColorByhtmlRGB("#01DDFF");
		tvShow.setText("采集结果：" + "#01DDFF");
		findViewById(R.id.mViewResult).setBackgroundColor(Color.parseColor("#01DDFF"));
		mtempDataSend.put("RGB_DATA",rgb_Data);
		mDevice.write(mtempDataSend,5);
    }

    public void btGreen(View view) {
		mtempDataSend = new ConcurrentHashMap<>();
		rgb_Data = 0x98CB00;
        //#98CB00
        mSeekBarColorPicker.setColorByhtmlRGB("#98CB00");
		tvShow.setText("采集结果：" + "#98CB00");
		findViewById(R.id.mViewResult).setBackgroundColor(Color.parseColor("#98CB00"));
		mtempDataSend.put("RGB_DATA",rgb_Data);
		mDevice.write(mtempDataSend,5);
    }

    public void btRed(View view) {
		mtempDataSend = new ConcurrentHashMap<>();
		rgb_Data = 0xff4444;
        //#FF4444
        mSeekBarColorPicker.setColorByhtmlRGB("#ff4444");
		tvShow.setText("采集结果：" + "#ff4444");
		findViewById(R.id.mViewResult).setBackgroundColor(Color.parseColor("#ff4444"));
		mtempDataSend.put("RGB_DATA",rgb_Data);
		mDevice.write(mtempDataSend,5);
    }
}
