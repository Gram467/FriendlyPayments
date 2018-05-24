package sithrak.fps;

/**
 * Created by Sithrak on 19.04.2018..
 */

public class ContactSupport {

    // fields

    private int ContactID;
    private String CName;
    private String CSurname;
    private int Number;
    private String Email;
    private String BName;
    private String BNum;


    // construct
    public ContactSupport(){}
    public ContactSupport(String name, String surname, int num, String email, String bank_name, String bank_num){
//        this.ContactID = id;
        this.CName = name;
        this.CSurname = surname;
        this.Number = num;
        this.Email = email;
        this.BName = bank_name;
        this.BNum = bank_num;
    }

    // properties

    public void setContactID(int cid) { this.ContactID = cid; }
    public int getContactID() { return this.ContactID; }

    public void setCName(String name) { this.CName = name; }
    public String getCName() { return this.CName; }

    public void setCSurname(String surname) { this.CSurname = surname; }
    public String getCSurname() { return this.CSurname; }

    public void setNumber(int num) { this.Number = num; }
    public int getNumber() { return this.Number; }

    public void setEmail(String mail) { this.Email = mail; }
    public String getEmail() { return this.Email; }

    public void setBName(String bName) { this.BName = bName; }
    public String getBName() { return this.BName; }

    public void setBNum(String bNum) { this.BNum = bNum; }
    public String getBNum() { return this.BNum; }


}
