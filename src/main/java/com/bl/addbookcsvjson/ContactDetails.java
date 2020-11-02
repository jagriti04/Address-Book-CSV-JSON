package com.bl.addbookcsvjson;

import com.opencsv.bean.CsvBindByName;

public class ContactDetails {
	public int contact_id;
	
	@CsvBindByName
	public String firstName, lastName, address, city, state, email;

	@CsvBindByName
	public String zip;

	@CsvBindByName
	public String phoneNo;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	public ContactDetails() { }
	
	public ContactDetails(String fName, String lName, String address, String city, String state, String zip, String phoneNo,
			String email) {
		this.firstName = fName;
		this.lastName = lName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNo = phoneNo;
		this.email = email;
	}
	
	public ContactDetails(int id, String fName, String lName, String address, String city, String state, String zip, String phoneNo,
			String email) {
		this(fName, lName, address, city, state, zip, phoneNo, email);
		this.contact_id = id;
	}

	@Override
	public String toString() {
		return String.format("Name: " + firstName + " " + lastName + ", Address: " + address + ", City: " + city
				+ ", State: " + state + ", zip " + zip + ", phone no. " + phoneNo + ", EmailId: " + email);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ContactDetails that = (ContactDetails) o;
		return contact_id == that.contact_id && email.equals(that.email) && firstName.equals(that.firstName);
	}
}