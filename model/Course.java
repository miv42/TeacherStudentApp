package lab3.model;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private long id;
    private String name;
    private Person teacher;
    private int maxEnrollment;
    private List<Student> studentsEnrolled;
    private int credits;

    public Course(long id, String name, Person teacher, int maxEnrollment, int credits) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.maxEnrollment = maxEnrollment;
        this.credits = credits;
        studentsEnrolled = new ArrayList<>();
    }

    //copy constructor
    public Course(Course c) {
        this.id = c.id;
        this.name = c.name;
        this.teacher = c.teacher;
        this.maxEnrollment = c.maxEnrollment;
        this.studentsEnrolled = c.studentsEnrolled;
        this.credits = c.credits;
    }

    public void addStudent(Student stud){
        studentsEnrolled.add(stud);
    }
        //-- GETTERS --//

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Person getTeacher() {
        return teacher;
    }

    public int getMaxEnrollment() {
        return maxEnrollment;
    }

    public List<Student> getStudentsEnrolled() {
        return studentsEnrolled;
    }

    public int getCredits() {
        return credits;
    }

        //-- SETTERS --//

    public void setCredits(int nr){
        credits = nr;
    }

    public void setTeacher(Teacher teahc){
        teacher = teahc;
    }

    public void setStudentsEnrolled(List<Student> studs){
        studentsEnrolled = studs;
    }

    public void setMaxEnrollment(int mE) {
        maxEnrollment = mE;
    }

    public void incrementMaxEnrollment(){
        maxEnrollment += 1;
    }


    @Override
    public String toString() {
        return  "ID: " + id +
                " - " + name + ",  " +
                "Teacher: " + teacher +
                ", maxEnrollment: " + maxEnrollment +
                ", credits: " + credits;
    }
}
