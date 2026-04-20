package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventoResourceTest {

    private static String tokenAdmin;
    private static String tokenUser;
    private static Long eventoId;

    // =================================================================
    // TESTES DE AUTENTICAÇÃO
    // =================================================================

    @Test
    @Order(1)
    @DisplayName("1. Deve fazer login como ADMIN e obter token")
    void testLoginAdmin() {
        tokenAdmin = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "admin",
                            "senha": "admin123"
                        }
                        """)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getString("token");

        System.out.println("✅ Token Admin obtido: " + tokenAdmin.substring(0, 50) + "...");
    }

    @Test
    @Order(2)
    @DisplayName("2. Deve fazer login como USER e obter token")
    void testLoginUser() {
        tokenUser = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "username": "user",
                            "senha": "user123"
                        }
                        """)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getString("token");

        System.out.println("✅ Token User obtido: " + tokenUser.substring(0, 50) + "...");
    }

    // =================================================================
    // TESTES PÚBLICOS (sem autenticação)
    // =================================================================

    @Test
    @Order(3)
    @DisplayName("3. Deve listar eventos sem autenticação (público)")
    void testListarEventosPublico() {
        given()
                .when()
                .get("/eventos")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].nome", notNullValue());

        System.out.println("✅ Listagem pública funcionando");
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve buscar evento por ID sem autenticação (público)")
    void testBuscarEventoPorIdPublico() {
        given()
                .when()
                .get("/eventos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nome", equalTo("Palestra sobre Sustentabilidade"))
                .body("linkInscricao", equalTo("https://forms.google.com/exemplo"));

        System.out.println("✅ Busca pública por ID funcionando");
    }

    @Test
    @Order(5)
    @DisplayName("5. Health check deve retornar OK")
    void testHealthCheck() {
        given()
                .when()
                .get("/eventos/health")
                .then()
                .statusCode(200);

        System.out.println("✅ Health check funcionando");
    }

    // =================================================================
    // TESTES DE PERMISSÃO (ADMIN vs USER)
    // =================================================================

    @Test
    @Order(6)
    @DisplayName("6. USER não deve conseguir criar evento (403 Forbidden)")
    void testUserNaoPodeCriarEvento() {
        given()
                .header("Authorization", "Bearer " + tokenUser)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Evento Teste",
                            "descricao": "Descrição do evento de teste",
                            "dataHora": "2030-12-20T14:00:00",
                            "local": "Local Teste",
                            "participantes": 50
                        }
                        """)
                .when()
                .post("/eventos")
                .then()
                .statusCode(403); // Forbidden

        System.out.println("✅ USER bloqueado de criar evento (correto)");
    }

    @Test
    @Order(7)
    @DisplayName("7. ADMIN deve conseguir criar evento")
    void testAdminPodeCriarEvento() {
        eventoId = given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Workshop de Reciclagem",
                            "descricao": "Workshop educativo sobre reciclagem de materiais",
                            "dataHora": "2030-12-20T14:00:00",
                            "local": "Centro Ambiental FMA",
                            "categoria": "Workshop",
                            "contato": "workshop@fma.palmas.to.gov.br",
                            "requisitos": "Trazer materiais recicláveis",
                            "participantes": 80,
                            "linkInscricao": "https://forms.google.com/workshop"
                        }
                        """)
                .when()
                .post("/eventos")
                .then()
                .log().all()
                .statusCode(201)
                .body("nome", equalTo("Workshop de Reciclagem"))
                .body("organizador", equalTo("admin"))
                .body("participantes", equalTo(80))
                .body("linkInscricao", equalTo("https://forms.google.com/workshop"))
                .extract()
                .jsonPath().getLong("id");

        System.out.println("✅ ADMIN criou evento com ID: " + eventoId);
    }

    // =================================================================
    // TESTES DE VALIDAÇÃO
    // =================================================================

    @Test
    @Order(8)
    @DisplayName("8. Deve retornar erro 400 ao criar evento sem nome")
    void testCriarEventoSemNome() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "descricao": "Evento sem nome",
                            "dataHora": "2030-12-20T14:00:00",
                            "local": "Local"
                        }
                        """)
                .when()
                .post("/eventos")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("titulo", equalTo("Dados inválidos"));

        System.out.println("✅ Validação de nome obrigatório funcionando");
    }

    @Test
    @Order(9)
    @DisplayName("9. Deve retornar erro 400 ao criar evento com data no passado")
    void testCriarEventoComDataPassado() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Evento Teste",
                            "descricao": "Descrição válida do evento",
                            "dataHora": "2020-01-01T10:00:00",
                            "local": "Local"
                        }
                        """)
                .when()
                .post("/eventos")
                .then()
                .statusCode(400)
                .body("status", equalTo(400));

        System.out.println("✅ Validação de data futura funcionando");
    }

    // =================================================================
    // TESTES DE ATUALIZAÇÃO
    // =================================================================

    @Test
    @Order(10)
    @DisplayName("10. ADMIN deve conseguir atualizar evento")
    void testAdminPodeAtualizarEvento() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Workshop de Reciclagem - ATUALIZADO",
                            "descricao": "Workshop educativo sobre reciclagem de materiais",
                            "dataHora": "2030-12-20T14:00:00",
                            "local": "Centro Ambiental FMA",
                            "categoria": "Workshop",
                            "participantes": 120,
                            "linkInscricao": "https://forms.google.com/workshop-novo"
                        }
                        """)
                .when()
                .put("/eventos/" + eventoId)
                .then()
                .statusCode(200)
                .body("nome", containsString("ATUALIZADO"))
                .body("participantes", equalTo(120));

        System.out.println("✅ ADMIN atualizou evento com sucesso");
    }

    @Test
    @Order(11)
    @DisplayName("11. USER não deve conseguir atualizar evento (403)")
    void testUserNaoPodeAtualizarEvento() {
        given()
                .header("Authorization", "Bearer " + tokenUser)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Tentativa de atualização",
                            "descricao": "Teste",
                            "dataHora": "2030-12-20T14:00:00",
                            "local": "Local"
                        }
                        """)
                .when()
                .put("/eventos/1")
                .then()
                .statusCode(403);

        System.out.println("✅ USER bloqueado de atualizar evento (correto)");
    }

    // =================================================================
    // TESTES SEM AUTENTICAÇÃO
    // =================================================================

    @Test
    @Order(12)
    @DisplayName("12. Deve retornar 401 ao tentar criar evento sem token")
    void testCriarEventoSemToken() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Evento Teste",
                            "descricao": "Teste sem autenticação",
                            "dataHora": "2030-12-20T14:00:00",
                            "local": "Local"
                        }
                        """)
                .when()
                .post("/eventos")
                .then()
                .statusCode(401);

        System.out.println("✅ Criação sem token bloqueada (401)");
    }

    // =================================================================
    // TESTE DE DELEÇÃO
    // =================================================================

    @Test
    @Order(13)
    @DisplayName("13. ADMIN deve conseguir deletar evento")
    void testAdminPodeDeletarEvento() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .when()
                .delete("/eventos/" + eventoId)
                .then()
                .statusCode(204);

        System.out.println("✅ ADMIN deletou evento com sucesso");
    }

    // =================================================================
    // TESTES DE INSCRIÇÃO
    // =================================================================

    @Test
    @Order(14)
    @DisplayName("14. Deve criar inscrição em evento público")
    void testCriarInscricao() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "eventoId": 1,
                            "nome": "João da Silva",
                            "email": "joao@teste.com",
                            "telefone": "63999999999"
                        }
                        """)
                .when()
                .post("/inscricoes")
                .then()
                .statusCode(201)
                .body("nome", equalTo("João da Silva"))
                .body("eventoNome", notNullValue());

        System.out.println("✅ Inscrição criada com sucesso");
    }

    @Test
    @Order(15)
    @DisplayName("15. Deve rejeitar inscrição com email duplicado")
    void testCriarInscricaoEmailDuplicado() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "eventoId": 1,
                            "nome": "João Duplicado",
                            "email": "joao@teste.com",
                            "telefone": "63999999999"
                        }
                        """)
                .when()
                .post("/inscricoes")
                .then()
                .statusCode(400);

        System.out.println("✅ Email duplicado rejeitado corretamente");
    }

    @Test
    @Order(16)
    @DisplayName("16. Deve listar inscrições apenas para ADM")
    void testListarInscricoesRequerAdmin() {
        given()
                .when()
                .get("/inscricoes")
                .then()
                .statusCode(401);

        System.out.println("✅ Listagem de inscrições bloqueada sem token");
    }

    @Test
    @Order(17)
    @DisplayName("17. ADM deve conseguir listar inscrições")
    void testAdminListaInscricoes() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .when()
                .get("/inscricoes")
                .then()
                .statusCode(200);

        System.out.println("✅ Admin listou inscrições com sucesso");
    }

    // =================================================================
    // TESTES DE USUÁRIO
    // =================================================================

    @Test
    @Order(18)
    @DisplayName("18. Deve criar novo usuário")
    void testCriarUsuario() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Novo Usuario",
                            "email": "novo@teste.com",
                            "username": "novousuario",
                            "senha": "senha123",
                            "id_perfil": 2
                        }
                        """)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(201)
                .body("username", equalTo("novousuario"));

        System.out.println("✅ Usuário criado com sucesso");
    }

    @Test
    @Order(19)
    @DisplayName("19. Deve rejeitar username duplicado")
    void testCriarUsuarioUsernameDuplicado() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "nome": "Admin Duplicado",
                            "email": "outro@teste.com",
                            "username": "admin",
                            "senha": "senha123",
                            "id_perfil": 2
                        }
                        """)
                .when()
                .post("/usuarios")
                .then()
                .statusCode(400);

        System.out.println("✅ Username duplicado rejeitado corretamente");
    }

    @Test
    @Order(20)
    @DisplayName("20. Deve retornar 404 para evento inexistente")
    void testBuscarEventoInexistente() {
        given()
                .when()
                .get("/eventos/99999")
                .then()
                .statusCode(404);

        System.out.println("✅ 404 retornado para evento inexistente");
    }
}
