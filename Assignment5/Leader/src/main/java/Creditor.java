package main.java;

public class Creditor {
    
    private String idString;
    private boolean hasCredit;
    private int creditTaken;

    public Creditor(String name){
        this.idString = name;
        this.hasCredit = true;
        this.creditTaken = 0;
    }
    
    public String getIdString() {
        return idString;
    }
    public void setIdString(String idString) {
        this.idString = idString;
    }
    public boolean isHasCredit() {
        return hasCredit;
    }
    public void setHasCredit(boolean hasCredit) {
        this.hasCredit = hasCredit;
    }
    public int getCreditAmount() {
        return creditTaken;
    }
    public void setCreditAmount(int creditAmount) {
        this.creditTaken = creditAmount;
    }
    
    

}