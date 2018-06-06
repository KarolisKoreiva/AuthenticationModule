package lt.baltic.talents.passwordRecognition;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

public interface ExternalDatabase {
	
	public ObjectInput openReadConnection(String fileName) throws FileNotFoundException, IOException;
	public ObjectOutput openWriteConnection(String fileName) throws FileNotFoundException, IOException;
	public Map<String, User> readData(ObjectInput reader) throws IOException;
	public void writeData(ObjectOutput writer, Map<String, User> users);
	//public void closeConnection(String fileName);

}
