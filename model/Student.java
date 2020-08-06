package lab3.model;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    //private long studentId;
    private int totalCredits;
    private List<Course> enrolledCourses;

    public Student(long sID, String fname, String lname){
        super(fname, lname, sID);
        enrolledCourses = new ArrayList<>();
        totalCredits = 0;
    }

         //-- GETTERS --//

    public long getStudentId(){
        return this.getId();
    }

    public String getStudentFullName(){return this.getFirstName() + " " + this.getLastName();}

    public int getTotalCredits(){
        return totalCredits;
    }

    public List<Course> getEnrolledCourses(){
        return enrolledCourses;
    }

        //-- SETTERS --//

    public void setTotalCredits(int tc){
        totalCredits = tc;
    }

    public void setEnrolledCourses( List<Course> listt){
        enrolledCourses = listt;
    }

    @Override
    public String toString() {
        return "ID: " + getId() + " - " + getFirstName() + " " + getLastName();
    }
}
