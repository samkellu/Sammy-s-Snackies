package Sammys.Snackies;


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

    public Integer getID() {
        return this.transactionID;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public Integer getQty() {
        return this.qty;
    }

    public Double getTotalAmount() {
        return this.totalAmount;
    }

    public FoodItem getProductBought() {
        return this.productBought;
    }

    public String toString() {
        return transactionID + "," + paymentMethod + "," + productBought.getName() + "," + qty;
    }
    
    // Genereates a receipt of the transaction
    public String toOutput() {
        return String.format("Transaction #%d: Payment Method: %s, %dx %s -> $%.2f", transactionID, paymentMethod, qty, productBought.getName(), totalAmount);
    }
}
