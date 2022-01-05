package sha1;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public abstract class SHA1 {
	
	/**
	 * Computing hash value with SHA1 algo. 
	 * @param is
	 * @return
	 * @throws Exception
	 */
    public static byte[] SHA1Checksum(InputStream is) throws Exception{
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        while(numRead != -1) {
            numRead = is.read(buffer);
            if(numRead > 0) {
                complete.update(buffer,0,numRead);
            }
        }
        is.close();
        return complete.digest();
    }
    
    /**
     * Converting hash value to String.
     * @param sha1
     * @return
     */
    public static String getHash(byte[] sha1){
        StringBuilder hashValue = new StringBuilder();
        for (byte b : sha1) {
            hashValue.append(Integer.toString((b >> 4) & 0x0F, 16)).append(Integer.toString(b & 0x0F, 16));
        }
        return hashValue.toString();
    }
    
    /**
     * Getting hash value of a file.
     * @param file
     * @return
     * @throws Exception
     */
    public static String getHash(File file) throws Exception {
        byte[] sha1 = SHA1Checksum(new FileInputStream(file));
        return getHash(sha1);
    }
    
    /**
     * Getting hash value of a String.
     * @param value
     * @return
     * @throws Exception
     */
    public static String getHash(String value) throws Exception {
        byte[] sha1 = SHA1Checksum(new ByteArrayInputStream(value.getBytes()));
        return getHash(sha1);
    }
}
