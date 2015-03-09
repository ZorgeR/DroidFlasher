package com.zlab.DroidFlasher;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    //<editor-fold desc="Init VARS">

    /** GLOBAL VARS **/
    public static String OS = System.getProperty("os.name").toLowerCase();
    public static String PLATFORM_TOOLS="";
    public static String PLATFORM_TOOLS_MAC="/bin/mac/platform-tools";
    public static String PLATFORM_TOOLS_NIX="/bin/nix/platform-tools";
    public static String PLATFORM_TOOLS_WIN="/bin/win/platform-tools";
    public static String PLATFORM_TOOLS_DIRECTORY=System.getProperty("user.dir");
    public static String ADB="adb";
    public static String FASTBOOT="fastboot";
    public static String MFASTBOOT="mfastboot";
    public static String ADB_BINARY="";
    public static String FASTBOOT_BINARY="";
    public static String MFASTBOOT_BINARY="";
    public static int DNDTYPE_OPEN_DFS=0;
    public static int DNDTYPE_SEND_FILE=1;
    public static boolean SIMPLEMODE;
    private static Alert global_alert;
    private static TextArea global_alert_text_area;

    /** Init UI **/
    private Scene mScene;
    @FXML private TabPane top_tab_pane;

    /** Adb Tab **/
    @FXML private Button tab_adb_btn_check_device;
    @FXML private Button tab_adb_btn_file_push;
    @FXML private Button tab_adb_btn_file_pull;
    @FXML private Button tab_adb_btn_backup;
    @FXML private Button tab_adb_btn_restore;
    @FXML private Button tab_settings_override_btn_unpack_binaries;
    @FXML private Button tab_adb_btn_console;
    @FXML private Button tab_adb_btn_run_dfs;
    @FXML private AnchorPane tab_adb_sendfile;
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
    @FXML private Button tab_adb_btn_install;
    @FXML private Button tab_adb_btn_install_multiple;
    @FXML private Button tab_adb_btn_uninstall;
    @FXML private Button tab_adb_btn_uninstall_keep_data;
    @FXML private ProgressBar tab_adb_progressbar;
    @FXML private Label  tab_adb_device_status_txt;
    @FXML private ImageView img_head_fileoperation;
    @FXML private ImageView img_head_application;
    @FXML private ImageView img_head_backup;
    @FXML private ImageView img_adb_status;
    @FXML private ImageView img_settings_unpack_binaries;
    @FXML private ImageView img_head_unlocking;
    @FXML private ImageView img_head_flash;
    @FXML private ImageView img_console_adb;

    /** FASTBOOT Tab **/
    @FXML private Button tab_fastboot_btn_check_device;
    @FXML private Button tab_fastboot_btn_reboot;
    @FXML private Button tab_fastboot_btn_reboot_bootloader;
    @FXML private Button tab_fastboot_btn_run_dfs;
    @FXML private Button tab_fastboot_btn_flash_recovery;
    @FXML private Button tab_fastboot_btn_flash_boot;
    @FXML private Button tab_fastboot_btn_flash_cache;
    @FXML private Button tab_fastboot_btn_flash_userdata;
    @FXML private Button tab_fastboot_btn_flash_system;
    @FXML private Button tab_fastboot_btn_flash_radio;
    @FXML private Button tab_fastboot_btn_boot_img;
    @FXML private Button tab_fastboot_btn_flash_mfastboot_system;
    @FXML private Button tab_fastboot_btn_oem_unlock;
    @FXML private Button tab_fastboot_btn_oem_lock;
    @FXML private Button tab_fastboot_btn_oem_get_unlock_data;
    @FXML private Button tab_fastboot_btn_oem_lock_begin;
    @FXML private Button tab_fastboot_btn_console;
    @FXML private Label  tab_fastboot_device_status_txt;
    @FXML private ProgressBar tab_fastboot_progressbar;
    @FXML private ImageView img_fastboot_status;
    @FXML private ImageView img_console_fastboot;
    @FXML private ImageView img_head_other;

    /** RECOVERY Tab **/
    @FXML private Button tab_recovery_btn_check_device;
    @FXML private Button tab_recovery_btn_adb_sideload;
    @FXML private Button tab_recovery_btn_flash_zip;
    @FXML private Button tab_recovery_btn_flash_multizip;
    @FXML private Button tab_recovery_btn_flash_zip_from_sd;
    @FXML private Button tab_recovery_btn_wipe;
    @FXML private Button tab_recovery_btn_backup;
    @FXML private Button tab_recovery_btn_restore;
    @FXML private Button tab_recovery_btn_mount;
    @FXML private Button tab_recovery_btn_mkdir;
    @FXML private Button tab_recovery_btn_chmod;
    @FXML private Button tab_recovery_btn_off_zip_verify;
    @FXML private Button tab_recovery_btn_run_dfs;
    @FXML private Button tab_recovery_btn_console;
    @FXML private Button tab_recovery_btn_file_push;
    @FXML private Button tab_recovery_btn_file_pull;
    @FXML private AnchorPane tab_recovery_sendfile;
    @FXML private CheckBox tab_recovery_chk_wipe_cache;
    @FXML private CheckBox tab_recovery_chk_wipe_data;
    @FXML private CheckBox tab_recovery_chk_wipe_dalvik;
    @FXML private CheckBox tab_recovery_chk_backup_system;
    @FXML private CheckBox tab_recovery_chk_backup_data;
    @FXML private CheckBox tab_recovery_chk_backup_cache;
    @FXML private CheckBox tab_recovery_chk_backup_recovery;
    @FXML private CheckBox tab_recovery_chk_backup_boot;
    @FXML private CheckBox tab_recovery_chk_backup_asec;
    @FXML private CheckBox tab_recovery_chk_backup_sdext;
    @FXML private CheckBox tab_recovery_chk_backup_compression;
    @FXML private CheckBox tab_recovery_chk_backup_nomd5;
    @FXML private MenuItem tab_recovery_btn_reboot_device;
    @FXML private MenuItem tab_recovery_btn_reboot_recovery;
    @FXML private MenuItem tab_recovery_btn_reboot_bootloader;
    @FXML private MenuItem tab_recovery_btn_server_kill;
    @FXML private MenuItem tab_recovery_btn_server_start;
    @FXML private ProgressBar tab_recovery_progressbar;
    @FXML private Label tab_recovery_device_status_txt;
    @FXML private ImageView img_console_recovery;
    @FXML private ImageView img_head_wipe;
    @FXML private ImageView img_head_flash_from_recovery;
    @FXML private ImageView img_head_backup_from_recovery;
    @FXML private ImageView img_head_backup_from_recovery_restore;
    @FXML private ImageView img_head_recovery_other;
    @FXML private ImageView img_recovery_status;
    @FXML private ImageView img_head_fileoperation_recovery;


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
        /** mfastboot */
        @FXML private TextField tab_settings_override_txt_mfastboot_path;
        @FXML private Button tab_settings_override_btn_mfastboot_browse;
        @FXML private ToggleButton tab_settings_override_btn_mfastboot_override;
        /** others */
        @FXML private Button tab_settings_others_btn_reinitialize;
        @FXML private CheckBox tab_settings_others_chk_simplemode;

    /** Console Tab **/
    @FXML private Accordion tab_settings_accord;
    @FXML private TextArea  tab_main_txt_area_log;

    @FXML private TextField console_text_input;
    @FXML private TextArea console_text_output;
    //</editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    public void initConfiguration() {
        tab_settings_accord.setExpandedPane(tab_settings_tool_set_group);
        img_head_fileoperation.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_bin.png")));
        img_head_application.setImage(new Image(getClass().getResourceAsStream("/img/application_view_icons.png")));
        img_head_backup.setImage(new Image(getClass().getResourceAsStream("/img/backups.png")));
        img_adb_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
        img_settings_unpack_binaries.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_zip.png")));
        img_head_flash.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_bat.png")));
        img_head_unlocking.setImage(new Image(getClass().getResourceAsStream("/img/lock-unlock.png")));
        img_console_adb.setImage(new Image(getClass().getResourceAsStream("/img/terminal-pencil.png")));
        img_console_fastboot.setImage(new Image(getClass().getResourceAsStream("/img/terminal-pencil.png")));
        img_console_recovery.setImage(new Image(getClass().getResourceAsStream("/img/terminal-pencil.png")));
        img_head_other.setImage(new Image(getClass().getResourceAsStream("/img/applications-other.png")));
        img_head_wipe.setImage(new Image(getClass().getResourceAsStream("/img/eraser.png")));
        img_head_flash_from_recovery.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_bat.png")));
        img_head_backup_from_recovery.setImage(new Image(getClass().getResourceAsStream("/img/backups.png")));
        img_head_recovery_other.setImage(new Image(getClass().getResourceAsStream("/img/applications-other.png")));
        img_recovery_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
        img_head_fileoperation_recovery.setImage(new Image(getClass().getResourceAsStream("/img/file_extension_bin.png")));
        img_head_backup_from_recovery_restore.setImage(new Image(getClass().getResourceAsStream("/img/site_backup_and_restore.png")));


         holdSplitPaneDivider(img_head_fileoperation, img_head_application, img_head_backup, img_head_flash, img_head_unlocking,
         img_head_other,img_head_flash_from_recovery, img_head_backup_from_recovery,img_head_recovery_other,img_head_fileoperation_recovery);
         //img_head_wipe



        /** Set simple mode **/
        if(tab_settings_others_chk_simplemode.isSelected()){
            SIMPLEMODE=true;}
        /** Set default bin directory **/
        tab_settings_tool_set_txt_tool_directory_browse.setText(PLATFORM_TOOLS_DIRECTORY+"/bin");

        mScene = top_tab_pane.getScene();
        initDragAndDrop();

        setPlatform();
        setBinaries();

        if(!checkAdbBin(new File(ADB_BINARY)) || !checkFastbootBin(new File(FASTBOOT_BINARY))) {
            if (showDialogConfirmation("Binaries", "ADB and FASTBOOT", "Can't locate adb or fastboot binaries, unpack built-in?")) {
                unpackBuildInBinaryDialog();
            }
        }
    }
    private void initSetOnDragOver(Node node, int dndType) {
        node.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        node.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath;
                    for (File file : db.getFiles()) {
                        filePath = file.getAbsolutePath();
                        logToConsole("drag and drop: " + filePath);
                        if (dndType == DNDTYPE_OPEN_DFS) {
                            runDfs(filePath);
                        } else if (dndType == DNDTYPE_SEND_FILE){
                            dfsAdbFilePush(filePath);
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }
    private void initDragAndDrop() {
        initSetOnDragOver(mScene.getRoot(),DNDTYPE_OPEN_DFS);
        initSetOnDragOver(tab_adb_sendfile,DNDTYPE_SEND_FILE);
        initSetOnDragOver(tab_recovery_sendfile,DNDTYPE_SEND_FILE);
    }
    /** Buttons initialize **/
    public void initToggleBtn() {
        tab_settings_override_btn_fastboot_override.setOnAction((event) -> setBinaries());
        tab_settings_override_btn_adb_override.setOnAction((event) -> setBinaries());
        tab_settings_override_btn_mfastboot_override.setOnAction((event) -> setBinaries());
    }

    private void runDfs(String dfsfilepath){
        try {
            File dfsFile;
            if(dfsfilepath==null){
                dfsFile = fileChooserAdv("Select *.dfs script file");
            } else {
                dfsFile = new File(dfsfilepath);
            }

            if (dfsFile == null) {
                throw new NullPointerException("Nothing selected");
            }
            File dir;

            if (!SIMPLEMODE){
                dir = directoryChooserAdv("Select working directory (with images)");
            } else {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("YY.MM.DD-hh-mm-ss");
                String time = dateFormat.format(now);
                dir = new File(System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"DroidFlasher"+File.separator+dfsFile.getName()+"_"+time);
                if(!dir.mkdirs()){throw new NullPointerException("Can't make dir: "+dir.getPath());}
            }

            if(dir!=null){
                new Thread(() -> {
                    try {
                        Platform.runLater(() -> showDialogInformationGlobal("DFS", "Operation in progress", "Running *.dfs script " + dfsFile.getName() + "\n\nPlease wait...\n"));
                        String dfsContent = readFileToString(dfsFile.getPath(), Charset.defaultCharset());
                        String[] cmd_lines = dfsContent.split("\n");
                        for (String args : cmd_lines){
                            //String[] commands = args.split(" ");
                            List<String> list = parseStringToArray(args);
                            String[] commands = list.toArray(new String[list.size()]);

                            int last = commands.length-1;
                            if(last>=0) {
                                if (commands[0].matches("fastboot|adb|mfastboot")) {

                                    final String cmdToConsole = Arrays.toString(commands).replaceAll("[,]", "");
                                    Platform.runLater(() -> {
                                        if (global_alert != null) {
                                            global_alert_text_area.appendText("exec: " + cmdToConsole + ":\n");
                                        }
                                    });

                                    switch (commands[0]) {
                                        case "fastboot":
                                            commands[0] = FASTBOOT_BINARY;
                                            break;
                                        case "adb":
                                            commands[0] = ADB_BINARY;
                                            break;
                                        case "mfastboot":
                                            commands[0] = MFASTBOOT_BINARY;
                                            break;
                                    }
                                    if (commands[1].equals("flash") || commands[1].equals("boot") || commands[1].equals("sideload")) {
                                        if (!new File(commands[last]).exists()) {
                                            commands[last] = dir.getPath() + "/" + commands[last];
                                        }
                                    }
                                    if (commands[1].equals("push")) {
                                        if (!new File(commands[2]).exists()) {
                                            commands[2] = dir.getPath() + "/" + commands[2];
                                        }
                                    }
                                    if (commands[1].equals("pull")) {
                                        if (!new File(commands[3]).exists()) {
                                            commands[3] = dir.getPath() + "/" + commands[3];
                                        }
                                    }
                                    runCmdToGlobalAlert(commands);
                                } else if (commands[0].equals("dfs")) {
                                    /** TODO: DFS commands fabric, need to implement more commands **/
                                    switch (commands[1]) {
                                        case "download":
                                            /** dfs download http://images.org/moto/boot.img **/
                                            URL remotefile = new URL(commands[2]);

                                            String fileName = remotefile.getFile();
                                            fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());

                                            Download dld = new Download(remotefile, dir.getPath());
                                            new Thread(dld::run);
                                        /*
                                        ReadableByteChannel rbc = Channels.newChannel(remotefile.openStream());
                                        FileOutputStream fos = new FileOutputStream(dir + "/" + fileName);
                                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);*/
                                            while (dld.getStatus() == Download.DOWNLOADING) {
                                                double dl = Double.parseDouble(String.valueOf(dld.getProgress()));
                                                Platform.runLater(() -> tab_adb_progressbar.setProgress(dl));
                                                try {
                                                    Thread.sleep(100);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            if (dld.getStatus() == Download.ERROR) {
                                                Platform.runLater(() -> showDialog(Alert.AlertType.ERROR,"Ooops!", "Download error.", "Try later."));
                                                break;
                                            } else if (dld.getStatus() == Download.COMPLETE) {
                                                Platform.runLater(() -> tab_adb_progressbar.setProgress(dld.getProgress()));
                                            }
                                            break;
                                        case "radio":
                                            /** dfs radiobox TWRP-2.8.5.0|PhilZ-Touch-6.58.7 **/
                                            break;
                                        case "checkbox":
                                            /** dfs checkbox TWRP-2.8.5.0|PhilZ-Touch-6.58.7 **/
                                            break;
                                    }
                                }
                            }
                        }
                        Platform.runLater(() -> {
                            if(global_alert!=null){
                                global_alert_text_area.appendText("---\n" + "ALL DFS OPERATION COMPLETE.\n"+"---");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();}
        } catch (Exception e) {
            logToConsole(e.getMessage());
            logToGlobalAlert(e.getMessage());
        }
    }
    /** ADB Commands **/
    private void dfsAdbCheckStatus(ImageView status_image, Label status_label){
            String adb_devices_output = runCmd(ADB_BINARY, "devices", "-l");
            String[] finder = adb_devices_output.split("\n");
            if (!finder[finder.length - 1].equals("List of devices attached ")) {
                status_image.setImage(new Image(getClass().getResourceAsStream("/img/bullet_green.png")));
                String[] device_info = finder[finder.length - 1].split("\\s+");
                showDialog(Alert.AlertType.INFORMATION,"Success!", "Adb device detected.", "Device information is: " + device_info[0]);

                status_label.setText(device_info[device_info.length-2] + " " + device_info[device_info.length-1] + " " + device_info[device_info.length-3]);
            } else {
                status_image.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
                showDialog(Alert.AlertType.ERROR,"Ooops!", "Adb device not detected.", "Try to reconnect.");
                status_label.setText("No device detected.");
            }
    }
    private void dfsAdbKillServer(){
        runCmd(ADB_BINARY,"kill-server");
        showDialog(Alert.AlertType.INFORMATION,"Adb","Operation complete","Command kill-server sended to adb.");
    }
    private void dfsAdbStartServer(){
        runCmd(ADB_BINARY,"start-server");
        showDialog(Alert.AlertType.INFORMATION,"Adb","Operation complete","Command start-server sended to adb.");
    }
    private void dfsAdbRebootDeviceNormal(){
            runCmd(ADB_BINARY, "reboot");
            showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Reboot command sended to device.");
    }
    private void dfsAdbRebootDeviceToRecovery(){
            runCmd(ADB_BINARY, "reboot", "recovery");
            showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Command \"reboot to recovery\" sended to device.");
    }
    private void dfsAdbRebootDeviceToBootloader(){
            runCmd(ADB_BINARY, "reboot", "bootloader");
            showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Command \"reboot to bootlader\" sended to device.");
    }
    private void dfsAdbFilePush(String localfilepath){
        try {
            File localfile;
            String remotefile;

            if(localfilepath==null){
                localfile = fileChooser();
                remotefile = remotePushSetPath(localfile.getName());
            } else {
                localfile = new File(localfilepath);
                remotefile = remotePushSetPath(localfile.getName());
                //remotefile = "/sdcard/" + localfile.getName();
            }

            if (!remotefile.equals("")) {
                new Thread(() -> {
                        runCmdAdbPushPull(tab_adb_progressbar, ADB_BINARY, "push", "-p", localfile.getPath(), remotefile);
                        Platform.runLater(() -> logToConsole("File " + localfile.getName() + " pushed to remote path " + remotefile));
                }).start();
            }
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsAdbFilePull(){
        try {
            String remotefile = remotePullSetPath("test.zip");
            if (!remotefile.equals("")) {
                File localfile = fileSaver();
                if(localfile!=null){
                    new Thread(() -> {
                            runCmdAdbPushPull(tab_adb_progressbar, ADB_BINARY, "pull", "-p", remotefile, localfile.getPath());
                            Platform.runLater(() -> logToConsole("File " + localfile.getName() + " pulled from remote path " + remotefile));
                    }).start();}
            }
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsAdbInstallApp(){
        try{
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        String log;
                        if(getInstallArgs(false)==null) {
                            log=runCmd(ADB_BINARY, "install", localfile.getPath());
                        } else {
                            log=runCmd(ADB_BINARY, "install", getInstallArgs(false), localfile.getPath());
                        }
                        Platform.runLater(() -> tab_adb_progressbar.setProgress(1.0));

                        Platform.runLater(() -> {
                            if(log.contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES")){
                                showDialog(Alert.AlertType.ERROR,"Error", "Installation FAIL!", "Selected apk is not signed!");
                            } else if (log.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
                                showDialog(Alert.AlertType.ERROR,"Error", "Installation FAIL!", "Application exist! Use replace option to overwrite.");
                            } else if (log.contains("Failure")) {
                                showDialog(Alert.AlertType.ERROR,"Error", "Installation FAIL!", "Can't install APK, see console for detail.");
                            } else {
                                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "File " + localfile.getName() + " installed on device.");
                            }
                        });
                }).start();}
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsAdbInstallMultipleApp(){
        try{
            List<File> localfiles = fileChooserMultiple();
            if(localfiles!=null){
                new Thread(() -> {
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

                            showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete",
                                    "Success: " + success
                                            + "\nFailure: " + failure + "\n"
                                            + "\nSee Console for detail." + "\n"
                                            + "\nFile list:\n" + Arrays.asList(localfiles).toString().replace(", ", "\n").replace("[", "").replace("]", ""));
                        });
                }).start();}
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsAdbUninstallApp(){
        try {
            String packagename = setUninstallPackage("com.zlab.datFM");
            if(!packagename.equals("")){
                new Thread(() -> {
                        final String finalLog = runCmd(ADB_BINARY, "uninstall", packagename);
                        Platform.runLater(() -> {
                            //int failure = (finalLog.length() - finalLog.substring(0).replaceAll("Failure", "").length())/7;
                            int success = (finalLog.length() - finalLog.substring(0).replaceAll("Success","").length())/7;

                            if(success==1){
                                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Package " + packagename + " uninstalled from device,");
                            } else {
                                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation not complete", "Can't uninstall " + packagename + ", see Console for detail.");
                            }
                            /** FOR THE NEXT RELEASE (MULTIPLE UNINSTALLER)
                             showDialogInformation("adb", "Operation complete",
                             "Success: "+success
                             +"\nFailure: "+failure+"\n"
                             +"\nSee Console for detail."+"\n");*/
                        });
                }).start();}
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsAdbUninstallAppKeepData(){

        boolean keep = showDialogConfirmation("WARNING!", "PLEASE READ CAREFULLY!", "The -k option uninstalls the application while retaining the data/cache.\n" +
                "\" +\n" +
                "                        \"At the moment, there is no way to remove the remaining data.\\n\" +\n" +
                "                        \"You will have to reinstall the application with the same signature, and fully uninstall it.\\n\" +\n" +
                "                        \"If you truly wish to continue, enter package name on next screen, for example com.zlab.datFM");

        if (keep){
            String packagename = setUninstallPackage("com.zlab.datFM");
            if(!packagename.equals("")){
                new Thread(() -> {
                        final String finalLog = runCmd(ADB_BINARY, "shell", "pm", "uninstall", "-k", packagename);
                        Platform.runLater(() -> {
                            //int failure = (finalLog.length() - finalLog.substring(0).replaceAll("Failure", "").length())/7;
                            int success = (finalLog.length() - finalLog.substring(0).replaceAll("Success","").length())/7;

                            if(success==1){
                                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Package " + packagename + " uninstalled from device,");
                            } else {
                                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation not complete", "Can't uninstall " + packagename + ", see Console for detail.");
                            }
                            /** FOR THE NEXT RELEASE (MULTIPLE UNINSTALLER)
                             showDialogInformation("adb", "Operation complete",
                             "Success: "+success
                             +"\nFailure: "+failure+"\n"
                             +"\nSee Console for detail."+"\n");*/
                        });
                }).start();}
        }// else {}
    }
    private void dfsAdbBackup(){
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

                showDialog(Alert.AlertType.INFORMATION,"Backup", "Attention", "Now:\n-unlock your device\n-press OK in this window\n-confirm the backup operation on the phone.\n\n"
                        + "Please don't close window on the phone.");
                new Thread(() -> {
                    try {
                        runCmd(commands);
                        Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION,"Backup", "Operation complete", "All done. Backup file stored at " + localfile.getPath() + "."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();}
        } else {
            Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION,"Backup", "Incorrect operation", "SHARED or ALL must be selected."));
        }
    }
    private void dfsAdbRestore(){
        try {
            File localfile = fileChooser();
            if (localfile !=null) {
                showDialog(Alert.AlertType.INFORMATION,"Restore", "Attention", "Now:\n-unlock your device\n-press OK in this window\n-confirm restore operation on the phone.\n\n"
                        + "Please don't close window on the phone.");
                new Thread(() -> {
                        runCmd(ADB_BINARY, "restore", localfile.getPath());
                        Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION,"Restore", "Operation complete", "File " + localfile.getName() + " restored. See Console for detail."));
                }).start();
            }
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsAdbCheckBackupAll(){
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
    }

    public void initBtn() {
        /*********/
        /** ADB **/
        /** Check device **/
        tab_adb_btn_check_device.setOnAction((event) -> dfsAdbCheckStatus(img_adb_status, tab_adb_device_status_txt));
        /** Server kill start **/
        tab_adb_btn_server_kill.setOnAction((event) -> dfsAdbKillServer());
        tab_adb_btn_server_start.setOnAction((event) -> dfsAdbStartServer());
        /** Console **/
        tab_adb_btn_console.setOnAction((event) -> openConsole(ADB_BINARY, "Adb"));
        /** Reboot device **/
        tab_adb_btn_reboot_device.setOnAction((event) -> dfsAdbRebootDeviceNormal());
        tab_adb_btn_reboot_recovery.setOnAction((event) -> dfsAdbRebootDeviceToRecovery());
        tab_adb_btn_reboot_bootloader.setOnAction((event) -> dfsAdbRebootDeviceToBootloader());
        tab_adb_btn_run_dfs.setOnAction((event) -> runDfs(null));
        /** File control **/
        tab_adb_btn_file_push.setOnAction((event) -> dfsAdbFilePush(null));
        tab_adb_btn_file_pull.setOnAction((event) -> dfsAdbFilePull());
        /** Application **/
        tab_adb_btn_install.setOnAction((event) -> dfsAdbInstallApp());
        tab_adb_btn_install_multiple.setOnAction((event) -> dfsAdbInstallMultipleApp());
        tab_adb_btn_uninstall.setOnAction((event) -> dfsAdbUninstallApp());
        tab_adb_btn_uninstall_keep_data.setOnAction((event) -> dfsAdbUninstallAppKeepData());
        /** Backup **/
        tab_adb_btn_backup.setOnAction((event) -> dfsAdbBackup());
        tab_adb_btn_restore.setOnAction((event) -> dfsAdbRestore());
        tab_adb_chk_backup_all.setOnAction((event) -> dfsAdbCheckBackupAll());

        /**************/
        /** FASTBOOT **/
        tab_fastboot_btn_check_device.setOnAction((event) -> {
                String fastboot_devices_output = runCmd(FASTBOOT_BINARY, "devices");
                if (!fastboot_devices_output.equals("")) {
                    String[] device_info = fastboot_devices_output.split("\t");
                    img_fastboot_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_green.png")));
                    showDialog(Alert.AlertType.INFORMATION,"Success!", "Fastboot device detected.", "Device information is: " + device_info[0] + " " + device_info[1]);
                    tab_fastboot_device_status_txt.setText(device_info[0]+" "+device_info[1]);
                } else {
                    img_fastboot_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
                    showDialog(Alert.AlertType.ERROR,"Ooops!", "Fastboot device not detected.", "Try to reconnect.");
                    tab_fastboot_device_status_txt.setText("No device detected.");
                }
        });
        tab_fastboot_btn_reboot.setOnAction((event) -> {
                runCmd(FASTBOOT_BINARY, "reboot");
                showDialog(Alert.AlertType.INFORMATION,"fastboot", "Operation complete", "Command \"reboot\" sended to device.");
        });
        tab_fastboot_btn_reboot_bootloader.setOnAction((event) -> {
                runCmd(FASTBOOT_BINARY, "reboot-bootloader");
                showDialog(Alert.AlertType.INFORMATION,"fastboot", "Operation complete", "Command \"reboot-bootloader\" sended to device.");
        });
        tab_fastboot_btn_run_dfs.setOnAction((event) -> runDfs(null));
        tab_fastboot_btn_flash_recovery.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash recovery " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "recovery", localfile.getPath());
                }).start();
        }});
        tab_fastboot_btn_flash_boot.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash boot " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "boot", localfile.getPath());
                }).start();
            }});
        tab_fastboot_btn_flash_cache.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash cache " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "cache", localfile.getPath());
                }).start();
            }});
        tab_fastboot_btn_flash_userdata.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash userdata " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "userdata", localfile.getPath());
                }).start();
            }});
        tab_fastboot_btn_flash_system.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash system " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "system", localfile.getPath());
                }).start();
            }});
        tab_fastboot_btn_flash_radio.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash radio " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "radio", localfile.getPath());
                }).start();
            }});
        tab_fastboot_btn_boot_img.setOnAction((event) -> {
            File localfile = fileChooser();
            if (localfile != null) {
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to boot " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(FASTBOOT_BINARY, "boot", localfile.getPath());
                }).start();
            }
        });
        tab_fastboot_btn_flash_mfastboot_system.setOnAction((event) -> {
            File localfile = fileChooser();
            if(localfile!=null){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("mfastboot", "Operation in progress", "Try to flash system " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(MFASTBOOT_BINARY, "flash", "system", localfile.getPath());
                }).start();
            }});
        tab_fastboot_btn_oem_unlock.setOnAction((event) -> new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem unlock." + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "unlock");
        }).start());
        tab_fastboot_btn_oem_lock.setOnAction((event) -> new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem lock." + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "lock");
        }).start());
        tab_fastboot_btn_oem_get_unlock_data.setOnAction((event) -> new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem get_unlock_data." + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "get_unlock_data");
        }).start());
        tab_fastboot_btn_oem_lock_begin.setOnAction((event) -> new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem lock begin." + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "lock", "begin");
        }).start());
        /** Console **/
        tab_fastboot_btn_console.setOnAction((event) -> openConsole(FASTBOOT_BINARY, "Fastboot"));

        /**************/
        /** RECOVERY **/
        tab_recovery_btn_check_device.setOnAction((event) -> {
                String adb_devices_output = runCmd(ADB_BINARY, "devices", "-l");
                String[] finder = adb_devices_output.split("\n");
                if (!finder[finder.length - 1].equals("List of devices attached ")) {
                    img_recovery_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_green.png")));
                    String[] device_info = finder[finder.length - 1].split("\\s+");
                    showDialog(Alert.AlertType.INFORMATION,"Success!", "Adb device detected.", "Device information is: " + device_info[0]);

                    tab_recovery_device_status_txt.setText(device_info[device_info.length-2] + " " + device_info[device_info.length-1] + " " + device_info[device_info.length-3]);
                } else {
                    img_recovery_status.setImage(new Image(getClass().getResourceAsStream("/img/bullet_red.png")));
                    showDialog(Alert.AlertType.ERROR,"Ooops!", "Adb device not detected.", "Try to reconnect.");
                    tab_recovery_device_status_txt.setText("No device detected.");
                }
        });
        tab_recovery_btn_adb_sideload.setOnAction((event) -> {
            try {
                File localfile = fileChooser();
                    new Thread(() -> {
                            Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Try to sideload " + localfile.getName() + "\n\nPlease wait...\n"));
                            runCmdToGlobalAlert(ADB_BINARY, "sideload", localfile.getPath());
                    }).start();
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_recovery_btn_flash_zip.setOnAction((event) -> {
            try {
                File localfile = fileChooser();
                String remotefile = remotePushSetPath(localfile.getName());
                new Thread(() -> {
                        runCmdAdbPushPull(tab_recovery_progressbar, ADB_BINARY, "push", "-p", localfile.getPath(), remotefile);
                        Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Try to flash " + localfile.getName() + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "install", remotefile);
                }).start();
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_recovery_btn_flash_multizip.setOnAction((event) -> {
            try{
                List<File> localfiles = fileChooserMultiple();
                    new Thread(() -> {
                                for(File f:localfiles){
                                    Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Try to flash multiple *.zip" + "\n\nPlease wait...\n"));
                                    Platform.runLater(() -> {if(global_alert!=null){global_alert_text_area.appendText("exec: push -p "+f.getPath()+" /sdcard/" + f.getName()+"\n");}});
                                    runCmdToGlobalAlert(ADB_BINARY, "push", "-p", f.getPath(), "/sdcard/" + f.getName());
                                    Platform.runLater(() -> {if(global_alert!=null){global_alert_text_area.appendText("exec: shell twrp install"+" /sdcard/" + f.getName()+"\n");}});
                                    runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "install", "/sdcard/" + f.getName());
                                }
                    }).start();
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_recovery_btn_flash_zip_from_sd.setOnAction((event) -> {
                String remotefile = remotePushSetPath("/sdcard/flash.zip");
                if(!remotefile.equals("")){
                    new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Try to flash " + remotefile + "\n\nPlease wait...\n"));
                        runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "install", remotefile);
                }).start();} else {logToConsole("No remote path selected.");}
        });
        tab_recovery_btn_file_push.setOnAction((event) -> {
            try {
                File localfile = fileChooser();
                String remotefile = remotePushSetPath(localfile.getName());

                if (!remotefile.equals("")) {
                    new Thread(() -> {
                            runCmdAdbPushPull(tab_adb_progressbar, ADB_BINARY, "push", "-p", localfile.getPath(), remotefile);
                            Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "File " + localfile.getName() + " pushed to remote path " + remotefile));
                    }).start();
                }
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_recovery_btn_file_pull.setOnAction((event) -> {
            try {
                String remotefile = remotePullSetPath("test.zip");
                if (!remotefile.equals("")) {
                    File localfile = fileSaver();
                    if(localfile!=null){
                        new Thread(() -> {
                                runCmdAdbPushPull(tab_adb_progressbar, ADB_BINARY, "pull", "-p", remotefile, localfile.getPath());
                                Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "File " + localfile.getName() + " pulled from remote path " + remotefile));
                        }).start();}
                }
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_recovery_btn_wipe.setOnAction((event) -> {
            if(tab_recovery_chk_wipe_cache.isSelected() || tab_recovery_chk_wipe_data.isSelected() || tab_recovery_chk_wipe_dalvik.isSelected()){
                new Thread(() -> {
                        Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "wipe\n\nPlease wait...\n"));
                        if(tab_recovery_chk_wipe_cache.isSelected()){
                            Platform.runLater(() -> {if(global_alert!=null){global_alert_text_area.appendText("exec: wipe cache\n");}});
                            runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "wipe", "cache");}
                        if(tab_recovery_chk_wipe_data.isSelected()){
                            Platform.runLater(() -> {if(global_alert!=null){global_alert_text_area.appendText("exec: wipe data\n");}});
                            runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "wipe", "data");}
                        if(tab_recovery_chk_wipe_dalvik.isSelected()){
                            Platform.runLater(() -> {if(global_alert!=null){global_alert_text_area.appendText("exec: wipe dalvik\n");}});
                            runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "wipe", "dalvik");}
                }).start();
            }
        });
        tab_recovery_btn_run_dfs.setOnAction((event) -> runDfs(null));
        tab_recovery_btn_console.setOnAction((event) -> openConsole(ADB_BINARY, "Recovery"));
        tab_recovery_btn_reboot_device.setOnAction((event) -> {
                runCmd(ADB_BINARY, "reboot");
                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Reboot command sended to device.");
        });
        tab_recovery_btn_reboot_recovery.setOnAction((event) -> {
                runCmd(ADB_BINARY, "reboot", "recovery");
                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Command \"reboot to recovery\" sended to device.");
        });
        tab_recovery_btn_reboot_bootloader.setOnAction((event) -> {
                runCmd(ADB_BINARY, "reboot", "bootloader");
                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Command \"reboot to bootlader\" sended to device.");
        });
        tab_recovery_btn_server_kill.setOnAction((event) -> {
                runCmd(ADB_BINARY, "kill-server");
                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Command kill-server sended to adb.");
        });
        tab_recovery_btn_server_start.setOnAction((event) -> {
                runCmd(ADB_BINARY, "kill-server");
                showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete", "Command kill-server sended to adb.");
        });
        tab_recovery_btn_backup.setOnAction((event) -> {
            String args="";
            if (tab_recovery_chk_backup_system.isSelected())       {args=args+"S";}
            if (tab_recovery_chk_backup_data.isSelected())         {args=args+"D";}
            if (tab_recovery_chk_backup_cache.isSelected())        {args=args+"C";}
            if (tab_recovery_chk_backup_recovery.isSelected())     {args=args+"R";}
            if (tab_recovery_chk_backup_boot.isSelected())         {args=args+"B";}
            if (tab_recovery_chk_backup_asec.isSelected())         {args=args+"A";}
            if (tab_recovery_chk_backup_sdext.isSelected())        {args=args+"E";}
            if (tab_recovery_chk_backup_compression.isSelected())  {args=args+"O";}
            if (tab_recovery_chk_backup_nomd5.isSelected())        {args=args+"M";}
            if(!args.equals("")){
                /** http://www.teamw.in/OpenRecoveryScript - backup SDCR123BAEOM foldername */
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss");
                Date date = new Date();
                String backupname = remoteBackupSetName(dateFormat.format(date));
                if(!backupname.equals("")) {
                        final String finalArgs = args;
                        new Thread(() -> {
                                    Platform.runLater(() -> {
                                        showDialogInformationGlobal("recovery", "Operation in progress", "Backup " + finalArgs + " "+ backupname+ "\n\nPlease wait...\n");
                                        if (global_alert != null) {
                                            global_alert_text_area.appendText("exec: shell twrp backup " + finalArgs + " " + backupname+ "\n");
                                        }
                                    });
                                    runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "backup", finalArgs, backupname);
                        }).start();
                } else {
                    logToConsole("No remote path selected.");
                }}
        });
        tab_recovery_btn_restore.setOnAction((event) -> {
            String args="";
            if (tab_recovery_chk_backup_system.isSelected())       {args=args+"S";}
            if (tab_recovery_chk_backup_data.isSelected())         {args=args+"D";}
            if (tab_recovery_chk_backup_cache.isSelected())        {args=args+"C";}
            if (tab_recovery_chk_backup_recovery.isSelected())     {args=args+"R";}
            if (tab_recovery_chk_backup_boot.isSelected())         {args=args+"B";}
            if (tab_recovery_chk_backup_asec.isSelected())         {args=args+"A";}
            if (tab_recovery_chk_backup_sdext.isSelected())        {args=args+"E";}
            if (tab_recovery_chk_backup_nomd5.isSelected())        {args=args+"M";}
            if(!args.equals("")){
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH-mm-ss");
                Date date = new Date();
                String backuppath = remoteRestoreSetName(dateFormat.format(date));
                if(!backuppath.equals("")){
                        final String finalArgs = args;
                        new Thread(() -> {
                                    Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Restore " + backuppath + " "+ finalArgs + "\n\nPlease wait...\n"));
                                    Platform.runLater(() -> {
                                        if (global_alert != null) {
                                            global_alert_text_area.appendText("exec: shell twrp restore " + backuppath + " " + finalArgs+ "\n");
                                        }
                                    });
                                    runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "restore", backuppath, finalArgs);
                        }).start();
                }
            }
         });

        /**
        tab_recovery_btn_mount;
        tab_recovery_btn_mkdir;
        tab_recovery_btn_chmod;
        tab_recovery_btn_off_zip_verify;
        **/

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
                    showDialog(Alert.AlertType.ERROR,"DroidFlasher","Can't find binaries.",binaries+" binary not found.");
                }
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        /** Tools bin override **/
        tab_settings_override_btn_fastboot_browse.setOnAction((event) -> {
            try {
                File fastboot = fileChooser();
                if (checkFastbootBin(fastboot)) {
                    tab_settings_override_txt_fastboot_path.setText(fastboot.getPath());
                } else {
                    showDialog(Alert.AlertType.ERROR, "DroidFlasher", "Can't find binaries.", "fastboot binary not found.");
                }
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_settings_override_btn_adb_browse.setOnAction((event) -> {
            try {
                File adb = fileChooser();
                if (checkAdbBin(adb)) {
                    tab_settings_override_txt_adb_path.setText(adb.getPath());
                } else {
                    showDialog(Alert.AlertType.ERROR, "DroidFlasher", "Can't find binaries.", "adb binary not found.");
                }
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        tab_settings_override_btn_mfastboot_browse.setOnAction((event) -> {
            try {
                File mfastboot = fileChooser();
                if (checkMFastbootBin(mfastboot)) {
                    tab_settings_override_txt_mfastboot_path.setText(mfastboot.getPath());
                } else {
                    showDialog(Alert.AlertType.ERROR, "DroidFlasher", "Can't find binaries.", "mfastboot binary not found.");
                }
            } catch (Exception e) {
                logToConsole(e.getMessage());
            }
        });
        /** Reinitialize **/
        tab_settings_others_btn_reinitialize.setOnAction((event) -> {
            tab_main_txt_area_log.appendText("Reinitialize inventory...\n");
            /** РЕИНИЦИАЛИЗАЦИЯ **/
            tab_main_txt_area_log.appendText("done...\n");
        });
        tab_settings_override_btn_unpack_binaries.setOnAction((event) -> unpackBuildInBinaryDialog());
        /** Simple mode **/
        tab_settings_others_chk_simplemode.setOnAction((event) -> SIMPLEMODE = tab_settings_others_chk_simplemode.isSelected());
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
    private String remoteBackupSetName(String filename) {
        if(getDeviceID()!=null){
        TextInputDialog dialog = new TextInputDialog(filename);
        dialog.setTitle("Set remote backup name");
        dialog.setHeaderText("Backup will be stored in /sdcard/TWRP/BACKUPS/" + getDeviceID()+"/*");
        dialog.setContentText("Backup name:");

        Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                return result.get();
            } else {
                return "";
            }
        } else {return "";}
    }
    private String remoteRestoreSetName(String filename) {
        if(getDeviceID()!=null){
        TextInputDialog dialog = new TextInputDialog("/sdcard/TWRP/BACKUPS/"+getDeviceID()+"/"+filename);
        dialog.setTitle("Set backup folder");
        dialog.setHeaderText("Set path to backup folder (fully qualified with /sdcard/TWRP/"+getDeviceID()+"/* etc.)");
        dialog.setContentText("Backup path:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
        } else {return "";}
    }

    /** File chooser **/
    private File directoryChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(Main.globalStage);
    }
    private File directoryChooserAdv(String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(Main.globalStage);
    }
    private File fileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showOpenDialog(Main.globalStage);
    }
    private File fileChooserAdv(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showOpenDialog(Main.globalStage);
    }
    private List<File> fileChooserMultiple() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showOpenMultipleDialog(Main.globalStage);
    }
    private File fileSaver() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select new file");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showSaveDialog(Main.globalStage);
    }
    private File directorySaver() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory for extraction");
        File defaultDirectory = new File(".");
        chooser.setInitialDirectory(defaultDirectory);
        return chooser.showDialog(Main.globalStage);
    }

    /** Show Dialog **/
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
        global_alert_text_area.setWrapText(false);

        global_alert_text_area.setMaxWidth(Double.MAX_VALUE);
        global_alert_text_area.setMaxHeight(Double.MAX_VALUE);
        global_alert_text_area.setScrollTop(Double.MAX_VALUE);

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
    private void showDialog(Alert.AlertType alertType, String title, String header, String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
    private boolean showDialogConfirmation(String title, String header, String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /** Check inventory **/
    private boolean checkAdbBin(File f) {
        return f.exists();
    }
    private boolean checkFastbootBin(File f) {
        return f.exists();
    }
    private boolean checkMFastbootBin(File f) {
        return f.exists();
    }

    /** Console **/
    private String runCmd(String... args) {
        String locallog = "";
        try {
            Process proc = Runtime.getRuntime().exec(args);

            logToConsole(Arrays.toString(args));
            logToGlobalAlert(Arrays.toString(args));

            InputStream errStream = proc.getErrorStream();
            InputStreamReader errStreamReader = new InputStreamReader(errStream);
            BufferedReader errBufferedReader = new BufferedReader(errStreamReader);

            InputStream stdStream = proc.getInputStream();
            InputStreamReader stdStreamReader = new InputStreamReader(stdStream);
            BufferedReader stdBufferedReader = new BufferedReader(stdStreamReader);

            String err;
            String std;

            while ((err = errBufferedReader.readLine()) != null) {
                logToGlobalAlert(err);
                locallog = locallog + err + "\n";
                logToConsole(err);
            }

            while ((std = stdBufferedReader.readLine()) != null) {
                logToGlobalAlert(std);
                locallog = locallog + std + "\n";
                logToConsole(std);
            }
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            logToGlobalAlert(e.getMessage());
            locallog = locallog + e.getMessage() + "\n";
            logToConsole(e.getMessage());
        }
        return locallog;
    }
    private void runCmdToGlobalAlert(String... args) {
        try {
        Process proc = Runtime.getRuntime().exec(args);

        InputStream errStream = proc.getErrorStream();
        InputStreamReader errStreamReader = new InputStreamReader(errStream);
        BufferedReader errBufferedReader = new BufferedReader(errStreamReader);

        InputStream stdStream = proc.getInputStream();
        InputStreamReader stdStreamReader = new InputStreamReader(stdStream);
        BufferedReader stdBufferedReader = new BufferedReader(stdStreamReader);

        String err;
        String std;

        while ((err = errBufferedReader.readLine()) !=null) {
            logToGlobalAlert(err);
        }

        while ((std = stdBufferedReader.readLine()) !=null) {
            logToGlobalAlert(std);
        }
            proc.waitFor();
        } catch (Exception e) {
            logToGlobalAlert(e.getMessage());
            logToConsole(e.getMessage());
        }
    }
    private void runCmdAdbPushPull(ProgressBar progressbar, String... args) {
        try {
        Process proc = Runtime.getRuntime().exec(args);
        InputStream inputStream = proc.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String s;

        while ((s = bufferedReader.readLine()) != null) {
            if (s.startsWith("Transferring")) {
                Double progress = Double.parseDouble(s.split("\\(")[1].split("%")[0]) / 100;
                Platform.runLater(() -> progressbar.setProgress(progress));
            } else {
                logToConsole("err:" + s);
            }
        }
            proc.waitFor();
        } catch (Exception e) {
            logToGlobalAlert(e.getMessage());
            logToConsole(e.getMessage());
        }
    }
    private void logToGlobalAlert(String msg){
        Platform.runLater(() -> {
            if (global_alert != null) {
                global_alert_text_area.appendText(msg + "\n");
            }
        });
    }
    private String getDeviceID(){
            String adb_devices_output = runCmd(ADB_BINARY, "devices", "-l");
            String[] finder = adb_devices_output.split("\n");
            if (!finder[finder.length - 1].equals("List of devices attached ")) {
                String[] device_info = finder[finder.length - 1].split("\\s+");
                return device_info[0];
            } else {return null;}
    }
    private void openConsole(String binary, String type){

        URL MainUIlocation = getClass().getResource("/layout/Console.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(MainUIlocation);

        Controller mController = new Controller();
        mController.initialize(getClass().getResource("/layout/Console.fxml"), fxmlLoader.getResources());
        fxmlLoader.setController(mController);

        Parent root = null;
        try { root = fxmlLoader.load();
        } catch (IOException e) { e.printStackTrace(); }

        Scene consoleScene = null;
        if (root != null) {
            consoleScene = new Scene(root);
        /* Console UI*/
            Button console_btn_send = (Button) consoleScene.lookup("#console_btn_send");
            console_text_input = (TextField) consoleScene.lookup("#console_text_input");
            console_text_output = (TextArea) consoleScene.lookup("#console_text_output");
            console_text_output.setScrollTop(Double.MAX_VALUE);

            console_btn_send.setOnAction((event) -> {
                String[] input = console_text_input.getText().split(" ");
                //if (input.length>0){
                String[] args = new String[input.length + 1];

                args[0] = binary;
                System.arraycopy(input, 0, args, 1, input.length);
                new Thread(() -> {
                        runCmdToConsoleOutput(console_text_output, args);
                }).start();
                //}
            });
        }
        Stage consoleStage = new Stage();
        consoleStage.setTitle(type +" console");
        consoleStage.setScene(consoleScene);

        consoleStage.show();
    }
    private void runCmdToConsoleOutput(TextArea output, String... args) {
        try {
        Process proc = Runtime.getRuntime().exec(args);

        InputStream errStream = proc.getErrorStream();
        InputStreamReader errStreamReader = new InputStreamReader(errStream);
        BufferedReader errBufferedReader = new BufferedReader(errStreamReader);

        InputStream stdStream = proc.getInputStream();
        InputStreamReader stdStreamReader = new InputStreamReader(stdStream);
        BufferedReader stdBufferedReader = new BufferedReader(stdStreamReader);

        String err;
        String std;

        while ((err = errBufferedReader.readLine()) !=null) {
            final String finalErr = err;
            Platform.runLater(() -> {
                if(output!=null){
                    output.appendText(finalErr + "\n");
                }
            });
        }

        while ((std = stdBufferedReader.readLine()) !=null) {
            final String finalStd = std;
            Platform.runLater(() -> {
                if(output!=null){
                    output.appendText(finalStd + "\n");
                }
            });
        }

            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> output.appendText(e.getMessage() + "\n"));
            logToConsole(e.getMessage());
        }
    }
    String readFileToString(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /** LOG **/
    public void logToConsole(String appendString) {
        tab_main_txt_area_log.appendText(appendString+"\n");
    }

    /** Platfrom detection **/
    private void setPlatform(){
        if (isWindows()) {
            PLATFORM_TOOLS=PLATFORM_TOOLS_WIN;
            ADB="adb.exe";
            FASTBOOT="fastboot.exe";
            MFASTBOOT="mfastboot.exe";
        } else if (isMac()) {
            PLATFORM_TOOLS=PLATFORM_TOOLS_MAC;
        } else if (isUnix()) {
            PLATFORM_TOOLS=PLATFORM_TOOLS_NIX;
        } else {
            PLATFORM_TOOLS="binary not found";
        }
    }
    private boolean isWindows() {
        return (OS.contains("win"));
    }
    private boolean isMac() {
        return (OS.contains("mac"));
    }
    private boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
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
        if (tab_settings_override_btn_mfastboot_override.isSelected()) {
            tab_settings_override_txt_mfastboot_path.setDisable(true);
            tab_settings_override_btn_mfastboot_browse.setDisable(true);
            MFASTBOOT_BINARY=tab_settings_override_txt_mfastboot_path.getText();
        } else {
            tab_settings_override_txt_mfastboot_path.setDisable(false);
            tab_settings_override_btn_mfastboot_browse.setDisable(false);
            MFASTBOOT_BINARY=tab_settings_tool_set_txt_tool_directory_browse.getText() + "/"+MFASTBOOT;
        }
    }
    private void unpackBuildInBinaryDialog(){
        File unpack_directory = directorySaver();
        if(unpack_directory!=null){
            unpackBuildInBinary(unpack_directory.getPath());
            tab_settings_override_txt_adb_path.setText(unpack_directory.getPath()+"/"+ADB);
            tab_settings_override_txt_fastboot_path.setText(unpack_directory.getPath()+"/"+FASTBOOT);
            tab_settings_override_txt_mfastboot_path.setText(unpack_directory.getPath()+"/"+MFASTBOOT);

            if(showDialogConfirmation("Unpack binaries", "Operation complete", "Binaries unpacked in  " + unpack_directory.getPath() + ".\n\nOverride adb and fastboot to this binary?")){
                ADB_BINARY=tab_settings_override_txt_adb_path.getText();
                FASTBOOT_BINARY=tab_settings_override_txt_fastboot_path.getText();
                MFASTBOOT_BINARY=tab_settings_override_txt_mfastboot_path.getText();

                tab_settings_override_btn_fastboot_override.setSelected(true);
                tab_settings_override_btn_adb_override.setSelected(true);
                tab_settings_override_btn_mfastboot_override.setSelected(true);
                tab_settings_override_txt_fastboot_path.setDisable(true);
                tab_settings_override_btn_fastboot_browse.setDisable(true);
                tab_settings_override_txt_adb_path.setDisable(true);
                tab_settings_override_btn_adb_browse.setDisable(true);
                tab_settings_override_txt_mfastboot_path.setDisable(true);
                tab_settings_override_btn_mfastboot_browse.setDisable(true);
            }
            if(showDialogConfirmation("Tools directory path", "Configuration", "Use built-in binary path as tools directory?")){
                PLATFORM_TOOLS_DIRECTORY=unpack_directory.getPath();
                tab_settings_tool_set_txt_tool_directory_browse.setText(PLATFORM_TOOLS_DIRECTORY);
            }
        }
    }
    private void unpackBuildInBinary(String directory){
        try {
            extractResource(ADB, directory);
            extractResource(FASTBOOT, directory);
            extractResource(MFASTBOOT, directory);
            if(isWindows()){
                extractResource("AdbWinApi.dll", directory);
                extractResource("AdbWinUsbApi.dll", directory);
            }
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
            logToConsole("Successfully extracted "+dstFile.getAbsolutePath());
        } catch (IOException e) {
            logToConsole("Error on extracting "+dstFile.getAbsolutePath());
        }
        if (dstFile.setExecutable(true)){logToConsole("Set executable "+dstFile.getAbsolutePath());}
        return dstFile.getAbsolutePath();
    }

    /** UI reconfigure **/
    private void holdSplitPaneDivider(Node... objects){
        for (Node obj : objects) {
            try {
                SplitPane splitpane = (SplitPane) obj.getParent().getParent().getParent();
                splitpane.lookupAll(".split-pane-divider").stream()
                        .forEach(div -> div.setMouseTransparent(true));
            } catch (Exception e) {
                logToConsole("Info: Can't set SplitPane properties.");
            }
        }
    }

    public List<String> parseStringToArray(String str){
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
        while (m.find())
            list.add(m.group(1).replace("\"", ""));
        return list;
    }
}