package com.bl.addbookcsvjson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {

	private PreparedStatement contactsDataStatement;
	private static AddressBookDBService addressBookDBService;

	public AddressBookDBService() {

	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null) {
			addressBookDBService = new AddressBookDBService();
		}
		return addressBookDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURl = "jdbc:mysql://localhost:3306/address_book_service?useSSL=false";
		String userName = "root";
		String password = "treadwill04";
		Connection con;
		con = DriverManager.getConnection(jdbcURl, userName, password);
		return con;
	}

	public long readAddressBookDB() {
		String sql = "SELECT * FROM address_book;";
		List<String> addressBookList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			addressBookList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return addressBookList.size();
	}

	public List<ContactDetails> readContactDetailsTable() {
		String sql = "SELECT * FROM contact_details;";
		List<ContactDetails> contactsList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactsList = this.getContactsData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactsList;
	}

	private List<String> getAddressBookData(ResultSet resultSet) {
		List<String> addBookDataList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("address_book_id");
				String address_book_name = resultSet.getString("address_book_name");
				addBookDataList.add(address_book_name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addBookDataList;
	}

	private List<ContactDetails> getContactsData(ResultSet resultSet) {
		List<ContactDetails> contactsDataList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("contact_id");
				String fName = resultSet.getString("first_name");
				String lName = resultSet.getString("last_name");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				String zip = resultSet.getString("zip");
				String phoneNo = resultSet.getString("phone");
				String email = resultSet.getString("email");
				contactsDataList.add(new ContactDetails(id, fName, lName, address, city, state, zip, phoneNo, email));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactsDataList;
	}

	public int updateEmployeeData(String contactName, String email) {
		String sql = String.format("update contact_details set email = '%s' where first_name = '%s';", email,
				contactName);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<ContactDetails> getContactsData(String contactName) {
		List<ContactDetails> contactDataList = null;
		if (this.contactsDataStatement == null)
			this.prepareStatementForContactData();
		try {
			contactsDataStatement.setString(1, contactName);
			ResultSet resultSet = contactsDataStatement.executeQuery();
			contactDataList = this.getContactsData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactDataList;
	}

	private void prepareStatementForContactData() {
		try {
			Connection conn = this.getConnection();
			String sql = "SELECT * FROM contact_details where first_name = ?";
			contactsDataStatement = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
