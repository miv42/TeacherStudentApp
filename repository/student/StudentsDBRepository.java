package lab3.repository.student;

import lab3.model.Course;
import lab3.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentsDBRepository implements StudentsRepo {

    List<Student> students;
    Connection con;

    public StudentsDBRepository() throws SQLException, ClassNotFoundException {
        con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Hogwarts","postgres" , "admin");
        readFromDB();
    }

    public void readFromDB() throws ClassNotFoundException, SQLException {

        students = new ArrayList<>();

        Class.forName("org.postgresql.Driver");

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM public.students");

        while(rs.next()){
            long id = rs.getLong("ID");
            String fname = rs.getString("FirstName");
            String lname = rs.getString("LastName");

            Student stud = new Student(id, fname, lname);

            //add a list of placeholder courses
            List<Course> enrolledCourses = new ArrayList<>();

            Statement st2 = con.createStatement();

            ResultSet rs2 = st2.executeQuery("SELECT * FROM public.enrolled\n" +
                    "\tWHERE \"studentID\" = " + id);

            while(rs2.next()){
                long cursID = rs2.getLong("courseID");
                Course cursEnrolled = new Course(cursID, "", null, 0,0);
                enrolledCourses.add(cursEnrolled);
            }

            stud.setEnrolledCourses(enrolledCourses);

            students.add(stud);

        }
        st.close();
    }

    @Override
    public Student findOne(Long id) throws SQLException {

        if(id == null){
            return null;
        }
        else{
            for(Student stud : students){
                if(stud.getStudentId() == id){
                    return stud;
                }
            }
            return null;    //wasn't found
        }
    }

    @Override
    public Iterable<Student> findAll(){
        return students;
    }

    @Override
    public Student save(Student entity) throws SQLException {
        if(entity != null){
            //there is no such item in the db => INSERT STATEMENT
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            st.executeUpdate("INSERT INTO public.students(\"ID\", \"FirstName\", \"LastName\") VALUES (" + entity.getId() + ", '" + entity.getFirstName() + "', '" + entity.getLastName() + "')");

            students.add(entity);

            return null;
        }
        return entity;  //spit it back >:(
    }

    @Override
    public Student delete(Long id) throws SQLException {
        //check with find one
        if(id != null){
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate("DELETE FROM public.students WHERE \"ID\" = " + id);

            for(Student stud: students){
                if(stud.getStudentId() == id){
                    students.remove(stud);          //removes the obj if found
                    return stud;
                }
            }
        }
        return null;
    }

    @Override
    public Student update(Student entity) throws SQLException {
        //update STATEMENT
        if(entity == null){
            return null;
        }
        else{

            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate("UPDATE public.students " +
                    "SET \"ID\" = " + entity.getId() + ", \"FirstName\" = '" + entity.getFirstName() + "', \"LastName\" = '" + entity.getLastName() +
                    "' WHERE \"ID\" = " + entity.getId());

            for(Student stud: students){
                if(stud.getStudentId() == entity.getStudentId()){
                    students.remove(stud);
                    students.add(entity);
                    return null;
                }
            }
        }
        return entity;
    }
}
