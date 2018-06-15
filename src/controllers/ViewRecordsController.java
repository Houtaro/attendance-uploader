
package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Attendance;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import utils.AlertHelper;
import utils.DateHelper;
import utils.GlobalHelper;

public class ViewRecordsController implements Initializable {

    private ObservableList<Attendance> attendance_list = FXCollections.observableArrayList();
    private ArrayList<Attendance> u_attendance_list = new ArrayList<>();
    private String[] data = {};
    
    @FXML
    private TableView<Attendance> records_tbl;
    @FXML
    private TableColumn<Attendance, String> nameColumn;
    @FXML
    private TableColumn<Attendance, String> timeInColumn;
    @FXML
    private TableColumn<Attendance, String> timeOutColumn;
    @FXML
    private Label date_period;
    @FXML
    private StackPane view_loading_panel;
    @FXML
    private Label net_status;
    @FXML
    private Button btnUpload;
    
    @FXML
    private void uploadData(ActionEvent event) throws IOException {
        if(data.length > 0) {
            Optional result = AlertHelper.showAndWait(Alert.AlertType.CONFIRMATION, "Upload", "", "Are you sure? This cannot be undone.");
            if(result.get() == ButtonType.OK) {
                new Thread(() -> {
                    Platform.runLater(() -> {
                        view_loading_panel.toFront();
                    });

                    for(String datum : data) {
                        String employee_id = GlobalHelper.employeeIdFormatter(datum.split("\t")[2]);
                        String punch_stamp = DateHelper.dateFormatter(datum.split("\t")[6], "yyyy/MM/dd HH:mm:ss", "yyyyMMddHHmmss");//format2.format(d);
                        String reference_id = employee_id + punch_stamp;
                        String punch_type = datum.split("\\|")[1]; 

                        Attendance att = new Attendance();

                        att.setEmployee_id(employee_id);
                        att.setPunch_stamp(punch_stamp);
                        att.setPunch_type(punch_type);
                        att.setReference_id(reference_id);
                        att.setDevice_type("3");
                        att.setDevice_id("20110510825");
                        att.setRemarks("");

                        u_attendance_list.add(att);
                    }

                        try {
                            CloseableHttpClient httpclient = HttpClients.createDefault(); 
                            HttpPost httpPost = new HttpPost("https://staging-hris.teratomo.com/timekeeping/punch");

                            List<BasicNameValuePair> params = new ArrayList<>();

                            for(Attendance attendance : u_attendance_list) {
                                params.add(new BasicNameValuePair("reference_id" , attendance.getReference_id()));
                                params.add(new BasicNameValuePair("employee_id"  , attendance.getEmployee_id()));
                                params.add(new BasicNameValuePair("punch_type"   , attendance.getPunch_type()));
                                params.add(new BasicNameValuePair("punch_stamp"  , attendance.getPunch_stamp()));
                                params.add(new BasicNameValuePair("device_type"  , attendance.getDevice_type()));
                                params.add(new BasicNameValuePair("device_id"    , attendance.getDevice_id()));
                                params.add(new BasicNameValuePair("remarks"      , attendance.getRemarks()));

                                httpPost.setEntity(new UrlEncodedFormEntity(params));

                                CloseableHttpResponse response = httpclient.execute(httpPost);
                                System.out.println("Status code: " + response.getStatusLine().getStatusCode());
                            }

                            AlertHelper.show(Alert.AlertType.INFORMATION, "Success", "", "Data uploaded successfully.");
                            httpclient.close();

                        } catch(IOException e) {
                            AlertHelper.show(Alert.AlertType.ERROR, "Error", "", e.getMessage());
                        }

                    Platform.runLater(() -> {
                        view_loading_panel.toBack();
                    });
                }).start();
            }
        } else {
            AlertHelper.show(Alert.AlertType.ERROR, "Error", "", "Data is empty.");
        }
    }
    
    @FXML
    private void cancelUpload(ActionEvent event) {
        try {
            // open another window
            Parent root = FXMLLoader.load(getClass().getResource("/resources/attendance_uploader.fxml"));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.show();

            // hide current window
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            AlertHelper.show(Alert.AlertType.ERROR, "Error", "", e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // get records from to_upload.txt
        ArrayList<String> allDates = new ArrayList();
        String text_file_path = System.getProperty("user.dir") + "/src/temp/to_upload.txt";
        try {
            File file = new File(text_file_path);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();
            String line, line2;
            
            while ((line2 = bufferedReader.readLine()) != null) {
                if(!(line2.equals(""))) {
                    String date = DateHelper.dateFormatter(line2.split("\t")[6], "yyyy/MM/dd HH:mm:ss", "MM/dd/yyyy");
                    if(!(allDates.contains(date))) {
                        allDates.add(date);
                    }
                }
            }
            
            bufferedReader.close();
            fileReader.close();
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            
            for(String date : allDates) {
                ArrayList<String> allEmployeeIds = new ArrayList();
                ArrayList<String> employeeIdHolder = new ArrayList();
                ArrayList<String> tempLineHolder = new ArrayList();
                while ((line = bufferedReader.readLine()) != null) {
                    String current_date = DateHelper.dateFormatter(line.split("\t")[6], "yyyy/MM/dd HH:mm:ss", "MM/dd/yyyy");
                    if(date.equals(current_date)) {
                        tempLineHolder.add(line);
                    }
                }
                
                bufferedReader.close();
                fileReader.close();
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                
                for(String s : tempLineHolder) {
                    allEmployeeIds.add(s.split("\t")[2]);
                }
                
                for(String singleLine : tempLineHolder) {
                    String current_employee_id = singleLine.split("\t")[2];
                    if( !(employeeIdHolder.contains(current_employee_id)) ) {
                        employeeIdHolder.add(current_employee_id);
                        sb.append(singleLine).append("|1");
                        sb.append("\n");
                    } else {
                        int total_occurrence = 0;
                        int occurrence = 0;

                        for(String employeeId : allEmployeeIds) {
                            if(employeeId.equals(current_employee_id)) 
                                total_occurrence++;
                        }

                        employeeIdHolder.add(current_employee_id);

                        for(String employeeId : employeeIdHolder) {
                            if(current_employee_id.equals(employeeId)) 
                                occurrence++;
                        }

                        if(total_occurrence == occurrence) {
                            employeeIdHolder.add(current_employee_id);
                            sb.append(singleLine).append("|2");
                            sb.append("\n");
                        }
                    }
                }
            }
            
            data = sb.toString().split("\n");
            
            OUTERLOOP: for (String data1 : data) {
                if(!(data1.equals(""))) {
                    String employee_id = GlobalHelper.employeeIdFormatter(data1.split("\t")[2]);
                    String punch_stamp = DateHelper.dateFormatter(data1.split("\t")[6], "yyyy/MM/dd HH:mm:ss", "yyyyMMddHHmmss");//format2.format(d);
                    String reference_id = employee_id + punch_stamp;
                    String punch_type = data1.split("\\|")[1]; // 1 if time-in; 2 if time-out

                    Attendance att = new Attendance();
                    if(punch_type.equals("2")) {
                        for(Attendance attendance : attendance_list) {

                            String date = DateHelper.dateFormatter(punch_stamp, "yyyyMMddHHmmss", "MM/dd/yyyy");
                            String att_time_in = DateHelper.dateFormatter(attendance.getTime_in(), "MM/dd/yyyy HH:mm a", "MM/dd/yyyy");

                            if(employee_id.equals(attendance.getEmployee_id()) && date.equals(att_time_in)) {
                                attendance.setTime_out(DateHelper.dateFormatter(punch_stamp, "yyyyMMddHHmmss", "MM/dd/yyyy HH:mm a"));
                                continue OUTERLOOP;
                            }
                        }
                    } 

                    att.setTime_in(DateHelper.dateFormatter(punch_stamp, "yyyyMMddHHmmss", "MM/dd/yyyy HH:mm a"));
                    att.setTime_out("-");
                    att.setReference_id(reference_id);
                    att.setEmployee_id(employee_id);
                    att.setPunch_stamp(punch_stamp);
                    att.setPunch_type(punch_type);
                    att.setDevice_type("3");
                    att.setDevice_id("20110510825");
                    att.setRemarks("");

                    attendance_list.add(att);
                }
            }
        } catch(IOException e) {
            System.out.println(e.getMessage());
        } finally {
            // set covered period
            date_period.setText(GlobalHelper.date_period_from + " to " + GlobalHelper.date_period_to);
            
            // add data to records table
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("employee_id"));
            timeInColumn.setCellValueFactory(new PropertyValueFactory<>("time_in"));
            timeOutColumn.setCellValueFactory(new PropertyValueFactory<>("time_out"));
            records_tbl.setItems(attendance_list);
            
            checkForInternetConnection();
        }
    }
    
    private void checkForInternetConnection() {
        new Thread(() -> {
            Platform.runLater(() -> {
               net_status.setText("Loading...");
            });
            
            Socket socket = null;
            boolean reachable = false;
            try {
                socket = new Socket("173.194.39.209", 80); // google.com
                reachable = true;
            } catch(Exception e) {
                if(e.getMessage().contains("Network is unreachable")) {
                    reachable = false;
                } else {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            } finally {
                if(socket != null) {
                    try {
                        socket.close();
                    } catch(IOException e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                }

                if(reachable) {
                    Platform.runLater(() -> {
                        net_status.setTextFill(Color.GREEN);
                        net_status.setText("Connected.");
                        btnUpload.disableProperty().set(false);
                    });
                    
                } else {
                    Platform.runLater(() -> {
                        net_status.setTextFill(Color.RED);
                        net_status.setText("No internet connection.");
                        btnUpload.disableProperty().set(true);
                    });
                }
            }
        }).start();
    }
    
    @FXML
    private void refreshNetStatus(ActionEvent event) {
        checkForInternetConnection();
    }
}
