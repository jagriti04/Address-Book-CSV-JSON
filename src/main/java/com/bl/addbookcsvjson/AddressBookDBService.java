package com.bl.addbookcsvjson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookDBService {

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

}
