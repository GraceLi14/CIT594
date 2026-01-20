package homework1;

import java.util.LinkedList;
import java.util.List;

public class TicketProcessor {
    public static void main(String[] args) {
        processTicketsLinkedList();
    }

    public static void processTicketsLinkedList() {

        LinkedList<String> ticketQueue = new LinkedList<>();

        // change to createLongQueue to test performance with a longer queue
        createShortQueue(ticketQueue);

        
        while (!ticketQueue.isEmpty()) {
            // grab the first item in the list
            String currentTicket = ticketQueue.remove(0); 
            
            System.out.println("Processing: " + currentTicket);
            
            System.out.println("Doing work....");

            System.out.println("Finished! Remaining in line: " + ticketQueue.size());
            System.out.println("---------------------------");
        }
    }

    public static void createShortQueue(List<String> queue) {
        // feel free to change the number of tickets here to test different queue sizes
        for (int i = 1; i <= 500; i++) {
            queue.add("Ticket #" + i);
        }
    }

    public static void createLongQueue(List<String> queue) {
        // feel free to change the number of tickets here to test different queue sizes
        for (int i = 1; i <= 20000; i++) {
            queue.add("Ticket #" + i);
        }
    }
}