package com.hgc.login;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.hgc.entity.CityCount;
import com.hgc.entity.ProviceCount;
import com.hgc.entity.User;
import com.hgc.hibernateUtils.HibernateUtils;
import com.hgc.utils.DataUtils;
import com.hgc.utils.GetJedis;
import com.hgc.utils.NetworkUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import redis.clients.jedis.Jedis;

public class Login extends ActionSupport {
	private String username;
	private String password;
	private String reusername;
	private String repassword;
	private String tableName;

	private String url;
	private Boolean flag = false;
	private Session session = HibernateUtils.getSessionFactory().openSession();
	private static int index;// Struts2���ÿһ������,����һ��Action��ʵ��������.Ϊ��ʵ������Ϊ��̬����

	// pageҳ���·�
	public String down() {
		List<String> tableList = DataUtils.getTableName();
//		ActionContext actionContext = ActionContext.getContext();
//		Map<String, Object> request = (Map) actionContext.get("request");
		int indexNow = getIndex() - 1;
		if (indexNow >= 0) {	
			setIndex(indexNow);
			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(indexNow));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
//			request.put("table", tableList.get(indexNow));
//			request.put("cityCount", liCityCounts);
//			request.put("tables", tableList);
//			request.put("provinceCount", lProviceCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		} else {
			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(getIndex()));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
//			request.put("table", tableList.get(getIndex()));
//			request.put("cityCount", liCityCounts);
//			request.put("tables", tableList);
//			request.put("provinceCount", lProviceCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		}
		return SUCCESS;
	}

	// pageҳ���Ϸ�
	public String up() {
		List<String> tableList = DataUtils.getTableName();
//		ActionContext actionContext = ActionContext.getContext();
//		Map<String, Object> request = (Map) actionContext.get("request");
		int indexNow = getIndex() + 1;
		
		if (indexNow < tableList.size()) {
			
			setIndex(indexNow);
			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(indexNow));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
//			request.put("table", tableList.get(indexNow));
//			request.put("tables", tableList);
//			request.put("provinceCount", lProviceCounts);
//			request.put("cityCount", liCityCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		} else {
			

			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(getIndex()));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
//			request.put("cityCount", liCityCounts);
//			request.put("tables", tableList);
//			request.put("table", tableList.get(getIndex()));
//			request.put("provinceCount", lProviceCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		}
		return SUCCESS;
	}
	
	//����ѡ��
	public String choice() throws Exception {
		
		List<String> tableList = DataUtils.getTableName();
		
//		ActionContext actionContext = ActionContext.getContext();
//		Map<String, Object> request = (Map) actionContext.get("request");
			
		List<CityCount> liCityCounts = DataUtils.getCityCount(tableName);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
		
//		request.put("tables", tableList);
//		request.put("table", tableName);
//		request.put("provinceCount", lProviceCounts);
//		request.put("cityCount", liCityCounts);
		date(lProviceCounts,liCityCounts,tableName,tableList);
		
		return SUCCESS;

	}

	// ��¼
	public String login() throws Exception {
		List<String> tableList = DataUtils.getTableName();
//		ActionContext actionContext = ActionContext.getContext();
//		Map<String, Object> request = (Map) actionContext.get("request");
		String firstTable = tableList.get(0);
		setIndex(0);
		List<CityCount> liCityCounts = DataUtils.getCityCount(firstTable);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
		
//		request.put("provinceCount", lProviceCounts);
//		request.put("cityCount", liCityCounts);
//		request.put("table", firstTable);
//		request.put("tables", tableList);
		
		date(lProviceCounts,liCityCounts,firstTable,tableList);

		String hql = "from User where username=?1 and password=?2";// �����и��ӣ�hql���(�������Ĳ�ѯ���ԣ���SQL����)������from��ӵ���ʵ�������������ݿ����
		Query query = session.createQuery(hql);
		HttpSession hsession = ServletActionContext.getRequest().getSession();
		hsession.setAttribute("username", username);

		query.setParameter(1, username);
		query.setParameter(2, password);

		List list = query.list();
		if (!list.isEmpty()) {// �����и��ӣ�����ʹ��==null��null��ʱ��û�з����ڴ棬�յ�ʱ�����Ѿ��������ڴ�ֻ�ǻ�û��д��ֵ
			flag = true;
		}
		
		HttpSession session=ServletActionContext.getRequest().getSession();
		if(session.getAttribute("username")!=null) {
			flag = true;
		}
		
		if (flag)
			return SUCCESS;
		else
			return ERROR;

	}
	
	//������غ���ת���棬ʹ��login���� session��ʧЧ���Ҳ�̫��� :(
	public String forLogin() {
		List<String> tableList = DataUtils.getTableName();
//		ActionContext actionContext = ActionContext.getContext();
//		Map<String, Object> request = (Map) actionContext.get("request");
		String firstTable = tableList.get(0);
		setIndex(0);
		List<CityCount> liCityCounts = DataUtils.getCityCount(firstTable);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
		
//		request.put("provinceCount", lProviceCounts);
//		request.put("cityCount", liCityCounts);
//		request.put("table", firstTable);
//		request.put("tables", tableList);
		date(lProviceCounts,liCityCounts,firstTable,tableList);
		
		return SUCCESS;
	}
	
	private void date(List<ProviceCount> lProviceCounts,List<CityCount> liCityCounts,String table,List<String> tables) {
		ActionContext actionContext = ActionContext.getContext();
		Map<String, Object> request = (Map) actionContext.get("request");
		request.put("provinceCount", lProviceCounts);
		request.put("cityCount", liCityCounts);
		request.put("table", table);
		request.put("tables", tables);
	}

	// ע��
	public String register() throws Exception {
		SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
		HttpSession hsession = ServletActionContext.getRequest().getSession();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		try {
			User user = new User();
			user.setPassword(repassword);// ������ô��md5����
			user.setUsername(reusername);

			hsession.setAttribute("username", reusername);
			hsession.setAttribute("userpassword", repassword);
			session.save(user);
			session.getTransaction().commit();
//			return SUCCESS;
			return LOGIN;
		} catch (Exception ex) {
			session.getTransaction().rollback();
			return ERROR;
		} finally {
			session.close();
		}
	}

	private static List<ProviceCount> liProvice;

	static {
		try {
			String sql_get_province = "select p.province from provincecount p";
			Connection conn = getConnetion();
			PreparedStatement preparedStatement = conn.prepareStatement(sql_get_province);
			ResultSet resultSet = preparedStatement.executeQuery();

			List<ProviceCount> liProviceCounts = new ArrayList<>();
			while (resultSet.next()) {
				ProviceCount proviceCount = new ProviceCount();
				proviceCount.setProvice(resultSet.getString("province"));
				proviceCount.setCount(0);
				liProviceCounts.add(proviceCount);
			}
			conn.close();
			liProvice = liProviceCounts;
		} catch (Exception ex) {

		}
	}

	// ��ȡ����Դ
	public String usePython() {
		if(NetworkUtils.isConnect()) {
			try {
				//ɾ�����ݿ�
				DataUtils.dropTable();
				//����Python�ű���ȡ����Դ
				String exe = "python";
				String command = "D:\\graduation\\��ҵ����\\city_count\\finall_city_count_time.py";
				String[] cmdArr = new String[] { exe, command, url };
				Runtime.getRuntime().exec(cmdArr);
				return "response";
			} catch (Exception ex) {
				System.out.println("��Ī������ı�����");
				return ERROR;
			}
		}else {
			return "disnet";
		}
	}

	// ͳ��ʡ��
	public List<ProviceCount> getAndUpdateProviceCount(List<CityCount> listCityCount) {
		List<ProviceCount> liProviceCounts = liProvice;
		List<ProviceCount> liCounts = new ArrayList<ProviceCount>();

		for (ProviceCount proviceCount : liProviceCounts) {
			int num = 0;
			String provice = proviceCount.getProvice();

			ProviceCount proviceCount1 = new ProviceCount();
			proviceCount1.setProvice(provice);

			for (CityCount cityCount : listCityCount) {
				String city = cityCount.getCity();

				if (city.startsWith(provice)) {
					num += cityCount.getCount();
					proviceCount1.setCount(num);
				} else {
					continue;
				}

			}
			liCounts.add(proviceCount1);

		}
		return DataUtils.removeZero(liCounts);
	}
	
	// �������ݿ�,�����ļ�
		public static Connection getConnetion() {
			String driver = "com.mysql.cj.jdbc.Driver";
			String host=ServletActionContext.getServletContext().getInitParameter("host");
			String database=ServletActionContext.getServletContext().getInitParameter("database");
//			String url = "jdbc:mysql://localhost:3306/graduation?serverTimezone=UTC";
			String url = "jdbc:mysql://"+host+"/"+database+"?serverTimezone=UTC";
//			String user = "root";
			String user=ServletActionContext.getServletContext().getInitParameter("user");
//			String password = "hgc123";
			String password=ServletActionContext.getServletContext().getInitParameter("password");

			try {
				Class.forName(driver);
				Connection connection = DriverManager.getConnection(url, user, password);
				return connection;
			} catch (Exception ex) {
				return null;
			}
		}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReusername() {
		return reusername;
	}

	public void setReusername(String reusername) {
		this.reusername = reusername;
	}

	public String getRepassword() {
		return repassword;
	}

	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		Login.index = index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
