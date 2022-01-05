package commander;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;


import core.*;
import fileoperation.FileDeletion;
import gitobject.Commit;
import repository.Repository;

public class CLI {	
	
	/**
	 * Command 'jit init repoPath filename'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitInit(String[] args) throws IOException {
		String path = "";

		if(args.length <= 2) { //get default working path
			path = new File(".").getCanonicalPath();//获取当前工作路径
			//System.out.println(path);
			JitInit.init(path);
		}else if(args[2].equals("-help")){ //see help
			System.out.println("usage: jit init [<path>] [-help]\r\n" +
					"\r\n" +
					"jit init [<path>]:	Create an empty jit repository or reinitialize an existing one in the path or your default working directory.");
		}else {
			path = args[2];
			if(!new File(path).isDirectory()) { //if the working path input is illegal
				System.out.println(path + "is not a legal directory. Please init your reposiroty again. See 'jit init -help'.");
			}else {
				JitInit.init(path);
			}
		}
	}

	/**
	 * Command 'jit add repoPath'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitAdd(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
        if(repo.exist()){
            if(repo.isDirectory()){

				File file=new File(args[3]);
				if (file.isDirectory() && Objects.requireNonNull(file.list()).length==0){
					throw new IOException("directory is empty,invalid~");
				}



                JitHash.hash(args[3], Repository.getGitDir());
            }
            else if(repo.isFile()){
                throw new IOException("check your repository address");

            }else {
				throw new IOException("file path does not exist");
			}

            }

        }

	/**
	 * Command 'jit commit repoPath -m msg'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitCommit(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
		if(repo.exist()){
			if(repo.isDirectory()){
				JitHash.commit(Repository.getGitDir(), args[4]);
			}
			else if(repo.isFile()){
				throw new IOException("check your repository address");

			}else {
				throw new IOException("file path does not exist");
			}

		}

	}

	/**
	 * Command 'jit rm repoPath filename'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitRemove(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
		if(repo.exist()){
			if(repo.isDirectory()){

				JitHash.remove(Repository.getGitDir(), args[3]);
			}
			else if(repo.isFile()){
				throw new IOException("check your repository address");

			}else {
				throw new IOException("file path does not exist");
			}

		}

	}

	/**
	 * Command 'jit log repoPath'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitLog(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
		if(repo.exist()){
			if(repo.isDirectory()){

				JitHash.log();
			}
			else if(repo.isFile()){
				throw new IOException("check your repository address");

			}else {
				throw new IOException("file path does not exist");
			}

		}

	}


	/**
	 * Command 'jit reset repoPath <mode> <commitId>'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitReset(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
		if(repo.exist()){
			if(repo.isDirectory()){
				if (!Objects.equals(args[3], "--soft") && !Objects.equals(args[3], "--hard") && !Objects.equals(args[3], "--mixed")){
					throw new IllegalArgumentException("mode参数错误~~");
				}
				if(args[4].length()!=40){
					throw new IllegalArgumentException("commitId错误~~");
				}
				JitHash.reset(Repository.getGitDir(),args[3],args[4]);

			}
			else if(repo.isFile()){
				throw new IOException("check your repository address");

			}else {
				throw new IOException("file path does not exist");
			}

		}

	}

	/**
	 * Command 'jit branch repoPath [-d] [<branchName>]'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitBranch(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
		if(repo.exist()){
			if(repo.isDirectory()){
				if (args.length==3){
					JitHash.branch(Repository.getGitDir());
				}
				else if (args[3].equals("-d")){
					if(args.length==4) {
						throw new IllegalArgumentException("缺少branchName~~");
					}
					else {
						JitHash.deleteBranch(Repository.getGitDir(), args[4]);
					}
				}
				else {
					JitHash.createBranch(Repository.getGitDir(), args[3]);
				}
			}
			else if(repo.isFile()){
				throw new IOException("check your repository address");

			}
			else {
				throw new IOException("file path does not exist");
			}

		}

	}

	/**
	 * Command 'jit checkout repoPath [-b] [<branchName>]'
	 * @param args 命令行参数
	 * @throws IOException IO异常
	 */
	public static void jitCheckout(String[] args) throws Exception {

		Repository repo = new Repository(args[2]);
		if(repo.exist()){
			if(repo.isDirectory()){
				if (args[3].equals("-b")){
					if(args.length==4) {
						throw new IllegalArgumentException("缺少branchName~~");
					}
					else {
						JitHash.createBranch(Repository.getGitDir(), args[4]);
						JitHash.checkout(Repository.getGitDir(), args[4]);
					}
				}
				else {
					JitHash.checkout(Repository.getGitDir(), args[3]);
				}
			}
			else if(repo.isFile()){
				throw new IOException("check your repository address");

			}else {
				throw new IOException("file path does not exist");
			}

		}

	}

	/**
	 * Command 'jit help'.
	 */
	public static void jitHelp() {
		System.out.println("usage: jit [--version] [--help] [-C <path>] [-c name=value]\r\n" +
				"           [--exec-path[=<path>]] [--html-path] [--man-path] [--info-path]\r\n" +
				"           [-p | --paginate | --no-pager] [--no-replace-objects] [--bare]\r\n" +
				"           [--git-dir=<path>] [--work-tree=<path>] [--namespace=<name>]\r\n" +
				"           <command> [<args>]\r\n" +
				"\r\n" +
				"These are common Jit commands used in various situations:\r\n" +
				"\r\n" +
				"start a working area\r\n" +
				"   init       Create an empty Jit repository or reinitialize an existing one\r\n" +
				"\r\n" +
				"work on the current change\r\n" +
				"   add        Add file contents to the index\r\n" +
				"   reset      Reset current HEAD to the specified state\r\n" +
				"   rm         Remove files from the working tree and from the index\r\n" +
				"\r\n" +
				"examine the history and state\r\n" +
				"   log        Show commit logs\r\n" +
				"   status     Show the working tree status\r\n" +
				"\r\n" +
				"grow, mark and tweak your common history\r\n" +
				"   branch     List, create, or delete branches\r\n" +
				"   checkout   Switch branches or restore working tree files\r\n" +
				"   commit     Record changes to the repository\r\n" +
				"   diff       Show changes between commits, commit and working tree, etc\r\n" +
				"   merge      Join two or more development histories together\r\n" +
				"\r\n" +
				"'jit help -a' and 'jit help -g' list available subcommands and some\r\n" +
				"concept guides. See 'jit help <command>' or 'jit help <concept>'\r\n" +
				"to read about a specific subcommand or concept.");
	}
	
	public static void main(String[] args) throws Exception {

		if(args.length <= 1 || args[1].equals("help")) {
			jitHelp();
		}else {
			switch (args[1]) {
				case "init" -> jitInit(args);
				case "add" -> {
					if (args.length <= 3) {
						throw new IllegalArgumentException("jit add命令缺少运行参数~~");
					}
					jitAdd(args);
				}
				case "commit" -> {
					if (args.length <= 4) {
						throw new IllegalArgumentException("jit commit命令缺少运行参数~~");
					}
					jitCommit(args);
				}
				case  "rm" ->{
					if (args.length<=3){
						throw  new IllegalArgumentException("jit rm 命令缺少运行参数");
					}
					jitRemove(args);
				}
				case "log" ->{
					if (args.length<=2){
						throw  new IllegalArgumentException("jit log 命令缺少运行参数");
					}
					jitLog(args);
				}
				case "reset" ->{
					if (args.length<=4){
						throw new IllegalArgumentException("jit reset 命令缺少运行参数");
					}
					jitReset(args);
				}
				case "branch" ->{
					if (args.length<=2){
						throw  new IllegalArgumentException("jit branch 命令缺少运行参数");
					}
					jitBranch(args);
				}
				case "checkout" ->{
					if (args.length<=3){
						throw  new IllegalArgumentException("jit checkout 命令缺少运行参数");
					}
					jitCheckout(args);
				}

				default -> System.out.println("jit: " + args[1] + "is not a git command. See 'git help'.");
			}
		}


	}




}
