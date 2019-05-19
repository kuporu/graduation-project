package com.hgc.entity;

import java.io.Serializable;

public class ProviceCount implements Serializable {
	private String provice;
	private int count;
	public String getProvice() {
		return provice;
	}
	public void setProvice(String provice) {
		this.provice = provice;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public ProviceCount(String provice, int count) {
		super();
		this.provice = provice;
		this.count = count;
	}
	public ProviceCount() {
		super();
		// TODO Auto-generated constructor stub
	}
}
