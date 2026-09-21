# 💬 Chat TCP Multiusuário Concorrente

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk)
![NetBeans](https://img.shields.io/badge/NetBeans-IDE-1B6AC6?style=for-the-badge&logo=apachenetbeans)
![TCP](https://img.shields.io/badge/Protocolo-TCP-blue?style=for-the-badge)

Projeto desenvolvido para a disciplina de **Sistemas Paralelos e Distribuídos**. Aplicação de chat centralizada em **Java** utilizando **Sockets TCP (`ServerSocket` e `Socket`)**, **Pool de Threads (`ExecutorService`)**, **Exclusão Mútua (`synchronized`)** e **Interface Gráfica Swing com escuta não-bloqueante**.

---

## 🎯 Requisitos Implementados

* **Comunicação Confiável TCP:** Conexões contínuas via streams de texto (`BufferedReader` e `PrintWriter`) tratadas com delimitador `\n`.
* **Gerenciamento Concorrente de Conectados:** Servidor com `ExecutorService` (10 threads) mantendo o mapa de clientes ativos.
* **Proteção de Região Crítica:** Acesso ao mapa de usuários protegidos por `synchronized` para evitar condições de corrida na troca de mensagens e cadastro de apelidos.
* **Mensagens Privadas (Unicast) e Globais (Broadcast):** Roteamento inteligente no servidor com base na estrutura JSON do protocolo.
* **Thread de Escuta no Cliente:** O cliente recebe mensagens em segundo plano sem travar a digitação do usuário.

---

## 🛠️ Estrutura do Protocolo JSON

```json
{
  "tipo": "MSG_PRIVADA",
  "remetente": "Alice",
  "destino": "Bob",
  "conteudo": "Olá Bob, tudo bem?",
  "listaUsuarios": ["Alice", "Bob", "Carlos"]
}
```

---

## 🚀 Como Executar

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/hugbrl09/chat-tcp-multiusuario.git
   ```
2. **Executar o Servidor:** No NetBeans, execute `ServidorChatApp.java`.
3. **Executar o Cliente:** Execute `TelaChat.java` quantas vezes desejar para simular múltiplos usuários.