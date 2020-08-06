package lab3;

import lab3.view.View;
import lab3.view.ViewRemastered;

import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Main class where program starts.
 */
public class StartApp {

    /**
     * Start point of the application
     * @param args command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, SQLException, ClassNotFoundException {
        //System.out.println("Start point");

        //Console app
        View ui = new View();
        ui.start();

        //JavaFX App
        //ViewRemastered ui = new ViewRemastered();
        //ui.showMenu();

    }
}
