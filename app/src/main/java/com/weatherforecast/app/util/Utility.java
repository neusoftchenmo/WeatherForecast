package com.weatherforecast.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.weatherforecast.app.model.City;
import com.weatherforecast.app.model.County;
import com.weatherforecast.app.model.Province;
import com.weatherforecast.app.model.WeatherForecastDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chenmo on 2015/5/24.
 */
public class Utility {
    /*
    * 解析和处理服务器返回的省级数据
    * */
    public synchronized  static  boolean handleProvincesResponse(WeatherForecastDB weatherForecastDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces =response.split(",");
            if(allProvinces!= null && allProvinces.length>0){
                for(String p: allProvinces){
                    //Log.i("handleProvincesResponse", "p=" + p);
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);

                    // 将解析出来的数据存储到province表
                    weatherForecastDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /*
    *   解析和处理服务器返回的市级数据
    * */

    public static  boolean handleCitiesResponse(WeatherForecastDB weatherForecastDB,String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities!=null&&allCities.length>0){
                for(String c: allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    Log.i("handleCitiesResponse", array[0] + ":" + array[1]);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的数据存储到city表
                    weatherForecastDB.saveCity(city);
                }
                return  true;
            }
        }
        return  false;
    }
    /*
    * 解析和处理服务器返回县级数据
    * */
    public  static  boolean handleCountiesResponse(WeatherForecastDB weatherForecastDB,String response,int cityId){

        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0){
                for (String c:allCounties){
                    String[] array = c.split("\\|");
                    Log.i("handleCountiesResponse", array[0] + ":" + array[1]);
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    // 将解析出来的数据存储到County表
                    weatherForecastDB.saveCounty(county);
                }
                return  true;
            }
        }
        return  false;
    }

    /*
       解析服务器返回的JSON数据 并将解析出来的数据库存储到本地
     */
    public static  void handleWeatherResponse(Context context, String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            Log.d("handleWeatherResponse", response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");

            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*
       将服务器返回的所有天气信息存储到SharedPreferences文件中
     */

    public static  void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                        String temp1, String temp2, String weatherDesp,String publishTime){

        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_Time", publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}




