package br.unipar.poo.model;

import java.time.LocalDateTime;
import java.util.Map;

public abstract class BaseModel {
    protected int id;
    protected LocalDateTime criadoEm;

    protected BaseModel() {
        this.criadoEm = LocalDateTime.now();
        this.id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "[ID =" + id + ", criadoEm = " + criadoEm + "]";
    }

    public abstract Map<String, Object> toDict();
}
