package com.mycompany.chattcpservidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorChatApp {
    private static final int PORTA = 9999;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static final GerenciadorClientes gerenciador = new GerenciadorClientes();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("=== Servidor de Chat TCP Ativo na porta " + PORTA + " ===");
            
            while (true) {
                // 1. Aguarda a conexão de um cliente (bloqueante)
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nova conexão TCP recebida de: " + socketCliente.getRemoteSocketAddress());
                
                // 2. Instancia a tarefa Runnable e entrega para o ThreadPool
                ThreadCliente tarefaCliente = new ThreadCliente(socketCliente, gerenciador);
                pool.execute(tarefaCliente);
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor de chat: " + e.getMessage());
        }
    }
}
