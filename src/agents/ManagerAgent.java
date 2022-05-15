package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Random;


public class ManagerAgent extends Agent {

    String Name;
    String Cuisine;

    String Dishes;

    int OpenTime;
    int CloseTime;
    int MaxPeople;


    String MessageDish;

    int MessageTime;

    int MessagePeople;


    protected void setup() {

        GetCommArgs(this.getArguments());


        MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);


        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage received = receive(messageTemplate);
                if (null == received) return;
                ACLMessage receivedReply = received.createReply();

                try {
                    ParseMessage(received.getContent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (Dishes.contains(MessageDish) && MessagePeople <= MaxPeople && (MessageTime <= CloseTime && MessageTime >= OpenTime)) {
                    System.out.println("Dish accepted");
                    Random rnd = new Random();
                    receivedReply.setContent(String.valueOf(rnd.nextInt(100)));
                    receivedReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);


                } else {
                    receivedReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    receivedReply.setContent("-1");
                }
                send(receivedReply);
            }
        });


    }

    private void ParseMessage(String message) throws IOException {
        int dishIndex = 0;
        int peopleIndex = 0;
        //System.out.println("Got Message: " + message);
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == ';') {
                dishIndex = i;
                break;
            }
        }
        MessageDish = message.substring(0, dishIndex);
        //System.out.println(MessageDish);

        for (int i = dishIndex + 1; i < message.length(); i++) {
            if (message.charAt(i) == ';') {
                peopleIndex = i;
                break;
            }
        }

        MessagePeople = Integer.parseInt(message.substring(dishIndex + 1, peopleIndex));
        //System.out.println(MessagePeople);
        MessageTime = Integer.parseInt(message.substring(peopleIndex + 1, message.length() - 1));
        //System.out.println(MessageTime);


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
    }

}
