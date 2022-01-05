package fileoperation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDeletion {
    /**
     * Delete file.
     * @param file
     */
	public static void deleteFile(File file){
        boolean flag;
        if(!file.exists())return;
        if(file.isDirectory()) {
            File[] fileList = file.listFiles();
            assert fileList != null;
            for (File value : fileList) {
                deleteFile(value);
            }
        }
        flag=file.delete();
    }
    
	/**
	 * Delete file.
	 * @param path
	 */
    public static void deleteFile(String path){
        deleteFile(new File(path));
    }
    
    /**
     * Delete the content of a file.
     * @param file
     */
    public static void deleteContent(File file) {
        try {
            boolean flag;
            if(!file.exists()) {
                flag=file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
