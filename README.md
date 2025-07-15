
# Cloud Storage Recommender (CLI-Based)

This project implements a command-line tool for comparing and recommending cloud storage plans from platforms like Google Drive, Dropbox, OneDrive, and Box. Users can input feature preferences, budget, and storage requirements to receive ranked plan recommendations.

---

## 🔧 Technologies Used

- Java (JDK 21+)
- Eclipse IDE
- JSON data (pre-parsed or crawled)
- CLI interface only (no GUI)

---

## 📁 Project Structure

```
src/
├── cli/
│   └── main.java                     → CLI entry point and flow controller
├── feature/                         → Core feature implementations
│   ├── WebCrawler.java
│   ├── HtmlParser.java
│   ├── SpellChecker.java
│   ├── WordCompleter.java
│   └── Recommender.java
├── filter/
│   └── UserFilter.java              → Collects user budget/storage/preferences
├── model/
│   ├── Plan.java                    → Represents a cloud storage plan
│   └── UserRequest.java             → Represents user input constraints
├── data/
│   └── cloud_storage_data_en_cleaned.json
```

---

## 🚀 CLI Flow (main.java)

The main logic follows this sequence:

1. **cli.Main**  
   Prompt user to choose a preferred platform (or skip to include all)
      → returns a `UserRequest` object

2. **feature.WebCrawler**  
   Crawl HTML from selected platform (simulated or real)

3. **feature.HtmlParser(REGEX)**  
   Extract structured plan data from HTML

4. **feature.SpellChecker**  
   Correct misspelled feature term

5. **feature.WordCompleter**  
   Suggest full matching feature terms

6. **filter.UserFilter**  
   Filter plans user budget, billing type, and storage needs  

7. **Recommender.recommender**  
   Sort plans with userrequest

---

## ✅ Input Format

- Feature keyword: free text (will be corrected)
- Budget constraint: numeric input + billing type (Monthly/Annual)
- Storage: text with unit (e.g., "100GB", "1TB")

---

## 🧪 Future Extensions

- Implement real-time web scraping
<!-- - Add logging and usage tracking -->
- Improve feature clustering & ranking algorithms

---

## 👥 Contributors

TBC
