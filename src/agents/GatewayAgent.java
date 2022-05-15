package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;


public class GatewayAgent extends Agent {

    String Name;
    String ManagerAgentName;

    protected void setup() {

        GetCommArgs(this.getArguments());


        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
        MessageTemplate messageTemplateInform = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = receive(messageTemplateInform);
                if (message == null) return;
                System.out.println("Client "+message.getSender().getLocalName()+" chose our restaurant! (" + Name + ")");
            }
        });

        addBehaviour(new ProposeResponder(this, messageTemplate) {
            @Override
            protected ACLMessage prepareResponse(ACLMessage messageFromClient) {

                //System.out.println("GateWayAgent got message: "+messageFromClient.getContent());

                ACLMessage messageToManager = new ACLMessage(ACLMessage.QUERY_IF);
                messageToManager.addReceiver(new AID(ManagerAgentName, AID.ISLOCALNAME));
                messageToManager.setContent(messageFromClient.getContent());


                ACLMessage messageToClient = messageFromClient.createReply();

                send(messageToManager);
                ACLMessage response = blockingReceive();
                //System.out.println(response.getContent());

                if (response.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                    messageToClient.setContent(response.getContent());
                    messageToClient.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                } else {
                    messageToClient.setContent(response.getContent());
                    messageToClient.setPerformative(ACLMessage.REJECT_PROPOSAL);
                }

                return messageToClient;


            }
        });

    }

    private void GetCommArgs(Object[] args) {
        if (args.length != 2) {
            System.out.println("The amount of arguments is wrong");
            doDelete();
        }
        Name = args[0].toString();
        ManagerAgentName = args[1].toString();
    }

}
