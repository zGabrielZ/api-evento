INSERT INTO TB_CIDADE(NOME, CODIGO, CREATED_AT) VALUES ('São Paulo', 'SAO_PAULO', '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_CIDADE(NOME, CODIGO, CREATED_AT) VALUES ('Rio de Janeiro', 'RIO_DE_JANEIRO', '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_CIDADE(NOME, CODIGO, CREATED_AT) VALUES ('Salvador', 'SALVADOR', '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_CIDADE(NOME, CODIGO, CREATED_AT) VALUES ('Porto Alegre', 'PORTO_ALEGRE', '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_CIDADE(NOME, CODIGO, CREATED_AT) VALUES ('Belo Horizonte', 'BELO_HORIZONTE', '2023-10-07 16:30:00.000000+00');

INSERT INTO TB_EVENTO(NOME, DATA_EVENTO, URL, ID_CIDADE, CREATED_AT) VALUES ('Feira do Software', '2021-05-16', 'https://feiradosoftware.com', 1, '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_EVENTO(NOME, DATA_EVENTO, URL, ID_CIDADE, CREATED_AT) VALUES ('CCXP', '2021-04-13', 'https://ccxp.com.br', 1, '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_EVENTO(NOME, DATA_EVENTO, URL, ID_CIDADE, CREATED_AT) VALUES ('Congresso Linux', '2021-05-23', 'https://congressolinux.com.br', 2, '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_EVENTO(NOME, DATA_EVENTO, URL, ID_CIDADE, CREATED_AT) VALUES ('Semana Spring React', '2021-05-03', 'https://devsuperior.com.br', 3, '2023-10-07 16:30:00.000000+00');

INSERT INTO TB_USUARIO (NOME, EMAIL, SENHA, CREATED_AT) VALUES ('Teste', 'teste@email.com', '$2a$10$y0gaYqL5wd5nKlRvyVyYd.YD7HyuogYxdjogEpM8v8Tt7VQllHe7K', '2023-10-07 16:30:00.000000+00');
INSERT INTO TB_USUARIO (NOME, EMAIL, SENHA, CREATED_AT) VALUES ('Gabriel Ferreira', 'gabriel123@email.com', '$2a$10$7JBQlZd1NVsPSjYwCZluHOlwHIxT4MdVV/6TnW4o2IaQ308cwVCXO', '2023-10-07 16:30:00.000000+00');

INSERT INTO TB_PERFIL (DESCRICAO, TIPO) VALUES ('ROLE_ADMIN', 'Administrador');
INSERT INTO TB_PERFIL (DESCRICAO, TIPO) VALUES ('ROLE_CLIENT', 'Cliente');

INSERT INTO TB_USUARIO_PERFIL (ID_USUARIO, ID_PERFIL) VALUES (1, 1);
INSERT INTO TB_USUARIO_PERFIL (ID_USUARIO, ID_PERFIL) VALUES (1, 2);
INSERT INTO TB_USUARIO_PERFIL (ID_USUARIO, ID_PERFIL) VALUES (2, 2);