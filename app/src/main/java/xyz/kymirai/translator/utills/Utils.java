package xyz.kymirai.translator.utills;

import android.content.Context;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import xyz.kymirai.translator.bean.Data;

public class Utils {
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getType(int i) {
        switch (i) {
            case 1:
                return "zh-CHS";
            case 2:
                return "en";
            case 3:
                return "ja";
            default:
                return "auto";
        }
    }

    public static int getType(String i) {
        switch (i) {
            case "auto":
                return 0;
            case "zh-CHS":
                return 1;
            case "en":
            case "En":
            case "EN":
                return 2;
            case "ja":
            case "JA":
            case "Ja":
                return 3;
            default:
                return -1;
        }
    }

    public static String encode(int from, int to) {
        return getType(from) + "2" + getType(to);
    }

    private static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodestr;

    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        String temp;
        for (byte i : bytes) {
            temp = Integer.toHexString(i & 0xFF);
            if (temp.length() == 1) {
                // 1得到一位的进行补0操作
                stringBuilder.append("0");
            }
            stringBuilder.append(temp);
        }
        return stringBuilder.toString();
    }

    private final static String
            appId = "76524f347382fd74",
            appKey = "TaMNhoPMaEhx11hxKAjotFKygoVuRQQ7",
            signType = "v3";

    public interface GetRequest_Interface {
        @GET("api")
        Call<Data> getCall(
                @Query("q") String text,
                @Query("from") String from,
                @Query("to") String to,
                @Query("appKey") String appId,
                @Query("salt") String uuid,
                @Query("sign") String sign,
                @Query("signType") String signType,
                @Query("curtime") String timeStamp
        );
    }

    public static void getData(String text, String from, String to, Callback<Data> callback) {
        long l = System.currentTimeMillis();
        String salt = String.valueOf(l);
        String timeStamp = String.valueOf(l / 1000);
        String sign;

        if (text.length() > 20) {
            int length = text.length();
            sign = getSHA256(appId + text.substring(0, 10) + length + text.substring(length - 10, length) + l + timeStamp + appKey);
        } else {
            sign = getSHA256(appId + text + l + timeStamp + appKey);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openapi.youdao.com/") //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        request.getCall(text, from, to, appId, salt, sign, signType, timeStamp).enqueue(callback);
    }

    static Data getData(String text, String from, String to) {
        long l = System.currentTimeMillis();
        String salt = String.valueOf(l);
        String timeStamp = String.valueOf(l / 1000);
        String sign;

        if (text.length() > 20) {
            int length = text.length();
            sign = getSHA256(appId + text.substring(0, 10) + length + text.substring(length - 10, length) + l + timeStamp + appKey);
        } else {
            sign = getSHA256(appId + text + l + timeStamp + appKey);
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openapi.youdao.com/") //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        try {
            return request.getCall(text, from, to, appId, salt, sign, signType, timeStamp).execute().body();
        } catch (IOException e) {
            return null;
        }
    }
}
