# -*- coding: utf-8 -*-
from django.db import models
from ecomm.utils import *
from ecomm.sistema.models import Loja, Usuario
import uuid
import random


TIPO_DESCONTO = (
     ('Percentual', 0),
     ("Absoluto", 1),
     (u"Frete Grátis", 2)
)


def upload_foto_produto(instance, filename):
    return str(instance.pk) + "/produto/" + instance.sku + "/" + filename

def upload_arquivo_produto_virtual_private(instance, filename):
    return str(instance.pk) + "/produto/privado/" + instance.sku + "/" + filename

def upload_arquivo_produto_virtual(instance, filename):
    return str(instance.pk) + "/produto/" + instance.sku + "/" + filename

class Sku(models.Model):
    sku = models.CharField(max_length=200, unique=True, null=False, blank=True)
    data_criacao = models.DateTimeField(auto_now_add=True)
    loja = models.ForeignKey(Loja, null=True, blank=True)
    
    @staticmethod
    def gerar_novo():
        def new_sku():
            valid_letters='ABCDEFGHIJKLMNOPQRSTUVWXYZ'
            letter = ''.join((random.choice(valid_letters) for i in xrange(3)))
            numbers = str(uuid.uuid4().fields[-1])[:4]
            return letter + numbers
        
        sku_ret = new_sku()
        while len(Sku.objects.filter(sku=sku_ret)) > 0:
            sku_ret  = new_sku()
        return sku_ret
    
class ProdutoBase(Sku):
    nome = models.CharField(max_length=200)
    chamada = models.TextField(max_length=1000)
    descricao = models.TextField(max_length=1000, null=True, blank=True)
    foto_principal = models.ImageField(upload_to=upload_foto_produto, null=True, blank=True)
    ativo = models.BooleanField(default=False)
    
    def __unicode__(self):
        return self.nome
    
    class Meta:
        verbose_name=u'Todos os produtos'
        verbose_name_plural=u'Todos os produtos'

class Produto(ProdutoBase):
    pass

class Kit(ProdutoBase):
    produtos = models.ManyToManyField("Produto")
    
class ProdutoFisico(Produto):
    altura = models.IntegerField(default=0, help_text=u"Valor em centímetros. Ex: 132 cm ( equivale a 1 metro e 32 )")
    largura = models.IntegerField(default=0, help_text=u"Valor em centímetros. Ex: 132 cm ( equivale a 1 metro e 32 )")
    profundidade = models.IntegerField(default=0, help_text=u"Valor em centímetros. Ex: 132 cm ( equivale a 1 metro e 32 )")
    peso = models.IntegerField(default=0, help_text=u"Valor em gramas. Ex: 1450 gr ( equivale a 1 quilo quatrocentos e cinquenta gramas )")
    quantidade = models.IntegerField(default=0)
    quantidade_min = models.IntegerField(default=0)
    
    class Meta:
        verbose_name=u'Produto Físico' 
        verbose_name_plural=u'Produtos Físicos'
    
class ProdutoVirtual(Produto):
    arquivo = models.FileField(upload_to=upload_arquivo_produto_virtual_private)
    amostra = models.FileField(upload_to=upload_arquivo_produto_virtual, null=True, blank=True)
    
    class Meta:
        verbose_name=u'Produto Virtual' 
        verbose_name_plural=u'Produtos Virtuais'

class PrecoProduto(models.Model):
    valor = models.DecimalField(max_digits=20, decimal_places=2)
    data_criacao = models.DateTimeField(auto_now_add=True)
    data_descontinuado = models.DateTimeField(null=True, blank=True)
    produto = models.ForeignKey("ProdutoBase")
    ativo = models.BooleanField(default=False)
    
class Estoque(ProdutoFisico):
    class Meta:
        proxy = True
        verbose_name=u'Estoque' 
        verbose_name_plural=u'Estoque'

class CategoriaProduto(models.Model):
    nome = models.CharField(max_length=200)
    produtos = models.ManyToManyField("ProdutoBase", null=True, blank=True)
    categoria_pai = models.ForeignKey("CategoriaProduto", null=True, blank=True)
    loja = models.ForeignKey(Loja)
    
    def __unicode__(self):
        return self.nome

class ProdutoFichaTecnica(models.Model):
    titulo = models.CharField(max_length=200)
    descricao = models.CharField(max_length=200)
    produto = models.ForeignKey("ProdutoBase")

class Promocao(models.Model):
    desconto = models.DecimalField(max_digits=20, decimal_places=2)
    tipo = models.IntegerField(choices=TIPO_DESCONTO)
    ativo = models.BooleanField(default=False)
    valor_minimo_aplicado = models.DecimalField(max_digits=20, decimal_places=2)
    loja = models.ForeignKey(Loja)
    necessita_cupom = models.BooleanField(default=False)

class PromocaoProduto(Promocao):
    produtos = models.ManyToManyField(Produto)

class PromocaoData(Promocao):
    de = models.DateTimeField()
    ate = models.DateTimeField()

#class PromocaoCupom(Promocao):
#    cupom_grupo = models.ForeignKey("CupomGrupo")
    
#class PromocaoCupomProduto(Promocao):
#    cupom_grupo = models.ForeignKey("CupomGrupo")
#    produtos = models.ManyToManyField("ProdutoBase")
    
#class Cupom(models.Model):
#    numero = models.CharField(max_length=200)
#    loja = models.ForeignKey("Loja")
#    queimado = models.BooleanField(default=False)
    
#class CupomGrupo(models.ForeignKey):
#    nome = models.CharField(max_length=200)
#    cupons = models.ManyToManyField("Cupom")
#    loja = models.ForeignKey("Loja")
    
class FotoProduto(models.Model):
    foto = models.ImageField(upload_to=upload_foto_produto)
    legenda = models.CharField(max_length=200)
    produto = models.ForeignKey("ProdutoBase")

