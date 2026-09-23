# 💬 Chat TCP Multiusuário Concorrente + Transferência P2P de Arquivos

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk)
![NetBeans](https://img.shields.io/badge/NetBeans-IDE-1B6AC6?style=for-the-badge&logo=apachenetbeans)
![TCP](https://img.shields.io/badge/Protocolo-TCP-blue?style=for-the-badge)
![Arquitetura](https://img.shields.io/badge/Arquitetura-H%C3%ADbrida%20(Cliente--Servidor%20%2B%20P2P)-green?style=for-the-badge)

Aplicação desenvolvida para a disciplina de **Sistemas Paralelos e Distribuídos**. O sistema combina uma **arquitetura centralizada cliente-servidor** para gerenciamento de presença e troca de mensagens de texto com um modelo **descentralizado Peer-to-Peer (P2P)** para transferência direta de arquivos via Sockets TCP.

---

## 📌 Funcionalidades do Sistema

### 1. Chat Centralizado (Requisitos Obrigatórios)
* **Autenticação com Apelido Único (`LOGIN`):** Validação no servidor para impedir apelidos duplicados na mesma sala.
* **Mensagens Públicas (`MSG_TODOS`):** Difusão em massa (*Broadcast*) para todos os participantes conectados.
* **Mensagens Privadas (`MSG_PRIVADA`):** Envio reservado (*Unicast*) para um destinatário específico escolhido na lista.
* **Lista Dinâmica de Usuários:** Atualização em tempo real do combo de destinatários no cliente ao haver conexões ou desconexões.
* **Notificações de Sistema (`SISTEMA`):** Avisos automáticos sobre entrada e saída de usuários.
* **Tratamento de Desconexão Graceful (`SAIR`):** Encerramento limpo de conexões individuais sem afetar o funcionamento do servidor nem dos demais clientes.

### 2. Transferência Direta de Arquivos (Bônus P2P +1,0)
* **Sinalização via Servidor (`SOLICITAR_P2P` / `RECUSAR_P2P`):** O servidor central intermedeia apenas o convite inicial enviando o IP, porta temporária e nome do arquivo.
* **Conexão TCP Direta:** O cliente remetente abre um `ServerSocket` em uma porta livre e transmite o arquivo diretamente para o destinatário, sem sobrecarregar a rede do servidor central.
* **Confirmação e Download:** O destinatário aceita/recusa o arquivo via caixa de diálogo (`JOptionPane`) e escolhe o diretório de destino pelo `JFileChooser`.

---

## 🧠 Conceitos de Sistemas Paralelos e Distribuídos Aplicados

### ⚙️ Concorrência no Servidor (`ExecutorService`)
Para atender múltiplos clientes simultaneamente sem bloquear a escuta de novas conexões no `ServerSocket`, o servidor utiliza um **Pool de Threads** (`Executors.newFixedThreadPool`). Cada cliente conectado é delegado para uma instância dedicada de `ThreadCliente`.

### 🔒 Proteção de Região Crítica (`synchronized`)
O repositório de conexões ativas (`GerenciadorClientes`) é uma **Região Crítica** acessada concorrentemente por múltiplas threads de clientes. Para evitar **Condições de Corrida (*Race Conditions*)**, todas as operações de inserção, remoção, busca e iteração são protegidas por **Exclusão Mútua (*Mutex*)** utilizando métodos sincronizados (`synchronized`).

### 🎨 Responsividade da Interface Gráfica (Swing EDT)
Para impedir o congelamento da interface do cliente (`TelaChat`) durante a recepção de dados pela rede, a escuta do socket é executada em uma **Thread de segundo plano**. A interface Swing (Event Dispatch Thread) permanece livre e responsiva para digitação e cliques do usuário.

---

## 🛠️ Especificação do Protocolo JSON (Gson)

A comunicação utiliza a biblioteca **Gson** para serialização e desserialização de objetos `Mensagem` em linhas JSON delimitadas por quebra de linha (`\n`).

Como a biblioteca Gson omite automaticamente campos nulos durante a serialização, o payload JSON adapta sua estrutura conforme a necessidade:

### A) Mensagem Enviada pelo Cliente (Chat Público / Privado)
*O cliente trafega apenas as informações do texto. O campo `listaUsuarios` é nulo e não aparece no JSON.*
```json
{
  "tipo": "MSG_PRIVADA",
  "remetente": "Alice",
  "destino": "Bob",
  "conteudo": "Olá Bob, tudo bem?"
}
```

### B) Mensagem Enviada pelo Servidor (Resposta a Login / Atualização de Presença)
*O servidor inclui a lista atualizada de usuários conectados para sincronizar a interface gráfica dos clientes.*
```json
{
  "tipo": "LOGIN",
  "remetente": "SERVIDOR",
  "destino": "Alice",
  "conteudo": "OK",
  "listaUsuarios": ["Alice","Bob","Carlos"]
}
```

### C) Mensagem de Sinalização P2P (Transferência de Arquivo)
*Utilizada para negociar os dados da conexão TCP direta entre dois clientes via servidor central.*
```json
{
  "tipo": "SOLICITAR_P2P",
  "remetente": "Alice",
  "destino": "Bob",
  "conteudo": "192.168.0.10",
  "nomeArquivo": "relatorio.pdf",
  "portaP2P": 52341
}
```

### Comandos do Protocolo:
| Tipo | Descrição |
| :--- | :--- |
| `LOGIN` | Solicita/confirma a entrada de um usuário com checagem de duplicidade |
| `MSG_TODOS` | Envia mensagem pública para todos os conectados (Broadcast) |
| `MSG_PRIVADA` | Envia mensagem privada para um usuário específico (Unicast) |
| `SOLICITAR_P2P` | Convite contendo IP, porta e nome do arquivo para início da conexão direta |
| `RECUSAR_P2P` | Notificação enviada ao remetente informando a recusa do arquivo |
| `LISTAR_USUARIOS` | Requisita a lista atualizada de usuários online |
| `SISTEMA` / `ERRO` | Mensagens informativas de controle e erros de validação do servidor |

---

## 📁 Estrutura do Repositório

```text
├── ChatTCPServidor/           # Módulo do Servidor Central
│   └── src/main/java/com/mycompany/chattcpservidor/
│       ├── ServidorChatApp.java     # Inicialização e escuta do ServerSocket (Porta 9999)
│       ├── ThreadCliente.java       # Processamento de mensagens e protocolo por cliente
│       ├── GerenciadorClientes.java # Gerenciador sincronizado de conexões ativas
│       └── Mensagem.java            # DTO do protocolo JSON
│
└── ChatTCPCliente/            # Módulo do Cliente Swing
    └── src/main/java/com/mycompany/chattcpcliente/
        ├── TelaChat.java            # Interface Swing e Thread de escuta de mensagens
        ├── ServidorArquivoP2P.java  # Servidor TCP local temporário para envio P2P
        └── Mensagem.java            # DTO do protocolo JSON
```

---

## 🚀 Como Executar o Projeto

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/hugbrl09/chat-tcp-multiusuario.git
   ```

2. **Iniciar o Servidor Central:**
   * No NetBeans, abra o projeto `ChatTCPServidor`.
   * Execute o arquivo `ServidorChatApp.java` (`Shift + F6`).

3. **Iniciar os Clientes:**
   * Abra o projeto `ChatTCPCliente`.
   * Execute o arquivo `TelaChat.java` duas ou mais vezes para instanciar múltiplos clientes na mesma máquina.

4. **Testar Envio de Mensagens e Arquivos:**
   * Conecte dois clientes com apelidos diferentes (ex: `Alice` e `Bob`).
   * Teste a troca de mensagens públicas e privadas.
   * Selecione `Bob` na lista de destinatários, clique em **Enviar Arquivo** e confirme a recepção na janela do destinatário.