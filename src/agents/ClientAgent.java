package agents;

import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

import java.util.Random;
import java.util.Vector;

public class ClientAgent extends Agent {

    private Vector<AID> gatewayAgents;

    private Vector<AID> gatewayAgentsAccepted;
    private String selectedCuisine;
    private boolean inRestaurant;

    Behaviour orderFood = new TickerBehaviour(this, 2000) {
        @Override
        protected void onTick() {
            if (gatewayAgentsAccepted.size() > 0 && !inRestaurant) {

                Random rnd = new Random();
                int chosenGatewayAgent = rnd.nextInt(gatewayAgentsAccepted.size());
                inRestaurant = true;
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(gatewayAgentsAccepted.get(chosenGatewayAgent));
                message.setContent("I choose your restaurant");
                send(message);
            }
        }
    };
    Behaviour findFood = new TickerBehaviour(this, 1000) {
        @Override
        protected void onTick() {
            if (inRestaurant) return;
            if (0 == gatewayAgents.size()) getGatewayAgents(myAgent);
            else {
                Random rnd = new Random();
                String dish = GetSomeDish();
                int people = rnd.nextInt(10) + 1;
                int hour = rnd.nextInt(24) + 1;
                for (final AID gatewayAgent : gatewayAgents) {
                    if (gatewayAgentsAccepted.contains(gatewayAgent)) continue;
                    ACLMessage callForProposal = new ACLMessage(ACLMessage.QUERY_IF);
                    callForProposal.addReceiver(gatewayAgent);


                    System.out.println("Client sends request for dish: " + dish + " for " + people + " people, at " + hour + " to "+gatewayAgent.getLocalName());
                    callForProposal.setContent(dish + ";" + people + ";" + hour + ";");
                    addBehaviour(new ProposeInitiator(myAgent, callForProposal) {
                        @Override
                        protected void handleAcceptProposal(ACLMessage accept_response) {

                            //System.out.println(accept_response.getContent());
                            System.out.println(accept_response.getSender().getLocalName() + " said yes");
                            if (!gatewayAgentsAccepted.contains(gatewayAgent)) gatewayAgentsAccepted.add(gatewayAgent);
                        }

                        @Override
                        protected void handleRejectProposal(ACLMessage reject_proposal) {
                            System.out.println(reject_proposal.getSender().getLocalName() + " said no");
                            gatewayAgents.remove(gatewayAgent);
                        }
                    });
                }
            }
        }
    };

    protected void setup() {
        System.out.println("Client " + getLocalName() + " is looking for food!");
        gatewayAgents = new Vector<>();

        gatewayAgentsAccepted = new Vector<>();

        inRestaurant = false;

        selectedCuisine = GetSelectedCuisine();

        System.out.println("Client " + getLocalName() + " would like to order " + selectedCuisine + " food");

        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        addBehaviour(findFood);
        addBehaviour(orderFood);
    }

    private void getGatewayAgents(jade.core.Agent myAgent) {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(selectedCuisine);
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFAgentDescription[] result = DFService.search(myAgent, dfAgentDescription);

            if (0 == result.length) {
                System.out.println("Client " + getLocalName() + " is not eating today");
                return;
            }

            for (DFAgentDescription agentDescription : result) {
                //System.out.println(result[i].getName());
                gatewayAgents.add(agentDescription.getName());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    private String GetSelectedCuisine() {
        Random rnd = new Random();
        int selection = rnd.nextInt(7);
        return switch (selection) {
            case 0 -> "Polish";
            case 1 -> "Asian";
            case 2 -> "Greek";
            case 3 -> "American";
            case 4 -> "Italian";
            case 5 -> "Mexican";
            case 6 -> "Indian";
            default -> "";
        };
    }

    private String GetSomeDish() {
        Random rnd = new Random();
        int selection = rnd.nextInt(6);
        return switch (selection) {
            case 0 -> "Pizza";
            case 1 -> "Burger";
            case 2 -> "Chicken";
            case 3 -> "Soup";
            case 4 -> "Juice";
            case 5 -> "Pasta";
            default -> "";
        };

    }


}
