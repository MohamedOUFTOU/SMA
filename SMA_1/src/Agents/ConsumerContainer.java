package Agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
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

public class ConsumerContainer extends Application {

    protected ConsumerAgent agent;
    ObservableList<String> observableList;

    public void setAgent(ConsumerAgent agent) {
        this.agent = agent;
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void startContainer() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Consumer","Agents.ConsumerAgent",new Object[] {this});
        agentController.start();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableList.add(aclMessage.getSender().getName()+" : "+aclMessage.getContent());
        });

    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);

        Label label = new Label("Livre :");
        TextField textFieldLiver = new TextField();
        Button buttonAchetter = new Button("Acheter");
        hBox.getChildren().addAll(label,textFieldLiver,buttonAchetter);

        VBox vBox = new VBox();
        ListView<String> listView = new ListView<String>();
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);
        vBox.getChildren().addAll(listView);
        vBox.setPadding(new Insets(10));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane,600,400);
        stage.setScene(scene);
        stage.setTitle("Consumer Interface");
        stage.show();

        buttonAchetter.setOnMouseClicked(e -> {
            if(!textFieldLiver.getText().toString().isEmpty()){
                String livre = textFieldLiver.getText().toString();
                //observableList.add(livre);
                GuiEvent event = new GuiEvent(this,1);
                event.addParameter(livre);
                this.agent.onGuiEvent(event);
            }
        });
    }
}
