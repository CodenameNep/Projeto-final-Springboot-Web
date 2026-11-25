# TechFix OS — Sistema de Gestão de Assistência Técnica

Sistema web completo desenvolvido para a disciplina de **Desenvolvimento de Software II (DS-II)**. O objetivo é fornecer uma solução integrada para assistência técnica e reparos, gerenciando clientes, equipamentos, catálogo de serviços e o ciclo de vida completo de ordens de serviço (abertura, adição de serviços, cálculo automático de totais, transição de status e encerramento com laudo técnico).

---

## 👥 Integrantes do Grupo

| Nome | Usuário GitHub |
| :--- | :--- |
| **Leandro Andrade** | [@CodenameNep](https://github.com/CodenameNep) |
| **Ryan Xavier** | [@Ryan-Xavier07](https://github.com/Ryan-Xavier07) |
| **Danilo Almeida** | [@danilo111267](https://github.com/danilo111267) |

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem & Plataforma:** Java 17
* **Framework Principal:** Spring Boot 3.2.5
  * **Spring MVC:** Arquitetura em camadas (*Controller*, *Service*, *Repository*)
  * **Spring Data JPA / Hibernate:** Mapeamento objeto-relacional e persistência
  * **Spring Security:** Autenticação e controle de acesso baseado em perfis (`ROLE_ADMIN` e `ROLE_TECNICO`)
  * **Bean Validation:** Validação de integridade de dados nos DTOs e entidades
* **Banco de Dados:** H2 Database (banco relacional em memória com console web habilitado)
* **Frontend:** Thymeleaf + Bootstrap 5.3.3 + JavaScript Vanilla (Fetch API / DOM)
* **API Externa:** [ViaCEP](https://viacep.com.br/) (consumo HTTP REST via `RestTemplate` para busca automática de endereço por CEP)
* **Gerenciador de Dependências:** Apache Maven

---

## 🏛️ Entidades Persistentes do Sistema (7 Entidades)

O domínio do sistema atende à exigência mínima de 7 entidades relacionais interligadas:

1. `Usuario`: Controle de acesso de técnicos e administradores com senhas criptografadas em BCrypt.
2. `Cliente`: Dados cadastrais de contato e vinculação aos equipamentos e ordens de serviço.
3. `Endereco`: Armazena dados de logradouro, bairro, cidade, UF e CEP (com preenchimento via ViaCEP).
4. `Equipamento`: Aparelhos trazidos para reparo (marca, modelo, tipo, número de série), vinculados ao cliente.
5. `Servico`: Catálogo de procedimentos da assistência com descrição e preço-base.
6. `OrdemServico`: Entidade central com protocolo único, datas de abertura/fechamento, status, laudo técnico e valor total.
7. `ItemOrdemServico`: Associação N:N entre a Ordem de Serviço e os Serviços executados, com quantidade, preço congelado no ato e subtotal.

---

## 🚀 Instruções de Execução

### Pré-requisitos
* **Java Development Kit (JDK) 17** ou superior instalado e configurado no `PATH`.
* **Apache Maven** (ou extensão Maven na sua IDE).

### 1. Clonar o repositório
```bash
git clone https://github.com/CodenameNep/Projeto-final-Springboot-Web.git
cd Projeto-final-Springboot-Web
```

### 2. Executar a aplicação

* **Via terminal (Maven):**
```bash
mvn spring-boot:run
```

* **Via IDE (VS Code, IntelliJ IDEA ou Eclipse):**
Abra o projeto e execute o método `main` da classe:
`src/main/java/com/assistencia/AssistenciaTecnicaApplication.java`

### 3. Acessar a aplicação
Abra o navegador e acesse:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🔑 Credenciais Pré-cadastradas para Demonstração

O sistema inicializa automaticamente (`DatabaseSeeder`) com dois usuários prontos para teste:

| Perfil | E-mail / Login | Senha | Acesso |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@techfix.com` | `admin123` | Acesso total (Usuários, Clientes, Equipamentos, Serviços e Ordens) |
| **Técnico** | `tecnico@techfix.com` | `tec123` | Operação diária (Clientes, Equipamentos, Serviços e Ordens) |

---

## 🗄️ Console do Banco de Dados H2

Para inspecionar as tabelas e registros diretamente no banco de dados em tempo de execução:
* **URL:** **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**
* **JDBC URL:** `jdbc:h2:mem:assistenciadb`
* **User Name:** `sa`
* **Password:** *(deixar em branco)*

---

## 🔗 Links

* **Repositório GitHub:** [https://github.com/CodenameNep/Projeto-final-Springboot-Web](https://github.com/CodenameNep/Projeto-final-Springboot-Web)
