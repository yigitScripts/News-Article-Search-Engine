# 📰 News Article Search Engine (Custom Hash Map Implementation)

> A high-performance, in-memory search engine for CNN news articles, built entirely from scratch using Java without built-in Collection frameworks.

## 🚀 Project Overview

This project implements a search engine designed to index and retrieve information from a dataset of approximately **28,600 CNN news articles** (2011-2022).

The core of the system is a **custom Hash Table** implementation that handles indexing, collision resolution, and dynamic resizing manually. The project demonstrates a deep understanding of Data Structures and Algorithms (DSA) by comparing different hashing strategies and their impact on search performance.

## 🛠 Technical Architecture

### 1. Custom Data Structures
Instead of using `java.util.HashMap`, this project features a custom implementation with:
* **Dynamic Resizing:** Automatically doubles the table size when the Load Factor ($\alpha$) exceeds thresholds (0.5 or 0.8).
* **Generic Types:** Designed to handle generic Key-Value pairs (`<K, V>`).

### 2. Hashing Algorithms
Two distinct hash functions were implemented to compare distribution efficiency:
* **SSF (Simple Summation Function):** Sums the ASCII values of the characters.
    * $$h(s) = \sum_{k=0}^{n-1} ch_k$$
* **PAF (Polynomial Accumulation Function):** Uses Horner's Rule with a prime constant ($z=33$) to avoid collisions.
    * $$h(s) = \sum_{i=0}^{n-1} ch_i \cdot z^{n-1-i}$$

### 3. Collision Handling Strategies
* **Linear Probing (LP):** Searches for the next available slot sequentially.
* **Double Hashing (DH):** Uses a secondary hash function to calculate the probe step, reducing clustering.
    * $$h_2(k) = (h(k) + j \cdot d(k)) \mod N$$

## ⚡ Features

* **Inverted Indexing:** Maps every unique word in the dataset to the list of articles containing it.
* **Text Processing:**
    * Stop-word removal (filtering common words like "the", "is", "at").
    * Punctuation cleaning and tokenization.
* **Search Capabilities:**
    * **Search by ID:** Retrieve full article details (Headline, Text, Date) using a unique ID.
    * **Search by Keyword:** Multi-keyword search that ranks results based on relevance (occurrence frequency).
* **Performance Monitoring:** A built-in benchmarking tool to measure collision counts and search times under different configurations.

## 📂 Installation & Usage

### Prerequisites
* Java Development Kit (JDK) 8 or higher.

### Setup
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/USERNAME/News-Article-Search-Engine.git](https://github.com/USERNAME/News-Article-Search-Engine.git)
    cd News-Article-Search-Engine
    ```

2.  **Download the Dataset:**
    > ⚠️ **Note:** The `CNN_Articels.csv` (160MB+) file is not included in this repo due to size limits.
    * Please place the `CNN_Articels.csv` file in the root directory (or `src/resources` folder depending on your setup).
    * Ensure `stop_words_en.txt` is also present in the project directory.

3.  **Run the Application:**
    Compile and run the `Main.java` file.
    ```bash
    javac Main.java
    java Main
    ```

## 📊 Performance Analysis

The project includes a detailed analysis of Hash Table performance based on the following metrics:

| Metric | Configuration A | Configuration B |
| :--- | :--- | :--- |
| **Load Factor** | 50% | 80% |
| **Hash Function** | SSF vs PAF | SSF vs PAF |
| **Collision Strategy** | Linear Probing | Double Hashing |

*Detailed benchmarks regarding collision counts and average search time (ms) can be found in the project report.*

## 📝 License

This project was developed for the **CME 2201 - Data Structures** course.
