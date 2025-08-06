package ufjf_dcc025.franquiasystem.controllers;

import ufjf_dcc025.franquiasystem.models.Produto;
import ufjf_dcc025.franquiasystem.repositories.ProdutoRepository;

import java.util.List;

public class ProdutoController {
    private final ProdutoRepository produtoRepository;

    public ProdutoController() {
        this.produtoRepository = new ProdutoRepository();
    }

    public void create(Produto produto) {
        produtoRepository.create(produto);
    }

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public void update(Produto produto) {
        produtoRepository.update(produto);
    }

    public void delete(int id) {
        produtoRepository.delete(id);
    }
}