package com.bl.addbookcsvjson;

import static org.junit.Assert.assertTrue;

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
	public void givenAddressBookInDB_whenRetrieved_ShouldMatchBooksCount() {
		long countEntries = addressBookService.getAddressBookDataFromDB();
		Assert.assertEquals(1, countEntries);
	}
	
	@Test
	public void givenAddressBookInDB_whenContactsRetrieved_ShouldMatchContactsCount() {
		long countEntries = addressBookService.getContactsDataFromDB();
		Assert.assertEquals(7, countEntries);
	}
}
