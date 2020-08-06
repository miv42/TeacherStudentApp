package lab3.repository.student;

import lab3.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentsRepository implements StudentsRepo {

    protected List<Student> students;

    public StudentsRepository() {
        students = new ArrayList<>();
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
