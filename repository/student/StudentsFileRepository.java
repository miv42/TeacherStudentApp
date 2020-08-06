package lab3.repository.student;

import lab3.model.Course;
import lab3.model.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentsFileRepository implements StudentsRepo {

    List<Student> students;

    public StudentsFileRepository(String filename) throws FileNotFoundException {
        readFromFile(filename);
    }

    public void readFromFile(String filename) throws FileNotFoundException{
        //Reads students from the file.
        students = new ArrayList<>();
        FileInputStream read = new FileInputStream(filename);
        Scanner scanner = new Scanner(read);
        while (scanner.hasNextLine()) {
            //read student info
            String line = scanner.nextLine();
            String[] bananasplit = line.split(", ");
            Student stud = new Student(Long.parseLong(bananasplit[0]), bananasplit[1], bananasplit[2]);

            //add placeholder Course with the ID of the actual course. To be completed in the controller
            List<Course> enrolleds = new ArrayList<>();
            for(int i = 3; i < bananasplit.length; i++){
                Course cursEnrolled = new Course(Long.parseLong(bananasplit[i]), "", null, 0,0);
                enrolleds.add(cursEnrolled);
            }
            stud.setEnrolledCourses(enrolleds);

            students.add(stud);
        }
    }

    public void writeToFile(String filename) throws IOException{
        File fout = new File(filename);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(Student stud: students){
            bw.write(stud.getId() + ", " + stud.getFirstName() + ", " + stud.getLastName());
            List<Course> curses = stud.getEnrolledCourses();
            if(!curses.isEmpty()){
                for(Course curs: curses){
                    bw.write(", " + curs.getId());
                }
            }
            bw.newLine();
        }
        bw.close();
    }

    @Override
    public Student findOne(Long id) {
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
    public Iterable<Student> findAll() {
        return students;
    }

    @Override
    public Student save(Student entity) {
        if(entity == null){
            return null;
        }
        else{
            if(findOne(entity.getStudentId()) == null){         //if an entity with the same id was found in the list
                students.add(entity);
            }
        }
        return entity;           //spit it back >:(
    }

    @Override
    public Student delete(Long id) {
        if(id == null){
            return null;
        }
        else{
            for(Student stud: students){
                if(stud.getStudentId() == id){
                    students.remove(stud);          //removes the obj if found
                    return stud;
                }
            }
            return null;        //none found with the given id
        }
    }

    @Override
    public Student update(Student entity) {
        if(entity == null){
            return null;
        }
        else{
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
