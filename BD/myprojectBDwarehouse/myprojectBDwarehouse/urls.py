"""
URL configuration for myprojectBDwarehouse project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

from django.contrib import admin
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from warehouse.views import UserViewSet, ShipmentViewSet, ProductViewSet, WriteOffOfProductsViewSet, ExtraditionViewSet, ProductsCurrentQuantityViewSet

from warehouse.login_logout import LoginView, LogoutView, ProfileView

# Создаем роутер и регистрируем ViewSet-ы
router = DefaultRouter()
router.register(r'users', UserViewSet)
router.register(r'shipments', ShipmentViewSet)
router.register(r'products', ProductViewSet)
router.register(r'write-off-products', WriteOffOfProductsViewSet)
router.register(r'extraditions', ExtraditionViewSet)
router.register(r'products-current-quantity', ProductsCurrentQuantityViewSet)

urlpatterns = [
    path('admin/', admin.site.urls),  # Админка
    path('api/', include(router.urls)),  # API для CRUD
    path('api/login/', LoginView.as_view(), name='login'),
    path('api/logout/', LogoutView.as_view(), name='logout'),
    path('api/profile/', ProfileView.as_view(), name='profile'),
]

#urlpatterns = [
#    path('admin/', admin.site.urls),
#]

