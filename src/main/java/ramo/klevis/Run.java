package ramo.klevis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ramo.klevis.ui.ProgressBar;
import ramo.klevis.ui.UI;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by klevis.ramo on 11/24/2017.
 */
public class Run {

    private final static Logger LOGGER = LoggerFactory.getLogger(Run.class);

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private static JFrame mainFrame = new JFrame();

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application is starting ... ");

        setHadoopHomeEnvironmentVariable();
        ProgressBar progressBar = new ProgressBar(mainFrame, true);
        progressBar.showProgressBar("Collecting data this make take several seconds!");
        UI ui = new UI();
        EXECUTOR_SERVICE.submit(ui::initUI);
    }


    private static void setHadoopHomeEnvironmentVariable() throws Exception {
        HashMap<String, String> hadoopEnvSetUp = new HashMap<>();
        hadoopEnvSetUp.put("HADOOP_HOME", new File("resources/winutils-master/hadoop-2.8.1").getAbsolutePath());
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.clear();
        env.putAll(hadoopEnvSetUp);
        Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        cienv.clear();
        cienv.putAll(hadoopEnvSetUp);
    }
}
