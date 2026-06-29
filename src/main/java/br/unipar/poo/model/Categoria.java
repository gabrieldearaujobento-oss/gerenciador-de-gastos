    package br.unipar.poo.model;

    import java.math.BigDecimal;
    import java.util.Map;

    public class Categoria extends BaseModel {
        private String nome;
        private String descricao;
        private BigDecimal orcamentoMensal;

        public Categoria(String descricao, String nome, BigDecimal orcamentoMensal) {
            super();
            this.descricao = descricao;
            this.nome = nome;
            this.orcamentoMensal = orcamentoMensal;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public BigDecimal getOrcamentoMensal() {
            return orcamentoMensal;
        }

        public void setOrcamentoMensal(BigDecimal orcamentoMensal) {
            this.orcamentoMensal = orcamentoMensal;
        }



        //MÉTODOS


        public void criar(){}
        public void listar(){}
        public void buscarPorId(){}
        public void atualizar(){}
        public void deletar(){}
        public void alterarOrcamento(){}
        private void validarNome(){}
        private void validarOrcamento(){}


        @Override
        public Map<String, Object> toDict() {
            return Map.of();
        }
    }
