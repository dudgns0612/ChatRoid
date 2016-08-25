package Communication;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.orsoncharts.util.json.JSONArray;
import com.orsoncharts.util.json.JSONObject;


public class WeightCalculation {
	ArrayList<String> questionlist = new ArrayList<>(); //���� ���� list             
	ArrayList<String> studylist = new ArrayList<>(); //��ü �н� ���� list
	
	ArrayList<String> sentencelist = new ArrayList<>(); // ���� list
	ArrayList<String> ketwordlist = new ArrayList<>(); //Ű���� list
	ArrayList<String> wordlist = new ArrayList<>();  //�� �ܾ� list
	
	
	JSONObject sentenceinfo = new JSONObject(); // ����  /�ܾ��/����ġ ���� �ϴ� json;
	JSONObject weightinfo = new JSONObject();
	JSONArray  weightarray = new JSONArray(); //��� ����ġjson ;
	
	
	int allsentence = 0;
	double compare = 0;
	public void weightsetting(ArrayList<String> study, ArrayList<String> question)
	{
		System.out.println("tf-idf ����");
		String divide[];
		
		System.out.println(study.toString());
		try
		{
			for(String str : study)
			{
				if(str == null)
				{
					break;
				}
				else
				{
					studylist.add(str); 
					if(str.contains("����"))
					{
						allsentence++; //���� ��ü��
					}
				}
			}
			
			for(String str : question)
			{
				if(str.contains("==========="))
				{
					break;
				}
				else
				{
					if(str.contains("���� :  "))
					{
						
					}
					else
					{
						if(str.contains("+"))
						{
							int cnt = 0;
							for(int i =0; i < str.length(); i++)
							{
								if(str.charAt(i) == '+')
								{
									cnt++;
								}
							}
							for(int k = 0; k < cnt+1 ; k++)
							{
								divide = str.split(Pattern.quote("+"));
								questionlist.add(divide[k]);
							}	
						}
						else
						{
							questionlist.add(str);
						}
						
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		setsentenceinfo(studylist);
	}
	
	
	public void setsentenceinfo(ArrayList<String> studylist)
	{
		System.out.println("sentendceinfo ����");
		try
		{
			for(String str : studylist)
			{
				//System.out.println(str);
				if(str == null)
				{
					break;
				}
				else
				{
					if(str.contains("���� :  "))
					{
						sentenceinfo.put("����", str);
					}
					else if(str.contains("�亯 :  "))
					{
						str= str.split("�亯 :  ")[1];
						sentenceinfo.put("�亯", str);
					}
					else if(str.contains("======="))
					{
						wordlist.add("===========");
						sentenceinfo.put("�ܾ��Ʈ", wordlist);
						wordWeight(sentenceinfo);
						wordlist.clear();
						sentenceinfo.clear();
						
					}
					else
					{
						String divide[];
						if(str.contains("+") && str.contains("/"))
						{
							int cnt = 0;
							for(int i =0; i < str.length(); i++)
							{
								if(str.charAt(i) == '+')
								{
									cnt++;
								}
							}
							for(int j = 0; j < cnt+1 ; j++)
							{
								divide = str.split(Pattern.quote("+"));
								wordlist.add(divide[j]);
							}								
						}
						else
						{
							wordlist.add(str);
						}
					}
					
				}
			}	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println(questionlist.toString());
	}
	
	//�Ѵܾ ���Ե� ������ ���� ���ϴ� �޼ҵ�
	
	
	public void wordWeight(JSONObject sentenceinfo)
	{
		System.out.println("wordWeight ����");
		double tf = 0;
		double idf = 0;
		double tf_idf = 0;
		double weight = 0;
		for(String str : questionlist)
		{
			tf = getTf(sentenceinfo, str);
			idf = getIdf(str);
			//System.out.println("tf : "+ tf);
			//System.out.println("idf : "+idf);
			tf_idf = tf * idf;
			if(tf_idf == 0)
			{
				continue;
			}
			else
			{
				weight = weight + tf_idf;
			}
		}
		//System.out.println(sentenceinfo.get("����"));
		//System.out.println("�� ������ ����ġ : "+weight);
		
		//�� 
		if(weightinfo.get("����ġ") != null)
		{
			compare= (double)weightinfo.get("����ġ");
			if(compare > weight)
			{
				weightinfo.put("����", sentenceinfo.get("����"));
				weightinfo.put("�亯", sentenceinfo.get("�亯"));
				weightinfo.put("����ġ", weight);
			}
		}
		else
		{
			weightinfo.put("����", sentenceinfo.get("����"));
			weightinfo.put("�亯", sentenceinfo.get("�亯"));
			weightinfo.put("����ġ", weight);
		}
		
		//System.out.println("���� ���� : " + weightinfo.get("����"));
		//System.out.println("���� �亯 : " + weightinfo.get("�亯"));
		//System.out.println("����ġ : " + weightinfo.get("����ġ"));
	}
	
	public double getIdf(String word)
	{
		System.out.println("getIdf ����");
		double idf = 0;
		int cnt = 0;
		//System.out.println("�з��� wordlist : "+ wordlist.toString());
		
		for(int i = 0 ; i < wordlist.size(); i++)
		{
			//System.out.println("�н��� : "+wordlist.get(i));
			//System.out.println("Word : "+word);
			if(wordlist.get(i) == null)
			{
				break;
			}
			else if(wordlist.get(i).contains("���� :  "))
			{
				
			}
			else
			{
				if(wordlist.get(i).equals(word))
				{
					cnt++;
					while(true)
					{
						if(wordlist.get(i).contains("======"))
						{
							break;
						}
						else
						{
							i++;
						}
					}
				}
			}
		}
		
		//System.out.println("�ܾ ������ ���尹�� : "+cnt);
		if(cnt == 0)
		{
			idf = 0;
		}
		else
		{
			idf = Math.log((double)cnt / allsentence);
		}
		return idf;
	}
	
	//�� �ܾ ���� tf;
	public double getTf(JSONObject info , String word)
	{
		System.out.println("getTf");
		double weight=0;
		ArrayList<String> wordlist = (ArrayList<String>)info.get("�ܾ��Ʈ");
		int wordsize = wordlist.size();//���峻�� ��� �ܾ� ����
		int includeword = 0; // ���峻 Ű���� ����
		
		
		// �Ѵܾ�� �׹��忡 tf ����ġ�� ���ؼ� �Ѱ���
		// wordweight���� �� ������ ����ġ�� ����  ���� ������ ����ġ�� json�� ���� ������.
		for(String str : wordlist)
		{
			if(str.equals(word))
			{
				includeword++;
			}
		}
		
		weight = ((double)includeword / wordsize);
		
		
		return weight;
	}
	public JSONObject getWeight()
	{
		return weightinfo; 
	}
	public static void main(String[] args) {
		/*
		BufferedReader br;
		BufferedReader br1;
		try {
			br1 = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_WRITE_NAME), "utf-8"));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(Statics.FILE_QUESTION_ANSWER_TEXT), "utf-8"));
			new WeightCalculation(br,br1);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	
	}
}
