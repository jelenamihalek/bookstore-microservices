# Bookstore Microservices

## 1. Uvod

Razvoj modernih softverskih sistema sve više se oslanja na mikroservisnu arhitekturu, koja omogućava podelu kompleksnih aplikacija na manje, nezavisne celine. Za razliku od monolitnih aplikacija, gde su sve funkcionalnosti objedinjene u okviru jedne aplikacije, mikroservisni pristup omogućava da svaka poslovna oblast bude implementirana kao zaseban servis sa jasno definisanim odgovornostima. Na taj način postiže se veća modularnost sistema, jednostavnije održavanje, lakše testiranje i mogućnost nezavisnog razvoja i proširivanja pojedinačnih komponenti.

U okviru ovog projekta razvijen je distribuirani informacioni sistem za upravljanje online knjižarom pod nazivom **Bookstore Microservices**. Sistem je realizovan korišćenjem Spring Boot i Spring Cloud tehnologija i sastoji se od više međusobno povezanih mikroservisa koji zajedno obavljaju poslovne procese neophodne za funkcionisanje jedne online prodavnice knjiga. Osnovna namena sistema jeste omogućavanje upravljanja korisnicima, knjigama, porudžbinama, procesom plaćanja i slanjem obaveštenja korisnicima. Svaka od navedenih funkcionalnosti implementirana je kao zaseban mikroservis koji poseduje sopstvenu poslovnu logiku i odgovoran je za određeni deo sistema. Na ovaj način postiže se jasno razdvajanje odgovornosti između servisa i smanjuje međuzavisnost komponenti.

Pored poslovnih mikroservisa, sistem sadrži i infrastrukturne komponente koje omogućavaju njihovo međusobno funkcionisanje. Eureka Discovery Server koristi se za registraciju i pronalaženje servisa unutar sistema, dok API Gateway predstavlja jedinstvenu ulaznu tačku kroz koju prolaze svi zahtevi korisnika. Na taj način klijentske aplikacije nemaju direktnu komunikaciju sa pojedinačnim servisima, već se sva komunikacija odvija preko gateway komponente.

Komunikacija između mikroservisa realizovana je kombinacijom sinhronog i asinhronog pristupa. Sinhrona komunikacija implementirana je korišćenjem OpenFeign klijenata i koristi se u situacijama kada je neophodno trenutno dobiti odgovor od drugog servisa, kao što je provera korisnika, dostupnosti knjige ili obrada plaćanja. Sa druge strane, za procese koji ne zahtevaju neposredan odgovor implementirana je asinhrona komunikacija korišćenjem RabbitMQ message broker-a. Na taj način se proces slanja notifikacija odvaja od procesa obrade porudžbine, čime se povećava pouzdanost i skalabilnost sistema. Sistem koristi PostgreSQL bazu podataka za čuvanje podataka, dok su kvalitet i ispravnost implementacije provereni korišćenjem unit i integracionih testova. Tokom razvoja posebna pažnja posvećena je primeni principa mikroservisne arhitekture, pravilnom razdvajanju poslovne logike između servisa, kao i implementaciji mehanizama koji omogućavaju jednostavnije održavanje i dalji razvoj sistema.

Cilj projekta jeste demonstracija primene savremenih tehnologija i obrazaca razvoja distribuiranih sistema kroz implementaciju funkcionalne mikroservisne aplikacije koja objedinjuje više poslovnih procesa u jedinstven informacioni sistem.

---

## 2. Opis poslovne logike sistema

Poslovna logika sistema zasniva se na simulaciji rada online knjižare koja korisnicima omogućava pregled dostupnih knjiga, kreiranje porudžbina, obradu plaćanja i dobijanje obaveštenja o statusu porudžbine. Sistem je podeljen na više međusobno povezanih mikroservisa, pri čemu je svaki servis odgovoran za određeni segment poslovnog procesa. Na taj način postiže se jasno razdvajanje odgovornosti, jednostavnije održavanje sistema i lakše proširivanje funkcionalnosti.

### 2.1 Upravljanje korisnicima

Upravljanje korisnicima predstavlja jednu od osnovnih funkcionalnosti sistema. Za svakog korisnika čuvaju se podaci neophodni za identifikaciju i korišćenje sistema, kao što su ime, email adresa i ostale relevantne informacije. Korisnici predstavljaju centralne aktere sistema, budući da su sve porudžbine direktno povezane sa konkretnim korisničkim nalogom. Prilikom kreiranja porudžbine sistem najpre proverava da li korisnik postoji i da li je moguće povezati porudžbinu sa odgovarajućim nalogom. Na ovaj način obezbeđuje se integritet podataka i sprečava kreiranje porudžbina za nepostojeće korisnike.

### 2.2 Upravljanje knjigama

Sistem omogućava upravljanje katalogom knjiga koje su dostupne za kupovinu. Za svaku knjigu evidentiraju se osnovni podaci, uključujući naziv, autora, cenu i količinu dostupnu na stanju. Korisnici mogu pregledati dostupne knjige, dok se tokom procesa poručivanja vrši dodatna provera postojanja knjige i raspoložive količine. Ukoliko knjiga nije pronađena ili tražena količina premašuje trenutno stanje na lageru, porudžbina se ne može realizovati. Ovakav pristup omogućava održavanje tačnih podataka o dostupnim knjigama i sprečava prodaju artikala koji nisu na stanju.

### 2.3 Kreiranje porudžbine

Proces kreiranja porudžbine predstavlja centralni poslovni proces sistema i povezuje više različitih mikroservisa u jedinstvenu funkcionalnu celinu.

Nakon što korisnik pošalje zahtev za kupovinu određene knjige, sistem najpre identifikuje korisnika i proverava validnost unetih podataka. Zatim se proverava da li tražena knjiga postoji u sistemu i da li je raspoloživa dovoljna količina primeraka. Ukoliko su svi uslovi zadovoljeni, količina knjige na stanju se umanjuje za broj naručenih primeraka i kreira se nova porudžbina. U početnoj fazi porudžbina dobija status koji označava da je proces obrade u toku, nakon čega se pokreće procedura plaćanja.

Ovakav način rada omogućava da se poslovna pravila izvršavaju redom i da se svaka porudžbina kreira samo ukoliko su ispunjeni svi neophodni uslovi.

### 2.4 Obrada plaćanja

Nakon uspešno kreirane porudžbine pokreće se proces obrade plaćanja. Sistem izračunava ukupan iznos porudžbine na osnovu cene knjige i količine koju je korisnik naručio, a zatim prosleđuje podatke servisu zaduženom za obradu plaćanja.

Rezultat procesa plaćanja direktno utiče na dalji tok obrade porudžbine. Ukoliko je plaćanje uspešno izvršeno, status porudžbine se ažurira i označava kao uspešno potvrđena porudžbina. U slučaju neuspešnog plaćanja ili greške tokom komunikacije sa servisom za obradu plaćanja, porudžbina dobija status neuspešne porudžbine. Kako bi se očuvala konzistentnost podataka, prethodno rezervisana količina knjiga vraća se nazad na stanje i ponovo postaje dostupna za buduće kupovine.

Na ovaj način obezbeđuje se ispravno upravljanje zalihama i sprečava gubitak podataka usled neuspešno realizovanih transakcija.

### 2.5 Slanje notifikacija

Jedna od važnih funkcionalnosti sistema jeste obaveštavanje korisnika o rezultatu procesa poručivanja. Nakon završetka obrade porudžbine korisnik dobija informaciju o tome da li je njegova porudžbina uspešno realizovana ili je došlo do greške tokom procesa kupovine.

Za razliku od ostalih poslovnih operacija koje zahtevaju trenutni odgovor, slanje notifikacija realizovano je korišćenjem asinhrone komunikacije. Nakon završetka obrade porudžbine generiše se događaj koji se prosleđuje RabbitMQ message broker-u. Notification Service preuzima poruku iz reda i na osnovu dobijenih podataka šalje email obaveštenje korisniku.

Ovakav pristup omogućava da proces slanja email poruka bude potpuno odvojen od procesa kreiranja porudžbine. Na taj način korisnik ne mora da čeka završetak slanja email-a kako bi dobio odgovor o uspešnosti svoje porudžbine, što doprinosi boljim performansama i većoj otpornosti sistema.

### 2.6 Tok izvršavanja porudžbine

Kompletan poslovni proces obrade porudžbine sastoji se od sledećih koraka:

1. Korisnik šalje zahtev za kreiranje porudžbine.
2. Sistem identifikuje korisnika i proverava njegovo postojanje.
3. Proverava se postojanje knjige i raspoloživa količina na stanju.
4. Stanje knjige se umanjuje za naručenu količinu.
5. Kreira se nova porudžbina u sistemu.
6. Pokreće se proces obrade plaćanja.
7. Status porudžbine se ažurira u zavisnosti od rezultata plaćanja.
8. Generiše se događaj za slanje notifikacije.
9. RabbitMQ prosleđuje događaj Notification servisu.
10. Korisniku se šalje email obaveštenje o rezultatu porudžbine.

Opisani proces predstavlja centralnu poslovnu funkcionalnost sistema i povezuje sve ključne mikroservise u jedinstvenu poslovnu celinu.

---

## 3. Arhitektura sistema

Arhitektura sistema zasnovana je na mikroservisnom pristupu, pri čemu je kompletna funkcionalnost aplikacije podeljena na više nezavisnih servisa. Svaki servis ima jasno definisanu odgovornost i zadužen je za određeni deo poslovne logike. Na ovaj način postiže se veća modularnost sistema, jednostavnije održavanje, lakše testiranje i mogućnost nezavisnog razvoja pojedinačnih komponenti.

Za međusobnu komunikaciju servisa koriste se infrastrukturne komponente koje omogućavaju pronalaženje servisa i usmeravanje zahteva. Korisnici ne komuniciraju direktno sa poslovnim servisima, već svi zahtevi prolaze kroz jedinstvenu ulaznu tačku sistema.

### 3.1 Pregled arhitekture sistema

Sistem se sastoji od dve osnovne grupe komponenti:

- infrastrukturnih komponenti
- poslovnih mikroservisa

Infrastrukturne komponente obezbeđuju registraciju servisa i rutiranje zahteva, dok poslovni mikroservisi implementiraju konkretnu funkcionalnost online knjižare.

U okviru sistema implementirani su sledeći servisi:

**Infrastrukturne komponente:**

- Eureka Discovery Server
- API Gateway

**Poslovni mikroservisi:**

- User Service
- Book Service
- Order Service
- Payment Service
- Notification Service

Pored navedenih servisa, projekat sadrži i zajedničke biblioteke koje omogućavaju deljenje DTO objekata, događaja, izuzetaka i pomoćnih klasa između mikroservisa.

U nastavku dokumentacije prikazan je dijagram arhitekture sistema.

### 3.2 Eureka Discovery Server

Eureka Discovery Server predstavlja centralnu komponentu za registraciju i pronalaženje servisa unutar sistema.

Prilikom pokretanja aplikacije svaki mikroservis se registruje na Eureka server i postaje dostupan ostalim servisima. Na taj način servisi ne moraju da poznaju fizičke adrese drugih servisa, već se komunikacija ostvaruje korišćenjem naziva servisa registrovanih u Eureka registru.

Korišćenjem servisne registracije postiže se veća fleksibilnost sistema, jer se adrese servisa mogu menjati bez potrebe za izmenom konfiguracije ostalih komponenti.

Pored registracije servisa, Eureka omogućava pregled trenutno aktivnih instanci sistema putem web interfejsa, što olakšava razvoj, testiranje i nadzor sistema.

### 3.3 API Gateway

API Gateway predstavlja jedinstvenu ulaznu tačku sistema i zadužen je za prijem svih zahteva koji dolaze od korisnika.

Umesto direktnog pristupa pojedinačnim mikroservisima, klijenti komuniciraju isključivo sa gateway komponentom. Nakon prijema zahteva, gateway na osnovu definisanih ruta određuje kojem servisu zahtev treba da bude prosleđen.

Korišćenje API Gateway komponente donosi više prednosti. Omogućeno je centralizovano upravljanje rutiranjem zahteva, jednostavnija implementacija bezbednosnih mehanizama, kao i sakrivanje interne strukture sistema od krajnjih korisnika.

Na ovaj način korisnici ne moraju da poznaju portove i lokacije pojedinačnih servisa, već sve zahteve šalju preko jedinstvene pristupne tačke.

### 3.4 User Service

User Service predstavlja mikroservis zadužen za upravljanje korisničkim nalozima i centralno mesto za obradu svih informacija vezanih za korisnike sistema.

Njegova osnovna odgovornost jeste registracija novih korisnika, pretraga postojećih korisnika, izmena korisničkih podataka i upravljanje korisničkim ulogama. Servis obezbeđuje da svi ostali delovi sistema koriste jedinstven izvor informacija o korisnicima.

Prilikom registracije novog korisnika sistem automatski dodeljuje ulogu USER. Korisnicima nije dozvoljeno da tokom procesa registracije sami definišu svoju ulogu, čime se sprečava mogućnost neovlašćenog dobijanja administratorskih privilegija.

Radi zaštite osetljivih podataka, lozinke se pre čuvanja u bazi podataka obrađuju korišćenjem BCrypt algoritma za hashovanje. Na taj način lozinke se nikada ne čuvaju u originalnom obliku, što predstavlja važan bezbednosni mehanizam sistema.

Posebna poslovna logika implementirana je za upravljanje administratorskim nalozima. Prilikom pokretanja sistema automatski se kreira podrazumevani administratorski nalog ukoliko on već ne postoji u bazi podataka. Time se obezbeđuje da sistem uvek poseduje barem jednog administratora koji može upravljati korisnicima.

Dodatno, implementirano je pravilo koje sprečava brisanje poslednjeg administratorskog naloga. Pre svakog brisanja administratora sistem proverava broj postojećih administratora i dozvoljava operaciju samo ukoliko postoji najmanje još jedan aktivan administratorski nalog. Na ovaj način sprečava se situacija u kojoj bi sistem ostao bez korisnika sa administrativnim privilegijama.

User Service učestvuje i u drugim poslovnim procesima sistema. Tokom kreiranja porudžbine Order Service koristi ovaj servis kako bi identifikovao korisnika i preuzeo podatke neophodne za nastavak procesa kupovine.

### 3.5 Book Service

Book Service zadužen je za upravljanje knjigama koje se nalaze u ponudi online knjižare.

U okviru ovog servisa čuvaju se informacije o knjigama, njihovim cenama i raspoloživim količinama. Servis omogućava dodavanje novih knjiga, pregled postojećih knjiga, izmenu podataka i proveru raspoloživosti artikala.

Tokom procesa kreiranja porudžbine Order Service komunicira sa Book Service servisom kako bi proverio da li tražena knjiga postoji i da li je dostupna dovoljna količina primeraka.

Pored toga, Book Service učestvuje u ažuriranju stanja lagera nakon uspešnog ili neuspešnog procesa kupovine. Ukoliko je porudžbina uspešno realizovana, stanje knjige ostaje umanjeno za naručenu količinu. U slučaju neuspešnog plaćanja ili greške tokom obrade, prethodno rezervisana količina vraća se na stanje kako bi bila dostupna za buduće kupovine.

Na ovaj način servis obezbeđuje tačnost podataka o raspoloživim knjigama i sprečava prodaju artikala kojih nema na stanju.

### 3.6 Order Service

Order Service predstavlja centralni poslovni servis sistema i najvažniju komponentu kompletnog procesa kupovine.

Njegova osnovna odgovornost nije samo čuvanje podataka o porudžbinama, već i koordinacija komunikacije između više različitih mikroservisa. Ovaj servis povezuje korisnike, knjige, plaćanja i notifikacije u jedinstven poslovni proces.

Prilikom kreiranja porudžbine servis najpre identifikuje korisnika na osnovu autentifikacionih podataka i proverava njegovo postojanje u sistemu. Nakon toga vrši validaciju zahteva i proverava da li je količina proizvoda veća od nule, kao i da li je identifikator knjige ispravan.

Nakon uspešne validacije korisnika, servis komunicira sa Book Service servisom kako bi proverio da li tražena knjiga postoji i da li je raspoloživa dovoljna količina primeraka. Ukoliko knjiga nije pronađena ili nema dovoljno primeraka na stanju, proces se prekida i korisniku se vraća odgovarajuća poruka o grešci.

U slučaju uspešne provere, stanje knjige se odmah umanjuje kako bi se sprečilo da više korisnika istovremeno rezerviše iste primerke knjige. Nakon toga kreira se nova porudžbina sa početnim statusom PENDING i pokreće se proces obrade plaćanja.

Order Service zatim komunicira sa Payment Service servisom koji obrađuje zahtev za plaćanje. U zavisnosti od rezultata obrade, status porudžbine se menja u CONFIRMED ili FAILED.

Ukoliko dođe do neuspešnog plaćanja ili greške tokom komunikacije sa servisom za plaćanje, prethodno rezervisana količina knjiga vraća se nazad na stanje kako bi bila dostupna za buduće kupovine. Time se održava konzistentnost podataka između porudžbina i stanja lagera.

Pored koordinacije procesa kupovine, Order Service omogućava korisnicima pregled sopstvenih porudžbina, pristup pojedinačnim porudžbinama i kontrolu pristupa podacima kako bi korisnici mogli videti isključivo svoje porudžbine.

Nakon završetka procesa obrade porudžbine servis generiše događaj koji se šalje RabbitMQ message broker-u. Time se pokreće proces slanja notifikacija bez direktnog pozivanja Notification servisa, čime se ostvaruje asinhrona komunikacija između komponenti sistema.

### 3.7 Payment Service

Payment Service zadužen je za obradu i evidenciju svih plaćanja u sistemu.

Prilikom kreiranja porudžbine Order Service ovom servisu prosleđuje podatke o transakciji, uključujući identifikator porudžbine, identifikator korisnika i ukupan iznos za plaćanje. Nakon prijema zahteva vrši se obrada transakcije i određuje rezultat plaćanja. Rezultat se zatim vraća Order Service servisu koji na osnovu njega određuje konačan status porudžbine. Pored same obrade plaćanja, servis omogućava pregled istorije izvršenih transakcija. Korisnicima je dozvoljen pristup isključivo sopstvenim podacima o plaćanjima, dok se prilikom svakog zahteva vrši dodatna provera identiteta korisnika.

Na ovaj način sprečava se neovlašćen pristup finansijskim podacima drugih korisnika i obezbeđuje zaštita osetljivih informacija.

Izdvajanjem procesa plaćanja u zaseban mikroservis ostvarena je jasna podela odgovornosti između poslovnih komponenti sistema i omogućena jednostavnija integracija sa stvarnim sistemima za elektronsko plaćanje u budućim verzijama aplikacije.

### 3.8 Notification Service

Notification Service zadužen je za slanje obaveštenja korisnicima nakon završetka procesa obrade porudžbine.

Za razliku od ostalih servisa koji koriste sinhronu komunikaciju, Notification Service komunicira sa Order Service servisom putem RabbitMQ message broker-a. Nakon uspešno obrađene porudžbine ili neuspešne transakcije, Order Service generiše događaj koji se smešta u RabbitMQ red poruka. Notification Service preuzima poruku iz reda, obrađuje primljene podatke i korisniku šalje odgovarajuće email obaveštenje.

Pored slanja email poruka, servis evidentira informacije o poslatim notifikacijama, uključujući status uspešnosti slanja. Na taj način omogućeno je praćenje istorije notifikacija i lakše otkrivanje eventualnih problema tokom rada sistema.

Korišćenjem asinhrone komunikacije postiže se veća otpornost sistema, jer proces slanja email poruka ne utiče direktno na brzinu obrade porudžbine. Ukoliko servis za slanje notifikacija trenutno nije dostupan, poruka ostaje sačuvana u RabbitMQ redu i biće obrađena nakon ponovnog uspostavljanja rada servisa.

---

## 4. Komunikacija između mikroservisa

Jedna od ključnih karakteristika ovog sistema jeste korišćenje različitih tipova komunikacije između mikroservisa, u zavisnosti od poslovnih zahteva i prirode same operacije. U okviru sistema implementirane su i sinhrona i asinhrona komunikacija, čime je postignuta bolja modularnost, skalabilnost i otpornost sistema.

### 4.1 Sinhrona komunikacija

Za sinhronu komunikaciju između mikroservisa korišćen je OpenFeign. Ovakav način komunikacije primenjuje se u situacijama kada je jednom servisu potrebno da odmah dobije odgovor od drugog servisa kako bi mogao da nastavi izvršavanje poslovne logike.

Centralnu ulogu u ovoj komunikaciji ima Order Service, koji prilikom obrade porudžbine komunicira sa više različitih servisa.

Prilikom kreiranja porudžbine, Order Service najpre kontaktira User Service kako bi dobio informacije o korisniku koji kreira porudžbinu. Nakon toga ostvaruje komunikaciju sa Book Service servisom radi preuzimanja informacija o knjigama koje se nalaze u porudžbini. Na kraju se uspostavlja komunikacija sa Payment Service servisom kako bi se izvršila obrada plaćanja.

Sve navedene komunikacije realizovane su putem OpenFeign klijenata, što omogućava jednostavno definisanje REST poziva između mikroservisa bez potrebe za ručnim korišćenjem HTTP klijenata.

Prednosti ovakvog pristupa su:

- jednostavnija implementacija komunikacije između servisa,
- bolja čitljivost i održavanje koda,
- automatska integracija sa Eureka Discovery Server komponentom,
- lakše proširenje sistema novim servisima.

Međutim, sinhrona komunikacija podrazumeva da servis koji poziva drugi servis mora da sačeka odgovor pre nastavka izvršavanja, zbog čega se koristi samo kada je odgovor neophodan za nastavak poslovnog procesa. OpenFeign klijenti koriste Eureka Discovery Server za pronalaženje instanci servisa, zbog čega nije potrebno definisati fizičke adrese mikroservisa unutar aplikacionog koda.

### 4.2 Asinhrona komunikacija

Pored sinhrone komunikacije, sistem koristi i asinhronu komunikaciju zasnovanu na RabbitMQ message broker-u.

Ovakav pristup primenjen je za slanje notifikacija nakon uspešno obrađene porudžbine. Za razliku od sinhronog pristupa, servis koji šalje poruku ne mora da čeka da druga strana obradi zahtev, već samo prosleđuje događaj brokeru i nastavlja sa izvršavanjem. Nakon završetka procesa obrade porudžbine, Order Service kreira događaj koji sadrži informacije potrebne za slanje notifikacije korisniku, bez obzira na to da li je porudžbina uspešno realizovana ili je došlo do neuspešnog plaćanja.

RabbitMQ preuzima odgovornost za čuvanje i prosleđivanje poruke odgovarajućem potrošaču. Notification Service sluša definisani queue i preuzima poruke čim postanu dostupne. Nakon prijema događaja, Notification Service obrađuje pristigle podatke i kreira email poruku koja se šalje korisniku putem SMTP servera. Tokom razvoja i testiranja sistema korišćen je Mailtrap servis kako bi se simuliralo slanje email poruka bez potrebe za korišćenjem stvarnog email servera.

Tok asinhrone komunikacije može se opisati sledećim koracima:

1. Korisnik kreira porudžbinu.
2. Order Service obrađuje porudžbinu.
3. Order Service objavljuje događaj u RabbitMQ.
4. RabbitMQ čuva i prosleđuje događaj.
5. Notification Service preuzima događaj.
6. Notification Service generiše email notifikaciju.
7. Email se šalje korisniku putem SMTP servera.

Korišćenje RabbitMQ-a donosi nekoliko značajnih prednosti:

- smanjenje međuzavisnosti između mikroservisa,
- bolju otpornost sistema na privremene greške,
- mogućnost obrade velikog broja događaja,
- jednostavnije skaliranje servisa za notifikacije,
- efikasniju obradu zadataka koji ne zahtevaju trenutni odgovor korisniku.

Kombinacijom sinhrone i asinhrone komunikacije ostvarena je fleksibilna arhitektura koja omogućava efikasnu realizaciju poslovnih procesa uz zadržavanje visokog nivoa modularnosti i proširivosti sistema.

---

## 5. Kontinuirana integracija (CI) korišćenjem GitHub Actions

U okviru projekta implementiran je proces kontinuirane integracije (Continuous Integration – CI) korišćenjem GitHub Actions platforme. Cilj ovog procesa je automatska provera ispravnosti sistema nakon svake izmene koda koja se šalje na Git repozitorijum. Kontinuirana integracija omogućava rano otkrivanje grešaka i osigurava da sve komponente sistema ostanu međusobno kompatibilne tokom razvoja. Na ovaj način se smanjuje mogućnost unošenja neispravnog koda u glavnu granu projekta i povećava pouzdanost celokupnog sistema.

### 5.1 GitHub Actions workflow

Za potrebe projekta kreiran je GitHub Actions workflow koji se automatski pokreće prilikom slanja izmena na glavnu granu repozitorijuma (`main`). Prilikom izvršavanja workflow-a automatski se pokreće virtuelno Linux okruženje u okviru kojeg se izvršavaju svi definisani koraci procesa izgradnje i testiranja sistema.

Workflow obavlja sledeće aktivnosti:

- preuzimanje izvornog koda iz Git repozitorijuma,
- instalaciju Java 17 okruženja,
- pokretanje PostgreSQL baze podataka kao servisnog kontejnera,
- pokretanje RabbitMQ servisa,
- instalaciju zajedničkih biblioteka (Util i Service Library),
- kompajliranje mikroservisa,
- izvršavanje testova za sve implementirane servise.

Na ovaj način se proverava da li je projekat moguće uspešno izgraditi i pokrenuti bez ručne intervencije.

### 5.2 Automatizovano testiranje

Nakon uspešne instalacije svih zavisnosti, workflow pokreće testove za svaki mikroservis pojedinačno.

Tokom procesa testiranja proveravaju se:

- poslovna logika servisa,
- ispravnost REST kontrolera,
- komunikacija između slojeva aplikacije,
- validacija ulaznih podataka,
- obrada izuzetaka,
- rad integracionih testova.

Automatskim izvršavanjem testova nakon svake izmene koda obezbeđuje se da postojeće funkcionalnosti ostanu ispravne i nakon dodavanja novih funkcionalnosti ili refaktorisanja koda.

### 5.3 Servisi korišćeni tokom CI procesa

Pojedini mikroservisi zahtevaju dodatne infrastrukturne komponente kako bi testovi mogli uspešno da se izvrše.

Zbog toga se tokom CI procesa automatski pokreću:

- **PostgreSQL** baza podataka koja se koristi za čuvanje podataka mikroservisa. Tokom pokretanja workflow-a kreira se privremena baza podataka koja se koristi isključivo za potrebe testiranja.
- **RabbitMQ** servis koji se koristi za asinhronu komunikaciju između mikroservisa. Tokom izvršavanja testova omogućava simulaciju slanja i prijema događaja između servisa bez potrebe za ručnim pokretanjem brokerskog sistema.

### 5.4 Prednosti implementiranog rešenja

Implementacijom kontinuirane integracije ostvarene su sledeće prednosti:

- automatska provera ispravnosti sistema nakon svake izmene,
- rano otkrivanje grešaka tokom razvoja,
- smanjenje potrebe za ručnim testiranjem osnovnih funkcionalnosti,
- povećanje stabilnosti sistema,
- jednostavnije održavanje i dalji razvoj projekta,
- veća pouzdanost mikroservisne arhitekture.

Na ovaj način je obezbeđen dodatni nivo kontrole kvaliteta softvera i unapređen celokupan proces razvoja sistema.

---

## 6. Deployment i pokretanje sistema

### 6.1 Pokretanje sistema u razvojnom okruženju (Development)

Tokom razvoja mikroservisi se mogu pokretati direktno iz razvojnog okruženja kao Spring Boot aplikacije. Ovakav način rada omogućava jednostavnije testiranje i razvoj pojedinačnih komponenti sistema bez potrebe za korišćenjem Docker okruženja.

Pre pokretanja mikroservisa potrebno je obezbediti da su dostupne sve infrastrukturne komponente od kojih sistem zavisi. To podrazumeva pokretanje PostgreSQL baze podataka i RabbitMQ message broker-a. Nakon toga potrebno je pokrenuti Eureka Discovery Server koji omogućava registraciju i međusobno pronalaženje mikroservisa.

Nakon uspešnog pokretanja Eureka servera, mikroservisi se pokreću sledećim redosledom:

1. User Service
2. Book Service
3. Payment Service
4. Notification Service
5. Order Service
6. API Gateway

Prilikom pokretanja svaki mikroservis se automatski registruje u Eureka Discovery Server-u i postaje dostupan ostalim komponentama sistema.

Nakon što su svi servisi uspešno pokrenuti, sistem je dostupan preko API Gateway komponente koja predstavlja jedinstvenu ulaznu tačku za sve klijentske zahteve.

| Servis               | URL                    |
| -------------------- | ---------------------- |
| Eureka Dashboard     | http://localhost:8761  |
| API Gateway          | http://localhost:8765  |
| User Service         | http://localhost:8100  |
| Book Service         | http://localhost:8300  |
| Order Service        | http://localhost:8200  |
| Payment Service      | http://localhost:8081  |
| Notification Service | http://localhost:8400  |
| RabbitMQ Management  | http://localhost:15672 |

### 6.2 Docker kontejnerizacija i Docker Compose orkestracija

Radi jednostavnijeg pokretanja i distribucije sistema, sve komponente mikroservisne arhitekture kontejnerizovane su korišćenjem Docker tehnologije. Za svaki mikroservis kreiran je poseban Docker image, dok se za upravljanje kompletnim okruženjem koristi Docker Compose.

Pre prvog pokretanja sistema potrebno je izgraditi Docker image za svaki mikroservis unutar odgovarajućeg direktorijuma:

```bash
docker build -t discovery-service ./discovery-service
docker build -t api-gateaway ./api-gateaway
docker build -t user-service ./user-service
docker build -t book-service ./book-service
docker build -t order-service ./order-service
docker build -t payment-service ./payment-service
docker build -t notification-service ./notification-service
```

Nakon izgradnje svih image-a, kompletan mikroservisni sistem pokreće se jednom komandom iz root direktorijuma projekta:

```bash
docker compose up
```

Prilikom izvršavanja ove komande automatski se pokreću sve komponente sistema:

- PostgreSQL baza podataka
- RabbitMQ message broker
- Eureka Discovery Server
- API Gateway
- User Service
- Book Service
- Order Service
- Payment Service
- Notification Service

Docker Compose automatski kreira zajedničku mrežu između kontejnera, omogućava međusobnu komunikaciju servisa i obezbeđuje pravilno pokretanje zavisnih komponenti sistema.

Nakon uspešnog pokretanja moguće je proveriti registraciju servisa putem Eureka Dashboard-a na adresi `http://localhost:8761`, dok se svi funkcionalni zahtevi prosleđuju preko API Gateway komponente dostupne na adresi `http://localhost:8765`.

### 6.3 Service Discovery

Za registraciju i pronalaženje mikroservisa koristi se Eureka Discovery Server. Nakon pokretanja sistema, Eureka Dashboard je dostupan na adresi: `http://localhost:8761`

Putem ove konzole moguće je pratiti status svih registrovanih mikroservisa i proveriti njihovu dostupnost u sistemu.

### 6.4 API Gateway

Svi zahtevi korisnika prolaze kroz API Gateway komponentu koja predstavlja jedinstvenu ulaznu tačku sistema. Gateway vrši rutiranje zahteva ka odgovarajućim mikroservisima, autentifikaciju i autorizaciju korisnika i komunikaciju sa Eureka Discovery Server-om radi pronalaženja instanci servisa. API Gateway je dostupan na adresi: `http://localhost:8765`

### 6.5 Produkciono okruženje

Arhitektura sistema je projektovana prema principima modernih mikroservisnih aplikacija i spremna je za produkciono okruženje.

Sistem koristi:

- Spring Cloud Eureka za Service Discovery
- API Gateway za centralizovano rutiranje zahteva
- Docker kontejnere za izolaciju i jednostavniji deployment
- RabbitMQ za asinhronu komunikaciju između mikroservisa
- PostgreSQL bazu podataka za trajno čuvanje podataka

Kao potencijalna buduća unapređenja predviđeni su: Kubernetes deployment, Prometheus i Grafana monitoring i napredne CI/CD strategije za automatski deployment.

---

## 7. Kredencijali i konfiguracija

### 7.1 Default kredencijali

Sledeći kredencijali važe za lokalno razvojno okruženje i Docker Compose deployment.

| Servis            | Korisničko ime    | Lozinka          | Napomena                                       |
| ----------------- | ----------------- | ---------------- | ---------------------------------------------- |
| PostgreSQL        | `postgres`        | `postgres`       | Baza: `bookstore`                              |
| RabbitMQ          | `guest`           | `guest`          | Management UI: http://localhost:15672          |
| Mailtrap SMTP     | `5b26caa907c45b`  | `b6285849dd9326` | Host: `sandbox.smtp.mailtrap.io`, Port: `2525` |
| Admin nalog (app) | `admin@gmail.com` | `admin123`       | Kreira se automatski pri prvom pokretanju      |

> **Napomena:** Kredencijali navedeni u ovoj tabeli koriste se isključivo u razvojnom i testnom okruženju. Za produkciono okruženje obavezno promeniti sve lozinke i koristiti environment varijable ili secrets menadžer.

### 7.2 Konfiguracija Notification Service-a

Notification Service zahteva podešavanje SMTP i RabbitMQ konekcije u `application.properties` fajlu:

```properties
spring.application.name=notification-service
server.port=8400

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database=POSTGRESQL
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Mailtrap SMTP
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=5b26caa907c45b
spring.mail.password=b6285849dd9326
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---
