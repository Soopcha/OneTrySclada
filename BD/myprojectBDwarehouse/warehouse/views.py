from django.shortcuts import render

#cd .\myprojectBDwarehouse\ табом
#(.venv) PS C:\lessons\prog\bd\1\new1\BD\myprojectBDwarehouse> python manage.py runserver

#curl -X GET http://127.0.0.1:8000/api/users/
#curl -X GET http://10.0.2.2:8000/api/users/

#curl -X PUT -H "Content-Type: application/json" -d "{\"user_name\": \"New Name\", \"email\": \"newemail@example.com\", \"login\": \"newlogin\", \"password\": \"newpassword\", \"role\": \"user\"}" http://127.0.0.1:8000/api/users/23/
#телефон не обязателен так что и без него можно
#curl -X PUT -H "Content-Type: application/json" -d "{\"user_name\": \"New Name222\", \"email\": \"newemail@example.com\", \"login\": \"newlogin\", \"password\": \"newpassword\", \"role\": \"user\", \"phone_number\": \"+1000007890\"}" http://127.0.0.1:8000/api/users/33/

#curl -X GET "http://127.0.0.1:8000/api/users/?user_name=User%202" фиьтрация
#curl -X GET "http://127.0.0.1:8000/api/users/?role=user" фиьтрация  - тут лучше пример только юзеры вылетают

#curl -X GET "http://127.0.0.1:8000/api/users/?user_name=John" - поиск
#curl -X GET "http://127.0.0.1:8000/api/users/?user_name=User%202&role=admin" - поиск несколько параметров

#curl -X GET "http://127.0.0.1:8000/api/users/?ordering=user_name" - сортировка по имени
#curl -X GET "http://127.0.0.1:8000/api/users/?ordering=-user_name" - сортировка в обратном порядке







# warehouse/views.py
#1. Использование REST API
#Создание API на Django: В Django можно настроить REST API с помощью библиотеки Django REST Framework (DRF).
# Этот API позволит вашему Kotlin-приложению отправлять HTTP-запросы для выполнения операций CRUD
# (создание, чтение, обновление, удаление) с таблицами базы данных.
#Пример: Создайте API-контроллеры, которые обрабатывают запросы для ваших моделей.
# Например, для модели User создайте API-представления (views) для GET, POST, PUT, DELETE запросов.
#Kotlin (Android или Desktop): В Kotlin используйте Retrofit или OkHttp для отправки HTTP-запросов к API.
# Это позволит вам отправлять данные из вашего приложения в Django и получать ответы.

#со стороны сервера создается спец интерфейс API (Application programming interface) и  Django REST Framework (DRF)
#обеспечивает взаимодествие через апи с приложением

from rest_framework import viewsets
from .models import User, Shipment, Product
from .serializers import *
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.filters import SearchFilter, OrderingFilter




#viewsets.ModelViewSet — это класс во viewsets из DRF, который предоставляет стандартные методы для работы с CRUD
#(создание, чтение, обновление, удаление) для моделей. Он автоматически создаёт обработчики для каждого типа запроса:
#
#GET для получения списка всех записей или одной записи,
#POST для создания новой записи,
#PUT/ PATCH для обновления существующей записи,
#DELETE для удаления записи.

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()   #указывает, с какими данными работать (в данном случае, все объекты модели User)
    serializer_class = UserSerializer   #указывает, каким сериализатором обрабатывать данные, передаваемые и получаемые через API.
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['user_name', 'email', 'phone_number']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['user_id']  # Сортировка по умолчанию

class ShipmentViewSet(viewsets.ModelViewSet):
    queryset = Shipment.objects.all()
    serializer_class = ShipmentSerializer

    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['quantity']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['shipment_id']

class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.all()
    serializer_class = ProductSerializer

    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['product_name']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['product_id']

class WriteOffOfProductsViewSet(viewsets.ModelViewSet):
    queryset = WriteOffOfProducts.objects.all()
    serializer_class = WriteOffOfProductsSerializer

    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['reason']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['id_product_write_off']

class ExtraditionViewSet(viewsets.ModelViewSet):
    queryset = Extradition.objects.all()
    serializer_class = ExtraditionSerializer

    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['quantity']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['extradition_id']

class ProductsCurrentQuantityViewSet(viewsets.ModelViewSet):
    queryset = ProductsCurrentQuantity.objects.all()
    serializer_class = ProductsCurrentQuantitySerializer

    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['quantity']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['product_current_quantity_id']

