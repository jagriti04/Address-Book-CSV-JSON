package com.bl.addbookcsvjson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class AddressBookFileIOService {
	public static String FILE_ADDRESS_BOOK = "address-book-file";
	public static String CSV_FILE_ADDRESS_BOOK = "address-book-csv";
	public static String JSON_FILE_ADDRESS_BOOK = "address-book-json";

	// function to write to the file
	public void writeToFile(ArrayList<ContactDetails> contactList) throws IOException, AddressBookException {
		Path filePath = Paths.get(FILE_ADDRESS_BOOK + ".txt");
		if (Files.notExists(filePath))
			Files.createFile(filePath);
		StringBuffer addressBookBuffer = new StringBuffer();
		contactList.forEach(book -> {
			String bookDataString = book.toString().concat("\n");
			addressBookBuffer.append(bookDataString);
		});

		try {
			Files.write(filePath, addressBookBuffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.FILE_WRITE_ERROR);
		}

	}

	// function to read from the file
	public void readFromFile() throws IOException, AddressBookException {
		Path filePath = Paths.get(FILE_ADDRESS_BOOK + ".txt");
		try {
			System.out.println("The contact details in the address book file are : ");
			Files.lines(filePath).map(line -> line.trim()).forEach(line -> System.out.println(line));
		} catch (IOException e) {
			e.printStackTrace();
			throw new AddressBookException(e.getMessage(), AddressBookException.ExceptionType.FILE_READ_ERROR);
		}
	}

	// function to write to CSV file
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeToCSVFile(ArrayList<ContactDetails> contactList)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		Path filePath = Paths.get(CSV_FILE_ADDRESS_BOOK + ".csv");
		if (Files.notExists(filePath))
			Files.createFile(filePath);
		try (Writer writer = Files.newBufferedWriter(Paths.get(filePath.toUri()));) {
			StatefulBeanToCsv<ContactDetails> beanToCsv = new StatefulBeanToCsvBuilder(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
			beanToCsv.write(contactList);
		}
	}

	// function to read from CSV file
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void readFromCSVFile() throws IOException {
		Path filePath = Paths.get(CSV_FILE_ADDRESS_BOOK + ".csv");
		try (Reader reader = Files.newBufferedReader(Paths.get(filePath.toUri()));) {

			CsvToBean<ContactDetails> csvToBean = new CsvToBeanBuilder(reader).withType(ContactDetails.class)
					.withIgnoreLeadingWhiteSpace(true).build();

			Iterator<ContactDetails> AddressBookIterator = csvToBean.iterator();

			while (AddressBookIterator.hasNext()) {
				ContactDetails contact = AddressBookIterator.next();
				System.out.println("Firstname : " + contact.firstName);
				System.out.println("Lastname : " + contact.lastName);
				System.out.println("Address : " + contact.address);
				System.out.println("City : " + contact.city);
				System.out.println("State : " + contact.state);
				System.out.println("Zip : " + contact.zip);
				System.out.println("Phone number : " + contact.phoneNo);
				System.out.println("Email : " + contact.email);
				System.out.println("-----");
			}
		}
	}

	// write to json file
	public void writeToJsonFile(ArrayList<ContactDetails> contactList) throws IOException {
		String SAMPLE_JSON_FILE = JSON_FILE_ADDRESS_BOOK + ".json";
		Gson gson = new Gson();
		String json = gson.toJson(contactList);
		FileWriter writer = new FileWriter(SAMPLE_JSON_FILE);
		writer.write(json);
		writer.close();
	}

	// read from JSON file
	public void readFromJsonFile() throws IOException {
		String SAMPLE_JSON_FILE = JSON_FILE_ADDRESS_BOOK + ".json";
		Gson gson = new Gson();

		BufferedReader br = new BufferedReader(new FileReader(SAMPLE_JSON_FILE));
		ContactDetails[] contact = gson.fromJson(br, ContactDetails[].class);
		List<ContactDetails> contactList = Arrays.asList(contact);
		for (ContactDetails a : contactList) {
			System.out.println("Firstname : " + a.firstName);
			System.out.println("Lastname : " + a.lastName);
			System.out.println("Address : " + a.address);
			System.out.println("City : " + a.city);
			System.out.println("State : " + a.state);
			System.out.println("Zip : " + a.zip);
			System.out.println("Phone number : " + a.phoneNo);
			System.out.println("Email : " + a.email);
			System.out.println("-----------");
		}
	}
}