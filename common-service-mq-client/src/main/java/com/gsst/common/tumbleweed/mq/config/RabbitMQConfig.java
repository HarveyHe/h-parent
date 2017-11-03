package com.gsst.common.tumbleweed.mq.config;

import java.io.Serializable;

public class RabbitMQConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String host;
	private String username;
	private String password;
	private Integer port;
	private String virtualHost;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
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
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getVirtualHost() {
		return virtualHost;
	}
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}
	
}
