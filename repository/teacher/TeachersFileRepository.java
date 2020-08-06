package lab3.repository.teacher;

import lab3.model.Teacher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TeachersFileRepository implements TeachersRepo{

    List<Teacher> teachers;

    public TeachersFileRepository(String filename) throws FileNotFoundException {
        readFromFile(filename);
    }

    public void readFromFile(String filename) throws FileNotFoundException{
        //Reads teachers from the file. Courses list is left empty to be completed in the controller

        teachers = new ArrayList<>();
        //FileInputStream read = new FileInputStream(filename);
        File f = new File(filename);
        Scanner scanner = new Scanner(f);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            //System.out.println(line);
            String[] bananasplit = line.split(", ");
            String vName = bananasplit[0];
            String lName = bananasplit[1];
            long id = Long.parseLong(bananasplit[2]);

            Teacher teach = new Teacher(vName, lName, id);
            teachers.add(teach);
        }

    }

    public void writeToFile(String filename) throws IOException {
        File fout = new File(filename);
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(Teacher teach: teachers){
            String id = "" + teach.getId();

            bw.write(teach.getFirstName() + ", " + teach.getLastName() + ", " + id);
            bw.newLine();
        }

        bw.close();
    }

    @Override
    public Teacher findOne(Long id) {
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
    public Teacher save(Teacher entity) {
        if(entity == null){
            return null;
        }
        else{
            if(findOne(entity.getId()) == null){         //if an entity with the same id was found in the list
                teachers.add(entity);
                return null;
            }
        }
        return entity;           //spit it back >:(
    }

    @Override
    public Teacher delete(Long id) {
        if(id == null){
            return null;
        }
        else{
            for(Teacher teach: teachers){
                if(teach.getId() == id){
                    teachers.remove(teach);          //removes the obj if found
                    return teach;
                }
            }
            return null;        //none found with the given id
        }
    }

    @Override
    public Teacher update(Teacher entity) throws NullPointerException{
        if(entity == null){
            return entity; //throw exception at UI lvl
        }
        else{
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
