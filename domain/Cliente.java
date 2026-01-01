package mbtec.com.mz.itemvendatest.domain;

public class Cliente {
    private int idcliente;
    private String nome;
    private String nuit;
    private String endereco;

    public Cliente() {
    }

    public Cliente(int idcliente, String nome, String nuit, String endereco) {
        this.idcliente = idcliente;
        this.nome = nome;
        this.nuit = nuit;
        this.endereco = endereco;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNuit() {
        return nuit;
    }

    public void setNuit(String nuit) {
        this.nuit = nuit;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idcliente=" + idcliente +
                ", nome='" + nome + '\'' +
                ", nuit='" + nuit + '\'' +
                ", endereco='" + endereco + '\'' +
                '}';
    }
}
