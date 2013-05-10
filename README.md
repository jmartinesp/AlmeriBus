[AlmeriBus](https://play.google.com/store/apps/details?id=org.arasthel.almeribus)
=========

Aplicación para conocer el tiempo de espera de las paradas en Almería

Cómo trabajar y compilar la aplicación
--------------------------------------

Descargue el código fuente de la aplicación. Vaya a Eclipse y pulse:

```bash
File -> Import -> General -> Existing Projects into Workspace
```
Ahí seleccione AlmeriBus.

Para cargar las librerías de 'ext-libs':

```bash
File -> Import -> Android -> Existing Android Code Into Workspace
```

Deben salir seleccionados todos los proyectos menos el propio AlmeriBus. Una vez cargado, debería de estar todo listo para compilar.

Más información sobre la aplicación
-----------------------------------

Las coordenadas de las paradas y las líneas a las que pertenecen están en:

<pre>assets/DBParadas.sqlite</pre>

La base de datos contiene las tablas:

* Paradas
* ParadasFavoritas
* ParadasRepetidas (aquellas paradas con el mismo nombre pero distinta ID)
* Linea_has_Parada

Las paradas a su vez almacenan las coordenadas, el ID y el nombre de la parada.

Linea_has_Parada contiene el ID de la parada, el nº de línea, y los ID de la parada anterior y siguiente en el recorrido (pueden ser incorrectos).

Esta base de datos se copia a la carpeta data de la aplicación y se usa como base para las búsquedas.

Licencia
--------

Este software está licenciado con GLPv3. Si con el código fuente de la aplicación no ha recibido una copia de la licencia, puede consultarla aquí:
http://www.gnu.org/licenses/gpl-3.0.txt

This software is licensed under GPLv3. If you didn't receive a copy of the license while downloading this code, you can check it here: http://www.gnu.org/licenses/gpl-3.0.txt

Esta versión de la aplicación contiene los accesos a las clases que hacen uso de la web de Surbus comentados o borrados. Si alguien quiere hacer uso de estos servicios puede descomentar el código y reparar los accesos bajo su propia responsabilidad.

Este software hace uso de las siguientes librerías:

* ActionBarSherlock (Apache): https://github.com/JakeWharton/ActionBarSherlock

* GooglePlayServices

* GridLayout

* SlidingMenu (Apache): https://github.com/jfeinstein10/SlidingMenu

* ViewPagerIndicator (Apache): https://github.com/JakeWharton/Android-ViewPagerIndicator



