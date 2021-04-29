package com.example.garbagekings.Modules;

public class User {
    private String name, email, password, phone;
    private int bonus;

    public User() {};

    public User(String name, String email, String password, String phone, int bonus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.bonus = bonus;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public int getBonus() {
        return bonus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
