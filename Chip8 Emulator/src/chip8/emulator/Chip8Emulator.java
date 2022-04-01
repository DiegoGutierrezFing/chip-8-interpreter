/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chip8.emulator;

import static chip8.emulator.Chip8Emulator.cpu;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Diego Gutierrez Primera version (lenguaje C) : Enero, Marzo 2013
 * Segunda version (lenguaje Java) : Febrero, Marzo 2022
 */
public class Chip8Emulator {

    // Variables de dimensiones iniciales de ventana 
    static final int ANCHO = 640;
    static final int ALTO = ANCHO / 2;
    static final double relacionAspecto = ANCHO/ALTO;
    static Chip8_CPU cpu = new Chip8_CPU();
    
    static KeyEventDemo keyEvent = new KeyEventDemo();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, LineUnavailableException {

        System.out.println("Argumentos: ");

        for (String s : args) {
            System.out.println(s);
        }

        try {
            cpu.chip8Inicializar();
            cpu.cargarPrograma(args[0]);
            //cpu.volcadoMemoria();

            JFrame frame = new JFrame("Emulador CHIP8 por Diego Andrés Gutiérrez Berón");
            frame.setSize(ANCHO, ALTO);

            JPanel panel = new JPanel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.drawImage(cpu.pantalla, 0, 0, getWidth(), getHeight(), this);
                }
            };
            
            keyEvent.inicializarComponente(frame);
            
            frame.add(panel);
            frame.setVisible(true);
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            while (true) {

                cpu.chip8EmularCiclo();
                
                //System.out.println((new Date()).toString() + " Program counter = " + cpu.getProgramCounter());
                
                // Ampliacion o reduccion de tamaño de pantalla segun se cambie el tamaño de la ventana
                frame.setSize(frame.getWidth(), (int)Math.round((double)frame.getWidth()/relacionAspecto));                
                
                if (cpu.isDrawFlag()) {
                    panel.repaint();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Chip8Emulator.class.getName()).log(Level.SEVERE, "No se pudo abrir el archivo: " + args[0]);
        }
    }
}

/** Clase auxiliar para controlar eventos por teclado
 * Se utilizo implementacion de Referencia:
 * https://docs.oracle.com/javase/tutorial/uiswing/examples/events/KeyEventDemoProject/src/events/KeyEventDemo.java
 * Adaptada por Diego Gutierrez - Marzo 2022
 */
class KeyEventDemo implements KeyListener, ActionListener
{
    JFrame windowFrame;
    
    public void inicializarComponente(JFrame frame) {
        windowFrame = frame;
        windowFrame.addKeyListener(this);
    }     
     
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        eventoTeclado(e, "KEY TYPED: ");
    }
     
    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        eventoTeclado(e, "KEY PRESSED: ");
    }
     
    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
        eventoTeclado(e, "KEY RELEASED: ");
    }
    
    /** Handle the button click. */
    public void actionPerformed(ActionEvent e) {
         
        //Return the focus to the typing area.
        windowFrame.requestFocusInWindow();
    }
    
    private void eventoTeclado(KeyEvent e, String keyStatus){
         
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
        
        if(keyStatus.contains("KEY PRESSED")){
            keyboardDown(e);
            cpu.setTeclaPresionada(true);
        }
        else if (keyStatus.contains("KEY RELEASED")){
            keyboardUp(e);        
            cpu.setTeclaPresionada(false);
        }
    }
        
    public void keyboardDown(KeyEvent keyEvent){

        if(keyEvent.getKeyCode() == 27){    // esc - Cerrar la ventana al pulsar escape o mostrar dialogo de cerrar
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
    
    public void keyboardUp(KeyEvent keyEvent){

        if(keyEvent.getKeyCode() == 27){    // esc - Cerrar la ventana al pulsar escape o mostrar dialogo de cerrar
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