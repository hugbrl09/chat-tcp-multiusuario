package com.mycompany.chattcpcliente;

import com.google.gson.Gson;
import java.util.List;

public class Mensagem {
    private String tipo;
    private String remetente;
    private String destino;
    private String conteudo;
    private List<String> listaUsuarios;

    public Mensagem() {
    }

    public Mensagem(String tipo, String remetente, String destino, String conteudo, List<String> listaUsuarios) {
        this.tipo = tipo;
        this.remetente = remetente;
        this.destino = destino;
        this.conteudo = conteudo;
        this.listaUsuarios = listaUsuarios;
    }
    
    // Serialização (Objeto -> JSON)
    public String paraLinha() {
        return new Gson().toJson(this);
    }
    
    // Desserialização (JSON -> Objeto)
    public static Mensagem fromLinha(String json) {
        return new Gson().fromJson(json, Mensagem.class);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getRemetente() {
        return remetente;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }
    
    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    public List<String> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<String> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
}