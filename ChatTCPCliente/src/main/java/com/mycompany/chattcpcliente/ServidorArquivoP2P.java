package com.mycompany.chattcpcliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorArquivoP2P implements Runnable {
    private final File arquivo;
    private final ServerSocket serverSocket;
    private final int porta;
    
    public ServidorArquivoP2P(File arquivo) throws Exception {
        this.arquivo = arquivo;
        // Porta 0 faz o SO escolher uma porta livre automaticamente
        this.serverSocket = new ServerSocket(0);
        this.porta = serverSocket.getLocalPort();
    }
    
    public int getPorta() {
        return porta;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("[P2P] Servidor de arquivo ativo na porta " + porta + " para o arquivo: " + arquivo.getName());
            
            // Aguarda conexão direta do destinatário (bloqueante)
            try (Socket socketCliente = serverSocket.accept();
                 FileInputStream fis = new FileInputStream(arquivo);
                 OutputStream os = socketCliente.getOutputStream()) {
                
                byte[] buffer = new byte[4096]; // Buffer de 4KB para transferência
                int bytesLidos;
                
                while ((bytesLidos = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesLidos);
                }
                os.flush();
                System.out.println("[P2P] Transferência do arquivo '" + arquivo.getName() + "' concluída com sucesso!");
            }
        } catch (Exception e) {
            System.err.println("[P2P] Erro durante a transferência do arquivo: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (Exception ignored) {}
        }
    }
}
