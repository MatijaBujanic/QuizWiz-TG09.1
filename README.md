# Programsko inženjerstvo

# Opis projekta
Ovaj projekt je rezultat timskog rada u sklopu projektnog zadatka kolegija [Programsko inženjerstvo](https://www.fer.unizg.hr/predmet/proinz) na Fakultetu elektrotehnike i računarstva Sveučilišta u Zagrebu. 

Pub kvizovi su popularan oblik društvene zabave, ali njihova organizacija često ovisi o neformalnim kanalima kao što su društvene mreže, poruke i usmeni dogovori. Ovakav pristup otežava praćenje prijava, ograničavanje broja timova i komunikaciju između organizatora i sudionika.

PUBquizAPP rješava ovaj problem digitalizacijom cijelog procesa - od objave kvizova do prijava timova i komunikacije. Naša motivacija proizlazi iz želje da unaprijedimo iskustvo sudjelovanja u kvizovima kroz transparentan i organiziran sustav koji štedi vrijeme i smanjuje napore svih uključenih.

# Funkcijski zahtjevi

* Kvizovi

  *  Organizatori mogu objavljivati kvizove s detaljima (naziv, opis, datum i vrijeme održavanja, lokacija, maksimalan broj timova, status prijava).  
  *  Sudionici mogu pretraživati dostupne kvizove prema datumu, lokaciji ili nazivu te se prijaviti za sudjelovanje dok postoji slobodno mjesto.  
  *  Nakon završetka kviza, organizatori mogu unijeti rezultate i rang liste timova.  

* Organizatori

  *  Organizatori mogu kreirati profile i ažurirati svoje podatke (naziv, kontakt, opis, lokacija).  
  *  Organizatori mogu objavljivati nove kvizove, uređivati postojeće i upravljati prijavama timova.
  *  Organizatori mogu ograničiti broj prijava, zatvoriti prijave te slati obavijesti prijavljenim timovima o promjenama termina ili lokacije.  

* Registracija i timovi

  * Korisnici se mogu registrirati putem obrasca s osnovnim podacima (korisničko ime, lozinka, e-mail, naziv tima, kontakt podaci).  
  * Nakon registracije, timovi mogu pregledavati i pratiti svoje prijave na kvizove te primati obavijesti o promjenama statusa kvizova.  
  * Timovi mogu uređivati svoj profil (naziv, članove, kontakt informacije) i povući prijavu s kviza prije isteka roka.  

* Praćenje sudjelovanja
  
  * Timovi mogu pregledavati povijest svojih sudjelovanja na kvizovima te rezultate i poredak.  
  * Organizatori i sudionici imaju pristup arhivi održanih kvizova i njihovim rezultatima.  
  * Aplikacija omogućuje pregled osobne statistike (broj odigranih kvizova, osvojenih bodova i pozicija).  

* Ocjene i povratne informacije

  * Sudionici mogu ocijeniti i ostaviti recenzije za kvizove.  
  * Organizatori kvizova mogu odgovarati na recenzije. 
  * Sustav omogućuje prikaz prosječne ocjene kvizova organizatorima radi poboljšanja kvalitete budućih kvizova.  

# Tehnologije

•	Frontend: React.js, HTML, CSS

•	Backend: Spring Boot

•	Baze podataka: PostgreSQL

•	Mapa: Google Maps

•	Autentifikacija: Oauth 2.0

# Instalacija
[Pokretanje aplikacije klikom na tekst.](https://quiz-wiz-tg-09-1.vercel.app/)

# Članovi tima 
Matija Bujanić (matija.bujanic@fer.unizg.hr) - voditelj, backend 
Mihael Vranić (mihael.vranic@fer.unizg.hr) - baze podataka  
Paula Vidak (paula.vidak@fer.unizg.hr) - dokumentacija  
Jona Matsumoto Šegota (jona.matsumoto-segota@fer.unizg.hr) - frontend  
Dario Herceg (dario.herceg@fer.unizg.hr) - dokumentacija  
Filip Gojak (filip.gojak@fer.unizg.hr) - frontend  
Ana Kasanić (ana.kasanic@fer.unizg.hr) - baze podataka  

# Kontribucije
> Pravila se nalaze u posebnom dokumentu CONTRIBUTING.md



# 📝 Kodeks ponašanja [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](CODE_OF_CONDUCT.md)
Kao studenti sigurno ste upoznati s minimumom prihvatljivog ponašanja definiran u [KODEKS PONAŠANJA STUDENATA FAKULTETA ELEKTROTEHNIKE I RAČUNARSTVA SVEUČILIŠTA U ZAGREBU](https://www.fer.hr/_download/repository/Kodeks_ponasanja_studenata_FER-a_procisceni_tekst_2016%5B1%5D.pdf), te dodatnim naputcima za timski rad na predmetu [Programsko inženjerstvo](https://wwww.fer.hr).
Očekujemo da ćete poštovati [etički kodeks IEEE-a](https://www.ieee.org/about/corporate/governance/p7-8.html) koji ima važnu obrazovnu funkciju sa svrhom postavljanja najviših standarda integriteta, odgovornog ponašanja i etičkog ponašanja u profesionalnim aktivnosti. Time profesionalna zajednica programskih inženjera definira opća načela koja definiranju  moralni karakter, donošenje važnih poslovnih odluka i uspostavljanje jasnih moralnih očekivanja za sve pripadnike zajednice.

Kodeks ponašanja skup je provedivih pravila koja služe za jasnu komunikaciju očekivanja i zahtjeva za rad zajednice/tima. Njime se jasno definiraju obaveze, prava, neprihvatljiva ponašanja te  odgovarajuće posljedice (za razliku od etičkog kodeksa). U ovom repozitoriju dan je jedan od široko prihvaćenih kodeks ponašanja za rad u zajednici otvorenog koda.
>### Poboljšajte funkcioniranje tima:
>* definirajte načina na koji će rad biti podijeljen među članovima grupe
>* dogovorite kako će grupa međusobno komunicirati.
>* ne gubite vrijeme na dogovore na koji će grupa rješavati sporove primijenite standarde!
>* implicitno podrazumijevamo da će svi članovi grupe slijediti kodeks ponašanja.
 
>###  Prijava problema
>Najgore što se može dogoditi je da netko šuti kad postoje problemi. Postoji nekoliko stvari koje možete učiniti kako biste najbolje riješili sukobe i probleme:
>* Obratite mi se izravno [e-pošta](mailto:vlado.sruk@fer.hr) i  učinit ćemo sve što je u našoj moći da u punom povjerenju saznamo koje korake trebamo poduzeti kako bismo riješili problem.
>* Razgovarajte s vašim asistentom jer ima najbolji uvid u dinamiku tima. Zajedno ćete saznati kako riješiti sukob i kako izbjeći daljnje utjecanje u vašem radu.
>* Ako se osjećate ugodno neposredno razgovarajte o problemu. Manje incidente trebalo bi rješavati izravno. Odvojite vrijeme i privatno razgovarajte s pogođenim članom tima te vjerujte u iskrenost.

# 📝 Licenca
Važeća (1)
[![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

Ovaj repozitorij sadrži otvoreni obrazovni sadržaji (eng. Open Educational Resources)  i licenciran je prema pravilima Creative Commons licencije koja omogućava da preuzmete djelo, podijelite ga s drugima uz 
uvjet da navođenja autora, ne upotrebljavate ga u komercijalne svrhe te dijelite pod istim uvjetima [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License HR][cc-by-nc-sa].
>
> ### Napomena:
>
> Svi paketi distribuiraju se pod vlastitim licencama.
> Svi upotrijebljeni materijali  (slike, modeli, animacije, ...) distribuiraju se pod vlastitim licencama.

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: https://creativecommons.org/licenses/by-nc/4.0/deed.hr 
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

Orginal [![cc0-1.0][cc0-1.0-shield]][cc0-1.0]
>
>COPYING: All the content within this repository is dedicated to the public domain under the CC0 1.0 Universal (CC0 1.0) Public Domain Dedication.
>
[![CC0-1.0][cc0-1.0-image]][cc0-1.0]

[cc0-1.0]: https://creativecommons.org/licenses/by/1.0/deed.en
[cc0-1.0-image]: https://licensebuttons.net/l/by/1.0/88x31.png
[cc0-1.0-shield]: https://img.shields.io/badge/License-CC0--1.0-lightgrey.svg

### Reference na licenciranje repozitorija
