# chip-8-interpreter

El siguiente programa implementa un interprete/emulador de la plataforma chip-8.

Background:
Este proyecto forma parte de uno de los proyectos de emulacion de sistemas que tenia deseos de realizar hace un par de años en mis tiempos libres, a modo de hobby, siendo el primero que logro concretar debido a su relativa baja complejidad. Resumiendo, se trata de un proyecto de implementacion "Quick and dirty", o en terminos mas formales, una prueba de concepto funcional con fines de aprendizaje.

Debido a la naturaleza del proyecto (mas para satisfaccion y aprendizaje personal que por utilidad practica) el mismo puede contener bastantes errores. Por otra parte el codigo en si no es muy modular que digamos.

Puesto que el proyecto en realidad contiene porciones de codigo basado en la implementacion de otros autores (en realidad, se trata de un port inicial que realice de codigo C++ a C, y luego a Java), agrego referencias a fin de indicar que porciones del mismo fueron basados en codigo ajeno, a fin de dar credito a quien corresponde por su trabajo.

Futuras mejoras a realizar:

* Mejorar la aritmetica de enteros que implementa el interprete: a diferencia de C y C++, Java no cuenta con el calificador "unsigned" para los tipos enteros, lo que puede provocar problemas al realizar operaciones aritmeticas cuando se produce desbordamiento. A modo de ejemplo:

 En C y C++: Si tengo un entero
-   unsigned char i (i puede tomar el rango de valores de 0 a 255, haciendo "wrap around" psando por cero cuando se sobrepasa ese valor).
-   i = 255 --> i + 1 = 0;

 En Java: Si tengo un entero
-   byte i (i puede tomar el rango de valores de -128 a 127, haciendo "wrap around" pasando por -128 o 127 segun cuando se sobrepasan esos valores).
-   i = 127 --> i + 1 = -128;
-   i = -128 --> i - 1 = 127;

* Separar en hilos de ejecucion independientes el codigo de la interfaz grafica (GUI) y el interprete: actualmente se ejecutan ambas funcionalidades en el mismo hilo de ejecucion.

* Modularizar el codigo en funciones: fetch, decode y execute forman parte del mismo bloque de codigo. Seria optimo separarlos en funciones independientes.

* Agregar menu de ajustes y de seleccion de ROM a la GUI.
* Agregar un debugger y opciones de ejecucion "single stepping".
* Agregar funcionalidad extendida (soporte a opcodes de Super Chip-8 y/o MegaChip).
