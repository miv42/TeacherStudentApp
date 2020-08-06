package lab3.controller;
import lab3.exceptions.AlreadyEnrolledException;
import lab3.exceptions.CreditsExceededException;
import lab3.exceptions.MaxStudentsForCourse;
import lab3.model.Course;
import lab3.model.Student;
import lab3.model.Teacher;
import lab3.repository.course.CoursesDBRepository;
import lab3.repository.course.CoursesRepo;
import lab3.repository.student.StudentsRepo;
import lab3.repository.teacher.TeachersRepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Controller {

    private StudentsRepo students;
    private TeachersRepo teachers;
    private CoursesRepo courses;

    public Controller(StudentsRepo sRepo, TeachersRepo tRepo, CoursesRepo cRepo) throws SQLException, ClassNotFoundException {
        students = sRepo;
        teachers = tRepo;
        courses = cRepo;

        bindObjects();
    }

    private void bindObjects() throws SQLException, ClassNotFoundException {
        List<Course> coursesList = (List<Course>) courses.findAll();
        List<Course> toUpdateCourses = new ArrayList<>();
        //add courses to teachers and vice versa
        for(Course curs: coursesList){
            //get the teacher corresponding to the course. set as teacher.
            Teacher teach = teachers.findOne(curs.getTeacher().getId());
            curs.setTeacher(teach);
            //add course to teacher courses.
            teach.addCourse(curs);

            teachers.update(teach);
            toUpdateCourses.add(curs);
        }

        for(Course curs: toUpdateCourses){
            courses.update(curs);
        }

        List<Student> studentsList = (List<Student>) students.findAll();
        List<Student> toUpdateStudents = new ArrayList<>();
        //for each student replace the placeholder courses list with a list of the actual courses
        for(Student stud: studentsList){
            List<Course> enrolledCourses = new ArrayList<>();
            int credits = 0;
            for(Course curs: stud.getEnrolledCourses()){

                Course searchCurs = courses.findOne(curs.getId());  //get actual course

                if(searchCurs != null){
                    enrolledCourses.add(searchCurs);
                    searchCurs.addStudent(stud);
                    courses.update(searchCurs);
                    credits += searchCurs.getCredits();
                }
            }
            stud.setEnrolledCourses(enrolledCourses);
            stud.setTotalCredits(credits);
            //students.update(stud);              //update student with the actual enrolledCourses
            toUpdateStudents.add(stud);
        }
        for(Student stud: toUpdateStudents){
            students.update(stud);
        }
    }

    /**
     * @param curs - course to which the student enrolls
     * @param stud - the student who enrolls
     * @return - true if the student can enroll to the given course
     * */
    public boolean register(Course curs, Student stud) throws MaxStudentsForCourse, AlreadyEnrolledException, CreditsExceededException, SQLException {
        if(curs.getStudentsEnrolled().size() == curs.getMaxEnrollment()){
            throw new MaxStudentsForCourse("The max nr of students for this course has been reached");           //max enrollment achieved
        }else{
            for(Course c : stud.getEnrolledCourses()){
                if(c.getId() == curs.getId()){
                    throw new AlreadyEnrolledException("Already enrolled to this course");   //already enrolled to this course
                }
            }
        }
        if(stud.getTotalCredits() + curs.getCredits() > 30){
            throw new CreditsExceededException("Max credit number exceeded");           //with this course the maximum credits will be exceeded
        }

        //add student to course list
        List<Student> courseStuds = curs.getStudentsEnrolled();
        courseStuds.add(stud);
        curs.setStudentsEnrolled(courseStuds);

        //add course to list
        List<Course> enrolledCs = stud.getEnrolledCourses();
        enrolledCs.add(curs);
        stud.setEnrolledCourses(enrolledCs);

        //increase credit number
        int creds = stud.getTotalCredits() + curs.getCredits();
        stud.setTotalCredits(creds);

        //update course and student
        courses.update(curs);
        students.update(stud);
        if(courses.getClass() == CoursesDBRepository.class)
            registerDB(curs, stud);

        return true;
    }

    /**
     * updates the database with the new enrolled data
     *
     * @param curs
     * @param stud
     * @throws SQLException
     */
    public void registerDB(Course curs, Student stud) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Hogwarts","postgres" , "admin");
        Statement st = con.createStatement();

        st.executeUpdate("INSERT INTO public.enrolled(\n" +
                "\t\"studentID\", \"courseID\")\n" +
                "\tVALUES (" + stud.getId() + ", " + curs.getId() + ")");

        st.close();
    }

    /**
     * @return - a list of courses where enrolled students list size smaller than maxEnrollment
     * */
    public Iterable<Course> retrieveCoursesWithFreePlaces() throws SQLException, ClassNotFoundException {
        Iterable<Course> allCourses = courses.findAll();

        return StreamSupport.stream(allCourses.spliterator(), false).filter(e-> e.getStudentsEnrolled().size() < e.getMaxEnrollment()).sorted(Comparator.comparing(Course::getId)).collect(Collectors.toList());
    }

    /**
     * @param curs - the the course for which the enrolled students are sought
     * @return - an iterable object of all the Students enrolled for the given course
     * */
    public Iterable<Student> retrieveStudentEnrolledForACourse(Course curs) throws SQLException {
        Course toGet = courses.findOne(curs.getId());
        return sortStudentsByName(toGet.getStudentsEnrolled());
    }

    /**
     * @return - returns an iterable Object of all the courses in the repository
     * */
    public Iterable<Course> getAllCourses() throws SQLException, ClassNotFoundException {
        return sortCoursesByID((List<Course>) courses.findAll());
    }

    public Iterable<Student> getAllStudents() throws SQLException, ClassNotFoundException {
        return sortStudentsByName((List<Student>) students.findAll());
    }

    public Iterable<Teacher> getAllTeachers() throws SQLException, ClassNotFoundException {
        return sortTeachersByName((List<Teacher>) teachers.findAll());
    }

    /**
     *          updates the course information and deletes the student from the enrolled students list of the
     *          course if the maximum number of credits for that student is exceeded after the modification
     *          also deletes the course form the students courses list if the before mentioned condition
     *          applies
     *
     * @param curs - the course to be updated
     * */
    public void updateCourse(Course curs) throws SQLException, ClassNotFoundException {
        Iterable<Student> allStuds = students.findAll();
        List<Student> toUpdate = new ArrayList<>();
        Course before = courses.findOne(curs.getId());

        for(Student stud : allStuds){
            if(stud.getEnrolledCourses().contains(before)){
                //if the student was enrolled to the course in the first place
                if(stud.getTotalCredits() - before.getCredits() + curs.getCredits() > 30){
                    //new course exceeds student max credits therefore must be removed
                    //delete student from courses enrolled students list

                    List<Student> newStudents = curs.getStudentsEnrolled();
                    newStudents.remove(stud);
                    curs.setStudentsEnrolled(newStudents);

                    //delete course from student list + decrease credits

                    List<Course> newCourses = stud.getEnrolledCourses();
                    newCourses.remove(before);
                    int newCredits = stud.getTotalCredits() - before.getCredits();

                    stud.setTotalCredits(newCredits);
                    stud.setEnrolledCourses(newCourses);
                    toUpdate.add(stud);

                    //remove the data from the enrolled table
                    removeEnrolled(curs, stud);
                }
                else{
                    //new course does not exceed max credits
                    //modify student credits
                    int newCredits = stud.getTotalCredits() - before.getCredits() + curs.getCredits();
                    stud.setTotalCredits(newCredits);
                    toUpdate.add(stud);
                }
                courses.update(curs);

            }
        }
        for(Student stud2: toUpdate){
            students.update(stud2);
        }
    }


    /**
     * removes the studentID, courseID pair from the enrolled table
     * @param curs
     * @param stud
     * @throws SQLException
     */
    public void removeEnrolled(Course curs, Student stud) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Hogwarts","postgres" , "admin");
        Statement st = con.createStatement();

        st.executeUpdate("DELETE FROM public.enrolled\n" +
                "\tWHERE " + "\"studentID\"=" + stud.getId() + " AND \"courseID\"=" + curs.getId());

        st.close();

    }


    /**
     * @param elProf - the teacher whose course will be deleted
     * @param curs   - the course of the teacher that must be deleted
     *               deletes the course from the repository
     *               also deletes the course form the teachers list of courses
     *
     * */
    public void teacherQuitsCourse(Teacher elProf, Course curs) throws SQLException {
        Teacher teachBefore = teachers.findOne(elProf.getId());     //find the teacher before
        List<Course> newStuff = teachBefore.getCourses();      //the list before the update
        if(newStuff.contains(curs)){
            newStuff.remove(curs);
            elProf.setCourses(newStuff);

            teachers.update(elProf);

            //must delete course form every student who was enrolled (update course)

            List<Student> enrolledStudents = curs.getStudentsEnrolled();
            for(Student stud : enrolledStudents){
                int cred = stud.getTotalCredits() - curs.getCredits();
                stud.setTotalCredits(cred);
                List<Course> studsCourses = stud.getEnrolledCourses();
                studsCourses.remove(curs);
            }

            courses.delete(curs.getId());
        }

    }

    //--  SORT methods  --//

    private Iterable<Student> sortStudentsByName(List<Student> stuff){
        stuff.sort(Comparator.comparing(Student::getFirstName));
        return stuff;
    }

    private Iterable<Teacher> sortTeachersByName(List<Teacher> stuff){
        stuff.sort(Comparator.comparing(Teacher::getFirstName));
        return stuff;
    }

    private Iterable<Course> sortCoursesByID(List<Course> stuff){
        stuff.sort(Comparator.comparing(Course::getId));
        return stuff;
    }

    public Course findCourseByID(long id) throws SQLException {
        return courses.findOne(id);
    }

    public Teacher findTeacherByID(long id) throws SQLException {
        return teachers.findOne(id);
    }

    public Student findStudentByID(long id) throws SQLException {
        return students.findOne(id);
    }

    public List<Course> coursesOverANumberOfCredits() throws SQLException, ClassNotFoundException {
        Iterable<Course> allCourses = courses.findAll();
        return StreamSupport.stream(allCourses.spliterator(), false)
                .filter(e-> e.getCredits() > 7)
                .sorted(Comparator.comparing(Course::getId))
                .collect(Collectors.toList());
    }

}
