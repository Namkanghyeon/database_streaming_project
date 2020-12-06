package db;

import java.util.Scanner;

public class DBproj {

	public static void main(String[] args) throws Exception {

		Scanner sc = new Scanner(System.in);
		AccessDB db = new AccessDB();

		while (true) {

			System.out.println("================================");
			System.out.println("LOG IN\n0. Exit\n1. Manager\n2. User\n3. Join membership");
			System.out.println("================================");
			System.out.print("Input: ");

			int input = sc.nextInt();

			if (input == 0) {

				sc.close();
				System.out.println("Bye");
				break;

			} else if (input == 1) {// manager

				boolean isPresident = false;

				while (true) {
					System.out.print("ID: ");
					String id = sc.next();
					System.out.print("PW: ");
					String pw = sc.next();
					String ID = db.isRightManagerID(id);
					if (ID != null && db.isRightManagerPW(id, pw)) {
						System.out.println("Welcome");
						if (ID.equals("president"))
							isPresident = true;
						break;
					}
					System.out.println("Wrong ID or PW");
				}

				while (true) {
					System.out.println("================================");
					System.out.println(
							"0. Log out\n1. Managing users\n2. Managing musics\n3. Managing managers\n4. Managing licenses");
					System.out.println("================================");
					System.out.print("Input: ");
					input = sc.nextInt();

					if (input == 0)
						break;
					else if (input == 1)
						db.manageUsers();
					else if (input == 2)
						db.manageMusics();
					else if (input == 3) {
						if (isPresident)
							db.manageManagers();
						else
							System.out.println("Only the president has this permission");
					} else if (input == 4)
						db.manageLicenses();
					else
						System.out.println("Wrong command number");
				}
			} else if (input == 2) {// user

				int userNumber;

				while (true) {
					System.out.print("ID: ");
					String id = sc.next();
					System.out.print("PW: ");
					String pw = sc.next();
					userNumber = db.isRightUserID(id);
					if (userNumber != -1 && db.isRightUserPW(id, pw)) {
						System.out.println("Welcome");
						break;
					}
					System.out.println("Wrong ID or PW");
				}

				while (true) {
					System.out.println("================================");
					db.showCurrectStreaming(userNumber);
					System.out.println(
							"0. Log out\n1. Listen to music\n2. Edit playlists\n3. Edit my profile\n4. About musics");
					System.out.println("================================");
					System.out.print("Input: ");
					input = sc.nextInt();

					if (input == 0)
						break;
					else if (input == 1)
						db.listenToMusic(userNumber);
					else if (input == 2)
						db.editPlaylist(userNumber);
					else if (input == 3)
						db.editMyProfile(userNumber);
					else if (input == 4)
						db.aboutMusics();
					else
						System.out.println("Wrong command number");
				}

			} else if (input == 3)// join
				db.joinMembership();
			else
				System.out.println("Wrong command number");

		}

	}
}