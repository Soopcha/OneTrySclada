from django.shortcuts import render

#cd .\myprojectBDwarehouse\ табом
#(.venv) PS C:\lessons\prog\bd\1\new1\BD\myprojectBDwarehouse> python manage.py runserver

#Authorization
#Token fe179bd9c61d09b2f08cc0d7ef5146ace411f120
#Header

#POST http://127.0.0.1:8000/api/users/
#{
#   "user_name": "New User",
#   "email": "newuser@example.com",
#   "login": "newuser",
#   "password": "newpassword",
#   "role": "user"
# }


# POST
# http://127.0.0.1:8000/api/login/
# {
#   "login": "admin2",
#   "password": "123456"
# }
# {
#   "login": "admin1",
#   "password": "password123"
# }

#curl -X GET http://127.0.0.1:8000/admin/users/
#curl -X GET http://10.0.2.2:8000/admin/users/

#curl -X PUT -H "Content-Type: application/json" -d "{\"user_name\": \"New Name\", \"email\": \"newemail@example.com\", \"login\": \"newlogin\", \"password\": \"newpassword\", \"role\": \"user\"}" http://127.0.0.1:8000/api/users/23/
#телефон не обязателен так что и без него можно
#curl -X PUT -H "Content-Type: application/json" -d "{\"user_name\": \"New Name222\", \"email\": \"newemail@example.com\", \"login\": \"newlogin\", \"password\": \"newpassword\", \"role\": \"user\", \"phone_number\": \"+1000007890\"}" http://127.0.0.1:8000/api/users/33/

#curl -X GET "http://127.0.0.1:8000/admin/users/?user_name=User%202" фиьтрация
#curl -X GET "http://127.0.0.1:8000/admin/users/?role=user" фиьтрация  - тут лучше пример только юзеры вылетают

#curl -X GET "http://127.0.0.1:8000/admin/users/?user_name=John" - поиск
#curl -X GET "http://127.0.0.1:8000/admin/users/?user_name=User%202&role=admin" - поиск несколько параметров

#curl -X GET "http://127.0.0.1:8000/admin/users/?ordering=user_name" - сортировка по имени
#curl -X GET "http://127.0.0.1:8000/admin/users/?ordering=-user_name" - сортировка в обратном порядке

#curl -H "Authorization: Token 42cec6b3980891e666c057b43cc562f26896de62" http://127.0.0.1:8000/api/profile/
#{"user_id":3,"user_name":"Admin 2","email":"admin2@example.com","login":"admin2","phone_number":null,"role":"admin"}





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
from .models import User, Shipment, Product, WriteOffOfProducts, Extradition, ProductsCurrentQuantity
from .permissions import IsAdminOrReadOnly
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
    permission_classes = [IsAdminOrReadOnly]  # Применяем кастомное разрешение
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['user_name', 'email', 'phone_number']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['user_id']  # Сортировка по умолчанию
    pagination_class = None  # Отключаем пагинацию

class ShipmentViewSet(viewsets.ModelViewSet):
    queryset = Shipment.objects.all()
    serializer_class = ShipmentSerializer
    permission_classes = [IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['quantity']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['shipment_id']
    pagination_class = None  # Отключаем пагинацию

class ProductViewSet(viewsets.ModelViewSet):
    queryset = Product.objects.all()
    serializer_class = ProductSerializer
    permission_classes = [IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['product_name']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['product_id']
    pagination_class = None  # Отключаем пагинацию

class WriteOffOfProductsViewSet(viewsets.ModelViewSet):
    queryset = WriteOffOfProducts.objects.all()
    serializer_class = WriteOffOfProductsSerializer
    permission_classes = [IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['reason']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['id_product_write_off']
    pagination_class = None  # Отключаем пагинацию

class ExtraditionViewSet(viewsets.ModelViewSet):
    queryset = Extradition.objects.all()
    serializer_class = ExtraditionSerializer
    permission_classes = [IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['quantity']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['extradition_id']
    pagination_class = None  # Отключаем пагинацию

class ProductsCurrentQuantityViewSet(viewsets.ModelViewSet):
    queryset = ProductsCurrentQuantity.objects.all()
    serializer_class = ProductsCurrentQuantitySerializer
    permission_classes = [IsAdminOrReadOnly]
    filter_backends = [DjangoFilterBackend, SearchFilter, OrderingFilter]
    filterset_fields = '__all__'  # Фильтрация по всем полям
    search_fields = ['quantity']  # Поля для поиска
    ordering_fields = '__all__'  # Сортировка по всем полям
    ordering = ['product_current_quantity_id']
    pagination_class = None  # Отключаем пагинацию

