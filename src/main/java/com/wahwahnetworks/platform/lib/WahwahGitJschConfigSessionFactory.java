package com.wahwahnetworks.platform.lib;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Level;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;

/**
 * Created by Justin on 12/31/2014.
 */
public class WahwahGitJschConfigSessionFactory extends JschConfigSessionFactory {

    private String privateKeyPath;

    public WahwahGitJschConfigSessionFactory(String privateKeyPath){
        this.privateKeyPath = privateKeyPath;
    }

    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {
        session.setConfig("StrictHostKeyChecking", "no"); // Disable Known Host Checking
    }

    @Override
    protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException {

        JSch jsch = super.getJSch(hc, fs);
        jsch.setLogger(new WahwahGitSSHLogger());
        jsch.removeAllIdentity();
        jsch.addIdentity(privateKeyPath);
        return jsch;
    }

    public class WahwahGitSSHLogger implements com.jcraft.jsch.Logger {

        private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JSch.class);

        @Override
        public boolean isEnabled(int level) {
            return log.isEnabledFor(getLevelForLevel(level));
        }

        private Level getLevelForLevel(int level){
            switch(level){
                case DEBUG:
                    return Level.DEBUG;
                case INFO:
                    return Level.INFO;
                case WARN:
                    return Level.WARN;
                case ERROR:
                    return Level.ERROR;
                case FATAL:
                    return Level.FATAL;
            }

            return Level.OFF;
        }

        @Override
        public void log(int level, String message) {
            log.log(getLevelForLevel(level),message);
        }
    }
}
