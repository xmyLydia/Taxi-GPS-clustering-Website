package DataMining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestFileWriter {
    public void write() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("G://data/data.txt",true);
            String c = "abs"+"\r\n";
            fw.write(c);
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("写入失败");
            System.exit(-1);
        }
    }
    public void filename() {
    	try{
    	File path=new File("G:\\data"); //先设置文件路径
    	File dir=new File(path,"data.txt"); //设置在文件路径下创建新文件的名称
    	if(!dir.exists()) //判断文件是否已经存在
    	dir.createNewFile(); //如果不存在的话就创建一个文件
    	}
    	catch(Exception e){ //如果存在就会报错，
    	System.out.print("创建失败");//输出创建失败信息，也就证明当前要创建的文件已经存在。
    	}
    	}
}