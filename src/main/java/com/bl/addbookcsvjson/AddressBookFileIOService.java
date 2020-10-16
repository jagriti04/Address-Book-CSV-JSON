package com.bl.addbookcsvjson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AddressBookFileIOService {
	public static String FILE_ADDRESS_BOOK = "address-book-file";

	// function to write to the file
	public void writeToFile(ArrayList<ContactDetails> contactList) throws IOException {
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
		}

	}

	// function to read from the file
	public void readFromFile() throws IOException {
		Path filePath = Paths.get(FILE_ADDRESS_BOOK+ ".txt");
		try {
			System.out.println("The contact details in the address book file are : ");
			Files.lines(filePath).map(line -> line.trim()).forEach(line -> System.out.println(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}