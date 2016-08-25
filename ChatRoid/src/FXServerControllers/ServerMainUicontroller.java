package FXServerControllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.orsoncharts.util.json.JSONObject;

import Communication.SelectWord;
import Files.FileServer;
import Study.SwearDiscriminatar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tools.Batchhandler;
import tools.NetworkProtocols;
import tools.Scenecontroll;
import tools.Statics;
import tools.Toolbox;
import tools.Worddivide;

public class ServerMainUicontroller implements Initializable{
	
	//���� ���� �ʵ� 
	Socket socket;
	public ServerSocket ssc;
	FileServer FileServer;

	//���ϼ��� ��Ʈ��
	ObjectOutputStream oo; 
	ObjectInputStream oi;
	
	//UI���� �ʵ�
	Stage stage;
	@FXML TextArea server_area;
	@FXML Button offBtn;
	@FXML Button onBtn;
	@FXML Button fileserverOn;
	@FXML Button fileserverOff;
	@FXML Button studyBtn;
	
	
	//�弳 �Ǻ� �н� ����
	//SwearDiscriminatar sd = new SwearDiscriminatar();
	
	//��ġ�ڵ鷯
	Batchhandler batchhandle = new Batchhandler();
	
	ArrayList<Socket> clientlist = new ArrayList<>(); //Ŭ���̾�Ʈ ������ ��� �κ�
	String messge; // Ŭ��� ���� �޾ƿ� �޼��� 
	String remessge;  // �м��Ͽ� �޾ƿ� �޼���
	SelectWord Tunnel;
	
	//�Ϻ��� �弳 list
	public static ArrayList<String> swearlist = new ArrayList<>();
	
	public void INIT_CONTROLLER(Stage  stage)
	{
		server_area.setEditable(true);
		this.stage = stage;
		offBtn.setDisable(true);
		onBtn.setDisable(true);
		fileserverOn.setDisable(true);
		fileserverOff.setDisable(true);
		studyBtn.setDisable(true);
	}
	
	public void start()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				int cnt = 3;
				try {
					ssc = new ServerSocket(NetworkProtocols.SERVER_PORT_INFO);
					server_area.appendText("STATE : Clinet in waitting\n");
					while(true)
					{
						socket = ssc.accept();
						server_area.appendText("STATE : Client in Success \n");
						server_area.appendText("STATE : Client Info : "+socket.getLocalSocketAddress().toString()+"\n");
						if(socket != null)
						{
							new Listener().start();
							System.out.println("socket : "+ socket);
							clientlist.add(socket);
						}

					}
				}  catch (SocketException e){
					String err = e.getMessage();
					if(err.equals(Statics.SERVER_ALREADY_JVM_ERR))
					{
						server_area.appendText("ERR : Already a Server Is On \n");
						server_area.appendText("STATE : 3�ʵڿ� ���� �մϴ�. \n");
						onBtn.setDisable(true);
						offBtn.setDisable(true);
						fileserverOn.setDisable(true);
						fileserverOff.setDisable(true);
						studyBtn.setDisable(true);
						while(true)
						{
							synchronized (this) {
								try {
									wait(1500);
									server_area.appendText("STATE : "+cnt+"\n");
									cnt--;
									if(cnt == -1)
									{
										System.exit(0);
									}
									
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
					else if(err.equals(Statics.SERVER_CLOSED_ERR))
					{
						server_area.appendText("STATE : ServerSocket Close \n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	// Ŭ���̾�Ʈ�� Ŀ�´����̼� ������ Ŭ���� 
	class Listener extends Thread
	{	
		//��Ʈ�� ���� �ʵ�
		ObjectOutputStream ow;
		ObjectInputStream or;
		JSONObject request;
		
		public void run()
		{
			try {
				server_area.appendText("STARE : Communication Start(Listener Thread) \n");
				ow = new ObjectOutputStream(socket.getOutputStream());
				or = new ObjectInputStream(socket.getInputStream());	
				System.out.println("��Ʈ�� ����");
				while(true)
				{
					request = (JSONObject)or.readObject();
					
					String type = request.get("type").toString();
					
					server_area.appendText("CLIENTS IN PROTOCOL : "+type+"\n");
					if(type.equals(NetworkProtocols.MESSAGE_RECIEVER_PROTOCOL_REQUEST))
					{
						Tunnel = new SelectWord();
						messge = request.get("msg").toString();
						remessge = Tunnel.getMessage(messge);
						JSONObject json = Toolbox.JsonRequest(NetworkProtocols.MESSAGE_RECIEVER_PROTOCOL_RESPOND);
						json.put("msg", remessge);
						SendProtocols(json);
					}
					else if(type.equals(NetworkProtocols.FILE_SERVER_OPEN_REQUEST))
					{
						JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SERVER_OPEN_RESPOND);
						SendProtocols(json);
						server_area.appendText("STATE : File Server Accept Request \n");
					}
					else if(type.equals(NetworkProtocols.LISTENER_CLOSE_REQUEST))
					{
						SendProtocols(Toolbox.JsonRequest(NetworkProtocols.LISTENER_CLOSE_RESPOND));
					}
					else if(type.equals(NetworkProtocols.SERVER_CONNECT_CHECK_REQUEST))
					{
						SendProtocols(Toolbox.JsonRequest(NetworkProtocols.SERVER_CONNECT_CHECK_RESPOND));
					}
				
				}
			} catch (Exception e) {
				server_area.appendText("STATE : Client Out \n");
			}
		}
		
		
		public void SendProtocols(JSONObject json)
		{
			try {
				System.out.println("Ŭ��� ������ ���̽�"+json.toString());
				ow.writeObject(json);
				ow.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	
	@FXML private void onStart()
	{	
		Platform.runLater(new Runnable() {		
			@Override
			public void run() {
				onBtn.setStyle("-fx-background-color : green");
				offBtn.setStyle("-fx-background-color : red");
				
				start();
				onBtn.setDisable(true);
				offBtn.setDisable(false);
				fileserverOn.setDisable(false);
				fileserverOff.setDisable(true);
			}
		});
		
	}
	
	@FXML private void onStop()
	{	
		Platform.runLater(new Runnable() {						
			@Override
			public void run() {
				try {
					onBtn.setStyle("-fx-background-color : red");
					offBtn.setStyle("-fx-background-color : green");
					offBtn.setDisable(true);
					onBtn.setDisable(false);
					fileserverOn.setDisable(true);
					fileserverOff.setDisable(true);
					studyBtn.setDisable(true);
					ssc.close();
					clientreset();
					FileServer.ssc.close();
					server_area.appendText("FILESERVER STATE: FileServer Off \n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}
		});
	}
	
	@FXML private void onExit()
	{
		System.exit(0);
	}
	//���ϼ��� on
	@FXML private void onFIleSeverOn()
	{
		Platform.runLater(new Runnable() {		
			@Override
			public void run() {
				FileServer = new FileServer();
				server_area.appendText("FILESERVER STATE: FileServer On \n");
				fileserverOn.setDisable(true);
				fileserverOff.setDisable(false);
				studyBtn.setDisable(false);
			}
		});
		
	}
	
	@FXML private void onStudy()
	{
		Platform.runLater(new Runnable() {	
			@Override
			public void run() {
				Scenecontroll.WindiwModalPane(Statics.SERVER_STUDY_FXML, "������ �н�â");
			}
		});
		
	}
	
	//���ϼ��� off
	@FXML private void onFIleSeverOff()
	{
		Platform.runLater(new Runnable() {						
			@Override
			public void run() {
				try {
				  FileServer.ssc.close();
				  server_area.appendText("FILESERVER STATE: FileServer Off \n");
				  fileserverOn.setDisable(false);
				  fileserverOff.setDisable(true);
				  studyBtn.setDisable(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void filesendState(String str)
	{
		server_area.appendText(str+"\n");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		File trueswearTxt = new File(Statics.CERTAIN_SWEAR_TXT); //swear ���¼� �м� �� ����
		File swearFile = new File(Statics.SWEAR_STUDY_FILE);
		File swearmorphmeTxt = new File(Statics.SWEAR_MORPHEME_TXT); // swear ���¼Һм� �� ����
		File basicFile = new File(Statics.BASIC_FILE);
		if(basicFile.mkdir())
		{
			server_area.appendText("STATE : ���� ���� ���: "+ Statics.BASIC_FILE+"\n");
		}
		
		if(swearFile.mkdirs())
		{
			server_area.appendText("���� ���� ��� : "+ Statics.SWEAR_STUDY_FILE+"\n");
		}
		
		if(!trueswearTxt.exists())
		{
			server_area.appendText("WARRING : "+Statics.CERTAIN_SWEAR_TXT +" not file \n");
			server_area.appendText("WARRING : Non-self executing the slang discriminator \n");
		}
		
		if(swearmorphmeTxt.exists())
		{
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						server_area.appendText("STATE : Server setting.....wait plase \n");
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(swearmorphmeTxt),"utf-8"));
						
						while(true)
						{
							String word = br.readLine();
							
							//System.out.println(word);
							if(word == null)
							{
								break;
							}
							swearlist.add(word);
						}
						swearlist = SwearDiscriminatar.overlap(swearlist); // �ߺ� �ܾ� ����
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			Platform.runLater(new Runnable() {	
				@Override
				public void run() {
					onBtn.setDisable(false);
					offBtn.setDisable(true);
					fileserverOn.setDisable(true);
					fileserverOff.setDisable(true);
					studyBtn.setDisable(true);
					server_area.appendText("STATE : Server setting success \n");
				}
			});
			
		}
		else
		{
		    new Thread(new Runnable() {
				@Override
				public void run() {
					server_area.appendText("STATE : Server setting.....wait plase \n");
					System.out.println(trueswearTxt.getPath());
					batchhandle.morphemeanalysis(trueswearTxt.getPath());
					swearlist = Worddivide.morphemeDivide(trueswearTxt.getPath(), Statics.SWEAR_MORPHEME_TXT, Statics.SWEARDIVIDE);
					swearlist = SwearDiscriminatar.overlap(swearlist);
					
					onBtn.setDisable(false);
					offBtn.setDisable(true);
					fileserverOn.setDisable(true);
					fileserverOff.setDisable(true);
					studyBtn.setDisable(true);
					server_area.appendText("STATE : Server setting success \n");
				}
	
			}).start();
		}
	    
	    
	    /* ���̺� �������� �޴���
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int cnt = 0;
				if(swearTxt.exists())
				{
					server_area.appendText("STATE : See text slang discriminator \n");
				}
				else
				{
					onBtn.setDisable(true);
					server_area.setEditable(true);if(swearFile.mkdirs())
		{
			server_area.appendText("���� ���� ��� : "+ Statics.SWEAR_STUDY_FILE+"\n");
		}
					offBtn.setDisable(true);
					fileserverOn.setDisable(true);
					fileserverOff.setDisable(true);
					studyBtn.setDisable(true);
					while(true)
					{
						if(swearTxt.exists())
						{
							server_area.appendText("STATE : See text slang discriminator \n");
							onBtn.setDisable(false);
							fileserverOn.setDisable(false);
							break;
						}
						if(cnt == 11)
						{
							onBtn.setDisable(false);
							fileserverOn.setDisable(false);
							server_area.appendText("WARRING : Non-self executing the slang discriminator \n");
							break;
						}
						if(cnt == 0) server_area.appendText("SERVER WARRING : ���� ��� : "+Statics.BAD_STUDY_WORDS_TXT +"\n");
						server_area.appendText("WARRING : �弳 �Ǻ��� ���� \n�ؽ�Ʈ�� �־��ּ��� ��� : "+cnt +"\n");
						cnt++;
						synchronized (this) {
							try {
								this.wait(3000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}).start();
		*/
		
		
		
		
		
	}
	
	private void clientreset()
	{
		for(int i = 0 ;  i < clientlist.size() ; i ++)
		{
			Socket socket = clientlist.get(i);
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
