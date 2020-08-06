package lab3.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lab3.controller.Controller;
import lab3.exceptions.AlreadyEnrolledException;
import lab3.exceptions.CreditsExceededException;
import lab3.exceptions.MaxStudentsForCourse;
import lab3.model.Course;
import lab3.model.Student;
import lab3.model.Teacher;
import lab3.repository.course.CoursesDBRepository;
import lab3.repository.student.StudentsDBRepository;
import lab3.repository.teacher.TeachersDBRepository;

import java.sql.SQLException;
import java.util.List;

public class ViewRemastered extends Application {

    private Controller ctrl;

    public ViewRemastered() throws SQLException, ClassNotFoundException {
        StudentsDBRepository students = new StudentsDBRepository();
        TeachersDBRepository teachers = new TeachersDBRepository();
        CoursesDBRepository courses = new CoursesDBRepository();

        ctrl = new Controller(students, teachers, courses);
    }

    private Student studentUser = null;
    private Teacher teacherUser = null;

    private Stage teacherStage = new Stage();
    private Stage studentStage = new Stage();

    @Override
    public void start(Stage primaryStage) {

        //  Create the two windows:
        //      for teacher and for student

        teacherStage.setTitle("Teacher Java FX Application");
        studentStage.setTitle("Student Java FX Application");

        loadLogInScene(studentStage);
        loadLogInScene(teacherStage);

        teacherStage.show();
        studentStage.show();
    }

    public void showMenu() {
        launch();
    }

    // -- scene load functions -- //

    //  Sets up a log in form consisting of a USERNAME field and an ID field
    //  On click, the SIGN IN button checks credentials and changes scene for
    //  the current user type
    private void loadLogInScene(Stage win) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label id = new Label("ID:");
        grid.add(id, 0, 2);


        TextField idBox = new TextField();
        grid.add(idBox, 1, 2);


        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(event -> {
            int userID = Integer.parseInt(idBox.getText());
            // Set sign in action for the student window
            if (win.getTitle().equals("Student Java FX Application")) {
                Student stud = null;
                try {
                    stud = ctrl.findStudentByID(userID);
                } catch (SQLException e) {
                    System.out.println("ERROR");
                }

                if (stud != null && stud.getStudentFullName().equals(userTextField.getText())) {
                    studentUser = stud;
                    loadStudentsScene(win);
                } else {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Incorrect User Name or ID");
                }
            }

            // Set sign in action for the teacher window
            if (win.getTitle().equals("Teacher Java FX Application")) {
                Teacher teach = null;
                try {
                    teach = ctrl.findTeacherByID(userID);
                } catch (SQLException e) {
                    System.out.println("ERROR");
                }

                if (teach != null && teach.getFullName().equals(userTextField.getText())) {
                    teacherUser = teach;
                    loadTeacherScene(win);
                } else {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Incorrect User Name or ID");
                }
            }

        });

        Scene scene = new Scene(grid, 900, 400);
        win.setScene(scene);
    }

    private void loadTeacherScene(Stage window) {
        StackPane root = new StackPane();
        ObservableList<Student> studentOList = FXCollections.observableArrayList();
        ListView<Student> listStudents = new ListView<>();

        Scene scene = new Scene(root, 900, 400);
        scene.getStylesheets().add("/css/studentMenu.css");

        // Holds the menu and the list View
        HBox fileRoot = new HBox();

        // menu with the navigation buttons
        VBox menu = new VBox();
        menu.setStyle("-fx-background-color: green;");

        //  holds the title of the list and the list of objects to be viewed
        VBox lista = new VBox();
        lista.setPrefWidth(350);
        lista.getChildren().addAll(listStudents);

        // Label showing the name of the current user
        String uName = "Current User: " + teacherUser.getFullName();
        Label userName = new Label(uName);
        userName.setPrefWidth(250);
        userName.getStyleClass().add("custom-menu-label");

        // Add the stuff before the course buttons
        Region labels_courseButtons = new Region();
        VBox.setVgrow(labels_courseButtons, Priority.ALWAYS);
        menu.getChildren().addAll(userName, labels_courseButtons);

        // Create a menu view button for every course the Teacher has
        List<Course> teacherCourses = teacherUser.getCourses();
        for(Course curs: teacherCourses){
            Button button = new Button("ID: " + curs.getId() + "- " + curs.getName());
            button.setPrefWidth(250);
            button.getStyleClass().add("custom-menu-button2");
            button.setOnAction(event -> {
                listStudents.getItems().clear();
                courseStudents_ListView(studentOList, listStudents, curs);
            });

            menu.getChildren().add(button);
        }


        // Holds the tray to get course id, and button to delete
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label courseIdLabel = new Label("Course ID:");
        grid.add(courseIdLabel, 0, 1);

        TextField courseIdTextField = new TextField();
        grid.add(courseIdTextField, 1, 1);

        Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        // Button that DELETES the course with the given ID
        Button deleteBtn = new Button("Delete");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(deleteBtn);
        grid.add(hbBtn, 1, 4);
        deleteBtn.setOnAction(event -> {
            // check if teacher has the course
            List<Course> coursesOfTeacher = teacherUser.getCourses();
            int givenCourseID = Integer.parseInt(courseIdTextField.getText());
            boolean ok = false;
            for(Course crs: coursesOfTeacher){
                if (crs.getId() == givenCourseID) {
                    ok = true;
                    break;
                }
            }

            if(!ok){
                // invalid course id given
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("You don't teach that!");
            }
            else{
                try {
                    ctrl.teacherQuitsCourse(teacherUser, ctrl.findCourseByID(givenCourseID));
                    loadTeacherScene(teacherStage);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });

        // LOGOUT button takes user back to the login scene
        Button backBtn = new Button("Log out");
        backBtn.setPrefWidth(250);
        backBtn.getStyleClass().add("custom-menu-button2");
        backBtn.setOnAction(event -> loadLogInScene(window));

        // add the stuff after the course buttons
        Region courseButtons_logoutBtn = new Region();
        VBox.setVgrow(courseButtons_logoutBtn, Priority.ALWAYS);
        menu.getChildren().addAll(courseButtons_logoutBtn, backBtn);

        // add menu and list to the HBox
        HBox.setHgrow(lista, Priority.ALWAYS);
        lista.getChildren().add(grid);
        fileRoot.getChildren().addAll(menu, lista);

        // add HBox to the StackPane
        root.getChildren().add(fileRoot);

        window.setScene(scene);

    }

    private void loadStudentsScene(Stage window) {

        StackPane root = new StackPane();
        ObservableList<Course> coursesOList = FXCollections.observableArrayList();
        ListView<Course> listCourses = new ListView<>();

        Scene scene = new Scene(root, 900, 400);
        scene.getStylesheets().add("/css/studentMenu.css");

        // Holds the menu and the list View
        HBox fileRoot = new HBox();

        // menu with the navigation buttons
        VBox menu = new VBox();
        menu.setStyle("-fx-background-color: blue;");

        //  holds the title of the list and the list of objects to be viewed
        VBox lista = new VBox();
        lista.setPrefWidth(350);
        lista.getChildren().addAll(listCourses);

        //  Label showing the number of credits
        String credsNr = "Credits: " + studentUser.getTotalCredits();
        Label credInfo = new Label(credsNr);
        credInfo.setPrefWidth(250);
        credInfo.getStyleClass().add("custom-menu-label");

        // Label showing the name of the current user
        String uName = "Current User: " + studentUser.getStudentFullName();
        Label userName = new Label(uName);
        userName.setPrefWidth(250);
        userName.getStyleClass().add("custom-menu-label");

        // Button to show all the courses available
        Button allCoursesBtn = new Button("All Courses");
        allCoursesBtn.setPrefWidth(250);
        allCoursesBtn.getStyleClass().add("custom-menu-button");
        allCoursesBtn.setOnAction(event -> {
            try {
                listCourses.getItems().clear();
                allCourses_ListView(coursesOList, listCourses);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        // Button to show courses the that student is enrolled to
        Button myCoursesBtn = new Button("My Courses");
        myCoursesBtn.setPrefWidth(250);
        myCoursesBtn.getStyleClass().add("custom-menu-button");
        myCoursesBtn.setOnAction(event -> {
            listCourses.getItems().clear();
            userCourses_ListView(coursesOList, listCourses);
        });

        // Button to show available courses
        Button availableCoursesBtn = new Button("Available Courses");
        availableCoursesBtn.setPrefWidth(250);
        availableCoursesBtn.getStyleClass().add("custom-menu-button");
        availableCoursesBtn.setOnAction(event -> {
            listCourses.getItems().clear();
            try {
                availableCourses_ListView(coursesOList, listCourses);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        // Holds the tray to get course id, and button to enroll
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label courseIdLabel = new Label("Course ID:");
        grid.add(courseIdLabel, 0, 1);

        TextField courseIdTextField = new TextField();
        grid.add(courseIdTextField, 1, 1);

        Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        // Button that ENROLLS the student with the given course id
        Button enrollBtn = new Button("Enroll me");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(enrollBtn);
        grid.add(hbBtn, 1, 4);
        enrollBtn.setOnAction(event -> {
            int givenCourseID = Integer.parseInt(courseIdTextField.getText());
            try{
                ctrl.register(ctrl.findCourseByID(givenCourseID), studentUser);
                loadTeacherScene(teacherStage);
                loadStudentsScene(studentStage);

            } catch (AlreadyEnrolledException e){
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Already enrolled");
            } catch (CreditsExceededException e){
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Credits Exceeded!");
            } catch (MaxStudentsForCourse e){
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Course is full");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Logout button takes user back to the login scene
        Button backBtn = new Button("Log out");
        backBtn.setPrefWidth(250);
        backBtn.getStyleClass().add("custom-menu-button");
        backBtn.setOnAction(event -> loadLogInScene(window));


        // add buttons to the menu
        Region labels_courseButtons = new Region();
        VBox.setVgrow(labels_courseButtons, Priority.ALWAYS);
        Region courseButtons_logoutBtn = new Region();
        VBox.setVgrow(courseButtons_logoutBtn, Priority.ALWAYS);
        menu.getChildren().addAll(userName, credInfo, labels_courseButtons, allCoursesBtn, availableCoursesBtn, myCoursesBtn, courseButtons_logoutBtn, backBtn);

        // add menu and list to the HBox
        HBox.setHgrow(lista, Priority.ALWAYS);
        lista.getChildren().add(grid);
        fileRoot.getChildren().addAll(menu, lista);

        // add HBox to the StackPane
        root.getChildren().add(fileRoot);

        window.setScene(scene);
    }


    // -- display functions -- //

    // Add all courses to the ObservableList
    private void allCourses_ListView(ObservableList<Course> destinationList, ListView<Course> viewableList) throws SQLException, ClassNotFoundException {
        List<Course> allCourses = (List<Course>) ctrl.getAllCourses();
        destinationList.removeAll();
        destinationList.addAll(allCourses);
        viewableList.setItems(destinationList);
    }

    // Add all the current students enrolled courses to the ObservableList
    private void userCourses_ListView(ObservableList<Course> destinationList, ListView<Course> viewableList){
        List<Course> userCourses = studentUser.getEnrolledCourses();
        destinationList.removeAll();
        destinationList.addAll(userCourses);
        viewableList.setItems(destinationList);
    }

    // Add the students enrolled to the given course to the ObsList
    private void courseStudents_ListView(ObservableList<Student> destinationList, ListView<Student> viewableList, Course curs){
        List<Student> courseStudents = curs.getStudentsEnrolled();
        destinationList.removeAll();
        destinationList.addAll(courseStudents);
        viewableList.setItems(destinationList);
    }

    // Add all courses not having max students count
    private void availableCourses_ListView(ObservableList<Course> destinationList, ListView<Course> viewableList) throws SQLException, ClassNotFoundException {
        List<Course> availableCourses = (List<Course>) ctrl.retrieveCoursesWithFreePlaces();
        destinationList.removeAll();
        destinationList.addAll(availableCourses);
        viewableList.setItems(destinationList);
    }

}
