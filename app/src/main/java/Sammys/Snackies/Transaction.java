package Sammys.Snackies;


// When writing the sale code in vendingMachine, create a new transaction and increment the transactionID  

public class Transaction {

    private Double totalAmount;
    private Integer transactionID;
    private String paymentMethod;
    private FoodItem productBought;
    private String userName;
    private Integer qty;

    public Transaction(Integer transactionID, String paymentMethod, FoodItem productBought, Integer qty, String userName) {
        this.transactionID = transactionID;
        this.paymentMethod = paymentMethod;
        this.productBought = productBought;
        this.qty = qty;
        this.userName = userName;
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

    public String getUserName() {
        return this.userName;
    }

    public Double getTotalAmount() {
        return this.totalAmount;
    }

    public FoodItem getProductBought() {
        return this.productBought;
    }

    public String toString() {
        return transactionID + "," + paymentMethod + "," + productBought.getName() + "," + qty + "," + userName;
    }
}
