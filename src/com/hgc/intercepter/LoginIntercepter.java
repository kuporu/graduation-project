package com.hgc.intercepter;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;

public class LoginIntercepter extends MethodFilterInterceptor {

	@Override
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		HttpSession session=ServletActionContext.getRequest().getSession();
		System.out.println(session.getAttribute("username"));
		if(session.getAttribute("username")==null) {
			return "login";
		}
		return invocation.invoke();
	}

}
