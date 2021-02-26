package lab;

import java.io.File;
import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author hkim
 */
public class PathTest extends Application {
    
    private void pln(Object line) { System.out.println(line); }
    @Override
    public void start(Stage primaryStage) {
        //jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/me/myimageapp/newpackage/image.png"))); // NOI18N
        String imgFile = "add_s.png";   // same directory     
//        String imgFile = "/resources/images/add_s.png";        
//        String imgFile = "../resources/images/add_s.png";
//        l.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResource("toms/resources/add_s.png").toString())));

//        Image logo = new Image(getClass().getClassLoader().getResource(imgFile).toString());
//        Image logo = new Image(getClass().getResource(imgFile).toString());
//        Image logo = new Image(getClass().getClassLoader().getResource("src/main/resources/images/add_s.png").toString());

//        ImageView imgView = new ImageView(getClass().getResource(imgFile).toExternalForm());
//        pln(imgView);
//        pln("Hello: "+logo);
        /** */
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("images/add_s.png");
        pln(url);
        Image logo = new Image(url.toString());
        primaryStage.getIcons().add(logo);


        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
