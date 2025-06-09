import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class RentalBusinessMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rental Business");
        RentalBusinessModel model = new RentalBusinessModel();
        RentalBusinessController controller = new RentalBusinessController(model);
        RentalBusinessView view = new RentalBusinessView(controller, model, primaryStage);

        Scene scene = new Scene(view.asParent(), 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}