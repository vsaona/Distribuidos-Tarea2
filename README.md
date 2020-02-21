# Distribuidos-Tarea2
Tarea sobre algoritmo de Suzuki-Kasami


Ejemplos de ejecución:
- `java -ea Process 5 LoremIpsum 10 1 10 True`
  - `-ea` activa los `assert`. Usalo mientras desarrollas.
- `java -ea Process 5 LoremIpsum 6 1 1000 false`


Colores:
- Rojo: Queda menos del 25% de los recursos.
- Amarillo: Queda menos del 50% de los recursos.
- Verde: Queda menos del 75% de los recursos.
- Azul: Queda al menos el 75% de los recursos.
- Cian: Estado ocioso + RN.
- Morado: Esperando token + RN.
- Blanco: Entrando a la sección critica + RN.
- Negro: El proceso entrega o recibe el token.


Notas de la implementación:
- Si algún error ocurre, el sistema no intenta recuperarse, si no que intenta cerrar todos los procesos conectados.


Referencias:
- RMI:
  - https://www.geeksForGeeks.org/remote-method-invocation-in-java/
- Algoritmo de Suzuki-Kasami:
  - https://www.geeksforgeeks.org/suzuki-kasami-algorithm-for-mutual-exclusion-in-distributed-system/
  - https://github.com/utkarshmankad/suzuki-kasami-broadcast-algorithm
