package com.bl.addbookcsvjson;

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
		Assert.assertEquals(1, countEntries);
	}
	
	@Test
	public void givenAddressBookInDB_whenContactsRetrieved_shouldMatchContactsCount() {
		long countEntries = addressBookService.getContactsDataFromDB();
		Assert.assertEquals(7, countEntries);
	}
	
	// Update contact info
	@Test
	public void givenAddressBookInDB_whenContactsUpdated_shouldBeInSyncWithDB() {
		addressBookService.getContactsDataFromDB();
		addressBookService.updateContactEmail("Jag", "qwerty@1223.com");
		boolean result = addressBookService.checkAddressBookContactsInSyncWithDB("Jag");
		Assert.assertTrue(result);
	}
	
	//contacts in given date range
	@Test
	public void givenDataRange_shouldReturnContactsFromDBAddedInDateRange() {
		addressBookService.getContactsDataFromDB();
		String start = "2019-01-01";
		String end = "2020-01-05";
		List<ContactDetails> contactsDataList = addressBookService.getContactsByDateRange(start, end);
		Assert.assertEquals(2, contactsDataList.size());
	}
}
