package fileoperation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import gitobject.Blob;
import gitobject.GitObject;
import gitobject.Tree;

public class FileCreation {
	/**
	 * Create a file
	 * @param parentPath
	 * @param filename
	 * @param text
	 * @throws IOException
	 */
    public static void createFile(String parentPath, String filename, String text) throws IOException {
        if(!new File(parentPath).isDirectory()){
            throw new IOException("The path doesn't exist!");
        }

        File file = new File(parentPath + File.separator + filename);
        //创建的文件对象不能是重名文件夹或者文件
        if(file.isDirectory()) {
            throw new IOException(filename + " already exists, and it's not a file.");
        }
        if(text!=null){
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.close();
        }else{
            boolean flag=file.createNewFile();//jdk 17，创建空文件，按需引用
        }
        
        System.out.println("文件创建成功： "+file.getPath());

    }
    
    /**
     * Create a dir.
     * @param parentPath
     * @param paths
     * @throws IOException
     */
    public static void createDirectory(String parentPath, String... paths) throws IOException{
        boolean flag;
        if(!new File(parentPath).isDirectory()){
            throw new IOException("The path doesn't exist!");
        }
        StringBuilder path = new StringBuilder(parentPath);

        for (String s : paths) {
            path.append(File.separator).append(s);
        }
        System.out.println("文件夹创建成功： "+path);
        flag=new File(path.toString()).mkdirs();

    }

    /**
     * Recover files in working directory from a tree.
     * @param t
     * @param parentTree
     * @throws IOException
     */
    public static void recoverWorkTree(Tree t, String parentTree) throws IOException, ClassNotFoundException {
        ArrayList<String> list = FileReader.readByBufferReader(t.getValue());
        ArrayList<GitObject> treeList = t.getTreeList();
        for (String s : list) {

            if (FileReader.readObjectFmt(s).equals("blob")) {
                Blob blob = Blob.deserialize(FileReader.readObjectKey(s));
                String fileName = FileReader.readObjectFileName(s);
                createFile(parentTree, fileName, blob.getValue());
            } else {
                Tree tree = Tree.deserialize(FileReader.readObjectKey(s));
                String dirName = FileReader.readObjectFileName(s);
                createDirectory(parentTree, dirName);
                if(!dirName.equals("temp")) {
                    recoverWorkTree(tree, parentTree + File.separator + dirName);
                }
            }
        }
    }
}
