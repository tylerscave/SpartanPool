package controller;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Context;
import model.NotificationSender;
import model.member.Member;

/**
 *COPYRIGHT (C) 2016 CmpE133_7. All Rights Reserved.
 * The controller for the SendNotificationScene
 * Solves CmpE133 SpartanPool
 * @author Tyler Jones,
*/
public class SendNotificationController implements Initializable {
	
    private Context context;
    private Member member;
    @FXML
    private TextField recipiantEmailField;
    @FXML
    private TextField notificationField;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
            context = Context.getInstance();
            member = context.getMember();
	}
	
    @FXML
    private void handleCancelButton(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/NotificationMenuScene.fxml"));
            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleSubmitButton(ActionEvent event) {
    	sendMessage(event);
    }
    
    @FXML
    private void onEnter(ActionEvent event) {
    	sendMessage(event);
    }
    
    private void sendMessage(ActionEvent event) {
        String email = recipiantEmailField.getText();
        String text = notificationField.getText();
        System.out.println(email+", "+text);
        if (email.equals("") || text.equals(""))
            return;
        
        NotificationSender ns = new NotificationSender(member);
        ns.send(email, text);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/NotificationMenuScene.fxml"));
            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            Logger.getLogger(LoginSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
