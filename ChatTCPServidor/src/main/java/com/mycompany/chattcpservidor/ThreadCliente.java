package com.mycompany.chattcpservidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ThreadCliente implements Runnable {
    private final Socket socket;
    private final GerenciadorClientes gerenciador;
    private PrintWriter saida;
    private String apelido;

    public ThreadCliente(Socket socket, GerenciadorClientes gerenciador) {
        this.socket = socket;
        this.gerenciador = gerenciador;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.saida = out;
            String linha;
            
            // Escuta continuamente as mensagens enviadas por este cliente
            while ((linha = entrada.readLine()) != null) {
                Mensagem msg = Mensagem.fromLinha(linha);
                if (msg != null) {
                    processarMensagem(msg);
                }
            }
        } catch (Exception e) {
            System.out.println("Conexão encerrada para " + (apelido != null ? apelido : "cliente desconhecido"));
        } finally {
            // Se o cliente sair ou cair, remove da lista e avisa os demais
            if (apelido != null) {
                gerenciador.remover(apelido);
                Mensagem avisoSair = new Mensagem("SISTEMA", "SERVIDOR", "TODOS",
                        apelido + " saiu do chat.", gerenciador.getListaApelidos());
                gerenciador.enviarBroadcast(avisoSair);
            }
            try { socket.close(); } catch (Exception ignored) {}
        }
    }
    
    private void processarMensagem(Mensagem msg) {
        switch (msg.getTipo()) {
            case "LOGIN":
                String novoApelido = msg.getRemetente();
                boolean sucesso = gerenciador.adicionar(novoApelido, this);
                
                if (sucesso) {
                    this.apelido = novoApelido;
                    // Confirma o login para o próprio usuário
                    Mensagem respOk = new Mensagem("LOGIN", "SERVIDOR", apelido,
                            "OK", gerenciador.getListaApelidos());
                    enviarMensagem(respOk);
                    
                    // Notifica todos no chat da entrada do novo participante
                    Mensagem avisoEntrou = new Mensagem("SISTEMA", "SERVIDOR", "TODOS",
                            apelido + " entrou no chat.", gerenciador.getListaApelidos());
                    gerenciador.enviarBroadcast(avisoEntrou);
                } else {
                    // Recusa o login caso o apelido já esteja em uso
                    Mensagem respErro = new Mensagem("ERRO", "SERVIDOR", novoApelido,
                            "Apelido já em uso. Escolha outro.", null);
                    enviarMensagem(respErro);
                }
                break;
                
            case "MSG_TODOS":
                gerenciador.enviarBroadcast(msg);
                break;
                
            case "MSG_PRIVADA":
                boolean entregue = gerenciador.enviarUnicast(msg.getDestino(), msg);
                if (!entregue) {
                    Mensagem erroDest = new Mensagem("ERRO", "SERVIDOR", apelido,
                            "Usuário '" + msg.getDestino() + "' não encontrado ou offline.", null);
                    enviarMensagem(erroDest);
                }
                break;
                
            case "LISTAR_USUARIOS":
                Mensagem respLista = new Mensagem("LISTAR_USUARIOS", "SERVIDOR", apelido,
                        "Lista atualizada", gerenciador.getListaApelidos());
                enviarMensagem(respLista);
                break;
                
            case "SAIR":
                try { socket.close(); } catch (Exception ignored) {}
                break;
        }
    }
    
    // Método responsável por enviar o JSON via socket para este cliente específico
    public void enviarMensagem(Mensagem msg) {
        if (saida != null) {
            saida.println(msg.paraLinha());
        }
    }
    
    public String getApelido() {
        return apelido;
    }
}
