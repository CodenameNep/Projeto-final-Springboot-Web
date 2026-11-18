package com.assistencia.service;

import com.assistencia.model.Endereco;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ViaCepServiceTest {

    @Test
    void devePreencherEnderecoComRespostaDoViaCep() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer servidor = MockRestServiceServer.createServer(restTemplate);
        servidor.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "01001-000",
                          "logradouro": "Praça da Sé",
                          "bairro": "Sé",
                          "localidade": "São Paulo",
                          "uf": "SP",
                          "ibge": "3550308"
                        }
                        """, MediaType.APPLICATION_JSON));

        Endereco endereco = new ViaCepService(restTemplate).buscarEnderecoPorCep("01001-000");

        assertEquals("01001-000", endereco.getCep());
        assertEquals("Praça da Sé", endereco.getLogradouro());
        assertEquals("Sé", endereco.getBairro());
        assertEquals("São Paulo", endereco.getCidade());
        assertEquals("SP", endereco.getUf());
        servidor.verify();
    }

    @Test
    void deveRejeitarCepInvalidoAntesDeFazerRequisicao() {
        ViaCepService service = new ViaCepService(new RestTemplate());
        assertThrows(IllegalArgumentException.class, () -> service.buscarEnderecoPorCep("123"));
    }

    @Test
    void deveAvisarQuandoCepNaoExiste() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer servidor = MockRestServiceServer.createServer(restTemplate);
        servidor.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": true}", MediaType.APPLICATION_JSON));

        ViaCepService service = new ViaCepService(restTemplate);
        assertThrows(IllegalArgumentException.class, () -> service.buscarEnderecoPorCep("99999999"));
        servidor.verify();
    }

    @Test
    void deveAvisarQuandoApiFalhar() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer servidor = MockRestServiceServer.createServer(restTemplate);
        servidor.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withServerError());

        ViaCepService service = new ViaCepService(restTemplate);
        assertThrows(IllegalStateException.class, () -> service.buscarEnderecoPorCep("01001000"));
        servidor.verify();
    }
}
