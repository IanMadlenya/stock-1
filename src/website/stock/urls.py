from django.conf.urls import patterns, url
from django.contrib import admin
from stock import views

admin.autodiscover()

urlpatterns = patterns('',
    # ex: /stock
    url(r'^$', views.index, name='index'),
    # ex: /stock/1
    url(r'^(?P<stockid>\d+)/$', views.detail, name='detail'),

    url(r'^register/$', views.register, name='register'),
    url(r'^(?P<stockid>\d+)/delete$', views.delete, name='delete'),
    url(r'^(?P<stockid>\d+)/update$', views.update, name='update'),
)
