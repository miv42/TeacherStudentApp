package lab3.model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {

    private List<Course> courses;

    public Teacher(String fn, String ln, long id) {
        super(fn, ln, id);
        this.courses = new ArrayList<>();
    }

    //copy constr
    public Teacher(Teacher t) {
        super(t.getFirstName(), t.getLastName(), t.getId());
        this.courses = t.getCourses();
    }

    public List<Course> getCourses() {
        return courses;
    }

    public String getFullName(){
        return getFirstName() + " " + getLastName();
    }

    public void setCourses(List<Course> listt){
        courses = listt;
    }


    public void addCourse(Course curs){
        courses.add(curs);
    }

    @Override
    public String toString() {
        return "ID: "+ super.getId() + " - " + super.getFirstName() + " "  + super.getLastName();
    }
}
