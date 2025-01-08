import os
import django
import random
from datetime import datetime, timedelta

# Настройка Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myprojectBDwarehouse.settings')
django.setup()

from warehouse.models import Product, Shipment, User, WriteOffOfProducts, Extradition, ProductsCurrentQuantity  # Импортируйте ваши модели


def generate_random_date(expiration_days=30):
    """Генерирует случайную дату истечения срока годности."""
    return datetime.now() + timedelta(days=random.randint(1, expiration_days))


def create_users(num_users=10):
    """Создает заданное количество пользователей."""
    existing_logins = set(User.objects.values_list('login', flat=True))  # Получаем существующие логины

    for _ in range(num_users):
        user_name = f"User {_ + 1}"
        email = f"user{_ + 1}@example.com"
        login = f"user{_ + 1}"

        # Убедитесь, что логин уникален
        while login in existing_logins:
            login = f"user{_ + 1}_{random.randint(1, 1000)}"  # Добавляем случайное число к логину

        password = f"password{_ + 1}"  # Храните пароли в хэшированном виде в реальных приложениях
        phone_number = f"+123456789{random.randint(0, 9)}"
        role = random.choice(['admin', 'user'])

        user = User(
            user_name=user_name,
            email=email,
            login=login,
            password=password,
            phone_number=phone_number,
            role=role
        )
        user.save()  # Сохранение пользователя в базе данных


def create_shipments(num_shipments=10):
    """Создает заданное количество поставок."""
    for _ in range(num_shipments):
        quantity = random.randint(1, 100)  # Случайное количество
        date_of_shipment = datetime.now().date()  # Текущая дата
        user = User.objects.order_by('?').first()  # Получаем случайного пользователя

        if user:  # Если найден пользователь
            shipment = Shipment(
                quantity=quantity,
                date_of_shipment=date_of_shipment,
                user=user,
            )
            shipment.save()  # Сохранение поставки в базе данных


def create_products(num_products=1000):
    """Создает заданное количество продуктов и их текущие количества."""
    for _ in range(num_products):
        product_name = f"Product {_ + 1}"
        expire_date = generate_random_date()
        product_type = random.choice(['Type A', 'Type B', 'Type C'])
        manufacturer = random.choice(['Manufacturer X', 'Manufacturer Y', 'Manufacturer Z'])
        weight = round(random.uniform(0.1, 100.0), 2)

        shipment = Shipment.objects.order_by('?').first()
        write_off = WriteOffOfProducts.objects.order_by('?').first()  # Случайное списание
        extradition = Extradition.objects.order_by('?').first()  # Случайная выдача

        if shipment and write_off:
            product = Product(
                product_name=product_name,
                expire_date=expire_date,
                product_type=product_type,
                manufacturer=manufacturer,
                weight=weight,
                shipment=shipment,
                write_off_of_products=write_off,
                extradition=extradition,
            )
            product.save()

            current_quantity = random.randint(1, 100)
            product_quantity = ProductsCurrentQuantity(
                quantity=current_quantity,
                product=product,
            )
            product_quantity.save()


# def create_products(num_products=1000):
#     """Создает заданное количество продуктов и их текущие количества."""
#     for _ in range(num_products):
#         product_name = f"Product {_ + 1}"
#         expire_date = generate_random_date()
#         product_type = random.choice(['Type A', 'Type B', 'Type C'])
#         manufacturer = random.choice(['Manufacturer X', 'Manufacturer Y', 'Manufacturer Z'])
#         weight = round(random.uniform(0.1, 100.0), 2)  # Случайный вес от 0.1 до 100.0
#
#         # Создайте запись о поставке для связи (или получите существующую)
#         shipment = Shipment.objects.order_by('?').first()  # Получает случайную поставку
#         if shipment:  # Если найдена поставка
#             product = Product(
#                 product_name=product_name,
#                 expire_date=expire_date,
#                 product_type=product_type,
#                 manufacturer=manufacturer,
#                 weight=weight,
#                 shipment=shipment,
#             )
#             product.save()  # Сохранение продукта в базе данных
#
#             # Генерация текущего количества для этого продукта
#             current_quantity = random.randint(1, 100)  # Случайное количество на текущий момент
#             product_quantity = ProductsCurrentQuantity(
#                 quantity=current_quantity,
#                 product=product,
#             )
#             product_quantity.save()  # Сохранение текущего количества в базе данных




def create_write_offs(num_write_offs=10):
    """Создает заданное количество списаний продуктов."""
    for i in range(num_write_offs):
        product_write_off_date = generate_random_date()
        quantity = random.randint(1, 20)
        reason = random.choice(["Просрочено", "Брак"])
        user = User.objects.order_by('?').first()

        if user:
            write_off = WriteOffOfProducts(
                product_write_off_date=product_write_off_date,
                quantity=quantity,
                reason=reason,
                user=user
            )
            write_off.save()


def create_extraditions(num_extraditions=10):
    """Создает заданное количество выдач продуктов."""
    for i in range(num_extraditions):
        date_of_extradition = generate_random_date()
        quantity = random.randint(1, 20)
        user = User.objects.order_by('?').first()

        if user:
            extradition = Extradition(
                date_of_extradition=date_of_extradition,
                quantity=quantity,
                user=user
            )
            extradition.save()


def clear_tables():
    """Очистка таблиц."""
    ProductsCurrentQuantity.objects.all().delete()  # Удаляем все записи из ProductsCurrentQuantity
    WriteOffOfProducts.objects.all().delete()
    Extradition.objects.all().delete()
    Product.objects.all().delete()                   # Удаляем все записи из Product
    Shipment.objects.all().delete()                  # Удаляем все записи из Shipment
    User.objects.all().delete()                      # Удаляем все записи из User


if __name__ == "__main__":
    clear_tables()
    create_users(10)           # Сначала создаем пользователей
    create_shipments(10)       # Затем создаем поставки
    create_write_offs(10)      # Создаем списания продуктов
    create_extraditions(10)    # Создаем выдачи продуктов
    create_products(100)       # И наконец, создаем продукты
