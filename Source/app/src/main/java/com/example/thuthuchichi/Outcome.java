package com.example.thuthuchichi;

public class Outcome {
    private String Date;
    private int money;
    private String note;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    private int Id;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }




    public  Outcome(){}

    public Outcome(int Id,String Date,int money,String note){
        this.Id=Id;
        this.Date = Date;
        this.money = money;
        this.note = note;
    }

    public Outcome(String Date,int money,String note){
        this.Date = Date;
        this.money = money;
        this.note = note;
    }

    public int prepareQuery(StringBuilder sb){
        if(!Date.equals("")&& money>0 && !note.equals("") ){
            sb.append("(null,'"+Date+"',-"+money+",'"+note+"')");
            return 1;
        }
        else
            return 0;
    }

}
