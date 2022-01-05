package gitobject;

import java.io.*;
import java.util.*;
import sha1.SHA1;
import zlib.ZLibUtils;

public class Tree extends GitObject{
    protected ArrayList<GitObject> treeList;	//GitObjects in tree

    public ArrayList<GitObject> getTreeList(){
        return treeList;
    }

    public static String treeValue;

    public Tree(){}

    /**
     * Constructor
     * @param file 文件对象
     * @throws Exception 抛出异常
     */
    public Tree(File file) throws Exception {
        this.treeList = genTreeList(file);
        this.fmt = "tree";
        this.mode = "040000";
        this.key = genKey(file);
        this.value = "040000 tree " + this.key + " " + file.getName() + treeValue;
        treeValue = "";
        //compressWrite();
    }


    /**
     * Deserialize a tree object with treeId and its path.

     * @param Id Tree对象的key值
     * @throws IOException 抛出IO异常
     */
    public static Tree deserialize(String Id) throws IOException, ClassNotFoundException {

        String parentPath= path+File.separator+Id.substring(0,2);
        String filename=Id.substring(2);
        String path=parentPath+File.separator+filename;
        FileInputStream fileInputStream=new FileInputStream(path);
        byte[] content= ZLibUtils.decompress(fileInputStream);
        InputStream inputStream=new ByteArrayInputStream(content);
        ObjectInputStream obj=new ObjectInputStream(inputStream);


        return (Tree) obj.readObject();

    }


    /**
     * Sort the files in a certain order. You should consider whether it's a file or a directory.
     * @param fs 文件对象数组
     * @return List<FILE> 文件对象List
     */
    public static List<File> sortFile(File[] fs){
        List<File> fileList = Arrays.asList(fs);
        fileList.sort(new Comparator<File>() {
            @Override //表示重写方法
            public int compare(File o1, File o2) {
                /* Todo: Add your code here. */
                if(o1.isDirectory() && o2.isFile()) {
                    return -1;
                }
                if(o1.isFile() && o2.isDirectory()) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());
            }

        });
        return fileList;
    }

    /**
     * Generate the key of a tree object.
     * @param dir 文件夹路径
     * @return String 文件夹key值
     * @throws Exception 抛出异常
     */
    public String genKey(File dir) throws Exception{
        /* Todo: Add your code here. */
        File[] fs =dir.listFiles();
        List<File> fileList=sortFile(fs);
        StringBuilder stringBuilder=new StringBuilder();
        for (File file : fileList) {
            if (file.isFile()) {
                stringBuilder.append(GitObject.getValue(file));
            }else if (file.isDirectory()){
                stringBuilder.append(genKey(file));
            }
        }

        String value=stringBuilder.toString();
        String content="040000 tree"+" "+value;
        return SHA1.getHash(content);
    }

    /**
     * Generate the key of a tree object.
     * @param dir 文件夹路径
     * @return String 文件夹value值
     * @throws Exception 抛出异常
     */
//    public String genValue(File dir, String key) throws Exception{
//
//        StringBuilder stringBuilder=new StringBuilder();
//        stringBuilder.append("040000 tree ").append(key).append(" ").append(dir.getName()).append("\n");
//        File[] fs =dir.listFiles();
//        List<File> fileList=sortFile(fs);
//        for (File file : fileList) {
//            stringBuilder.append(" ").append(file.getName());
//        }
//
//        String value=stringBuilder.toString();
//        return value;
//    }

    /**
     * Generate the treeList of a tree object.
     * @param dir 文件夹路径
     * @return ArrayList<String> 文件夹属性值
     * @throws Exception 抛出异常
     */
    public ArrayList<GitObject> genTreeList(File dir) throws Exception{
        /* Todo: Add your code here. */
        ArrayList<GitObject> treeList = new ArrayList<GitObject>();
        File[] fs =dir.listFiles();
        List<File> fileList=sortFile(fs);
        StringBuilder stringBuilder = new StringBuilder();
        for (File file : fileList) {
            if (file.isFile()) {
                Blob blob = new Blob(file);
                treeList.add(blob);
                stringBuilder.append("\n100644 blob ").append(blob.getKey()).append(" ").append(file.getName());
            }else if (file.isDirectory()){
                Tree tree = new Tree(file);
                treeList.add(tree);
                stringBuilder.append("\n040000 tree ").append(tree.getKey()).append(" ").append(file.getName());
            }
        }

        treeValue = stringBuilder.toString();

        return treeList;
    }

    @Override
    public String toString(){
        return "040000 tree " + key;
    }

}
