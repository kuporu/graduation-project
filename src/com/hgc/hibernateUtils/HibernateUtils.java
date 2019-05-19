package com.hgc.hibernateUtils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
	private static final SessionFactory sessionFactory;
	static {
		Configuration cfg=new Configuration().configure();
		sessionFactory=cfg.buildSessionFactory();
	}
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public static void main(String[] args) {
		
	}
}
