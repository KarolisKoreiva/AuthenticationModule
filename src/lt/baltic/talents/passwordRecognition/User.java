package lt.baltic.talents.passwordRecognition;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int numberOfConnections = 0;
	private LocalDateTime validityStart;
	private List<String> passwordList;
	
	public User(String password){
		passwordList = new ArrayList<>();
		passwordList.add(password);
		validityStart = LocalDateTime.now();
		numberOfConnections++;
	}

	public int getNumberOfConnections() {
		return numberOfConnections;
	}

	public void updateNumberOfConnections() {
		numberOfConnections++;
	}

	public LocalDateTime getValidityStart() {
		return validityStart;
	}

	public List<String> getPasswordList() {
		return passwordList;
	}
	
	public String getActivePassword() {
		return passwordList.get(passwordList.size() - 1);
	}
	
	public void setPassword(String password) {
		if (passwordList.size() < 10) {
			passwordList.add(password);
		} else {
			passwordList.remove(passwordList.size() - 1);
			passwordList.add(password);
		}
		
	}
	
	public boolean isPasswordInUse(String password) {
		if (!passwordList.isEmpty()) {
			for (String p : passwordList) {
				if (p.equals(password)) {
					return true;
				}
			}
		}
		return false; 
	}
	
	public void resetConnections() {
		numberOfConnections = 0;
	}
	
	public void resetExpiration() {
		validityStart = LocalDateTime.now();
	}
	
	public boolean isPasswordExpired() {
		if (LocalDateTime.now().isAfter(validityStart.plusMonths(3))) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "User [Number of connections: " + numberOfConnections + ", Registered date: " + validityStart
				+ ", passwordList=" + passwordList + "]";
	}
	
	
}
