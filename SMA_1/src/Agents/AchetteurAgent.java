package Agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class AchetteurAgent extends GuiAgent {

    protected  AchetteurGui gui;
    protected AID[] sellersAgents;
    @Override
    protected void setup() {
        if(getArguments().length == 1){
            gui = (AchetteurGui) getArguments()[0];
            gui.agent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,6000) {
            @Override
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("books");
                serviceDescription.setName("Vente");
                template.addServices(serviceDescription);

                try {
                    DFAgentDescription[] results = DFService.search(myAgent,template);
                    System.out.println("Results : "+results.length);
                    sellersAgents = new AID[results.length];
                    for(int i=0;i<results.length;i++){
                        sellersAgents[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }

            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int compteur = 0;
            private List<ACLMessage> aclMessages = new ArrayList<ACLMessage>();
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null){
                    gui.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.REQUEST:
                            ACLMessage aclMessage1 = new ACLMessage(ACLMessage.CFP);
                            for(AID aid: sellersAgents){
                                aclMessage1.addReceiver(aid);
                                System.out.println(aid);
                            }
                            aclMessage1.setContent(aclMessage.getContent());

                            send(aclMessage1);
                            break;
                        case ACLMessage.PROPOSE:
                            ++compteur;
                            aclMessages.add(aclMessage);
                            if(compteur == sellersAgents.length){
                                ACLMessage meilleuroffre = aclMessages.get(0);
                                double min = Double.parseDouble(aclMessages.get(0).getContent());
                                for(ACLMessage acl : aclMessages){
                                    double price = Double.parseDouble(acl.getContent());
                                    if(price < min){
                                        meilleuroffre = acl;
                                        min = price;
                                    }
                                }
                                ACLMessage aclMessage2 = meilleuroffre.createReply();
                                aclMessage2.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                aclMessage2.setContent(meilleuroffre.getContent());
                                send(aclMessage2);
                            }
                            break;
                        case ACLMessage.AGREE:
                            ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage2.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
                            aclMessage2.setContent(aclMessage.getContent());
                            send(aclMessage2);
                            break;
                        case ACLMessage.REFUSE:
                            break;
                        default:
                            break;
                    }
                }else block();
            }
        });

        addBehaviour(parallelBehaviour);
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
