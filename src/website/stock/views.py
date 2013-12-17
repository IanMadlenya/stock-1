from django.shortcuts import get_object_or_404
from django.http import HttpResponse, HttpResponseRedirect
from django.template import RequestContext, loader
from django.core.urlresolvers import reverse
from stock.models import Stock, Price, News, Twitter
import subprocess
import datetime


def index(request):
    stock_list = Stock.objects.all()
    template = loader.get_template('stock/index.html')
    context = RequestContext(request, {
        'stock_list': stock_list,
    })
    return HttpResponse(template.render(context))


def register(request):
    stock = Stock.objects.filter(symbol=request.POST['symbol'])
    if not stock:
        stock = Stock(symbol=request.POST['symbol'], name=request.POST['name'])
        stock.save()
        subprocess.Popen(['java', '-jar', '/Users/shijieru/Desktop/updateDB.jar'])
    return HttpResponseRedirect(reverse('index'))


def detail(request, stockid):
    stock = get_object_or_404(Stock, pk=stockid)
    price_list = Price.objects.filter(stock_id=stockid).order_by('-date')[:30]
    for price in price_list:
        price.date = price.date.strftime("%Y-%m-%d")
    news_list = News.objects.filter(stock_id=stockid).order_by('-date')
    template = loader.get_template('stock/detail.html')
    context = RequestContext(request, {
        'stock': stock,
        'price_list': price_list,
        'news_list': news_list,
    })
    return HttpResponse(template.render(context))


def delete(request, stockid):
    get_object_or_404(Stock, pk=stockid).delete()
    Price.objects.filter(stock_id=stockid).delete()
    News.objects.filter(stock_id=stockid).delete()
    Twitter.objects.filter(stock_id=stockid).delete()
    return HttpResponseRedirect(reverse('index'))


def update(request, stockid):
    stock = get_object_or_404(Stock, pk=stockid)
    stock.name = request.POST['name']
    stock.save()
    return HttpResponseRedirect(reverse('index'))


