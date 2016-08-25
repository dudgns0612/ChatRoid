package FXClientsControllers;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ResourceBundle;
import com.orsoncharts.util.json.JSONObject;

import Files.FileStatics;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import tools.CustomDialog;
import tools.NetworkProtocols;
import tools.Scenecontroll;
import tools.Statics;
import tools.Toolbox;

public class ClientsStudycontroller implements Initializable{
	
	Stage stage;
	Socket socket;
	@FXML TextField answerField;
	@FXML TextField questField;
	@FXML TextField filenameField;
	@FXML ProgressBar progressBar;
	@FXML ProgressBar progressBar2; // �ϳ��� ���� �н�
	@FXML ComboBox<String> fileCombo = new ComboBox<>();
	@FXML Label stateLabel;
	@FXML Label stateLabel2;
	@FXML Button oneStudyBtn;
	@FXML Button fileStudyBtn;
	ObservableList<String> option = FXCollections.observableArrayList("��   ��", "�⺻ �ؽ�Ʈ" , "īī���� �ؽ�Ʈ");
	
	

	Listener listenner;
	
	//Stream
	ObjectOutputStream oo;
	ObjectInputStream oi;
	
	//file Stream
	ObjectOutputStream foo;
	ObjectInputStream foi;
	
	//file
	byte[] bytes;
	Socket fsocket;
	int bytesize;
	int textsize;
	int selectfile;
	File file;

	boolean fileservercheck = true;
	
	public void INIT_CONTROLLER(Stage stage , Socket socket)
	{
		this.socket = socket;
		this.stage = stage;
		oo = Scenecontroll.oo;
		oi = Scenecontroll.oi;
		fileCombo.setItems(option);
		new Listener().start();
		SendProtocol(Toolbox.JsonRequest(NetworkProtocols.SERVER_CONNECT_CHECK_REQUEST));
	}
	

	
	//�ɽ��̼��� ������
	class Listener extends Thread
	{
		public void run()
		{
			while(true)
			{
				try {			
					JSONObject request = (JSONObject)oi.readObject();
						
					String type = request.get("type").toString();
					
					if(type.equals(NetworkProtocols.LISTENER_CLOSE_RESPOND))
					{
						
						System.out.println("�н�â ����");
						return;
					}
					else if(type.equals(NetworkProtocols.SERVER_CONNECT_CHECK_RESPOND))
					{
						System.out.println("fileserver");
						fileserverStart();
					}
				} catch (SocketException e){
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							int ok = CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��������", "������ ������ ���������ϴ�.");
							if(ok == Statics.OK_SELECTION)
							{
								System.exit(0);
							}
						}
					});
					break;
				} catch(EOFException e) {
					Platform.runLater(new Runnable() {		
						@Override
						public void run() {
							int ok = CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��������", "������ ������ ���������ϴ�.");
							if(ok == Statics.OK_SELECTION)
							{
								System.exit(0);
							}	
						}
					});
					break;
				}  catch (IOException e){
					e.printStackTrace();	
				} catch (ClassNotFoundException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void fileserverStart()
	{
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					fsocket = new Socket(CustomInputDialogController.inputword, FileStatics.FSERVER_PORT);	
					if(fsocket != null)
					{
						new FileListener().start();
						System.out.println("���� ���� ���� �Ϸ�");	
						fileservercheck = true;
					}
					else
					{
						System.out.println("���� ���� ���ӿ��� ���̾�α�");
					}
				} catch(SocketException e){	
					System.out.println(socket.isConnected());
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							int ok = CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "��    ��", "���ϼ��� ���ӿ���", "���ϼ��� �������Դϴ�. ����Ŀ� �õ��Ͽ� �ּ���.");
							if(ok == Statics.OK_SELECTION)
							SendProtocol(Toolbox.JsonRequest(NetworkProtocols.LISTENER_CLOSE_REQUEST));
						}
					});
					Scenecontroll.changeScene(Statics.MAIN_FXML);
				}catch (UnknownHostException e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							int OK = CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "���� ȣ��Ʈ ����", "������ �ּҰ� �ùٸ��� �ʽ��ϴ�. �ٽ� �����Ͽ� �ּ���.");
							if(OK == Statics.OK_SELECTION)
							{
								 System.exit(0);
							}
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}								
			}
		});				
	}
	
	
	private void SendProtocol(JSONObject json)
	{
		try {
			oo.writeObject(json);
			oo.flush();
		} catch (IOException e) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					int ok = CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��������", "������ ������ ���������ϴ�.");
					if(ok == Statics.OK_SELECTION)
					{
						System.exit(0);
					}
				}
			});
		}
	}
	

	
	
	//���� �� �亯 �ϳ� ���� �����ϴ� ��ư
	@FXML
	private void onOneBtn()
	{
		String answer = answerField.getText().trim();
		String question = questField.getText().trim();
		stateLabel2.setText("�弳 �Ǻ���....^^");
		System.out.println(answer+":"+question);
		//�Ѵ� ���� �ƴϸ�
		if(!(answer.equals("")) && !(question.equals("")))
		{
			String[] keys = {"����","�亯"};
			String[] values = {question,answer};
			
			JSONObject json = Toolbox.JsonRequest(NetworkProtocols.WORD_STUDY_ONE_PROTOCOL_REQUEST, keys, values);
			System.out.println("��� : "+json.toString());
			SendFileProtocol(json);
		}
		else
		{
			CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��    ��", "������ �亯 ��� �Է��Ͽ� �ּ���.");
		}
	}
	
	//��� ��ư 
	@FXML
	private void onCancle()
	{
		Platform.runLater(new Runnable() {	
			@Override
			public void run() {
				if(fileservercheck)
				{
					if(!(stateLabel.getText().trim().contains("���ε�")) && !(stateLabel2.getText().trim().contains("�н���")))
					{
						try {
							foi.close();
							foo.close();
							fsocket.close();
						} catch(SocketException e){
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						fileservercheck = false;
					}
				}
				SendProtocol(Toolbox.JsonRequest(NetworkProtocols.LISTENER_CLOSE_REQUEST));
				Scenecontroll.changeScene(Statics.MAIN_FXML);	
			}
		});
	}
	
	//�ؽ�Ʈ ���Ϸ� �н��ϴ� ��ư
	@FXML
	private void onFileBtn()
	{
		if(file == null)
		{
			CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��    ��", "������ �������� �ʾҽ��ϴ�.");
			return;
		}
		else
		{
			fileStudyBtn.setDisable(true);
			if(selectfile == 1)
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SERVER_RECIEBER_TEXT_REQUEST);
				json.put("bytesize", bytesize);				
				SendFileProtocol(json);
				stateLabel.setText("���ε���...^^");
			}
			else if(selectfile == 2)
			{
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SERVER_RECIEBER_TEXT_REQUEST);
				json.put("bytesize", bytesize);				
				SendFileProtocol(json);
				stateLabel.setText("���ε���....^^");
			}
		}
	}
	
	//�ؽ�Ʈ ���� ���ù�ư
	@FXML
	private void onFileSelectBtn()
	{	
		Platform.runLater(new Runnable() {	
			@Override
			public void run() {	
				FileChooser filecho = new FileChooser();
				filecho.setTitle("���� ����");	
				filecho.getExtensionFilters().add(new ExtensionFilter("TXT", "*.txt"));
				if(selectfile == 0)
				{
					CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��    ��" , "������ ������ ���� �� �ּ���,");
				}
				else if(selectfile == 1)
				{
					file = filecho.showOpenDialog(stage);
					if(file == null)
					{
						return;
					}
					else
					{
						boolean encoding = Toolbox.fileEncodingConfirm(file); //���� ���ڵ� Ȯ��
						if(encoding)
						{
							String filename = file.getPath();
						    filenameField.setText(filename);
							try {
								bytes = Files.readAllBytes(file.toPath());
								bytesize = bytes.length;	
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "���� ���ڵ� ����", "���� ���ڵ��� UTF-8�� ���ּ���.");
							fileCombo.getSelectionModel().select(0);
						}
					}
					
				}
				else if(selectfile == 2)
				{
					file = filecho.showOpenDialog(stage);
					if(file == null)
					{
						return;
					}
					else
					{
						boolean encoding = Toolbox.fileEncodingConfirm(file); //���� ���ڵ� Ȯ��
						if(encoding)
						{
							String filename = file.getPath();
						    filenameField.setText(filename);
							try {
								bytes = Files.readAllBytes(file.toPath());
								bytesize = bytes.length;	
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else
						{
							CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "���� ���ڵ� ����", "���� ���ڵ��� UTF-8�� ���ּ���.");
							fileCombo.getSelectionModel().select(0);
							
						}
						
					}
				}
			}
		});
	}
	
	
	//���� ���� ������
	class FileListener extends Thread
	{
		int idx = 0;
		JSONObject frequest;
		
		public void run()
		{
			try {
				foo = new ObjectOutputStream(fsocket.getOutputStream());
				foi = new ObjectInputStream(fsocket.getInputStream());
				System.out.println("���� ��Ʈ�� ���� Ŭ��");
				while(true)
				{
					frequest = (JSONObject)foi.readObject();
					String type = frequest.get("type").toString();
					if(type.equals(NetworkProtocols.FILE_SERVER_RECIEBER_TEXT_RESPOND))
					{
						if(selectfile == 1)
						{
							JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SERVER_BASIC_SEND_REQUEST);
							SendFileProtocol(json);
							for(int i=0; i < bytesize ; i++)
							{
								foo.write(bytes[i]);
								foo.flush();
								//System.out.println(i);
								progressBar.setProgress((double)i / (double)bytesize);
							}
							JSONObject jsono = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_SUCCESS_REQUEST);
							SendFileProtocol(jsono);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									stateLabel.setText("���� �弳 �Ǻ���..");	
								}
							});
							
						}
						else
						{
							JSONObject json = Toolbox.JsonRequest(NetworkProtocols.FILE_SERVER_KAKAO_SEND_REQUEST);
							SendFileProtocol(json);
							for(int i=0; i < bytesize ; i++)
							{
								foo.write(bytes[i]);
								foo.flush();
								//System.out.println(i);
								progressBar.setProgress((double)i / (double)bytesize);
							}
							JSONObject jsono = Toolbox.JsonRequest(NetworkProtocols.FILE_SEND_SUCCESS_REQUEST);
							SendFileProtocol(jsono);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									stateLabel.setText("���� �弳 �Ǻ���..");	
								}
							});
						
						}
					}
					else if(type.equals(NetworkProtocols.FILE_SEND_SUCCESS_RESPOND))
					{	
						CustomDialog.customOkDialog(Statics.CUSTOM_OK_DIALOG_FXML, "���ε� Ȯ�� â", "��    ��!", "������ ���ε尡 �Ϸ� �Ǿ����ϴ�. ���� �� ���� �˴ϴ�.");
						Platform.runLater(new Runnable() {		
							@Override
							public void run() {
								fileStudyBtn.setDisable(false);
								filenameField.setText("");
								progressBar.setProgress(0);
								stateLabel.setText("-- -- --");		
								fileCombo.getSelectionModel().select(0);
								if(!fileservercheck)
								{
									try {
										foi.close();
										foo.close();
										fsocket.close();
									} catch(SocketException e){
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									fileservercheck = true;
								}
							}
						});
					}
					else if(type.equals(NetworkProtocols.FILE_SEND_STATE_RESPOND))
					{
						new Thread(new Runnable() {
							@Override
							public void run() {
								
								for(int i = 0+idx ; i < idx+100 ; i++)
								{
									progressBar2.setProgress((double)i / (double)1000);
									if(idx == 300)
									{
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												stateLabel2.setText("�н���....^^");
												
											}
										});
									}
									if(i == 1099)
									{
										Platform.runLater(new Runnable() {	
											@Override
											public void run() {
												oneStudyBtn.setDisable(false);
												progressBar2.setProgress(0);
												answerField.setText("");
												questField.setText("");
												stateLabel2.setText("-- -- --");
												CustomDialog.customOkDialog(Statics.CUSTOM_OK_DIALOG_FXML, "�н� Ȯ�� â", "��    ��!" , "�Է��Ͻ� ������ �亯�� �н� �Ǿ����ϴ�.");
												idx = 0;
											}
										});	
									}
								}								
								idx += 100;
							}
						}).start();
					}
					else if(type.equals(NetworkProtocols.WORD_SWEAR_INCLUDE_REQUEST))
					{
						Platform.runLater(new Runnable() {	
							@Override
							public void run() {
								CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â","������ �ܾ� ����", "�������� ���� "
										+ "�ܾ ���ԵǾ� �ֽ��ϴ�.");
								oneStudyBtn.setDisable(false);
								progressBar2.setProgress(0);
								answerField.setText("");
								questField.setText("");
								stateLabel2.setText("-- -- --");
								idx = 0;
							}
						});
					}
					else if(type.equals(NetworkProtocols.FILE_SWEAR_INCLUDE_REQUEST))
					{
						Platform.runLater(new Runnable() {	
							@Override
							public void run() {
								CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â","������ �ܾ� ���� �н��Ұ�", "�������� ���� "
										+ "�ܾ ���ԵǾ� �ֽ��ϴ�. ");
								fileStudyBtn.setDisable(false);
								filenameField.setText("");
								progressBar.setProgress(0);
								stateLabel.setText("-- -- --");		
								fileCombo.getSelectionModel().select(0);
							
							}
						});
					}
			    }
				
					
				
			} catch(SocketException e){	
				System.out.println("���� ��������");
				try {
					foo.close();
					foi.close();
					fsocket.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e){
				e.printStackTrace();
				
			}
			
			
		}
	}
	

	private void SendFileProtocol(JSONObject json)
	{
		try {
			foo.writeObject(json);
			foo.flush();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void onFileSelect()
	{
		selectfile = fileCombo.getSelectionModel().getSelectedIndex();
		//System.out.println(selectfile);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
}
