package pt.tecnico.distledger.server.domain;

public class User {
    private String userID;

    private Integer balance;

    public User() {}

    public User(String userID) {
        this.userID  = userID;
        this.balance = 0;
    }

    public User(String userID, Integer balance) {
        this.userID = userID;
        this.balance = balance;
    }

    public String getUserID() {
        return this.userID;
    }

    public Integer getBalance() {
        return this.balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

}
