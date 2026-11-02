# Proyecto X — Arsenal experimental (NeoForge 1.21.1)

> *Año 2187. Tras el apagón de la red orbital, el Directorio Ferrum reconvirtió sus fundiciones
> y activó el Proyecto X: ciento un prototipos que no dependen de munición, porque cada uno se
> alimenta de su propio núcleo de xeranio.*

Mod de armas para **Minecraft 1.21.1 + NeoForge 21.1**. Ciento una armas con modelo 3D poligonal
(OBJ) y textura HD, efectos visuales propios en alta resolución, su propio sistema de energía (ninguna usa
munición), sonidos, mensajes de muerte y recetas de fabricación.

## Versión 7.0

- **36 armas nuevas en tres series con estilo propio** (PX-66 – PX-101), cada una con su modelo 3D y textura HD, sus
  efectos, sonidos, receta y mensajes de muerte:
  - **NEÓN** (PX-66 – PX-77), tecnología urbana y componentes electrónicos: carcasas impresas en 3D, placas de circuito,
    tubos de neón, pantallas LCD, cables y baterías. PÍXEL (pistola láser), VOLTIO (subfusil cuyos pulsos saltan en arco),
    ESTÁTICA (escopeta de pulsos), DIODO (láser de carga que atraviesa a todos), FUSIBLE (granadas EMP que paralizan),
    ANTENA (fusil de precisión), VINILO (discos que rebotan), BAJO (onda de graves que atraviesa paredes), NEXO
    (minidrones que buscan y estallan), NEÓN (katana), RELÉ (bate que paraliza) y FRECUENCIA (haz de ondas continuo).
  - **CHATARRA** (PX-78 – PX-89), armas improvisadas, oxidadas y modificadas: tubos, chapa, cinta americana, madera
    vieja, abrazaderas y soldaduras. TUBERÍA (pistola de tubo), CHAPA (escopeta), CLAVO (clavadora automática),
    SERRUCHO (lanzasierras), PERNO (ballesta de varillas), MOLOTOV (charcos de fuego), BIDÓN (mortero), CACHARRO (chatarra
    que rebota), MANIVELA (ametralladora de manivela), GARFIO (arrastra criaturas o te lleva hasta un bloque), PALANCA
    (mazo con onda) y ÓXIDO (machete). Las de pólvora echan fogonazo y humo de verdad.
  - **XENO** (PX-90 – PX-101), tecnología extraterrestre y formas desconocidas: nácar iridiscente, quitina, venas que
    brillan, glifos, cristales y oro. ESPORA (esporas que buscan y envenenan), GLIFO (rayo que atraviesa), ABISMO (pozo
    de gravedad), LARVA (larvas que saltan hacia su presa), ECLIPSE (haz que atraviesa a todos), NÉBULA (orbe y nube que
    frenan), SIMBIONTE (espada que te cura), ÉTER (cono de levitación), CÉNIT (arco de flechas de luz que buscan), UMBRAL
    (grieta que atrae), OBSERVADOR (ojo que flota y dispara) y AGUIJÓN (lanza venenosa).
- Las armas que eligen solas a quién atacar (drones, esporas, larvas, flechas de luz, el arco del VOLTIO y el ojo del
  OBSERVADOR) nunca persiguen a jugadores que no peleen contigo, mascotas, aldeanos ni gólems.
- **Texturas de las armas anteriores reempaquetadas sin pérdida**: los mismos píxeles en lienzos más justos, para que
  el atlas de bloques tenga sitio para las 36 nuevas. En el juego se ven exactamente igual.
- **Esta entrega es un ZIP con solo los archivos nuevos y los que cambian**, y se sube **tal cual, sin descomprimirlo**:
  el flujo de compilación de GitHub lo descomprime solo encima de tu repositorio de la 6.2 (ver *Compilar en GitHub*).

## Versión 6.2

- **Daño:** AGUJA de 20 a 50 · AVISPÓN de 3 a 8 (sube con el calor; en rojo, 8) · ROTOR 2 por bala · TRUENO por etapas:
  1 el primer segundo, 5, 20 y, al rojo (a los 3 s), 75 · COLIBRÍ 6 por aguja · CENTELLA 10 · MAREA 2.
- **MARTINETE:** el terremoto del golpe en caída, además de paralizar 3 s a todas las criaturas de alrededor, las sacude
  cada segundo con 2 de daño mientras dura.
- **SOPLETE: el fuego, rehecho.** Antes era un brillo que se sumaba hasta quedarse en una mancha blanca; ahora es fuego
  opaco de colores vivos que se ve igual de día que de noche: una lengua que sale de la boquilla, un chorro continuo que
  se ensancha hasta el final, bocanadas naranjas que se vuelven rojas y suben, humo negro, gotas de combustible
  encendido, una salpicadura de fuego cuando choca contra un bloque y llamas sobre las criaturas alcanzadas.

## Versión 6.1

- **Equilibrado del daño** (la tabla de abajo ya tiene los valores nuevos):
  - **MARTINETE**: 8 por golpe. Si golpeas a una criatura cayendo desde más de 3 bloques, el golpe hace **25** y abre
    un **mini terremoto** de 4 bloques que **paraliza 3 s** a todas las criaturas de alrededor; la onda amortigua tu
    caída con un pequeño salto, así que no te haces daño.
  - **PULSAR**: la esfera sigue atravesando a las criaturas; a cada una que toca le hace 6 y la **paraliza 1 s** (sus
    rayos, también). Al estallar hace 10 en radio 4 y **paraliza 3 s** a todo lo de alrededor.
  - **AGUJA**: ahora dispara un **láser** (20 de daño sin cargar, 40 con la carga completa).
  - **AVISPÓN** 1 por pulso y 3 cuando salen rojos · **CIZALLA** 10 cuerpo a cuerpo y 8 el tajo · **REMACHE** 28 hasta
    7 bloques y menos a partir de ahí (la cuarta parte a 32 bloques; el cono es más cerrado para que de cerca acierten
    todos) · **ROTOR** 1 · **TRUENO** 1 y 5 en rojo · **ESCARCHA** 4 a cualquier criatura · **ECO** 6 · **COLIBRÍ** 2 ·
    **SIERRA** 10 · **CENTELLA** 6 · **ARPÓN** estallido de 10 · **PRISMA** 8 por rayo y 24 de cerca · **ENJAMBRE** 1 ·
    **CÁUSTICA** 4 · **TEMPESTAD** 4 · **ESQUIRLA** 10 · **CICLÓN** 2 · **CENIZA** 7, que ignora escudos y armaduras ·
    **SAETA** 10 – 25 · **GUADAÑA** 9 · **PICA** 10 · **GRANIZO** 2 · **RETORNO** 11 · **MAREA** 1 · **MANGUAL** 8 ·
    **ARIETE** 12.
- **Parálisis**, efecto nuevo (icono de rayo amarillo): la criatura no puede andar ni saltar y los monstruos pierden a
  su objetivo mientras dura.
- **TALADRO**: al atacar solo gira el portabrocas con la broca; el cuerpo se queda quieto en la mano.
- **SOPLETE**: el fuego ahora se ve bien, también de día (llamas opacas además del brillo).
- **Armas de carga** (AGUJA, SOLAR, SAETA y COLMENA): ya no aparece el círculo en la mira mientras cargas. La AGUJA
  usa una cruz fina.

## Versión 6.0

- **Fuego y humo de verdad en las seis armas de balas** (YUNQUE, CHACAL, TRABUCO, COYOTE, GRANIZO y LINCE):
  al disparar sale una llamarada anaranjada con lenguas de fuego y granos de pólvora encendidos, una bocanada
  de humo gris que se queda flotando y sube despacio, los chorros laterales del freno de boca o del
  compensador, el fogonazo del hueco del tambor del revólver y un hilo de humo por la ventana de expulsión
  junto con el casquillo. Las trazadoras son finas y cálidas, como balas de verdad. Y **suenan a pólvora**:
  cada arma tiene sus propios disparos sintetizados (estampido, retumbe y eco), la corredera y el cerrojo
  también, en lugar de los sonidos de Minecraft. Los disparos se oyen de lejos.
- **15 armas nuevas (PX-51 – PX-65) con estética militar realista y contemporánea**: diseños originales (no
  copian ningún modelo comercial) con acabados de polímero, cerakote, acero pavonado e inoxidable, aluminio
  anodizado, latón y goma, rieles, miras réflex y visores.
  - 2 revólveres: **BISONTE** (magnum de acero inoxidable, 12 de daño) y **VÍBORA** (táctico con punto rojo, 9).
  - 3 pistolas: **TEJÓN** (compacta, 7), **SOMBRA** (silenciada, 8: casi no se oye ni se ve) y **GALGO**
    (de competición con compensador y punto rojo, 8).
  - 2 fusiles: **AZOR** (carabina con mira holográfica, 12 balas por segundo de 8) y **CÓNDOR** (fusil de
    tirador con visor, 15).
  - 3 subfusiles: **HURÓN** (15 balas por segundo de 5), **BÚHO** (silenciado, 13 de 6) y **PIRAÑA**
    (microsubfusil, 20 de 4).
  - 2 escopetas: **JABALÍ** (semiautomática, 28 a quemarropa) y **AVALANCHA** (automática de tambor, 4 disparos
    por segundo de 24).
  - 3 armas pesadas: **DILUVIO** (ametralladora rotativa, 40 balas por segundo), **VOLCÁN** (lanzagranadas de
    40 mm: 30 de daño en radio 4) y **MAMUT** (fusil antimaterial: 40 de daño a 400 bloques).
- **Se apunta por la mira**: con clic derecho el arma sube a la cara y se mira a través del alza y el punto de
  mira, de la mira de punto rojo (el punto rojo aparece en el centro), de la holográfica (anillo y punto) o del
  visor (pantalla completa en el CÓNDOR y el MAMUT).
- Todas llevan el fogonazo realista de fuego y humo, casquillos (cartuchos rojos en las escopetas, vainas
  grandes en la antimaterial) y la granada del VOLCÁN vuela en parábola con su propio modelo 3D y estalla con
  una explosión realista (destello, bola de fuego que se apaga en humo negro, tierra, polvo y metralla).
- Mismas reglas: clic izquierdo dispara, clic derecho apunta, sin habilidades ni debilidades, mucho daño y
  alcance, y **ninguna empuja**. Las texturas de las armas nuevas están ajustadas a su tamaño (512 o
  1024 × 512 px a la misma resolución) para no agrandar el atlas del juego.

## Versión 5.0

- **15 armas nuevas (PX-36 – PX-50)**. Las seis que se pidieron, con diseño propio de Proyecto X y el daño
  exacto: **YUNQUE** (pistola pesada, 8 de daño), **CHACAL** (fusil de asalto, 7), **TRABUCO** (escopeta,
  28 de cerca: 8 perdigones de 3,5), **COYOTE** (revólver, 7), **GRANIZO** (ametralladora, 6) y **LINCE**
  (francotirador con visor, 20). Y nueve más: **RUGIDO** (lanzacohetes), **METEORO** (designador de
  meteoritos), **PULSAR** (esfera de plasma que lanza rayos), **FALLA** (martillo sísmico que abre una
  grieta), **CENTINELA** (torreta desplegable), **RETORNO** (chakram que vuelve a la mano), **MAREA** (cañón
  de agua a presión), **MANGUAL** (giro de 360°) y **ARIETE** (guantelete de onda de choque).
- Las armas de balas tienen trazadoras, casquillos que salen despedidos (cartuchos rojos en la escopeta),
  agujeros de bala y fragmentos y polvo del color del bloque alcanzado.
- El cohete, el meteorito, el chakram y la torreta se ven en el mundo con su propio modelo 3D.
- Efectos nuevos: picos de roca y grietas incandescentes, diana del meteorito y cráter, rayos magenta,
  chorro de agua con gotas y salpicaduras, onda de choque del puñetazo, giro del mangual.
- Mismas reglas: clic izquierdo dispara (o golpea), clic derecho apunta, sin habilidades ni debilidades,
  mucho daño y alcance, y **ninguna empuja**.

## Versión 4.0

- **15 armas nuevas (PX-21 – PX-35)** con su modelo poligonal, textura HD, efectos propios, sonidos,
  receta y mensajes de muerte: CENTELLA (revólver de plasma), ARPÓN (arpones que se clavan y estallan),
  MORTERO (obús en parábola hasta el punto de mira), PRISMA (haz que se divide en 5 rayos de colores),
  ENJAMBRE (nanobots que persiguen al enemigo), CÁUSTICA (ácido que deja charcos), SOLAR (sol en
  miniatura que se carga), TEMPESTAD (nube que lanza rayos), ESQUIRLA (cristal que se parte en esquirlas),
  CICLÓN (tornado que atrae y tritura), MINADOR (minas de proximidad), CENIZA (anillo desintegrador),
  SAETA (arco de energía), GUADAÑA (barrido de 150°) y PICA (estocada de 7 bloques).
- Mismas reglas que la 3.0: clic izquierdo dispara (o golpea), clic derecho apunta, sin habilidades ni
  debilidades, mucho daño y alcance, y **ninguna empuja**.
- Efectos nuevos: rayos prismáticos, relámpagos, nubes de tormenta, tornados con remolinos de polvo,
  charcos de ácido con burbujas, novas solares, minas que parpadean al armarse, enjambres, anillos
  desintegradores, barridos y estocadas de plasma con animación en primera persona.
- Un clic muy corto ya nunca se pierde (útil para el revólver).

## Versión 3.0

- **Controles de shooter**: el **clic izquierdo dispara** y el **clic derecho apunta** (el arma sube al
  centro de la vista, la cámara hace zoom, la dispersión se reduce y el ratón se vuelve más fino;
  la AGUJA mira por un visor de francotirador). El BASTIÓN usa el clic derecho para cubrirse y la
  SERPENTINA y la CIZALLA para su cuerda y su tajo.
- **Armas más grandes en la mano**, en primera y en tercera persona.
- **Efectos visuales nuevos en alta resolución** (atlas de 2048 px con filtrado suave): fogonazos
  pegados a la boca del cañón, retroceso, haces de riel con hélice, rayos eléctricos ramificados,
  láser que pasa de celeste a rojo, llamas de plasma animadas, explosiones con bolas de fuego, onda
  de choque, escombros y humo, vórtices, ondas sónicas, discos de sierra girando, estelas de cada
  proyectil, marcas de fijación y una retícula holográfica distinta para cada arma con marcador de
  aciertos.
- **Sin habilidades secundarias ni debilidades**: cada arma dispara su proyectil, hace mucho más
  daño, llega más lejos, no ralentiza, no se bloquea y tiene mucha más energía con recarga rápida.
- **Ninguna arma empuja a las entidades** (sin retroceso de golpes, balas ni explosiones).
- Las armas tampoco golpean cuerpo a cuerpo ni rompen bloques sin querer, y los endermans ya no
  esquivan sus disparos.

## Armas

| Código | Arma | Clase | Cómo dispara | Energía |
|---|---|---|---|---|
| PX-01 | **MARTINETE** | Mazo de ruptura hidráulico | Clic izquierdo: golpe de 8 de daño (0,9 golpes por segundo). Si golpeas a una criatura cayendo desde más de 3 bloques, el golpe hace 25 de daño y abre un terremoto de 4 bloques que paraliza 3 s a todas las criaturas de alrededor y, mientras dura, las sacude cada segundo con 2 de daño. La onda del terremoto amortigua tu caída con un pequeño salto: no te haces daño. No empuja al enemigo. | No gasta energía. |
| PX-02 | **AGUJA** | Fusil de riel de precisión | Mantén clic izquierdo para cargar (1,5 s) y suelta: láser celeste de 256 bloques que atraviesa paredes y a todos los enemigos. 20 de daño sin cargar, 50 con la carga completa. Clic derecho: apuntar con visor. Espera de 0,8 s entre disparos. | Batería de xeranio: 12 disparos; se recarga 1 cada 0,75 s. |
| PX-03 | **AVISPÓN** | Carabina de pulsos de plasma | Mantén clic izquierdo: fuego automático (10 pulsos por segundo). Cada pulso hace de 3 a 8 de daño: cuanto más rato disparas, más se calienta y más daño hace. Con más de la mitad de calor los pulsos salen rojos: hacen 8 de daño e incendian al enemigo. Nunca se bloquea. Clic derecho: apuntar. | Cámara de plasma: 200 pulsos; se rellena en 10 s sin disparar. |
| PX-04 | **COLMENA** | Lanzador de microcohetes | Mantén clic izquierdo para fijar hasta 6 objetivos cerca de la mira y suelta: salva de microcohetes dirigibles (20 de daño cada uno). Los cohetes sin objetivo siguen el punto al que apuntas. Espera de 1,2 s entre salvas. Clic derecho: apuntar. | Nanofábrica: 36 microcohetes; ensambla 1 cada 0,2 s sin disparar. |
| PX-05 | **VÓRTICE** | Proyector gravitacional | Clic izquierdo: singularidad que vuela hasta 260 bloques. Al tocar a una criatura estalla (20 de daño en radio 4). Contra un bloque abre un vórtice de 2 s que atrae a las criaturas y después colapsa (20 de daño). Nunca te atrae a ti. Clic derecho: apuntar. | Núcleo en suspensión: 10 singularidades; 1 cada segundo. |
| PX-06 | **CIZALLA** | Hoja de vibroenergía | Clic izquierdo: estocada de 10 de daño (1,8 golpes por segundo), sin empujar. Clic derecho: tajo de media luna que atraviesa a todos los enemigos que encuentra (8 de daño). | Celda de filo: 10 tajos; 1 cada 0,5 s. |
| PX-07 | **TALADRO** | Perforadora de asedio | Mantén clic izquierdo sobre una criatura: el portabrocas gira y la broca la perfora. Cuanto más tiempo, más daño: 3 → 10 (a 1,5 s) → 50 (a 3 s), cinco veces por segundo. Sobre los bloques excava como un pico y una pala de netherita a la vez (+1 bloque de alcance). | Motor de fusión: no gasta energía. |
| PX-08 | **SERPENTINA** | Látigo de cadena electrificada | Clic izquierdo: latigazo de 6 de daño con 3 bloques más de alcance. Clic derecho: cuerda azul de 24 bloques que atrapa al enemigo y lo arrastra hasta ti; tu primer golpe después hace 18 de daño. | Bobina superconductora: la cuerda se recarga en 0,8 s. |
| PX-09 | **CHISPA** | Pistola de arco eléctrico | Mantén clic izquierdo: rayos eléctricos continuos (40 bloques) que electrocutan al enemigo de la mira y saltan a 3 enemigos cercanos. 7 de daño a cada uno, cinco veces por segundo. Clic derecho: apuntar. | Condensador: 300 cargas (30 s de rayos); se recarga en 15 s. |
| PX-10 | **REMACHE** | Escopeta de remaches magnéticos | Clic izquierdo: 9 remaches que, hasta 7 bloques, hacen 28 de daño si aciertan todos. Más lejos pierden fuerza poco a poco, hasta la cuarta parte a 32 bloques o más (alcance de 96 bloques). Clic derecho: apuntar (la dispersión se reduce a la mitad). | Tambor magnético: 24 disparos; 1 cada 0,5 s. |
| PX-11 | **TRITURADORA** | Lanzagranadas de racimo | Clic izquierdo: granada que rebota 2 veces y estalla (30 de daño en radio 4) soltando 4 bombetas de 10 de daño. No te daña ni empuja a nadie. Clic derecho: apuntar. | Forja de granadas: 12 granadas; 1 cada 0,8 s. |
| PX-12 | **ROTOR** | Ametralladora rotativa de energía | Mantén clic izquierdo: 20 trazadoras por segundo de 2 de daño, sin tiempo de arranque y sin frenarte. Clic derecho: apuntar (más precisión). | Cinta de energía: 500 disparos; se recarga 20 por segundo sin disparar. |
| PX-13 | **HALCÓN** | Lanzador de drones de combate | Clic izquierdo: despliega un dron de combate que caza enemigos en 32 bloques durante 40 s (12 de daño por pulso). Hasta 3 drones a la vez; uno nuevo releva al más antiguo. Clic derecho: apuntar. | Hangar: 3 drones; 1 cada 5 s. |
| PX-14 | **TRUENO** | Cañón láser térmico | Mantén clic izquierdo: láser continuo de 64 bloques que atraviesa a 3 criaturas y daña cinco veces por segundo. Empieza celeste: 1 de daño el primer segundo. Cuanto más tiempo sigue encendido más se calienta y más daño hace: 5 desde el segundo 1, 20 desde el segundo 2 y, a los 3 s, rojo: 75. Clic derecho: apuntar. | Celda láser: 400 cargas (20 s de láser); se recarga en 20 s. |
| PX-15 | **SOPLETE** | Lanzallamas de plasma | Mantén clic izquierdo: chorro de fuego de 20 bloques, opaco y bien visible de día y de noche, que hace 3 de daño diez veces por segundo y deja ardiendo 6 s. Quema también a las criaturas inmunes al fuego. Clic derecho: apuntar. | Tanque de gel: 300 unidades (15 s de chorro); se rellena en 15 s. |
| PX-16 | **ESCARCHA** | Proyector criogénico automático | Mantén clic izquierdo: ráfaga automática de pequeños láseres de hielo (10 por segundo, 4 de daño a cualquier criatura). Cada impacto ralentiza un poco más al enemigo hasta dejarlo inmóvil. Clic derecho: apuntar. | Criotanque: 300 láseres; se rellena en 15 s. |
| PX-17 | **ECO** | Cañón sónico | Clic izquierdo: onda expansiva que avanza 64 bloques atravesando paredes y criaturas mientras se ensancha. Hace 6 de daño en área (ignora la armadura) a todo lo que alcanza. Clic derecho: apuntar. | Resonador: 12 ondas; 1 cada 0,6 s. |
| PX-18 | **BASTIÓN** | Escudo de energía de asalto | Mantén clic derecho para cubrirte: bloquea el daño venga de donde venga (también por los flancos y la espalda). Clic izquierdo: balas cinéticas de 8 de daño (5 por segundo), también mientras te cubres. | Proyector de campo: 120 balas; se recarga en 12 s. |
| PX-19 | **COLIBRÍ** | Subfusil de agujas | Mantén clic izquierdo: 20 agujas de cristal por segundo de 6 de daño. Clic derecho: apuntar (más precisión). | Cargador de agujas: 240 agujas; se regenera 20 por segundo sin disparar. |
| PX-20 | **SIERRA** | Lanzadiscos de sierra | Clic izquierdo: disco de sierra de 10 de daño que atraviesa a todos los enemigos y rebota hasta 4 veces. Nunca te corta a ti. Clic derecho: apuntar. | Forja de discos: 20 discos; 1 cada 0,5 s. |
| PX-21 | **CENTELLA** | Revólver de plasma | Clic izquierdo: bala de plasma pesada de 10 de daño que vuela 170 bloques. Cada clic dispara (hasta 5 por segundo); manteniendo el clic, 2,5 por segundo. Clic derecho: apuntar. | Tambor de plasma: 36 disparos; recarga 1 cada 0,25 s. |
| PX-22 | **ARPÓN** | Fusil de arpones de fusión | Clic izquierdo: arpón que se clava en la criatura o en la pared (18 de daño) y un segundo después estalla: 10 de daño en radio 3,5. Nunca te daña a ti. Clic derecho: apuntar. | Forja de arpones: 10 arpones; fabrica 1 cada 0,8 s. |
| PX-23 | **MORTERO** | Mortero de mano | Clic izquierdo: obús en parábola alta que cae justo en el punto que marca la mira (hasta 120 bloques): 35 de daño en radio 5. Sirve para atacar por encima de los muros. Nunca te daña a ti. Clic derecho: apuntar. | Recámara: 8 obuses; carga 1 cada segundo. |
| PX-24 | **PRISMA** | Cañón prismático | Clic izquierdo: haz de luz que a 2,5 bloques se divide en 5 rayos de colores en abanico (64 bloques, 8 de daño cada uno; cada rayo atraviesa 2 criaturas). Si la criatura está cerca, los cinco rayos la golpean juntos: 24 de daño (nunca más de 24 por disparo). Clic derecho: apuntar. | Cristal de foco: 30 disparos; recarga 1 cada 0,25 s. |
| PX-25 | **ENJAMBRE** | Lanzador de nanoenjambre | Clic izquierdo: cápsula que, al chocar, libera un enjambre de nanobots que persigue a la criatura más cercana durante 5 s. El enjambre hace 1 de daño cada 0,25 s a todo lo que alcanza y nunca te ataca a ti. Clic derecho: apuntar. | Colmena: 6 cápsulas; ensambla 1 cada 1,5 s. |
| PX-26 | **CÁUSTICA** | Proyector de ácido | Mantén clic izquierdo: fuego automático de glóbulos de ácido (casi 7 por segundo); 4 de daño, y salpican 2 a los que están al lado. Dejan en el suelo un charco que hace 2 de daño cada 0,5 s durante 4 s. Clic derecho: apuntar. | Depósito de ácido: 120 glóbulos; se rellena en 12 s sin disparar. |
| PX-27 | **SOLAR** | Cañón de fusión | Mantén clic izquierdo para encender el reactor (2 s) y suelta: un sol en miniatura que estalla al chocar. Sin cargar, 20 de daño en radio 3; con la carga completa, 60 en radio 6. Nunca te daña a ti. Espera de 1 s entre disparos. Clic derecho: apuntar. | Reactor: 8 soles; enciende 1 cada segundo. |
| PX-28 | **TEMPESTAD** | Cañón de tormenta | Clic izquierdo: forma una nube de tormenta sobre el punto al que apuntas (hasta 96 bloques); durante 4 s lanza rayos de 4 de daño a las criaturas que tiene debajo. Los rayos no queman ni rompen nada y nunca te alcanzan. Clic derecho: apuntar. | Condensador: 6 nubes; carga 1 cada 2 s. |
| PX-29 | **ESQUIRLA** | Fusil de fragmentación cristalina | Clic izquierdo: cristal de 10 de daño que se parte en 7 esquirlas de 8: si atraviesa a una criatura siguen hacia delante en abanico; si choca con una pared, rebotan. Clic derecho: apuntar con más precisión. | Cargador de cristales: 24 cristales; crece 1 cada 0,4 s. |
| PX-30 | **CICLÓN** | Cañón de torbellino | Clic izquierdo: tornado que avanza por el suelo durante 3 s (unos 21 bloques); atrae a las criaturas de alrededor y tritura a las que atrapa (2 de daño cada 0,25 s). Nunca te atrae a ti. Clic derecho: apuntar. | Compresor: 5 tornados; 1 cada 2 s. |
| PX-31 | **MINADOR** | Lanzaminas de proximidad | Clic izquierdo: mina que se pega a cualquier superficie, se arma en 0,6 s y estalla cuando una criatura se acerca: 30 de daño en radio 4. Si toca a una criatura estalla en el acto. Como mucho 8 minas: al poner otra, la más vieja estalla. Duran 60 s y nunca te dañan a ti. | Tambor: 8 minas; carga 1 cada segundo. |
| PX-32 | **CENIZA** | Desintegrador molecular | Clic izquierdo: anillo desintegrador verde y lento que atraviesa a todas las criaturas que encuentra (7 de daño a cada una) a lo largo de 70 bloques. Ignora escudos y armaduras. Se detiene al chocar con un bloque. Clic derecho: apuntar. | Núcleo de disolución: 10 anillos; 1 cada 0,8 s. |
| PX-33 | **SAETA** | Arco de energía | Mantén clic izquierdo para tensar la cuerda de luz (1 s) y suelta: flecha de luz que atraviesa hasta 3 criaturas. De 10 de daño sin tensar a 25 con la tensión completa. Tensada, la flecha vuela más rápido y más recta. Clic derecho: apuntar. | Cuerda de luz: 20 flechas; 1 cada 0,5 s. |
| PX-34 | **GUADAÑA** | Guadaña de energía | Clic izquierdo: barrido de 150° y 4,5 bloques de radio que golpea a todas las criaturas que tienes delante (9 de daño). Manteniendo el clic, un barrido cada 0,6 s. No atraviesa paredes y nunca empuja. | Filo de plasma: no gasta energía. |
| PX-35 | **PICA** | Lanza de plasma | Clic izquierdo: estocada de plasma de 7 bloques que atraviesa a todas las criaturas en línea (10 de daño). Manteniendo el clic, una estocada cada 0,5 s. Nunca empuja. | Punta de plasma: no gasta energía. |
| PX-36 | **YUNQUE** | Pistola pesada | Clic izquierdo: bala de gran calibre de 8 de daño con 200 bloques de alcance. Cada clic dispara (hasta 5 por segundo); manteniendo el clic, unos 3 por segundo. Apuntando, precisión total. Clic derecho: apuntar. | Cargador de energía: 28 balas; recarga 1 cada 0,25 s. |
| PX-37 | **CHACAL** | Fusil de asalto | Mantén el clic izquierdo: fuego automático, 10 balas por segundo de 7 de daño con 200 bloques de alcance. Apuntando, la dispersión se reduce mucho. Clic derecho: apuntar. | Cargador de energía: 120 balas; se recarga solo (20 por segundo) medio segundo después de dejar de disparar. |
| PX-38 | **TRABUCO** | Escopeta de corredera | Clic izquierdo: 8 perdigones de 3,5 de daño; a quemarropa aciertan todos (28 de daño). Alcance de 64 bloques; un disparo cada 0,75 s. Cuanto más cerca, más perdigones aciertan. Clic derecho: apuntar. | Recámara: 16 cartuchos; carga 1 cada 0,5 s. |
| PX-39 | **COYOTE** | Revólver | Clic izquierdo: bala de 7 de daño con 220 bloques de alcance. Cada clic dispara (hasta 4 por segundo); manteniendo el clic, casi 3 por segundo. Apuntando, precisión total. Clic derecho: apuntar. | Tambor de energía: 36 balas; recarga 1 cada 0,25 s. |
| PX-40 | **GRANIZO** | Ametralladora pesada | Mantén el clic izquierdo: 20 balas por segundo de 2 de daño con 200 bloques de alcance. Apuntando, la dispersión se reduce. Clic derecho: apuntar. | Cinta de energía: 400 balas; se recarga sola (20 por segundo) medio segundo después de dejar de disparar. |
| PX-41 | **LINCE** | Fusil de francotirador | Clic izquierdo: bala de 20 de daño con 320 bloques de alcance; un disparo cada 1,2 s (cerrojo). Clic derecho: mirar por el visor (zoom de francotirador y precisión total). | Cargador de energía: 12 balas; recarga 1 cada 0,75 s. |
| PX-42 | **RUGIDO** | Lanzacohetes | Clic izquierdo: cohete que vuela recto 216 bloques y estalla al chocar: 40 de daño en radio 4,5 (el objetivo directo recibe el daño completo). Un cohete cada 1,5 s. Nunca te daña a ti. Clic derecho: apuntar. | Tubo de carga: 6 cohetes; carga 1 cada 1,5 s. |
| PX-43 | **METEORO** | Designador de meteoritos | Clic izquierdo: marca el punto de la mira (hasta 128 bloques) y un segundo después cae un meteorito justo ahí: 45 de daño en radio 6. Bajo techo, el meteorito aparece por debajo del techo. Nunca te daña a ti. Clic derecho: apuntar. | Enlace orbital: 4 meteoritos; 1 cada 2,5 s. |
| PX-44 | **PULSAR** | Proyector de plasma | Clic izquierdo: esfera de energía lenta que atraviesa a las criaturas: a cada una que toca le hace 6 de daño y la paraliza 1 s. Cada 0,5 s lanza además un rayo a los 3 enemigos más cercanos (6 bloques) con el mismo efecto. Al chocar con un bloque, o a los 5 s, estalla: 10 de daño en radio 4 y parálisis de 3 s a todo lo de alrededor. Nunca te daña a ti. Clic derecho: apuntar. | Condensador: 6 esferas; 1 cada 1,5 s. |
| PX-45 | **FALLA** | Martillo sísmico | Clic izquierdo: golpe contra el suelo que abre una grieta de 29 bloques en la dirección en que miras; los picos de roca hacen 22 de daño a cada criatura que alcanzan. La grieta sigue el terreno y sube escalones; se detiene ante un muro o un precipicio. Clic derecho: apuntar. | Resonador: 6 golpes; 1 cada 1,5 s. |
| PX-46 | **CENTINELA** | Torreta desplegable | Clic izquierdo: coloca una torreta donde apuntas (hasta 12 bloques) que durante 20 s dispara 4 balas por segundo de 5 de daño a los enemigos que ve en 24 bloques. Ataca a los monstruos y a quien pelee contigo, nunca a ti. Hasta 2 a la vez: una tercera releva a la más antigua. | Hangar: 3 torretas; 1 cada 5 s. |
| PX-47 | **RETORNO** | Lanzador de chakram | Clic izquierdo: chakram que vuela 32 bloques cortando a todas las criaturas que cruza (11 de daño) y vuelve a tu mano cortando otra vez (11). Si choca con una pared, vuelve antes; a la vuelta atraviesa los bloques. Clic derecho: apuntar. | Riel magnético: 6 chakrams; 1 cada 0,75 s. |
| PX-48 | **MAREA** | Cañón hidráulico | Mantén el clic izquierdo: chorro de agua a presión de 26 bloques que hace 2 de daño 10 veces por segundo a todas las criaturas que toca. Con el depósito lleno, el chorro dura 15 s seguidos. Clic derecho: apuntar. | Depósito a presión: 300 unidades; se rellena solo. |
| PX-49 | **MANGUAL** | Mangual de energía | Clic izquierdo: haces girar la bola a tu alrededor: 8 de daño a todas las criaturas a menos de 3,6 bloques, en los 360°. Manteniendo el clic, un giro cada 0,3 s. No atraviesa paredes y nunca empuja. | Bola de plasma: no gasta energía. |
| PX-50 | **ARIETE** | Guantelete de impacto | Clic izquierdo: puñetazo que suelta una onda de choque en un cono de 60° y 5 bloques: 12 de daño a todas las criaturas que alcanza. Manteniendo el clic, un puñetazo cada 0,75 s. No atraviesa paredes y nunca empuja. | Pistón de choque: no gasta energía. |
| PX-51 | **BISONTE** | Revólver magnum | Clic izquierdo: bala magnum de 12 de daño con 220 bloques de alcance. Cada clic dispara (más de 3 por segundo); manteniendo el clic, unos 2 por segundo. Apuntando miras por el alza y el punto de mira: precisión total. Clic derecho: apuntar. | Tambor de energía: 30 balas; recarga 1 cada 0,25 s. |
| PX-52 | **VÍBORA** | Revólver táctico | Clic izquierdo: bala de 9 de daño con 200 bloques de alcance. Cada clic dispara (hasta 4 por segundo); manteniendo el clic, casi 3 por segundo. Apuntando miras por la mira de punto rojo: precisión total. Clic derecho: apuntar. | Tambor de energía: 35 balas; recarga 1 cada 0,25 s. |
| PX-53 | **TEJÓN** | Pistola compacta | Clic izquierdo: bala de 7 de daño con 160 bloques de alcance. Cada clic dispara (casi 7 por segundo); manteniendo el clic, 4 por segundo. Apuntando miras por el alza y el punto de mira: casi sin dispersión. Clic derecho: apuntar. | Cargador de energía: 45 balas; recarga 1 cada 0,2 s. |
| PX-54 | **SOMBRA** | Pistola silenciada | Clic izquierdo: bala de 8 de daño con 180 bloques de alcance. Cada clic dispara (hasta 5 por segundo); manteniendo el clic, más de 3 por segundo. El silenciador apaga el fogonazo y el disparo apenas se oye a 16 bloques. Apuntando, precisión total. Clic derecho: apuntar. | Cargador de energía: 51 balas; recarga 1 cada 0,2 s. |
| PX-55 | **GALGO** | Pistola de competición | Clic izquierdo: bala de 8 de daño con 200 bloques de alcance. Cada clic dispara (casi 7 por segundo); manteniendo el clic, 5 por segundo. El compensador mantiene el cañón abajo. Apuntando por la mira de punto rojo, precisión total. Clic derecho: apuntar. | Cargador de energía: 60 balas; recarga 1 cada 0,15 s. |
| PX-56 | **AZOR** | Carabina táctica | Mantén el clic izquierdo: fuego automático, 12 balas por segundo de 8 de daño con 220 bloques de alcance. Apuntando por la mira holográfica la dispersión casi desaparece. Clic derecho: apuntar. | Cargador de energía: 120 balas; se recarga solo (20 por segundo) medio segundo después de dejar de disparar. |
| PX-57 | **CÓNDOR** | Fusil de tirador | Clic izquierdo: bala de 15 de daño con 280 bloques de alcance. Cada clic dispara (hasta 4 por segundo); manteniendo el clic, más de 3 por segundo. Clic derecho: mirar por el visor (aumento y precisión total). | Cargador de energía: 40 balas; recarga 1 cada 0,3 s. |
| PX-58 | **HURÓN** | Subfusil compacto | Mantén el clic izquierdo: fuego automático, 15 balas por segundo de 5 de daño con 160 bloques de alcance. Apuntando por la mira de punto rojo la dispersión se reduce mucho. Clic derecho: apuntar. | Cargador de energía: 150 balas; se recarga solo (20 por segundo) medio segundo después de dejar de disparar. |
| PX-59 | **BÚHO** | Subfusil silenciado | Mantén el clic izquierdo: fuego automático, 13 balas por segundo de 6 de daño con 180 bloques de alcance. El silenciador integral apaga el fogonazo y casi todo el ruido. Apuntando, la dispersión se reduce mucho. Clic derecho: apuntar. | Cargador de energía: 120 balas; se recarga solo (20 por segundo) medio segundo después de dejar de disparar. |
| PX-60 | **PIRAÑA** | Microsubfusil | Mantén el clic izquierdo: fuego automático, 20 balas por segundo de 4 de daño con 140 bloques de alcance. Apuntando, la dispersión se reduce. Clic derecho: apuntar. | Cargador de energía: 200 balas; se recarga solo (20 por segundo) medio segundo después de dejar de disparar. |
| PX-61 | **JABALÍ** | Escopeta semiautomática | Clic izquierdo: 8 perdigones de 3,5 de daño; a quemarropa aciertan todos (28 de daño). Alcance de 64 bloques; cada clic dispara (más de 3 por segundo). Cuanto más cerca, más perdigones aciertan. Apuntando por el alza de anillo, el cono se cierra. Clic derecho: apuntar. | Cargador de energía: 32 cartuchos; carga 1 cada 0,4 s. |
| PX-62 | **AVALANCHA** | Escopeta automática | Mantén el clic izquierdo: 4 disparos por segundo de 8 perdigones de 3 de daño (24 a quemarropa). Alcance de 56 bloques. Cuanto más cerca, más perdigones aciertan. Apuntando, el cono se cierra. Clic derecho: apuntar. | Tambor de energía: 40 cartuchos; carga 1 cada 0,3 s. |
| PX-63 | **DILUVIO** | Ametralladora rotativa | Mantén el clic izquierdo: 40 balas por segundo de 4 de daño con 200 bloques de alcance. Apuntando, la dispersión se reduce a la mitad. Clic derecho: apuntar. | Cinta de energía: 800 balas; se recarga sola (40 por segundo) medio segundo después de dejar de disparar. |
| PX-64 | **VOLCÁN** | Lanzagranadas de tambor | Clic izquierdo: granada de 40 mm que vuela en parábola y estalla al chocar: 30 de daño en radio 4 (el objetivo directo recibe el daño completo). Cada clic dispara (hasta 2 por segundo). Nunca te daña a ti ni rompe bloques. Clic derecho: apuntar con la mira réflex. | Tambor de energía: 12 granadas; carga 1 por segundo. |
| PX-65 | **MAMUT** | Fusil antimaterial | Clic izquierdo: bala de gran calibre de 40 de daño con 400 bloques de alcance; un disparo cada 1,5 s (cerrojo). Clic derecho: mirar por el visor (aumento máximo y precisión total). | Cargador de energía: 10 balas; recarga 1 por segundo. |
| PX-66 | **PÍXEL** | Pistola láser | Clic izquierdo: pulso láser magenta de 8 de daño que vuela 128 bloques. Cada clic dispara (hasta 5 por segundo); manteniendo el clic, algo más de 3 por segundo. Apuntando, precisión total. Clic derecho: apuntar. | Batería de neón: 40 pulsos; recarga 1 cada 0,15 s. |
| PX-67 | **VOLTIO** | Subfusil de arco | Mantén clic izquierdo: 12 pulsos eléctricos por segundo de 5 de daño. Cada pulso que acierta salta en un arco a otra criatura cercana (4,5 bloques) y le hace 2. Clic derecho: apuntar. | Batería: 120 pulsos; recarga 1 cada 0,1 s. |
| PX-68 | **ESTÁTICA** | Escopeta de pulsos | Clic izquierdo: 8 descargas de estática de 3,5 de daño en abanico (alcance 24 bloques). Una cada 0,7 s. De cerca aciertan casi todas: 28 de daño. Clic derecho: apuntar. | Condensadores: 24 descargas; recarga 1 cada 0,5 s. |
| PX-69 | **DIODO** | Láser de carga | Mantén clic izquierdo para cargar (1 s) y suelta: haz de luz de 96 bloques que atraviesa a todas las criaturas. 12 de daño sin cargar, 30 con la carga completa. El haz se detiene en los bloques. Clic derecho: apuntar. | Celdas: 16 disparos; recarga 1 cada 0,75 s. |
| PX-70 | **FUSIBLE** | Lanzagranadas EMP | Clic izquierdo: granada EMP que rebota y estalla a los 1,2 s (o al tocar a una criatura): 10 de daño en 4 bloques. Todo lo alcanzado queda paralizado 2 s: no puede moverse ni atacar. Clic derecho: apuntar. | Tambor: 8 granadas; recarga 1 cada 1,25 s. |
| PX-71 | **ANTENA** | Fusil de precisión de neón | Clic izquierdo: rayo de 22 de daño con 200 bloques de alcance que atraviesa a 2 criaturas (alcanza hasta 3). Uno cada 1,2 s. Apuntando miras por la mira telescópica. Clic derecho: apuntar. | Batería: 12 disparos; recarga 1 cada 1 s. |
| PX-72 | **VINILO** | Lanzadiscos | Clic izquierdo: disco de vinilo afilado de 9 de daño que corta a todas las criaturas que toca y rebota hasta 4 veces en las paredes. Cada clic lanza uno (hasta 2,5 por segundo). Clic derecho: apuntar. | Cargador: 20 discos; recarga 1 cada 0,4 s. |
| PX-73 | **BAJO** | Cañón de graves | Clic izquierdo: onda de graves en un cono de 40° y 10 bloques que atraviesa paredes y hace 12 de daño a todas las criaturas. Una cada 0,8 s; ignora la armadura. Clic derecho: apuntar. | Batería: 12 ondas; recarga 1 cada 0,75 s. |
| PX-74 | **NEXO** | Lanzadrones | Clic izquierdo: suelta tres minidrones que buscan a los enemigos cercanos y estallan al tocarlos: 8 de daño en 1,5 bloques. Una tanda cada 1,5 s. Clic derecho: apuntar. | Hangar: 30 de energía (3 por tanda); recarga 1 cada 0,3 s. |
| PX-75 | **NEÓN** | Katana de neón | Clic izquierdo: barrido de 120° y 3,6 bloques que alcanza a todas las criaturas delante de ti (10 de daño). Manteniendo el clic, un barrido cada 0,5 s. No atraviesa paredes. | Hoja de luz: no gasta energía. |
| PX-76 | **RELÉ** | Bate eléctrico | Clic izquierdo: golpe de 9 de daño a la criatura que tienes delante (hasta 3,8 bloques) que la deja paralizada 1 s. Manteniendo el clic, un golpe cada 0,7 s. | Batería de descarga: no se gasta. |
| PX-77 | **FRECUENCIA** | Proyector de ondas | Mantén clic izquierdo: haz de ondas de 40 bloques que hace 3 de daño diez veces por segundo y atraviesa a 2 criaturas. Gasta 10 de energía por segundo. Clic derecho: apuntar. | Batería: 100 pulsos; recarga 1 cada 0,1 s. |
| PX-78 | **TUBERÍA** | Pistola de tubo | Clic izquierdo: bala de 14 de daño con 120 bloques de alcance. Una cada 0,7 s. Pólvora de verdad: fogonazo y humo. Clic derecho: apuntar. | Recámara: 10 balas; recarga 1 cada 0,6 s. |
| PX-79 | **CHAPA** | Escopeta de chatarra | Clic izquierdo: 9 perdigones de 3 de daño (alcance 32 bloques). Uno cada 0,9 s. De cerca aciertan casi todos: 27 de daño. Clic derecho: apuntar. | Cartuchos: 12; recarga 1 cada 0,7 s. |
| PX-80 | **CLAVO** | Clavadora automática | Mantén clic izquierdo: 10 clavos por segundo de 4 de daño. Los clavos caen un poco con la distancia. Clic derecho: apuntar. | Peine: 150 clavos; recarga 1 cada 0,1 s. |
| PX-81 | **SERRUCHO** | Lanzasierras | Clic izquierdo: disco de sierra de 11 de daño que corta a todas las criaturas que toca y rebota 3 veces. Uno cada 0,6 s; el disco cae un poco. Clic derecho: apuntar. | Pila: 12 discos; recarga 1 cada 0,6 s. |
| PX-82 | **PERNO** | Ballesta de chatarra | Mantén clic izquierdo para tensar (1,2 s) y suelta: varilla de 10 de daño sin tensar y 28 tensada del todo, que atraviesa a 2 criaturas. Tensada vuela más rápido y más recta. Clic derecho: apuntar. | Carcaj: 10 varillas; recarga 1 cada 0,9 s. |
| PX-83 | **MOLOTOV** | Lanzacócteles | Clic izquierdo: lanza un cóctel molotov (6 de daño si da de lleno) que estalla en un charco de fuego de 2,5 bloques durante 4 s. El fuego hace 3 de daño cada 0,5 s y deja ardiendo 4 s a lo que lo pisa. Clic derecho: apuntar. | Cajón: 10 cócteles; recarga 1 cada 1 s. |
| PX-84 | **BIDÓN** | Mortero de hombro | Clic izquierdo: dispara un bidón que estalla al chocar: 24 de daño en 4,5 bloques. Va en parábola: apunta un poco por encima. Uno cada 1,5 s. Clic derecho: apuntar. | Bidones: 6; recarga 1 cada 2 s. |
| PX-85 | **CACHARRO** | Cañón de chatarra | Clic izquierdo: escupe un trasto de chatarra de 12 de daño que rebota 3 veces en paredes y criaturas. Cada rebote puede alcanzar a otra criatura. Clic derecho: apuntar. | Tolva: 16 trastos; recarga 1 cada 0,6 s. |
| PX-86 | **MANIVELA** | Ametralladora de manivela | Mantén clic izquierdo: 15 balas por segundo de 4 de daño (120 bloques de alcance). Dispersa mucho: mejor a media distancia. Clic derecho: apuntar. | Lata de munición: 300 balas; recarga 1 cada 0,05 s. |
| PX-87 | **GARFIO** | Lanzagarfios | Clic izquierdo: lanza el garfio (unos 45 bloques). Si engancha a una criatura le hace 8 de daño y la arrastra hacia ti. Si engancha un bloque, el cable te lleva volando hasta él (sin daño por caída al llegar). Clic derecho: apuntar. | Carrete: 8 lanzamientos; recarga 1 cada 1 s. |
| PX-88 | **PALANCA** | Mazo de chatarra | Clic izquierdo: golpe de 14 de daño a la criatura que tienes delante (hasta 4 bloques) y onda de 7 a las de alrededor (2,5 bloques). Golpeando el suelo también levanta la onda. Un golpe cada 0,9 s. | Indestructible: no gasta energía. |
| PX-89 | **ÓXIDO** | Machete de chatarra | Clic izquierdo: barrido de 100° y 3 bloques que alcanza a todas las criaturas delante de ti (9 de daño). Manteniendo el clic, un barrido cada 0,45 s. | Indestructible: no gasta energía. |
| PX-90 | **ESPORA** | Lanzaesporas | Mantén clic izquierdo: 4 esporas por segundo que buscan solas a su presa (7 de daño). Cada espora envenena 2 s. Clic derecho: apuntar. | Saco: 40 esporas; recarga 1 cada 0,2 s. |
| PX-91 | **GLIFO** | Rifle de glifos | Clic izquierdo: rayo de glifos de 11 de daño con 80 bloques de alcance que atraviesa a 3 criaturas. Uno cada 0,4 s; ignora la armadura. Clic derecho: apuntar. | Glifos: 30 rayos; recarga 1 cada 0,25 s. |
| PX-92 | **ABISMO** | Lanzapozos gravitatorios | Clic izquierdo: esfera negra que al chocar abre un pozo de gravedad de 5 bloques durante 3 s. El pozo atrae a las criaturas y les hace 2 de daño cada 0,25 s (ignora la armadura). Clic derecho: apuntar. | Núcleo: 4 pozos; recarga 1 cada 3 s. |
| PX-93 | **LARVA** | Lanzalarvas | Clic izquierdo: suelta 4 larvas que saltan por el suelo hacia sus presas (5 de daño cada una). Cada larva salta hasta 8 veces buscando a quién morder. Clic derecho: apuntar. | Panal: 24 de energía (4 por nidada); recarga 1 cada 0,3 s. |
| PX-94 | **ECLIPSE** | Cañón de haz | Mantén clic izquierdo: haz de 48 bloques que atraviesa a todas las criaturas y les hace 4 de daño diez veces por segundo. Ignora la armadura. Gasta 10 de energía por segundo. Clic derecho: apuntar. | Corona: 200 pulsos; recarga 1 cada 0,1 s. |
| PX-95 | **NÉBULA** | Lanzanebulosas | Clic izquierdo: orbe lento de nebulosa que hace 3 de daño cada 0,25 s y frena a todo lo que pasa cerca (2,5 bloques). Al chocar o apagarse deja una nube de 3 bloques durante 4 s: 2 de daño cada 0,5 s y lentitud. Clic derecho: apuntar. | Vejiga: 6 orbes; recarga 1 cada 2 s. |
| PX-96 | **SIMBIONTE** | Espada viva | Clic izquierdo: barrido de 140° y 3,8 bloques que alcanza a todas las criaturas delante de ti (11 de daño, ignora la armadura). Te cura medio corazón por cada criatura alcanzada. Un barrido cada 0,6 s. | Se alimenta de lo que corta: no gasta energía. |
| PX-97 | **ÉTER** | Emisor de levitación | Clic izquierdo: cono de 30° y 12 bloques que hace 6 de daño y hace levitar 1,5 s a las criaturas que ves. Una cada 0,8 s. Clic derecho: apuntar. | Cristales: 12 descargas; recarga 1 cada 0,75 s. |
| PX-98 | **CÉNIT** | Arco celeste | Mantén clic izquierdo para tensar (1 s) y suelta: tres flechas de luz que buscan a sus presas. 6 de daño cada una sin tensar, 16 tensadas del todo. Las flechas giran solas hacia los enemigos de delante. Clic derecho: apuntar. | Luz: 12 tiros; recarga 1 cada 0,8 s. |
| PX-99 | **UMBRAL** | Abridor de grietas | Clic izquierdo: abre una grieta donde apuntas (hasta 32 bloques) durante 3 s. La grieta atrae a todo lo que está a menos de 6 bloques y hace 3 de daño cada 0,25 s a lo que está a menos de 3 (ignora la armadura). Clic derecho: apuntar. | Llaves: 3 grietas; recarga 1 cada 4 s. |
| PX-100 | **OBSERVADOR** | Invocador del ojo | Clic izquierdo: invoca un ojo que flota 15 s donde miras y dispara un rayo de 5 de daño cada 0,5 s a los enemigos que ve (hasta 20 bloques). Como mucho 2 ojos a la vez: al invocar otro se cierra el más viejo. Solo ataca a monstruos y a quien pelea contigo. | Ojos: 4; recarga 1 cada 5 s. |
| PX-101 | **AGUIJÓN** | Lanza aguijón | Clic izquierdo: estocada de 6 bloques que atraviesa a todas las criaturas en línea (12 de daño). Envenena 2 s. Manteniendo el clic, una estocada cada 0,6 s. | Indestructible: no gasta energía. |

En el juego: pasa el ratón por encima del arma y mantén **Mayús** para ver los detalles.

## Fabricación

Todas las armas necesitan un **Núcleo de xeranio**. Recetas de mesa de trabajo (3 × 3, `·` = hueco):

| Objeto | Patrón | Ingredientes |
|---|---|---|
| Núcleo de xeranio | `ARA` / `RDR` / `ARA` | A fragmento de amatista · R redstone · D diamante |
| MARTINETE | `BPB` / `·X·` / `·N·` | B bloque de hierro · P pistón · X núcleo · N lingote de netherita |
| AGUJA | `SLL` / `CXC` / `IN·` | S catalejo · L pararrayos · C lingote de cobre · X núcleo · I lingote de hierro · N lingote de netherita |
| AVISPÓN | `FBF` / `IXI` / `·IR` | F carga ígnea · B vara de blaze · I lingote de hierro · X núcleo · R bloque de redstone |
| COLMENA | `RRR` / `DXD` / `IOI` | R cohete de fuegos artificiales · D dispensador · X núcleo · I bloque de hierro · O observador |
| VÓRTICE | `PEP` / `CXC` / `OIO` | P perla de ender · E ojo de ender · C obsidiana llorosa · X núcleo · O obsidiana · I lingote de hierro |
| CIZALLA | `·N·` / `·N·` / `IXI` | N lingote de netherita · I lingote de hierro · X núcleo |
| TALADRO | `·D·` / `DPD` / `IXI` | D diamante · P pistón · I bloque de hierro · X núcleo |
| SERPENTINA | `··C` / `·C·` / `XL·` | C cadena · X núcleo · L pararrayos |
| CHISPA | `CLC` / `·XI` / `··I` | C lingote de cobre · L pararrayos · X núcleo · I lingote de hierro |
| REMACHE | `III` / `CXC` / `·IB` | I lingote de hierro · C bloque de cobre · X núcleo · B bloque de hierro |
| TRITURADORA | `ITI` / `TXT` / `ITI` | I bloque de hierro · T dinamita · X núcleo |
| ROTOR | `III` / `LXR` / `III` | I lingote de hierro · L pararrayos · R bloque de redstone · X núcleo |
| HALCÓN | `P·P` / `IXI` / `P·P` | P membrana de phantom · I lingote de hierro · X núcleo |
| TRUENO | `·S·` / `LXL` / `IRI` | S catalejo · L pararrayos · X núcleo · I lingote de hierro · R bloque de redstone |
| SOPLETE | `FFF` / `BXB` / `·I·` | F carga ígnea · B vara de blaze · X núcleo · I lingote de hierro |
| ESCARCHA | `BBB` / `IXI` / `·I·` | B hielo azul · I lingote de hierro · X núcleo |
| ECO | `NIN` / `IXI` / `N·N` | N bloque musical · I lingote de hierro · X núcleo |
| BASTIÓN | `OIO` / `IXI` / `OIO` | O obsidiana · I lingote de hierro · X núcleo |
| COLIBRÍ | `··A` / `IXA` / `·I·` | A fragmento de amatista · I lingote de hierro · X núcleo |
| SIERRA | `·S·` / `IXI` / `·I·` | S cortapiedras · I lingote de hierro · X núcleo |
| CENTELLA | `GBI` / `·XI` / `··I` | G bloque de oro · B vara de blaze · I lingote de hierro · X núcleo |
| ARPÓN | `··T` / `IXI` / `P··` | T tridente · I lingote de hierro · X núcleo · P cristales de prismarina |
| MORTERO | `I·I` / `IXI` / `TBT` | I bloque de hierro · X núcleo · T dinamita · B alto horno |
| PRISMA | `QAQ` / `GXG` / `·I·` | Q cuarzo del Nether · A fragmento de amatista · G cristal · X núcleo · I lingote de hierro |
| ENJAMBRE | `HHH` / `IXI` / `·I·` | H panal · I lingote de hierro · X núcleo |
| CÁUSTICA | `SSS` / `BXB` / `·I·` | S bola de slime · B cubo · X núcleo · I lingote de hierro |
| SOLAR | `GSG` / `BXB` / `GIG` | G lingote de oro · S girasol · B polvo de blaze · X núcleo · I bloque de hierro |
| TEMPESTAD | `LLL` / `CXC` / `·I·` | L pararrayos · C bloque de cobre · X núcleo · I lingote de hierro |
| ESQUIRLA | `AAA` / `IXI` / `·S·` | A bloque de amatista · I lingote de hierro · X núcleo · S catalejo |
| CICLÓN | `BWB` / `IXI` / `·I·` | B vara de breeze · W carga de viento · I lingote de hierro · X núcleo |
| MINADOR | `TPT` / `IXI` / `·I·` | T dinamita · P placa de presión para peso pesado · I lingote de hierro · X núcleo |
| CENIZA | `EEE` / `OXO` / `·I·` | E esmeralda · O obsidiana · X núcleo · I lingote de hierro |
| SAETA | `·XS` / `B·S` / `·GS` | X núcleo · S cuerda · B arco · G polvo de piedra luminosa |
| GUADAÑA | `NNI` / `·R·` / `RX·` | N lingote de netherita · I lingote de hierro · R vara de blaze · X núcleo |
| PICA | `··D` / `·R·` / `X··` | D diamante · R vara de breeze · X núcleo |
| YUNQUE | `III` / `GXB` / `··I` | I lingote de hierro · G pólvora · X núcleo · B bloque de hierro |
| CHACAL | `IIG` / `WXI` / `·I·` | I lingote de hierro · G pólvora · W tronco de roble sin corteza · X núcleo |
| TRABUCO | `IIG` / `RXW` / `·W·` | I lingote de hierro · G pólvora · R tinte rojo · X núcleo · W tronco de abeto sin corteza |
| COYOTE | `·IG` / `IXC` / `·W·` | I lingote de hierro · G lingote de oro · X núcleo · C lingote de cobre · W tronco de roble oscuro sin corteza |
| GRANIZO | `III` / `GXG` / `IBI` | I lingote de hierro · G pólvora · X núcleo · B bloque de hierro |
| LINCE | `S··` / `IXI` / `·DI` | S catalejo · I lingote de hierro · X núcleo · D diamante |
| RUGIDO | `III` / `TXF` / `III` | I lingote de hierro · T dinamita · X núcleo · F cohete de fuegos artificiales |
| METEORO | `MFM` / `OXO` / `·S·` | M bloque de magma · F carga ígnea · O obsidiana · X núcleo · S catalejo |
| PULSAR | `PLP` / `EXE` / `·I·` | P bloque de púrpur · L pararrayos · E ojo de ender · X núcleo · I bloque de hierro |
| FALLA | `DBD` / `·X·` / `·R·` | D pizarra profunda rocosa (cobbled deepslate) · B bloque de hierro · X núcleo · R vara de blaze |
| CENTINELA | `DID` / `IXI` / `R·R` | D dispensador · I lingote de hierro · X núcleo · R barrotes de hierro |
| RETORNO | `·G·` / `GXG` / `·I·` | G lingote de oro · X núcleo · I lingote de hierro |
| MAREA | `WPW` / `PXP` / `·I·` | W cubo de agua · P fragmento de prismarina · X núcleo · I lingote de hierro |
| MANGUAL | `··B` / `·CX` / `R··` | B bloque de hierro · C cadena · X núcleo · R vara de blaze |
| ARIETE | `IPI` / `IXI` / `·L·` | I lingote de hierro · P pistón · X núcleo · L cuero |
| BISONTE | `III` / `GXQ` / `··B` | I lingote de hierro · G pólvora · X núcleo · Q cuarzo del Nether · B bloque de hierro |
| VÍBORA | `RII` / `GXI` / `··K` | R polvo de redstone · I lingote de hierro · G pólvora · X núcleo · K bloque de carbón |
| TEJÓN | `·II` / `GX·` / `·K·` | I lingote de hierro · G pólvora · X núcleo · K carbón |
| SOMBRA | `WII` / `·XG` / `·D·` | W lana blanca · I lingote de hierro · X núcleo · G pólvora · D tinte marrón |
| GALGO | `AII` / `·XR` / `·Q·` | A lingote de oro · I lingote de hierro · X núcleo · R polvo de redstone · Q cuarzo del Nether |
| AZOR | `IIR` / `GXI` / `D··` | I lingote de hierro · R polvo de redstone · G pólvora · X núcleo · D tinte marrón |
| CÓNDOR | `S··` / `IXI` / `GDI` | S catalejo · I lingote de hierro · X núcleo · G pólvora · D tinte verde |
| HURÓN | `III` / `GXR` / `·K·` | I lingote de hierro · G pólvora · X núcleo · R polvo de redstone · K carbón |
| BÚHO | `WWI` / `GXI` / `·K·` | W lana blanca · I lingote de hierro · G pólvora · X núcleo · K carbón |
| PIRAÑA | `·IG` / `·XI` / `·K·` | I lingote de hierro · G pólvora · X núcleo · K carbón |
| JABALÍ | `IIG` / `GXD` / `·D·` | I lingote de hierro · G pólvora · X núcleo · D tinte marrón |
| AVALANCHA | `IIG` / `BXN` / `·C·` | I lingote de hierro · G pólvora · B bloque de hierro · X núcleo · N tinte verde · C barril |
| DILUVIO | `III` / `BXR` / `GCG` | I lingote de hierro · B bloque de hierro · X núcleo · R bloque de redstone · G pólvora · C cofre |
| VOLCÁN | `IIT` / `TXT` / `D··` | I lingote de hierro · T dinamita · X núcleo · D tinte verde |
| MAMUT | `S··` / `BXB` / `IGI` | S catalejo · B bloque de hierro · X núcleo · I lingote de hierro · G pólvora |
| PÍXEL | `·GI` / `·XR` / `·P·` | G polvo de piedra luminosa · I lingote de hierro · X núcleo · R redstone · P tinte rosa |
| VOLTIO | `CII` / `LXR` / `·P·` | C lingote de cobre · I lingote de hierro · L pararrayos · X núcleo · R redstone · P tinte cian |
| ESTÁTICA | `CCI` / `RXI` / `·L·` | C bloque de cobre · I lingote de hierro · R bloque de redstone · X núcleo · L tinte verde lima |
| DIODO | `GGL` / `RX·` / `I··` | G cristal · L piedra luminosa · R antorcha de redstone · X núcleo · I lingote de hierro |
| FUSIBLE | `IRI` / `TXT` / `·I·` | I lingote de hierro · R bloque de redstone · T gancho de cuerda trampa · X núcleo |
| ANTENA | `S··` / `LXA` / `·IA` | S catalejo · L pararrayos · X núcleo · A fragmento de amatista · I lingote de hierro |
| VINILO | `·D·` / `IXI` / `·J·` | D disco de música «cat» · I lingote de hierro · X núcleo · J tocadiscos |
| BAJO | `NNN` / `IXI` / `NJN` | N bloque musical · I lingote de hierro · X núcleo · J tocadiscos |
| NEXO | `PEP` / `IXI` / `·R·` | P membrana de phantom · E ojo de ender · I lingote de hierro · X núcleo · R bloque de redstone |
| NEÓN | `··G` / `·G·` / `X··` | G piedra luminosa · X núcleo |
| RELÉ | `··C` / `·C·` / `X··` | C bloque de cobre · X núcleo |
| FRECUENCIA | `SDS` / `IXI` / `·R·` | S sensor de sculk · D sensor de luz solar · I lingote de hierro · X núcleo · R redstone |
| TUBERÍA | `III` / `GX·` / `·P·` | I lingote de hierro · G pólvora · X núcleo · P tablones de roble |
| CHAPA | `II·` / `GXT` / `·PP` | I lingote de hierro · G pólvora · X núcleo · T trampilla de hierro · P tablones de roble |
| CLAVO | `NNY` / `PX·` / `·I·` | N pepita de hierro · Y tinte amarillo · P pistón · X núcleo · I lingote de hierro |
| SERRUCHO | `·S·` / `IXI` / `·B·` | S cortapiedras · I lingote de hierro · X núcleo · B tinte azul |
| PERNO | `ISI` / `BXB` / `·P·` | I barrotes de hierro · S cuerda · B lingote de hierro · X núcleo · P tablones de roble |
| MOLOTOV | `GFG` / `IXI` / `·P·` | G frasco de cristal · F carga ígnea · I lingote de hierro · X núcleo · P tinte verde |
| BIDÓN | `ITI` / `IXI` / `BPB` | I lingote de hierro · T dinamita · X núcleo · B cubo · P tinte rojo |
| CACHARRO | `HCH` / `IXI` / `·P·` | H tolva · C caldero · I lingote de hierro · X núcleo · P tablones de roble |
| MANIVELA | `III` / `GXG` / `PCP` | I lingote de hierro · G pólvora · X núcleo · P tablones de roble · C barril |
| GARFIO | `·TC` / `IXC` / `P··` | T gancho de cuerda trampa · C cadena · I lingote de hierro · X núcleo · P tablones de roble |
| PALANCA | `IAI` / `·X·` / `·I·` | I bloque de hierro · A yunque · X núcleo |
| ÓXIDO | `··I` / `·I·` / `X··` | I lingote de hierro · X núcleo |
| ESPORA | `MSM` / `CXC` / `·C·` | M champiñón marrón · S flor de esporas · C fruta coral · X núcleo |
| GLIFO | `PAP` / `CXC` / `·C·` | P bloque de púrpur · A fragmento de amatista · C fruta coral reventada · X núcleo |
| ABISMO | `OEO` / `CXC` / `·C·` | O obsidiana llorosa · E perla de ender · C fruta coral reventada · X núcleo |
| LARVA | `HSH` / `CXC` / `·C·` | H panal · S bola de slime · C fruta coral reventada · X núcleo |
| ECLIPSE | `GBG` / `CXC` / `C·C` | G bloque de oro · B hormigón negro · C fruta coral reventada · X núcleo |
| NÉBULA | `PGP` / `CXC` / `·C·` | P tinte rosa · G lágrima de ghast · C fruta coral reventada · X núcleo |
| SIMBIONTE | `··E` / `·S·` / `X··` | E ojo de ender · S catalizador de sculk · X núcleo |
| ÉTER | `FFF` / `CXC` / `·C·` | F pluma · C fruta coral reventada · X núcleo |
| CÉNIT | `·GS` / `CXS` / `·GS` | G lingote de oro · S cuerda · C fruta coral reventada · X núcleo |
| UMBRAL | `GEG` / `CXC` / `·C·` | G lingote de oro · E ojo de ender · C fruta coral reventada · X núcleo |
| OBSERVADOR | `FEF` / `CXC` / `·C·` | F ojo de araña fermentado · E ojo de ender · C fruta coral reventada · X núcleo |
| AGUIJÓN | `··T` / `·C·` / `X··` | T espeleotema puntiagudo · C fruta coral reventada · X núcleo |

También aparecen en la pestaña creativa **Proyecto X · Arsenal**.

## Compilar en GitHub (sin instalar nada)

1. Crea un repositorio nuevo en GitHub (puede ser privado), o usa el de la versión anterior.
2. Sube **todo el contenido de esta carpeta**, incluidas las carpetas ocultas `.github` y `gradle`.
   - **Actualizar con un ZIP** (como el de la 7.0, que solo trae los archivos nuevos y los que cambian): **súbelo tal
     cual, sin descomprimirlo**, a la raíz del repositorio (*Add file → Upload files*). Al compilar, el flujo lo
     descomprime encima del código, guarda los archivos en el repositorio, borra el ZIP y compila la versión nueva.
     No borres nada a mano.
   - **Solo la primera vez**, si tu repositorio es de la 6.2 o anterior: cambia `.github/workflows/build.yml` por el
     nuevo, que es el que sabe descomprimir los ZIP (entra en la carpeta `.github/workflows` y súbelo ahí con
     *Add file → Upload files*). Actions no puede cambiar sus propios flujos, así que ese archivo siempre se sube a mano.
   - Si subes una versión completa ya descomprimida, **borra antes la carpeta `src`** del repositorio y sube la
     nueva (así no quedan archivos viejos).
   - Con Git: `git init && git add . && git commit -m "Proyecto X" && git branch -M main && git remote add origin <url> && git push -u origin main`
   - Desde la web: *Add file → Upload files* y arrastra los archivos y carpetas (comprueba que se suben `.github/workflows/build.yml` y `gradle/wrapper/gradle-wrapper.jar`).
3. Al subirlo se ejecuta solo el flujo **Compilar Proyecto X** (pestaña *Actions*). Si no arranca, entra en *Actions → Compilar Proyecto X → Run workflow*.
4. Cuando termine (la primera vez tarda unos minutos porque NeoForge prepara Minecraft), abre la **última** ejecución y descarga el artefacto **proyectox-mod**: dentro está `proyectox-7.0.0.jar`.
5. Copia el `.jar` en la carpeta `mods` de una instalación de **NeoForge 21.1.x para Minecraft 1.21.1**.

## Compilar en tu ordenador

Necesitas **Java 21** (JDK). En la carpeta del proyecto:

```
./gradlew build          # Windows: gradlew.bat build
./gradlew runClient      # abre Minecraft con el mod para probarlo
```

El `.jar` queda en `build/libs/`.

## Estructura

```
src/main/java/com/proyectox/
  ProyectoX.java          clase principal (@Mod)
  item/                   las 101 armas: GunItem (armas de fuego), BulletGun (armas de balas), RealGun (la serie
                          realista PX-51…PX-65, descrita con su ficha técnica), SpecGun y SpecMelee (las series NEÓN,
                          CHATARRA y XENO, con sus fichas y disparos en Arsenal7), MeleeItem (cuerpo a cuerpo),
                          PXWeapon (energía), PropItem (modelos de las piezas que se ven en el mundo)
  entity/                 proyectiles, granadas, discos, dron, singularidad, onda sónica, orbes (obús, sol, cápsula,
                          ácido, cristal, cohete, meteorito, esfera del PULSAR, granada de 40 mm), arpones y minas, anillo, flecha y
                          chakram perforantes, zonas (charco, nube, tornado, enjambre, grieta, torreta); v7: SpecBolt
                          (proyectiles de las series nuevas, con su ficha BoltSpec) y SpecField (fuego, pozo de gravedad,
                          nebulosa, grieta y el ojo del OBSERVADOR)
  effect/                 parálisis (el efecto del terremoto del MARTINETE y de la esfera del PULSAR)
  fx/                     avisos de efectos que el servidor envía a los clientes
  client/                 apuntado, retroceso, retícula, controles y renderizadores
  client/fx/              motor de efectos: partículas aditivas, haces, rayos, anillos y aspecto de cada proyectil
  network/                paquetes de controles (cliente → servidor) y de efectos (servidor → cliente)
  registry/               objetos, entidades, sonidos, componentes, tipos de daño, efectos, pestaña
  util/                   puntería, daño sin empuje, efectos de sonido, tareas diferidas
src/main/resources/
  assets/proyectox/models/item/   modelos OBJ + MTL + JSON (cargador neoforge:obj)
  assets/proyectox/textures/item/ texturas de las armas (hasta 1024 px)
  assets/proyectox/textures/fx/   atlas de efectos 2048 px (filtrado lineal)
  assets/proyectox/textures/mob_effect/ icono de la parálisis
  assets/proyectox/sounds.json    sonidos (los efectos se componen con sonidos de Minecraft)
  assets/proyectox/sounds/arma/   disparos, corredera, cerrojo, tambor y explosión sintetizados (OGG mono)
  assets/proyectox/lang/          español (es_*) e inglés (en_us)
  data/proyectox/                 recetas y tipos de daño
```

Notas:
- Los disparos de las armas de pólvora son archivos `.ogg` propios (`sounds/arma`); el resto de sonidos
  reutilizan sonidos de Minecraft con otro tono. Puedes sustituir cualquiera sin tocar el código.
- Las texturas de las armas son de hasta 1024 × 1024 px: el atlas de bloques crece bastante (≈ 8192 × 8192).
  Cualquier gráfica de los últimos años lo admite sin problemas.
- Versión de NeoForge, nombre, autor, licencia y versión del mod se cambian en `gradle.properties`.
