package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity
{
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	
	private ListView listView;
	private TextView textText;
	
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	
	/**
	 *省列表
	 */
	private List<Province> provinceList;
	
	/**
	 * 市列表
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<County> countyList;
	
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的市
	 */
	private City selectedCity;
	/**
	 * 选中的县城
	 */
	private County selectedCounty;
	
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*
		 *一开始就读取sharedPreferences文件读取city_selected标志位。
		 *如果为true则说明当前已经选择过城市了，直接进行显示即可，转到weatherActivity即可。 
		 */
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false))
		{
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			//finish();//?销毁了就不能返回了
			//return;//?
		}
		
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		
		listView=(ListView)findViewById(R.id.list_view);
		textText=(TextView)findViewById(R.id.title_text);
		
		//适配器
		adapter=new ArrayAdapter<String>
		            (this,android.R.layout.simple_list_item_1,dataList);
		//加载适配器
		listView.setAdapter(adapter);
		
		//获取天气数据库对象
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3)
			{
				// TODO Auto-generated method stub
				//Toast.makeText(ChooseAreaActivity.this, "this is a joke", Toast.LENGTH_SHORT).show();
				if(currentLevel==LEVEL_PROVINCE)
				{
					selectedProvince=provinceList.get(index);
					queryCities();//加载市级数据
				}
				else if(currentLevel==LEVEL_CITY)
				{
					selectedCity=cityList.get(index);
					queryCounties();//加载县级数据
					
				}
				else if(currentLevel==LEVEL_COUNTY)
				{
					String countyCode=countyList.get(index).getCountyCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					//finish();//销毁了就不能返回了
				}
			}
			
		});
			queryProvinces();//加载省级数据						
	}

	/*
	 * 查询全国所有的省，优先从数据库查询，如果没有再去服务器上查询
	 */
	private void queryProvinces()
	{
		// TODO Auto-generated method stub
		provinceList=coolWeatherDB.loadProvince();
		if(provinceList.size()>0)
		{
			dataList.clear();
			for(Province province:provinceList)
			{
				dataList.add(province.getProvinceName());
			}
			//通知数据已经发生改变
			adapter.notifyDataSetChanged();
			
			listView.setSelection(0);
			textText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
			
		}
		else
		{
			queryFromServer(null,"province");
		}
	}
	
	/*
	 * 查询选中省内所有的市，优先从数据库中查询，如果没有则再去服务器上查询
	 */
	protected void queryCities()
	{
		// TODO Auto-generated method stub
		cityList=coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0)
		{
			dataList.clear();
			for(City city:cityList)
			{
				dataList.add(city.getCityName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}
		else
		{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	/*
	 * 查询选中市内所有的县，优先从数据库中查询，如果没有查询到再去服务器上查询
	 */
	protected void queryCounties()
	{
		// TODO Auto-generated method stub
		countyList=coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0)
		{
			dataList.clear();
			for(County county:countyList)
			{
				dataList.add(county.getCountyName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}
		else
		{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}

	/*
	 * 根据传入的代号和类型从服务器上查询省市县的数据
	 */
	private void queryFromServer(final String code, final String type)
	{
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code))
		{
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";	
		}
		else
		{
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener()
								{

									public void onFinish(String response)
									{
										// TODO Auto-generated method stub
										boolean result=false;
										if("province".equals(type))
										{
											result=Utility.
													handleProvincesResponse(coolWeatherDB, response);
										}
										else if("city".equals(type))
										{
											result=Utility.
													handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
										}
										else if("county".equals(type))
										{
											result=Utility.
													handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
											
										}
										
										if(result)
										{
											//通过runOnUiThread()方法回调到主线程对控件进行显示
											runOnUiThread(new Runnable()
																{

																	public void run()
																	{
																		// TODO Auto-generated method stub
																		closeProgressDialog();
																		if("province".equals(type))
																		{
																			queryProvinces();
																		}
																		else if("city".equals(type))
																		{
																			queryCities();
																		}
																		else if("county".equals(type))
																		{
																			queryCounties();
																		}
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
																closeProgressDialog();
																Toast.makeText(ChooseAreaActivity.this,
																		"加载失败", Toast.LENGTH_SHORT).show();
																
															}
															
														});
									}
									
								});
							}

	/*
	 * 显示进度对话框
	 */
	private void showProgressDialog()
	{
		// TODO Auto-generated method stub
		if(progressDialog==null)
		{
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/*
	 * 关闭进度对话框
	 */
	private void closeProgressDialog()
	{
		// TODO Auto-generated method stub
		if(progressDialog!=null)
		{
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		//super.onBackPressed();
		if(currentLevel==LEVEL_COUNTY)
		{
			queryCities();
		}
		else if(currentLevel==LEVEL_CITY)
		{
			queryProvinces();
		}
		else 
		{
			finish();
		}
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("xiefengWeather", "onDestroy");
	}
	
	
	
}
