package Study;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.snu.ids.ha.ma.Tokenizer;

import FXServerControllers.ServerMainUicontroller;
import javafx.application.Platform;
import tools.CustomDialog;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;

public class SwearDiscriminatar {
	//���̺� �������� ���� Ȱ��
	BufferedWriter nomalbw; // ���� �н� �ؽ�Ʈ 
	BufferedWriter swearbw; // �弳 �н� �ؽ�Ʈ
	BufferedReader learningbr; // �н���ų �ؽ�Ʈ
	
	

	private static int undulicate = 0; //�ߺ��� ���� ���� ����
	
	
	private int nomality = 0; 
	private int unnomality =0;
	
	StringTokenizer st;
	
    static ArrayList<String> alllist = new ArrayList<>(); // ��ü ���� ����
	static ArrayList<String> nomallist = new ArrayList<>(); // ���� ���� ����
	static ArrayList<String> unnoamllist = new ArrayList<>(); //������ ���� ����
	static ArrayList<String> undulicatelist = new ArrayList<>(); //��ü ���忡�� �ߺ� ���� �� ����Ʈ
	
	public SwearDiscriminatar()
	{
		/*
		new File(Statics.BAD_NOMAL_STUDY_TXT);
		new File(Statics.BAD_STUDY_WORDS_TXT);
		String word;
		try {
			nomalbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.BAD_NOMAL_STUDY_TXT),"utf-8"));
			swearbw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.BAD_SWEAR_STUDY_TXT),"utf-8"));
			learningbr = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.BAD_STUDY_WORDS_TXT)));
			while(true)
			{
				word = learningbr.readLine();
				//System.out.println( word);
				if(word == null)
				{
					break;
				}
				word = word.replaceAll(Pattern.quote("+"), " ");
				if(word.startsWith("-1"))
				{
					int pass = word.split("\t")[0].length();
					word = word.substring(pass, word.length());
					word = word.replaceAll(Pattern.quote("\t")," ");
					st = new StringTokenizer(word," ");
					
					while(st.hasMoreTokens())
					{
						word = st.nextToken();
						if(word.contains("/"))
						{
							alllist.add(word);
							nomallist.add(word);
						}
					}
				}
				
				if(word.startsWith("1"))
				{
					int pass = word.split("\t")[0].length();
					word = word.substring(pass, word.length());
					word = word.replaceAll(Pattern.quote("\t")," ");
					st = new StringTokenizer(word," ");
					
					while(st.hasMoreTokens())
					{
						word = st.nextToken();
						if(word.contains("/"))
						{
							unnoamllist.add(word);
							alllist.add(word);
						}
					}					
				}	
			}
			
			for(String str : unnoamllist)
			{
				swearbw.write(str);
				swearbw.newLine();
				swearbw.flush();
			}	
			for(String str : nomallist)
			{
				nomalbw.write(str);
				nomalbw.newLine();
				nomalbw.flush();
			}
				
			undulicatelist = overlap(alllist); //��ü ������ �ߺ� ���� ���� �޼ҵ�
			
			nomalbw.close();
			swearbw.close();
			learningbr.close();
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		//badworddiscriminatar(alllist);
		*/
		 
	}
	
	
	//���̺� �������� �̿� Ȯ�� ����.
	public static double badwordDiscriminatar(ArrayList<String> list)
	{
		int cnt = 1; // ���� ����;  1�� ���� ���� : Smoothing�̿� 0 ����
		double tmp;
		
		double nomalvalue = 0; // �� ���� ����
		double unnomalvalue = 0; // �� ������ ����
		
		double nomalweight; // ��ü ����ܾ� ����
		double swearweight; // ��ü ������ܾ� ����
		
		int txtall = alllist.size();
		int nomalcnt = nomallist.size(); //�븻 ���� ����
		int swearcnt = unnoamllist.size(); // ������ ���� ����
		int unduplicate = undulicatelist.size(); // �ߺ����� ���� ����
		double probability = 0;
		
		nomalweight = ((double)nomalcnt / txtall);
		swearweight = ((double)swearcnt / txtall);
		
		System.out.println("��ü �ܾ�  ���� : " + txtall);
		System.out.println("���� �ܾ� ����  : " + nomalcnt);
		System.out.println("������ �ܾ�  ���� : " + swearcnt);
		System.out.println("�ߺ� ���� ��ü �ܾ� ���� : " + unduplicate);
		System.out.println("����ܾ� ���� : " + nomalweight);
		System.out.println("������ܾ� ���� : " + swearweight);
		
		System.out.println("=========== ���� ================");
		//������ Ȯ��
		for(String str : list)
		{
			System.out.println(str);
			if(str == null)
			{
				break;
			}
			for(String liststr : nomallist)
			{
				if(str.equals(liststr))
				{
					cnt++;
				}
			}
			System.out.println("�ѹ��� ���� : "+cnt);
			// �ܾ�� ���� ����
			tmp = ((double)cnt /(double)(nomalcnt+unduplicate));
			tmp = Math.log(tmp);
			cnt = 1;
			nomalvalue = nomalvalue+tmp;
			
			
			System.out.println("�ѹ����� ���� : "+ tmp);
			System.out.println("�� ���� ���� : "+ nomalvalue );
			
		}
		nomalweight = Math.log(nomalweight);
		
		nomalvalue = nomalvalue + nomalweight;
		System.out.println("������ Ȯ�� : " + nomalvalue);
		
		
		System.out.println("=========== ������ ================");
		//�������� Ȯ��
		for(String str : list)
		{
			System.out.println(str);
			if(str == null)
			{
				break;
			}
			for(String liststr : unnoamllist)
			{
				if(str.equals(liststr))
				{
					cnt++;
				}
			}
			System.out.println("�ѹ��� ���� : "+cnt);
			// �ܾ�� ���� ����
			tmp = ((double)cnt /(double)(swearcnt+unduplicate));
			//System.out.println(tmp);
			tmp = Math.log(tmp);
			cnt = 1;
			
			unnomalvalue = unnomalvalue+tmp;
			
			System.out.println("�ѹ����� ���� : "+ tmp);
			System.out.println("�� ���� ���� : "+ unnomalvalue );
		}
		swearweight = Math.log(swearweight);
		
		unnomalvalue = unnomalvalue + swearweight;
		System.out.println("�������� Ȯ�� : " + unnomalvalue);
		
		
		return probability;
	}
	

	
	public static boolean skipSwear(ArrayList<String> list)
	{
		boolean check = true;
		ArrayList<String> swearlist = ServerMainUicontroller.swearlist;
			for(String a: list)
			{
				for(String b : swearlist)
				{
					if(a.equals(b))
					{
						check = false;
					}
				}
				if(check == false)
				{
					break;
				}
			}
		
		return check;
	}
	
	public static boolean skipSwear(ArrayList<String> list, ObjectOutputStream oo, String type)
	{
		boolean check = true;
		ArrayList<String> swearlist = ServerMainUicontroller.swearlist;
		try {
			for(String a: list)
			{
				for(String b : swearlist)
				{
					if(a.equals(b))
					{
						check = false;
					}
				}
				if(check == false)
				{
					break;
				}
			}
			if(type.equals(Statics.WORD))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check;
	}
	
	
	
	//�ߺ� �� ���� ���� ��
	public static ArrayList<String> overlap(ArrayList<String> list)
	{
		ArrayList<String> duplicate = new ArrayList<>();
		boolean check = false;
		for(int i = 0 ; i < list.size() ; i++)
		{
			for(int j = i+1 ; j < list.size() ; j++)
			{
				if(list.get(i).equals(list.get(j)))
				{
					check = true;
					break;
				}
				else
				{
					check = false;
					continue;
				}
			}
			if(!check)
			{
				duplicate.add(list.get(i));
				check = true;
			}
		}
		return duplicate;
	}
	
	public static void main(String[] args) {
		//new SwearDiscriminatar();
	}
}
