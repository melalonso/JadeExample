package ucr.jade.agents.example.agent;

import ucr.jade.agents.example.gui.SaleGui;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

/**
 * Agent that sells products.
 * It specifies what it sells at construction.
 */
public class SellerAgent extends Agent {
    // The catalogue of books for sale (maps the title of a book to its price)
    private Hashtable inventory;
    // The GUI by means of which the user can add books in the catalogue
    private SaleGui myGui;

    // Put agent initializations here
    protected void setup() {
        // Create the inventory
        System.out.println("Hey! Seller Agent " + getAID().getName() + " was created.");
        inventory = new Hashtable();

        // Create and show the GUI
        myGui = new SaleGui(this);
        myGui.showGui();

        // Register the book-selling service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("product-sale");
        sd.setName("Investment portfolio JADE");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour serving queries from buyer agents
        addBehaviour(new OfferRequestsServer());

        // Add the behaviour serving purchase orders from buyer agents
        addBehaviour(new PurchaseOrdersServer());
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
        myGui.dispose();
        // Printout a dismissal message
        System.out.println("Seller Agent " + getAID().getName() + " has finished.");
    }

    /**
     * This is invoked by the GUI when the user adds a new book for sale
     */
    public void updateInventory(final String product, final int price) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                inventory.put(product, new Integer(price));
                System.out.println("New product added to " + getAID().getName() + " inventory: " + product + " ,price = " + price);
            }
        });
    }
    
        /**
     * Inner class OfferRequestsServer.
     * This is the behaviour used by product-seller agents to serve incoming requests
     * for offer from buyer agents.
     * If the requested product is in the local inventory the seller agent replies
     * with a PROPOSE message specifying the price. Otherwise a REFUSE message is
     * sent back.
     */
    private class OfferRequestsServer extends CyclicBehaviour {
        
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // CFP Message received. Process it
                String product = msg.getContent();
                ACLMessage answer = msg.createReply();

                Integer price = (Integer) inventory.get(product);
                if (price != null) {
                    // The requested product is available for sale. Reply with the price
                    answer.setPerformative(ACLMessage.PROPOSE);
                    answer.setContent(String.valueOf(price.intValue()));
                } else {
                    // The requested product is NOT available for sale.
                    answer.setPerformative(ACLMessage.REFUSE);
                    answer.setContent("Not Available");
                }
                myAgent.send(answer);
            } else {
                block();
            }
        }
        
    }  // End of inner class OfferRequestsServer

    
    /**
     * Inner class PurchaseOrdersServer.
     * This is the behaviour used by product-seller agents to serve incoming
     * offer acceptances (i.e. purchase orders) from buyer agents.
     * The seller agent removes the purchased product from its inventory
     * and replies with an INFORM message to notify the buyer that the
     * purchase has been successfully completed.
     */
    private class PurchaseOrdersServer extends CyclicBehaviour {
        
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // ACCEPT_PROPOSAL Message received. Process it
                String product = msg.getContent();
                ACLMessage answer = msg.createReply();

                Integer price = (Integer) inventory.remove(product);
                if (price != null) {
                    answer.setPerformative(ACLMessage.INFORM);
                    System.out.println("The product " + product + " was sold to agent " + msg.getSender().getName());
                } else {
                    // The requested book has been sold to another buyer in the meanwhile .
                    answer.setPerformative(ACLMessage.FAILURE);
                    answer.setContent("Not Available");
                }
                myAgent.send(answer);
            } else {
                block();
            }
        }
        
    }  // End of inner class OfferRequestsServer
    
}
