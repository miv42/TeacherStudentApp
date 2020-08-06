package lab3.model;

public abstract class Person{

    private String firstName;
    private String lastName;
    private long id;

    Person(String fn, String ln, long id){
        firstName = fn;
        lastName = ln;
        this.id = id;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public long getId() {
        return id;
    }

}
