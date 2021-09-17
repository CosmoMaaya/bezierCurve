import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.tools.Tool;
import java.io.*;

public class Main extends Application {

    Model model;
    CanvasView canvas;
    ToolbarView toolbar;

    Stage stage;

    @Override
    public void start(Stage stage) throws Exception{
        this.stage = stage;
        BorderPane root = new BorderPane();

        model = new Model();

        MenuBar menubar = new MyMenuBar();
        initMenubar(menubar);
        toolbar = new ToolbarView(model);
        canvas = new CanvasView(model);

        model.addView(canvas);
        model.addView(toolbar);

        root.setCenter(canvas);
        root.setTop(menubar);
        root.setLeft(toolbar);
        Scene scene = new Scene(root, 1200, 900);

        scene.setOnKeyReleased(model::handleKeyReleased);

        stage.setScene(scene);
        stage.setResizable(true);

        // Set resize limit
        stage.setMinHeight(480);
        stage.setMinWidth(640);
        stage.setMaxHeight(1440);
        stage.setMaxWidth(1920);
        stage.setTitle("Bezier Curve");
//        canvas.setMaxHeight(stage.getMaxHeight()-menubar.getHeight() + 1000);
//        canvas.setMaxWidth(stage.getMaxWidth()-toolbar.getWidth() + 1000);
//        System.out.format("sceneSize (%f, %f)", canvas.getMaxHeight(), canvas.getMaxWidth());
        stage.setOnCloseRequest(this::savePrompt);
        stage.show();
    }

    private void initMenubar(MenuBar menubar){
        Menu fileMenu = new javafx.scene.control.Menu("File");
        MenuItem fileNew = new javafx.scene.control.MenuItem("New");
        MenuItem fileLoad = new javafx.scene.control.MenuItem("Load");
        MenuItem fileSave = new javafx.scene.control.MenuItem("Save");
        MenuItem fileQuit = new javafx.scene.control.MenuItem("Quit");
        fileMenu.getItems().addAll(fileNew, fileLoad, fileSave, fileQuit);

        Menu helpMenu = new javafx.scene.control.Menu("Help");
        MenuItem helpAbout = new MenuItem("About");
        helpMenu.getItems().add(helpAbout);

        menubar.getMenus().addAll(fileMenu, helpMenu);

        fileNew.setOnAction(event -> newAction());
        fileLoad.setOnAction(event -> load());
        fileSave.setOnAction(event -> save());
        fileQuit.setOnAction(event -> quitAction());

        helpAbout.setOnAction(event -> help());
    }

    private void quitAction(){
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void newAction(){
        savePrompt(null);
        model.resetCanvas();
    }

    private void save() {
        model.saved = true;

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MAYA", "*.maya");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(stage);

        if(file == null) {
            return;
        }

        if (!file.getName().endsWith(".maya")){
            file = new File(file.getAbsolutePath().concat(".maya"));
            System.out.println(file);
        }


        try {
            ModelData modelData = new ModelData(model);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(modelData);
            objectOutputStream.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.showAndWait();
        }
    }

    private void load(){
        if (!model.saved){
            if (!savePrompt(null)){
                // user canceled;
                return;
            }
        }

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("MAYA(*.maya)", "*.maya");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(stage);

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            ModelData modelData = (ModelData) objectInputStream.readObject();
            objectInputStream.close();
            model.loadFromData(modelData);
        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Load Failed!");
            System.out.println("LOAD FAILED");
            alert.showAndWait();
        }

    }

    private boolean savePrompt(WindowEvent event){
        if (!model.saved){
            Alert alert = new Alert(Alert.AlertType.NONE,
                    "You have not saved you work. \n Do you want to save first?",
                    ButtonType.NO, ButtonType.YES, ButtonType.CANCEL);
            alert.getDialogPane().setMinHeight(100);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES){
                save();
                return true;
            } else if (alert.getResult() == ButtonType.CANCEL){
                if (event != null){
                    event.consume();
                }
                return false;
            }
        }
        return true;
    }

    private void help(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Yusu Zhao \nY555ZHAO");
        alert.setHeaderText("Bezier Curve");
        alert.getDialogPane().setMinHeight(100);
        alert.showAndWait();
    }
}
