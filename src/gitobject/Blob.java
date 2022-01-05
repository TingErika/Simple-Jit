package gitobject;

import java.io.*;
import repository.Repository;
import sha1.SHA1;
import zlib.ZLibUtils;

public class Blob extends GitObject{
    
	public String getFmt(){
        return fmt;
    }
    public String getMode(){
        return mode;
    }
    public String getPath() {
        return path;
    }
    public String getValue(){
        return value;
    }
    public String getKey() { return key; }
    public Blob(){};
    /**
     * Constructing blob object from a file
     * @param file 文件对象
     * @throws Exception 异常
     */
    public Blob(File file) throws Exception {
        fmt = "blob";
        mode = "100644";
        value = getValue(file);
        key = genKey(file);
        //compressWrite(getPath()+File.separator+key.substring(0,2)+File.separator+key.substring(2));
    }

    /**
     * Deserialize a blob object from an existed hash file in .jit/objects.
     * @param Id Blob对象的key
     * @throws IOException 抛出IO异常
     */
    public static Blob deserialize(String Id) throws IOException, ClassNotFoundException {

        String parentPath= path+File.separator+Id.substring(0,2);
        String filename=Id.substring(2);
        String path=parentPath+File.separator+filename;
        FileInputStream fileInputStream=new FileInputStream(path);
        byte[] content=ZLibUtils.decompress(fileInputStream);
        InputStream inputStream=new ByteArrayInputStream(content);

        ObjectInputStream obj=new ObjectInputStream(inputStream);

        return (Blob) obj.readObject();
    }

    /**
     * Generate key from file.
     * @param file 文件对象
     * @throws Exception 异常
     */
    //Key为文件标志头加上内容的SHA-1值
    public String genKey(File file) throws Exception {

        String value=getValue(file);
        String content="100644 blob"+" "+value;
        key=SHA1.getHash(content);
        return key;

    }
    @Override
    public String toString(){

        return "100644 blob " + key ;
    }
}
