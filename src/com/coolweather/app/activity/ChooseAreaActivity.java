package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
	 *ʡ�б�
	 */
	private List<Province> provinceList;
	
	/**
	 * ���б�
	 */
	private List<City> cityList;
	
	/**
	 * ���б�
	 */
	private List<County> countyList;
	
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�е���
	 */
	private City selectedCity;
	/**
	 * ѡ�е��س�
	 */
	private County selectedCounty;
	
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView)findViewById(R.id.list_view);
		textText=(TextView)findViewById(R.id.title_text);
		
		//������
		adapter=new ArrayAdapter<String>
		            (this,android.R.layout.simple_list_item_1,dataList);
		//����������
		listView.setAdapter(adapter);
		
		//��ȡ�������ݿ����
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
					queryCities();//�����м�����
				}
				else if(currentLevel==LEVEL_CITY)
				{
					selectedCity=cityList.get(index);
					queryCounties();//�����ؼ�����
					
				}
			}
			
		});
			queryProvinces();//����ʡ������						
	}

	/*
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û����ȥ�������ϲ�ѯ
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
			//֪ͨ�����Ѿ������ı�
			adapter.notifyDataSetChanged();
			
			listView.setSelection(0);
			textText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
			
		}
		else
		{
			queryFromServer(null,"province");
		}
	}
	
	/*
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��в�ѯ�����û������ȥ�������ϲ�ѯ
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
	 * ��ѯѡ���������е��أ����ȴ����ݿ��в�ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ���ص�����
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
											//ͨ��runOnUiThread()�����ص������̶߳Կؼ�������ʾ
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
																		"����ʧ��", Toast.LENGTH_SHORT).show();
																
															}
															
														});
									}
									
								});
							}

	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog()
	{
		// TODO Auto-generated method stub
		if(progressDialog==null)
		{
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/*
	 * �رս��ȶԻ���
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
	
	
	
}