/**
 * 
 */
package com.asd.ssh.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href="mailto:wengyj@59store.com">翁英健</a>
 * @version 1.1 2015年11月27日
 * @since 1.1
 */
public class TomcatRestartUtil {

    private static Logger       logger                      = new Logger();
    public static final int     PORT_DEFAULT                = 8080;
    private static final String CMD_CHECK_PORT_PREFIX       = "netstat -anp|grep ";
    private static final String CMD_START_CATALINA_SUFFIX   = "bin/startup.sh";
    private static final String CMD_PRINT_LOG_PREFIX        = "tail -f ";
    private static final String CMD_LOG_FILE_DEFAULT_SUFFIX = "/logs/catalina.out";
    private static final String CMD_KILL_PROCESS_PREFIX     = "kill ";

    public static void restart(String host, int sshPort, String user, String password, String catalinaHome, int catalinaPort, String loggerFile)
            throws Exception {

        SshUtil ssh = SshUtil.config(host, user, password, sshPort);
        // 1.获取pid
        String pidCmd = CMD_CHECK_PORT_PREFIX + catalinaPort;
        String pid = getPid(ssh.execute(pidCmd));
        logger.debug("执行了获取进程的命令：" + pidCmd);
        // 2.（杀死进程，）启动tomcat
        String startBash = catalinaHome + "/" + CMD_START_CATALINA_SUFFIX;
        if (pid == null) {
            logger.debug("未发现端口号为{}的tomcat进程，直接启动tomcat", catalinaPort);
            ssh.execute(startBash);
        } else {
            logger.debug("发现端口号为{}的tomcat进程,杀死{}进程，启动tomcat", catalinaPort, pid);
            String killCmd = CMD_KILL_PROCESS_PREFIX + pid;
            logger.debug("执行了杀死进程的命令：" + killCmd);
            ssh.execute(new String[] { killCmd, startBash });
        }
        // 3.控制台打印日志
        String logCmd = CMD_PRINT_LOG_PREFIX + loggerFile;
        logger.debug("执行了日志的命令：" + logCmd);
        ssh.executePrint(logCmd);
    }

    public static void restart(String host, String user, String password, String catalinaHome) throws Exception {
        restart(host, SshUtil.DEFAULT_PORT, user, password, catalinaHome, PORT_DEFAULT, catalinaHome + CMD_LOG_FILE_DEFAULT_SUFFIX);
    }

    /**
     * 分析netstat -anp | grep 8080 得到的字符串，解析出pid返回
     * 
     * @param portResult
     * @return
     */
    private static String getPid(String portResult) {
        String pid = null;
        Pattern p = Pattern.compile(".{5}/java");
        Matcher matcher = p.matcher(portResult);
        if (matcher.find()) {
            String pidWithJava = matcher.group(0);
            pid = pidWithJava.replace("/java", "");
        }
        return pid;
    }

    static class Logger {
        void debug(Object... args) {
            for (Object arg : args) {
                System.out.print(arg);
            }
            System.out.println();
        }
    }
}
