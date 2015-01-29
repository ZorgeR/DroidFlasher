package com.zlab.DroidFlasher;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    //<editor-fold desc="Init UI">
    /** Init UI **/

    /** Adb Tab **/
    @FXML private Button tab_adb_btn_check_device;
    @FXML private Button tab_adb_btn_file_push;
    @FXML private Button tab_adb_btn_file_pull;
    @FXML private Button tab_adb_btn_backup;
    @FXML private Button tab_adb_btn_restore;
    @FXML private CheckBox tab_adb_chk_backup_apk;
    @FXML private CheckBox tab_adb_chk_backup_obb;
    @FXML private CheckBox tab_adb_chk_backup_shared;
    @FXML private CheckBox tab_adb_chk_backup_system;
    @FXML private CheckBox tab_adb_chk_backup_all;
    @FXML private CheckBox tab_adb_btn_install_flock;
    @FXML private CheckBox tab_adb_btn_install_replace;
    @FXML private CheckBox tab_adb_btn_install_test;
    @FXML private CheckBox tab_adb_btn_install_sdcard;
    @FXML private CheckBox tab_adb_btn_install_downgrade;
    @FXML private CheckBox tab_adb_btn_install_partial;
    @FXML private MenuItem tab_adb_btn_reboot_device;
    @FXML private MenuItem tab_adb_btn_reboot_recovery;
    @FXML private MenuItem tab_adb_btn_reboot_bootloader;
    @FXML private MenuItem tab_adb_btn_server_kill;
    @FXML private MenuItem tab_adb_btn_server_start;
    @FXML private MenuItem tab_adb_btn_install;
    @FXML private MenuItem tab_adb_btn_install_multiple;
    @FXML private MenuItem tab_adb_btn_uninstall;
    @FXML private MenuItem tab_adb_btn_uninstall_keep_data;
    @FXML private ProgressBar tab_adb_progressbar;
    @FXML private Circle tab_adb_device_status_orb;
    @FXML private Label  tab_adb_device_status_txt;


    /** Settings Tab **/
    @FXML private TitledPane tab_settings_tool_set_group;
    /** Tools section */
        @FXML private TextField tab_settings_tool_set_txt_tool_directory_browse;
        @FXML private Button tab_settings_tool_set_btn_tool_directory_browse;
    /** Override section */
        /** fastboot */
        @FXML private TextField tab_settings_override_txt_fastboot_path;
        @FXML private Button tab_settings_override_btn_fastboot_browse;
        @FXML private ToggleButton tab_settings_override_btn_fastboot_override;
        /** adb */
        @FXML private TextField tab_settings_override_txt_adb_path;
        @FXML private Button tab_settings_override_btn_adb_browse;
        @FXML private ToggleButton tab_settings_override_btn_adb_override;
        /** others */
        @FXML private Button tab_settings_others_btn_reinitialize;

    /** Console Tab **/
    @FXML private Accordion tab_settings_accord;
    @FXML private TextArea  tab_main_txt_area_log;
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public void initUIPreferences() {
        tab_settings_accord.setExpandedPane(tab_settings_tool_set_group);
    }

    /** Buttons initialize **/
    public void initToggleBtn() {
        tab_settings_override_btn_fastboot_override.setOnAction((event) -> {
            if (tab_settings_override_btn_fastboot_override.isSelected()) {
                tab_settings_override_txt_fastboot_path.setDisable(true);
                tab_settings_override_btn_fastboot_browse.setDisable(true);
            } else {
                tab_settings_override_txt_fastboot_path.setDisable(false);
                tab_settings_override_btn_fastboot_browse.setDisable(false);
            }
        });
        tab_settings_override_btn_adb_override.setOnAction((event) -> {
            if (tab_settings_override_btn_adb_override.isSelected()) {
                tab_settings_override_txt_adb_path.setDisable(true);
                tab_settings_override_btn_adb_browse.setDisable(true);
            } else {
                tab_settings_override_txt_adb_path.setDisable(false);
                tab_settings_override_btn_adb_browse.setDisable(false);
            }
        });
    }
    public void initBtn() {
        /** Check device **/
        tab_adb_btn_check_device.setOnAction((event) -> {
            try {
                String adb_devices_output = runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "devices", "-l");
                String[] finder = adb_devices_output.split("\n");
                if (!finder[finder.length - 1].equals("List of devices attached ")) {
                    tab_adb_device_status_orb.setFill(Color.GREENYELLOW);
                    String[] device_info = finder[finder.length - 1].split("\\s+");
                    showDialogInformation("Success!", "Adb device detected.", "Device information is: " + device_info[0]);
                    tab_adb_device_status_txt.setText(device_info[4] + " " + device_info[5] + " " + device_info[3]);
                } else {
                    tab_adb_device_status_orb.setFill(Color.RED);
                    showDialogError("Ooops!", "Adb device not detected.", "Try to reconnect.");
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
                if (checkFastbootBin(dir)) {
                    tab_settings_override_txt_fastboot_path.setText(dir.getPath() + "/fastboot");
                } else {
                    showDialogErrorIsNotValidToolsDirectorySelected("fastboot");
                }
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_settings_override_btn_adb_browse.setOnAction((event) -> {
            try {
                File dir = directoryChooser();
                if (checkAdbBin(dir)) {
                    tab_settings_override_txt_adb_path.setText(dir.getPath() + "/adb");
                } else {
                    showDialogErrorIsNotValidToolsDirectorySelected("adb");
                }
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });

        /** Application **/
        tab_adb_btn_install.setOnAction((event) -> {
            try{
            File localfile = fileChooser();
                if(localfile!=null){
            new Thread(() -> {
                try {
                    String log;
                    if(getInstallArgs(false)==null) {
                        log=runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "install", localfile.getPath());
                    } else {
                        log=runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "install", getInstallArgs(false), localfile.getPath());
                    }
                    Platform.runLater(() -> tab_adb_progressbar.setProgress(1.0));

                    Platform.runLater(() -> {
                        if(log.contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES")){
                            showDialogError("Error","Installation FAIL!","Selected apk is not signed!");
                        } else if (log.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
                            showDialogError("Error","Installation FAIL!","Application exist! Use replace option to overwrite.");
                        } else if (log.contains("Failure")) {
                            showDialogError("Error","Installation FAIL!","Can't install APK, see console for detail.");
                        } else {
                            showDialogInformation("adb", "Operation complete", "File " + localfile.getName() + " installed on device.");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();}
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_adb_btn_install_multiple.setOnAction((event) -> {
            try{
            List<File> localfiles = fileChooserMultiple();
                if(localfiles!=null){
                new Thread(() -> {
                    try {
                        String log="";
                        Double percentile=0.0;
                        int maxpercentile=localfiles.size();
                        Double counter=0.0;

                        if(getInstallArgs(false)==null) {
                            for(File f:localfiles){
                                log=log+runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "install", f.getPath())+"\n";
                                counter++;
                                percentile=counter / maxpercentile;
                                final Double finalPercentile = percentile;
                                Platform.runLater(() -> tab_adb_progressbar.setProgress(finalPercentile));}
                        } else {
                            for(File f:localfiles){
                                log=log+runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "install", getInstallArgs(false), f.getPath())+"\n";
                                counter++;
                                percentile=counter / maxpercentile;
                                final Double finalPercentile = percentile;
                                Platform.runLater(() -> tab_adb_progressbar.setProgress(finalPercentile));
                            }
                        }

                        final String finalLog = log;
                        Platform.runLater(() -> {
                            int failure = (finalLog.length() - finalLog.substring(0).replaceAll("Failure", "").length())/7;
                            int success = (finalLog.length() - finalLog.substring(0).replaceAll("Success","").length())/7;

                            showDialogInformation("adb", "Operation complete",
                                    "Success: "+success
                                    +"\nFailure: "+failure+"\n"
                                    +"\nSee Console for detail."+"\n"
                                    +"\nFile list:\n"+Arrays.asList(localfiles).toString().replace(", ","\n").replace("[","").replace("]",""));
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();}
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_adb_btn_uninstall.setOnAction((event) -> {
            try {
                String packagename = setUninstallPackage("com.zlab.datFM");

                    new Thread(() -> {
                        try {
                            String log=runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "uninstall", packagename);

                            final String finalLog = log;
                            Platform.runLater(() -> {
                                int failure = (finalLog.length() - finalLog.substring(0).replaceAll("Failure", "").length())/7;
                                int success = (finalLog.length() - finalLog.substring(0).replaceAll("Success","").length())/7;

                                if(success==1){
                                    showDialogInformation("adb", "Operation complete", "Package " + packagename + " uninstalled from device,");
                                } else {
                                    showDialogInformation("adb", "Operation not complete", "Can't uninstall " + packagename + ", see Console for detail.");
                                }
                                /** FOR THE NEXT RELEASE (MULTIPLE UNINSTALLER)
                                showDialogInformation("adb", "Operation complete",
                                        "Success: "+success
                                                +"\nFailure: "+failure+"\n"
                                                +"\nSee Console for detail."+"\n");*/
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_adb_btn_uninstall_keep_data.setOnAction((event) -> {
            try {
                String packagename = setUninstallPackage("com.zlab.datFM");

                new Thread(() -> {
                    try {
                        String log=runCmd(tab_settings_tool_set_txt_tool_directory_browse.getText() + "/adb", "uninstall", "-k", packagename);

                        final String finalLog = log;
                        Platform.runLater(() -> {
                            int failure = (finalLog.length() - finalLog.substring(0).replaceAll("Failure", "").length())/7;
                            int success = (finalLog.length() - finalLog.substring(0).replaceAll("Success","").length())/7;

                            if(success==1){
                                showDialogInformation("adb", "Operation complete", "Package " + packagename + " uninstalled from device,");
                            } else {
                                showDialogInformation("adb", "Operation not complete", "Can't uninstall " + packagename + ", see Console for detail.");
                            }
                            /** FOR THE NEXT RELEASE (MULTIPLE UNINSTALLER)
                             showDialogInformation("adb", "Operation complete",
                             "Success: "+success
                             +"\nFailure: "+failure+"\n"
                             +"\nSee Console for detail."+"\n");*/
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
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

    /** Application **/
    public String getInstallArgs(boolean isMultipleApk){
        String install_args = "-";
        if(tab_adb_btn_install_flock.isSelected()){install_args=install_args+"l";}
        if(tab_adb_btn_install_replace.isSelected()){install_args=install_args+"r";}
        if(tab_adb_btn_install_test.isSelected()){install_args=install_args+"t";}
        if(tab_adb_btn_install_sdcard.isSelected()){install_args=install_args+"s";}
        if(tab_adb_btn_install_downgrade.isSelected()){install_args=install_args+"d";}
        if(isMultipleApk){
            if(tab_adb_btn_install_partial.isSelected()){install_args=install_args+"p";}
        }
        if(install_args.equals("-")){install_args=null;}
        return install_args;
    }
    public String setUninstallPackage(String packagename) {
        TextInputDialog dialog = new TextInputDialog(packagename);
        dialog.setTitle("Set package name");
        dialog.setHeaderText("Enter package name to uninstall");
        dialog.setContentText("Package:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }

    /** File control **/
    public String remotePushSetPath(String filename) {
        TextInputDialog dialog = new TextInputDialog("/sdcard/" + filename);
        dialog.setTitle("Set remote path");
        dialog.setHeaderText("File will be pushed to this remote path");
        dialog.setContentText("Remote path:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }
    public String remotePullSetPath(String filename) {
        TextInputDialog dialog = new TextInputDialog("/sdcard/" + filename);
        dialog.setTitle("Enter remote path");
        dialog.setHeaderText("File will be pulled from this remote path to the local machine");
        dialog.setContentText("Remote path:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }

    /** File chooser **/
    public File directoryChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.globalStage);
        return selectedDirectory;
    }
    public File fileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedFile = chooser.showOpenDialog(Main.globalStage);
        return selectedFile;
    }
    public List<File> fileChooserMultiple() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        List<File> selectedFile = chooser.showOpenMultipleDialog(Main.globalStage);
        return selectedFile;
    }
    public File fileSaver() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select new file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedFile = chooser.showSaveDialog(Main.globalStage);
        return selectedFile;
    }

    /** Show Dialog **/
    private void showDialogErrorNoDirectorySelected() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops..");
        alert.setHeaderText("Operation rejected by user!");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }
    private void showDialogErrorIsNotValidToolsDirectorySelected(String binaries) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Oops..");
        alert.setHeaderText("No " + binaries + " binaries in this directory!");
        alert.setContentText("Please try again.");
        alert.showAndWait();
    }
    private void showDialogInformation(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    private void showDialogError(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

    /** Check inventory **/
    private boolean checkAdbBin(File f) {
        if (new File(f.getPath() + "/adb").exists()) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkFastbootBin(File f) {
        if (new File(f.getPath() + "/fastboot").exists()) {
            return true;
        } else {
            return false;
        }
    }

    /** Console **/
    private String runCmd(String... args) throws IOException {
        String locallog = "";

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
            locallog = locallog + s + "\n";
        }

        while ((s = stdError.readLine()) != null) {
            logToConsole("err:" + s + "\n");
            locallog = locallog + "err:" + s + "\n";
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

    /** LOG **/
    public void logToConsole(String appendString) {
        tab_main_txt_area_log.appendText(appendString);
    }
}
