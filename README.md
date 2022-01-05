# SimpleGit
项目文档：[Simple Jit - 飞书文档 (feishu.cn)](https://q3gz2lgnjx.feishu.cn/docs/doccnlber3AQGnLgVuUMlv2rtkc#)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Build your own Git with Java

SimpleGit是基于我们组去年开发的项目为模板的项目框架。

需要大家完成的部分主要集中于gitobject与repository文件夹中。

##项目要求至少实现以下方法
###gitobject/GitObject
这是一个key-value类的父类，其中包含了tree，blob和commit共有的一些方法。

另外需要注意的是，writeObject和compressWrite两个方法都是序列化方法，尽管都要求实现，但是最后使用的时候只需选择其中一个使用即可

如果选择使用compressWrite方法，则需要在反序列化时进行decompress操作

> 
> public static String getValue(File file)
> 
> public void writeObject( )
> 
> public void compressWrite( )
> 
> 
###gitobject/Blob
Blob类用来存储文件的信息。需要实现其中的反序列化方法。
>public static Blob deserialize(String Id)
> 
> public String genKey(File file)

###gitobject/Tree
Tree类用来存储文件夹信息。由于其递归的数据结构，因此在生成其treelist的时候也要使用递归的方法来生成。

另外，在该类中也需要实现一个简单的排序算法。注意比较文件与文件夹优先顺序即可。
>public static Tree deserialize(String Id)
> 
> public List sortFile(File[] fs)
> 
> public String genKey(File dir)

###repository/Repository
这个类负责实现关于创建版本库相关的方法，需要用到大量文件操作。需要注意的一点是，在创建子文件夹之前，必须先创建父文件夹。即不能在路径 "/first" 不存在的情况下直接创建"/first/second"

由于Repository类事实上在整个项目中只能拥有一个实例，因此它满足单例模式的设计模式。有兴趣的同学可以自行学习，并将这个类改造为单例模式。（设计模式面试问的还挺多的）
> public void createRepo()

###core/JitHash
* 在core文件夹中的类，最终命令行直接调用其中的方法。
  这样也更加方便我们对功能进行审查。
  
* 最终检查功能是否实现，主要会检查调用JitHash.hash()方法的结果。

* 单元测试的内容，也应当包括对core中命令接口的测试。
> public static void hash(String filename)



