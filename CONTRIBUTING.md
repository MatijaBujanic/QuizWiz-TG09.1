# Contributing Guidelines

Hvala Vam na interesu za doprinos na ovom projektu.
U nastavku su navedene smjernice koje će Vam pomoći da uspješno postavite razvojno okruženje, pridonesete kodu te održavate kvalitetu projekta u skladu s dogovorenim standardima.

## 1. Postavljanje lokalnog okruženja

Projekt se sastoji od backend i frontend dijela.

**Backend (Spring Boot)**
Za pokretanje backend aplikacije potrebno je imati instaliran Java SDK (v17+) te Maven.
> git clone https://github.com/MatijaBujanic/QuizWiz-TG09.1.git
>
> cd backend
>
> mvn clean install
>
> mvn spring-boot:run

Aplikacija će se pokrenuti na adresi: http://localhost:8080

**Frontend (React)**
Za frontend potrebno je imati instaliran Node.js i npm.
> cd frontend
>
> npm install
>
> npm start

Aplikacija će se pokrenuti na adresi: http://localhost:3000

Kako bi se pokrenula deployana aplikacija (frontend u Vercelu, backend u Railwayu).
[Kliknite kako biste pokrenuli aplikaciju.](https://quiz-wiz-tg-09-1.vercel.app/)

## 2. Rad s granama

Koristimo Git Feature Branch Workflow radi jednostavnijeg razvoja i preglednosti.
Molimo Vas da ne radite izravno u glavnoj grani (main).

|Vrsta grane|Namjena|
|-----|---------|
|main|Glavna, stabilna verzija projekta (finalni kod koji ide u produkciju).|
|frontend|Glavna razvojna grana za React dio sustava.|
|frontend_auth_promjene|Eksperimentalna grana izmjene vezane za autentifikaciju na frontendu.|
|sekvencijski|Grana korištena za razvoj i pohranu sekvencijskih dijagrama i srodne dokumentacije.|
|baze|Grana korištena za razvoj baze podataka i njezino hostanje na Supabase.|
|DEPLOYMENT|Grana korištena za deployanje aplikacije|

## 3. Smjernice za kodiranje
**Frontend (React)**
* Koristiti functional components i React Hooks.
* Kod mora biti jasan, konzistentan i u skladu s ESLint pravilima.
* Nazive datoteka i komponenti pisati u camelCasu (npr. profileUser.jsx).
* Složenije logističke dijelove izdvajati u zasebne funkcije ili module.

**Backend (Spring Boot)**
* Poštovati slojevitu strukturu (Controller, Service, Repository, Model).
* Koristiti odgovarajuće Spring anotacije (@RestController, @Service...).
* Nazivi klasa i metoda trebaju jasno održavati svoju funkciju.
* Kod treba biti čitljiv, uz kratke i jasne komentare tamo gdje je potrebno.

## 4. Testiranje
Prije predaje promjena potrebno je provjeriti da sav kod prolazi testove.

**Backend**
> mvn test

**Frontend**
> npm test

U slučaju dodavanja nove funkcionalnosti, molimo da dodate i odgovarajuće testove.

## 5. Commit poruke
Commit poruke trebaju biti kratke, jasne i opisne.

## 6. Pull Request postupak
1. Forkajte repozitorij (ako niste član tima).
2. Kreirajte novu granu.
3. Napravite promjene i testirajte ih lokalno.
4. Pushajte promjene i otvorite Pull Request.
5. U opisu PR-a navedite:
    * što je promijenjeno ili dodano
    * povezan issue (ako postoji)
    * upute za testiranje

Svaka PR mora proći code review prije spajanja (merge).

## 7. Code Review smjernice
* Pregled obavlja barem jedan član tima.
* Komentari trebaju biti konstruktivni i usmjereni na poboljšanje kvalitete.
* Nakon odobrenja PR se može mergeati u main.
* Svaka promjena treba biti testirana prije integracije.

## 8. Pravila ponašanja
Svi suradnici dužni su poštovati naš [CODE_OF_CONDUCT](https://github.com/MatijaBujanic/QuizWiz-TG09.1/blob/main/CODE_OF_CONDUCT.md). Očekuje se profesionalnost, suradnja i međusobno poštovanje.

Hvala što doprinosite i dijelite svoje znanje s timom.