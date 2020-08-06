package lab3.repository.course;

import lab3.model.Course;
import lab3.model.Student;
import lab3.model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursesDBRepository implements CoursesRepo {

    List<Course> courses;
    Connection con;
    
    public CoursesDBRepository() throws SQLException, ClassNotFoundException {
        con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Hogwarts","postgres" , "admin");
        readFromDB();
    }

    public void readFromDB() throws ClassNotFoundException, SQLException {

        courses = new ArrayList<>();
        
        Class.forName("org.postgresql.Driver");
        
        Statement st = con.createStatement();
        
        ResultSet rs = st.executeQuery("SELECT * FROM public.courses");

        while(rs.next()){
            long id = rs.getLong("ID");
            String cname = rs.getString("Name");
            int teach_id = rs.getInt("teacher_id");
            int maxEnr = rs.getInt("maxEnrollement");
            int creds = rs.getInt("credits");

            Teacher teacherBoi = new Teacher("","",teach_id);
            
            Course curs = new Course(id, cname, teacherBoi, maxEnr, creds);

            courses.add(curs);

        }
        st.close();

    }

    @Override
    public Course findOne(Long id) throws SQLException {
        if(id == null){
            return null;
        }
        else{
            for(Course curs : courses){
                if(curs.getId() == id){
                    return curs;
                }
            }
            return null;    //wasn't found
        }
    }

    @Override
    public Iterable<Course> findAll() {
        return courses;
    }

    @Override
    public Course save(Course entity) throws SQLException {
        if(entity != null){
            //there is no such item in the db => INSERT STATEMENT
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            st.executeUpdate("INSERT INTO public.courses(\n" +
                    "\t\"ID\", \"Name\", teacher_id, \"maxEnrollement\", credits)\n" +
                    "\tVALUES (" + entity.getId() + ", '" + entity.getName() + "', " + entity.getTeacher().getId() + ", " +
                    entity.getMaxEnrollment() + ", " + entity.getCredits() + ")");

            courses.add(entity);

            return null;
        }
        return entity;  //spit it back >:(
    }

    @Override
    public Course delete(Long id) throws SQLException {
        //check with find one
        if(id != null){
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate("DELETE FROM public.enrolled WHERE \"courseID\" = " + id);
            st.executeUpdate("DELETE FROM public.courses WHERE \"ID\" = " + id);

            for(Course curs: courses){
                if(curs.getId() == id){
                    courses.remove(curs);          //removes the obj if found
                    return curs;
                }
            }
        }
        return null;
    }

    @Override
    public Course update(Course entity) throws SQLException {
        //update STATEMENT
        if(entity == null){
            return null;
        }
        else{
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            st.executeUpdate("UPDATE public.courses\n" +
                    "\tSET \"ID\"= " + entity.getId() + " , \"Name\"='" + entity.getName() + "', teacher_id=" + entity.getTeacher().getId() + ", \"maxEnrollement\"=" + entity.getMaxEnrollment() + ", credits=" + entity.getCredits()+ "\n" +
                    "\tWHERE \"ID\" = " + entity.getId());

            for(Course curs: courses){
                if(curs.getId() == entity.getId()){
                    courses.remove(curs);
                    courses.add(entity);
                    return null;
                }
            }
        }
        return entity;
    }
}
