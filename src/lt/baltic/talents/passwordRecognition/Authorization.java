package lt.baltic.talents.passwordRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Authorization implements ExternalDatabase {

	public static final String EMAIL_PATTERN = "^[A-Za-z0-9.!#$%&'*+-\\/=?^_`{|}~]+@[A-za-z0-9-]+\\.[A-Za-z0-9-]+$";
	public static final String PASSWORD_PATTERN = "^(?=.*[$#@!].*[$#@!])(?=.*\\d.*\\d).{8,}$";
	
	//Email, User with(password, numberofconnections and etc.)
	private Map<String, User> users;
	
	public static final String powerUserEmail = "admin@admin.lt";
	public static final String powerUserPassword = "admin";
	public static final String FILENAME = "resources/authentication.txt";
	
	public Authorization() {
		users = new HashMap<>();
		try {
			users = readData(openReadConnection(Authorization.FILENAME));
		} catch (FileNotFoundException e) {
			System.out.println("System used first time. New database created.");
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public Map<String, User> getUsers() {
		return users;
	}

	public void setUser(String email, User user) {
		users.put(email, user);
	}
	
	public User getUser(String email) {
		return users.get(email);
	}
	
	public boolean checkPasswordPatternValidity(String password) {
		return password.matches(PASSWORD_PATTERN);
	}
	
	public boolean checkEmailPatternValidity(String email) {
		return email.matches(EMAIL_PATTERN);
	}
	
	public void passwordConnectionControl(Scanner input, String email) {
		System.out.println("Password used more than 10 times. Please change your password.");
		do {
			System.out.println("Please enter your new password (min 8 symbols, min 2 digits and min any of 2 symbols: $#@!): ");
			String password = input.nextLine();
			if (!checkPasswordPatternValidity(password)) {
				System.out.println("Password not according to the rules. Try again.");
				continue;
			}
			if (getUser(email).isPasswordInUse(password)) {
				System.out.println("Password already used. Try again.");
				continue;
			}
			getUser(email).setPassword(password);
			getUser(email).resetConnections();
			System.out.println("Password changed successfully");
			break;
		} while (true);
	}

	public void passwordExpirationControl(Scanner input, String email) {
		System.out.println("Password used more than 3 months. Please change your password.");
		do {
			System.out.println("Please enter your new password (min 8 symbols, min 2 digits and min any of 2 symbols: $#@!): ");
			String password = input.nextLine();
			if (!checkPasswordPatternValidity(password)) {
				System.out.println("Password not according to the rules. Try again.");
				continue;
			}
			if (getUser(email).isPasswordInUse(password)) {
				System.out.println("Password already used. Try again.");
				continue;
			}
			getUser(email).setPassword(password);
			getUser(email).resetExpiration();
			System.out.println("Password changed successfully");
			break;
		} while (true);
		
	}

	public void passwordChangeControl(Scanner input, String email) {
		do {
			System.out.println("Please enter your new password (min 8 symbols, min 2 digits, and min any of 2 symbols: #@!): ");
			String password = input.nextLine();
			if (!checkPasswordPatternValidity(password)) {
				System.out.println("Password not according to the rules. Try again.");
				break;
			}
			if (getUser(email).isPasswordInUse(password)) {
				System.out.println("Password already used. Try again.");
				break;
			}
			getUser(email).setPassword(password);
			System.out.println("Password changed successfully");
			break;
		} while(true);
	}

	@SuppressWarnings("resource")
	public void adminMenu(Scanner input) {
		int menu = -1;
		String key;
		do {
			System.out.println("Administrator menu: 1 - print users, 2 - delete user, 3 - change user email, 4 - change user password, 0 - exit");
			input = new Scanner(System.in);
			menu = input.nextInt();
			switch (menu) {
			case 1 : //print users
				int i = 1;
				if (users.isEmpty()) {
					System.out.println("No users registered");
				}
				for (String key1 : users.keySet()) {
					System.out.println(i + " email: " + key1 + " " + users.get(key1));
				}
				break;
			case 2 : //delete user
				System.out.println("Enter user email to delete user: ");
				input = new Scanner(System.in);
				key = input.nextLine();
				users.remove(key);
				System.out.println("User deleted succesfully");
				break;
			case 3 : // change email
				System.out.println("Enter user email to change that user email: ");
				input = new Scanner(System.in);
				String keyOld = input.nextLine();
				System.out.println("Enter new user email: ");
				key = input.nextLine();
				User oldUser = users.get(keyOld);
				users.put(key,  oldUser);
				users.remove(keyOld);
				System.out.println("User email changed successfully");
				break;
			case 4 : //change password
				System.out.println("Enter user email to change that user password: ");
				input = new Scanner(System.in);
				key = input.nextLine();
				System.out.println(key + " password list: " + users.get(key).getPasswordList().toString());
				System.out.println("Enter password number to change it: ");
				int index = input.nextInt();
				System.out.println("Enter new password: ");
				String newPassword = input.nextLine();
				users.get(key).getPasswordList().get(index).replaceAll(".+", newPassword);
				System.out.println("Password changed successfully");
				break;
			case 0 : //baigiam
				break;
			default: 
				System.out.println("No menu item. Try again");
				break;
			}
		} while (menu != 0);
		
	}

	@Override
	public ObjectInput openReadConnection(String fileName) throws FileNotFoundException, IOException {
		return new ObjectInputStream(new FileInputStream(new File(fileName)));
	}

	@Override
	public ObjectOutput openWriteConnection(String fileName) throws FileNotFoundException, IOException {
		return new ObjectOutputStream(new FileOutputStream(new File(fileName)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, User> readData(ObjectInput reader) throws IOException {
		try {
			return (Map<String, User>) reader.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			reader.close();
		}
	}

	@Override
	public void writeData(ObjectOutput writer, Map<String, User> users) {
		try {
			try {
				writer.writeObject(users);
				writer.flush();
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
