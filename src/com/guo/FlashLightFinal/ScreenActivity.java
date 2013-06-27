package com.guo.FlashLightFinal;


import com.guo.Constant.Constant.ColorMsg;
import com.guo.Custom.SeekBar.OnSeekBarChangeListener;
import com.guo.FlashLightFinal.R;

import android.media.ExifInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class ScreenActivity extends Activity {
	private RelativeLayout screenbackgroud;
	private int screencolor;
	private Button back;
	private boolean  onTouch=true;
	private com.guo.Custom.SeekBar seekbar;
	private int  progress_up=100;
	private TextView liangdu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			//设置全屏
		  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		     //去掉标题栏   
		  requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.activity_screen);
		Intent intent=getIntent();
		screencolor=intent.getIntExtra("screencolor", ColorMsg.WHITE);
		initView();
		setListener();
		setBackgroundColor();
	}
	private class BackOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	}
	private class ScreenOnTouchListener implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(onTouch){
				seekbar.setVisibility(View.VISIBLE);
				liangdu.setVisibility(View.VISIBLE);
				onTouch=false;
			}else{
			    seekbar.setVisibility(View.INVISIBLE);
			    liangdu.setVisibility(View.INVISIBLE);
				onTouch=true;
			}
			return false;
		}
	}
	private class ScreenLightOnSeekBarChange implements OnSeekBarChangeListener{
		@Override
		public void onProgressChanged(com.guo.Custom.SeekBar VerticalSeekBar,
				int progress, boolean fromUser) {
			// TODO Auto-generated method stub
			progress_up=100-progress;
			System.out.println("progress--->"+progress_up);
			setScreenBrightness(progress_up);
			liangdu.setText("亮度:"+progress_up+"%");
		}
		@Override
		public void onStartTrackingTouch(com.guo.Custom.SeekBar VerticalSeekBar) {
		}
		@Override
		public void onStopTrackingTouch(com.guo.Custom.SeekBar VerticalSeekBar) {
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	private void initView(){
		screenbackgroud=(RelativeLayout)findViewById(R.id.relative_screen);
		back=(Button)findViewById(R.id.back);
		seekbar=(com.guo.Custom.SeekBar)findViewById(R.id.screenlight_seekbar);
		seekbar.setVisibility(View.INVISIBLE);
		liangdu=(TextView)findViewById(R.id.liangdu);
		liangdu.setVisibility(View.INVISIBLE);
	}
	private void setListener(){
		back.setOnClickListener(new BackOnClickListener());
		screenbackgroud.setOnTouchListener(new ScreenOnTouchListener());
		seekbar.setOnSeekBarChangeListener(new ScreenLightOnSeekBarChange());
	}
	private void setBackgroundColor(){
		if(screencolor==ColorMsg.RED){
			screenbackgroud.setBackgroundResource(R.color.red);
		}else if(screencolor==ColorMsg.BLACK){
			screenbackgroud.setBackgroundResource(R.color.black);
		}else if(screencolor==ColorMsg.BLUE){
			screenbackgroud.setBackgroundResource(R.color.blue);
		}else if(screencolor==ColorMsg.GREEN){
			screenbackgroud.setBackgroundResource(R.color.green);
		}else if(screencolor==ColorMsg.ORANGE){
			screenbackgroud.setBackgroundResource(R.color.orange);
		}else if(screencolor==ColorMsg.PURPLE){
			screenbackgroud.setBackgroundResource(R.color.purple);
		}else if(screencolor==ColorMsg.WHITE){
			screenbackgroud.setBackgroundResource(R.color.white);
		}
	}
	/** 
     * 获得当前屏幕亮度的模式     
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度 
     */  
      private int getScreenMode(){  
        int screenMode=0;  
        try{  
            screenMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);  
        }  
        catch (Exception localException){  
        }  
        return screenMode;  
      }  
     /** 
     * 获得当前屏幕亮度值  0--255 
     */  
      private int getScreenBrightness(){  
        int screenBrightness=100;  
        try{  
            screenBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);  
        }  
        catch (Exception localException){  
            
        }  
        return screenBrightness;  
      }  
    /** 
     * 设置当前屏幕亮度的模式     
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度 
     */  
      private void setScreenMode(int paramInt){  
        try{  
          Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);  
        }catch (Exception localException){  
          localException.printStackTrace();  
        }  
      }  
      /** 
       * 设置当前屏幕亮度值  0--100
       * 
       */  
      private void saveScreenBrightness(int paramInt){  
        try{  
          Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);  
        }  
        catch (Exception localException){  
          localException.printStackTrace();  
        }  
      }  
      /** 
       * 保存当前的屏幕亮度值，并使之生效 
       */  
      private void setScreenBrightness(float screenBrightness2){  
        Window localWindow = getWindow();  
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();  
        float f = screenBrightness2 / 50; 
        localLayoutParams.screenBrightness = f;  
        localWindow.setAttributes(localLayoutParams);  
      }

}
