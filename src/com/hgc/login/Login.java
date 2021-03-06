package com.hgc.login;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.log4j.Logger;
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
	private static int index;// Struts2会对每一个请求,产生一个Action的实例来处理.为多实例设置为静态属性
	
	private static Logger logger = Logger.getLogger("ScriptMaint");

	// page页面下翻
	public String down() {
		List<String> tableList = DataUtils.getTableName();
		int indexNow = getIndex() - 1;
		if (indexNow >= 0) {	
			setIndex(indexNow);
			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(indexNow));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		} else {
			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(getIndex()));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		}
		return SUCCESS;
	}

	// page页面上翻
	public String up() {
		List<String> tableList = DataUtils.getTableName();
		int indexNow = getIndex() + 1;
		
		if (indexNow < tableList.size()) {
			
			setIndex(indexNow);
			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(indexNow));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		} else {
			

			List<CityCount> liCityCounts = DataUtils.getCityCount(tableList.get(getIndex()));
			List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
			
			date(lProviceCounts,liCityCounts,tableList.get(getIndex()),tableList);
		}
		return SUCCESS;
	}
	
	//日期选择
	public String choice() throws Exception {
		
		List<String> tableList = DataUtils.getTableName();			
		List<CityCount> liCityCounts = DataUtils.getCityCount(tableName);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
		
		date(lProviceCounts,liCityCounts,tableName,tableList);
		
		return SUCCESS;

	}

	// 登录
	public String login() throws Exception {
		List<String> tableList = DataUtils.getTableName();
		String firstTable = tableList.get(0);
		setIndex(0);
		List<CityCount> liCityCounts = DataUtils.getCityCount(firstTable);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);
		
		date(lProviceCounts,liCityCounts,firstTable,tableList);

		String hql = "from User where username=?1 and password=?2";// 这里有个坑，hql语句(面向对象的查询语言，和SQL类似)，其中from后接的是实体名，不是数据库表名
		Query query = session.createQuery(hql);
		HttpSession hsession = ServletActionContext.getRequest().getSession();
		hsession.setAttribute("username", username);

		query.setParameter(1, username);
		query.setParameter(2, password);

		logger.debug("登录成功了");
		
		List list = query.list();
		if (!list.isEmpty()) {// 这里有个坑，不能使用==null，null的时候还没有分配内存，空的时候是已经分配了内存只是还没有写入值
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
	
	//点击返回后跳转界面，使用login方法 session会失效，我不太清楚 :(
	public String forLogin() {
		List<String> tableList = DataUtils.getTableName();
		String firstTable = tableList.get(0);
		setIndex(0);
		List<CityCount> liCityCounts = DataUtils.getCityCount(firstTable);
		List<ProviceCount> lProviceCounts = getAndUpdateProviceCount(liCityCounts);

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

	// 调用python获取新闻源
	public String usePython() {
		if(NetworkUtils.isConnect()) {
			try {
				//删除数据库
				DataUtils.dropTable();
				
				String exe = "python";
				String command=ServletActionContext.getServletContext().getInitParameter("python");
				String[] cmdArr = new String[] { exe, command, url };
				Process pr=Runtime.getRuntime().exec(cmdArr);
				//等待子进程执行完
				pr.waitFor();
				return "response";
			} catch (Exception ex) {
				logger.debug(ex.toString());
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

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
