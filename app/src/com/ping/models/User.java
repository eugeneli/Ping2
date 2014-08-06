package com.ping.models;

public class User
{
	private int id;
	private String firstName;
	private String lastName;
	private String bio;
	private String email;
	
	public static final String ID = "id";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String BIO = "bio";
	public static final String EMAIL = "email";
	
	public void setId(int userId) { id = userId; }
	public int getId() { return id; }
	
	public void setFirstName(String fname) { firstName = fname; }
	public String getFirstName() { return firstName; }
	
	public void setLastName(String lname) { lastName = lname; }
	public String getLastName() { return lastName; }
	
	public void setBio(String b) { bio = b; }
	public String getBio() { return bio; }
	
	public void setEmail(String em) { email = em; }
	public String getEmail() { return email; }
}
