package com.bl.addbookcsvjson;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
 
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AddressBookRestTest {
	AddressBookManage addressBookService = null;
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
		ContactDetails[] arrayOfContacts = getContactsList();
		addressBookService = new AddressBookManage(new ArrayList<>(Arrays.asList(arrayOfContacts)));
	}

	private ContactDetails[] getContactsList() {
		Response response = RestAssured.get("/contacts");
		ContactDetails[] arrayOfContacts = new Gson().fromJson(response.asString(), ContactDetails[].class);
		return arrayOfContacts;
	}
	
	
	// Read data from JSON server
	@Test
	public void givenEmployeeDataInJSONServer_whenRetrieved_shouldReturnCorrectEntryCount() {
		long entries = addressBookService.countEntries();
		Assert.assertEquals(1, entries);
	}
}
