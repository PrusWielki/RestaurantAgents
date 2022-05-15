package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ProposeResponder;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;


public class RestaurantAgent extends Agent {

    String Name;

    String GatewayAgentName;

    String Cuisine;

    String Dishes;

    int OpenTime;
    int CloseTime;
    int MaxPeople;


    protected void setup() {

        GetCommArgs(this.getArguments());


        AgentController agent = null;
        AgentController agent2 = null;
        try {

            agent = getContainerController().createNewAgent("GatewayAgent-" + Name, "agents.GatewayAgent", new Object[]{Name, "ManagerAgent-" + Name});
            agent2 = getContainerController().createNewAgent("ManagerAgent-" + Name, "agents.ManagerAgent", new Object[]{Name, Cuisine, Dishes, MaxPeople, OpenTime, CloseTime});

        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        try {
            agent.start();
            agent2.start();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
        RegisterAgent();


        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
        addBehaviour(new ProposeResponder(this, messageTemplate) {
            @Override
            protected ACLMessage prepareResponse(ACLMessage response) {

                System.out.println(response.getContent());
                ACLMessage accept = response.createReply();
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                accept.setContent(myAgent.getLocalName());
                return accept;

            }
        });

    }

    private void GetCommArgs(Object[] args) {
        if (args.length != 6) {
            System.out.println("The amount of arguments is wrong");
            doDelete();
        }
        Name = args[0].toString();
        Cuisine = args[1].toString();
        Dishes = args[2].toString();
        MaxPeople = Integer.parseInt(args[3].toString());
        OpenTime = Integer.parseInt(args[4].toString());
        CloseTime = Integer.parseInt(args[5].toString());
        GatewayAgentName = "GatewayAgent-" + Name;
    }

    private void RegisterAgent() {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(new AID(GatewayAgentName, AID.ISLOCALNAME));
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(Cuisine);
        serviceDescription.setName(Name);
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }


}
