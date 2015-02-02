package com.zlab.DroidFlasher;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    //<editor-fold desc="Init VARS">

    /** GLOBAL VARS **/
    public static String OS = System.getProperty("os.name").toLowerCase();
    public static String PLATFORM_TOOLS="";
    public static String PLATFORM_TOOLS_MAC="/bin/mac/platform-tools";
    public static String PLATFORM_TOOLS_NIX="/bin/win/platform-tools";
    public static String PLATFORM_TOOLS_WIN="/bin/nix/platform-tools";
    public static String PLATFORM_TOOLS_DIRECTORY=System.getProperty("user.dir");
    public static String ADB="adb";
    public static String FASTBOOT="fastboot";
    public static String ADB_BINARY="";
    public static String FASTBOOT_BINARY="";
    private static Alert global_alert;
    private static TextArea global_alert_text_area;

    /** Init UI **/

    /** Adb Tab **/
    @FXML private Button tab_adb_btn_check_device;
    @FXML private Button tab_adb_btn_file_push;
    @FXML private Button tab_adb_btn_file_pull;
    @FXML private Button tab_adb_btn_backup;
    @FXML private Button tab_adb_btn_restore;
    @FXML private Button tab_settings_override_btn_unpack_binaries;
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
    @FXML private Label  tab_adb_device_status_txt;
    @FXML private ImageView img_head_fileoperation;
    @FXML private ImageView img_head_application;
    @FXML private ImageView img_head_backup;
    @FXML private ImageView img_head_adb_status;
    @FXML private ImageView img_settings_unpack_binaries;

    /** FASTBOOT Tab **/
    @FXML private Button tab_fastboot_btn_check_device;
    @FXML private Button tab_fastboot_btn_reboot;
    @FXML private Button tab_fastboot_btn_reboot_bootloader;
    @FXML private Button tab_fastboot_btn_flash_recovery;
    @FXML private Label  tab_fastboot_device_status_txt;
    @FXML private ImageView img_head_fastboot_status;

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
    public void initConfiguration() {
        tab_settings_accord.setExpandedPane(tab_settings_tool_set_group);
        img_head_fileoperation.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_bin.png")));
        img_head_application.setImage(new Image(getClass().getResourceAsStream("/img/application_view_icons.png")));
        img_head_backup.setImage(new Image(getClass().getResourceAsStream("/img/backups.png")));
        img_head_adb_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
        img_settings_unpack_binaries.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_zip.png")));

        setPlatform();
        setBinaries();

        if(checkAdbBin(new File(ADB_BINARY)) || checkFastbootBin(new File(FASTBOOT_BINARY))) {
            if (showConfirmDialogs("Binaries", "ADB and FASTBOOT", "Can't locate adb and fastboot binaries, unpack built-in?")) {
                unpackBuildInBinaryDialog();
            }
        }
    }

    /** Buttons initialize **/
    public void initToggleBtn() {
        tab_settings_override_btn_fastboot_override.setOnAction((event) -> setBinaries());
        tab_settings_override_btn_adb_override.setOnAction((event) -> setBinaries());
    }
    public void initBtn() {
        /*********/
        /** ADB **/
        /** Check device **/
        tab_adb_btn_check_device.setOnAction((event) -> {
            try {
                String adb_devices_output = runCmd(ADB_BINARY, "devices", "-l");
                String[] finder = adb_devices_output.split("\n");
                if (!finder[finder.length - 1].equals("List of devices attached ")) {
                    img_head_adb_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_green.png")));
                    String[] device_info = finder[finder.length - 1].split("\\s+");
                    showDialogInformation("Success!", "Adb device detected.", "Device information is: " + device_info[0]);
                    tab_adb_device_status_txt.setText(device_info[4] + " " + device_info[5] + " " + device_info[3]);
                } else {
                    img_head_adb_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
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
                runCmd(ADB_BINARY, "kill-server");
                showDialogInformation("adb", "Operation complete", "Command kill-server sended to adb.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_server_start.setOnAction((event) -> {
            try {
                runCmd(ADB_BINARY, "start-server");
                showDialogInformation("adb", "Operation complete", "Command start-server sended to adb.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        /** Reboot device **/
        tab_adb_btn_reboot_device.setOnAction((event) -> {
            try {
                runCmd(ADB_BINARY, "reboot");
                showDialogInformation("adb", "Operation complete", "Reboot command sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_recovery.setOnAction((event) -> {
            try {
                runCmd(ADB_BINARY, "reboot", "recovery");
                showDialogInformation("adb", "Operation complete", "Command \"reboot to recovery\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_adb_btn_reboot_bootloader.setOnAction((event) -> {
            try {
                runCmd(ADB_BINARY, "reboot", "bootloader");
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

                if (!remotefile.equals("") || localfile !=null) {
                    new Thread(() -> {
                        try {
                            runCmdAdbPushPull(ADB_BINARY, "push", "-p", localfile.getPath(), remotefile);

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
                    if(localfile!=null){
                        new Thread(() -> {
                        try {
                            runCmdAdbPushPull(ADB_BINARY, "pull", "-p", remotefile, localfile.getPath());
                            Platform.runLater(() -> showDialogInformation("adb", "Operation complete", "File " + localfile.getName() + " pulled from remote path " + remotefile));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();}
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
                        log=runCmd(ADB_BINARY, "install", localfile.getPath());
                    } else {
                        log=runCmd(ADB_BINARY, "install", getInstallArgs(false), localfile.getPath());
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
                                log=log+runCmd(ADB_BINARY, "install", f.getPath())+"\n";
                                counter++;
                                percentile=counter / maxpercentile;
                                final Double finalPercentile = percentile;
                                Platform.runLater(() -> tab_adb_progressbar.setProgress(finalPercentile));}
                        } else {
                            for(File f:localfiles){
                                log=log+runCmd(ADB_BINARY, "install", getInstallArgs(false), f.getPath())+"\n";
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
                if(!packagename.equals("")){
                    new Thread(() -> {
                        try {
                            String log=runCmd(ADB_BINARY, "uninstall", packagename);

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
                    }).start();}
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_adb_btn_uninstall_keep_data.setOnAction((event) -> {

            boolean keep = showConfirmDialogs("WARNING!", "PLEASE READ CAREFULLY!", "The -k option uninstalls the application while retaining the data/cache.\n" +
                    "\" +\n" +
                    "                        \"At the moment, there is no way to remove the remaining data.\\n\" +\n" +
                    "                        \"You will have to reinstall the application with the same signature, and fully uninstall it.\\n\" +\n" +
                    "                        \"If you truly wish to continue, enter package name on next screen, for example com.zlab.datFM");

                if (keep){
                    String packagename = setUninstallPackage("com.zlab.datFM");
                    if(!packagename.equals("")){
                    new Thread(() -> {
                        try {
                            String log=runCmd(ADB_BINARY, "shell", "pm", "uninstall", "-k", packagename);

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
                    }).start();}
                } else {}
        });
        /** Backup **/
        tab_adb_btn_backup.setOnAction((event) -> {
            if(tab_adb_chk_backup_shared.isSelected() || tab_adb_chk_backup_all.isSelected()){
            File localfile = fileSaver();
            if(localfile!=null){
                int count=8;
                if(tab_adb_chk_backup_all.isSelected()) {count++;}

                String[] commands = new String[count];

                commands[0]=ADB_BINARY;
                commands[1]="backup";
                commands[2]="-f";
                commands[3]=localfile.getPath();

                if (tab_adb_chk_backup_shared.isSelected()) {commands[4]="-shared";} else {commands[4]="-noshared";}
                if (tab_adb_chk_backup_apk.isSelected())    {commands[5]="-apk";}    else {commands[5]="-noapk";}
                if (tab_adb_chk_backup_obb.isSelected())    {commands[6]="-obb";}    else {commands[6]="-noobb";}
                if (tab_adb_chk_backup_system.isSelected()) {commands[7]="-system";} else {commands[7]="-nosystem";}
                if (tab_adb_chk_backup_all.isSelected())    {commands[8]="-all";}

                showDialogInformation("Backup", "Attention", "Now:\n-unlock your device\n-press OK in this window\n-confirm the backup operation on the phone.\n\n"
                        +"Please don't close window on the phone.");
            new Thread(() -> {
                try {
                    runCmd(commands);
                    Platform.runLater(() -> showDialogInformation("Backup", "Operation complete", "All done. Backup file stored at "+localfile.getPath()+"."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();}
            } else {
                Platform.runLater(() -> showDialogInformation("Backup", "Incorrect operation", "SHARED or ALL must be selected."));
            }
        });
        tab_adb_btn_restore.setOnAction((event) -> {
            try {
                File localfile = fileChooser();
                if (localfile !=null) {
                    showDialogInformation("Restore", "Attention", "Now:\n-unlock your device\n-press OK in this window\n-confirm restore operation on the phone.\n\n"
                            +"Please don't close window on the phone.");
                    new Thread(() -> {
                        try {
                            runCmd(ADB_BINARY, "restore", localfile.getPath());
                            Platform.runLater(() -> showDialogInformation("Restore", "Operation complete", "File " + localfile.getName() + " restored. See Console for detail."));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (Exception e) {
                showDialogErrorNoDirectorySelected();
            }
        });
        tab_adb_chk_backup_all.setOnAction((event) -> {
            if(tab_adb_chk_backup_all.isSelected()){
                tab_adb_chk_backup_apk.setSelected(true);
                tab_adb_chk_backup_obb.setSelected(true);
                tab_adb_chk_backup_shared.setSelected(true);
                tab_adb_chk_backup_system.setSelected(true);
            } else {
                tab_adb_chk_backup_apk.setSelected(false);
                tab_adb_chk_backup_obb.setSelected(false);
                tab_adb_chk_backup_shared.setSelected(false);
                tab_adb_chk_backup_system.setSelected(false);
            }
        });

        /**************/
        /** FASTBOOT **/
        tab_fastboot_btn_check_device.setOnAction((event) -> {
            try {
                String fastboot_devices_output = runCmd(FASTBOOT_BINARY, "devices");
                if (!fastboot_devices_output.equals("")) {
                    String[] device_info = fastboot_devices_output.split("\t");
                    img_head_fastboot_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_green.png")));
                    showDialogInformation("Success!", "Fastboot device detected.", "Device information is: " + device_info[0]+" "+device_info[1]);
                    tab_fastboot_device_status_txt.setText(device_info[0]+" "+device_info[1]);
                } else {
                    img_head_fastboot_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
                    showDialogError("Ooops!", "Fastboot device not detected.", "Try to reconnect.");
                    tab_fastboot_device_status_txt.setText("No device detected.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_fastboot_btn_reboot.setOnAction((event) -> {
            try {
                runCmd(FASTBOOT_BINARY, "reboot");
                showDialogInformation("fastboot", "Operation complete", "Command \"reboot\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_fastboot_btn_reboot_bootloader.setOnAction((event) -> {
            try {
                runCmd(FASTBOOT_BINARY, "reboot-bootloader");
                showDialogInformation("fastboot", "Operation complete", "Command \"reboot-bootloader\" sended to device.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tab_fastboot_btn_flash_recovery.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                    try {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash recovery " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "recovery", localfile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
        }});

        /**************/
        /** SETTINGS **/
        /** Tools directory select **/
        tab_settings_tool_set_btn_tool_directory_browse.setOnAction((event) -> {
            try {
                File dir = directoryChooser();
                if (checkAdbBin(dir) && checkFastbootBin(dir)) {
                    tab_settings_tool_set_txt_tool_directory_browse.setText(dir.getPath());
                    setBinaries();
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
                    tab_settings_override_txt_fastboot_path.setText(dir.getPath() + "/" + FASTBOOT);
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
                    tab_settings_override_txt_adb_path.setText(dir.getPath() + "/" + ADB);
                } else {
                    showDialogErrorIsNotValidToolsDirectorySelected("adb");
                }
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
        tab_settings_override_btn_unpack_binaries.setOnAction((event) -> unpackBuildInBinaryDialog());
    }

    /** Application **/
    private String getInstallArgs(boolean isMultipleApk){
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
    private String setUninstallPackage(String packagename) {
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
    private String remotePushSetPath(String filename) {
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
    private String remotePullSetPath(String filename) {
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
    private File directoryChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.globalStage);
        return selectedDirectory;
    }
    private File fileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedFile = chooser.showOpenDialog(Main.globalStage);
        return selectedFile;
    }
    private List<File> fileChooserMultiple() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        List<File> selectedFile = chooser.showOpenMultipleDialog(Main.globalStage);
        return selectedFile;
    }
    private File fileSaver() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select new file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedFile = chooser.showSaveDialog(Main.globalStage);
        return selectedFile;
    }
    private File directorySaver() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory for extraction");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Main.globalStage);
        return selectedDirectory;
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
    private void showDialogInformationGlobal(String title, String header, String text) {
        global_alert = new Alert(Alert.AlertType.INFORMATION);
        global_alert.setResizable(true);
        global_alert.setTitle(title);
        global_alert.setHeaderText(header);
        global_alert.setContentText(text);
// Create expandable Exception.

        Label label = new Label("Console output:");

        global_alert_text_area = new TextArea("");
        global_alert_text_area.setEditable(false);
        global_alert_text_area.setWrapText(true);

        global_alert_text_area.setMaxWidth(Double.MAX_VALUE);
        global_alert_text_area.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(global_alert_text_area, Priority.ALWAYS);
        GridPane.setHgrow(global_alert_text_area, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(global_alert_text_area, 0, 1);

// Set expandable Exception into the dialog pane.
        global_alert.getDialogPane().setExpandableContent(expContent);
        global_alert.getDialogPane().setExpanded(true);
        global_alert.show();
    }
    private void showDialogError(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    private boolean showConfirmDialogs(String title, String header, String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

    /** Check inventory **/
    private boolean checkAdbBin(File f) {
        if (new File(f.getPath() + "/"+ADB).exists()) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkFastbootBin(File f) {
        if (new File(f.getPath() + "/"+FASTBOOT).exists()) {
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
    private void runCmdToGlobalAlert(String... args) throws IOException {
        Process proc = Runtime.getRuntime().exec(args);

        InputStream errStream = proc.getErrorStream();
        InputStreamReader errStreamReader = new InputStreamReader(errStream);
        BufferedReader errBufferedReader = new BufferedReader(errStreamReader);

        InputStream stdStream = proc.getInputStream();
        InputStreamReader stdStreamReader = new InputStreamReader(stdStream);
        BufferedReader stdBufferedReader = new BufferedReader(stdStreamReader);

        String err = null;
        //String std = null;

        while ((err = errBufferedReader.readLine()) !=null) {
            //final String finalStd = std;
            final String finalErr = err;
            Platform.runLater(() -> {
                if(global_alert!=null){
                    global_alert_text_area.setText(global_alert_text_area.getText()+finalErr + "\n");
                }
            });

        }
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    /** Platfrom detection **/
    private void setPlatform(){
        if (isWindows()) {
            PLATFORM_TOOLS=PLATFORM_TOOLS_WIN;
            ADB="adb.exe";
            FASTBOOT="fastboot.exe";
        } else if (isMac()) {
            PLATFORM_TOOLS=PLATFORM_TOOLS_MAC;
        } else if (isUnix()) {
            PLATFORM_TOOLS=PLATFORM_TOOLS_NIX;
        } else {
            PLATFORM_TOOLS="binary not found";
        }
    }
    private boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }
    private boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }
    private boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    /** Binaries **/
    private void setBinaries(){
        if (tab_settings_override_btn_fastboot_override.isSelected()) {
            tab_settings_override_txt_fastboot_path.setDisable(true);
            tab_settings_override_btn_fastboot_browse.setDisable(true);
            FASTBOOT_BINARY=tab_settings_override_txt_fastboot_path.getText();
        } else {
            tab_settings_override_txt_fastboot_path.setDisable(false);
            tab_settings_override_btn_fastboot_browse.setDisable(false);
            FASTBOOT_BINARY=tab_settings_tool_set_txt_tool_directory_browse.getText() + "/"+FASTBOOT;
        }
        if (tab_settings_override_btn_adb_override.isSelected()) {
            tab_settings_override_txt_adb_path.setDisable(true);
            tab_settings_override_btn_adb_browse.setDisable(true);
            ADB_BINARY=tab_settings_override_txt_adb_path.getText();
        } else {
            tab_settings_override_txt_adb_path.setDisable(false);
            tab_settings_override_btn_adb_browse.setDisable(false);
            ADB_BINARY=tab_settings_tool_set_txt_tool_directory_browse.getText() + "/" + ADB;
        }
    }
    private void unpackBuildInBinaryDialog(){
        File unpack_directory = directorySaver();
        if(unpack_directory!=null){
            unpackBuildInBinary(unpack_directory.getPath());
            tab_settings_override_txt_adb_path.setText(unpack_directory.getPath()+"/"+ADB);
            tab_settings_override_txt_fastboot_path.setText(unpack_directory.getPath()+"/"+FASTBOOT);

            if(showConfirmDialogs("Unpack binaries", "Operation complete", "Binaries unpacked in  "+unpack_directory.getPath()+".\n\nOverride adb and fastboot to this binary?")){
                ADB_BINARY=tab_settings_override_txt_adb_path.getText();
                FASTBOOT_BINARY=tab_settings_override_txt_fastboot_path.getText();
                tab_settings_override_btn_fastboot_override.setSelected(true);
                tab_settings_override_btn_adb_override.setSelected(true);
                tab_settings_override_txt_fastboot_path.setDisable(true);
                tab_settings_override_btn_fastboot_browse.setDisable(true);
                tab_settings_override_txt_adb_path.setDisable(true);
                tab_settings_override_btn_adb_browse.setDisable(true);
            }
            if(showConfirmDialogs("Tools directory path", "Configuration", "Use built-in binary path as tools directory?")){
                PLATFORM_TOOLS_DIRECTORY=unpack_directory.getPath();
                tab_settings_tool_set_txt_tool_directory_browse.setText(PLATFORM_TOOLS_DIRECTORY);
            }
        }
    }
    private void unpackBuildInBinary(String directory){
        try {
            extractResource(ADB, directory);
            extractResource(FASTBOOT, directory);
        } catch (IOException e) {e.printStackTrace();}
    }
    private String extractResource(String name, String dstDir) throws IOException {
        File dstFile=new File(dstDir+"/"+name);
        try {
            InputStream resource = getClass().getResourceAsStream(PLATFORM_TOOLS+"/"+name);
            FileOutputStream outStream = new FileOutputStream(dstFile);
            byte[] buffer = new byte[1024];
            int bytes;
            while ((bytes = resource.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytes);
            }
            outStream.close();
            resource.close();
        } catch (IOException e) {
            //System.exit(-1);
        }
        dstFile.setExecutable(true);
        System.out.println("Successfully extracted "+dstFile.getAbsolutePath());
        return dstFile.getAbsolutePath();
    }
}

