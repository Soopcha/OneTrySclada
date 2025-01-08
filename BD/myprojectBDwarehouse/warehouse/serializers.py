#Сериализаторы в Django REST Framework (DRF) отвечают за преобразование данных модели в формат,
# удобный для передачи по сети, и обратно. Они выполняют две основные функции:
#Конвертация объектов модели в JSON (или другой формат): Когда клиент (например, ваше Kotlin-приложение)
# отправляет HTTP-запрос к вашему API для получения данных, сериализатор преобразует объекты Django
# (например, User, Shipment, Product) в формат JSON. JSON — стандартный формат для передачи данных между
# клиентом и сервером. Это позволяет вашему приложению Kotlin легко читать данные, поступающие с сервера.
#Проверка и сохранение данных из запросов: Когда клиент отправляет данные на сервер
# (например, добавляет или обновляет запись в таблице), сериализатор проверяет эти данные и
# преобразует их обратно в объекты модели. Если данные корректны, они сохраняются в базе данных.
# Таким образом, сериализаторы помогают контролировать и обрабатывать данные, поступающие от клиента.

from rest_framework import serializers
from .models import *

class UserSerializer(serializers.ModelSerializer):
#serializers.ModelSerializer — это упрощен класс сериализатора, который автоматически создает поля на основе модели.
    class Meta:
        model = User
        fields = '__all__' #— указание, что нужно включить все поля модели.
        # Можно также указать конкретные поля, например, fields = ['field1', 'field2'].

class ShipmentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Shipment
        fields = '__all__'

class ProductSerializer(serializers.ModelSerializer):
    class Meta:
        model = Product
        fields = '__all__'

class WriteOffOfProductsSerializer(serializers.ModelSerializer):
    class Meta:
        model = WriteOffOfProducts
        fields = '__all__'

class ExtraditionSerializer(serializers.ModelSerializer):
    class Meta:
        model = Extradition
        fields = '__all__'

class ProductsCurrentQuantitySerializer(serializers.ModelSerializer):
    class Meta:
        model = ProductsCurrentQuantity
        fields = '__all__'