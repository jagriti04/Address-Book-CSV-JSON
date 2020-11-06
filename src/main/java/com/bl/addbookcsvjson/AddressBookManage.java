package com.bl.addbookcsvjson;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class AddressBookManage {

	private Map<String, AddressBook> nameToAddressBookMap;
	public List<String> valuePrinted;
	private int countPerson;
	private AddressBookDBService addressBookDBService = new AddressBookDBService();
	private List<ContactDetails> contactsList = new ArrayList<ContactDetails>();

	public AddressBookManage() {
		nameToAddressBookMap = new HashMap<>();
		valuePrinted = new ArrayList<>();
	}

	public AddressBookManage(List<ContactDetails> contactsList) {
		this();
		this.contactsList = contactsList;
	}

	public void addAddressBook(String addBookName, AddressBook addBook) {
		nameToAddressBookMap.put(addBookName, addBook);
	}

	public boolean createAddBooks(Scanner input)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		System.out.println("Enter the num of address books to create");
		int num = input.nextInt();
		input.nextLine();

		for (int i = 0; i < num; i++) {
			int serialNo = i + 1;
			System.out.println("Enter the name of add. book num. " + serialNo);
			String aBookName = input.nextLine();
			AddressBook addBookObj = new AddressBook();
			addBookObj = addBookObj.addressBookOption();
			addBookObj.setAddressBookName(aBookName);
			addAddressBook(aBookName, addBookObj);
		}
		return true;
	}

	public void viewAddBooks() {
		for (int i = 0; i < nameToAddressBookMap.size(); i++) {
			System.out.println(i + "Name of add. book are : " + nameToAddressBookMap);
		}
	}

	public void findPersonByCity(String cityName) {
		try {
			nameToAddressBookMap.forEach((key, addresBookValue) -> {

				valuePrinted = addresBookValue.cityToPersonsMap.get(cityName).stream()
						.peek(n -> System.out.println("person names: " + n)).collect(Collectors.toList());
			});
			countPerson = valuePrinted.size();
		} catch (NullPointerException e) {
			System.out.println("no person in the city");
		}
	}

	public void findPersonByState(String stateName) {
		try {
			nameToAddressBookMap.forEach((key, addresBookValue) -> {
				valuePrinted = addresBookValue.cityToPersonsMap.get(stateName).stream()
						.peek(n -> System.out.println("person names: " + n)).collect(Collectors.toList());
			});
			countPerson = valuePrinted.size();
		} catch (NullPointerException e) {
			System.out.println("no person in the state");
		}

	}

	// database reading
	public long getAddressBookDataFromDB() {
		long count = addressBookDBService.readAddressBookDB();
		return count;
	}

	public long getContactsDataFromDB() {
		contactsList = addressBookDBService.readContactDetailsTable();
		return contactsList.size();
	}

	private ContactDetails getContactsDataFromDB(String contactName) {
		return this.contactsList.stream().filter(contact -> contact.getFirstName().equals(contactName)).findFirst()
				.orElse(null);
	}

	public List<ContactDetails> getContactsByDateRangeFromDB(String start, String end) {
		this.contactsList = addressBookDBService.getContactsWithStartDateInGivenRange(start, end);
		return this.contactsList;
	}

	public List<ContactDetails> getContactsByCityFromDB(String city) {
		this.contactsList = addressBookDBService.getContactsDataByCity(city);
		return this.contactsList;
	}

	public List<ContactDetails> getContactsByStateFromDB(String state) {
		this.contactsList = addressBookDBService.getContactsDataByState(state);
		return this.contactsList;
	}

	public void updateContactEmailInDB(String contactName, String email) {
		int result = addressBookDBService.updateEmployeeData(contactName, email);
		if (result == 0)
			return;
		ContactDetails contactsData = this.getContactsDataFromDB(contactName);
		if (contactsData != null)
			contactsData.setEmail(email);

	}

	public void addContactsDetailsInDB(String addressBookName, String fName, String lName, String address, String city,
			String state, String zip, String phoneNo, String email, String contactAddedDate, String contactType) {
		this.contactsList.add(addressBookDBService.addContactDetailsInDB(addressBookName, fName, lName, address, city,
				state, zip, phoneNo, email, contactAddedDate, contactType));
	}

	// match local array and data from db after updating
	public boolean checkAddressBookContactsInSyncWithDB(String contactName) {
		List<ContactDetails> contactDataList = addressBookDBService.getContactsData(contactName);
		return contactDataList.get(0).equals(getContactsDataFromDB(contactName));
	}

	// Multithreading
	public void addContactsDetailsInDBWithThreads(String addBookName, List<ContactDetails> contactList) {
		Map<Integer, Boolean> contactsAdditionStatus = new HashMap<Integer, Boolean>();
		contactList.forEach(contacts -> {
			Runnable task = () -> {
				contactsAdditionStatus.put(contacts.hashCode(), false);
				System.out.println("Contact being added: " + Thread.currentThread().getName());
				this.addContactsDetailsInDB(addBookName, contacts.firstName, contacts.lastName, contacts.address,
						contacts.city, contacts.state, contacts.zip, contacts.phoneNo, contacts.email, "2020-11-05",
						contacts.contactType);
				contactsAdditionStatus.put(contacts.hashCode(), true);
				System.out.println("Contact added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, contacts.getFirstName());
			thread.start();
		});
		while (contactsAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public long countEntries() {
		return this.contactsList.size();
	}

	public static void main(String[] args)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		Scanner userInput = new Scanner(System.in);
		System.out.println("welcome and create address books ");
		AddressBookManage addBookManage = new AddressBookManage();
		boolean created = addBookManage.createAddBooks(userInput);
		System.out.println("Successfully created address books");
		addBookManage.viewAddBooks();

		if (created) {
			System.out.println("Enter 1 to find by state \nEnter 2 to find by city");
			int ch = userInput.nextInt();
			userInput.nextLine();
			switch (ch) {
			case 1:
				System.out.println("Enter the name of state: ");
				String stateName = userInput.nextLine();
				addBookManage.findPersonByState(stateName);
				break;

			case 2:
				System.out.println("Enter the name of city: ");
				String cityName = userInput.nextLine();
				addBookManage.findPersonByCity(cityName);
				break;

			default:
				break;
			}

		}
		System.out.println("Number of persons found = " + addBookManage.countPerson);
	}

}
