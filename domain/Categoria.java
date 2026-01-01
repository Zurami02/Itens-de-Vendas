package mbtec.com.mz.itemvendatest.domain;

public class Categoria {
    private int idcategoria;
    private String descricaocategoria;

    public Categoria() {
    }

    public Categoria(int idcategoria, String descricaocategoria) {
        this.idcategoria = idcategoria;
        this.descricaocategoria = descricaocategoria;
    }

    public int getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getDescricaocategoria() {
        return descricaocategoria;
    }

    public void setDescricaocategoria(String descricaocategoria) {
        this.descricaocategoria = descricaocategoria;
    }
}
