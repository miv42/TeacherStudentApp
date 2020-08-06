package lab3.repository.teacher;

import lab3.model.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeachersRepository implements TeachersRepo{

    List<Teacher> teachers;

    public TeachersRepository() {
        teachers = new ArrayList<>();
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
    public Teacher update(Teacher entity) {
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
