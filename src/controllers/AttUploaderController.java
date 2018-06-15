
package controllers;

import utils.DateHelper;
import utils.AlertHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.GlobalHelper;

public class AttUploaderController implements Initializable {
    
    private static Stage currentStage = null;
    private String absolute_file_path = "";
    
    @FXML
    private DatePicker date_from;
    @FXML
    private DatePicker date_to;
    @FXML
    private StackPane loading_panel;
    @FXML
    private TextField file_name;
    
    @FXML
    private void browseFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            
            absolute_file_path = file.getAbsolutePath();
            String[] file_name_array = file.getName().split("\\.");
            String file_extension = file_name_array[file_name_array.length - 1];

            if(file_extension.toLowerCase().equals("txt")) {
                file_name.setText(file.getName());
                date_from.disableProperty().set(false);
                date_to.disableProperty().set(false);
            } else {
                AlertHelper.show(AlertType.ERROR, "Invalid", "", "Invalid file.");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    private void previewData(ActionEvent event) throws InterruptedException {
        new Thread(() -> {
            Platform.runLater(() -> {
                loading_panel.toFront();
            });
            
            if( !(date_from.getValue() == null || date_to.getValue() == null) ) {
                try {
                    File file = new File(absolute_file_path);
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    StringBuilder sb = new StringBuilder();
                    String line;

                    // filter the file by date range
                    boolean validFile = true;
                    boolean firstIteration = true;
                    while((line = bufferedReader.readLine()) != null ) {
                        if(!firstIteration) {
                            String date = line.split("\t")[6];

                            if(DateHelper.isWithinRange(date, date_from.getValue(), date_to.getValue())) {
                                sb.append(line);
                                sb.append("\n");
                            }
                        } else {
                            if( !(line.contains("Mchn") && line.contains("IOMd") && line.contains("EnNo")) ) {
                                validFile = false;
                                break;
                            }
                            firstIteration = false;
                        }
                    }
                    
                    if(validFile) {
                        Platform.runLater(() -> {
                            try {
                                // write the filtered data to a text file
                                String[] records = sb.toString().split("\n");
                                PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + "/src/temp/to_upload.txt", "UTF-8");
                                for (String record : records) {
                                    writer.println(record);
                                }
                                writer.close();

                                // open view_records window
                                Parent root = FXMLLoader.load(getClass().getResource("/resources/view_records.fxml"));
                                Stage stage = new Stage();
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.setScene(new Scene(root));
                                stage.show();

                                // hide current window
                                ((Node)(event.getSource())).getScene().getWindow().hide();
                            } catch (IOException e) {
                                AlertHelper.show(AlertType.ERROR, "Error", "", e.getMessage());
                            }

                        });
                    } else {
                        AlertHelper.show(AlertType.ERROR, "Error", "", "Invalid file.");
                    }
                } catch (IOException | ParseException e) {
                    AlertHelper.show(AlertType.ERROR, "Error", "", e.getMessage());
                } finally {
                    // set date_from and date_to to global
                    GlobalHelper.date_period_from = DateHelper.dateFormatter(date_from.getValue().toString(), "yyyy-MM-dd", "MM/dd/yyyy");
                    GlobalHelper.date_period_to = DateHelper.dateFormatter(date_to.getValue().toString(), "yyyy-MM-dd", "MM/dd/yyyy");
                    System.out.println(GlobalHelper.date_period_from);
                }
            } else {
                AlertHelper.show(AlertType.ERROR, "Invalid", "", "Invalid date.");
            }
            
            Platform.runLater(()-> {
                loading_panel.toBack();
            });
            
        }).start();
    }
    
    @FXML
    private void closeFrame(ActionEvent event) {
        Optional<ButtonType> result = AlertHelper.showAndWait(AlertType.CONFIRMATION, "Exit", "", "Are you sure?");
        
        if(result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }
    
    @FXML
    private void minimizeFrame(ActionEvent event) {
        currentStage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //
    }
    
    public static void setStage(Stage stage) {
        currentStage = stage;
    }
}
