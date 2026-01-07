# 🤖 WordleBotVS – Telegram Wordle Bot

**WordleBotVS** è un bot Telegram scritto in **Java** che permette agli utenti di giocare a **Wordle direttamente in chat**, creare un profilo personale e tenere traccia delle statistiche di gioco.

Il bot supporta:
- profili utente persistenti
- partite Wordle classiche e personalizzate
- statistiche (partite giocate e vittorie)
- GIF animate tramite Giphy
- database locale SQLite

---

## 🚀 Funzionalità principali

- Creazione, modifica ed eliminazione del profilo
- Visualizzazione profilo con statistiche
- Partite Wordle standard
- Partite Wordle personalizzate (lunghezza parola e tentativi)
- Aggiornamento automatico di vittorie e partite
- Supporto multilingua
- Pulsanti inline Telegram
- Database SQLite persistente

---

## 📋 Comandi disponibili

### `/create_profile`
Crea un nuovo profilo Wordle.

Dopo il comando, il bot richiede **un solo messaggio** nel formato:
WordleUsername,Tag,Lingua

**Esempio**
Wizard,1234,it


**Regole**
- `WordleUsername`: 3–20 caratteri
- `Tag`: esattamente 4 caratteri alfanumerici
- `Lingua`: `en`, `es`, `it`, `de`, `fr`
- Un solo profilo per account Telegram

---

### `/profile`
Mostra il profilo dell’utente, includendo:
- Username Telegram
- Username Wordle
- Tag
- Lingua preferita (con bandiera)
- Statistiche:
  - Partite giocate
  - Vittorie
  - Percentuale di vittoria

Se l’utente ha una foto profilo Telegram, viene mostrata automaticamente.

---

### `/play`
Avvia una **partita Wordle standard**:
- 6 tentativi
- parola da 5 lettere
- lingua basata sul profilo

📌 Requisiti:
- Devi avere un profilo creato

---

### `/play_variant`
Avvia una **partita Wordle personalizzata**.

Dopo il comando, il bot chiede un messaggio nel formato:


**Esempio**
6,7


**Regole**
- `MaxTries` ≥ 1
- `WordLength` tra 4 e 10

---

### `/giveup`
Abbandona la partita in corso.

**Effetti**
- Il bot rivela la parola
- La partita termina

---

## 🧠 Pulsanti Inline

Nel messaggio del profilo sono disponibili:

### ✏️ Edit profile
- Elimina il profilo attuale
- Avvia la creazione di un nuovo profilo

### 🗑 Delete profile
- Cancella definitivamente il profilo dal database

---

## 🎮 Gameplay

Durante una partita:
- Invia una parola come tentativo
- Feedback con indicatori:
  - 🟩 lettera corretta e posizione corretta
  - 🟨 lettera presente ma posizione errata
  - ⬛ lettera assente

Alla vittoria:
- La vittoria viene salvata nel database
- Le statistiche vengono aggiornate

---

## 🗄️ Database

Il bot utilizza **SQLite** con una tabella `players`:

| Campo              | Tipo    | Note                     |
|--------------------|---------|--------------------------|
| tag                | TEXT    | PRIMARY KEY              |
| username_wordle    | TEXT    | NOT NULL                 |
| favlang            | TEXT    | NOT NULL                 |
| matches            | INTEGER | default 0                |
| wins               | INTEGER | default 0                |
| telegram_username  | TEXT    | UNIQUE                    |

📌 Il database è locale e **non deve essere caricato su GitHub**.
