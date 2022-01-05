package core;

import java.io.*;


import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

import fileoperation.FileReader;
import gitobject.GitObject;
import gitobject.Blob;
import gitobject.Tree;
import gitobject.Commit;
import fileoperation.*;
import repository.Repository;
import sha1.SHA1;

public class JitHash {

    private static Object FileWriter;

    /**
     * git add命令的具体实现
     * @param filename 文件名绝对路径
     * @throws IOException 抛出IO异常
     */
    //public static int count=0;
    public static void hash(String filename,String repoPath) throws Exception {
        /* Todo: You should pass the filename in this function, and generate a hash file in your repository.
         *   Add your code here.*/

        String projPath = repoPath.replaceAll(".jit","");
        File file = new File(filename);

        //Blob hash
        if (file.isFile()) {

            Blob blob = new Blob(file);
            String hashRes = blob.getKey();

            if (hashRes.length() != 40) {
                throw new IOException("hash value length error!");

            }
            String parentPath = blob.getPath();//Blob类getPath()需要借助Repository类的加载


            File check_directory_exist = new File(parentPath + File.separator + hashRes.substring(0, 2));
            if (!check_directory_exist.exists()) {
                FileCreation.createDirectory(parentPath, hashRes.substring(0, 2));

            }



            File check_blob_exist = new File(parentPath + File.separator + hashRes.substring(0, 2) + File.separator + hashRes.substring(2));
            if (!check_blob_exist.exists()) {

                FileCreation.createFile(parentPath + File.separator + hashRes.substring(0, 2), hashRes.substring(2), null);
                String path = parentPath + File.separator + hashRes.substring(0, 2) + File.separator + hashRes.substring(2);

                blob.compressWrite(path, blob);//对象序列化,压缩后写入.jit/objects
            }


            File index_file = new File(repoPath + File.separator + "index");

            if (!index_file.exists()) {
                FileCreation.createFile(repoPath, "index", null);
            }

            String relativePath = file.getPath().replaceAll(projPath.replace("\\", "\\\\"), "");
            String line = "100644" + " " + hashRes + " " + "0" + " " + relativePath + "\n";

            FileInputStream in = new FileInputStream(index_file);
            // size 为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer = new byte[size];
            int flag = in.read(buffer);
            in.close();
            String str = new String(buffer, StandardCharsets.UTF_8);
            if (!str.contains(line)) {
                if (!str.contains(hashRes) && str.contains(relativePath)) {
                    //add了一个修改过内容却没修改过文件名的处理方法
                    //file.getPath()中有\,需要进行转义
                    String filePath = relativePath.replace("\\", "\\\\");
                    String regex = "\n.*?" + filePath;
                    String newStr = str.replaceAll(regex, "");

                    FileWriter fileWriter = new FileWriter(index_file);
                    fileWriter.write(newStr);
                    fileWriter.close();
                }

                FileWriter fileWriter = new FileWriter(index_file, true);

                fileWriter.write(line);

                fileWriter.close();
            }

        }


        //Tree hash
        if (file.isDirectory()) {
            File[] fs = file.listFiles();

            List<File> fileList = Tree.sortFile(fs);
            for (File f : fileList) {
                if (!f.getName().equals(".jit")){
                    JitHash.hash(f.getPath(),repoPath);
                }

            }


        }

    }

    /**
     * git commit命令的具体实现
     * @param message 提交消息
     * @throws IOException 抛出IO异常
     */
    public static void commit(String repoPath, String message) throws Exception {

        FileInputStream in = new FileInputStream(repoPath + File.separator + "index");
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));

        String line;
        while((line = buffer.readLine()) != null) {
            String[] list = line.split(" ");
            Blob blob = Blob.deserialize(list[1]);
            //临时tree filePath: ../.jit/temp/相对路径
            String filePath = repoPath + File.separator + "temp" + File.separator + list[3];
            int index = filePath.lastIndexOf('\\');
            String dirPath = filePath.substring(0, index);
            String filename = filePath.substring(index+1);
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(dirPath, filename);
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(blob.getValue());
            fileWriter.close();
        }

        in.close();
        buffer.close();

        File file = new File(repoPath + File.separator + "temp");
        Tree tree = new Tree(file);

        String hashRes = tree.getKey();
        if (hashRes.length() != 40) {
            throw new IOException("hash value length error!");
        }
        String parentPath = tree.getPath(); //Tree类getPath()需要借助Repository类的加载

        File check_directory_exist = new File(parentPath + File.separator + hashRes.substring(0, 2));
        if (!check_directory_exist.exists()) {
            FileCreation.createDirectory(parentPath, hashRes.substring(0, 2));
        }

        File check_tree_exist = new File(parentPath + File.separator + hashRes.substring(0, 2) + File.separator + hashRes.substring(2));
        if (!check_tree_exist.exists()) {
            FileCreation.createFile(parentPath + File.separator + hashRes.substring(0, 2), hashRes.substring(2), null);
            String path = parentPath + File.separator + hashRes.substring(0, 2) + File.separator + hashRes.substring(2);
            tree.compressWrite(path, tree); //对象序列化,压缩后写入.jit/objects
        }

        FileDeletion.deleteFile(file);


        InetAddress addr = InetAddress.getLocalHost();
        Date date = new Date();
        String hostname = addr.getHostName() + " " + date.getTime()/1000 + " +0800";
        Commit commit = new Commit(tree.getKey(), hostname, hostname, message);

        hashRes = commit.getKey();
        if (hashRes.length() != 40) {
            throw new IOException("hash value length error!");
        }

        check_directory_exist = new File(parentPath + File.separator + hashRes.substring(0, 2));
        if (!check_directory_exist.exists()) {
            FileCreation.createDirectory(parentPath, hashRes.substring(0, 2));
        }

        File check_commit_exist = new File(parentPath + File.separator + hashRes.substring(0, 2) + File.separator + hashRes.substring(2));
        if (!check_commit_exist.exists()) {
            FileCreation.createFile(parentPath + File.separator + hashRes.substring(0, 2), hashRes.substring(2), null);
            String path = parentPath + File.separator + hashRes.substring(0, 2) + File.separator + hashRes.substring(2);
            commit.compressWrite(path, commit); //对象序列化,压缩后写入.jit/objects
        }
        // 第一次commit时创建main分支
        file = new File(repoPath + File.separator + "refs" +File.separator + "heads");
        if(file.list().length == 0) {
            FileCreation.createFile(file.getPath(), "main", hashRes);
        }

    }


    /**
     * git rm命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void remove(String repoPath, String filename) throws Exception {

        //定位工作区
        String workingPath=repoPath.replace(".jit","");
        if(!filename.contains(workingPath)){
            throw new IOException("filename is illegal");
        }
        if(!new File(filename).exists()){
            throw new IOException("file/directory does not exist");
        }
        if (new File(filename).isFile()){
            String relativePath=filename.replaceAll(workingPath.replace("\\","\\\\"),"");

            //删除index文件里相应的行
            File index_file = new File(repoPath + File.separator + "index");
            FileInputStream in = new FileInputStream(index_file);

            // size 为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer = new byte[size];
            int flag = in.read(buffer);
            in.close();
            String str = new String(buffer, StandardCharsets.UTF_8);

            //获取相对路径在Index中的位置以及blob对应的hash
            int file_index=str.lastIndexOf(relativePath);
            if (file_index!=-1){

                String file_hash=str.substring(file_index-43,file_index-3);


                String filePath = relativePath.replace("\\", "\\\\");
                String regex = "\n.*?" + filePath;
                String newStr = str.replaceAll(regex, "");

                //重写index文件
                FileWriter fileWriter = new FileWriter(index_file);
                fileWriter.write(newStr);
                fileWriter.close();


                //删除.jit/objects目录下的相应blob
                String dir=file_hash.substring(0,2);
                String file=file_hash.substring(2);
                String deletePath=repoPath+File.separator+"objects"+File.separator+dir+File.separator+file;
                FileDeletion.deleteFile(deletePath);
            }
        }
        //递归删除文件夹下的文件
        if(new File(filename).isDirectory()){

            File[] fs = new File(filename).listFiles();
            List<File> fileList = Tree.sortFile(fs);
            for (File f : fileList) {
                if (!f.getName().equals(".jit")){
                    JitHash.remove(repoPath,f.getPath());
                }

            }
        }


    }


    /**
     * git log命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void log() throws IOException, ClassNotFoundException {

        //取最新commit
        File HEAD = new File(Repository.getGitDir() + File.separator + "HEAD");
        String path = GitObject.getValue(HEAD).substring(5).replace("\r\n", "");
        File branchFile = new File(Repository.getGitDir() + File.separator + path);

        //path末尾有两个无效字符需要切掉
        String newest_commit=GitObject.getValue(branchFile).substring(0,40);


        //commit对象解压缩、反序列化
        Commit commit=Commit.deserialize(newest_commit);

        //用链表存成commit链
        LinkedList<Commit> commit_list=new LinkedList<>();
        commit_list.add(commit);
        if(commit.getParent()!=null){
            do {
            String next_commit=commit.getParent().substring(0,40);
            commit= Commit.deserialize(next_commit);
            commit_list.add(commit);

            } while (!Objects.equals(commit.getParent(), ""));
        }



        System.out.println("-------------------------------jit log-----------------------------------");
        for(Commit c: commit_list){
            String[] list = c.getAuthor().split(" ");
            String author = list[0];
            Date date = new Date(Long.parseLong(list[1]));
            System.out.println("commit:  " + c.getKey());
            System.out.println("Author:  " + author);
            System.out.println("Date:  " + date + " " + list[2]);
            System.out.println("message:  " + c.getMessage());
            System.out.println("-------------------------------------------------------------------------");
        }


    }

    /**
     * git reset命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void reset(String repoPath, String mode, String commitId) throws IOException, ClassNotFoundException {

        //检查commit对象是否存在
        Commit check_exist=Commit.deserialize(commitId);
        if(check_exist==null){
            throw new IOException("需要reset的commit对象不存在~~");
        }
        //获取当前分支指向的commit对象
        File HEAD = new File(repoPath + File.separator + "HEAD");
        String path = GitObject.getValue(HEAD).substring(5).replace("\r\n", "");
        File branchFile = new File(Repository.getGitDir() + File.separator + path);

        //重写commit指针
        FileWriter fileWriter = new FileWriter(branchFile);
        fileWriter.write(commitId);
        fileWriter.close();
        switch (mode){
            case "--soft" ->{
                break;
            }
            case "--mixed" ->{
                //重置index暂存区
                Commit commit = Commit.deserialize(commitId);
                Tree tree = Tree.deserialize(commit.getTree());
                File file = new File(repoPath + File.separator + "index");
                if(file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                recoverIndex(tree, "", file);
                break;
            }
            case "--hard" ->{
                //重置工作区
                Commit commit = Commit.deserialize(commitId);
                Tree tree = Tree.deserialize(commit.getTree());
                int index = repoPath.lastIndexOf('\\');
                String workDirectory = repoPath.substring(0, index);
                File file = new File(workDirectory);
                File[] fs = file.listFiles();
                for(File f : fs) {
                    if(!f.getName().equals(".jit")) {
                        FileDeletion.deleteFile(f);
                    }
                }
                recoverWorkTree(tree, workDirectory);

            }

        }

    }

    /**
     * git branch命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void branch(String repoPath) throws IOException {
        File HEAD = new File(Repository.getGitDir() + File.separator + "HEAD");
        String path = GitObject.getValue(HEAD).substring(5).replace("\r\n", "");
        int index = path.lastIndexOf('/');
        String head = path.substring(index+1);
        File file = new File(repoPath + File.separator + "refs" + File.separator + "heads");
        if(file.isDirectory()) {
            File[] fs = file.listFiles();
            for(File f : fs) {
                if(f.getName().equals(head)) {
                    System.out.println("* " + head);
                }
                else {
                    System.out.println("  " + f.getName());
                }
            }
        }
    }

    /**
     * git branch命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void createBranch(String repoPath, String branchName) throws IOException {
        //取当前commit
        File HEAD = new File(Repository.getGitDir() + File.separator + "HEAD");
        String path = GitObject.getValue(HEAD).substring(5).replace("\r\n", "");
        File branchFile = new File(Repository.getGitDir() + File.separator + path);

        //path末尾有两个无效字符需要切掉
        String current_commit=GitObject.getValue(branchFile).substring(0,40);

        File file = new File(repoPath + File.separator + "refs" + File.separator + "heads" + File.separator + branchName);
        if(!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(current_commit);
        fileWriter.close();
    }

    /**
     * git branch命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void deleteBranch(String repoPath, String branchName) throws Exception {
        File HEAD = new File(Repository.getGitDir() + File.separator + "HEAD");
        String path = GitObject.getValue(HEAD).substring(5).replace("\r\n", "");
        int index = path.lastIndexOf('/');
        String head = path.substring(index+1);

        if(branchName.equals(head)) {
            throw new IllegalArgumentException("error: Cannot delete branch " + branchName);
        }
        else {
            FileDeletion.deleteFile(repoPath + File.separator + "refs" + File.separator + "heads" + File.separator + branchName);
        }
    }

    /**
     * git checkout命令的具体实现
     * @throws IOException 抛出IO异常
     */
    public static void checkout(String repoPath, String branchName) throws IOException, ClassNotFoundException {
        File file = new File(repoPath + File.separator + "refs" + File.separator + "heads" + File.separator + branchName);
        if(!file.exists()) {
            throw new IOException("需要checkout的分支不存在~~");
        }
        //path末尾有两个无效字符需要切掉
        String current_commit=GitObject.getValue(file).substring(0,40);

        //更新head
        FileDeletion.deleteFile(repoPath + File.separator + "HEAD");
        FileCreation.createFile(repoPath,"HEAD","ref: refs/heads/"+branchName);//创建“HEAD”文件，存储当前HEAD指向的分支

        //更新工作区
        JitHash.reset(repoPath, "--hard", current_commit);
    }

    /**
     * Recover files in working directory from a tree.
     * @param t
     * @param parentTree
     * @throws IOException
     */
    public static void recoverIndex(Tree t, String parentTree, File indexFile) throws IOException {
        ArrayList<String> list = FileReader.readByBufferReader(t.getValue());
        ArrayList<GitObject> treeList = t.getTreeList();
        Iterator iterator = treeList.iterator();
        boolean isRootDir = true;

        for (String s : list) {

            if (FileReader.readObjectFmt(s).equals("blob")) {
                Blob blob = (Blob)iterator.next();
                String fileName = FileReader.readObjectFileName(s);
                String filePath = parentTree + File.separator + fileName;
                String line = "100644" + " " + blob.getKey() + " " + "0" + " " + filePath + "\n";
                FileWriter fileWriter = new FileWriter(indexFile, true);
                fileWriter.write(line);
                fileWriter.close();
            }
            else {
                String dirName = FileReader.readObjectFileName(s);
                if(isRootDir) {
                    isRootDir = false;
                }
                else {
                    Tree tree = (Tree)iterator.next();
                    if(parentTree.equals("")) {
                        recoverIndex(tree, dirName, indexFile);
                    }
                    else {
                        recoverIndex(tree, parentTree + File.separator + dirName, indexFile);
                    }
                }
            }
        }
    }

    /**
     * Recover files in working directory from a tree.
     * @param t
     * @param parentTree
     * @throws IOException
     */
    public static void recoverWorkTree(Tree t, String parentTree) throws IOException {
        ArrayList<String> list = FileReader.readByBufferReader(t.getValue());
        ArrayList<GitObject> treeList = t.getTreeList();
        Iterator iterator = treeList.iterator();
        boolean isRootDir = true;

        for (String s : list) {

            if (FileReader.readObjectFmt(s).equals("blob")) {
                String fileName = FileReader.readObjectFileName(s);
                Blob blob = (Blob)iterator.next();
                FileCreation.createFile(parentTree, fileName, blob.getValue());
            }
            else {
                String dirName = FileReader.readObjectFileName(s);
                if(isRootDir) {
                    isRootDir = false;
                }
                else {
                    Tree tree = (Tree)iterator.next();
                    FileCreation.createDirectory(parentTree, dirName);
                    recoverWorkTree(tree, parentTree + File.separator + dirName);
                }
            }
        }
    }
}
