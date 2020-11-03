package com.bl.addbookcsvjson;

import java.sql.Connection;
import java.sql.Date;
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

	public List<ContactDetails> getContactsDataByCity(String city) {
		List<ContactDetails> contactDataList = null;
		try {
			Connection connection = this.getConnection();
			String sql = String.format("SELECT * FROM contact_details where city='%s';", city);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactDataList = getContactsData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactDataList;
	}

	public List<ContactDetails> getContactsDataByState(String state) {
		List<ContactDetails> contactDataList = null;
		try {
			Connection connection = this.getConnection();
			String sql = String.format("SELECT * FROM contact_details where state='%s';", state);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactDataList = getContactsData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactDataList;
	}

	public List<ContactDetails> getContactsWithStartDateInGivenRange(String startDate, String endDate) {
		List<ContactDetails> contactsDataList = new ArrayList<>();

		try {
			Connection connection = this.getConnection();
			String sql = String.format(
					"SELECT * FROM contact_details where start BETWEEN CAST('%s' AS DATE) and CAST('%s' AS DATE);",
					startDate, endDate);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactsDataList = getContactsData(resultSet);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactsDataList;
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

	public ContactDetails addContactDetailsInDB(String addressBookName, String fName, String lName, String address,
			String city, String state, String zip, String phoneNo, String email, String contactAddedDate,
			String contactType) {
		int contactDetailsId = -1;
		int addressBookId = -1;
		ContactDetails contactsData = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format("INSERT INTO address_book(address_book_name)" + "VALUES ('%s')",
					addressBookName);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					addressBookId = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO contact_details(first_name, last_name, address, city, state, zip, phone, email, start)"
							+ "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')",
					fName, lName, address, city, state, zip, phoneNo, email, contactAddedDate);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contactDetailsId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO contact_type(address_book_id, contact_type, contact_id)" + "VALUES ('%s','%s','%s')",
					addressBookId, contactType, contactDetailsId);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contactDetailsId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return contactsData;
	}
}
