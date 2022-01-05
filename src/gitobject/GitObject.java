package gitobject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import fileoperation.FileCreation;
import fileoperation.FileDeletion;
import repository.Repository;
import zlib.ZLibUtils;


public class GitObject implements Serializable {

    protected String fmt;                  //type of object
    protected String key;                  //key of object
    protected String mode;                 //mode of object
    protected static String path = Repository.getGitDir() + File.separator + "objects";          //absolute path of objects
    protected String value;                //value of object

    public String getFmt(){
        return fmt;
    }
    public String getKey() { return key; }
    public String getMode(){
        return mode;
    }
    public String getPath() {
        return path;
    }
    public String getValue(){
        return value;
    }


    public GitObject(){}
    /**
     * Get the value(content) of file
     * @param file 文件对象
     * @return String 文件原生内容
     * @throws IOException IO异常抛出
     */
    public static String getValue(File file) throws IOException {
        /* Todo: Add your code here
        *   You should delete the return statement below before coding.  */

        FileInputStream fileInputStream=new FileInputStream(file);
        InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        String line;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            line=bufferedReader.readLine();
            while(line!=null){
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line=bufferedReader.readLine();
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return String.valueOf(stringBuilder);

    }


    /**
     * Todo: Serialize the object and compress with zlib.
     * @throws Exception 序列化异常
     */

     // compressWrite take place of writeObject
    public void compressWrite(String path, GitObject gitObject) throws Exception{


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(gitObject);
        byte[] data = byteArrayOutputStream.toByteArray();
        File file=new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ZLibUtils.compress(data, fileOutputStream);

    }

}
