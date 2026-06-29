package br.unipar.poo.repository;

import java.util.List;

public interface Repositorio<T> {
    T salvar(T obj);
    T buscarPorId(int id);
    List<T> listar();
    boolean deletar(int id);
    T atualizar(int id, T dados);
}
