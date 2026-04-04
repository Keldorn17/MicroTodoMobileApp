# Todo App - Android Projekt

## Projekt Összefoglaló

Ez a projekt egy natív Android feladatkezelő (Todo) alkalmazás fejlesztése, amely egy külsős REST API-val kommunikálva kezeli a felhasználói adatokat és a feladatokat. Az alkalmazás kiemelt fókuszt helyez a csapatmunkára (feladatok megosztása), a vizuális visszajelzésekre (statisztikák) és a letisztult, modern UI/UX dizájnra a meglévő Figma tervek alapján.

A felhasználók azonosítását és a biztonságos munkamenet-kezelést egy integrált **Keycloak** szerver biztosítja.

---

## Főbb Funkciók

* **Biztonságos Autentikáció:** Bejelentkezés, regisztráció és munkamenet-kezelés Keycloak integrációval.
* **Komplex Feladatkezelés (CRUD):** Feladatok létrehozása, szerkesztése, törlése és állapotuk (Kész/Folyamatban) módosítása.
    * Határidők (dátum és idő) és prioritási szintek (Not Required, Low, Normal, High, Critical) beállítása.
* **Kategóriák Kezelése:** Egyedi kategóriák (címkék) létrehozása, hozzárendelése a feladatokhoz, és törlése.
* **Megosztás és Együttműködés:**
    * Feladatok megosztása más felhasználókkal email cím alapján.
    * **Jogosultsági szintek (RBAC):** Read, Write, Manage, Owner.
* **Részletes Statisztikák:**
    * Interaktív kördiagramok (Saját/Megosztott arány, Befejezett/Folyamatban lévő státuszok).
    * Oszlopdiagramok (Prioritás szerinti eloszlás).
* **Személyre szabhatóság:** Témaválasztás a Beállítások menüben.

---

## Csapat és Feladatkiosztás (4 fő)

A hatékony párhuzamos munkavégzés érdekében a feladatokat képernyők és logikai modulok szerint osztottuk fel. Minden fejlesztő felelős a saját részéhez tartozó UI implementálásáért és a vonatkozó API végpontok bekötéséért.

### Csíkos Csaba: "Core, Auth & Settings" (Alapok és Beállítások)
**Felelősségi körök:** A projekt alapjainak lerakása, azonosítás és az általános alkalmazás-keret.
* **Alapstruktúra:** Projekt setup, hálózati réteg konfigurálása, alsó navigációs sáv és fő routing elkészítése.
* **Autentikáció:** Keycloak login/logout folyamat integrálása, tokenek biztonságos tárolása és csatolása az API hívásokhoz.
* **Settings (Beállítások) oldal:** A Settings felület lefejlesztése, a téma váltás globális logikájának megírása, profil beállítások alapjai.

### Patai Zoltán: "Dashboard & Todo Cards" (Listázás és Todo Kártyák)
**Felelősségi körök:** A meglévő feladatok megjelenítése és a kártyák létrehozása.
* **Dashboard:** A főképernyő felépítése, a komplex lista elemek (kártyák) leprogramozása a tervek alapján (címkékkel, határidőkkel, checkbox-szal).
* **Szűrés/Lista logika:** A felső statisztikai számlálók (Finished, Unfinished, Total) logikája, adatok lekérése (GET) az API-ból.

### Szappanos Szabolcs: "Forms, Sharing & Permissions" (Űrlapok és Jogosultságok)
**Felelősségi körök:** Új tartalmak létrehozása és a megosztási logika implementálása.
* **New Todo oldal:** A feladat létrehozó/szerkesztő űrlap elkészítése (cím, leírás, határidő/idő választó dialogok, prioritás legördülő menü). Csatlakozás (POST/PUT) az API-hoz.
* **Manage Shares oldal:** A megosztási felület UI-ja, felhasználók hozzáadása email alapján.
* **Manage Categories oldal:** A kategóriák kezelését (létrehozás, listázás, törlés) végző UI elkészítése és az ehhez tartozó API hívások bekötése.
* **Jogosultság-kezelés:** A jogosultsági szintek (Owner, Manage, Write, Read) legördülő menüjének kezelése, és a módosítási lehetőségek UI szintű letiltása, ha a felhasználónak nincs joga szerkeszteni.

### Marosi Róbert: "Statistics & Visualization" (Statisztika és Vizualizáció)
**Felelősségi körök:** Az API által adott adatok látványos, grafikus megjelenítése.
* **Lapozható Statisztika Oldal:** Egyetlen statisztikai oldal felépítése, amelyen belül lapozni lehet a különböző grafikonos nézetek között.
* **Diagramok implementálása:** Külsős könyvtár (MPAndroidChart) integrálása, a kördiagramok (Donut chart) és a prioritás alapú oszlopdiagram (Bar chart) megrajzolása.
* **Adatfeldolgozás:** A statisztikai API végpontok bekötése és a kapott JSON adatok diagramokhoz való illesztése.
