package Sammys.Snackies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.HashMap;

public class App {

    private static double parseDenom(String s) {

        switch (s) {
            case "5c":
                return 0.05;
            case "10c":
                return 0.10;
            case "20c":
                return 0.20;
            case "50c":
                return 0.50;
            case "$1":
                return 1.00;
            case "$2":
                return 2.00;
            case "$5":
                return 5.00;
            case "$10":
                return 10.00;
            case "$20":
                return 20.00;
            case "$50":
                return 50.00;
            case "$100":
                return 100.00;
            default:
                System.out.println("Please enter a valid monetary denomination.");
                return -1;
        }
    }

    private static HashMap<String, Integer> getDenominations(double totalGiven){
        HashMap<String, Integer> denominations = new HashMap<String, Integer>();
        denominations.put("5c", 0);
        denominations.put("10c", 0);
        denominations.put("20c", 0);
        denominations.put("50c", 0);
        denominations.put("$1", 0);
        denominations.put("$2", 0);
        denominations.put("$5", 0);
        denominations.put("$10", 0);
        denominations.put("$20", 0);
        denominations.put("$50", 0);
        denominations.put("$100", 0);

        while( totalGiven >= 100){
            totalGiven =- 100;
            denominations.put("$100", denominations.get("$100")+ 1);
        }
        while( totalGiven >= 50){
            totalGiven =- 50;
            denominations.put("$50", denominations.get("$50")+ 1);
        }
        while( totalGiven >= 20){
            totalGiven =- 20;
            denominations.put("$20", denominations.get("$20")+ 1);
        }
        while( totalGiven >= 10){
            totalGiven =- 10;
            denominations.put("$10", denominations.get("$10")+ 1);
        }
        while( totalGiven >= 5){
            totalGiven =- 5;
            denominations.put("$5", denominations.get("$5")+ 1);
        }
        while( totalGiven >= 2){
            totalGiven =- 2;
            denominations.put("$2", denominations.get("$2")+ 1);
        }
        while( totalGiven >= 1){
            totalGiven =- 1;
            denominations.put("$1", denominations.get("$1")+ 1);
        }
        while( totalGiven >= 0.50){
            totalGiven =- 0.50;
            denominations.put("50c", denominations.get("50c")+ 1);
        }
        while( totalGiven >= 0.20){
            totalGiven =- 0.20;
            denominations.put("20c", denominations.get("20c")+ 1);
        }
        while( totalGiven >= 0.10){
            totalGiven =- 0.10;
            denominations.put("10c", denominations.get("10c")+ 1);
        }
        while( totalGiven >= 0.05){
            totalGiven =- 0.05;
            denominations.put("5c", denominations.get("5c")+ 1);
        }

        return denominations;
    }

    private static void buyer(ArrayList<String> inputs) {

        // ensure enough arguments
        if (inputs.size() < 5) {
            System.out.println("Not enough arguments. Use \"help buyer\" to see required arguments");
            return;
        }

        // check cash or card
        boolean cash = false;
        if (inputs.get(1).toLowerCase().equals("cash"))
            cash = true;
        else if (!inputs.get(1).toLowerCase().equals("card")) {
            System.out.println("Please specify payment type (cash or card).");
            return;
        }

        // TODO
        // check product validity, need vending machine to know what's there

        // TODO
        // check product quantity, need vending machine to know the quantity

        // TODO
        // if cash, check denominations
        // get totalOwed
        
        int[] denoms = new int[11];
        double totalGiven = 0.00;
        if (cash) {
            ArrayList<String> inputDenoms = new ArrayList<String>(inputs.subList(4, inputs.size()-1));

            for (String s : inputDenoms) {
                String[] values = s.split("*");

                if (values.length != 2) {
                    System.out.println("Unrecognisable denomination.\nPlease use the format <value>*<amount>, where value can be 50c, $2, $5 etc. and amount is a positive integer.");
                    return;
                }
                
                String v = values[0];
                String amount = values[1];
                int amt = -1;
                
                try {
                    amt = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    System.out.println("Please ensure the amount is a positive integer.");
                    return;
                } finally {
                    if (amt <= 0) {
                        System.out.println("Please ensure the amount is a positive integer.");
                        return;
                    }
                }

                double value = parseDenom(v);
                if (value == -1) return;

                totalGiven += value*amt;

                //Returns the denominations in a hashmap of the change
                HashMap<String, Integer> ChangeDenominations = getDenominations(totalGiven);

                
            }
        }

        // TODO
        // ensure enough money given, need the vending machine to know the price.
    }

    private static void seller(ArrayList<String> inputs) {

    }

    private static void supplier(ArrayList<String> inputs) {

    }

    private static void owner(ArrayList<String> inputs) {

    }

    private static void userLogin(ArrayList<String> inputs) {

        if (inputs.size() != 3) {
            System.out.println("Incorrect number of parameters. Use \"help login\" for more information.");
            return;
        }

        // TODO
        // check login, maybe we use a file of users and pwds?
    }

    private static void helpCommand(ArrayList<String> inputs) {
        if (inputs.size() >= 2) {
            switch(inputs.get(1).toLowerCase()) {
                case "buyer":
                    System.out.println("\nUse this command to buy a product from the vending machine.");
                    System.out.println("Usage:");
                    System.out.println("buyer <cash/card> <product> <amount> [denominations...]\n");
                break;
                case "seller":
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("seller TODO\n");
                break;
                case "owner":
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("owner TODO\n");
                break;
                case "supplier":
                    System.out.println("\nUse this command to TODO");
                    System.out.println("Usage:");
                    System.out.println("supplier TODO\n");
                break;
                case "login":
                    System.out.println("\nUse this command to log in to a supplier/owner/seller account.");
                    System.out.println("Usage:");
                    System.out.println("login <username> <password>\n");
                break;
                case "help":
                    System.out.println("\nUse this command to see available commands or for more information on a command");
                    System.out.println("Usage:");
                    System.out.println("help [command]\n");
                break;
                case "quit":
                    System.out.println("\nUse this command to quit the program.");
                    System.out.println("Usage:");
                    System.out.println("quit\n");
                break;
                default:
                    System.out.println(String.format("\nUnrecognised command: %s\n", inputs.get(1)));
                break;
            }
        } else {
            System.out.println("\nAvailable Commands:");
            System.out.println("buyer - buy a product");
            System.out.println("seller - TODO"); // TODO
            System.out.println("owner - TODO"); // TODO
            System.out.println("supplier - TODO"); // TODO
            System.out.println("login - login to a supplier/owner/seller account");
            System.out.println("help - display this screen");
            System.out.println("quit - quit the program\n");
        }
    }

    private static void endProgram() {
        System.out.println("Quitting...");
    }

    private static void unknownCommand(ArrayList<String> inputs) {
        System.out.println("Unknown Command, use the help command to see available commands");
    }


    public static void main(String[] args) {
        
        Scanner s = new Scanner(System.in);

        // VendingMachine vm = new VendingMachine();

        System.out.println("Welcome to Sammy's Snackies!");
        while (true){
            while(s.hasNextLine()){
                String input = s.nextLine();
                String[] userInput = input.split(" ");
                ArrayList<String> inputs = new ArrayList<String>(Arrays.asList(userInput));
                String cmd = inputs.get(0);
    
                switch(cmd.toLowerCase()) {
    
                    case "buyer":
                        buyer(inputs);
                    break;
                    case "seller":
                        seller(inputs);
                    break;
                    case "supplier":
                        supplier(inputs);
                    break;
                    case "owner":
                        owner(inputs);
                    break;
                    case "login":
                        userLogin(inputs);
                    break;
                    case "help":
                        helpCommand(inputs);
                    break;
                    case "quit":
                        endProgram();
                        s.close();
                        return;
                    default:
                        unknownCommand(inputs);
                    break;
                }
            }
        }


    }

}
