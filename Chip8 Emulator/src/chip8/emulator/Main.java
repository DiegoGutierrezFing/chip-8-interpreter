/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.emulator;

import static chip8.emulator.Chip8Emulator.ALTO;
import static chip8.emulator.Chip8Emulator.ANCHO;
import static chip8.emulator.Chip8Emulator.cpu;
import static chip8.emulator.Chip8Emulator.keyEvent;
import static chip8.emulator.Chip8Emulator.relacionAspecto;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.SourceVersion;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Windows 7
 */
public class Main extends javax.swing.JFrame {

    // Variables de dimensiones iniciales de ventana 
    static final int ANCHO = 640;
    static final int ALTO = ANCHO / 2;
    static final double relacionAspecto = ANCHO / ALTO;
    static Chip8_CPU cpu = new Chip8_CPU();
    static KeyEventDemo1 keyEvent = new KeyEventDemo1();
    static JFrame frame;

    public Main() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(cpu.pantalla, 0, 0, getWidth(), getHeight(), this);
            }}
            ;
            jMenuBar1 = new javax.swing.JMenuBar();
            jMenu1 = new javax.swing.JMenu();
            jMenuItem1 = new javax.swing.JMenuItem();
            jMenuItem2 = new javax.swing.JMenuItem();
            jMenu2 = new javax.swing.JMenu();
            jMenuItem3 = new javax.swing.JMenuItem();

            jDialog1.setTitle("Acerca de");
            jDialog1.setResizable(false);
            jDialog1.setSize(new java.awt.Dimension(422, 300));

            jTextArea1.setEditable(false);
            jTextArea1.setColumns(20);
            jTextArea1.setFont(new java.awt.Font("Verdana", 0, 13)); // NOI18N
            jTextArea1.setLineWrap(true);
            jTextArea1.setRows(5);
            jTextArea1.setText("Emulador CHIP-8\n2013, 2022 Diego Andrés Gutiérrez Berón\n\nDedicado a:\n\nMis padres, Miguel y Teresa, quienes con su amor, esfuerzo y sacrificio me han permitido ir a la Universidad para poder formarme en una profesion y tener de esa forma una mejor calidad de vida que la que ellos tuvieron.\n\nMi familia y amigos cercanos, quienes con su amor y afecto siempre me apoyaron en mis emprendimientos.");
            jTextArea1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTextArea1)
                    .addContainerGap())
            );
            jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTextArea1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .addContainerGap())
            );

            jDialog1.getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle("Intérptete CHIP-8");
            setName("frame"); // NOI18N
            setPreferredSize(new java.awt.Dimension(840, 420));

            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 640, Short.MAX_VALUE)
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 320, Short.MAX_VALUE)
            );

            jMenu1.setText("Archivo");

            jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            jMenuItem1.setText("Abrir");
            jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem1ActionPerformed(evt);
                    jMenuItem1ActionPerformed1(evt);
                }
            });
            jMenu1.add(jMenuItem1);

            jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            jMenuItem2.setText("Salir");
            jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem2ActionPerformed(evt);
                }
            });
            jMenu1.add(jMenuItem2);

            jMenuBar1.add(jMenu1);

            jMenu2.setText("Ayuda");

            jMenuItem3.setText("Acerca de");
            jMenuItem3.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    jMenuItem3MouseClicked(evt);
                }
            });
            jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem3ActionPerformed(evt);
                }
            });
            jMenu2.add(jMenuItem3);

            jMenuBar1.add(jMenu2);

            setJMenuBar(jMenuBar1);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed1

    }//GEN-LAST:event_jMenuItem1ActionPerformed1

    private void jMenuItem3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem3MouseClicked
        jDialog1.setVisible(true);
        jDialog1.show();
    }//GEN-LAST:event_jMenuItem3MouseClicked

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        jDialog1.setVisible(true);
        jDialog1.show();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    JFrame f = new Main();
                    f.setVisible(true);

                    try {
                        cpu.chip8Inicializar();
                        cpu.cargarPrograma(args[0]);
                    } catch (Exception e) {

                    }

                    
                        cpu.chip8EmularCiclo();

                        if (cpu.isDrawFlag()) {
                           f.repaint();
                        }
                    

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog jDialog1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}

/**
 * Clase auxiliar para controlar eventos por teclado Se utilizo implementacion
 * de Referencia:
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/events/KeyEventDemoProject/src/events/KeyEventDemo.java
 * Adaptada por Diego Gutierrez - Marzo 2022
 */
class KeyEventDemo1 implements KeyListener, ActionListener {

    JFrame windowFrame;

    public void inicializarComponente(JFrame frame) {
        windowFrame = frame;
        windowFrame.addKeyListener(this);
    }

    /**
     * Handle the key typed event from the text field.
     */
    public void keyTyped(KeyEvent e) {
        eventoTeclado(e, "KEY TYPED: ");
    }

    /**
     * Handle the key pressed event from the text field.
     */
    public void keyPressed(KeyEvent e) {
        eventoTeclado(e, "KEY PRESSED: ");
    }

    /**
     * Handle the key released event from the text field.
     */
    public void keyReleased(KeyEvent e) {
        eventoTeclado(e, "KEY RELEASED: ");
    }

    public void actionPerformed(ActionEvent e) {

        //Return the focus to the typing area.
        windowFrame.requestFocusInWindow();
    }

    private void eventoTeclado(KeyEvent e, String keyStatus) {

        //You should only rely on the key char if the event
        //is a key typed event.
        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";
        }

        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }

        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }

        String locationString = "key location: ";
        int location = e.getKeyLocation();
        switch (location) {
            case KeyEvent.KEY_LOCATION_STANDARD:
                locationString += "standard";
                break;
            case KeyEvent.KEY_LOCATION_LEFT:
                locationString += "left";
                break;
            case KeyEvent.KEY_LOCATION_RIGHT:
                locationString += "right";
                break;
            case KeyEvent.KEY_LOCATION_NUMPAD:
                locationString += "numpad";
                break;
            default:
                // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
                locationString += "unknown";
                break;
        }

        if (keyStatus.contains("KEY PRESSED")) {
            keyboardDown(e);
            cpu.setTeclaPresionada(true);
        } else if (keyStatus.contains("KEY RELEASED")) {
            keyboardUp(e);
            cpu.setTeclaPresionada(false);
        }
    }

    public void keyboardDown(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode() == 27) {    // esc - Cerrar la ventana al pulsar escape o mostrar dialogo de cerrar
            this.windowFrame.dispose();
            System.exit(0);
        }

        switch (keyEvent.getKeyChar()) {
            case '1':
                cpu.keyboard[0x1] = 1;
                break;
            case '2':
                cpu.keyboard[0x2] = 1;
                break;
            case '3':
                cpu.keyboard[0x3] = 1;
                break;
            case '4':
                cpu.keyboard[0xC] = 1;
                break;
            case 'q':
                cpu.keyboard[0x4] = 1;
                break;
            case 'w':
                cpu.keyboard[0x5] = 1;
                break;
            case 'e':
                cpu.keyboard[0x6] = 1;
                break;
            case 'r':
                cpu.keyboard[0xD] = 1;
                break;
            case 'a':
                cpu.keyboard[0x7] = 1;
                break;
            case 's':
                cpu.keyboard[0x8] = 1;
                break;
            case 'd':
                cpu.keyboard[0x9] = 1;
                break;
            case 'f':
                cpu.keyboard[0xE] = 1;
                break;
            case 'z':
                cpu.keyboard[0xA] = 1;
                break;
            case 'x':
                cpu.keyboard[0x0] = 1;
                break;
            case 'c':
                cpu.keyboard[0xB] = 1;
                break;
            case 'v':
                cpu.keyboard[0xF] = 1;
                break;
            default:
                break;
        }
    }

    public void keyboardUp(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode() == 27) {    // esc - Cerrar la ventana al pulsar escape o mostrar dialogo de cerrar
            this.windowFrame.dispose();
            System.exit(0);
        }

        switch (keyEvent.getKeyChar()) {
            case '1':
                cpu.keyboard[0x1] = 0;
                break;
            case '2':
                cpu.keyboard[0x2] = 0;
                break;
            case '3':
                cpu.keyboard[0x3] = 0;
                break;
            case '4':
                cpu.keyboard[0xC] = 0;
                break;
            case 'q':
                cpu.keyboard[0x4] = 0;
                break;
            case 'w':
                cpu.keyboard[0x5] = 0;
                break;
            case 'e':
                cpu.keyboard[0x6] = 0;
                break;
            case 'r':
                cpu.keyboard[0xD] = 0;
                break;
            case 'a':
                cpu.keyboard[0x7] = 0;
                break;
            case 's':
                cpu.keyboard[0x8] = 0;
                break;
            case 'd':
                cpu.keyboard[0x9] = 0;
                break;
            case 'f':
                cpu.keyboard[0xE] = 0;
                break;
            case 'z':
                cpu.keyboard[0xA] = 0;
                break;
            case 'x':
                cpu.keyboard[0x0] = 0;
                break;
            case 'c':
                cpu.keyboard[0xB] = 0;
                break;
            case 'v':
                cpu.keyboard[0xF] = 0;
                break;
            default:
                break;
        }
    }

}