package lab3.repository.course;

import lab3.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CoursesRepository implements CoursesRepo {

    protected List<Course> courses;

    public CoursesRepository() {
        courses = new ArrayList<>();
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
