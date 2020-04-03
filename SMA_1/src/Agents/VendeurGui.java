package Agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class VendeurGui extends Application {
    protected  VendeurAgent agent;
    AgentContainer agentContainer;
    ObservableList<String> observableList;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startConatiner();
        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox();hBox.setSpacing(10);hBox.setPadding(new Insets(10));
        Label label = new Label("Agent Name :");
        TextField textFieldAgentName = new TextField();
        Button buttonDeploy = new Button("Deploy");
        hBox.getChildren().addAll(label,textFieldAgentName,buttonDeploy);


        VBox vBox = new VBox();vBox.setPadding(new Insets(10));
        ListView<String> listView = new ListView<String>();
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        borderPane.setTop(hBox);

        Scene scene = new Scene(borderPane,400,300);
        stage.setScene(scene);
        stage.setTitle("Vendeurs Interface");
        stage.show();

        buttonDeploy.setOnMouseClicked(e -> {
            if(!textFieldAgentName.getText().toString().isEmpty()){
                try {
                    AgentController agentController = agentContainer.createNewAgent(textFieldAgentName.getText(), "Agents.VendeurAgent",new Object[]{this});
                    agentController.start();
                } catch (StaleProxyException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableList.add(aclMessage.getSender().getName()+" -> "+this.agent.getAID()+" : "+aclMessage.getContent());
        });

    }

    private void startConatiner() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer= runtime.createAgentContainer(profile);

        agentContainer.start();
    }
}
