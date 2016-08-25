package Study;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.orsoncharts.util.json.JSONObject;
import FXServerControllers.ServerMainUicontroller;
import FXServerControllers.ServerStudyUicontroller;
import tools.Batchhandler;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;



public class MachineStudy{
	//File temporarliy;
	BufferedReader br;
	BufferedWriter wr;
	BufferedReader br1;
	BufferedWriter wr1;
	String word;
	ArrayList<String> longword ;	
	Batchhandler bat;

	
	// ��Ÿ
	String filename;
	int change;
	String sockettype;
	boolean check = true;
	ObjectOutputStream oo;
	
	public MachineStudy(String filetype ,String sockettype, String filename ,ObjectOutputStream oo)
	{	
		this.oo = oo;
		this.sockettype = sockettype;
		if(filetype.equals("basic"))
		{		
			System.out.println("�⺻�ؽ�Ʈ ���� �н�����");
			basicCut(filename);
		}
		else
		{
			kakaoCut(filename);
		}
	}
	
	//���� ���� ����
	/*
	public void file_anlaysis()
	{
		String file;
		bat = new Batchhandler();
		
		//1�� : �⺻ �ؽ�Ʈ 2�� : īī����  3�� : 
		System.out.println("���� ������ �����ϼ���. (1.������ ���� �ؽ�Ʈ  2.īī���� �ؽ�Ʈ)");
		Scanner sc = new Scanner(System.in);
		
		String select_num = sc.next();
		
		
		if(select_num.contains("2"))
		{	
			System.out.println("���� �̸��� �Է��ϼ��� ex)love.txt");
			filename = sc.next();
			System.out.println("\t īī���ؽ�Ʈ : "+filename);
			kakaor = new File("./txt/"+filename);
			kakaoCut();
		}
		else if(select_num.contains("1"))
		{
			System.out.println("���� �̸��� �Է��ϼ��� ex)love.txt");
			filename = sc.next();
			System.out.println("\t �⺻�ؽ�Ʈ : " + filename);
			file = "./txt/"+filename;  
			basicCut(file);
		}
	}
	*/
	public void basicCut(String filename)
	{
		
		boolean check = Toolbox.wordDivide(filename , oo , sockettype);
		if(check)
		{
			word_divide_Question();
		}
	}
	
	public void kakaoCut(String filename)
	{
		System.out.println("īī���� ���� ¥����");
		String word;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.FILE_WRITE_KAKAO),"utf-8"));
			
			
			while(true)
			{
				word = br.readLine();
				//System.out.println("�о�� �ܾ�  : "+word);
				
				if(word == null)
				{
					break;
					
				}
				else if(word.contains("["))
				{
					wr.write(word.trim());
					wr.newLine();
					wr.flush();
				}
			}
			
			br.close();
			wr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		boolean check = Toolbox.wordDivide(Statics.FILE_WRITE_KAKAO,"2", oo , sockettype);	
		if(check)
		{
			word_divide_Question();
		}	
	}
	
	
	
	public void word_divide_Question()
	{
		
		String str;
		bat = new Batchhandler();
		try {
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "���� ������....");
				oo.writeObject(json);
			}
			br = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_CONNECT_TEXT), "utf-8"));
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.FILE_CONNECT_QUESTION_TEXT),"utf-8"));
			
			while(true)
			{
				str = br.readLine();
				if(str == null)
				{
					break;
				}
				
				String question = str.split("::")[0];
				
				wr.append(question);
				wr.newLine();
				wr.flush();				
			}
			
			wr.close();
			br.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		try {
			
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
				oo.flush();
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "��ġ���� ������....");
				oo.writeObject(json);
				oo.flush();
			}

			System.out.println("��ġ ���� ����");
			bat.create_batch(Statics.FILE_CONNECT_QUESTION_TEXT);
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
				oo.flush();
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "���¼� �м���....");
				oo.writeObject(json);
				oo.flush();
			}
			System.out.println("���¼� �м� �Ϸ�");
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "��ġ ���� �м���....");
				oo.writeObject(json);
			}
			bat.start_batch();
			morpheme_sort(Statics.FILE_CONNECT_QUESTION_TEXT);
			study();
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "��    ��");
				oo.writeObject(json);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public void morpheme_sort(String filename)
	{
		try {
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
				oo.flush();
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "���¼� ���� ��....");
				oo.writeObject(json);
				oo.flush();
			}
				
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename+".ma"),"utf-8"));
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.FILE_WRITE_NAME),"utf-8"));
			
			
			
			longword = new ArrayList<String>();
			while(true){
				word = br.readLine();
				if(word == null)
				{
					break;
				}
				else if(word.contains("INPUT"))
				{
					String str;
					int cnt=0;
					wr.append("���� : ");
					for(int i=0;i<word.length();i++)
					{
						if(word.charAt(i)==':')
						{
							 str=word.substring(i+1, word.length());
							 wr.append(str);
						}
					}
					wr.newLine();
					wr.flush();	
				}
				else if(word.contains("TOKEN"))
				{
					
				}
				else if(word.contains("NUM_OUTPUT"))
				{
					int index = Integer.parseInt(word.split(" ")[1]);
					String name[] = new String[index];
					String num[] = new String[index];
					if(index == 1)
					{
						//System.out.println("OUPUT�� 1�϶� ���� ���� : "+word);
						word = br.readLine();
						//System.out.println(word);
						if(word.contains("[/SW"))
						{
							br = tools.Toolbox.kakaoDelete(br);
						}
						else if(word.contains("-----"))
						{
							br = tools.Toolbox.kakaoDelete(br);
						}
						else
						{

							longword.add(word);
						}
					}
					else{
						int change = 0;
						for(int i=0; index > i; i++)
						{
							word = br.readLine();
							num[i]= word.split("\t")[1];
							name[i] = word.split("\t")[0];
							
							int change1 = Integer.parseInt(num[i]);
							if(change < change1)
							{
								change = change1;
							}	
						}
						for(int j =0; index > j ; j++)
						{
							if(num[j]==null)
							{
								break;
							}
							if(Integer.parseInt(num[j]) == change)
							{
								longword.add(name[j]+"\t"+num[j]);
								break;
							}
						}
					}
				}
				else if(word.contains("====="))
				{
					for(int i =0; i<longword.size() ; i++)
					{
						wr.append(longword.get(i).trim());
						wr.newLine();
						wr.flush();
						int size = longword.size();
						if(i+1 == size)
						{
							wr.append("===============");
							wr.newLine();
							wr.flush();
						}
					}
					longword = new ArrayList<String>();
				}
			}
			br.close();
			wr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void study()
	{
		String str_one;
		String str_two;
		int cnt = 0;
		try {
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_WRITE_NAME), "utf-8"));
			br1 = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_CONNECT_TEXT), "utf-8"));
			wr1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.FILE_QUESTION_ANSWER_TEXT,true), ("utf-8")));
			if(sockettype.equals("client"))
			{
				oo.writeObject(Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND));
				oo.flush();
			}
			else
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_STATE_RESPOND);
				json.put("state", "���� �� ���¼� �н���....");
				oo.writeObject(json);
				oo.flush();
			}

			while(true)
			{
				str_two = br1.readLine();
				if(str_two == null)
				{
					break;
				}
				
				str_two = str_two.split("::")[1];
					
				while(true)
				{
					cnt ++;
					str_one = br.readLine();
					System.out.println("���� : "+str_one);
					if(cnt == 1)
					{
						wr1.append(str_one);
						wr1.newLine();
						wr1.flush();
						wr1.append("�亯 :  "+str_two);
						wr1.newLine();
						wr1.flush();
					}
					else if(str_one.contains("======"))
					{
						wr1.append("================");
						wr1.newLine();
						wr1.flush();
						break;
					}
					else
					{
						wr1.append(str_one);
						wr1.newLine();
						wr1.flush();
					}
				}
				cnt = 0;
				System.out.println("\t�亯  :"+str_two);
			}
		br1.close();
		br.close();
		wr1.close();
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

}
