package lab3.repository.course;

import lab3.model.Course;
import lab3.model.Teacher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CoursesFileRepository implements CoursesRepo{

    List<Course> courses;

    public CoursesFileRepository(String filename) throws FileNotFoundException {
        readFromFile(filename);
    }

    public void readFromFile(String filename) throws FileNotFoundException{
        /*
        Reads courses from file with a placeholder Teacher object having the ID of the
        actual teacher object. This will be changed in the controller when the teacher
        objects are added to the TeacherRepository
        */

        courses = new ArrayList<>();
        File f = new File(filename);
        Scanner scanner = new Scanner(f);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] bananasplit = line.split(", ");
            Course curs = new Course(Long.parseLong(bananasplit[0]), bananasplit[1], new Teacher("", "", Long.parseLong(bananasplit[2])), Integer.parseInt(bananasplit[3]), Integer.parseInt(bananasplit[4]));
            courses.add(curs);
        }

    }

    public void writeToFile(String filename) throws IOException {
        File fout = new File(filename);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(Course curs: courses){
            bw.write(curs.getId() + ", " + curs.getName() + ", " + curs.getTeacher().getId() + ", " + curs.getMaxEnrollment() + ", " + curs.getCredits());
            bw.newLine();
        }

        bw.close();
    }

    @Override
    public Course findOne(Long id) {
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
    public Course save(Course entity) {
        if(entity == null){
            return null;
        }
        else{
            if(findOne(entity.getId()) == null){         //if an entity with the same id was found in the list
                courses.add(entity);
                return null;
            }
        }
        return entity;           //spit it back >:(
    }

    @Override
    public Course delete(Long id) {
        if(id == null){
            return null;
        }
        else{
            for(Course curs: courses){
                if(curs.getId() == id){
                    courses.remove(curs);          //removes the obj if found
                    return curs;
                }
            }
            return null;        //none found with the given id
        }
    }

    @Override
    public Course update(Course entity) {
        if(entity == null){
            return null;
        }
        else{
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
