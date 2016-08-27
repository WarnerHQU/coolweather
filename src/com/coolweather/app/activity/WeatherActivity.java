package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener
{
	
	/*
	 * 天气信息控件对象
	 */
	private LinearLayout weatherInfoLayout;
	
	/*
	 * 显示城市名称
	 */
	private TextView cityNameText;
	
	/*
	 * 用于显示发布时间
	 */
	private TextView publishText;
	
	/*
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	
	/*
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	

	/*
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	
	/*
	 * 用于显示当前时间
	 */
	private TextView currentDataText;
	
	
	/*
	 *切换城市按钮
	 */
	
	private Button switchCity;
	/*
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.weather_layout);
		
		//初始化各个控件
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDataText=(TextView) findViewById(R.id.current_data);
		String countyCode=getIntent().getStringExtra("county_code");
		
		//初始化按钮
		switchCity=(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		
		if(!TextUtils.isEmpty(countyCode))
		{
			//有县级代号就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}
		else
		{
			//没有县级代号就直接显示本地天气
			showWeather();
		}
		
	}
	
	
	/*
	 * 查询县级代号对应的天气代号
	 */
	private void queryWeatherCode(String countyCode)
	{
		// TODO Auto-generated method stub
		String address="http://www.weather.com.cn/data/list3/city"+
		                 countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	
	/*
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode)
	{
		// TODO Auto-generated method stub
		String address="http://www.weather.com.cn/data/cityinfo/"+
						weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}
	
	
	/*
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 */
	
	private void queryFromServer(final String address, final String type)
	{
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
									{

										public void onFinish(final String response)
										{
											// TODO Auto-generated method stub
											if("countyCode".equals(type))
											{
												//从服务器返回的数据中解析出天气代号
												String[] array=response.split("\\|");
												if(array!=null&&array.length==2)
												{
													String weatherCode=array[1];
													queryWeatherInfo(weatherCode);
													
												}
											}
											else if("weatherCode".equals(type))
											{
												//处理服务器返回的天气信息
												Utility.handleWeatherResponse(WeatherActivity.this, response);
												
												//需要对控件进行设置故要在主线程中进行,该方法回到主线程
												runOnUiThread(new Runnable()
																{

																	public void run()
																	{
																		// TODO Auto-generated method stub
																		showWeather();
																	}
																	
																});
											}
										}


										public void onError(Exception e)
										{
											// TODO Auto-generated method stub
											runOnUiThread(new Runnable()
											{

												public void run()
												{
													// TODO Auto-generated method stub
													publishText.setText("同步失败");
												}
												
											});
										}
										
									});
	}


	/*
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather()
	{
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager
				        .getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name",""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_dsp", ""));
		publishText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		currentDataText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
		Intent intent=new Intent(this,AutoUpdateService.class);
		startService(intent);
	}


	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs=PreferenceManager.
					             getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode))
			{
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}



	
}
