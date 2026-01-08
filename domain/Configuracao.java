package mbtec.com.mz.itemvendatest.domain;

public class Configuracao {
    private final String chave;
    private String valor;

    public Configuracao(String chave, String valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public String getChave() {
        return chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
