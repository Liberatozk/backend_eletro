USE [EletroRecicla]
GO
SET IDENTITY_INSERT [dbo].[CategoriaProduto] ON 

INSERT [dbo].[CategoriaProduto] ([Id], [Nome], [Descricao]) VALUES (1, N'Celulares e Tablets', N'Smartphones, tablets e acessórios relacionados')
INSERT [dbo].[CategoriaProduto] ([Id], [Nome], [Descricao]) VALUES (2, N'Computadores e Periféricos', N'Notebooks, desktops, teclados, mouses, cabos')
INSERT [dbo].[CategoriaProduto] ([Id], [Nome], [Descricao]) VALUES (3, N'Pilhas e Baterias', N'Pilhas comuns, baterias recarregáveis e de celular')
INSERT [dbo].[CategoriaProduto] ([Id], [Nome], [Descricao]) VALUES (4, N'Eletrodomésticos Pequenos', N'Liquidificadores, secadores, ferros de passar, etc.')
SET IDENTITY_INSERT [dbo].[CategoriaProduto] OFF
GO
SET IDENTITY_INSERT [dbo].[Produto] ON 

INSERT [dbo].[Produto] ([Id], [Nome], [CategoriaId], [PontosPorKg], [Descricao], [Status]) VALUES (1, N'Smartphone', 1, CAST(15.00 AS Decimal(5, 2)), N'Aparelhos de celular sem uso', N'ATIVO')
INSERT [dbo].[Produto] ([Id], [Nome], [CategoriaId], [PontosPorKg], [Descricao], [Status]) VALUES (2, N'Notebook', 2, CAST(10.00 AS Decimal(5, 2)), N'Notebooks e laptops', N'ATIVO')
INSERT [dbo].[Produto] ([Id], [Nome], [CategoriaId], [PontosPorKg], [Descricao], [Status]) VALUES (3, N'Teclado/Mouse', 2, CAST(5.00 AS Decimal(5, 2)), N'Periféricos de computador', N'ATIVO')
INSERT [dbo].[Produto] ([Id], [Nome], [CategoriaId], [PontosPorKg], [Descricao], [Status]) VALUES (4, N'Bateria de Íon-Lítio', 3, CAST(20.00 AS Decimal(5, 2)), N'Baterias recarregáveis', N'ATIVO')
INSERT [dbo].[Produto] ([Id], [Nome], [CategoriaId], [PontosPorKg], [Descricao], [Status]) VALUES (5, N'Pilha Comum', 3, CAST(8.00 AS Decimal(5, 2)), N'Pilhas AA/AAA e similares', N'ATIVO')
SET IDENTITY_INSERT [dbo].[Produto] OFF
GO
