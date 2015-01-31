package com.squeezymo.mutibo.model;

import java.util.ArrayList;
import java.util.Collection;

public class User {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

	private long id;

	private String login;
	private String password;
    private Collection<String> authorityNames;
	
	public User() {}
	
	public User(String login, String password) {
		this.login = login;
		this.password = password;
        this.authorityNames = new ArrayList<String>() {{ add(ROLE_USER); }};
	}
	
	public long getId() { return id; }
	public String getLogin() { return login; }
	public String getPassword() { return password; }
    public Collection<String> getAuthorityNames() { return authorityNames; }

	public void setLogin(String login) { this.login = login; }
	public void setPassword(String password) { this.password = password; }
    public void setAuthorityNames(Collection<String> authorityNames) { this.authorityNames = authorityNames; }
	
}
