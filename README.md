# 🤖 WordleVS — Telegram Bot

**WordleVS** is a competitive Telegram bot inspired by the classic **Wordle** game.  
It allows users to play Wordle directly on Telegram, track their statistics, store match history, and compete with other players on a global leaderboard.

📌 **Telegram Bot Username:** `@wordlevs_bot`

---

## 🛠 Technologies & Tools

- **Language:** Java (JDK 21)
- **Build Tool:** Maven
- **Telegram Library:** TelegramBots Java Library
- **Database:** SQLite
- **External APIs:**
    - DictionaryAPI
    - Giphy API
    - Random Word API

---

## 🎯 Project Features

- Persistent user profiles
- Match history (career) saved in database
- Player statistics (wins, matches, leaderboard)
- Support for classic and custom Wordle games
- Integration with multiple public APIs
- API key management via external configuration
- SQLite relational database with foreign keys and cascade rules

---

## 🎮 Available Commands

### 📖 General
- **/help**  
  Show the list of available commands

---

### 👤 Profile Management
- **/profile**  
  View your WordleVS profile information

- **/create_profile**  
  Create a new WordleVS profile

- **/edit_profile**  
  Edit your profile settings

- **/delete_profile**  
  Delete your profile and all related data

- **/career**  
  View your last 10 played games

- **/clear_career**  
  Clear your career history

---

### 🏆 Competition
- **/leaderboard**  
  Show the top 10 WordleVS players

---

### 🎲 Gameplay
- **/play**  
  Play classic Wordle

- **/play_variant**  
  Play Wordle with custom word length and number of tries

- **/give_up**  
  Give up the current game

- **Definition**  
  After the game ends, you can request the definition of the guessed word  
  (English only, due to API limitations)

---

## 🌐 External APIs Used

### 📚 Dictionary API
🔗 https://dictionaryapi.dev/

Used to retrieve:
- Definitions
- Synonyms
- Part of speech

📌 Used **after a game ends** to show the meaning of the guessed word.  
⚠️ Supports **English language only**.

---

### 🎞 Giphy API
🔗 https://developers.giphy.com/

Used to:
- Display victory GIFs when a player wins
- Show reaction GIFs when a game ends

📌 Requires an **API key**.

---

### 🔤 Random Word API
🔗 https://random-word-api.herokuapp.com/

Used to:
- Generate random words for Wordle games
- Support variable word lengths in `/play_variant`

---

## 🗄 Database Design (SQLite)

The project uses a **relational SQLite database** with persistent storage.

### 📌 Tables

#### `players`
| Field | Type | Description |
|------|------|-------------|
| tag | INTEGER (PK) | Unique player identifier |
| username_wordle | TEXT | Wordle username |
| favlang | TEXT | Preferred language |
| matches | INTEGER | Total matches played |
| wins | INTEGER | Total wins |
| telegram_username | TEXT | Telegram username |

#### `career`
| Field | Type | Description |
|------|------|-------------|
| id | INTEGER (PK AUTOINCREMENT) | Match ID |
| mdate | DATE | Match date |
| guesses | TEXT | User guesses |
| colors | TEXT | Guess results |
| word | TEXT | Secret word |
| username | TEXT | Username |
| tag | INTEGER (FK) | Player reference |

🔗 `career.tag → players.tag`  
✔ `ON DELETE CASCADE`  
✔ `ON UPDATE CASCADE`

---

## 📊 Statistics & Queries Implemented

- Total matches per player
- Total wins per player
- Win rate calculation
- Global leaderboard (Top 10 players)
- Last 10 matches per user (career)

---

## 🔐 API Key & Configuration

⚠️ **API keys are NOT committed to the repository**

### Configuration file
Create a file called:
config.properties

Example:
```properties
BOT_TOKEN=insert_your_telegram_bot_token
GIPHY_API_KEY=insert_your_giphy_api_key
```

📌 The file is listed in .gitignore
A template is provided:
config.properties.example