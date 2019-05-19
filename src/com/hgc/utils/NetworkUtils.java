package com.hgc.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetworkUtils {
	public static boolean isConnect() {  
	    Runtime runtime = Runtime.getRuntime();  
	    try {  
	        Process process = runtime.exec("ping " + "www.baidu.com");  
	        InputStream is = process.getInputStream();  
	        InputStreamReader isr = new InputStreamReader(is);  
	        BufferedReader br = new BufferedReader(isr);  
	        String line = null;  
	        StringBuffer sb = new StringBuffer();  
	        while ((line = br.readLine()) != null) {  
	            sb.append(line);
	        }  
	        is.close();  
	        isr.close();  
	        br.close();  
	        if (null != sb && !sb.toString().equals("")) {  
	            String logString = "";  
	            if (sb.toString().indexOf("TTL") > 0) {  
	               // ÍøÂç³©Í¨  
	              return true;
	            } else {  
	                // ÍøÂç²»³©Í¨    
	              return false;
	            }    
	        }else {
	        	return false;
	        }
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return false;
	    }  
	}
	
//	public static void main(String[] args) {
//		boolean bool=NetworkUtils.isConnect();
//		System.out.println(bool);
//	}
}
