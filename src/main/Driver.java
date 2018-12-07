/*
 * Michael Cao, Period 6
 * Objective:
 */

package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import student.Profile;

/**
 * @author Michael
 */
public class Driver extends javax.swing.JFrame {
    private static StartWindow startWindow;
    private static final Runnable RUNNER;

    static {
        Profile profile = Profile.read();
        System.out.println(profile);
        RUNNER = new Runnable() {
            @Override
            public void run() {
                startWindow = new StartWindow(profile);
                startWindow.setLocationRelativeTo(null);
                startWindow.setVisible(true);
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            java.awt.EventQueue.invokeLater(RUNNER);
            synchronized (RUNNER) {
                RUNNER.wait();
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(StartWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (startWindow.getProfile() != null)
                startWindow.getProfile().write();
            else
                System.out.println("TEST: profile is null upon exit");
        }
    }

    public static void exit() {
        synchronized (RUNNER) {
            RUNNER.notify();
        }
        System.out.println("TEST: exited");
    }
}
