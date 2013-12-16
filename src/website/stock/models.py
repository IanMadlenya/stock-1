from django.db import models


class Stock(models.Model):
    symbol = models.CharField(max_length=50)
    name = models.CharField(max_length=100)


class News(models.Model):
    stock = models.ForeignKey(Stock)
    date = models.DateField()
    content = models.TextField()
    url = models.CharField(max_length=500)
    title = models.CharField(max_length=500)


class Twitter(models.Model):
    stock = models.ForeignKey(Stock)
    symbol = models.CharField(max_length=100)
    time = models.DateTimeField()
    content = models.TextField()


class Price(models.Model):
    stock = models.ForeignKey(Stock)
    date = models.DateField()
    open = models.FloatField()
    close = models.FloatField()
    high = models.FloatField()
    low = models.FloatField()
    volume = models.FloatField()
