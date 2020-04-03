package Agents;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AchetteurGui extends Application {
    protected  AchetteurAgent agent;
    ObservableList<String> observableList;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startConatiner();
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();vBox.setPadding(new Insets(10));
        ListView<String> listView = new ListView<String>();
        observableList = FXCollections.observableArrayList();
        listView.setItems(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane,400,300);
        stage.setScene(scene);
        stage.setTitle("Achetteur Interface");
        stage.show();
    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(() -> {
            observableList.add(aclMessage.getSender().getName()+" : "+aclMessage.getContent());
        });

    }

    private void startConatiner() throws Exception {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer= runtime.createAgentContainer(profile);
        AgentController agentController = agentContainer.createNewAgent("Achetteur", "Agents.AchetteurAgent",new Object[]{this});
        agentController.start();
    }
}
