package com.mycompany.chattcpservidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorClientes {
    // Região Crítica: Mapa com apelido -> ThreadCliente
    private final Map<String, ThreadCliente> clientes = new HashMap<>();
    
    // Adiciona o cliente garantindo exclusão mútua e apelido único
    public synchronized boolean adicionar(String apelido, ThreadCliente cliente) {
        if (clientes.containsKey(apelido)) {
            return false; // Apelido já cadastrado/logado
        }
        clientes.put(apelido, cliente);
        return true;
    }
    
    // Remove o cliente da lista de usuários ativos
    public synchronized void remover(String apelido) {
        if (apelido != null) {
            clientes.remove(apelido);
        }
    }
    
    // Retorna uma cópia da lista de apelidos conectados
    public synchronized List<String> getListaApelidos() {
        return new ArrayList<>(clientes.keySet());
    }
    
    // Envia uma mensagem para todos os clientes conectados (Broadcast)
    public void enviarBroadcast(Mensagem msg) {
        List<ThreadCliente> lista;
        synchronized (this) {
            lista = new ArrayList<>(clientes.values());
        }
        for (ThreadCliente cliente : lista) {
            cliente.enviarMensagem(msg);
        }
    }
    
    // Envia uma mensagem para um cliente específico (Unicast / Privada)
    public boolean enviarUnicast(String destino, Mensagem msg) {
        ThreadCliente alvo;
        synchronized (this) {
            alvo = clientes.get(destino);
        }
        if (alvo != null) {
            alvo.enviarMensagem(msg);
            return true;
        }
    }
}
