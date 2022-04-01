package chip8.emulator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * @author Diego Gutierrez. Primera version (implementacion en lenguaje C):
 * Enero, Marzo 2013. Segunda version (implementacion en lenguaje Java):
 * Febrero, Marzo 2022. Nota: 08/03/2022 - Version comprobada con ROM de testeo:
 * opcodes OK.
 */
public class Chip8_CPU {

    /**
     * Mapa de memoria del sistema 0x000-0x1FF - Interprete Chip 8 (contiene el
     * conjunto de fuentes en el emulador) 0x050-0x0A0 - Usado por el conjunto
     * de fuentes integrado de 4x5 pixeles (0-F) 0x200-0xFFF - ROM de Programa y
     * area de memoria RAM de trabajo Las direcciones de memoria del CHIP-8
     * tienen un rango 200h a FFFh, lo que hacen 3.584 bytes. La razón del
     * porqué la memoria comienza en 200h varía de acuerdo a la máquina. Para el
     * Cosmac VIP y el Telmac 1800, los primeros 512 bytes son reservados para
     * el intérprete. En esa máquinas, los 256 bytes más altos (F00h-FFFh en
     * máquinas de 4K) fueron reservados para el refresco de pantalla, y los 96
     * bytes más bajos (EA0h-EFFh) fueron reservados para los llamados de la
     * pila, uso interno y otras variables.
     */

    /* Definiciones de componentes Hardware e Implementacion de la maquina CHIP-8*/
    private int[] chip8_fontset
            = {
                0xF0, 0x90, 0x90, 0x90, 0xF0, //0
                0x20, 0x60, 0x20, 0x20, 0x70, //1
                0xF0, 0x10, 0xF0, 0x80, 0xF0, //2
                0xF0, 0x10, 0xF0, 0x10, 0xF0, //3
                0x90, 0x90, 0xF0, 0x10, 0x10, //4
                0xF0, 0x80, 0xF0, 0x10, 0xF0, //5
                0xF0, 0x80, 0xF0, 0x90, 0xF0, //6
                0xF0, 0x10, 0x20, 0x40, 0x40, //7
                0xF0, 0x90, 0xF0, 0x90, 0xF0, //8
                0xF0, 0x90, 0xF0, 0x10, 0xF0, //9
                0xF0, 0x90, 0xF0, 0x90, 0x90, //A
                0xE0, 0x90, 0xE0, 0x90, 0xE0, //B
                0xF0, 0x80, 0x80, 0x80, 0xF0, //C
                0xE0, 0x90, 0x90, 0x90, 0xE0, //D
                0xF0, 0x80, 0xF0, 0x80, 0xF0, //E
                0xF0, 0x80, 0xF0, 0x80, 0x80 //F
            };

    /**
     * Tabla de instrucciones
     *
     * CHIP-8 tiene 35 instrucciones, las cuales tienen un tamaño de 2 bytes.
     * Estos opcodes se listan a continuación, en hexadecimal y con los
     * siguientes símbolos:
     *
     * NNN: Dirección KK: constante de 8-bit N: constante de 4-bit X e Y:
     * registro de 4-bit
     *
     * PC: Contador de programa (del inglés Program Counter) SP: Puntero de pila
     * (del inglés Stack Pointer)
     *
     *
     * Opcode Explicación
     *
     * 0NNN Salta a un código de rutina en NNN. Se usaba en los viejos
     * computadores que implementaban Chip-8. Los actuales intérpretes lo
     * ignoran.
     *
     * 00E0 Limpia la pantalla.
     *
     * 00EE Retorna de una subrutina. Se decrementa en 1 el Stack Pointer (SP).
     * El intérprete establece el Program Counter como la dirección donde apunta
     * el SP en la Pila.
     *
     * 1NNN Salta a la dirección NNN. El intérprete establece el Program Counter
     * a NNN.
     *
     * 2NNN Llama a la subrutina NNN. El intérprete incrementa el Stack Pointer,
     * luego pone el actual PC en el tope de la Pila. El PC se establece a NNN.
     *
     * 3XKK Salta a la siguiente instrucción si VX = NN. El intérprete compara
     * el registro VX con el KK, y si son iguales, incrementa el PC en 2.
     *
     * 4XKK Salta a la siguiente instrucción si VX != KK. El intérprete compara
     * el registro VX con el KK, y si no son iguales, incrementa el PC en 2.
     *
     * 5XY0 Salta a la siguiente instrucción si VX = VY. El intérprete compara
     * el registro VX con el VY, y si no son iguales, incrementa el PC en 2.
     *
     * 6XKK Hace VX = KK. El intérprete coloca el valor KK dentro del registro
     * VX.
     *
     * 7XKK Hace VX = VX + KK. Suma el valor de KK al valor de VX y el resultado
     * lo deja en VX.
     *
     * 8XY0 Hace VX = VY. Almacena el valor del registro VY en el registro VX.
     *
     * 8XY1 Hace VX = VX OR VY. Realiza un bitwise OR (OR Binario) sobre los
     * valores de VX y VY, entonces almacena el resultado en VX. Un bitwise OR
     * compara cada uno de los bit respectivos desde 2 valores, y si al menos
     * uno es true (1), entonces el mismo bit en el resultado es 1. De otra
     * forma es 0.
     *
     * 8XY2 Hace VX = VX AND VY. 8XY3 Hace VX = VX XOR VY.
     *
     * 8XY4 Suma VY a VX. VF se pone a 1 cuando hay un acarreo (carry), y a 0
     * cuando no.
     *
     * 8XY5 VY se resta de VX. VF se pone a 0 cuando hay que restarle un dígito
     * al numero de la izquierda, más conocido como "pedir prestado" o borrow, y
     * se pone a 1 cuando no es necesario.
     *
     * 8XY6 Setea VF = 1 o 0 según bit menos significativo de VX. Divide VX por
     * 2.
     *
     * 8XY7 Si VY > VX => VF = 1, sino 0. VX = VY - VX.
     *
     * 8XYE Setea VF = 1 o 0 según bit más significativo de VX. Multiplica VX
     * por 2.
     *
     * 9XY0 Salta a la siguiente instrucción si VX != VY.
     *
     * ANNN Setea I = NNNN.
     *
     * BNNN Salta a la ubicación V[0]+ NNNN.
     *
     * CXKK Setea VX = un Byte Aleatorio AND KK.
     *
     * DXYN Pinta un sprite en la pantalla. El interprete lee N bytes desde la
     * memoria, comenzando desde el contenido del registro I. Y se muestra dicho
     * byte en las posiciones VX, VY de la pantalla. A los sprites que se pintan
     * se le aplica XOR con lo que está en pantalla. Si esto causa que algún
     * pixel se borre, el registro VF se setea a 1, de otra forma se setea a 0.
     * Si el sprite se posiciona afuera de las coordenadas de la pantalla, dicho
     * sprite se le hace aparecer en el lado opuesto de la pantalla.
     *
     * EX9E Salta a la sgte. instrucción si valor de VX coincide con tecla
     * presionada.
     *
     * EXA1 Salta a la sgte. instrucción si valor de VX no coincide con tecla
     * presionada (soltar tecla). FX07 Setea Vx = valor del delay timer.
     *
     * FX0A Espera por una tecla presionada y la almacena en el registro.
     *
     * FX15 Setea Delay Timer = VX. FX18 Setea Sound Timer = VX.
     *
     *
     * FX1E Indice = Indice + VX.
     *
     * FX29 Setea I = VX * largo Sprite Chip-8.
     *
     * FX33 Setea I = VX * largo Sprite Sprite Super Chip-8.
     *
     * FX55 Almacena centenas, decenas y unidades en la memoria[I], memoria[I+1]
     * y memoria[I+2].
     *
     * FX65 Guarda en memoria[I] valor de V0 a VX.
     */
    
    /*
    * Implementacion de emulacion de funciones del Hardware.
    *
    * Funciones auxiliares.
    */
    
    void limpiarPantalla() {
        /*for (i = 0; i < 64 * 32; ++i) {
            GFX[i] = 0x00;
        }*/
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                GFX[i][j] = 0x00;
            }
        }
    }

    String volcadoMemoria() {

        System.out.println("-------------------------------------------------------------------");
        String volcado = "";

        for (int j = 1; j <= 4096; j++) {
            volcado += (String.format("0x%08X", ((memoria[j - 1])) /*& 0xFF*/) + " ");
            if ((j % 16) == 0) {
                volcado += "\n";
            }
        }
        System.out.println("-------------------------------------------------------------------");
        return volcado;
    }

    /* definicion de operaciones del emulador */
    void detectarTecla() {

        for (int i = 0; i < 16; ++i) {
            if (keyboard[i] != 0) {
                registrosV[(opcode & 0x0F00) >> 8] = i;
                teclaPresionada = true;
            }
        }
    }

    void inicializarTeclado() {
        for (int i = 0; i < 16; ++i) {
            keyboard[i] = 0x00;   //inicializar (limpiar) teclado
        }
    }

    void chip8Inicializar() {
        // Inicializar los registros y la memoria una vez

        for (int i = 0; i < 4096; ++i) {
            memoria[i] = 0x0000;      //inicializar la memoria
        }

        for (int i = 0; i < 16; ++i) {
            registrosV[i] = 0x0000;   //inicializar los registros de la CPU
        }

        for (int i = 0; i < 16; ++i) {
            stack[i] = 0x0000;      //inicializar la pila (stack)
        }

        inicializarTeclado();       //inicializar teclado

        // cargar fuentes del sistema
        for (int i = 0; i < 80; ++i) {
            memoria[i] = chip8_fontset[i];
        }

        programCounter = 0x0200;   // inicializar el Contador de Programa: el codigo del programa inicia en la direccion 0x200
        stackPointer = 0x0000;   // inicializar el Puntero de Pila
        registroIndice = 0x0000;   // inicializar el Registro Indice
        opcode = 0x0000;   // inicializar el registro de codigo de instruccion en uso actualmente

        limpiarPantalla();          // inicializar la pantalla
        drawFlag = true;            // inicializar bandera de estado de dibujado de pantalla

        // inicializar los temporizadores
        delay_Timer = 0;
        sound_Timer = 0;

        //inicializar generador de numeros pseudoaleatorios
        rand = new Random();
    }

    void chip8EmularCiclo() throws LineUnavailableException, InterruptedException {

        //Variable para  de medicion de tiempos: variable auxiliar que permite calcular el avance del reloj del sistema (cpu tick)
        long t1 = System.nanoTime();

        //System.out.println(volcadoMemoria());
        // Obtener Opcode (Fetch Opcode)
        opcode = (memoria[programCounter] << 8) | (memoria[programCounter + 1] & 0xFF);

        /*
        System.out.println("programCounter: " + String.format("0x%04X", programCounter) + " opcode: " + String.format("0x%04X", opcode) + " I: " + String.format("0x%04X", registroIndice));
        System.out.println( "V0: " + String.format("0x%04X", registrosV[0]) +
                            " V1: " + String.format("0x%04X", registrosV[1]) +
                            " V2: " + String.format("0x%04X", registrosV[2]) +
                            " V3: " + String.format("0x%04X", registrosV[3]) +
                            " V4: " + String.format("0x%04X", registrosV[4]) +
                            " V5: " + String.format("0x%04X", registrosV[5]) +
                            " V6: " + String.format("0x%04X", registrosV[6]) +
                            " V7: " + String.format("0x%04X", registrosV[7]) +
                            " V8: " + String.format("0x%04X", registrosV[8]) +
                            " V9: " + String.format("0x%04X", registrosV[9]) +
                            " VA: " + String.format("0x%04X", registrosV[10]) +
                            " VB: " + String.format("0x%04X", registrosV[11]) +
                            " VC: " + String.format("0x%04X", registrosV[12]) +
                            " VD: " + String.format("0x%04X", registrosV[13]) +
                            " VE: " + String.format("0x%04X", registrosV[14]) +
                            " VF: " + String.format("0x%04X", registrosV[15]));
         */
        // Decodificar Opcode (Decode Opcode)
        switch (opcode & 0xF000) {
            case 0x0000:
                switch (opcode & 0x00FF) {
                    case 0x00E0:
                        //Limpia la pantalla
                        limpiarPantalla();
                        drawFlag = true;
                        programCounter += 2;
                        break;

                    case 0x00EE:
                        //Retorna de una subrutina.
                        //Se decrementa en 1 el Stack Pointer (SP).
                        //El intérprete establece el Program Counter como la dirección donde apunta el SP en la Pila.
                        stackPointer--;
                        programCounter = stack[stackPointer];
                        programCounter += 2;
                        break;
                    default:
                        System.out.println("Opcode desconocido: " + Integer.toHexString(opcode));
                        break;

                }
                break;

            case 0x1000:
                //Salta a la dirección NNN.
                //El intérprete establece el Program Counter a NNN.
                programCounter = opcode & 0x0FFF;
                break;

            case 0x2000:
                //Llama a la subrutina NNN.
                //El intérprete incrementa el Stack Pointer, luego de poner el actual PC en el tope de la Pila.
                //El PC se establece a NNN.

                stack[stackPointer] = programCounter;
                stackPointer++;
                programCounter = opcode & 0x0FFF;
                break;

            case 0x3000:
                //Se saltea la siguiente instrucción si VX = KK.
                //El intérprete compara el registro VX con el KK, y si son iguales, incrementa el PC en 4.

                //registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;
                if (registrosV[(opcode & 0x0F00) >> 8] == (opcode & 0x00FF & 0xFF)) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;

            case 0x4000:
                //Se saltea la siguiente instrucción si VX != KK.
                //El intérprete compara el registro VX con el KK, y si son iguales, incrementa el PC en 4.

                registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                if (registrosV[(opcode & 0x0F00) >> 8] != (opcode & 0x00FF & 0xFF)) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;

            case 0x5000:
                //Se saltea la siguiente instrucción si VX = VY.
                //El intérprete compara el registro VX con el VY, y si son iguales, incrementa el PC en 4.

                registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                if (registrosV[(opcode & 0x0F00) >> 8] == registrosV[(opcode & 0x00FF) >> 4]) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;

            case 0x6000:
                //Hace VX = KK. El intérprete coloca el valor KK dentro del registro VX.

                registrosV[(opcode & 0x0F00) >> 8] = (opcode & 0x00FF) & 0xFF;
                programCounter += 2;
                break;

            case 0x7000:
                //Hace VX = VX + KK. Suma el valor de KK al valor de VX y el resultado lo deja en VX.
                registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                registrosV[(opcode & 0x0F00) >> 8] = (registrosV[(opcode & 0x0F00) >> 8] + (opcode & 0x00FF)) & 0xFF;
                programCounter += 2;
                break;

            case 0x8000:
                switch (opcode & 0x000F) {
                    case 0x0000:
                        //Hace VX = VY. Almacena el valor del registro VY en el registro VX.

                        registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        registrosV[(opcode & 0x0F00) >> 8] = registrosV[(opcode & 0x00F0) >> 4];
                        programCounter += 2;
                        break;

                    case 0x0001:
                        //Hace VX = VX OR VY.
                        //Realiza un bitwise OR (OR Binario) sobre los valores de VX y VY, entonces almacena el resultado en VX.
                        //Un bitwise OR compara cada uno de los bit respectivos desde 2 valores, y si al menos uno es true (1),
                        //entonces el mismo bit en el resultado es 1. De otra forma es 0.

                        registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        registrosV[(opcode & 0x0F00) >> 8] |= (registrosV[(opcode & 0x00F0) >> 4]);
                        programCounter += 2;
                        break;

                    case 0x0002:
                        //Hace VX = VX AND VY.

                        registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        registrosV[(opcode & 0x0F00) >> 8] &= (registrosV[(opcode & 0x00F0) >> 4]);
                        programCounter += 2;
                        break;

                    case 0x0003:
                        //Hace VX = VX XOR VY.

                        registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        registrosV[(opcode & 0x0F00) >> 8] ^= (registrosV[(opcode & 0x00F0) >> 4]);
                        programCounter += 2;
                        break;

                    case 0x0004:
                        //Suma VY a VX.
                        //VF se pone a 1 cuando hay un acarreo (carry), y a 0 cuando no.

                        registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        if ((registrosV[(opcode & 0x00F0) >> 4] + registrosV[(opcode & 0x0F00) >> 8]) > 0xFF) {
                            registrosV[0xF] = 1;
                        } else {
                            registrosV[0xF] = 0;
                        }

                        // Sumar los valores y quitar el excedente de bits para no sobrepasar 0xFF
                        registrosV[(opcode & 0x0F00) >> 8] = (registrosV[(opcode & 0x0F00) >> 8] + registrosV[(opcode & 0x00F0) >> 4]) & 0xFF;

                        programCounter += 2;
                        break;

                    case 0x0005:
                        //8XY5
                        //VY se resta de VX.
                        //VF se pone a 0 cuando hay que restarle un dígito al numero de la izquierda, más conocido como
                        //"pedir prestado" o borrow, y se pone a 1 cuando no es necesario.

                        registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        if (registrosV[(opcode & 0x00F0) >> 4] > (registrosV[(opcode & 0x0F00) >> 8])) {
                            registrosV[0xF] = 0;
                        } else {
                            registrosV[0xF] = 1;
                        }
                        // Restar los valores y quitar el excedente de bits para no sobrepasar 0xFF
                        registrosV[(opcode & 0x0F00) >> 8] = (registrosV[(opcode & 0x0F00) >> 8] - registrosV[(opcode & 0x00F0) >> 4]) & 0xFF;
                        programCounter += 2;
                        break;

                    case 0x0006:
                        //8XY6
                        //Setea VF = 1 o 0 según bit menos significativo de VX. Divide VX por 2.

                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        registrosV[0xF] = registrosV[(opcode & 0x0F00) >> 8] & 0x1;

                        // Division por 2 usando Shift-right un lugar.
                        registrosV[(opcode & 0x0F00) >> 8] >>>= 1;
                        programCounter += 2;
                        break;

                    case 0x0007:
                        //8XY7
                        //VX = VY - VX
                        //Si VY >= VX => VF = 1, sino 0. VX = VY - VX.
                        //(Nota: Revisar el signo de igualdad: deberia ser la misma condicion que la instruccion 8XY5)

                        //registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;
                        //registrosV[(opcode & 0x00F0) >> 4] &= 0xFF;
                        if (registrosV[(opcode & 0x0F00) >> 8] > (registrosV[(opcode & 0x00F0) >> 4])) {
                            registrosV[0xF] = 0;
                        } else {
                            registrosV[0xF] = 1;
                        }
                        // Restar los valores
                        registrosV[(opcode & 0x0F00) >> 8] = (registrosV[(opcode & 0x00F0) >> 4] - registrosV[(opcode & 0x0F00) >> 8]) & 0xFF;
                        programCounter += 2;
                        break;

                    case 0x000E:
                        //Establece VF = 1 o 0 según bit más significativo de VX. Multiplica VX por 2.
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        int bit = (registrosV[(opcode & 0x0F00) >> 8]) & 0x80;

                        if (bit != 0) {
                            bit &= 0x1;
                        }

                        registrosV[0xF] = bit;

                        // Multiplicacion por 2 usando Shift-left un lugar.
                        registrosV[(opcode & 0x0F00) >> 8] = (registrosV[(opcode & 0x0F00) >> 8] << 1) & 0xFF;
                        programCounter += 2;
                        break;
                    default:
                        System.out.println("Opcode desconocido: " + Integer.toHexString(opcode));
                }
                break;

            case 0x9000:
                //Se saltea la siguiente instrucción si VX != VY.

                // Ejecutar Opcode (Execute Opcode)
                registrosV[opcode & 0x0F00 >> 8] &= 0xFF;
                registrosV[opcode & 0x00F0 >> 4] &= 0xFF;

                if ((registrosV[opcode & 0x0F00 >> 8]) != (registrosV[opcode & 0x00F0 >> 4])) {
                    programCounter += 4;
                } else {
                    programCounter += 2;
                }
                break;

            case 0xA000:
                //Establece registroIndice = NNN.

                registroIndice &= 0xFFFF;

                // Ejecutar Opcode (Execute Opcode)
                registroIndice = opcode & 0x0FFF;
                programCounter += 2;
                break;

            case 0xB000:
                //Salta a la ubicación V0 + NNN.

                // Ejecutar Opcode (Execute Opcode)
                registrosV[0x0] &= 0xFF;
                programCounter = (registrosV[0x0] + (opcode & 0x0FFF)) & 0xFFFF;
                break;

            case 0xC000:
                //Setea VX = un Byte Aleatorio AND NN.
                registrosV[opcode & 0x0F00 >> 8] &= 0xFF;
                // Ejecutar Opcode (Execute Opcode)
                rand = new Random();
                (registrosV[opcode & 0x0F00 >> 8]) = ((opcode & 0x00FF) & 0xFF) & (rand.nextInt(256) & 0xFF);
                programCounter += 2;
                break;

            case 0xD000: /**
             * implementacion de
             * http://www.multigesture.net/articles/how-to-write-an-emulator-chip-8-interpreter/
             * autor: Laurence Muller modificado por Diego Gutierrez - 2022
             * Descripcion: Pinta un sprite en la pantalla. El interprete lee N
             * bytes desde la memoria, comenzando desde el contenido del
             * registro I. Y se muestra dicho byte en las posiciones VX, VY de
             * la pantalla. A los sprites que se pintan se le aplica XOR con lo
             * que está en pantalla. Si esto causa que algún pixel se borre, el
             * registro VF se setea a 1, de otra forma se setea a 0. Si el
             * sprite se posiciona afuera de las coordenadas de la pantalla,
             * dicho sprite se le hace aparecer en el lado opuesto de la
             * pantalla. Notas: el sprite a mostrar se encuentra almacenado en
             * memoria y apuntado por el registro Indice (I)
             *
             * Notas: Marzo 2022 - Se modifica la implementacion a fin de
             * hacerla mas sencilla utilizando una matriz 64x32 en lugar de un
             * arreglo de tamaño 64x32 (Diego Gutierrez)
             */
            {
                short x = (short) ((registrosV[(opcode & 0x0F00) >> 8]) & 0xFF);
                short y = (short) ((registrosV[(opcode & 0x00F0) >> 4]) & 0xFF);
                short height = (short) ((opcode & 0x000F) & 0xFF);
                short pixel;

                short xline, yline;
                short xp = x;
                short yp = y;

                registrosV[0xF] = 0;

                for (yline = 0; yline < height; yline++) {
                    pixel = (short) ((memoria[registroIndice + yline]) & 0xFF);
                    for (xline = 0; xline < 8; xline++) {
                        if ((pixel & (0x80 >> xline)) != 0) {
                            // Verificar que siempre se este dentro del rango del arreglo (agregado el 06/03/2022)

                            /*if ((xp + xline) >= Screen.WIDTH) {
                                xp = 0;
                            }

                            if ((yp + yline) >= Screen.HEIGHT) {
                                yp = 0;
                            }

                            if (xp < 0) {
                                xp = Screen.WIDTH - 1;
                            }

                            if (yp < 0) {
                                yp = Screen.HEIGHT - 1;
                            }*/
                            //System.out.println("xp + xline: " + (xp + xline));
                            //System.out.println("yp + yline: " + (yp + yline));
                            //System.out.println("xp: " + xp);
                            //System.out.println("yp: " + yp);
                            //System.out.println("programCounter: " + programCounter);
                            if (GFX[(xp + xline) % WIDTH][(yp + yline) % HEIGHT] == 1) {
                                registrosV[0xF] = 1;
                            }
                            GFX[(xp + xline) % WIDTH][(yp + yline) % HEIGHT] ^= 1;

                        }
                    }
                }

                drawFlag = true;
                pantalla = Screen.getPantalla(GFX);
                programCounter += 2;
            }
            break;

            case 0xE000:

                switch (opcode & 0x00FF) {
                    //implementacion de http://www.multigesture.net/articles/how-to-write-an-emulator-chip-8-interpreter/
                    //autor: Laurence Muller
                    case 0x009E:
                        // EX9E: Skips the next instruction if the key stored in VX is pressed.
                        if (keyboard[registrosV[(opcode & 0x0F00) >> 8]] != 0) {
                            programCounter += 4;
                        } else {
                            programCounter += 2;
                        }
                        break;

                    case 0x00A1:
                        // EXA1: Skips the next instruction if the key stored in VX is not pressed.
                        if (keyboard[registrosV[(opcode & 0x0F00) >> 8]] == 0) {
                            programCounter += 4;
                        } else {
                            programCounter += 2;
                        }
                        break;

                    default:
                        System.out.println("Opcode desconocido: " + Integer.toHexString(opcode));
                }
                break;

            case 0xF000:
                switch (opcode & 0x00FF) {
                    case 0x0007:
                        //Setea Vx = valor del delay timer.
                        delay_Timer &= 0xFF;
                        registrosV[(opcode & 0x0F00) >> 8] = delay_Timer;
                        programCounter += 2;
                        break;

                    case 0x000A:
                        //Implementacion basada en la de Laurence Muller.
                        //Espera por una tecla presionada y la almacena en el registro.
                        teclaPresionada = false;
                        detectarTecla();

                        // If we didn't received a keypress, skip this cycle and try again.
                        if (!(teclaPresionada)) {
                            return;
                        }
                        programCounter += 2;
                        break;

                    case 0x0015:
                        // FX15: Establecer el delay timer a VX
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;
                        delay_Timer = registrosV[(opcode & 0x0F00) >> 8];
                        programCounter += 2;
                        break;

                    case 0x0018:
                        // FX18: Establecer el sound timer a VX
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;
                        sound_Timer = registrosV[(opcode & 0x0F00) >> 8];
                        programCounter += 2;
                        break;

                    case 0x001E:
                        // FX1E: Suma VX a I
                        // VF se establece a 1 cuando existe overflow de rango (registroIndice + VX > 0xFFF), y 0 cuando no se produce.

                        //registroIndice &= 0xFFFF;
                        //registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;
                        if (registroIndice + registrosV[(opcode & 0x0F00) >> 8] > 0xFFF) {
                            registrosV[0xF] = 1;
                        } else {
                            registrosV[0xF] = 0;
                        }

                        registroIndice = (registroIndice + registrosV[(opcode & 0x0F00) >> 8]) & 0xFFF;

                        programCounter += 2;
                        break;

                    case 0x0029:
                        // FX29: Set I to the memory address of the sprite data corresponding to the hexadecimal digit stored in register VX
                        //Characters 0-F (in hexadecimal) are represented by a 4x5 font
                        registroIndice &= 0xFFFF;
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;

                        registroIndice = ((registrosV[(opcode & 0x0F00) >> 8]) * 0x5);
                        //System.out.println("Indice de caracter: " + (registrosV[(opcode & 0x0F00) >> 8] * 0x5));
                        //System.out.println("Valor del registro: " + (registrosV[(opcode & 0x0F00) >> 8]));
                        programCounter += 2;
                        break;

                    case 0x0033:
                        // FX33: Stores the Binary-coded decimal representation of VX at the addresses I, I plus 1, and I plus 2
                        registrosV[(opcode & 0x0F00) >> 8] &= 0xFF;
                        memoria[registroIndice] = ((registrosV[(opcode & 0x0F00) >> 8] / 100)) & 0xFF;
                        memoria[registroIndice + 1] = ((registrosV[(opcode & 0x0F00) >> 8] / 10) % 10) & 0xFF;
                        memoria[registroIndice + 2] = ((registrosV[(opcode & 0x0F00) >> 8] % 100) % 10) & 0xFF;

                        //System.out.println("Centenas: " + (registrosV[(opcode & 0x0F00) >> 8] / 100));
                        //System.out.println("Decenas: " + (registrosV[(opcode & 0x0F00) >> 8] / 10) % 10);
                        //System.out.println("Unidades: " + (registrosV[(opcode & 0x0F00) >> 8] % 100) % 10);
                        //System.out.println("Valor del registro: " + (registrosV[(opcode & 0x0F00) >> 8]));
                        programCounter += 2;
                        break;

                    case 0x0055: // FX55: Stores V0 to VX in memory starting at address I
                    {
                        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); i++) {
                            //registrosV[i] &= 0xFF;
                            //registroIndice &= 0x0FFF;
                            memoria[registroIndice + i] = registrosV[i];
                        }

                        // On the original interpreter, when the operation is done, I = I + X + 1.
                        registroIndice = (registroIndice + (((opcode & 0x0F00) >> 8) + 1)) & 0x0FFF;
                    }

                    programCounter += 2;
                    break;

                    case 0x0065: // FX65: Fills V0 to VX with values from memory starting at address I
                    {
                        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); i++) {
                            //memoria[registroIndice + i] &= 0xFF;
                            //registroIndice &= 0x0FFF;
                            registrosV[i] = memoria[registroIndice + i];
                        }

                        // On the original interpreter, when the operation is done, I = I + X + 1.
                        registroIndice = (registroIndice + (((opcode & 0x0F00) >> 8) + 1)) & 0x0FFF;
                    }
                    programCounter += 2;
                    break;

                    default:
                        System.out.println("Opcode desconocido: " + Integer.toHexString(opcode));
                }
                break;

            default:
                System.out.println("Opcode desconocido: " + Integer.toHexString(opcode));
        }

        //Variable para  de medicion de tiempos: variable auxiliar que permite calcular el avance del reloj del sistema (cpu tick)
        long t2 = System.nanoTime();

        // Emulacion simple de pulso de reloj del CPU (1.76 MHz = 568.1818 nanosegundos)
        TimeUnit.NANOSECONDS.sleep(1000000000 / clockFrequency);

        // Contador de pulsos de reloj de cpu (implementacion simple con enteros, podria hacerse mas exacto si se implementara con flotantes)
        clockPulses += 1 + (t2 - t1) / ((double) 1000000000 / clockFrequency);

        //System.out.println("Pulsos de reloj: " + clockPulses);
        //System.out.println((double)(t2-t1)/(1000*1000*1000/clockFrequency));
        //System.out.println("delayTimer: " + delay_Timer);
        //System.out.println("soundTimer: " + sound_Timer);
        // Actualizar temporizadores
        if ((clockFrequency / 60) - clockPulses >= 0) {

            if ((delay_Timer) > 0) {
                delay_Timer--;
                //delay_Timer &= 0xFF;
            }

            if ((sound_Timer) > 0) {
                if ((sound_Timer) == 1) {
                    //System.out.println("BEEP!");
                    Sound.tone(1000, 50);
                }

                sound_Timer--;
                //sound_Timer &= 0xFF;
            }

            clockPulses = 0;
        }
    }

    void cargarPrograma(String filename) throws IOException {
        chip8Inicializar();
        Logger.getLogger(Chip8Emulator.class.getName()).log(Level.INFO, "Abriendo archivo: " + filename);

        byte[] fileArray;

        // Abrir archivo
        Path file = Paths.get(filename);
        fileArray = Files.readAllBytes(file);

        // Verificar tamaño de archivo
        long lSize = fileArray.length;
        Logger.getLogger(Chip8Emulator.class.getName()).log(Level.INFO, "Tamaño del archivo en bytes: " + lSize);

        // Copiar archivo a la memoria del Chip8
        if ((4096 - 512) > lSize) {
            int i;
            for (i = 0; i < lSize; ++i) {
                memoria[i + 512] = fileArray[i] & 0xFF;
                memoria[i + 512] &= 0xFF;
            }
        } else {
            Logger.getLogger(Chip8Emulator.class.getName()).log(Level.SEVERE, ("Error: ROM demasiado grande para la memoria disponible"));
        }

    }

    /**
     * Definiciones de componentes Hardware
     */
    private int opcode;                     // Codigo de instruccion opcode en uso actualmente.
    private int[] memoria = new int[4096];  // Memoria (RAM y ROM) disponible en la maquina CHIP-8 (4096 bytes = 4KiB).
    private int[] registrosV = new int[16]; // Registros de la CPU.
    private int registroIndice;             // Registro Indice: utilizado en operaciones de memoria. 0x000 a 0xFFF.
    private int programCounter;             // Contador de Programa (Program Counter, PC): 0x000 a 0xFFF.

    public int clockFrequency = 1760000;    // Frecuencia de la CPU en Hz (1.76 MHz en el COSMAC VIP
    private int clockPulses = 0;

    /**
     * Sub-sistema de Video (Gráficos). La Resolución de Pantalla estándar es de
     * 64×32 píxels, y la profundidad del color es Monocromo (solo 2 colores, en
     * general representado por los colores blanco y negro). Los gráficos son
     * dibujados en pantalla solo mediante Sprites los cuales son de 8 pixels de
     * ancho por 1 a 15 pixels de alto. Si un pixel del Sprite está activo,
     * entonces se pinta el color del respectivo pixel en la pantalla, en cambio
     * si no lo está, no se hace nada. El flag de acarreo o carry flag (VF) se
     * pone a 1 si cualquier pixel de la pantalla se borra (se pasa de 1 a 0)
     * mientras un pixel se está pintando.
     *
     * The graphics system: The chip 8 has one instruction that draws sprite to
     * the screen. Drawing is done in XOR mode and if a pixel is turned off as a
     * result of drawing, the VF register is set. This is used for collision
     * detection.
     *
     * The graphics of the Chip 8 are black and white and the screen has a total
     * of 2048 pixels (64 x 32). This can easily be implemented using an array
     * that hold the pixel state (1 or 0).
     */
    private final int WIDTH = 64;
    private final int HEIGHT = 32;
    private int GFX[][] = new int[WIDTH][HEIGHT];   // area de video (pantalla) de 64x32 pixeles.
    public BufferedImage pantalla;

    private boolean drawFlag;                       // bandera de estado de dibujado de pantalla: si es true, significa que debe redibujarse la pantalla.

    /**
     * Temporizadores (Timers)
     *
     * El CHIP-8 tiene 2 timers o temporizadores. Ambos corren hacia atrás hasta
     * llegar a 0 y lo hacen a 60 hertz.
     *
     * Timer para Retardo (Delay): este timer se usado para sincronizar los
     * eventos. Este valor puede ser escrito y leído. Timer para Sonido: Este
     * timer es usado para efectos de sonidos. Cuando el valor no es 0, se
     * escucha un beep. Debe recordarse que el sonido a emitir debe ser de un
     * solo tono.
     */
    private int delay_Timer;                // Registro Temporizador de retardo: se utiliza para sincronizar eventos.
    private int sound_Timer;                // Registro Temporizador de sonido: se utiliza para efectos de sonidos.

    /**
     * La pila o stack
     *
     * La pila solo se usa para almacenar direcciones que serán usadas luego, al
     * regresar de una subrutina. La versión original 1802 permitía almacenar 48
     * bytes hacia arriba en 12 niveles de profundidad. Las implementaciones
     * modernas en general tienen al menos 16 niveles.
     */
    private int stack[] = new int[16];      // Pila (Stack): estructura para almacenar direcciones de memoria.
    private int stackPointer;               // Puntero de pila (Stack Pointer, SP): apunta a una direccion de memoria almacenada dentro del Stack.

    /**
     * Entrada
     *
     * La entrada está hecha con un teclado de tipo hexadecimal que tiene 16
     * teclas en un rango de 0 a F. Las teclas '8', '4', '6' y '2' son las
     * típicas usadas para las direcciones. Se usan 3 opcodes para detectar la
     * entrada. Una se activa si la tecla es presionada, el segundo hace lo
     * mismo cuando la no ha sido presionada y el tercero espera que se presione
     * una tecla. Estos 3 opcodes se almacenan en uno de los registros de datos.
     */
    public int keyboard[] = new int[16];
    private boolean teclaPresionada;        // Bandera de tecla presionada

    private Random rand;                    // Generador de numeros pseudoaleatorios

    public int[] getChip8_fontset() {
        return chip8_fontset;
    }

    public void setChip8_fontset(int[] chip8_fontset) {
        this.chip8_fontset = chip8_fontset;
    }

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public int[] getMemoria() {
        return memoria;
    }

    public void setMemoria(int[] memoria) {
        this.memoria = memoria;
    }

    public int[] getRegistrosV() {
        return registrosV;
    }

    public void setRegistrosV(int[] registrosV) {
        this.registrosV = registrosV;
    }

    public int getRegistroIndice() {
        return registroIndice;
    }

    public void setRegistroIndice(int registroIndice) {
        this.registroIndice = registroIndice;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int[][] getGFX() {
        return GFX;
    }

    public void setGFX(int[][] GFX) {
        this.GFX = GFX;
    }

    public boolean isDrawFlag() {
        return drawFlag;
    }

    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }

    public int getDelay_Timer() {
        return delay_Timer;
    }

    public void setDelay_Timer(int delay_Timer) {
        this.delay_Timer = delay_Timer;
    }

    public int getSound_Timer() {
        return sound_Timer;
    }

    public void setSound_Timer(int sound_Timer) {
        this.sound_Timer = sound_Timer;
    }

    public int[] getStack() {
        return stack;
    }

    public void setStack(int[] stack) {
        this.stack = stack;
    }

    public int getStackPointer() {
        return stackPointer;
    }

    public void setStackPointer(int stackPointer) {
        this.stackPointer = stackPointer;
    }

    public int[] getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(int[] keyboard) {
        this.keyboard = keyboard;
    }

    public boolean isTeclaPresionada() {
        return teclaPresionada;
    }

    public void setTeclaPresionada(boolean teclaPresionada) {
        this.teclaPresionada = teclaPresionada;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }
}

/**
 * Clase auxiliar para generar sonido Referencia:
 * https://stackoverflow.com/questions/34611134/java-beep-sound-produce-sound-of-some-specific-frequencies
 */
class Sound {

    private Sound() {

    }

    static float SAMPLE_RATE = 8000f;

    static void tone(int hz, int msecs) throws LineUnavailableException {
        tone(hz, msecs, 1.0);
    }

    static void tone(int hz, int msecs, double vol) throws LineUnavailableException {
        byte[] buf = new byte[1];
        AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for (int i = 0; i < msecs * 8; i++) {
            double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
            sdl.write(buf, 0, 1);
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
}

/**
 * Clase auxiliar para representar el frame buffer (pantalla) Referencia:
 * Deitel: Como programar en C, C++ y Java (libro)
 */
class Screen {

    private Screen() {

    }

    public static final int WIDTH = 64;
    public static final int HEIGHT = 32;

    private static BufferedImage pantalla = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    public static BufferedImage getPantalla(int[][] GFX) {

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                pantalla.setRGB(x, y, obtenerColor(GFX[x][y]).getRGB());
            }
        }
        return pantalla;
    }

    private static Color obtenerColor(int[] GFX, int indice) {
        if (GFX[indice] != 0) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    private static Color obtenerColor(int pixel) {
        if (pixel != 0) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
