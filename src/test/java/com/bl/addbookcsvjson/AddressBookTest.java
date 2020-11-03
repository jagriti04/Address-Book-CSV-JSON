package com.bl.addbookcsvjson;

import java.time.LocalDate;
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

	@Test
	public void givenAddressBookInDB_whenRetrieved_shouldMatchBooksCount() {
		long countEntries = addressBookService.getAddressBookDataFromDB();
		Assert.assertEquals(6, countEntries);
	}

	@Test
	public void givenAddressBookInDB_whenContactsRetrieved_shouldMatchContactsCount() {
		long countEntries = addressBookService.getContactsDataFromDB();
		Assert.assertEquals(13, countEntries);
	}

	// Update contact info
	@Test
	public void givenAddressBookInDB_whenContactsUpdated_shouldBeInSyncWithDB() {
		addressBookService.getContactsDataFromDB();
		addressBookService.updateContactEmailInDB("Jag", "qwerty@1223.com");
		boolean result = addressBookService.checkAddressBookContactsInSyncWithDB("Jag");
		Assert.assertTrue(result);
	}

	// contacts in given date range
	@Test
	public void givenDataRange_shouldReturnContactsFromDBAddedInDateRange() {
		addressBookService.getContactsDataFromDB();
		String start = "2019-01-01";
		String end = "2020-01-05";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByDateRangeFromDB(start, end);
		Assert.assertEquals(4, contactsDataList.size());
	}

	// contacts by city
	@Test
	public void givenAddressBookData_whenFindContactsByCity_shouldMatchContactsCount() {
		addressBookService.getContactsDataFromDB();
		String city = "Kota";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByCityFromDB(city);
		Assert.assertEquals(2, contactsDataList.size());
	}

	// contacts by state
	@Test
	public void givenAddressBookData_whenFindContactsByState_shouldMatchContactsCount() {
		addressBookService.getContactsDataFromDB();
		String state = "Rajasthan";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByStateFromDB(state);
		Assert.assertEquals(2, contactsDataList.size());
	}

	// add address book data into database
	@Test
	public void givenData_whenAdded_shouldBeInSyncWithDB() {
		addressBookService.getContactsDataFromDB();
		String date = "2017-02-05";
		String contactType = "Office";
		addressBookService.addContactsDetailsInDB("thirdBook", "Shaam","Mohan", "India" , "Mumbai",
				"MH", "100222", "12365470085", "Shyam@123.com", date, contactType);
		boolean result = addressBookService.checkAddressBookContactsInSyncWithDB("Shaam");
		Assert.assertTrue(result);
	}

}
