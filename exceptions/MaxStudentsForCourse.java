package lab3.exceptions;

public class MaxStudentsForCourse extends Exception {
    public MaxStudentsForCourse(String errormessage){
        super(errormessage);
    }
}
