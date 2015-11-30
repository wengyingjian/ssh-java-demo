/**
 * 
 */
package com.asd.ssh.demo;

import org.junit.Test;

/**
 * 
 * 
 * @author <a href="mailto:wengyj@59store.com">翁英健</a>
 * @version 1.1 2015年11月28日
 * @since 1.1
 * 
 */
public class SshTest {
    
    private String host;
    private int    sshPort;
    private String user;
    private String password;
    private String catalinaHome;
    private int    catalinaPort;
    private String loggerFile;

    @Test
    public void testSsh() throws Exception {
        TomcatRestartUtil.restart(host, sshPort, user, password, catalinaHome, catalinaPort, loggerFile);
    }

}
