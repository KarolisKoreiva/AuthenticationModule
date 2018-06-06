package lt.baltic.talents.passwordRecognition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
	

	public static void main(String[] args) {
		
		System.out.println("Welcome to our top secret laboratory. Only authorized access available.");
		
		//Inicializuojames
		Scanner input = new Scanner(System.in);
		Authorization authorize = new Authorization();
		
		int menu = -1;
		String email = "";
		String password = "";
		
		//Pagrindinis menu ciklas
		do {
			System.out.println("Authorization menu. 1 - login, 2 - register, 3 - change password, 0 - exit");
			try {
				menu = input.nextInt();
				switch (menu) {
				case 1 : //prisijungimas
					input = new Scanner(System.in);
					System.out.println("Please enter your email: ");
					email = input.nextLine();
					System.out.println("Please enter your password: ");
					password = input.nextLine();
					if (email.equals(Authorization.powerUserEmail) && password.equals(Authorization.powerUserPassword)) {
						System.out.println("Administrator login successfull. Welcome. Now you can access all the secret data and manage users.");
						authorize.adminMenu(input);
					} else if (authorize.getUsers().containsKey(email) && authorize.getUser(email).getActivePassword().contentEquals(password)) {
							System.out.println("Login successfull. Welcome. Now you can access all the secret data.");
							authorize.getUser(email).updateNumberOfConnections();
							if (authorize.getUser(email).getNumberOfConnections() > 10) {
								authorize.passwordConnectionControl(input, email);
							}
							if (authorize.getUser(email).isPasswordExpired()) {
								authorize.passwordExpirationControl(input, email);
							}
					} else {
						System.out.println("Login failed. Try again.");
					}
					break;
					
				case 2 : //uzsiregistravimas
					input = new Scanner(System.in);
					System.out.println("Please enter your valid email for registration: ");
					email = input.nextLine();
					if (!authorize.checkEmailPatternValidity(email)) {
						System.out.println("Invalid email. Try again.");
						break;
					}
					System.out.println("Please enter your new password (min 8 symbols, min 2 digits and min any of 2 symbols: $#@!): ");
					password = input.nextLine();
					if (!authorize.checkPasswordPatternValidity(password)) {
						System.out.println("Password not according to the rules. Try again.");
						break;
					}
					try {
						if (authorize.getUser(email).isPasswordInUse(password)) {
							System.out.println("Password already used. Try again.");
							break;
						}
					} catch (NullPointerException e) {
						
					} finally {
						authorize.setUser(email, new User(password));
						System.out.println("User registered successfully");
					}
					break;
					
				case 3 : //slaptazodzio keitimas
					input = new Scanner(System.in);
					System.out.println("Please enter your registered email for password change: ");
					email = input.nextLine();
					System.out.println("Please enter your password");
					password = input.nextLine();
					if (authorize.getUsers().containsKey(email) && authorize.getUser(email).getActivePassword().contentEquals(password)) {
						authorize.passwordChangeControl(input, email);
					} else {
						System.out.println("Password change procedure have failed. Incorrect password or email. Try again.");
					}
					break;
					
				case 0: //uzsaugom i duombaze ir iseinam
					try {
						authorize.writeData(authorize.openWriteConnection(Authorization.FILENAME), authorize.getUsers());
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
					
				default: //kartojam
					System.out.println("No menu item. Try again.");
					break;
					
				}
			} catch (NoSuchElementException e) {
				System.out.println("It is not a number of menu. Try again");
				input.skip(".*");
			}
		} while (menu != 0);

		//Uzdarom ka zinom
		input.close();
	}

}
