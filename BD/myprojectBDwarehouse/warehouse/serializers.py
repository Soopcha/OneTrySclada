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
    class Meta:
        model = User
        fields = ['user_id', 'user_name', 'email', 'login', 'phone_number', 'role', 'password']
        extra_kwargs = {
            'password': {'write_only': True},  # Пароль не возвращается в ответах
        }

    def create(self, validated_data):
        """
        Переопределяем метод create, чтобы хэшировать пароль при создании пользователя.
        """
        # Извлекаем пароль из данных
        password = validated_data.pop('password', None)
        # Создаём пользователя через create_user для корректного хэширования
        user = User.objects.create_user(**validated_data)
        if password:
            user.set_password(password)  # Хэшируем пароль
            user.save()  # Сохраняем изменения
        return user

    def update(self, instance, validated_data):
        """
        Переопределяем метод update, чтобы хэшировать пароль при обновлении.
        """
        # Извлекаем пароль из данных, если он есть
        password = validated_data.pop('password', None)
        # Обновляем остальные поля
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        # Если передан новый пароль, хэшируем его
        if password:
            instance.set_password(password)
        instance.save()
        return instance

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