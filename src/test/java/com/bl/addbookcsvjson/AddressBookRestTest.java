package com.bl.addbookcsvjson;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookRestTest {
	AddressBookManage addressBookService = null;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
		ContactDetails[] arrayOfContacts = getContactsList();
		addressBookService = new AddressBookManage(new ArrayList<>(Arrays.asList(arrayOfContacts)));
	}

	// to get contacts from JSON server
	private ContactDetails[] getContactsList() {
		Response response = RestAssured.get("/contacts");
		ContactDetails[] arrayOfContacts = new Gson().fromJson(response.asString(), ContactDetails[].class);
		return arrayOfContacts;
	}

	// to add contacts to JSON server
	private Response addContactToJsonServer(ContactDetails contact) {
		String contactJson = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.post("/contacts");
	}

	// Read data from JSON server
	@Test
	public void givenContactDataInJSONServer_whenRetrieved_shouldReturnCorrectEntryCount() {
		long entries = addressBookService.countEntries();
		Assert.assertEquals(2, entries);
	}

	// Write data to JSON server
	@Test
	public void givenNewContactDetails_whenAdded_shouldMatch201ResponseAndCount() {
		ContactDetails contact = new ContactDetails(0, "Robin", "Raj", "India", "Kanpur", "UP", "258741", "123654789", "raj@123.in");
		Response response = addContactToJsonServer(contact);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		contact = new Gson().fromJson(response.asString(), ContactDetails.class);
		
		addressBookService.addContactDetailsJSONServer(contact);
		long entries = addressBookService.countEntries();
		Assert.assertEquals(2, entries);
	}

}
