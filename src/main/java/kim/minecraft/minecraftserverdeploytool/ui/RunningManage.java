package kim.minecraft.minecraftserverdeploytool.ui;

import kim.minecraft.minecraftserverdeploytool.Main;
import kim.minecraft.minecraftserverdeploytool.utils.GitHubUtil;
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil;
import kotlin.jvm.internal.ArrayIteratorKt;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RunningManage {

    public final File settings = new File("MCSDTSettings", "settings.yml");
    private final MainUI.MainFrame frame;
    public Map<String, String> tempSettings = new HashMap<>();
    JPanel progressBarPanel = new JPanel();

    public RunningManage(MainUI.MainFrame frame) {
        this.frame = frame;
        new Thread(() -> {
            checkUpdate();
            sendBroadcast();
        }).start();
    }

    private boolean checkIfFirstRun() {
        return !settings.exists();
    }

    public void saveSettings() throws IOException {
        if (!settings.getParentFile().exists()) settings.getParentFile().mkdir();
        if (!settings.exists()) settings.createNewFile();
        new Yaml().dump(tempSettings, new FileWriter(settings));
    }

    private void firstRun(String core) {
        try {
            deployServerCore(core);
            setRuntimeArgs();
            commonRun();
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder();
            ArrayIteratorKt.iterator(e.getStackTrace()).forEachRemaining((a) -> builder.append(a).append("\n"));
            JOptionPane.showConfirmDialog(frame.startLabel, "服务端部署失败，因为" + e.toString() + "\n" + builder.toString(), "服务端部署失败", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void deployServerCore(String core) {
        tempSettings.put("CoreName", core);
        showProgressBar();
        JavaRunningManageHelper.INSTANCE.deployServerCore(this, core);
        JOptionPane.showConfirmDialog(frame.startLabel, "资源文件已全部下载完成", null, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        disappearProgressBar();
    }

    private void setRuntimeArgs() {
        JavaRunningManageHelper.INSTANCE.setRuntimeArgs(this, frame);
    }

    public String askForCacheDir() {
        String input = JOptionPane.showInputDialog(frame.startLabel, "请输入您欲部署的服务端资源临时目录，留空则使用默认名称", null, JOptionPane.QUESTION_MESSAGE);
        if (Objects.equals(input, "")) input = null;
        return input;
    }

    public String askForDoUseBMCLAPI() {
        return String.valueOf(JOptionPane.showInputDialog(frame.startLabel, "请选择下载资源文件使用的镜像源", null, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"mcbbs", "bmclapi"}, "mcbbs"));
    }

    public String askForDoUseBMCLAPIWithNo() {
        return String.valueOf(JOptionPane.showInputDialog(frame.startLabel, "请选择下载资源文件使用的镜像源", null, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"mcbbs", "bmclapi", "不使用镜像源"}, "mcbbs"));
    }

    public String askForServerCoreVersion() {
        String input = JOptionPane.showInputDialog(frame.startLabel, "请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本", null, JOptionPane.QUESTION_MESSAGE);
        if (Objects.equals(input, "")) input = null;
        return input;
    }

    public String askForFileName() {
        String input = JOptionPane.showInputDialog(frame.startLabel, "请输入您欲部署的服务端核心保存文件名，留空即为原名称", null, JOptionPane.QUESTION_MESSAGE);
        if (Objects.equals(input, "")) input = null;
        return input;
    }

    public String askForSaveDir() {
        String input = JOptionPane.showInputDialog(frame.startLabel, "请输入您欲部署到的目录，留空即为当前目录", null, JOptionPane.QUESTION_MESSAGE);
        if (Objects.equals(input, "")) input = "./";
        return input;
    }

    public String askForServerCoreBuildVersion() {
        String input = JOptionPane.showInputDialog(frame.startLabel, "请输入您欲下载的服务端核心构建版本，留空即为下载最新构建", null, JOptionPane.QUESTION_MESSAGE);
        if (Objects.equals(input, "")) input = null;
        return input;
    }

    public String askForServerCoreBuildVersion(Collection<String> tab) {
        String input = String.valueOf(JOptionPane.showInputDialog(frame.startLabel, "请选择您欲下载的服务端核心构建版本，留空即为下载最新构建", null, JOptionPane.QUESTION_MESSAGE, null, tab.toArray(), ""));
        if (Objects.equals(input, "")) input = null;
        return input;
    }

    public String askForServerCoreVersion(final String normalVersion) {
        String input = JOptionPane.showInputDialog(frame.startLabel, "请输入您欲下载的服务端核心游戏版本，留空即为下载" + normalVersion + "版本", null, JOptionPane.QUESTION_MESSAGE);
        if (Objects.equals(input, "")) input = normalVersion;
        return input;
    }

    public String askForServerCoreVersion(Collection<String> tab) {
        String input = String.valueOf(JOptionPane.showInputDialog(frame.startLabel, "请选择您欲下载的服务端核心游戏版本，留空即为下载最新版本", null, JOptionPane.QUESTION_MESSAGE, null, tab.toArray(), ""));
        if (Objects.equals(input, "")) input = null;
        return input;
    }

    public boolean isNotAgreeEULA() throws IOException {
        File eula = new File(tempSettings.get("CoreFileSavedDir"), "eula.txt");
        if (eula.exists()) {
            Properties eulaProperty = new Properties();
            eulaProperty.load(new FileReader(eula));
            return Objects.equals(eulaProperty.getProperty("eula"), "false");
        } else {
            return true;
        }
    }

    private void agreeEULA() throws IOException {
        File eula = new File(tempSettings.get("CoreFileSavedDir"), "eula.txt");
        if (isNotAgreeEULA()) {
            int response = JOptionPane.showConfirmDialog(frame.startLabel, "开服前，您需要同意Minecraft软件用户最终许可协议(EULA)，您可前往\nhttps://account.mojang.com/documents/minecraft_eula \n获取EULA全文", "是否接受 Mojang EULA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (response == 0) {
                if (!eula.exists()) eula.createNewFile();
                Properties eulaProperty = new Properties();
                eulaProperty.load(new FileReader(eula));
                eulaProperty.setProperty("eula", "true");
                eulaProperty.store(
                        new FileWriter(eula),
                        "EULA has been auto-agreed by MinecraftServerDeployTools\nthe user has been agree Minecraft EULA by our application on " + new Date().toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                JOptionPane.showConfirmDialog(frame.startLabel, "EULA已同意", "信息", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void commonRun() throws IOException {
        agreeEULA();
        JOptionPane.showConfirmDialog(frame.startLabel, "服务端部署已完成，请通过命令行方式运行本程序以启动服务端", "服务端部署完成", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    public void run(String core) {
        if (checkIfFirstRun()) {
            firstRun(core);
        } else {
            try {
                commonRun();
            } catch (IOException ignored) {
            }
        }
    }

    private void checkUpdate() {
        try {
            String latestVersion = new GitHubUtil("shaokeyibb", "MinecraftServerDeployTool").getLatestReleaseTagName();
            if (!Objects.equals(latestVersion, Main.version)) {
                JOptionPane.showConfirmDialog(frame.startLabel, "您正在使用过时的发行版本「" + Main.version + "」\n当前最新版本是「" + latestVersion + "」\n请尽快更新", "检测到新版本", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(frame.startLabel, "由于" + e.toString() + "，检查更新失败", "检查更新失败", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendBroadcast() {
        try {
            JOptionPane.showConfirmDialog(frame.startLabel, IOUtil.INSTANCE.doSimpleGet("https://raw.githubusercontent.com/shaokeyibb/MinecraftServerDeployTool/master/broadcast"), "公告", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(frame.startLabel, "由于" + e.toString() + "，获取公告失败", "获取公告失败", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showProgressBar() {
        SwingUtilities.invokeLater(() -> {
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);

            progressBarPanel.add(progressBar);

            frame.add(progressBarPanel, BorderLayout.AFTER_LAST_LINE);

            frame.pack();
        });
    }

    public void disappearProgressBar() {
        frame.remove(progressBarPanel);

        frame.pack();
    }
}
