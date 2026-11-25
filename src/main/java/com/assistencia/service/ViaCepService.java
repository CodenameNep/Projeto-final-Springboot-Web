package com.assistencia.service;

import com.assistencia.dto.ViaCepDTO;
import com.assistencia.model.Endereco;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate;

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Endereco buscarEnderecoPorCep(String cep) {

        if (cep == null || !cep.trim().matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP invalido. Informe 8 digitos.");
        }

        String cepLimpo = cep.trim().replace("-", "");
        String url = "https://viacep.com.br/ws/{cep}/json/";

        ViaCepDTO resposta;
        try {

            resposta = restTemplate.getForObject(url, ViaCepDTO.class, cepLimpo);
        } catch (RestClientException e) {
            throw new IllegalStateException("Nao foi possivel consultar o ViaCEP.", e);
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.getErro()) || resposta.getCep() == null) {
            throw new IllegalArgumentException("CEP nao encontrado.");
        }

        Endereco endereco = new Endereco();
        endereco.setCep(resposta.getCep());
        endereco.setLogradouro(resposta.getLogradouro());
        endereco.setBairro(resposta.getBairro());
        endereco.setCidade(resposta.getCidade());
        endereco.setUf(resposta.getUf());

        return endereco;
    }
}
