package com.zlab.DroidFlasher;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;


import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    //<editor-fold desc="Init UI">
    /** Init UI **/
    /** Console Tab **/
    @FXML private Accordion tab_settings_accord;
    @FXML private TextArea tab_main_txt_area_log;

    /** Adb Tab **/
    @FXML private Button tab_adb_btn_check_device;
    @FXML private Circle tab_adb_device_status_orb;
    @FXML private Label tab_adb_device_status_txt;

    @FXML private MenuItem tab_adb_btn_reboot_device;
    @FXML private MenuItem tab_adb_btn_reboot_recovery;
    @FXML private MenuItem tab_adb_btn_reboot_bootloader;

    @FXML private MenuItem tab_adb_btn_server_kill;
    @FXML private MenuItem tab_adb_btn_server_start;

    @FXML private Button tab_adb_btn_file_push;
    @FXML private Button tab_adb_btn_file_pull;

    @FXML private ProgressBar tab_adb_progressbar;

    /** Settings Tab **/
    @FXML private TitledPane tab_settings_tool_set_group;

        /** Tools section **/
        @FXML private TextField tab_settings_tool_set_txt_tool_directory_browse;
        @FXML private Button tab_settings_tool_set_btn_tool_directory_browse;

        /** Override section **/
        /** fastboot **/
        @FXML private TextField  tab_settings_override_txt_fastboot_path;
        @FXML private Button     tab_settings_override_btn_fastboot_browse;
        @FXML private ToggleButton tab_settings_override_btn_fastboot_override;

        /** adb **/
        @FXML private TextField  tab_settings_override_txt_adb_path;
        @FXML private Button     tab_settings_override_btn_adb_browse;
        @FXML private ToggleButton     tab_settings_override_btn_adb_override;

        /** others **/
        @FXML private Button    tab_settings_others_btn_reinitialize;
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void logToConsole(String appendString){
        tab_main_txt_area_log.appendText(appendString);
    }

    public void initToggleBtn(){
        tab_settings_override_btn_fastboot_override.setOnAction((event) -> {
            if (tab_settings_override_btn_fastboot_override.isSelected()) {
                tab_settings_override_txt_fastboot_path.setDisable(true);
                tab_settings_override_btn_fastboot_browse.setDisable(true);
            } else {
                tab_settings_override_txt_fastboot_path.setDisable(false);
                tab_settings_override_btn_fastboot_browse.setDisable(false);}
            });
        tab_settings_override_btn_adb_override.setOnAction((event) -> {
                if(tab_settings_override_btn_adb_override.isSelected()){
                    tab_settings_override_txt_adb_path.setDisable(true);
                    tab_settings_override_btn_adb_browse.setDisable(true);
                } else {
                    tab_settings_override_txt_adb_path.setDisable(false);
                    tab_settings_override_btn_adb_browse.setDisable(false);}
                });
    }
    public void initBtn(){

        /** Check device **/
        tab_adb_btn_check_device.setOnAction((event) -> {
            try {
                String adb_devices_output=runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "devices", "-l");
                String[] finder = adb_devices_output.split("\n");
                if(!finder[finder.length-1].equals("List of devices attached ")){
                    tab_adb_device_status_orb.setFill(Color.GREENYELLOW);
                    String[] device_info = finder[finder.length-1].split("\\s+");
                    showDialogInformation("Success!", "Adb device detected.", "Device information is: " + device_info[0]);
                    tab_adb_device_status_txt.setText(device_info[4]+" "+device_info[5]+" "+device_info[3]);
                } else {
                    tab_adb_device_status_orb.setFill(Color.RED);
                    showDialogError("Ooops!", "Adb device not detected.","Try to reconnect.");
                    tab_adb_device_status_txt.setText("No device detected.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /** Server kill start **/
        tab_adb_btn_server_kill.setOnAction((event) -> {
            try {
                runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "kill-server");
                showDialogInformation("adb", "Operation complete", "Command kill-server sended to adb.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_server_start.setOnAction((event) -> {
            try {
                runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "start-server");
                showDialogInformation("adb", "Operation complete", "Command start-server sended to adb.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /** Reboot device **/
        tab_adb_btn_reboot_device.setOnAction((event) -> {
            try {
                runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "reboot");
                showDialogInformation("adb", "Operation complete", "Reboot command sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_recovery.setOnAction((event) -> {
            try {
                runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "reboot", "recovery");
                showDialogInformation("adb", "Operation complete", "Command \"reboot to recovery\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_bootloader.setOnAction((event) -> {
            try {
                runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "reboot", "bootloader");
                showDialogInformation("adb", "Operation complete", "Command \"reboot to bootlader\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /** File control **/
        tab_adb_btn_file_push.setOnAction((event) -> {
            try {
                File localfile = fileChooser();
                String remotefile = remotePushSetPath(localfile.getName());

                if (!remotefile.equals("")) {
                    new Thread(() -> {
                        try {
                            runCmdAdbPushPull(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "push", "-p", localfile.getPath(), remotefile);
                            Platform.runLater(() -> showDialogInformation("adb", "Operation complete", "File " + localfile.getName() + " pushed to remote path " + remotefile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_adb_btn_file_pull.setOnAction((event) -> {
            try {
                String remotefile = remotePullSetPath("test.zip");
                if (!remotefile.equals("")) {
                    File localfile = fileSaver();
                    new Thread(() -> {
                        try {
                            runCmdAdbPushPull(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "pull", "-p", remotefile, localfile.getPath());
                            Platform.runLater(() -> showDialogInformation("adb", "Operation complete", "File " + localfile.getName() + " pulled from remote path " + remotefile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });

        /** Tools directory select **/
        tab_settings_tool_set_btn_tool_directory_browse.setOnAction((event) -> {
            try {
                File dir = directoryChooser();
                if (checkAdbBin(dir) && checkFastbootBin(dir)) {
                    tab_settings_tool_set_txt_tool_directory_browse.setText(dir.getPath());
                } else {
                    String binaries = "";
                    if (!checkAdbBin(dir) && !checkFastbootBin(dir)) {
                        binaries = "adb and fastboot";
                    } else if (!checkAdbBin(dir)) {
                        binaries = "adb";
                    } else {
                        binaries = "fastboot";
                    }
                    showDialogErrorIsNotValidToolsDirectorySelected(binaries);
                }
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });

        /** Tools bin override **/
        tab_settings_override_btn_fastboot_browse.setOnAction((event) -> {
            try {
                File dir = directoryChooser();
                if(checkFastbootBin(dir)){
                    tab_settings_override_txt_fastboot_path.setText(dir.getPath()+"/fastboot");
                } else {
                    showDialogErrorIsNotValidToolsDirectorySelected("fastboot");
                }
            } catch (Exception e){
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_settings_override_btn_adb_browse.setOnAction((event) -> {
            try {
                File dir = directoryChooser();
                if(checkAdbBin(dir)){
                    tab_settings_override_txt_adb_path.setText(dir.getPath()+"/adb");
                } else {
                    showDialogErrorIsNotValidToolsDirectorySelected("adb");
                }
            } catch (Exception e){
                showDialogErrorNoDirectorySelected();
            }
        });

        /** Reinitialize **/
        tab_settings_others_btn_reinitialize.setOnAction((event) -> {
            tab_main_txt_area_log.appendText("Reinitialize inventory...\n");
            /** РЕИНИЦИАЛИЗАЦИЯ **/
            tab_main_txt_area_log.appendText("done...\n");
        });
    }
    public void initUIPreferences(){
        tab_settings_accord.setExpandedPane(tab_settings_tool_set_group);
    }

    public File directoryChooser(){
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.globalStage);
        return selectedDirectory;
    }
    public File fileChooser(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedFile = chooser.showOpenDialog(Main.globalStage);
        return selectedFile;
    }
    public File fileSaver(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select new file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedFile = chooser.showSaveDialog(Main.globalStage);
        return selectedFile;
    }

    public String remotePushSetPath(String filename){
        TextInputDialog dialog = new TextInputDialog("/sdcard/"+filename);
        dialog.setTitle("Set remote path");
        dialog.setHeaderText("File will be pushed to this remote path");
        dialog.setContentText("Remote path:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        } else {
            return "";
        }
    }
    public String remotePullSetPath(String filename){
        TextInputDialog dialog = new TextInputDialog("/sdcard/"+filename);
        dialog.setTitle("Enter remote path");
        dialog.setHeaderText("File will be pulled from this remote path to the local machine");
        dialog.setContentText("Remote path:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        } else {
            return "";
        }
    }

    private void showDialogErrorNoDirectorySelected(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops..");
        alert.setHeaderText("Operation rejected by user!");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }
    private void showDialogErrorIsNotValidToolsDirectorySelected(String binaries){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Oops..");
        alert.setHeaderText("No " + binaries + " binaries in this directory!");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }
    private void showDialogInformation(String title, String header, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    private void showDialogError(String title, String header, String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }


    private boolean checkAdbBin(File f){
        if(new File(f.getPath()+"/adb").exists()){
            return true;
        } else {
            return false;
        }
    }
    private boolean checkFastbootBin(File f){
        if(new File(f.getPath()+"/fastboot").exists()){
            return true;
        } else {
            return false;
        }
    }

    private String runCmd(String... args) throws IOException {
        String locallog="";

        Runtime rt = Runtime.getRuntime();

        Process proc = rt.exec(args);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        logToConsole("===\nrun cmd: " +/*bin+*/" " + Arrays.toString(args) + "\n\n");

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            //System.out.println(s);
            logToConsole(s + "\n");
            locallog=locallog+s+"\n";
        }

        while ((s = stdError.readLine()) != null) {
            logToConsole("err:" + s + "\n");
            locallog="err";
            //System.out.println(s);
        }//}
        return locallog;
    }
    private void runCmdAdbPushPull(String... args) throws IOException {
        Process proc = Runtime.getRuntime().exec(args);
        InputStream inputStream = proc.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String s = null;

        while ((s = bufferedReader.readLine()) != null) {
            if (s.startsWith("Transferring")) {
                Double progress = Double.parseDouble(s.split("\\(")[1].split("%")[0]) / 100;
                Platform.runLater(() -> tab_adb_progressbar.setProgress(progress));
            } else {
                logToConsole("err:" + s + "\n");
            }
        }
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
