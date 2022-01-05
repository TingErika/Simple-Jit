package gitobject;

import java.io.*;
import java.util.ArrayList;

import fileoperation.FileReader;
import sha1.SHA1;
import repository.Repository;
import zlib.ZLibUtils;

public class Commit extends GitObject{
    protected String tree; 		// the sha1 value of present committed tree
    protected String parent; 	// the sha1 value of the parent commit
    protected String author; 	// the author's name and timestamp
    protected String committer; // the committer's info
    protected String message; 	// the commit memo

    public String getParent(){return parent;}
    public String getTree(){return tree;}
    public String getAuthor(){return author;}
    public String getCommitter(){return committer;}
    public String getMessage(){return message;}

    public Commit(){}
    /**
     * Construct a commit directly from a file.
     * @param author, committer, message参数在git commit命令里创建
     * @throws Exception
     */
    public Commit(String treeKey, String author, String committer, String message) throws Exception {
        this.fmt = "commit"; 	//type of object
        this.tree = treeKey;
        this.parent = getLastCommit() == null ? "" : getLastCommit(); //null means there is no parent commit.
        this.author = author;
        this.committer = committer;
        this.message = message;

        /*Content of this commit, like this:
         *tree bd31831c26409eac7a79609592919e9dcd1a76f2
         *parent d62cf8ef977082319d8d8a0cf5150dfa1573c2b7
         *author xxx  1502331401 +0800
         *committer xxx  1502331401 +0800
         *修复增量bug
         * */
        this.value = "tree " + this.tree + "\nparent " + this.parent+ "\nauthor " + this.author + "\ncommitter " + this.committer + "\n" + this.message;
        
        this.key = genKey();
        //compressWrite();

        File HEAD = new File(Repository.getGitDir() + File.separator + "HEAD");

        String path = getValue(HEAD).substring(5).replace("\r\n", "");
        File branchFile = new File(Repository.getGitDir() + File.separator + path);

        if (!branchFile.isFile()) {
            branchFile.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(branchFile);
        fileWriter.write(this.key);
        fileWriter.close();

    }
    

    
    /**
     * Deserialize the commit object from its key(commitId)
     * @param commitId
     * @throws IOException
     */
    public static Commit deserialize(String commitId) throws IOException, ClassNotFoundException {

        String parentPath= path+File.separator+commitId.substring(0,2);
        String filename=commitId.substring(2);
        String path=parentPath+File.separator+filename;
        FileInputStream fileInputStream=new FileInputStream(path);
        byte[] content= ZLibUtils.decompress(fileInputStream);
        InputStream inputStream=new ByteArrayInputStream(content);
        ObjectInputStream obj=new ObjectInputStream(inputStream);

        return (Commit) obj.readObject();

    }
    /**
     * Generate the hash value of this commit.
     * @return key
     * */
    public String genKey() throws Exception {

        return SHA1.getHash(value);
    }




    /**
     * Get the parent commit from the HEAD file.
     * @return
     * @throws IOException
     */
    public static String getLastCommit() throws IOException {
        File HEAD = new File(Repository.getGitDir() + File.separator + "HEAD");

        String path = getValue(HEAD).substring(5).replace("\r\n", "");
        File branchFile = new File(Repository.getGitDir() + File.separator + path);

        if (branchFile.isFile()) {
            return getValue(branchFile);
        } else {
            return null;
        }
    }
}

