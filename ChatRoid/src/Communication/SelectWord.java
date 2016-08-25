package Communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import com.orsoncharts.util.json.JSONObject;

import Study.SwearDiscriminatar;
import tools.Batchhandler;
import tools.Statics;
import tools.Toolbox;
import tools.Worddivide;

public class SelectWord {
	
	//IO
	BufferedReader Machinestream;
	BufferedWriter bw; // ����� �ִ� ������ ������������ ��Ʈ��
	BufferedReader QuesStream;
	
	JSONObject comparejson = new JSONObject();
	
	//����
	ArrayList<String> Machinelist; // ���� �Ǿ��ִ� ������ �����Ͽ� ���ϱ� ���� arraylist;
	ArrayList<String> Queslist; // ���� �ϳ��� ���¼ұ����� �����ϴ� arraylist;
	ArrayList<String> Anslist; // �亯�� ������ ���� ��̸���
	ArrayList<String> Tmplist; // �ӽø���Ʈ ���� ����;
	String word = "start";
	String question;
	char one_word;
	int check;
	int nocheck;
	String a;
	String b;
	WeightCalculation wc = new WeightCalculation();
	Batchhandler batch = new Batchhandler(); // ��ġ�ڵ鷯 ��ü
	int cnt = 0 ;
	String sendmessage = "";
	
	
	public SelectWord()
	{
		
	}
	
	public void test(String msg)
	{  
		try{
			Machinestream = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_QUESTION_ANSWER_TEXT),"utf-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Statics.ONE_QUESTION_TXT), "utf-8"));
			QuesStream = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_WRITE_NAME),"utf-8"));
			question = msg;
			one_Word(question); // �Է� ���ڹ޾Ƽ� ���¼ҷ� ���� // testing()�޼ҵ� ����
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public void testing()
	{		
		sendmessage = null;
		boolean check = false;
		System.out.println("�׽�Ʈ ��ŸƮ");
		Machinelist = new ArrayList<String>();
		Queslist = new ArrayList<String>();
		Anslist = new ArrayList<String>();
		String abc = null;
		int score = 0;
		int strtmp = 0;
		
		try {			
			while(true)
			{
				String word = QuesStream.readLine();
				if(word.contains("===="))
				{
					break;
				}
				else
				{
					System.out.println("���� ���¼� : "+word);
					Queslist.add(word);
				}
			}
			while(true)
			{	
				if(check)
				{
					break;
				}
				else
				{
					Machinelist.clear();
				}
				if(word == null)
				{
					break;
				}
				else
				{
					while(true)
					{	
						word = Machinestream.readLine();
						if(word == null)
						{
							break;
						}
						if(word.contains("====="))
						{
							System.out.println("�н���Ų �ؽ�Ʈ : "+word);
							break;
						}
						else
						{
							Machinelist.add(word);
						}	
					}
					comparejson = Toolbox.rankingCompare(Queslist, Machinelist);

					Anslist = (ArrayList<String>)comparejson.get("arr_list");
					System.out.println(Anslist.toString());
					score = (int)comparejson.get("score");
					check = (boolean) comparejson.get("check");	
					if(Anslist!=null)
					{
						// ������ �� ��� �¾��� ���
						if(check)
						{
							for(String str : Anslist)
							{
								if(str.contains("�亯 : "))
								{
									abc = str.split("�亯 : ")[1];
								}
							}
						}
						//���� ���
						else
						{
							//System.out.println("�� ���� : "+ strtmp);
							//System.out.println("���� ���� :" + score);
							if(strtmp < score)
							{
								strtmp = score;
								Tmplist = new ArrayList<>();
								
								for(String str : Anslist)
								{
									Tmplist.add(str);
								}
							}
						}
					}
				}
			}
			
			//System.out.println(Tmplist.toString());
			System.out.println("���� ���ھ� : " +strtmp);
			
			if(abc != null)
			{
				sendmessage = abc;
			}
			else
			{
				if(strtmp < 40)
				{
					
					//sendmessage  = "���� ������ �߸𸣰ھ�� ^^";
					wc.weightsetting(Toolbox.getFileList(Statics.FILE_QUESTION_ANSWER_TEXT,false), Toolbox.getFileList(Statics.FILE_WRITE_NAME,false));
					JSONObject message = wc.getWeight();
					System.out.println("���� ����ġ"+message.get("����ġ"));
					if((double)message.get("����ġ") == 0.0)
					{
						sendmessage  = "���� ������ �߸𸣰ھ�� ^^";
					}
					else
					{
						sendmessage = message.get("�亯").toString();
					}
					
				}
				else
				{
					System.out.println("------���õ� ���� ---------");
					for(String str : Tmplist)
					{
						System.out.println(str);
						if(str.contains("�亯 : "))
						{
							sendmessage  = str.split("�亯 :  ")[1];
						}
					}
				}
			}
			
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
	
	
	public void one_Word(String word)
	{
		try {
			bw.write(word);
			bw.flush();
			
			
			batch.create_batch(Statics.ONE_QUESTION_TXT);
			batch.start_batch();
			Worddivide.morphemeDivide(Statics.ONE_QUESTION_TXT ,Statics.FILE_WRITE_NAME ,Statics.SENTENCEINCLUE);
			
			//�弳�� �Ǻ��ϱ� ���� ����Ʈ
			ArrayList<String> list= Toolbox.getFileList(Statics.FILE_WRITE_NAME,true);
			//�弳�Ǻ�
			if(SwearDiscriminatar.skipSwear(list))
			{
				testing();
			}
			else
			{
				sendmessage = "�������� ���� ������ּ���^^";
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public String getMessage(String msg)
	{
		test(msg);
		
		if(sendmessage != null)
		{
			msg = sendmessage;
			sendmessage = null;
		}
		else
		{
			msg = "����";
		}
		
		try {
			QuesStream.close();
			Machinestream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
		
	}
	
	/*
	public void one_Word(String word)
	{
		try{
		  MorphemeAnalyzer ma = new MorphemeAnalyzer();

	         List<MExpression> ml = ma.analyze("���Ծ� �� �Ծ���?");
	         ml = ma.leaveJustBest(ml);
	         List<Sentence> s = ma.divideToSentences(ml);
	         System.out.println("==================================");
	         for(Sentence target : s)
	         {
	            for(Eojeol e : target)
	            {
	               for(Morpheme m : e)
	               {
	                  System.out.println(m.toString());
	               }
	            }
	         }
	         
	      
			
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SelectWord test = new SelectWord();
	}
}
