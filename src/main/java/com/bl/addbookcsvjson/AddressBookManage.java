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
	// used to get and save data to database
	private AddressBookDBService addressBookDBService = new AddressBookDBService();
	private List<ContactDetails> contactsList = new ArrayList<ContactDetails>(); // maintains list of contacts

	public AddressBookManage() {
		nameToAddressBookMap = new HashMap<>();
		valuePrinted = new ArrayList<>();
	}

	// Parameterized constructor
	public AddressBookManage(List<ContactDetails> contactsList) {
		this();
		this.contactsList = contactsList;
	}

	public void addAddressBook(String addBookName, AddressBook addBook) {
		nameToAddressBookMap.put(addBookName, addBook);
	}

	// function to create a address book (from console)
	public boolean createAddBooks(Scanner input)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, AddressBookException {
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

	// function to view the created address books from console
	public void viewAddBooks() {
		for (int i = 0; i < nameToAddressBookMap.size(); i++) {
			System.out.println(i + "Name of add. book are : " + nameToAddressBookMap);
		}
	}

	// to find the contact details by giving city name (from console)
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

	// to find the contact details by giving state name (from console)
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

	// database reading - address book table
	public long getAddressBookDataFromDB() throws AddressBookException {
		long count = addressBookDBService.readAddressBookDB();
		return count;
	}

	// database reading - contact details table
	public long getContactsDataFromDB() throws AddressBookException {
		contactsList = addressBookDBService.readContactDetailsTable();
		return contactsList.size();
	}

	// database reading
	private ContactDetails getContactsDataFromDB(String contactName) {
		return this.contactsList.stream().filter(contact -> contact.getFirstName().equals(contactName)).findFirst()
				.orElse(null);
	}

	// database reading to get contacts if date range given
	public List<ContactDetails> getContactsByDateRangeFromDB(String start, String end) throws AddressBookException {
		this.contactsList = addressBookDBService.getContactsWithStartDateInGivenRange(start, end);
		return this.contactsList;
	}

	// database reading to get contacts if city given
	public List<ContactDetails> getContactsByCityFromDB(String city) throws AddressBookException {
		this.contactsList = addressBookDBService.getContactsDataByCity(city);
		return this.contactsList;
	}

	// database reading to get contacts if state given
	public List<ContactDetails> getContactsByStateFromDB(String state) throws AddressBookException {
		this.contactsList = addressBookDBService.getContactsDataByState(state);
		return this.contactsList;
	}

	// update database by using the first name of contact
	public void updateContactEmailInDB(String contactName, String email) throws AddressBookException {
		int result = addressBookDBService.updateEmployeeData(contactName, email);
		if (result == 0)
			return;
		ContactDetails contactsData = this.getContactsDataFromDB(contactName);
		if (contactsData != null)
			contactsData.setEmail(email);

	}

	// function to add the contacts details in database
	public void addContactsDetailsInDB(String addressBookName, String fName, String lName, String address, String city,
			String state, String zip, String phoneNo, String email, String contactAddedDate, String contactType)
			throws AddressBookException {
		this.contactsList.add(addressBookDBService.addContactDetailsInDB(addressBookName, fName, lName, address, city,
				state, zip, phoneNo, email, contactAddedDate, contactType));
	}

	// match local array and data from db after updating
	public boolean checkAddressBookContactsInSyncWithDB(String contactName) throws AddressBookException {
		List<ContactDetails> contactDataList = addressBookDBService.getContactsData(contactName);
		return contactDataList.get(0).equals(getContactsDataFromDB(contactName));
	}

	// adding multiple contacts using Multi-threading
	public void addContactsDetailsInDBWithThreads(String addBookName, List<ContactDetails> contactList) {
		Map<Integer, Boolean> contactsAdditionStatus = new HashMap<Integer, Boolean>();
		contactList.forEach(contacts -> {
			Runnable task = () -> {
				contactsAdditionStatus.put(contacts.hashCode(), false);
				System.out.println("Contact being added: " + Thread.currentThread().getName());
				try {
					this.addContactsDetailsInDB(addBookName, contacts.firstName, contacts.lastName, contacts.address,
							contacts.city, contacts.state, contacts.zip, contacts.phoneNo, contacts.email, "2020-11-05",
							contacts.contactType);
				} catch (AddressBookException e) {
					e.printStackTrace();
				}
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

	// get the number of entries in the contacts list of address book
	public long countEntries() {
		return this.contactsList.size();
	}

	// add given contact (from JSON server) to the list
	public void addContactDetailsJSONServer(ContactDetails contact) {
		this.contactsList.add(contact);
	}

	public static void main(String[] args)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, AddressBookException {
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
