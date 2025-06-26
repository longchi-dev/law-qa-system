# ⚖️ Law Q&A and Search System
## 📌 Introduction

The **Law Q&A and Search System** is a web application that enables users to:

- 🔍 Search for legal documents  
- 🤖 Ask questions to an AI-powered chatbot  
- ⚡ Quickly and accurately access legal information

The system leverages **Artificial Intelligence** to understand user queries and improve search results.

---
## ✨ Features
- 🤖 AI-Powered Chatbot: Ask legal questions and receive accurate answers.
- 🔎 Legal Document Search: Search by keywords or semantic meaning using AI & Elasticsearch.
- 👤 Account Management: Register, login, recharge balance, and manage chatbot subscriptions.
- 📌 Save & Share: Save search results and share with others.
- 📊 Reports & Statistics: Track usage and spending history.

## 🛠️ Technologies Used

### 1. 💻 Programming Languages
- **Backend**: Java (Spring Boot)  
- **Frontend**: HTML, CSS, JavaScript

### 2. ⚙️ Frameworks & Libraries
- **Backend**: Spring Boot, Spring Data JPA, Spring Security  
- **Frontend**: Thymeleaf  
- **Search**: Elasticsearch

### 3. 🗄️ Databases
- **MySQL**: Stores user data and legal documents  
- **Elasticsearch**: Powers fast full-text legal document search

### 4. 🚀 Deployment
- **Application Server**: Embedded Tomcat (via Spring Boot)  
- **Version Control**: Github
- **CI/CD**: Github CI/CD

---
## 🧰 Installation & Setup

### ✅ System Requirements
- ☕ JDK 17+  
- 🐬 MySQL 8.0+  
- 🧠 Elasticsearch 8.x  
- 🧱 Maven 3.8+  
- 🎨 Thymeleaf

### 📥 Setup Steps
1. 📂 Clone the Repository:
```bash
git clone
cd hoi-dap-tim-kiem-luat
```

2. 🛠️ Configure the Database:
- Create a database named law_qa in MySQL.
- Update application.properties with MySQL and Elasticsearch credentials.

3. 🧠 Download Ollama:
- 👉 https://ollama.com/download
- Run:
```bash
ollama pull nomic-embed-text
```

4. 📦 Install Elasticsearch:
- 👉 https://github.com/elastic/elasticsearch
- Extract and run:
```bash
bin/elasticsearch.bat
```
- If prompted, set the password in application.properties.

5. ▶️ Run the Project:
- Visit: http://localhost:8080/
- Login via:
  - 🔐 Google
  - 🔐 Facebook
  - 📝 Register manually
- Admin Login:
  - ✉️ Email: admin@gmail.com
  - 🔑 Password: admin
  - 💳 Use VNPAY sandbox (https://sandbox.vnpayment.vn/apis/vnpay-demo/) to test payment functionality.


