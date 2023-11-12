package org.pet.home.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.pet.home.entity.AddressInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @description:
 * @author: 22866
 * @date: 2023/11/12
 **/
public class GaoDeMapUtil {
    public static final String KEY = "a74fa72290a51819932e8fc1b1129c34";
    public static final String URL = "https://restapi.amap.com/v3/geocode/geo?address=";

    public static AddressInfo getLngAndLag(String address) throws UnsupportedEncodingException {
        address = address.trim();
        String url = URL + URLEncoder.encode(address, "utf-8") + "&output=JSON" + "&key="+ KEY;

        try {
            URL url2 = new URL(url);    // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();// 打开连接
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
            JSONObject a = JSON.parseObject(sb.toString());
            JSONArray sddressArr = JSON.parseArray(a.get("geocodes").toString());
            JSONObject c = JSON.parseObject(sddressArr.get(0).toString());

            String formattedAddress = c.get("formatted_address").toString();
            String country = c.get("country").toString();
            String province = c.get("province").toString();
            String cityCode = c.get("citycode").toString();
            String city = c.get("city").toString();
            String district = c.get("district").toString();
            String adCode = c.get("adcode").toString();
            String location = c.get("location").toString();

            return new AddressInfo(formattedAddress, country, province, cityCode, city, district, adCode, location);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
        return null;
    }
}
