from stock.models import Stock
from django.contrib import admin


class StockAdmin(admin.ModelAdmin):
    # ...
    list_display = ('symbol', 'name')

admin.site.register(Stock, StockAdmin)
