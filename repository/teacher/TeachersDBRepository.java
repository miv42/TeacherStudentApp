package lab3.repository.teacher;

import lab3.model.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeachersDBRepository implements TeachersRepo {

    List<Teacher> teachers;
    Connection con;

    public TeachersDBRepository() throws SQLException, ClassNotFoundException {
        con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Hogwarts","postgres" , "admin");
        readFromDB();
    }

    /**
     * reads the data from the teachers table in the database
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void readFromDB() throws ClassNotFoundException, SQLException {

        teachers = new ArrayList<>();

        Class.forName("org.postgresql.Driver");

        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM public.teachers");

        while(rs.next()){
            long id = rs.getLong("ID");
            String fname = rs.getString("FirstName");
            String lname = rs.getString("LastName");

            Teacher teach = new Teacher(fname, lname, id);

            teachers.add(teach);
        }
        st.close();
    }

    @Override
    public Teacher findOne(Long id) throws SQLException {
        if(id == null){
            return null;
        }
        else{
            for(Teacher teach : teachers){
                if(teach.getId() == id){
                    return teach;
                }
            }
            return null;    //wasn't found
        }
    }

    @Override
    public Iterable<Teacher> findAll() {
        return teachers;
    }

    @Override
    public Teacher save(Teacher entity) throws SQLException {
        if(entity != null){
            //there is no such item in the db => INSERT STATEMENT
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            st.executeUpdate("INSERT INTO public.teachers(\"ID\", \"FirstName\", \"LastName\") VALUES (" + entity.getId() + ", '" + entity.getFirstName() + "', '" + entity.getLastName() + "')");

            teachers.add(entity);

            return null;
        }
        return entity;  //spit it back >:(
    }

    @Override
    public Teacher delete(Long id) throws SQLException {
        //check with find one
        if(id != null){
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.executeUpdate("DELETE FROM public.teachers WHERE \"ID\" = " + id);

            for(Teacher teach: teachers){
                if(teach.getId() == id){
                    teachers.remove(teach);          //removes the obj if found
                    return teach;
                }
            }
        }
        return null;
    }

    @Override
    public Teacher update(Teacher entity) throws SQLException {
        //update STATEMENT
        if(entity == null){
            return null;
        }
        else{
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            st.executeUpdate("UPDATE public.teachers " +
                    "SET \"ID\" = " + entity.getId() + ", \"FirstName\" = '" + entity.getFirstName() + "', \"LastName\" = '" + entity.getLastName() +
                    "' WHERE \"ID\" = " + entity.getId());

            for(Teacher teach: teachers){
                if(teach.getId() == entity.getId()){
                    teachers.remove(teach);
                    teachers.add(entity);
                    return null;
                }
            }
        }
        return entity;
    }
}
