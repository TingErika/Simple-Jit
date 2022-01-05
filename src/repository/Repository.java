package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import fileoperation.FileCreation;

/**
 * Todo: Add your own code. JitInit.init("worktree") should be able to create a repository "worktree/.jit" ,
 *       which contains all the default files and other repositories inside. If the repository has
 *       already exists, delete the former one and create a new one. You're welcome to reconstruct the code,
 *       but don't change the code in the core directory.
 */
public class Repository {
    private static String workTree;	//working directory
    private static String gitDir;	//jit repository path

    /**
     * Constructor
     */
    //构造函数传参为空字符串需要抛出一个path为空的异常，repository不能被初始化
    public Repository() throws IOException {
        if(Objects.equals(gitDir, "")){
            throw new IOException("The repository does not exist!");
        }
    }
    
    /**
     * Construct a new repository instance with certain path.
     * Constructor
     * @param path
     * @throws IOException
     */
    public Repository(String path) throws IOException {
        workTree = path;
        gitDir = path + File.separator + ".jit";
    }

    public static String getGitDir() {
        return gitDir;
    }

    public static String getWorkTree() {
        return workTree;
    }
    
    /**
     * Helper functions.
     * @return null
     */
    public boolean exist(){ return new File(gitDir).exists(); }

    public boolean isFile(){ return new File(gitDir).isFile(); }

    public boolean isDirectory(){ return new File(gitDir).isDirectory(); }


    /**
     * Create the repository and files and directories inside.
     * @return boolean
     * @throws IOException
     */
    public void createRepo() throws IOException {
    /* Todo：Add your code here. */
        FileCreation.createDirectory(getWorkTree(),".jit");//创建仓库目录

        FileCreation.createFile(getGitDir(),"COMMIT_EDITMSG", null);//创建保存commit信息的文件
        FileCreation.createFile(getGitDir(),"config",null);//创建配置文件
        FileCreation.createFile(getGitDir(),"HEAD","ref: refs/heads/main");//创建“HEAD”文件，存储当前HEAD指向的分支
        FileCreation.createFile(getGitDir(),"description",null);//创建仓库描述文件，存储仓库名称等信息
        FileCreation.createDirectory(getGitDir(),"objects","info");//创建objects目录。保存blob,tree,commit的hash文件
        FileCreation.createDirectory(getGitDir(),"objects","pack");
        FileCreation.createDirectory(getGitDir(),"refs","heads");//创建refs目录，存储各分支信息
        FileCreation.createDirectory(getGitDir(),"refs","tags");
        FileCreation.createDirectory(getGitDir(),"logs");//创建logs目录，存储不同分支下的commit记录
        FileCreation.createDirectory(getGitDir(),"info");//创建info目录，存储仓库的其他信息

        FileCreation.createDirectory(getGitDir(),"hooks");//存储GIT命令需要用的自定义脚本，默认禁用

    }

}
