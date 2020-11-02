package com.bl.addbookcsvjson;

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
}
