from django.conf.urls import patterns, include, url
from django.contrib import admin
from dajaxice.core import dajaxice_autodiscover, dajaxice_config
from django.contrib.staticfiles.urls import staticfiles_urlpatterns

dajaxice_autodiscover()
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^stock/', include('stock.urls')),
    (dajaxice_config.dajaxice_url, include('dajaxice.urls')),

    url(r'^admin/', include(admin.site.urls)),
)

urlpatterns += staticfiles_urlpatterns()