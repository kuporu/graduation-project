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

	private String url;
	private Boolean flag = false;
	private Session session = HibernateUtils.getSessionFactory().openSession();
	private static int index;// Struts2会对每一个请求,产生一个Action的实例来处理.为多实例设置为静态属性

	// page页面下翻
	public String down() {
		List<String> tableList = DataUtils.getTableName();
		ActionContext actionContext = ActionContext.getContext();
		Map<String, Object> request = (Map) actionContext.get("request");
		int indexNow = getIndex() - 1;
		if (indexNow >= 0) {
			request.put("table", tableList.get(indexNow));
			setIndex(indexNow);

			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(indexNow));
			request.put("cityCount", liCityCounts);

			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			request.put("provinceCount", lProviceCounts);
		} else {
			request.put("table", tableList.get(getIndex()));

			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(getIndex()));
			request.put("cityCount", liCityCounts);

			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			request.put("provinceCount", lProviceCounts);
		}
		return SUCCESS;
	}

	// page页面上翻
	public String up() {
		List<String> tableList = DataUtils.getTableName();
		ActionContext actionContext = ActionContext.getContext();
		Map<String, Object> request = (Map) actionContext.get("request");
		int indexNow = getIndex() + 1;

		if (indexNow < tableList.size()) {
			request.put("table", tableList.get(indexNow));
			setIndex(indexNow);

			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(indexNow));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			request.put("provinceCount", lProviceCounts);

			request.put("cityCount", liCityCounts);
		} else {
			request.put("table", tableList.get(getIndex()));

			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(getIndex()));
			request.put("cityCount", liCityCounts);

			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			request.put("provinceCount", lProviceCounts);
		}
		return SUCCESS;
	}

	// 登录
	public String login() throws Exception {
//		System.out.println(ServletActionContext.getServletContext().getInitParameter("user"));
		List<String> tableList = DataUtils.getTableName();
		ActionContext actionContext = ActionContext.getContext();
		Map<String, Object> request = (Map) actionContext.get("request");
		String firstTable = tableList.get(0);
		request.put("table", firstTable);
		setIndex(0);
		List<CityCount> liCityCounts = DataUtils.getCityCount(firstTable);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
		request.put("provinceCount", lProviceCounts);
		request.put("cityCount", liCityCounts);

		String hql = "from User where username=?1 and password=?2";// 这里有个坑，hql语句(面向对象的查询语言，和SQL类似)，其中from后接的是实体名，不是数据库表名
		Query query = session.createQuery(hql);
		HttpSession hsession = ServletActionContext.getRequest().getSession();
		hsession.setAttribute("username", username);

		query.setParameter(1, username);
		query.setParameter(2, password);

		List list = query.list();
		if (!list.isEmpty()) {// 这里有个坑，不能使用==null，null的时候还没有分配内存，空的时候是已经分配了内存只是还没有写入值
			flag = true;
		}
		if (flag)
			return SUCCESS;
		else
			return ERROR;

	}

	// 注册
	public String register() throws Exception {
		SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
		HttpSession hsession = ServletActionContext.getRequest().getSession();
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		try {
			User user = new User();
			user.setPassword(repassword);// 密码怎么用md5加密
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

	// 获取新闻源
	public String usePython() {
		if(NetworkUtils.isConnect()) {
			try {
				//删除数据库
				DataUtils.dropTable();
				//调用Python脚本获取新闻源
				String exe = "python";
				String command = "D:\\graduation\\毕业论文\\city_count\\finall_city_count_time.py";
				String[] cmdArr = new String[] { exe, command, url };
				Runtime.getRuntime().exec(cmdArr);
				return "response";
			} catch (Exception ex) {
				System.out.println("我莫名其妙的报错了");
				return ERROR;
			}
		}else {
			return "disnet";
		}
	}

	// 统计省份
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
	
	// 连接数据库,配置文件
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
}
