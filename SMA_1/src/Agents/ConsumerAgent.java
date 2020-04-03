package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent extends GuiAgent {

    // transient ==> c'est pas serializable
    private  transient ConsumerContainer gui;
    @Override
    protected void setup() {
        /*System.out.println("*********************************");
        System.out.println("Agent Initialization ..... "+getAID());
        if(this.getArguments().length == 1){
            System.out.println("I will try to buy "+this.getArguments()[0]);
        }
        System.out.println("*********************************");*/

        if(this.getArguments().length == 1){
            this.gui = (ConsumerContainer) getArguments()[0];
            this.gui.setAgent(this);
        }

        // ParallelBehaviour pour lancer des behaviour simultanément
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        // Behaviour qui s'éxecute tout le temps
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage != null){

                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CONFIRM:
                            gui.logMessage(aclMessage);
                            break;
                    }
                }else block();

            }
        });
        /*
        // Behaviour générique
        parallelBehaviour.addSubBehaviour(new Behaviour() {
           private int compteur =0;
            @Override
            public void action() {
                ++compteur;
                System.out.println("Etape : "+compteur);
            }

            @Override
            public boolean done() {
                if(compteur > 10)
                    return true;
                return false;
            }
        });

        // Behaviour qui s'éxecute une seule fois
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("Action....");
            }
        });

        // Behaviour qui s'éxecute périodéquemet
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,1000) {
            @Override
            protected void onTick() {
                System.out.println("TIC...");
            }
        });*/

        addBehaviour(parallelBehaviour);
    }

    @Override
    protected void beforeMove() {
        System.out.println("*********************************");
        System.out.println("Avant migration .......");
        System.out.println("*********************************");
    }

    @Override
    protected void afterMove() {
        System.out.println("*********************************");
        System.out.println("Aprés migration .......");
        System.out.println("*********************************");
    }

    @Override
    protected void takeDown() {
        System.out.println("*********************************");
        System.out.println("I'm going to die .......");
        System.out.println("*********************************");
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        if(guiEvent.getType() == 1){
            String livre = (String) guiEvent.getParameter(0);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(livre);
            aclMessage.addReceiver(new AID("Achetteur",AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}
