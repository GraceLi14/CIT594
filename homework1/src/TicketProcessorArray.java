package homework1.src;

import java.util.ArrayList;
import java.util.List;

public class TicketProcessorArray {
    public static void main(String[] args) {
            processTicketsArrayList();
                }

    public static void processTicketsArrayList() {

        long totalRunTime = 0; //keep track of total runtime of all trials
        int trials = 10; //# of trials

        for (int i = 0; i<trials; i++){

            ArrayList<String> ticketQueue = new ArrayList<>();

            // Uncomment the queue length you want to test with
            // createShortQueue(ticketQueue);
            createLongQueue(ticketQueue);

            long start = System.nanoTime(); // start testing runtime
            while (!ticketQueue.isEmpty()) {
                // grab the last item in the list
                String currentTicket = ticketQueue.remove(ticketQueue.size()-1);

                // Comment out while benchmarking
                // System.out.println("Processing: " + currentTicket);

                // System.out.println("Finished! Remaining in line: " + ticketQueue.size());
                // System.out.println("---------------------------");
        }
            long end = System.nanoTime(); // end testing runtime

            //add new runtime to totalRunTime
            totalRunTime += (end-start);
        }

        long avgRunTime = (totalRunTime/trials);
        System.out.println("Average run time: " + avgRunTime/ 1000000.0);

    }

    public static void createShortQueue(List<String> queue) {
        // feel free to change the number of tickets here to test different queue sizes
        for (int i = 1; i <= 50; i++) {
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