package simplechessserver;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import simplechessserver.ClientCommunication.Handler;

/**
 * A class that represents a window that keeps track of a temporary log. 
 * With this window you can access a client via a PrintWriter.
 * @author Jed Wang
 */
public class LogWindow extends JFrame {
    /**
     * The handler for the client being accessed by this window
     */
    private Handler handler;
    
    /** 
     * Creates new form LogWindow.
     * @param handler the handler for the client
     */
    public LogWindow(Handler handler) {
        this.handler = handler;
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        jScrollPane = new JScrollPane();
        jTextPane1 = new JTextPane();
        
        handler.setActionListener((ActionEvent e) -> {
            jTextPane1.setText(jTextPane1.getText() + e.getActionCommand() + "\n");
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simple Chess Server");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                handler.clearActionListener();
            }
        });

        jTextPane1.setEditable(false);
        jTextPane1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jTextPane1.setFont(new Font("Consolas", 0, 14)); // NOI18N
        jScrollPane.setViewportView(jTextPane1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 248, GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>                        
    
    /**
     * Creates a new LogWindow and returns it.
     * @param handler the handler for the client
     * @return the new LogWindow
     */
    public static LogWindow run(Handler handler) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.LookAndFeelInfo[] installedLookAndFeels=UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo installedLookAndFeel : installedLookAndFeels) {
                if ("Nimbus".equals(installedLookAndFeel.getName())) {
                    UIManager.setLookAndFeel(installedLookAndFeel.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        LogWindow lw = new LogWindow(handler);
        EventQueue.invokeLater(() -> {
           lw.setResizable(true);
           lw.setVisible(true);
        });
        return lw;
    }
    
    // Variables declaration - do not modify
    private JScrollPane jScrollPane;
    private JTextPane jTextPane1;
    // End of variables declaration                   
}