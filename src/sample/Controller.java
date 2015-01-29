package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
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


import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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

    @FXML private MenuItem tab_adb_btn_kill_server;
    @FXML private MenuItem tab_adb_btn_start_server;

    @FXML private Button tab_adb_btn_push_file;
    @FXML private Button tab_adb_btn_pull_file;

    @FXML private ProgressBar tab_adb_progressbar;

    /** Settings Tab **/
    @FXML private TitledPane tab_settings_toolset_group;

        /** Tools section **/
        @FXML private TextField  tab_settings_toolset_txt_tool_directory_browse;
        @FXML private Button     tab_settings_toolset_btn_tool_directory_browse;

        /** Override section **/
        /** fastboot **/
        @FXML private TextField  tab_settings_override_txt_fastboot_path;
        @FXML private Button     tab_settings_override_btn_fastboot_browse;
        @FXML private ToggleButton tab_settings_override_btn_fastboot_override;
        //@FXML private CheckBox tab_settings_override_chk_fastboot_override;
        /** adb **/
        @FXML private TextField  tab_settings_override_txt_adb_path;
        @FXML private Button     tab_settings_override_btn_adb_browse;
        @FXML private ToggleButton     tab_settings_override_btn_adb_override;

        /** others **/
        @FXML private Button    tab_settings_others_btn_reinitialize;

    //public static Double progressbarpossition=0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void appendMainTabLog(String appenString){
        tab_main_txt_area_log.appendText(appenString);
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

        tab_adb_btn_check_device.setOnAction((event) -> {
            try {
                String adbdeviceout=runCmd(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "devices", "-l");

                String[] finder = adbdeviceout.split("\n");

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

                String s=finder[0];

            } catch (IOException e) {
                e.printStackTrace();
            }
        });



        tab_adb_btn_kill_server.setOnAction((event) -> {
            try {
                runCmd(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "kill-server");
                showDialogInformation("adb", "Operation complete", "Command kill-server sended to adb.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_start_server.setOnAction((event) -> {
            try {
                runCmd(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "start-server");
                showDialogInformation("adb", "Operation complete", "Command start-server sended to adb.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_device.setOnAction((event) -> {
            try {
                runCmd(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "reboot");
                showDialogInformation("adb", "Operation complete", "Reboot command sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_recovery.setOnAction((event) -> {
            try {
                runCmd(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "reboot", "recovery");
                showDialogInformation("adb", "Operation complete", "Command \"reboot to recovery\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_bootloader.setOnAction((event) -> {
            try {
                runCmd(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "reboot", "bootloader");
                showDialogInformation("adb", "Operation complete", "Command \"reboot to bootlader\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

/*
        progrestest.setOnAction((event) -> {
            try {
                runProgressTest();
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                tab_adb_progressbar.setProgress(progressbarpossition);
            }).start();


            while(progressbarpossition!=1.0){
                tab_adb_progressbar.setProgress(progressbarpossition);
            }

            tab_adb_progressbar.setProgress(1.0);
        });*/


        tab_adb_btn_push_file.setOnAction((event) -> {
            try {
                File localfile = fileChooser();
                String remotefile = remotePushSetPath(localfile.getName());

                if (!remotefile.equals("")){
                    new Thread(() -> {
                        try {
                            runCmdWithProgress(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "push", "-p", localfile.getPath(), remotefile);
                            Platform.runLater(() ->  showDialogInformation("adb", "Operation complete", "File " + localfile.getName() + " pushed to remote path " + remotefile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e){
                showDialogErrorNoDirectorySelected();
            }
        });

        tab_adb_btn_pull_file.setOnAction((event) -> {
            try {
                String remotefile = remotePullSetPath("test.zip");
                if (!remotefile.equals("")){
                    File localfile = fileSaver();
                    new Thread(() -> {
                        try {
                            runCmdWithProgress(tab_settings_toolset_txt_tool_directory_browse.getText() + "/adb", "pull", "-p", remotefile, localfile.getPath());
                            Platform.runLater(() ->  showDialogInformation("adb", "Operation complete", "File "+localfile.getName()+" pulled from remote path "+remotefile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e){
                showDialogErrorNoDirectorySelected();
            }
        });


        tab_settings_toolset_btn_tool_directory_browse.setOnAction((event) -> {
            try {
                File dir = directoryChooser();
                if(checkAdbBin(dir) && checkFastbootBin(dir)){
                    tab_settings_toolset_txt_tool_directory_browse.setText(dir.getPath());
                } else {
                    String binaries="";
                    if(!checkAdbBin(dir) && !checkFastbootBin(dir)){
                        binaries = "adb and fastboot";
                    } else if (!checkAdbBin(dir)){
                        binaries = "adb";
                    } else {
                        binaries = "fastboot";
                    }
                    showDialogErrorIsNotValidToolsDirectorySelected(binaries);
                }
            } catch (Exception e){
                showDialogErrorNoDirectorySelected();
            }
        });

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




        tab_settings_others_btn_reinitialize.setOnAction((event) -> {
            tab_main_txt_area_log.appendText("Reinitialize inventory...\n");
            /** РЕИНИЦИАЛИЗАЦИЯ **/
            tab_main_txt_area_log.appendText("done...\n");
        });
    }
    public void initUIPreferences(){
        tab_settings_accord.setExpandedPane(tab_settings_toolset_group);
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

// Traditional way to get the response value.
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

// Traditional way to get the response value.
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

        //String[] commands = {bin,args};

        Process proc = rt.exec(args);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

// read the output from the command
        //System.out.println("Here is the standard output of the command:\n");
        appendMainTabLog("===\nrun cmd: "+/*bin+*/" "+ Arrays.toString(args)+"\n\n");

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            //System.out.println(s);
            appendMainTabLog(s+"\n");
            locallog=locallog+s+"\n";
        }

// read any errors from the attempted command
        //System.out.println("Here is the standard error of the command (if any):\n");
        /*if(stdError == null){
            appendMainTabLog("\n\nNo error detected.\n===");
        } else {*/
        while ((s = stdError.readLine()) != null) {
            appendMainTabLog("err:"+s+"\n");
            locallog="err";
            //System.out.println(s);
        }//}
        return locallog;
    }


    private void runCmdWithProgress(String... args) throws IOException {
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
                appendMainTabLog("err:" + s + "\n");
            }
        }
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

                //String locallog = "";
                //Runtime rt = Runtime.getRuntime();
                //String[] commands = {bin,args};
                //Process proc = null;
               // try {
               //     proc = rt.exec(args);
               // } catch (IOException e) {
               //     e.printStackTrace();
               // }

               // BufferedReader stdInput = new BufferedReader(new
               //         InputStreamReader(proc.getInputStream()));

              //  BufferedReader stdError = new BufferedReader(new
              //          InputStreamReader(proc.getErrorStream()));

              //  appendMainTabLog("===\nrun cmd: " +/*bin+*/" " + Arrays.toString(args) + "\n\n");

              //  String s = null;
             /*   try {
                    while ((s = stdInput.readLine()) != null) {
                        //System.out.println(s);
                        appendMainTabLog(s + "\n");
                        locallog = locallog + s + "\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

      //  try {
              //      while ((s = stdError.readLine()) != null) {
                 //       if (s.startsWith("Transferring")) {
                          //  Double progress = Double.parseDouble(s.split("\\(")[1].split("%")[0]) / 100;
                           // Platform.runLater(() -> tab_adb_progressbar.setProgress(progress));
                            //updateProgressBar(progress);
                            //Platform.runLater(() -> updateProgressBar(progress));
                            //progressbarpossition=progress;
        //updateProgressBar(progress);
        //Platform.runLater(() -> updateProgressBar(progress));
        //progressbarpossition=progress;

        //  } else {
                          //  appendMainTabLog("err:" + s + "\n");
                         //   locallog = "err";
                      //  }
                        //System.out.println(s);
                    //}//}
             //   } catch (IOException e) {
               //     e.printStackTrace();
               // }
               // }
               // return null;
            //}
        //};
      //  Thread th = new Thread(task);
       // th.setDaemon(true);
      //  th.start();


    private void updateProgressBar(Double progress){
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                    Platform.runLater(() -> tab_adb_progressbar.setProgress(progress));
                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }
}
