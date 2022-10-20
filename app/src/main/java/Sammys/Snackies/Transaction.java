package Sammys.Snackies;

import java.util.ArrayList;
import java.util.HashMap;

// When writing the sale code in vendingMachine, create a new transaction and increment the transactionID  

public class Transaction {

    private Double totalAmount;
    private Integer transactionID;
    private String paymentMethod;
    private FoodItem productBought;
    private Integer qty;

    public Transaction(Integer transactionID, String paymentMethod, FoodItem productBought, Integer qty) {
        this.transactionID = transactionID;
        this.paymentMethod = paymentMethod;
        this.productBought = productBought;
        this.qty = qty;

        totalAmount = productBought.getPrice() * qty;
    }

    // Genereates a receipt of the transaction
    public String toString() {
        // Could add cash denominations here for cash payments buy idc
        return "Transaction #" + transactionID + "\n" + productBought.getName() + " x" + qty + "\nTotal: $" + totalAmount + "\nPayment Method: " + paymentMethod;
    }
}
