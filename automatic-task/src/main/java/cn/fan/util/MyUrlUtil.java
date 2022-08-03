package cn.fan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MyUrlUtil {
    public static String getUrlStr(String urlStr){
        String data="";
        try{
            URL url=new URL(urlStr);

            try {
                InputStream is = url.openStream();
                InputStreamReader isr = new InputStreamReader(is, "utf-8");

                BufferedReader br = new BufferedReader(isr);
                data = br.readLine();

                br.close();
                isr.close();
                is.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return data;
    }
}
