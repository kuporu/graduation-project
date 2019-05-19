package com.hgc.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hgc.entity.CityCount;
import com.hgc.entity.ProviceCount;
import com.hgc.login.Login;

public class DataUtils extends HttpServlet {
	
	// �������ݿ��������ȡweb.xml�еĲ���ʧ�ܣ��������ݿ������ҷ�����action��
//	public static Connection getConnetion() {
//		String driver = "com.mysql.cj.jdbc.Driver";
//		String url = "jdbc:mysql://localhost:3306/graduation?serverTimezone=UTC";
//		String user = "root";
//		String password = "hgc123";
//
//		try {
//			Class.forName(driver);
//			Connection connection = DriverManager.getConnection(url, user, password);
//			return connection;
//		} catch (Exception ex) {
//			return null;
//		}
//	}

	// ��ȡʱ���ļ���
	public static List<String> getTableName() {
		try {
			String sql = "select table_name from information_schema.tables where table_schema='graduation'";
			Connection conn = Login.getConnetion();
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			List<String> tableList = new ArrayList<String>();
			while (resultSet.next()) {
				String city = resultSet.getString("TABLE_NAME");
				if (city.indexOf("��") != -1) {
					tableList.add(city);
				}
			}
			conn.close();
			return tableList;
		} catch (Exception ex) {
			return null;
		}
	}

	// ��ȡ��Ӧʱ����г������ƣ���������γ�ȼ���
	public static List<CityCount> getCityCount(String dbName) {
		try {
			String sql = "select cc.city,cc.count,c.lat,c.lng from " + dbName
					+ " cc inner join city c on cc.city like \"%\"||c.name||\"%\" where c.name=substring(cc.city,4) or c.name=substring(cc.city,3) or c.name=cc.city";
			Connection conn = Login.getConnetion();
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet resultSet = preparedStatement.executeQuery();
			List<CityCount> liCityCounts = new ArrayList<>();
			while (resultSet.next()) {
				CityCount cityCount = new CityCount();
				String city = resultSet.getString("city");
				int count = resultSet.getInt("count");
				double lat = resultSet.getDouble("lat");
				double lng = resultSet.getDouble("lng");
				cityCount.setLat(lat);
				cityCount.setLng(lng);
				cityCount.setCity(city);
				cityCount.setCount(count);
				liCityCounts.add(cityCount);
			}
			conn.close();
			return liCityCounts;
		} catch (Exception ex) {
			return null;
		}
	}
	
	//ɾ����һ������ʱ������ȡ���ݱ�
		public static void dropTable() {
			Connection conn= Login.getConnetion();
			List<String> dateTable = DataUtils.getTableName();
			try {
				Statement statement=conn.createStatement();

				for(int i=0;i<dateTable.size();i++) {
					String sql_drop_time_table="drop table "+dateTable.get(i)+"";
					statement.executeUpdate(sql_drop_time_table);
				}			
				conn.close();
			}catch(Exception ex) {
				System.out.println("����ɾ�����ʱ�򱨴���");
			}
		}
		
		//ȥ��countΪ0,����ʹ�õ���
		public static List<ProviceCount> removeZero(List<ProviceCount> listProviceCounts){		
			Iterator<ProviceCount> iterator=listProviceCounts.iterator();
			List<ProviceCount> list=new ArrayList<ProviceCount>();
			while(iterator.hasNext()) {
				ProviceCount proviceCount=iterator.next();
				if(proviceCount.getCount()==0) {
					iterator.remove();
				}
				else {
					list.add(proviceCount);
				}
			}
			
			return list;
		}
		
		
}
