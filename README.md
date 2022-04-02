# chip-8-interpreter

### Descripción
Interprete/emulador de la plataforma CHIP-8 en Java SE.

___

### Uso
En linea de comandos ejecutar

```bash
java -jar Chip8_Emulator.jar <path y nombre de ROM a ejecutar>
```

**Mapa del teclado**

<pre>
Teclas PC              CHIP-8
| 1 2 3 4 |            | 1 2 3 C |
| q w e r |  --------> | 4 5 6 D |
| a s d f |            | 7 8 9 E |
| z x c v |            | A 0 B F |
</pre>
___

### Historia - Background

Este proyecto forma parte de uno de los proyectos de emulación de sistemas que tenía deseos de realizar hace un par de años en mis tiempos libres a modo de hobby, siendo el primero que logro concretar debido a su relativa baja complejidad.

> Resumiendo, se trata de un proyecto de implementación rápido o de **prueba de concepto** funcional con fines de aprendizaje.

Debido a la naturaleza del proyecto (con finalidad más enfocada en la satisfacción y aprendizaje personal que por utilidad práctica) el mismo contiene bastantes errores. Por otra parte el código en si no respeta nociones básicas de modularidad.

> *En otras palabras, es desprolijo pero funciona*

Puesto que el proyecto en realidad contiene porciones de codigo basado en la implementacion de otros autores (en realidad, se trata de un port inicial que realice de un codigo C++ a codigo C, y recientemente a Java), agrego referencias en los ficheros fuente a fin de indicar que porciones del mismo fueron basados en codigo ajeno o en otro tipo de fuentes de información, a fin de dar crédito a quien corresponde por su trabajo, por la inspiración para la implementación de alguna funcionalidad particular o simplemente a modo de referencia bibliográfica.

___

### Capturas de pantalla - Test screenshots

| Nombre del ROM | Screenshot | 
| --------- | --------- | 
| ***KEYPADTEST*** | <center><img width="300" src="https://user-images.githubusercontent.com/43502194/161349778-ddd85838-9c45-4866-8fbe-c49a33f1e222.png"/></center> | 
| ***ASTRODODGE*** |  <img width="300" src="https://user-images.githubusercontent.com/43502194/161350046-d86b71fa-b9bb-4573-939b-ffcdddcd1e49.png"/> <img width="300" src="https://user-images.githubusercontent.com/43502194/161350111-6238274d-3b8d-4eac-8c07-b91344575fbb.png"/> | 
| ***INVADERS*** | <img width="300" src="https://user-images.githubusercontent.com/43502194/161355049-a0959980-fdd3-4306-840b-37c824d91e1a.png"/> <img width="300" src="https://user-images.githubusercontent.com/43502194/161355220-ea7ae39f-7651-4e37-997e-9c06b9a01a0b.png"/>|
| ***PONG 2*** | <img width="300" src="https://user-images.githubusercontent.com/43502194/161355595-52f37b41-c596-4fac-bda7-995f3643236e.png"/>|

___

### Futuras mejoras a realizar

- [ ] Mejorar la aritmetica de enteros que implementa el interprete: a diferencia de C y C++, Java no cuenta con el calificador ***unsigned*** para los tipos enteros, lo que puede provocar problemas al realizar operaciones aritmeticas cuando se produce desbordamiento.[^1].
- [ ] Separar en hilos de ejecucion independientes el codigo de la interfaz grafica (GUI) y el interprete: actualmente se ejecutan ambas funcionalidades en el mismo hilo de ejecucion.
- [ ] Modularizar el codigo en funciones: fetch, decode y execute forman parte del mismo bloque de codigo. Seria optimo separarlos en funciones independientes.
- [ ] Agregar menu de ajustes y de seleccion de ROM a la GUI.
- [ ] Agregar un debugger y opciones de ejecucion "single stepping".
- [ ] Agregar funcionalidad extendida (soporte a opcodes de Super Chip-8 y/o MegaChip).
- [ ] Agregar opcion de configuración de teclado.
- [ ] Agregar funcionalidad de realizar memory dumps - savestates.

[^1]: En C y C++, si tengo un entero *unsigned char i*, puede tomar el rango de valores de 0 a 255 haciendo "wrap around" pasando por cero cuando se sobrepasa ese valor. Por otra parte, en Java, si tengo un entero *byte i*, i puede tomar el rango de valores de -128 a 127 haciendo "wrap around" pasando por -128 o 127 cuando se sobrepasan esos valores.
