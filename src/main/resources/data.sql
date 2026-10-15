-- Dados de exemplo carregados no H2 ao iniciar a aplicação
INSERT INTO equipamento (codigo, nome, setor, fabricante, ativo) VALUES
('TORNO-01',  'Torno CNC',                 'Usinagem',   'Romi',    TRUE),
('TORNO-02',  'Torno CNC',                 'Usinagem',   'Romi',    TRUE),
('PRENSA-01', 'Prensa hidráulica 200 t',   'Estamparia', 'Schuler', TRUE),
('COMP-01',   'Compressor de parafuso',    'Utilidades', 'Atlas Copco', TRUE),
('ROBO-01',   'Robô de solda',             'Montagem',   'ABB',     TRUE),
('FRESA-OLD', 'Fresadora convencional',    'Usinagem',   'Clever',  FALSE);

INSERT INTO ordem_servico (equipamento_id, descricao, tipo, prioridade, status, tecnico, solucao, horas_trabalhadas, aberta_em, iniciada_em, encerrada_em) VALUES
(1, 'Vibração excessiva no cabeçote',          'CORRETIVA',  'ALTA',    'CONCLUIDA',    'Marcos Silva',  'Troca dos rolamentos do fuso',      6.50, '2026-03-02 07:40:00', '2026-03-02 08:10:00', '2026-03-02 14:40:00'),
(3, 'Vazamento de óleo no cilindro principal', 'CORRETIVA',  'CRITICA', 'CONCLUIDA',    'Paulo Rocha',   'Substituição das vedações',         4.00, '2026-03-05 10:00:00', '2026-03-05 10:20:00', '2026-03-05 14:20:00'),
(4, 'Troca de filtros e óleo (2.000 h)',      'PREVENTIVA', 'MEDIA',   'CONCLUIDA',    'Marcos Silva',  'Plano preventivo executado',        2.00, '2026-03-09 08:00:00', '2026-03-09 08:00:00', '2026-03-09 10:00:00'),
(1, 'Análise de vibração mensal',              'PREDITIVA',  'BAIXA',   'CONCLUIDA',    'Juliana Lopes', 'Espectro dentro do limite',         2.50, '2026-03-16 09:00:00', '2026-03-16 09:30:00', '2026-03-16 11:00:00'),
(5, 'Falha intermitente na tocha de solda',    'CORRETIVA',  'ALTA',    'EM_ANDAMENTO', 'Paulo Rocha',   NULL,                                NULL, '2026-03-18 13:15:00', '2026-03-18 13:40:00', NULL),
(2, 'Lubrificação das guias',                  'PREVENTIVA', 'BAIXA',   'ABERTA',       NULL,            NULL,                                NULL, '2026-03-19 07:30:00', NULL, NULL),
(3, 'Ruído anormal na bomba hidráulica',       'CORRETIVA',  'CRITICA', 'ABERTA',       NULL,            NULL,                                NULL, '2026-03-20 15:05:00', NULL, NULL),
(4, 'Inspeção termográfica do painel',         'PREDITIVA',  'MEDIA',   'CANCELADA',    NULL,            'Cancelada: câmera em calibração',   NULL, '2026-03-12 09:00:00', NULL, '2026-03-12 11:00:00');
