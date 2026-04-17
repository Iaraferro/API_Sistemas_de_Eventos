-- =====================================================
-- SEED DATA - Dados iniciais para testes
-- IDs gerados automaticamente pelo banco (sem conflito)
-- =====================================================

-- Usuário ADMINISTRADOR — username: admin / senha: admin123
INSERT INTO Usuario (username, senha, perfil, email)
VALUES (
    'admin',
    'L8WgDw8LUrM6YCfnW8/YM1Nhp0il+oTyhaUJNFNmZLfKXnVmwDXUIKNoI2UqdmRtne6VbKDWsNX1Qcu5TPwL2A==',
    1,
    'admin@fma.palmas.to.gov.br'
);

-- Usuário COMUM — username: user / senha: user123
INSERT INTO Usuario (username, senha, perfil, email)
VALUES (
    'user',
    '1hqwsHJyIo1VwsLkMdoOBIIj/pe8FtP0LYkNhKpcikh0bc8EMajBDr6gKJVPWff+I3y/D3SMZRgpMpxy9MRehg==',
    2,
    'user@teste.com'
);

-- =====================================================
-- EVENTOS PASSADOS — aparecem em "Eventos Realizados"
-- =====================================================

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Palestra sobre Sustentabilidade',
    'Evento de conscientização ambiental promovido pela FMA Palmas. Discutimos práticas sustentáveis no cotidiano.',
    '2024-12-15 14:00:00',
    'Auditório FMA',
    'Palestra',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Trazer caneta e caderno',
    100,
    null,
    'https://forms.google.com/exemplo'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Mutirão de Limpeza do Lago de Palmas',
    'Ação coletiva de limpeza das margens do Lago de Palmas com coleta seletiva e educação ambiental.',
    '2025-03-22 07:30:00',
    'Orla de Palmas - Praia da Graciosa',
    'Mutirão',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Usar protetor solar e trazer luvas',
    200,
    null,
    'https://forms.google.com/mutirao-lago'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Feira de Produtos Orgânicos',
    'Feira com produtores locais de alimentos orgânicos, artesanato sustentável e gastronomia natural.',
    '2025-08-10 08:00:00',
    'Praça dos Girassóis',
    'Feira',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Nenhum requisito',
    500,
    null,
    'https://forms.google.com/feira-organicos'
);

-- =====================================================
-- EVENTOS FUTUROS — aparecem em "Próximos Eventos"
-- =====================================================

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Workshop de Tecnologia Verde',
    'Hands-on sobre energias renováveis e sustentabilidade digital. Aprenda sobre painéis solares e IoT ambiental.',
    '2027-06-20 09:00:00',
    'Sala de Reuniões FMA',
    'Workshop',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Nenhum requisito',
    50,
    null,
    'https://forms.google.com/workshop-tech'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Plantio de Mudas Nativas do Cerrado',
    'Atividade de reflorestamento com espécies nativas do Cerrado tocantinense. Venha plantar o futuro!',
    '2027-07-05 07:00:00',
    'Parque Cesamar - Palmas',
    'Mutirão',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Usar roupas que possam sujar e calçado fechado',
    150,
    null,
    'https://forms.google.com/plantio-cerrado'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Ciclo de Palestras: Mudanças Climáticas',
    'Série de palestras com pesquisadores e especialistas sobre os impactos das mudanças climáticas no Tocantins.',
    '2027-08-12 14:00:00',
    'Auditório da UFT - Campus Palmas',
    'Palestra',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Inscrição prévia obrigatória',
    300,
    null,
    'https://forms.google.com/mudancas-climaticas'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Hackathon Sustentável Palmas 2027',
    'Maratona de inovação para criar soluções tecnológicas para problemas ambientais da cidade de Palmas.',
    '2027-09-20 08:00:00',
    'Hub de Inovação de Palmas',
    'Hackathon',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Trazer notebook e ter conhecimentos básicos de programação',
    120,
    null,
    'https://forms.google.com/hackathon-sustentavel'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Seminário de Gestão de Resíduos Sólidos',
    'Debate sobre políticas públicas e tecnologias para gestão de resíduos sólidos urbanos em Palmas.',
    '2027-10-08 09:00:00',
    'Centro de Convenções de Palmas',
    'Seminário',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Nenhum requisito',
    400,
    null,
    'https://forms.google.com/residuos-solidos'
);

INSERT INTO Evento (nome, descricao, dataHora, local, categoria, organizador, contato, requisitos, participantes, imagemPrincipal, linkInscricao)
VALUES (
    'Corrida Ecológica EcoRun Palmas',
    'Corrida de rua com percurso de 5km e 10km em meio à natureza, com coleta de lixo ao longo do trajeto.',
    '2027-11-15 06:00:00',
    'Parque dos Povos Indígenas',
    'Esporte',
    'admin',
    'eventos@fma.palmas.to.gov.br',
    'Trazer garrafa d''água reutilizável',
    800,
    null,
    'https://forms.google.com/ecorun-palmas'
);