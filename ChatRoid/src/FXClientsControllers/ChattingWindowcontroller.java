package FXClientsControllers;

import java.awt.AWTException;
import java.awt.Event;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import com.orsoncharts.util.json.JSONObject;
import com.sun.javafx.css.Style;
import com.sun.javafx.scene.control.behavior.ScrollPaneBehavior;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.prism.paint.Color;
import com.sun.security.ntlm.Client;

import Communication.SelectWord;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tools.CustomDialog;
import tools.NetworkProtocols;
import tools.Scenecontroll;
import tools.Statics;
import tools.Toolbox;

public class ChattingWindowcontroller implements Initializable{
	
	//����
	@FXML ScrollPane scrollpane;
	@FXML TextField sendFiled;
	@FXML VBox chatAreaBox;
	
	ObjectOutputStream oo;
	ObjectInputStream oi;
	Stage stage;
	
	//����
	String remessage ;
	JSONObject request;
	ObservableList<VBox> chatList;
	static int cnt = 0;
	
	public void INIT_CONTROLLER(Stage stage)
	{
		this.stage = stage;
		this.oo = Scenecontroll.oo;
		this.oi = Scenecontroll.oi;
		new Listener().start();
		
		chatList = FXCollections.observableArrayList();
		Toolbox.getTime();

		
	}
	
	
	public void SendProtocols(JSONObject requestjson)
	{
		System.out.println(requestjson.toJSONString());
		try {
			oo.writeObject(requestjson);
			oo.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	class Listener extends Thread
	{
		public void run()
		{
			System.out.println("Client : Listener Thread Start");
			try {
				while(true)
				{
					 request = (JSONObject)oi.readObject();
					 
					 String type = request.get("type").toString();
					 System.out.println("������ ���� ���� ���̽� : "+request.toString());
					 if(type.equals(NetworkProtocols.MESSAGE_RECIEVER_PROTOCOL_RESPOND))
					 {
						 Platform.runLater(new Runnable() {
							@Override
							public void run() {
								 remessage = request.get("msg").toString();
								 System.out.println("Ŭ�� : "+remessage);
								 
								 chatList.removeAll(chatList);
								 chatList = Toolbox.chatAreaWrite("Bot", remessage, chatList);
								 chatAreaBox.getChildren().addAll(chatList);
								 scrollpane.setVvalue(1.0);
								
							}
						});
					 }
					 else if(type.equals(NetworkProtocols.LISTENER_CLOSE_RESPOND))
					 {
						 System.out.println("ä��â ����");
						 return;
					 }
				}	
			}catch(EOFException e){
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						int ok = CustomDialog.customWarringDialog(Statics.CUSTOM_WARNING_DIALOG_FXML, "�˸�â", "��������", "�������� ������ ���������ϴ�.");
						if(ok == Statics.OK_SELECTION)
						{
							System.exit(0);
						}
					}
				});
			}catch(SocketException e){
				//System.out.println("������ ����Ǿ����ϴ� ���̾�α� â ���� Ŭ���̾�Ʈ ����");
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sendFiled.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER)
				{
					onSend();
				}
				
			}
		});	
	}

	//�ǵ��ư��� ��ư
	@FXML public void onReturn()
	{
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SendProtocols(Toolbox.JsonRequest(NetworkProtocols.LISTENER_CLOSE_REQUEST));
				System.out.println("�ǵ��� ���� ��ư");
				Scenecontroll.changeScene(Statics.MAIN_FXML);
			}
		});
	}

	//������ ��ư
	@SuppressWarnings("unchecked")
	@FXML public void onSend()
	{
		String firstMsg;
		String twoMsg;
		Platform.runLater(new Runnable() {	
			@Override
			public void run() {
				chatList.removeAll(chatList);
				System.out.println("�������ư");
				String msg = sendFiled.getText().trim();
				
				chatList = Toolbox.chatAreaWrite("User", msg, chatList);
				chatAreaBox.getChildren().addAll(chatList);
				
				
				chatAreaBox.heightProperty().addListener(new ChangeListener() {
					@Override 
						public void changed(ObservableValue observable, Object oldvalue, Object newValue) 
						{ 
							scrollpane.setVvalue((Double)newValue); 
						} 
					}
				); 
							
			
				sendFiled.setText("");
				sendFiled.selectionProperty();
				JSONObject json = Toolbox.JsonRequest(NetworkProtocols.MESSAGE_RECIEVER_PROTOCOL_REQUEST);
				json.put("msg", msg);
				SendProtocols(json);
			}
		});
	

	}
	
	//���� ��ư
	@SuppressWarnings("deprecation")
	@FXML public void onCancle()
	{
		System.exit(0);
	}

	
}
