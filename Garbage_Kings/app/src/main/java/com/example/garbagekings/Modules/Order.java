package com.example.garbagekings.Modules;

public class Order {
    public Order () {};

    public Order(String address, String typeOfGarbage, String comment, int status) {
        this.address = address;
        this.typeOfGarbage = typeOfGarbage;
        this.comment = comment;
        this.status = status;
    }

    public Order(String address, String typeOfGarbage, String comment, int number, int status, int bonus)
    {
        this.address = address;
        this.typeOfGarbage = typeOfGarbage;
        this.comment = comment;
        this.number = number;
        this.status = status;
        this.bonus = bonus;
    }

    public String getAddress() {
        return address;
    }

    public String getTypeOfGarbage() {
        return typeOfGarbage;
    }

    public String getComment() {
        return comment;
    }


    public int getNumber() {
        return number;
    }

    public int getStatus() {
        return status;
    }

    public int getBonus() {
        return bonus;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTypeOfGarbage(String typeOfGarbage) {
        this.typeOfGarbage = typeOfGarbage;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    private String address, typeOfGarbage, comment;
    private int number, status, bonus;
}
