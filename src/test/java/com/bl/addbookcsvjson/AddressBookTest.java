package com.bl.addbookcsvjson;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AddressBookTest {
	AddressBookManage addressBookService = null;

	@Before
	public void initializeVariables() {
		addressBookService = new AddressBookManage();
	}

	//get address book data from database
	@Test
	public void givenAddressBookInDB_whenRetrieved_shouldMatchBooksCount() throws AddressBookException {
		long countEntries = addressBookService.getAddressBookDataFromDB();
		Assert.assertEquals(25, countEntries);
	}

	//get contacts data from database
	@Test
	public void givenAddressBookInDB_whenContactsRetrieved_shouldMatchContactsCount() throws AddressBookException {
		long countEntries = addressBookService.getContactsDataFromDB();
		Assert.assertEquals(14, countEntries);
	}

	// Update contact info
	@Test
	public void givenAddressBookInDB_whenContactsUpdated_shouldBeInSyncWithDB() throws AddressBookException {
		addressBookService.getContactsDataFromDB();
		addressBookService.updateContactEmailInDB("Jag", "qwerty@1223.com");
		boolean result = addressBookService.checkAddressBookContactsInSyncWithDB("Jag");
		Assert.assertTrue(result);
	}

	// contacts in given date range
	@Test
	public void givenDataRange_shouldReturnContactsFromDBAddedInDateRange() throws AddressBookException {
		addressBookService.getContactsDataFromDB();
		String start = "2019-01-01";
		String end = "2020-01-05";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByDateRangeFromDB(start, end);
		Assert.assertEquals(3, contactsDataList.size());
	}

	// contacts by city
	@Test
	public void givenAddressBookData_whenFindContactsByCity_shouldMatchContactsCount() throws AddressBookException {
		addressBookService.getContactsDataFromDB();
		String city = "Kota";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByCityFromDB(city);
		Assert.assertEquals(3, contactsDataList.size());
	}

	// contacts by state
	@Test
	public void givenAddressBookData_whenFindContactsByState_shouldMatchContactsCount() throws AddressBookException {
		addressBookService.getContactsDataFromDB();
		String state = "Rajasthan";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByStateFromDB(state);
		Assert.assertEquals(3, contactsDataList.size());
	}

	// add address book data into database
	@Test
	public void givenData_whenAdded_shouldBeInSyncWithDB() throws AddressBookException {
		addressBookService.getContactsDataFromDB();
		String date = "2017-02-05";
		String contactType = "Office";
		addressBookService.addContactsDetailsInDB("thirdBook", "Shaam","Mohan", "India" , "Mumbai",
				"MH", "100222", "12365470085", "Shyam@123.com", date, contactType);
		boolean result = addressBookService.checkAddressBookContactsInSyncWithDB("Shaam");
		Assert.assertTrue(result);
	}

	// Add multiple contacts
	@Test
	public void givenDataForMultipleContacts_whenAdded_shouldBeInSyncWithDB() throws AddressBookException {
		addressBookService.getContactsDataFromDB();
		String addressBookName = "LocalBook";
		ContactDetails[] arrayOfContacts = {
				new ContactDetails(0, "Shaam", "Mohan", "India", "Mumbai", "MH", "100222", "12365470085",
						"Shyam@123.com", "Family"),
				new ContactDetails(0, "Pinky", "Roy", "India", "Pune", "MH", "560222", "12365470085", "pinky@123.com",
						"Friend"),
				new ContactDetails(0, "Sita", "Shakti", "India", "Jaipur", "Rajasthan", "0213245", "000470085",
						"Sita@123.com", "Office") };
		Instant threadStart = Instant.now();
		addressBookService.addContactsDetailsInDBWithThreads(addressBookName, Arrays.asList(arrayOfContacts));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread: " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(14, addressBookService.countEntries());
	}
}
