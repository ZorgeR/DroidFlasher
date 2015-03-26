package com.zlab.DroidFlasher;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
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
    private static int APP_VERSION=109;
    public static int TITLE=0;
    public static int HEADER=1;
    public static int CONTENT=2;

    /** Init UI **/
    private Scene mScene;
    @FXML private TabPane top_tab_pane;
    @FXML private Label label_version;

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
    /** Initialize **/
    public void initConfiguration() {
        label_version.setText("r"+APP_VERSION);
        tab_settings_accord.setExpandedPane(tab_settings_tool_set_group);

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
        new Thread(this::isNewVersionAvailable).start();
    }
    /** Drag-n-Drop**/
    private void initDragAndDrop() {
        setOnDragOver(mScene.getRoot(), DNDTYPE_OPEN_DFS);
        setOnDragOver(tab_adb_sendfile, DNDTYPE_SEND_FILE);
        setOnDragOver(tab_recovery_sendfile, DNDTYPE_SEND_FILE);
    }
    private void setOnDragOver(Node node, int dndType) {
        node.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        node.setOnDragDropped(event -> {
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
                    } else if (dndType == DNDTYPE_SEND_FILE) {
                        dfsAdbFilePush(filePath);
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    /** Buttons initialize **/
    public void initToggleBtn() {
        tab_settings_override_btn_fastboot_override.setOnAction((event) -> setBinaries());
        tab_settings_override_btn_adb_override.setOnAction((event) -> setBinaries());
        tab_settings_override_btn_mfastboot_override.setOnAction((event) -> setBinaries());
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
        tab_fastboot_btn_check_device.setOnAction((event) -> dfsFastbootCheckStatus());
        tab_fastboot_btn_reboot.setOnAction((event) -> dfsFastbootReboot());
        tab_fastboot_btn_reboot_bootloader.setOnAction((event) -> dfsFastbootRebootBootloader());
        tab_fastboot_btn_run_dfs.setOnAction((event) -> runDfs(null));
        tab_fastboot_btn_flash_recovery.setOnAction((event) -> dfsFastbootFlashRecovery());
        tab_fastboot_btn_flash_boot.setOnAction((event) -> dfsFastbootFlashBoot());
        tab_fastboot_btn_flash_cache.setOnAction((event) -> dfsFastbootFlashCache());
        tab_fastboot_btn_flash_userdata.setOnAction((event) -> dfsFastbootFlashUserData());
        tab_fastboot_btn_flash_system.setOnAction((event) -> dfsFastbootFlashSystem());
        tab_fastboot_btn_flash_radio.setOnAction((event) -> dfsFastbootFlashRadio());
        tab_fastboot_btn_boot_img.setOnAction((event) -> dfsFastbootBootKernel());
        tab_fastboot_btn_flash_mfastboot_system.setOnAction((event) -> dfsFastbootMfastbootFlashSystem());
        tab_fastboot_btn_oem_unlock.setOnAction((event) -> dfsFastbootOemUnlock());
        tab_fastboot_btn_oem_lock.setOnAction((event) -> dfsFastbootOemLock());
        tab_fastboot_btn_oem_get_unlock_data.setOnAction((event) -> dfsFastbootGetUnlockData());
        tab_fastboot_btn_oem_lock_begin.setOnAction((event) -> dfsFastbootLockBegin());
        /** Console **/
        tab_fastboot_btn_console.setOnAction((event) -> openConsole(FASTBOOT_BINARY, "Fastboot"));

        /**************/
        /** RECOVERY **/
        tab_recovery_btn_check_device.setOnAction((event) -> dfsAdbCheckStatus(img_recovery_status, tab_recovery_device_status_txt));
        tab_recovery_btn_adb_sideload.setOnAction((event) -> dfsRecoverySideload());
        tab_recovery_btn_flash_zip.setOnAction((event) -> dfsRecoveryFlashZip());
        tab_recovery_btn_flash_multizip.setOnAction((event) -> dfsRecoveryFlashMultiZip());
        tab_recovery_btn_flash_zip_from_sd.setOnAction((event) -> dfsRecoveryFlashZipFromSD());
        tab_recovery_btn_file_push.setOnAction((event) -> dfsAdbFilePush(null));
        tab_recovery_btn_file_pull.setOnAction((event) -> dfsAdbFilePull());
        tab_recovery_btn_wipe.setOnAction((event) -> dfsRecoveryWipe());
        tab_recovery_btn_run_dfs.setOnAction((event) -> runDfs(null));
        tab_recovery_btn_console.setOnAction((event) -> openConsole(ADB_BINARY, "Recovery"));
        tab_recovery_btn_reboot_device.setOnAction((event) -> dfsAdbRebootDeviceNormal());
        tab_recovery_btn_reboot_recovery.setOnAction((event) -> dfsAdbRebootDeviceToRecovery());
        tab_recovery_btn_reboot_bootloader.setOnAction((event) -> dfsAdbRebootDeviceToBootloader());
        tab_recovery_btn_server_kill.setOnAction((event) -> dfsAdbKillServer());
        tab_recovery_btn_server_start.setOnAction((event) -> dfsAdbStartServer());
        tab_recovery_btn_backup.setOnAction((event) -> dfsRecoveryBackup());
        tab_recovery_btn_restore.setOnAction((event) -> dfsRecoveryRestore());

        /**
         tab_recovery_btn_mount;
         tab_recovery_btn_mkdir;
         tab_recovery_btn_chmod;
         tab_recovery_btn_off_zip_verify;
         **/

        /**************/
        /** SETTINGS **/
        /** Tools directory select **/  /*TODO - migrate to general browse */
        tab_settings_tool_set_btn_tool_directory_browse.setOnAction((event) -> settingsBrowseSDK());
        /** Tools bin override **/  /*TODO - migrate to general browse */
        tab_settings_override_btn_adb_browse.setOnAction((event) -> settingsBrowseAdb());
        tab_settings_override_btn_fastboot_browse.setOnAction((event) -> settingsBrowseFastboot());
        tab_settings_override_btn_mfastboot_browse.setOnAction((event) -> settingsBrowseMfastboot());
        /** Reinitialize **/
        tab_settings_others_btn_reinitialize.setOnAction((event) -> settingsReinitialize());
        tab_settings_override_btn_unpack_binaries.setOnAction((event) -> unpackBuildInBinaryDialog());
        /** Simple mode **/
        tab_settings_others_chk_simplemode.setOnAction((event) -> SIMPLEMODE = tab_settings_others_chk_simplemode.isSelected());
    }

    /** Update manager **/
    private void isNewVersionAvailable(){
        try {
            final URL url = new URL("http://files.z-lab.me/distr/DroidFlasher/version");
            InputStream i = url.openStream();
            Scanner scan = new Scanner(i);
            String version = scan.nextLine();
            if (APP_VERSION < Integer.parseInt(version)) {
                Platform.runLater(() -> showDialog(Alert.AlertType.INFORMATION,"Update manager", "New version available!", "Please check you preferred forum to get new version: r"+version));
                logToConsole("New version available: "+version);
            } else {
                logToConsole("No updates available.");
            }
        } catch (Exception e){
            logToConsole("Error while check new version.");
        }
    }

    /** DFS Worker **/
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
            final File[] dir = new File[1];

            if (!SIMPLEMODE){
                dir[0] = directoryChooserAdv("Select working directory (with images)");
            } else {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("YY.MM.DD-hh-mm-ss");
                String time = dateFormat.format(now);
                dir[0] = new File(System.getProperty("user.home")+File.separator+"Downloads"+File.separator+"DroidFlasher"+File.separator+dfsFile.getName()+"_"+time);
                if(!dir[0].mkdirs()){throw new NullPointerException("Can't make dir: "+ dir[0].getPath());}
            }

            if(dir[0] !=null){
                new Thread(() -> {
                    try {
                        Platform.runLater(() -> showDialogInformationGlobal("DFS", "Operation in progress", "Running *.dfs script " + dfsFile.getName() + "\n\nPlease wait...\n"));
                        String dfsContent = readFileToString(dfsFile.getPath(), Charset.defaultCharset());
                        String[] cmd_lines = dfsContent.split("\n");
                        String radioboxResult="";
                        boolean showResult=true;
                        final String[] dialogsData = new String[3];

                        for (String args : cmd_lines){
                            if(showResult){

                            args = args.replace("%RADIOBOXRESULT%", radioboxResult);
                            args = args.replace("%SHOWRESULT%",Boolean.toString(true));

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
                                            commands[last] = dir[0].getPath() + "/" + commands[last];
                                        }
                                    }
                                    if (commands[1].equals("push")) {
                                        if (!new File(commands[2]).exists()) {
                                            commands[2] = dir[0].getPath() + "/" + commands[2];
                                        }
                                    }
                                    if (commands[1].equals("pull")) {
                                        if (!new File(commands[3]).exists()) {
                                            commands[3] = dir[0].getPath() + "/" + commands[3];
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

                                            Download dld = new Download(remotefile, dir[0].getPath());
                                            new Thread(dld::run);

                                            while (dld.getStatus() == Download.DOWNLOADING) {
                                                double dl = Double.parseDouble(String.valueOf(dld.getProgress()));
                                                Platform.runLater(() -> tab_adb_progressbar.setProgress(dl));
                                                try {
                                                    Thread.sleep(100);
                                                } catch (InterruptedException e) {
                                                    logToConsole(e.getMessage());
                                                    logToGlobalAlert(e.getMessage());
                                                }
                                            }
                                            if (dld.getStatus() == Download.ERROR) {
                                                Platform.runLater(() -> showDialog(Alert.AlertType.ERROR,"Ooops!", "Download error.", "Try later."));
                                                break;
                                            } else if (dld.getStatus() == Download.COMPLETE) {
                                                Platform.runLater(() -> tab_adb_progressbar.setProgress(dld.getProgress()));
                                            }
                                            break;
                                        case "show":
                                            /**
                                             dfs show %ALERTTYPE% title header content
                                             %ALERTTYPE% = info, error,warning, none, confirmation
                                             **/

                                            Alert.AlertType alertType = Alert.AlertType.INFORMATION;

                                            switch (commands[2]) {
                                                case "info":
                                                    alertType = Alert.AlertType.INFORMATION;
                                                    break;
                                                case "error":
                                                    alertType = Alert.AlertType.ERROR;
                                                    break;
                                                case "warning":
                                                    alertType = Alert.AlertType.WARNING;
                                                    break;
                                                case "none":
                                                    alertType = Alert.AlertType.NONE;
                                                    break;
                                                case "confirmation":
                                                    alertType = Alert.AlertType.CONFIRMATION;
                                                    break;
                                            }

                                            dialogsData[TITLE] = commands[3];
                                            dialogsData[HEADER] = commands[4];
                                            dialogsData[CONTENT] = commands[5];

                                            final Alert.AlertType alertTypeFinal = alertType;

                                            FutureTask show = new FutureTask<>(() -> showDialogForResult(alertTypeFinal, dialogsData[TITLE], dialogsData[HEADER], dialogsData[CONTENT]));
                                            Platform.runLater(show);

                                            try {
                                                showResult = (boolean) show.get();
                                            } catch (InterruptedException | ExecutionException e) {
                                                logToConsole(e.getMessage());
                                                logToGlobalAlert(e.getMessage());
                                            }

                                            break;
                                        case "radiobox":
                                            /**
                                             dfs radiobox "TWRP|CWM|Phiz" "http://z-lab.me/twrp.img|http://z-lab.me/cwm.img|http://z-lab.me/phiz.img"
                                             fastboot flash recovery %RESULT%
                                             **/

                                            String[] radioboxText = commands[2].split("\\|");
                                            String[] radioboxValue = commands[3].split("\\|");

                                            dialogsData[TITLE] = commands[4];
                                            dialogsData[HEADER] = commands[5];
                                            dialogsData[CONTENT] = commands[6];

                                            FutureTask choice = new FutureTask<>(() -> choiceDialog(radioboxText, dialogsData[TITLE], dialogsData[HEADER], dialogsData[CONTENT]));
                                            Platform.runLater(choice);

                                            try {
                                                String choiced = (String) choice.get();
                                                for(int i=0;i<radioboxValue.length;i++){
                                                    if(radioboxText[i].equals(choiced)){
                                                        radioboxResult=radioboxValue[i];
                                                    }
                                                }
                                            } catch (InterruptedException | ExecutionException e) {
                                                logToConsole(e.getMessage());
                                                logToGlobalAlert(e.getMessage());
                                            }
                                            break;
                                        case "checkbox": /*TODO - need implement dfs checkbox*/
                                            /** dfs checkbox TWRP-2.8.5.0|PhilZ-Touch-6.58.7 **/
                                            break;
                                        case "unzip": /*TODO - make dfs unzip possible*/
                                            /** dfs unzip firmware.zip **/
                                            break;
                                        case "set":
                                            if(commands[2].equals("workdir") && SIMPLEMODE){
                                                FutureTask query = new FutureTask<>(() -> directoryChooserAdv("Select working directory (with images)"));
                                                Platform.runLater(query);
                                                try {
                                                    dir[0] = (File) query.get();
                                                } catch (InterruptedException | ExecutionException e) {
                                                    logToConsole(e.getMessage());
                                                    logToGlobalAlert(e.getMessage());
                                                }
                                            }
                                            break;
                                        /*TODO - file selector for next operation*/
                                    }
                                }
                            }
                            } else {
                                showResult=true;
                            }
                        }
                        Platform.runLater(() -> {
                            if(global_alert!=null){
                                global_alert_text_area.appendText("---\n" + "ALL DFS OPERATION COMPLETE.\n"+"---");
                            }
                        });
                    } catch (IOException e) {
                        logToConsole(e.getMessage());
                        logToGlobalAlert(e.getMessage());
                    }
                }).start();}
        } catch (Exception e) {
            logToConsole(e.getMessage());
            logToGlobalAlert(e.getMessage());
        }
    }

    /** ADB **/
    private void dfsAdbCheckStatus(ImageView status_image, Label status_label){
        String adb_devices_output = runCmd(ADB_BINARY, "devices", "-l");
        String[] finder = adb_devices_output.split("\n");
        if (!finder[finder.length - 1].equals("List of devices attached ")) {
            status_image.setImage(new Image(getClass().getResourceAsStream("/img/ui/icons/status_green.png")));
            String[] device_info = finder[finder.length - 1].split("\\s+");
            showDialog(Alert.AlertType.INFORMATION,"Success!", "Adb device detected.", "Device information is: " + device_info[0]);

            status_label.setText(device_info[device_info.length-2] + " " + device_info[device_info.length-1] + " " + device_info[device_info.length-3]);
        } else {
            status_image.setImage(new Image(getClass().getResourceAsStream("/img/ui/icons/status_red.png")));
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
                        int failure = (finalLog.length() - finalLog.replaceAll("Failure", "").length())/7;
                        int success = (finalLog.length() - finalLog.replaceAll("Success", "").length())/7;

                        showDialog(Alert.AlertType.INFORMATION,"adb", "Operation complete",
                                "Success: " + success
                                        + "\nFailure: " + failure + "\n"
                                        + "\nSee Console for detail." + "\n"
                                        + "\nFile list:\n" + Collections.singletonList(localfiles).toString().replace(", ", "\n").replace("[", "").replace("]", ""));
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
                        int success = (finalLog.length() - finalLog.replaceAll("Success", "").length())/7;

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
                        int success = (finalLog.length() - finalLog.replaceAll("Success", "").length())/7;

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
    /** FASTBOOT **/
    /**  private void dfsFastbootRunGeneralCommand(String... args){
        runCmd(args);
        showDialog(Alert.AlertType.INFORMATION,"fastboot", "Operation complete", "Command \""+args[args.length-1]+"t\" sended to device.");
    }*/
    /*TODO - Migrate to general command*/
    private void dfsFastbootCheckStatus(){
        String fastboot_devices_output = runCmd(FASTBOOT_BINARY, "devices");
        if (!fastboot_devices_output.equals("")) {
            String[] device_info = fastboot_devices_output.split("\t");
            img_fastboot_status.setImage(new Image(getClass().getResourceAsStream("/img/ui/icons/status_green.png")));
            showDialog(Alert.AlertType.INFORMATION,"Success!", "Fastboot device detected.", "Device information is: " + device_info[0] + " " + device_info[1]);
            tab_fastboot_device_status_txt.setText(device_info[0]+" "+device_info[1]);
        } else {
            img_fastboot_status.setImage(new Image(getClass().getResourceAsStream("/img/ui/icons/status_red.png")));
            showDialog(Alert.AlertType.ERROR,"Ooops!", "Fastboot device not detected.", "Try to reconnect.");
            tab_fastboot_device_status_txt.setText("No device detected.");
        }
    }
    private void dfsFastbootReboot(){
        runCmd(FASTBOOT_BINARY, "reboot");
        showDialog(Alert.AlertType.INFORMATION,"fastboot", "Operation complete", "Command \"reboot\" sended to device.");
    }
    private void dfsFastbootRebootBootloader(){
        runCmd(FASTBOOT_BINARY, "reboot-bootloader");
        showDialog(Alert.AlertType.INFORMATION,"fastboot", "Operation complete", "Command \"reboot-bootloader\" sended to device.");
    }
    private void dfsFastbootFlashRecovery(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash recovery " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "recovery", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootFlashBoot(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash boot " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "boot", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootFlashCache(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash cache " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "cache", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootFlashUserData(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash userdata " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "userdata", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootFlashSystem(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash system " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "system", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootFlashRadio(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to flash radio " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "flash", "radio", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootBootKernel(){
        File localfile = fileChooser();
        if (localfile != null) {
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to boot " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(FASTBOOT_BINARY, "boot", localfile.getPath());
            }).start();
        }
    }
    private void dfsFastbootMfastbootFlashSystem(){
        File localfile = fileChooser();
        if(localfile!=null){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("mfastboot", "Operation in progress", "Try to flash system " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(MFASTBOOT_BINARY, "flash", "system", localfile.getPath());
            }).start();
        }}
    private void dfsFastbootOemUnlock(){
        new Thread(() -> {
        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem unlock." + "\n\nPlease wait...\n"));
        runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "unlock");
    }).start();
    }
    private void dfsFastbootOemLock(){
        new Thread(() -> {
        Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem lock." + "\n\nPlease wait...\n"));
        runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "lock");
    }).start();
    }
    private void dfsFastbootGetUnlockData(){
        new Thread(() -> {
            Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem get_unlock_data." + "\n\nPlease wait...\n"));
            runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "get_unlock_data");
        }).start();
    }
    private void dfsFastbootLockBegin(){
        new Thread(() -> {
            Platform.runLater(() -> showDialogInformationGlobal("fastboot", "Operation in progress", "Try to oem lock begin." + "\n\nPlease wait...\n"));
            runCmdToGlobalAlert(FASTBOOT_BINARY, "oem", "lock", "begin");
        }).start();
    }
    /** RECOVERY **/
    private void dfsRecoverySideload(){
        try {
            File localfile = fileChooser();
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Try to sideload " + localfile.getName() + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(ADB_BINARY, "sideload", localfile.getPath());
            }).start();
        } catch (Exception e) {
            logToConsole(e.getMessage());
        }
    }
    private void dfsRecoveryFlashZip(){
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
    }
    private void dfsRecoveryFlashMultiZip(){
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
    }
    private void dfsRecoveryFlashZipFromSD(){
        String remotefile = remotePushSetPath("/sdcard/flash.zip");
        if(!remotefile.equals("")){
            new Thread(() -> {
                Platform.runLater(() -> showDialogInformationGlobal("recovery", "Operation in progress", "Try to flash " + remotefile + "\n\nPlease wait...\n"));
                runCmdToGlobalAlert(ADB_BINARY, "shell", "twrp", "install", remotefile);
            }).start();} else {logToConsole("No remote path selected.");}
    }
    private void dfsRecoveryWipe(){
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
    }
    private void dfsRecoveryBackup(){
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
    }
    private void dfsRecoveryRestore(){
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
    }

    /** Settings **/
    private void settingsBrowseSDK(){
        try {
            File dir = directoryChooser();
            if (checkAdbBin(dir) && checkFastbootBin(dir)) {
                tab_settings_tool_set_txt_tool_directory_browse.setText(dir.getPath());
                setBinaries();
            } else {
                String binaries;
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
    }
    private void settingsBrowseAdb(){
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
    }
    private void settingsBrowseFastboot(){
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
    }
    private void settingsBrowseMfastboot(){
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
    }
    private void settingsReinitialize(){
        tab_main_txt_area_log.appendText("Reinitialize inventory...\n");
        /** РЕИНИЦИАЛИЗАЦИЯ **/
        tab_main_txt_area_log.appendText("done...\n");
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
                List<String> list = new ArrayList<>(Arrays.asList(input));
                list.removeAll(Arrays.asList("", null));
                input = list.toArray(new String[list.size()]);
                String[] args = new String[input.length + 1];
                args[0] = binary;

                System.arraycopy(input, 0, args, 1, input.length);

                new Thread(() -> {
                        runCmdToConsoleOutput(console_text_output, args);
                }).start();
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

    /** LOG **/
    public void logToConsole(String appendString) {
        tab_main_txt_area_log.appendText(appendString+"\n");
    }
    private void logToGlobalAlert(String msg){
        Platform.runLater(() -> {
            if (global_alert != null) {
                global_alert_text_area.appendText(msg + "\n");
            }
        });
    }

    /** Dialogs **/
    /** Generals **/
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
    private boolean showDialogForResult(Alert.AlertType alertType, String title, String header, String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    private boolean showDialogConfirmation(String title, String header, String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    /** File Operation Dialogs **/
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
    /** File Chooser/Saver Dialogs **/
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
    private String choiceDialog(String[] text, String title, String header, String content){
        List<String> choices = new ArrayList<>();
        choices.addAll(new ArrayList<>(Arrays.asList(text)));

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);

        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){
            return result.get();
        } else {
            return null;
        }
    }

    /** Inventory **/
    /** General **/
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
    private String getDeviceID(){
        String adb_devices_output = runCmd(ADB_BINARY, "devices", "-l");
        String[] finder = adb_devices_output.split("\n");
        if (!finder[finder.length - 1].equals("List of devices attached ")) {
            String[] device_info = finder[finder.length - 1].split("\\s+");
            return device_info[0];
        } else {return null;}
    }
    /** Check binaries **/
    private boolean checkAdbBin(File f) {
        return f.exists();
    }
    private boolean checkFastbootBin(File f) {
        return f.exists();
    }
    private boolean checkMFastbootBin(File f) {
        return f.exists();
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
    /** Unzip Binaries **/
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

    /** Other **/
    private List<String> parseStringToArray(String str){
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);
        while (m.find())
            list.add(m.group(1).replace("\"", ""));
        return list;
    }
    private String readFileToString(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
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
}