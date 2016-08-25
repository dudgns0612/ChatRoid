package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Batchhandler {
	File createbat = new File(Statics.USER_FILE+"\\"+"BAT.bat");
	BufferedWriter bw;
	
	//bat ���Ϸ� ���� �� ma������ �������ٿ� ==========�߰�
	BufferedWriter bwbat;
	File mafilename;
	
	String filename;
	
	public void create_batch(String filename)
	{
		try {
			//String file = null ;
			this.filename = filename;
			//System.out.println(file);
			
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(createbat),"utf-8"));
			bw.write("cd C:\\Users\\LG\\Desktop\\cnuma\\cnuma\\py_ver");
			bw.newLine();
			bw.write("ma.bat "+filename);
			bw.newLine();
			bw.write("pause");
			bw.flush();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
	}
	
	public void start_batch()
	{
		System.out.println("\t\t ���¼� �м� ����");
		String filename = createbat.getPath();
		System.out.println("\t �ļ��� ��ġ���� �̸� : "+filename);
		
		try{
		    Process p = Runtime.getRuntime().exec(filename);
		    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    
		    String line = null;
		    while((line = br.readLine()) != null){

		        System.out.println(line);
		    }
		    
		    System.out.println("\t\t\t ���¼� �м� �Ϸ�");
		    modify();
		}catch(Exception e){
		    System.out.println(e);
		}
		
	}
	public void modify()
	{
		//�������� �ҷ��ͼ� �������ٿ�  ==============�߰��ϱ�
		System.out.println("modify");
		String file = filename.split(Pattern.quote("\\"))[2];
		String word;
		System.out.println(file);
		mafilename = new File(filename+".ma");
		try {
			bwbat = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mafilename,true),"utf-8"));		
		
			bwbat.append("============");
			bwbat.newLine();
			bwbat.flush();
			bwbat.close();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void morphemeanalysis(String filename)
	{
		create_batch(filename);
		start_batch();
	}

}
