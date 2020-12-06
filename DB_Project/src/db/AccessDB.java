package db;

import java.util.Scanner;
import java.sql.*;

public class AccessDB {

	String url;
	Scanner sc;

	Connection con;
	Statement stmt;
	ResultSet rs;
	int r;

	public AccessDB() throws ClassNotFoundException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		this.url = "jdbc:mysql://localhost:3306/music_streaming";
		this.sc = new Scanner(System.in);

	}

	// ID/PW 조회 함수들
	public String isRightManagerID(String id) throws SQLException {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();
		rs = stmt.executeQuery("select id from manager");
		while (rs.next())
			if (id.equals(rs.getString(1))) {
				rs.close();
				stmt.close();
				con.close();
				return id;
			}
		rs.close();
		stmt.close();
		con.close();
		return null;

	}

	public boolean isRightManagerPW(String id, String pw) throws SQLException {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();
		rs = stmt.executeQuery("select pw from manager where id='" + id + "'");
		if (rs.next())
			if (pw.equals(rs.getString(1))) {
				rs.close();
				stmt.close();
				con.close();
				return true;
			}
		rs.close();
		stmt.close();
		con.close();
		return false;

	}

	public int isRightUserID(String id) throws SQLException {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();
		rs = stmt.executeQuery("select number, id from user");
		while (rs.next())
			if (id.equals(rs.getString(2))) {
				int userNumber = rs.getInt(1);
				rs.close();
				stmt.close();
				con.close();
				return userNumber;
			}
		rs.close();
		stmt.close();
		con.close();
		return -1;

	}

	public boolean isRightUserPW(String id, String pw) throws SQLException {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();
		rs = stmt.executeQuery("select pw from user where id='" + id + "'");
		if (rs.next())
			if (pw.equals(rs.getString(1))) {
				rs.close();
				stmt.close();
				con.close();
				return true;
			}
		rs.close();
		stmt.close();
		con.close();
		return false;

	}

	public void joinMembership() throws SQLException {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();

		String id;
		while (true) {// 아이디 중복 체크
			System.out.print("ID: ");
			id = sc.next();
			boolean duplicateID = false;
			rs = stmt.executeQuery("select id from user");
			while (rs.next())
				if (id.equals(rs.getString(1))) {
					System.out.println("This ID already exists");
					duplicateID = true;
					break;
				}
			if (!duplicateID)
				break;
		}

		rs = stmt.executeQuery("select max(number) from user");
		int lastNumber = 1;
		if (rs.next())
			lastNumber = rs.getInt(1);
		int number = lastNumber + 1;

		System.out.print("PW: ");
		String pw = sc.next();
		System.out.print("NAME: ");
		String name = sc.next();
		System.out.print("License(1-small, 2-midium, 3-big): ");
		int license = sc.nextInt();

		stmt.executeUpdate(
				"insert into user values(" + number + ",'" + name + "',0," + license + ",'" + id + "','" + pw + "')");

		System.out.println("Join completed");

		rs.close();
		stmt.close();
		con.close();
	}

	// Manager 전용 함수들

	public void manageUsers() throws Exception {

		while (true) {

			System.out.println("================================");
			System.out.println(
					"0. Return to previous menu\n1. Insert user\n2. Delete user\n3. Retrieve user\n4. Update user's info\n5. Show all users");
			System.out.println("================================");
			System.out.print("Input: ");
			int inputNo = sc.nextInt();

			if (inputNo == 0) {
				return;

			} else if (inputNo == 1) {// insert user

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				String id;
				while (true) {// 아이디 중복 체크
					System.out.print("ID: ");
					id = sc.next();
					boolean duplicateID = false;
					rs = stmt.executeQuery("select id from user");
					while (rs.next())
						if (id.equals(rs.getString(1))) {
							System.out.println("ID already exists");
							duplicateID = true;
							break;
						}
					if (!duplicateID)
						break;
				}

				rs = stmt.executeQuery("select max(number) from user");
				int lastNumber = 1;
				if (rs.next())
					lastNumber = rs.getInt(1);
				int number = lastNumber + 1;
				System.out.print("NAME: ");
				String name = sc.next();
				System.out.print("License: ");
				int license = sc.nextInt();
				System.out.print("PW: ");
				String pw = sc.next();

				stmt.executeUpdate("insert into user values(" + number + ",'" + name + "',0," + license + ",'" + id
						+ "','" + pw + "')");

				rs.close();
				stmt.close();
				con.close();

				System.out.println("Inserted");

			} else if (inputNo == 2) {// delete user

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {

					System.out.print("User name to Delete: ");
					String name = sc.next();
					int number = 0;

					rs = stmt.executeQuery("select count(number) from user where name='" + name + "'");

					int sameNameNumber = 0;
					if (rs.next())
						sameNameNumber = rs.getInt(1);

					if (sameNameNumber == 0) {
						System.out.println("No such user");
						continue;
					}

					if (sameNameNumber == 1) {
						rs = stmt.executeQuery("select number from user where name='" + name + "'");
						if (rs.next())
							number = rs.getInt(1);

					} else if (sameNameNumber >= 2) {
						rs = stmt.executeQuery("select number, name, id from user where name='" + name + "'");
						System.out.println("Which one?");
						int[] candidate = new int[sameNameNumber];
						int k = 0;
						while (rs.next()) {
							candidate[k] = rs.getInt(1);
							System.out.println(++k + " - " + rs.getString(2) + " (ID: " + rs.getString(3) + ")");
						}
						System.out.print("input: ");
						int temp = sc.nextInt();
						number = candidate[temp - 1];
					}

					stmt.executeUpdate("delete from streaming where user_number=" + number);
					stmt.executeUpdate("delete from contain where playlist_user=" + number);
					stmt.executeUpdate("delete from playlist where user_number=" + number);
					stmt.executeUpdate("delete from user where number=" + number);
					System.out.println("Deleted");

					break;

				}
				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 3) {// retrieve user

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {

					System.out.println("================================");
					System.out.println(
							"0. Return to previous menu\n1. Retrieve by name\n2. Retrieve by ID");
					System.out.println("================================");
					System.out.print("Input: ");
					int input = sc.nextInt();
					boolean temp = true;

					if (input == 0) {
						break;

					} else if (input == 1) {

						System.out.print("Name: ");
						String name = sc.next();
						rs = stmt.executeQuery("select * from user where name='" + name + "'");

					} else if (input == 2) {
						System.out.print("ID: ");
						String id = sc.next();
						rs = stmt.executeQuery("select * from user where id='" + id + "'");

					} else {
						System.out.println("Wrong command number");
						temp = false;
					}

					if (temp) {
						while (rs.next()) {
							System.out.println("--------------------------------");
							System.out.println("Number: " + rs.getInt(1));
							System.out.println("Name: " + rs.getString(2));
							System.out.println("Streamed number: " + rs.getInt(3));
							System.out.println("License: " + rs.getInt(4));
							System.out.println("ID: " + rs.getString(5));
							System.out.println("PW: " + rs.getString(6));
							System.out.println("--------------------------------");
						}
					}
				}

				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 4) {// update user's information

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				int number = 0;
				System.out.print("User name to update: ");
				String name = sc.next();

				rs = stmt.executeQuery("select count(number) from user where name='" + name + "'");

				int sameNameNumber = 0;
				if (rs.next())
					sameNameNumber = rs.getInt(1);

				if (sameNameNumber == 0) {
					System.out.println("No such user");
					continue;
				}

				if (sameNameNumber == 1) {
					rs = stmt.executeQuery("select number from user where name='" + name + "'");
					if (rs.next())
						number = rs.getInt(1);

				} else if (sameNameNumber >= 2) {
					rs = stmt.executeQuery("select number, name, id from user where name='" + name + "'");
					System.out.println("Which one?");
					int[] candidate = new int[sameNameNumber];
					int k = 0;
					while (rs.next()) {
						candidate[k] = rs.getInt(1);
						System.out.println(++k + " - " + rs.getString(2) + " (ID: " + rs.getString(3) + ")");
					}
					System.out.print("input: ");
					int temp = sc.nextInt();
					number = candidate[temp - 1];
				}

				System.out.print("Attribute to update (1-name, 2-license, 3-ID, 4-PW): ");
				int attribute = sc.nextInt();
				String stringValue;
				int intValue;

				switch (attribute) {
				case 1:
					System.out.print("New name: ");
					stringValue = sc.next();
					stmt.executeUpdate("update user set name='" + stringValue + "' where number=" + number);
					break;
				case 2:
					System.out.print("1-small, 2-midium, 3-big\nNew license:");
					intValue = sc.nextInt();
					stmt.executeUpdate("update user set license=" + intValue + " where number=" + number);
					break;
				case 3:
					while (true) {// 아이디 중복 체크
						System.out.print("New ID: ");
						stringValue = sc.next();
						boolean duplicateID = false;
						rs = stmt.executeQuery("select id from user");
						while (rs.next())
							if (stringValue.equals(rs.getString(1))) {
								System.out.println("ID already exists");
								duplicateID = true;
								break;
							}
						if (!duplicateID)
							break;
					}
					stmt.executeUpdate("update user set id='" + stringValue + "' where number=" + number);
					break;
				case 4:
					System.out.print("New password: ");
					stringValue = sc.next();
					stmt.executeUpdate("update user set pw='" + stringValue + "' where number=" + number);
					break;
				}

				rs = stmt.executeQuery("select * from user where number=" + number);
				if (rs.next()) {
					System.out.println("--------------------------------");
					System.out.println("Number: " + rs.getInt(1));
					System.out.println("Name: " + rs.getString(2));
					System.out.println("Streamed number: " + rs.getInt(3));
					System.out.println("License: " + rs.getInt(4));
					System.out.println("ID: " + rs.getString(5));
					System.out.println("PW: " + rs.getString(6));
					System.out.println("--------------------------------");
				}

				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 5) {// show all users

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				rs = stmt.executeQuery("select * from user");
				while (rs.next()) {
					System.out.println("--------------------------------");
					System.out.println("Number: " + rs.getInt(1));
					System.out.println("Name: " + rs.getString(2));
					System.out.println("Streamed number: " + rs.getInt(3));
					System.out.println("License: " + rs.getInt(4));
					System.out.println("ID: " + rs.getString(5));
					System.out.println("PW: " + rs.getString(6));
					System.out.println("--------------------------------");
				}

				rs.close();
				stmt.close();
				con.close();

			} else
				System.out.println("Wrong Command Number");
		}

	}

	public void manageMusics() throws Exception {

		while (true) {

			System.out.println("================================");
			System.out.println(
					"0. Return to previous menu\n1. Insert music\n2. Delete music\n3. Retrieve music\n4. Update music's info\n5. Show all musics");
			System.out.println("================================");
			System.out.print("Input: ");
			int inputNo = sc.nextInt();

			if (inputNo == 0) {
				return;

			} else if (inputNo == 1) {// insert music

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				rs = stmt.executeQuery("select max(number) from music");
				int lastNumber = 1;
				if (rs.next())
					lastNumber = rs.getInt(1);
				int number = lastNumber + 1;
				System.out.print("Write new music's\nNAME: ");
				String name = sc.next();
				System.out.println("Singer: ");
				String singer = sc.next();
				System.out.println("Genre: ");
				String genre = sc.next();
				int playedNumber = 0;

				stmt.executeUpdate("insert into music values(" + number + ",'" + name + "','" + singer + "','" + genre
						+ "'," + playedNumber + ")");

				rs.close();
				stmt.close();
				con.close();

				System.out.println("Inserted");

			} else if (inputNo == 2) {// delete music

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {

					System.out.print("Music name to Delete: ");
					String name = sc.next();
					int number = 0;

					rs = stmt.executeQuery("select count(number) from music where name='" + name + "'");

					int sameNameNumber = 0;
					if (rs.next())
						sameNameNumber = rs.getInt(1);

					if (sameNameNumber == 0) {
						System.out.println("No such music");
						continue;
					}

					if (sameNameNumber == 1) {
						rs = stmt.executeQuery("select number from music where name='" + name + "'");
						if (rs.next())
							number = rs.getInt(1);

					} else if (sameNameNumber >= 2) {
						rs = stmt.executeQuery("select number, name, singer from music where name='" + name + "'");
						System.out.println("Which one?");
						int[] candidate = new int[sameNameNumber];
						int k = 0;
						while (rs.next()) {
							candidate[k] = rs.getInt(1);
							System.out.println(++k + " - " + rs.getString(2) + " - " + rs.getString(3));
						}
						System.out.print("input: ");
						int singer = sc.nextInt();
						number = candidate[singer - 1];
					}

					stmt.executeUpdate("delete from streaming where music_number=" + number);
					stmt.executeUpdate("delete from contain where music_number=" + number);
					stmt.executeUpdate("delete from music where number=" + number);
					System.out.println("Deleted");

					break;

				}
				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 3) {// retrieve music

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {

					System.out.println("================================");
					System.out.println(
							"0. Return to previous menu\n1. Retrieve by name\n2. Retrieve by singer\n3. Retrieve by genre");
					System.out.println("================================");
					System.out.print("Input: ");
					int input = sc.nextInt();
					boolean temp = true;

					if (input == 0) {
						break;

					} else if (input == 1) {
						System.out.print("Name: ");
						String name = sc.next();

						rs = stmt.executeQuery("select * from music where name='" + name + "'");

					} else if (input == 2) {
						System.out.print("Singer: ");
						String singer = sc.next();

						rs = stmt.executeQuery("select * from music where singer='" + singer + "'");

					} else if (input == 3) {
						System.out.print("Genre: ");
						String genre = sc.next();

						rs = stmt.executeQuery("select * from music where genre='" + genre + "'");

					} else {
						System.out.println("Wrong command number");
						temp = false;
					}
					if (temp) {
						while (rs.next()) {
							System.out.println("--------------------------------");
							System.out.println("Number: " + rs.getInt(1));
							System.out.println("Name: " + rs.getString(2));
							System.out.println("Signer: " + rs.getString(3));
							System.out.println("Genre: " + rs.getString(4));
							System.out.println("Played number: " + rs.getInt(5));
							System.out.println("--------------------------------");
						}
					}
				}

				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 4) {// update music's information

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				int number = 0;
				System.out.print("Music name to update: ");
				String name = sc.next();

				rs = stmt.executeQuery("select count(number) from music where name='" + name + "'");

				int sameNameNumber = 0;
				if (rs.next())
					sameNameNumber = rs.getInt(1);

				if (sameNameNumber == 0) {
					System.out.println("No such music");
					continue;
				}

				if (sameNameNumber == 1) {
					rs = stmt.executeQuery("select number from music where name='" + name + "'");
					if (rs.next())
						number = rs.getInt(1);

				} else if (sameNameNumber >= 2) {
					rs = stmt.executeQuery("select number, name, singer from music where name='" + name + "'");
					System.out.println("Which one?");
					int[] candidate = new int[sameNameNumber];
					int k = 0;
					while (rs.next()) {
						candidate[k] = rs.getInt(1);
						System.out.println(++k + " - " + rs.getString(2) + " - " + rs.getString(3));
					}
					System.out.print("input: ");
					int singer = sc.nextInt();
					number = candidate[singer - 1];
				}

				rs = stmt.executeQuery("select number from music where name='" + name + "'");
				if (rs.next())
					number = rs.getInt(1);

				System.out.print("Attribute to update (1-name, 2-singer, 3-genre): ");
				int attribute = sc.nextInt();
				System.out.print("Value to update: ");
				String value = sc.next();

				switch (attribute) {
				case 1:
					stmt.executeUpdate("update music set name='" + value + "' where number=" + number);
					break;
				case 2:
					stmt.executeUpdate("update music set singer='" + value + "' where number=" + number);
					break;
				case 3:
					stmt.executeUpdate("update music set genre='" + value + "' where number=" + number);
					break;
				}

				rs = stmt.executeQuery("select * from music where number=" + number);

				if (rs.next()) {
					System.out.println("--------------------------------");
					System.out.println("Number: " + rs.getInt(1));
					System.out.println("Name: " + rs.getString(2));
					System.out.println("Signer: " + rs.getString(3));
					System.out.println("Genre: " + rs.getString(4));
					System.out.println("Played number: " + rs.getInt(5));
					System.out.println("--------------------------------");
				}

				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 5) {

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				rs = stmt.executeQuery("select * from music");

				while (rs.next()) {
					System.out.println("--------------------------------");
					System.out.println("Number: " + rs.getInt(1));
					System.out.println("Name: " + rs.getString(2));
					System.out.println("Signer: " + rs.getString(3));
					System.out.println("Genre: " + rs.getString(4));
					System.out.println("Played number: " + rs.getInt(5));
					System.out.println("--------------------------------");
				}

				rs.close();
				stmt.close();
				con.close();
			} else
				System.out.println("Wrong Command Number");
		}

	}

	public void manageManagers() throws Exception {

		while (true) {

			System.out.println("================================");
			System.out.println(
					"0. Return to previous menu\n1. Insert manager\n2. Delete manager\n3. Retrieve manager\n4. Update manager's info\n5. Show all managers");
			System.out.println("================================");
			System.out.print("Input: ");
			int inputNo = sc.nextInt();

			if (inputNo == 0) {
				return;

			} else if (inputNo == 1) {// insert manager

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				System.out.print("NAME: ");
				String name = sc.next();
				String id;
				while (true) {// 아이디 중복 체크
					System.out.print("ID: ");
					id = sc.next();
					boolean duplicateID = false;
					rs = stmt.executeQuery("select id from manager");
					while (rs.next())
						if (id.equals(rs.getString(1))) {
							System.out.println("ID already exists");
							duplicateID = true;
							break;
						}
					if (!duplicateID)
						break;
				}
				rs = stmt.executeQuery("select max(number) from manager");
				int lastNumber = 1;
				if (rs.next())
					lastNumber = rs.getInt(1);
				int number = lastNumber + 1;
				System.out.print("PW: ");
				String pw = sc.next();

				stmt.executeUpdate(
						"insert into manager values(" + number + ",'" + name + "','" + id + "','" + pw + "')");

				rs.close();
				stmt.close();
				con.close();

				System.out.println("Inserted");

			} else if (inputNo == 2) {// delete manager

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {

					System.out.print("Manager name to Delete: ");
					String name = sc.next();
					int number = 0;

					if (name.equals("President")) {
						System.out.println("The president cannot be deleted");
						continue;
					}

					rs = stmt.executeQuery("select count(number) from manager where name='" + name + "'");

					int sameNameNumber = 0;
					if (rs.next())
						sameNameNumber = rs.getInt(1);

					if (sameNameNumber == 0) {
						System.out.println("No such manager");
						continue;
					}

					if (sameNameNumber == 1) {
						rs = stmt.executeQuery("select number from manager where name='" + name + "'");
						if (rs.next())
							number = rs.getInt(1);

					} else if (sameNameNumber >= 2) {
						rs = stmt.executeQuery("select number, name, id from manager where name='" + name + "'");
						System.out.println("Which one?");
						int[] candidate = new int[sameNameNumber];
						int k = 0;
						while (rs.next()) {
							candidate[k] = rs.getInt(1);
							System.out.println(++k + " - " + rs.getString(2) + " (ID: " + rs.getString(3) + ")");
						}
						System.out.print("input: ");
						int temp = sc.nextInt();
						number = candidate[temp - 1];
					}

					stmt.executeUpdate("delete from manager where number=" + number);
					System.out.println("Deleted");

					break;

				}
				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 3) {// retrieve manager

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {

					System.out.println("================================");
					System.out.println("0. Return to previous menu\n1. Retrieve by name\n2. Retrieve by ID");
					System.out.println("================================");
					System.out.print("Input: ");
					int input = sc.nextInt();
					boolean temp = true;

					if (input == 0) {
						break;

					} else if (input == 1) {

						System.out.print("Name: ");
						String name = sc.next();

						rs = stmt.executeQuery("select * from manager where name='" + name + "'");

					} else if (input == 2) {
						System.out.print("ID: ");
						String id = sc.next();

						rs = stmt.executeQuery("select * from manager where id='" + id + "'");

					} else {
						System.out.println("Wrong command number");
						temp = false;
					}
					if (temp) {
						while (rs.next()) {
							System.out.println("--------------------------------");
							System.out.println("Number: " + rs.getInt(1));
							System.out.println("Name: " + rs.getString(2));
							System.out.println("ID: " + rs.getString(3));
							System.out.println("PW: " + rs.getString(4));
							System.out.println("--------------------------------");
						}
					}
				}

				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 4) {// update manager's information

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				int number = -1;
				System.out.print("Manager name to update: ");
				String name = sc.next();

				rs = stmt.executeQuery("select count(number) from manager where name='" + name + "'");

				int sameNameNumber = 0;
				if (rs.next())
					sameNameNumber = rs.getInt(1);

				if (sameNameNumber == 0) {
					System.out.println("No such manager");
					continue;
				}

				if (sameNameNumber == 1) {
					rs = stmt.executeQuery("select number from manager where name='" + name + "'");
					if (rs.next())
						number = rs.getInt(1);

				} else if (sameNameNumber >= 2) {
					rs = stmt.executeQuery("select number, name, id from manager where name='" + name + "'");
					System.out.println("Which one?");
					int[] candidate = new int[sameNameNumber];
					int k = 0;
					while (rs.next()) {
						candidate[k] = rs.getInt(1);
						System.out.println(++k + " - " + rs.getString(2) + " (ID: " + rs.getString(3) + ")");
					}
					System.out.print("input: ");
					int temp = sc.nextInt();
					number = candidate[temp - 1];
				}

				rs = stmt.executeQuery("select number from manager where name='" + name + "'");
				if (rs.next())
					number = rs.getInt(1);

				System.out.print("Attribute to update (1-name, 2-ID, 3-PW): ");
				int attribute = sc.nextInt();
				System.out.print("Value to update: ");
				String value;

				switch (attribute) {
				case 1:
					value = sc.next();
					stmt.executeUpdate("update manager set name='" + value + "' where number=" + number);
					break;
				case 2:
					while (true) {// 아이디 중복 체크
						System.out.print("ID: ");
						value = sc.next();
						boolean duplicateID = false;
						rs = stmt.executeQuery("select id from manager");
						while (rs.next())
							if (value.equals(rs.getString(1))) {
								System.out.println("ID already exists");
								duplicateID = true;
								break;
							}
						if (!duplicateID)
							break;
					}
					stmt.executeUpdate("update manager set id='" + value + "' where number=" + number);
					break;
				case 3:
					value = sc.next();
					stmt.executeUpdate("update manager set pw='" + value + "' where number=" + number);
					break;
				}

				rs = stmt.executeQuery("select * from manager where number=" + number);

				if (rs.next()) {
					System.out.println("--------------------------------");
					System.out.println("Number: " + rs.getInt(1));
					System.out.println("Name: " + rs.getString(2));
					System.out.println("ID: " + rs.getString(3));
					System.out.println("PW: " + rs.getString(4));
					System.out.println("--------------------------------");
				}

				rs.close();
				stmt.close();
				con.close();

			} else if (inputNo == 5) {

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				rs = stmt.executeQuery("select * from manager");

				while (rs.next()) {
					System.out.println("--------------------------------");
					System.out.println("Number: " + rs.getInt(1));
					System.out.println("Name: " + rs.getString(2));
					System.out.println("ID: " + rs.getString(3));
					System.out.println("PW: " + rs.getString(4));
					System.out.println("--------------------------------");
				}

				rs.close();
				stmt.close();
				con.close();
			} else
				System.out.println("Wrong Command Number");
		}

	}

	public void manageLicenses() throws Exception {

		while (true) {

			con = DriverManager.getConnection(url, "root", "");
			stmt = con.createStatement();

			rs = stmt.executeQuery("select * from license");
			while (rs.next()) {
				System.out.println("--------------------------------");
				System.out.println("- " + rs.getString(2));
				System.out.println("Streamable songs: " + rs.getInt(3));
				System.out.println("Fee: " + rs.getInt(4));
				System.out.println("--------------------------------");
			}

			rs.close();
			stmt.close();
			con.close();

			System.out.println("================================");
			System.out.println("0. Return to previous menu\n1. Insert license\n2. Update license's info");
			System.out.println("================================");
			System.out.print("Input: ");
			int inputNo = sc.nextInt();

			if (inputNo == 0) {
				return;

			} else if (inputNo == 1) {// insert license

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				rs = stmt.executeQuery("select max(number) from license");
				int lastNumber = 1;
				if (rs.next())
					lastNumber = rs.getInt(1);
				int number = lastNumber + 1;
				System.out.print("Write new license's\nNAME: ");
				String name = sc.next();
				System.out.print("Streamable songs: ");
				int streamableSongs = sc.nextInt();
				System.out.print("Fee: ");
				int fee = sc.nextInt();

				stmt.executeUpdate("insert into license values(" + number + ",'" + name + "'," + streamableSongs + ","
						+ fee + ")");

				rs.close();
				stmt.close();
				con.close();

				System.out.println("Inserted");

			} else if (inputNo == 2) {// update license's information

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				int number = 0;
				boolean exist = false;
				System.out.print("License name to update: ");
				String name = sc.next();

				rs = stmt
						.executeQuery("select exists(select number from license where name='" + name + "') as success");
				if (rs.next())
					exist = rs.getBoolean(1);
				if (!exist)
					System.out.println("No such license");
				else {

					rs = stmt.executeQuery("select number from license where name='" + name + "'");
					if (rs.next())
						number = rs.getInt(1);

					System.out.print("Attribute to update (1-name, 2-streamable_songs, 3-fee): ");
					int attribute = sc.nextInt();

					if (attribute == 1) {
						System.out.print("New name: ");
						String newName = sc.next();
						stmt.executeUpdate("update license set name='" + newName + "' where number=" + number);
					} else if (attribute == 2) {
						System.out.print("New streamable_songs: ");
						int songs = sc.nextInt();
						stmt.executeUpdate("update license set streamable_songs=" + songs + " where number=" + number);
					} else if (attribute == 3) {
						System.out.print("New fee: ");
						int fee = sc.nextInt();
						stmt.executeUpdate("update license set fee=" + fee + " where number=" + number);
					}
				}

				rs.close();
				stmt.close();
				con.close();

			} else
				System.out.println("Wrong Command Number");

		}

	}

	public void showCurrectStreaming(int userNumber) throws Exception {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();

		rs = stmt.executeQuery("select m.name, m.singer from streaming as s, music as m where s.user_number="
				+ userNumber + " and s.music_number=m.number");
		if (rs.next())
			System.out.println("Current Streaming Song: " + rs.getString(1) + " - " + rs.getString(2));

		rs.close();
		stmt.close();
		con.close();

	}

	// User 전용 함수들
	public void listenToMusic(int userNumber) throws Exception {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();

		int musicNumber = 0;

		// music number 구하기
		System.out.print("Music name: ");
		String name = sc.next();

		rs = stmt.executeQuery("select count(name) from music where name='" + name + "'");
		int sameNameNumber = 0;
		if (rs.next())
			sameNameNumber = rs.getInt(1);

		if (sameNameNumber == 1) {
			rs = stmt.executeQuery("select number from music where name='" + name + "'");
			if (rs.next())
				musicNumber = rs.getInt(1);

		} else if (sameNameNumber >= 2) {
			rs = stmt.executeQuery("select number,singer from music where name='" + name + "'");
			System.out.println("Which one?");
			int[] candidate = new int[sameNameNumber];
			int i = 0;
			while (rs.next()) {
				candidate[i] = rs.getInt(1);
				i++;
				System.out.println(i + " - " + rs.getString(2));
			}
			System.out.print("input: ");
			int singer = sc.nextInt() - 1;
			musicNumber = candidate[singer];

		} else {
			System.out.println("No such music");
			return;
		}

		// streaming table에서 기존에 듣던 내용 삭제
		stmt.executeUpdate("delete from streaming where user_number=" + userNumber);
		// streaming 정보 넣기
		stmt.executeUpdate("insert into streaming values(" + userNumber + "," + musicNumber + ")");
		// user의 streaming한 횟수 +1
		stmt.executeUpdate("update user set streamed_num=streamed_num+1 where number=" + userNumber);
		// music의 play된 횟수 +1
		stmt.executeUpdate("update music set played_number=played_number+1 where number=" + musicNumber);

		rs.close();
		stmt.close();
		con.close();

	}

	public void editPlaylist(int userNumber) throws Exception {

		while (true) {

			con = DriverManager.getConnection(url, "root", "");
			stmt = con.createStatement();

			int input;

			rs = stmt.executeQuery("select name from playlist where user_number=" + userNumber + " order by name");
			String playlistName = "";
			System.out.println("----------My playlists----------");
			while (rs.next())
				System.out.println("- " + rs.getString(1));
			System.out.println("--------------------------------");

			rs.close();
			stmt.close();
			con.close();

			System.out.println("================================");
			System.out.println("0. Return to previous menu\n1. Create playlist\n2. Edit playlist");
			System.out.println("================================");
			System.out.print("Input: ");
			input = sc.nextInt();

			if (input == 0)
				break;

			else if (input == 1) {// create playlist

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				while (true) {// 이름 중복 체크
					System.out.print("PlayList name: ");
					playlistName = sc.next();
					boolean duplicateName = false;
					rs = stmt.executeQuery("select name from playlist where user_number=" + userNumber);
					while (rs.next())
						if (playlistName.equals(rs.getString(1))) {
							System.out.println("Duplicated name. Try another.");
							duplicateName = true;
							break;
						}
					if (!duplicateName)
						break;
				}

				rs = stmt.executeQuery("select max(number) from playlist");
				int lastNumber = 1;
				if (rs.next())
					lastNumber = rs.getInt(1);
				int number = lastNumber + 1;

				stmt.executeUpdate(
						"insert into playlist values(" + userNumber + "," + number + ",'" + playlistName + "')");

				stmt.close();
				con.close();

				System.out.println("Created");

			} else if (input == 2) {// edit playlist

				System.out.print("Playlist name to edit: ");
				playlistName = sc.next();

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				rs = stmt.executeQuery("select number from playlist where user_number=" + userNumber + " and name='"
						+ playlistName + "'");
				int playlistNumber = 0;
				if (rs.next())
					playlistNumber = rs.getInt(1);

				rs.close();
				stmt.close();
				con.close();

				while (true) {

					con = DriverManager.getConnection(url, "root", "");
					stmt = con.createStatement();

					rs = stmt.executeQuery(
							"select distinct m.name, m.singer from playlist as p, contain as c, music as m where p.number="
									+ playlistNumber + " and c.playlist_user=" + userNumber
									+ " and c.playlist_number=p.number and c.music_number=m.number order by m.name");

					int j = 0;
					System.out.println("--------------------------------");
					while (rs.next())
						System.out.println("(" + ++j + ") " + rs.getString(1) + " - " + rs.getString(2));
					System.out.println("--------------------------------");

					rs.close();
					stmt.close();
					con.close();

					System.out.println("================================");
					System.out.println(
							"0. Return to previous menu\n1. Change playlist's name\n2. Insert music\n3. Delete music\n4. Delete playlist");
					System.out.println("================================");
					System.out.print("Input: ");
					input = sc.nextInt();

					if (input == 0)
						break;
					else if (input == 1) {// change playlist's name

						con = DriverManager.getConnection(url, "root", "");
						stmt = con.createStatement();

						String newName;

						while (true) {// 이름 중복 체크
							System.out.print("New name: ");
							newName = sc.next();
							boolean duplicateName = false;
							rs = stmt.executeQuery("select name from playlist where user_number=" + userNumber);
							while (rs.next())
								if (newName.equals(rs.getString(1))) {
									System.out.println("Duplicated name. Try another.");
									duplicateName = true;
									break;
								}
							if (!duplicateName)
								break;
						}

						stmt.executeUpdate("update playlist set name='" + newName + "' where number=" + playlistNumber);

						stmt.close();
						con.close();

						System.out.println("Changed");

					} else if (input == 2) {// insert music

						while (true) {
							con = DriverManager.getConnection(url, "root", "");
							stmt = con.createStatement();

							System.out.print("Name of the song to insert: ");
							String musicName = sc.next();
							int musicNumber = 0;

							rs = stmt.executeQuery("select count(number) from music where name='" + musicName + "'");

							int sameNameNumber = 0;
							if (rs.next())
								sameNameNumber = rs.getInt(1);

							if (sameNameNumber == 0) {
								System.out.println("No such music");
								continue;
							}
							if (sameNameNumber == 1) {
								rs = stmt.executeQuery("select number from music where name='" + musicName + "'");
								if (rs.next())
									musicNumber = rs.getInt(1);
							} else if (sameNameNumber >= 2) {
								rs = stmt.executeQuery(
										"select number, name, singer from music where name='" + musicName + "'");
								System.out.println("Which one?");
								int[] candidate = new int[sameNameNumber];
								int k = 0;
								while (rs.next()) {
									candidate[k] = rs.getInt(1);
									System.out.println(++k + ") " + rs.getString(2) + " - " + rs.getString(3));
								}
								System.out.print("input: ");
								int singer = sc.nextInt();
								musicNumber = candidate[singer - 1];
							}

							rs = stmt.executeQuery("select count(music_number) from contain where playlist_user="
									+ userNumber + " and playlist_number=" + playlistNumber + " and music_number="
									+ musicNumber);
							boolean alreadyExist = false;
							if (rs.next())
								if (rs.getInt(1) == 1)
									alreadyExist = true;

							if (alreadyExist) {
								System.out.println("The song already exists");
								continue;
							} else {
								stmt.executeUpdate("insert into contain values(" + userNumber + "," + playlistNumber
										+ "," + musicNumber + ")");
								System.out.println("Inserted");
							}

							rs.close();
							stmt.close();
							con.close();

							break;
						}

					} else if (input == 3) {// delete music

						while (true) {
							con = DriverManager.getConnection(url, "root", "");
							stmt = con.createStatement();

							System.out.print("Name of the song to delete: ");
							String musicName = sc.next();
							int musicNumber = 0;
							int sameNameNumber = 0;

							rs = stmt.executeQuery(
									"select count(distinct m.number) from playlist as p, contain as c, music as m where c.playlist_user="
											+ userNumber + " and c.playlist_number=" + playlistNumber
											+ " and c.music_number=m.number and m.name='" + musicName + "'");
							if (rs.next())
								sameNameNumber = rs.getInt(1);

							rs = stmt.executeQuery(
									"select distinct m.name, m.singer, m.number from playlist as p, contain as c, music as m where c.playlist_user="
											+ userNumber + " and c.playlist_number=" + playlistNumber
											+ " and c.music_number=m.number and m.name='" + musicName + "'");

							if (sameNameNumber == 0) {
								System.out.println("No such music");
								continue;
							}

							if (sameNameNumber == 1) {
								if (rs.next())
									musicNumber = rs.getInt(3);
							} else if (sameNameNumber >= 2) {
								int[] candidate = new int[sameNameNumber];
								int k = 0;
								System.out.println("Which one?");
								while (rs.next()) {
									candidate[k] = rs.getInt(3);
									System.out.println(++k + ") " + rs.getString(1) + " - " + rs.getString(2));
								}
								System.out.print("input: ");
								int singer = sc.nextInt();
								musicNumber = candidate[singer - 1];
							}

							stmt.executeUpdate("delete from contain where music_number=" + musicNumber);

							rs.close();
							stmt.close();
							con.close();

							System.out.println("Deleted");

							break;
						}

					} else if (input == 4) {// delete playlist

						con = DriverManager.getConnection(url, "root", "");
						stmt = con.createStatement();

						stmt.executeUpdate("delete from contain where playlist_number=" + playlistNumber
								+ " and playlist_user=" + userNumber);
						stmt.executeUpdate("delete from playlist where number=" + playlistNumber + " and user_number="
								+ userNumber);

						stmt.close();
						con.close();

						System.out.println("Deleted");

						break;

					}

				}
			} else
				System.out.println("Wrong Command Number");

		}

	}

	public void editMyProfile(int userNumber) throws Exception {

		while (true) {
			
			con = DriverManager.getConnection(url, "root", "");
			stmt = con.createStatement();

			rs = stmt.executeQuery(
					"select u.name, u.streamed_num, l.name, u.id, u.pw from user as u,license as l where u.license=l.number and u.number="
							+ userNumber);
			if (rs.next()) {
				System.out.println("-------------Profile------------");
				System.out.println("ID: " + rs.getString(4));
				System.out.println("PW: " + rs.getString(5));
				System.out.println("Name: " + rs.getString(1));
				System.out.println("License: " + rs.getString(3));
				System.out.println("Streamed number: " + rs.getInt(2));
			}

			rs.close();
			stmt.close();
			con.close();
			
			System.out.println("================================");
			System.out.println(
					"0. Return to previous menu\n1. Change name\n2. Change license\n3. Change password");
			System.out.println("================================");
			System.out.print("Input: ");
			int input = sc.nextInt();

			if (input == 0)
				break;

			else if (input == 1) {// 이름 바꾸기

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				System.out.print("New name: ");
				String newName = sc.next();

				stmt.executeLargeUpdate("update user set name='" + newName + "' where number=" + userNumber);

				System.out.println("Changed");

				stmt.close();
				con.close();

			} else if (input == 2) {// 이용권 바꾸기

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				int currentLicensce = 0;
				int newLicense;
				// 현재 이용중인 이용권 정보 보여주기
				rs = stmt.executeQuery(
						"select l.number, l.name,l.streamable_songs, l.fee from license as l, user as u where l.number=u.license and u.number="
								+ userNumber);
				if (rs.next()) {
					currentLicensce = rs.getInt(1);
					System.out.println("-----Currenct using license-----");
					System.out.println("License: " + rs.getString(2));
					System.out.println("Streamable songs: " + rs.getInt(3));
					System.out.println("Fee: " + rs.getInt(4));
				}

				// license 목록 보여주기
				rs = stmt.executeQuery("select * from license order by fee");
				System.out.println("==============Menu==============");
				while (rs.next()) {
					System.out.println(rs.getInt(1) + ". " + rs.getString(2));
					System.out.println("Streamable songs: " + rs.getInt(3));
					System.out.println("Fee: " + rs.getInt(4));
				}
				System.out.println("================================");

				System.out.println("Which license do you want?");
				System.out.print("Input: ");
				newLicense = sc.nextInt();

				// 번호 입력 받아서 정보 업데이트
				int max = 0;
				rs = stmt.executeQuery("select count(*) from license");
				if (rs.next())
					max = rs.getInt(1);

				if (newLicense > max)
					System.out.println("No such license");
				else if (currentLicensce != newLicense) {
					stmt.executeUpdate("update user set license=" + newLicense + " where number=" + userNumber);
					System.out.println("Changed");
				} else
					System.out.println("Same license - unchanged");

				rs.close();
				stmt.close();
				con.close();

			} else if (input == 3) {// 패스워드 바꾸기

				con = DriverManager.getConnection(url, "root", "");
				stmt = con.createStatement();

				System.out.print("New PW: ");
				String newPW = sc.next();

				stmt.executeLargeUpdate("update user set pw='" + newPW + "' where number=" + userNumber);

				System.out.println("Changed");

				stmt.close();
				con.close();

			} else
				System.out.println("Wrong Command Number");
		}
	}

	public void aboutMusics() throws Exception {

		con = DriverManager.getConnection(url, "root", "");
		stmt = con.createStatement();

		while (true) {

			System.out.println("================================");
			System.out.println(
					"0. Return to previous menu\n1. Retrieve by name\n2. Retrieve by singer\n3. Retrieve by genre\n4. Show music ranking");
			System.out.println("================================");
			System.out.print("lnput: ");

			int input = sc.nextInt();

			if (input == 0)
				break;

			if (input == 1) {

				System.out.print("Music name:");
				String name = sc.next();
				boolean exist = false;

				rs = stmt.executeQuery("select exists(select name, singer, genre, played_number from music where name='"
						+ name + "') as success");

				if (rs.next())
					exist = rs.getBoolean(1);

				if (exist) {
					rs = stmt.executeQuery("select name, singer, genre, played_number from music where name='" + name
							+ "' order by played_number desc");

					while (rs.next()) {
						System.out.println("--------------------------------");
						System.out.println("name: " + rs.getString(1));
						System.out.println("singer: " + rs.getString(2));
						System.out.println("genre: " + rs.getString(3));
						System.out.println("--------------------------------");
					}
				} else
					System.out.println("No such music");

			} else if (input == 2) {

				System.out.print("Singer:");
				String singer = sc.next();
				boolean exist = false;

				rs = stmt.executeQuery(
						"select exists(select name, singer, genre, played_number from music where singer='" + singer
								+ "') as success");

				if (rs.next())
					exist = rs.getBoolean(1);

				if (exist) {
					rs = stmt.executeQuery("select name, singer, genre, played_number from music where singer='"
							+ singer + "' order by played_number desc");

					while (rs.next()) {
						System.out.println("--------------------------------");
						System.out.println("name: " + rs.getString(1));
						System.out.println("singer: " + rs.getString(2));
						System.out.println("genre: " + rs.getString(3));
						System.out.println("--------------------------------");
					}
				} else
					System.out.println("No such singer");

			} else if (input == 3) {

				System.out.print("Genre :");
				String genre = sc.next();
				boolean exist = false;

				rs = stmt
						.executeQuery("select exists(select name, singer, genre, played_number from music where genre='"
								+ genre + "') as success");

				if (rs.next())
					exist = rs.getBoolean(1);

				if (exist) {
					rs = stmt.executeQuery("select name, singer, genre, played_number from music where genre='" + genre
							+ "' order by played_number desc");

					while (rs.next()) {
						System.out.println("--------------------------------");
						System.out.println("name: " + rs.getString(1));
						System.out.println("singer: " + rs.getString(2));
						System.out.println("genre: " + rs.getString(3));
						System.out.println("--------------------------------");
					}
				} else
					System.out.println("No such genre");

			} else if (input == 4) {

				rs = stmt.executeQuery("select name, singer from music order by played_number desc");

				int i = 1;
				System.out.println("--------------------------------");
				while (rs.next()) {
					System.out.println(i + ". " + rs.getString(1) + " - " + rs.getString(2));
					if (++i > 100)
						break;
				}
				System.out.println("--------------------------------");

			} else
				System.out.println("Wrong Command Number");

		}

		rs.close();
		stmt.close();
		con.close();
	}

}