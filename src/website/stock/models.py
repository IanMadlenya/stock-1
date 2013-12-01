from django.db import models


class Stock(models.Model):
    symbol = models.CharField(max_length=50)
    name = models.CharField(max_length=100)


class News(models.Model):
    stock = models.ForeignKey(Stock)
    date = models.DateField()
    content = models.TextField()
    url = models.URLField()
    title = models.CharField(max_length=500)
    source = models.CharField(max_length=1000)


class Twitter(models.Model):
    stock = models.ForeignKey(Stock)
    time = models.DateTimeField()
    author = models.CharField(max_length=200)
    content = models.TextField()


class Price(models.Model):
    stock = models.ForeignKey(Stock)
    date = models.DateField()
    open = models.FloatField()
    close = models.FloatField()
    high = models.FloatField()
    low = models.FloatField()
    volume = models.FloatField()
