# 🤖 WordleVS — Telegram Bot

**WordleVS** is a competitive Telegram bot inspired by the classic **Wordle** game.  
Challenge yourself, track your stats, and compete with other players on the leaderboard!

---

## 🎮 Available Commands

### 📖 General
- **/help**  
  Get the full list of WordleVS commands

---

### 👤 Profile Management
- **/profile**  
  View your WordleVS profile information

- **/create_profile**  
  Create your WordleVS profile

- **/edit_profile**  
  Edit your WordleVS profile

- **/delete_profile**  
  Delete your WordleVS profile permanently

- **/career**  
  View your previous games

- **/clear_career**  
  Clear your career history

---

### 🏆 Competition
- **/leaderboard**  
  View the top 10 WordleVS players

---

### 🎲 Gameplay
- **/play**  
  Play the classic Wordle game

- **/play_variant**  
  Play Wordle with custom word length and number of tries

- **/give_up**  
  Give up your current Wordle game

- **Definition**  
  After the game ends, you can get the definition of the guessed word.  
  This feature is available **only for English words** due to API limitations.

---

## 🌐 External APIs Used

WordleVS relies on several public APIs to enhance gameplay and user experience:

### 📚 Dictionary API
**https://api.dictionaryapi.dev/api/v2/entries/en/{word}**

Used to retrieve:
- word definitions
- synonyms
- grammatical categories

This API is used **after a game ends** to show the meaning of the guessed word.  
⚠️ Only English words are supported.

---

### 🎞 Giphy API
**https://api.giphy.com/v1/gifs/search**

Used to display:
- victory GIFs when a player wins
- defeat or fun reaction GIFs when a game ends

This makes the bot more interactive and engaging.

---

### 🔤 Random Word API
**https://random-word-api.herokuapp.com/word?length={n}**

Used to:
- generate random words for Wordle games
- support custom word lengths in `/play_variant`

Ensures each game uses a new and unpredictable word.

---

## 🚀 How to Play
1. Start a new game with `/play` or `/play_variant`
2. Guess the hidden word
3. Interpret the results:
    - 🟩 Letter is correct and in the right position
    - 🟨 Letter is correct but in the wrong position
    - ⬛ Letter is not in the word
4. Win the game or try again… but choose wisely!

---

## 👨‍💻 Author
Created by **Alex Munaro**  
🎓 *ITIS A. Rossi*

---

Enjoy playing **WordleVS** and climb the leaderboard! 🏆
