import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void CreateAgents(Runtime runtime) throws StaleProxyException {
        // Object[][] gatewayAgents = new Object[][]{new Object[]{"Bazyliszek", "ManagerAgent-Bazyliszek"},new Object[]{"Gosciniec", "ManagerAgent-Gosciniec"}, new Object[]{"Pizzeria Italiano", "ManagerAgent-Pizzeria Italiano"}, new Object[]{"Pho 67", "ManagerAgent-Pho 67"}, new Object[]{"Dreamland", "ManagerAgent-Dreamland"}, new Object[]{"Maltemi", "ManagerAgent-Maltemi"}, new Object[]{"DobraBula", "ManagerAgent-DobraBula"},new Object[]{"Jeffs", "ManagerAgent-Jeffs"}, new Object[]{"La Sirena", "ManagerAgent-La Sirena"},};
        Object[][] managerAgents = new Object[][]{new Object[]{"Bazyliszek", "Polish", "BurgerPizzaSoupJuiceChicken", "6", "10", "21"}, new Object[]{"Gessler na widelcu", "Polish", "BurgerPizzaSoupJuiceChickenPasta", "10", "9", "21"}, new Object[]{"Little Hanoi", "Asian", "SoupJuiceChickenPasta", "10", "9", "21"}, new Object[]{"Kuchnia spotkan", "Asian", "SoupJuiceChickenPasta", "10", "9", "21"}, new Object[]{"Gosciniec", "Polish", "BurgerPizzaSoupJuiceChickenPasta", "10", "10", "21"}, new Object[]{"Pizzeria Italiano", "Italian", "BurgerPizzaSoupPastaChicken", "8", "10", "23"},  new Object[]{"Le Braci Cucina", "Italian", "BurgerPizza", "8", "10", "23"}, new Object[]{"Tutto Bene Italian Bistro", "Italian", "Pizza", "4", "10", "23"},new Object[]{"Pho 67", "Asian", "SoupJuicePastaChicken", "5", "9", "19"}, new Object[]{"Dreamland", "Indian", "SoupJuiceChicken", "4", "8", "20"},  new Object[]{"Mandala", "Indian", "SoupJuiceChicken", "4", "8", "20"},new Object[]{"Namaste India", "Indian", "SoupJuiceChicken", "4", "8", "20"},new Object[]{"Maltemi", "Greek", "PastaChicken", "8", "8", "22"}, new Object[]{"Santorini", "Greek", "PastaChicken", "8", "8", "22"},new Object[]{"Taverna Patris", "Greek", "PastaChicken", "8", "8", "22"},new Object[]{"Jeffs", "American", "BurgerPizzaPastaChickenJuice", "12", "8", "23"}, new Object[]{"Pink Flamingo", "American", "BurgerPizzaPastaChickenJuice", "6", "8", "23"},new Object[]{"DobraBula", "American", "Burger", "3", "12", "20"}, new Object[]{"La Sirena", "Mexican", "SoupPasta", "6", "8", "18"},new Object[]{"The Mexican", "Mexican", "SoupPastaPizza", "6", "10", "22"},new Object[]{"Dos Tacos", "Mexican", "ChickenBurgerSoupPasta", "6", "8", "20"},};
        //Object[][] restaurantAgents = new Object[][]{new Object[]{"Bazyliszek", "Polish", "GatewayAgent-Bazyliszek"}, new Object[]{"Gosciniec", "Polish", "GatewayAgent-Gosciniec"}, new Object[]{"Pizzeria Italiano", "Italian", "GatewayAgent-Pizzeria Italiano"}, new Object[]{"Pho 67", "Asian", "GatewayAgent-Pho 67"}, new Object[]{"Dreamland", "Indian", "GatewayAgent-Dreamland"}, new Object[]{"Maltemi", "Greek", "GatewayAgent-Maltemi"}, new Object[]{"DobraBula", "American", "GatewayAgent-DobraBula"},new Object[]{"Jeffs", "American","GatewayAgent-Jeffs"}, new Object[]{"La Sirena", "Mexican", "GatewayAgent-La Sirena"},};

        for (int i = 0; i < managerAgents.length; i++) {
            Profile profile = new ProfileImpl();
            AgentContainer cont = runtime.createAgentContainer(profile);

            AgentController agent3 = cont.createNewAgent("RestaurantAgent-" + managerAgents[i][0], "agents.RestaurantAgent", managerAgents[i]);
            agent3.start();
        }
    }

    public static void main(String[] args) {

        jade.core.Runtime runtime = jade.core.Runtime.instance();


        Profile profile = new ProfileImpl();


        AgentContainer mainContainer = runtime.createMainContainer(profile);


        try {
            AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            rma.start();

            //TimeUnit.SECONDS.sleep(1);
            CreateAgents(runtime);


            AgentController client = mainContainer.createNewAgent("Patrick", "agents.ClientAgent", new Object[0]);
            client.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        } //catch (InterruptedException e) {
        //throw new RuntimeException(e);
        //}
    }
}
