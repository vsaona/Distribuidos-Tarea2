# Distribuidos-Tarea2
Tarea sobre algoritmo de Suzuki-Kasami

Integrantes:
	Anghelo Carvajal	Rol: 201473062-4
	Vicente Saona		Rol: 201641002-3

Todo el código y el makefile están en la carpeta `src`.


Ejemplos de ejecución:
- Caso de prueba 1:
  - Terminal1: `java Process 3 TestCase1.txt 5 1 10 True`
  - Terminal2: `java Process 3 TestCase1.txt 5 1 5 false`
  - Terminal3: `java Process 3 TestCase1.txt 5 1 1 false`
- Caso de prueba 2:
  - Terminal1: `java Process 4 TestCase2.txt 2 1 20 False`
  - Terminal2: `java Process 4 TestCase2.txt 2 1 10 True`
  - Terminal3: `java Process 4 TestCase2.txt 2 1 5 False`
  - Terminal4: `java Process 4 TestCase2.txt 2 1 1 False`
- Caso de prueba 3:
  - Terminal1: `java Process 5 TestCase3.txt 1 1 30 False`
  - Terminal2: `java Process 5 TestCase3.txt 1 1 20 False`
  - Terminal3: `java Process 5 TestCase3.txt 1 1 10 True`
  - Terminal4: `java Process 5 TestCase3.txt 1 1 5 False`
  - Terminal5: `java Process 5 TestCase3.txt 1 1 1 False`


Colores:
- Rojo: Queda menos del 25% de los recursos.
- Amarillo: Queda menos del 50% de los recursos.
- Verde: Queda menos del 75% de los recursos.
- Azul: Queda al menos el 75% de los recursos.
- Cian: Estado ocioso + RN.
- Blanco: Entrando a la sección critica + RN.
- Morado con blanco: Esperando token + RN.
- Morado con negro: El proceso entrega o recibe el token.
  - Un proceso muestra el LN y la Queue del token que acaba de recibir.

Cada vez que se recibe un request de otro proceso se muestra el estado actual y el RN.


Métodos extras en RMI:
- `int getId();`. Retorna el `id` de este proceso.
- `int generateNewId()`. Retorna un nuevo id para otro proceso.
- `long getOriginalSize()`. Retorna el tamaño original del archivo.
- `void registerMe(SiteInterface otherSite)`. Recibe un objeto y lo registra para poder comunicarse con el.


Notas de la implementación:
- Si algún error ocurre, el sistema no intenta recuperarse, si no que intenta cerrar todos los procesos conectados.


EasterEgg:
- Se puede agregar un argumento extra `easterEgg` despues del argumento 'bearer' al ejecutar el proceso.


Referencias:
- RMI:
  - https://www.geeksForGeeks.org/remote-method-invocation-in-java/
- Algoritmo de Suzuki-Kasami:
  - https://www.geeksforgeeks.org/suzuki-kasami-algorithm-for-mutual-exclusion-in-distributed-system/
  - https://github.com/utkarshmankad/suzuki-kasami-broadcast-algorithm
