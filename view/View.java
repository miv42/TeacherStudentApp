package lab3.view;

import lab3.controller.Controller;
import lab3.model.Course;
import lab3.model.Student;
import lab3.model.Teacher;
import lab3.repository.course.CoursesDBRepository;
import lab3.repository.course.CoursesFileRepository;
import lab3.repository.student.StudentsDBRepository;
import lab3.repository.student.StudentsFileRepository;
import lab3.repository.teacher.TeachersDBRepository;
import lab3.repository.teacher.TeachersFileRepository;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class View {

    //private StudentsDBRepository students;
    //private CoursesDBRepository courses;
    //private TeachersDBRepository teachers;

    private StudentsFileRepository students;
    private CoursesFileRepository courses;
    private TeachersFileRepository teachers;

    private Controller ctrl;

    public View() throws FileNotFoundException, SQLException, ClassNotFoundException {
        //students = new StudentsDBRepository();
        //teachers = new TeachersDBRepository();
        //courses = new CoursesDBRepository();

        students = new StudentsFileRepository("resources\\students.txt");
        teachers = new TeachersFileRepository("resources\\teachers.txt");
        courses = new CoursesFileRepository("resources\\courses.txt");

        ctrl = new Controller(students, teachers, courses);
    }

    private void showMenu(){
        System.out.println("-------------------------");
        System.out.println("Please choose one option:");
        System.out.println("1. Show Courses with free places");
        System.out.println("2. Show Students enrolled to a certain course");
        System.out.println("3. Show all Courses");
        System.out.println("4. Register a Student to a Course");
        System.out.println("5. Delete a Course");
        System.out.println("6. Modify credits of a Course");
        System.out.println("7. Show all Courses over 7 credits");
        System.out.println("0. Exit");
    }

    // -- display functions -- //

    private void showCoursesWithFreePlaces() throws SQLException, ClassNotFoundException {
        List<Course> courses = (List<Course>) ctrl.retrieveCoursesWithFreePlaces();
        for(Course curs: courses){
            System.out.println(curs);
        }
    }

    private void showAllCourses() throws SQLException, ClassNotFoundException {
        List<Course> allCourses = (List<Course>) ctrl.getAllCourses();
        for(Course crs: allCourses){
            System.out.println(crs);
        }
    }

    private void showAllStudents() throws SQLException, ClassNotFoundException {
        List<Student> allStudents = (List<Student>) ctrl.getAllStudents();
        for(Student stud: allStudents){
            System.out.println(stud);
        }
    }

    private void showAllTeachers() throws SQLException, ClassNotFoundException {
        List<Teacher> allTeachers = (List<Teacher>) ctrl.getAllTeachers();
        for(Teacher teach: allTeachers){
            System.out.println(teach);
        }
    }

              // --  //

    private void showStudentForChosenCourse() throws SQLException, ClassNotFoundException {
        System.out.println("Choose course ID: ");
        showAllCourses();
        Scanner scanner = new Scanner(System.in);
        String input;
        input = scanner.nextLine();

        long courseID = Long.parseLong(input);
        Course curs = ctrl.findCourseByID(courseID);

        List<Student> studentsEnrolled = (List<Student>) ctrl.retrieveStudentEnrolledForACourse(curs);
        for(Student stud: studentsEnrolled){
            System.out.println(stud);
        }
    }

    private void showRegisterResults() throws SQLException, ClassNotFoundException {
        System.out.println("Choose Student ID: ");
        showAllStudents();
        Scanner scanner = new Scanner(System.in);
        String input;
        input = scanner.nextLine();

        long studentID = Long.parseLong(input);
        Student st = ctrl.findStudentByID(studentID);

        System.out.println("Choose Course ID: ");
        showAllCourses();
        input = scanner.nextLine();

        long courseID = Long.parseLong(input);
        Course cr = ctrl.findCourseByID(courseID);

        try{
            ctrl.register(cr, st);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showDeleteCourseResults() throws SQLException, ClassNotFoundException {
        System.out.println("Choose Course ID: ");
        String input;
        Scanner scanner = new Scanner(System.in);

        showAllCourses();
        input = scanner.nextLine();

        long courseID = Long.parseLong(input);
        Course deletThisCourse = ctrl.findCourseByID(courseID);

        showAllTeachers();
        input = scanner.nextLine();

        long teacherID = Long.parseLong(input);
        Teacher deletThisTeacher = ctrl.findTeacherByID(teacherID);

        ctrl.teacherQuitsCourse(deletThisTeacher, deletThisCourse);
    }

    private void showCoursesOver7Credits() throws SQLException, ClassNotFoundException {
        List<Course> curses = ctrl.coursesOverANumberOfCredits();
        for(Course curs: curses){
            System.out.println(curs);
        }
    }

    private void modifyCreditsOfCourse() throws SQLException, ClassNotFoundException {
        System.out.println("Choose Course ID: ");
        String input;
        Scanner scanner = new Scanner(System.in);

        showAllCourses();
        input = scanner.nextLine();

        long courseID = Long.parseLong(input);
        ctrl.updateCourse(ctrl.findCourseByID(courseID));
    }

    private void waitForInput(Scanner scanner) {
        System.out.println("Press ENTER to continue...");
        scanner.nextLine();
    }

    private void showInvalidInput() {
        System.out.println("The option you selected is not valid. Please try again!");
    }

//    private void saveReposToFile(){
//        try{
//            students.writeToFile("F:\\Java\\Java facultate\\lab3\\resources\\students.txt");
//            courses.writeToFile("F:\\Java\\Java facultate\\lab3\\resources\\courses.txt");
//            teachers.writeToFile("F:\\Java\\Java facultate\\lab3\\resources\\teachers.txt");
//        }catch(Exception e){
//            System.out.println("Something went wrong with saving to file :(");
//        }
//
//    }

    public void start() throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String input;
        showMenu();
        while (!(input = scanner.nextLine()).equals("0")) {
            switch (input) {
                case "1":
                    showCoursesWithFreePlaces();
                    break;
                case "2":
                    showStudentForChosenCourse();
                    break;
                case "3":
                    showAllCourses();
                    break;
                case "4":
                    showRegisterResults();
                    break;
                case "5":
                    showDeleteCourseResults();
                    break;
                case "6":
                    modifyCreditsOfCourse();
                    break;
                case "7":
                    showCoursesOver7Credits();
                    break;
                default:
                    showInvalidInput();
                    break;
            }
            waitForInput(scanner);
            showMenu();
        }
        //saveReposToFile();
        System.out.println("Job's done");
        scanner.close();
    }
}
