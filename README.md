# 💬 Java Real-Time Chat Application

A **LAN-based multi-client chat application** built using **Java Socket Programming** and **Multithreading**.  
It enables multiple users to communicate in real time, send private messages, view active participants, and use custom commands — all from the console.

---

## 📌 Features
- **Multi-client support** – Multiple users can connect to the server simultaneously.
- **Real-time messaging** – Instant broadcasting of messages to all connected clients.
- **Private messaging** – Send direct messages to specific users using `/w`.
- **Custom commands**:
  - `/nick <newName>` – Change your username.
  - `/w <user> <message>` – Send a private message.
  - `/list` – View all online users.
  - `/quit` – Leave the chat.
- **System notifications** – User join/leave alerts.
- **Thread-safe operations** – Managed with `ConcurrentHashMap` and multithreading.

---

## 🛠️ Tech Stack
- **Java SE** – Core programming language.
- **Socket Programming** – For network communication between clients and server.
- **Multithreading** – For handling multiple client connections at once.
- **ConcurrentHashMap** – For safe concurrent client management.

---

## 📂 Project Structure
java-chat-app/
├── src/
│ ├── ChatServer.java # Server-side logic (handles connections, commands, broadcasts)
│ └── ChatClient.java # Client-side console application (sends/receives messages)
├── .gitignore # Ignore compiled .class files
├── LICENSE # MIT License
└── README.md # Project documentation

## 🚀 Getting Started

### 1️⃣ Prerequisites
- **Java JDK 8+** installed  
- Terminal or command prompt  
- Basic understanding of running Java programs

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/java-chat-app.git
cd java-chat-app/src
````

### 3️⃣ Compile the Project

```bash
javac ChatServer.java ChatClient.java
```

### 4️⃣ Run the Server

```bash
java ChatServer
```

Or specify a custom port:

```bash
java ChatServer 6000
```

### 5️⃣ Run the Client

In separate terminals:

```bash
java ChatClient
```

Or connect to a specific server IP and port:

```bash
java ChatClient 192.168.1.20 5555
```

---

## 💬 Commands

| Command               | Description                      |
| --------------------- | -------------------------------- |
| `/nick <newName>`     | Change your username             |
| `/w <user> <message>` | Send a private message to a user |
| `/list`               | Show all connected users         |
| `/quit`               | Disconnect from the chat         |
| *(anything else)*     | Broadcast message to all users   |

**Example:**

```
Enter your nickname: rishi
[SYSTEM]: rishi joined.
Hello everyone!
/list
Online (3): rishi, alice, bob
/w alice Hey Alice, how's it going?
```

---

## 🔮 Future Enhancements

* **GUI Support** – Upgrade client to JavaFX or Swing for better UI.
* **SSL/TLS Encryption** – Secure communication using Java Secure Socket Extension (JSSE).
* **Message History** – Store chat logs for each session.
* **Authentication** – Add user login and password protection.
