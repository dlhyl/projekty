# Datove struktury

Implementoval som v jazyku C nasledovne datove struktury:

- [Hash Table s Chainingom](data_structures/MyHashTable.c)
- [Splay Tree](data_structures/mySplay.c)

ktore som porovnal uz s existujucimi:

- Red-Black Tree
- Hash Table s Linear Probing

pomocou vlastneho [testera](data_structures/Tester.c), ktory meral casy vkladania aj hladania.

Vysledky som zhrnul v [dokumentacii](data_structures/dokumentacia.pdf).

# Dijkstra

Implementacia dijkstrovho algoritmu s binarnou haldou v rieseni problemu tzv. popolvara, ktory hlada princezne cez co najkratsiu moznu trasu.

[Zdrojovy kod.](dijkstra/popolvar.c)

[Dokumentacia](dijkstra/dokumentacia.pdf)

# Vlastna implementacia mallocu

Implementacia dynamickej spravy pamate v C pomocou metody spajaneho zoznamu volnych blokov zoradenych podla velkosti. Dolezite bolo urcenie obsahu a velkosti hlaviciek pre co najmensiu reziu a zaroven efektivne alokovanie/uvolnovanie pamate.

[Zdrojovy kod.](malloc/malloc.c)

[Dokumentacia](malloc/dokumentacia.pdf)