import os
import django
import random
from datetime import datetime, timedelta

# Настройка Django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'myprojectBDwarehouse.settings')
django.setup()

from warehouse.models import Product, Shipment, User, WriteOffOfProducts, Extradition, ProductsCurrentQuantity


def generate_random_date(start_days=-30, end_days=30):
    """Генерирует случайную дату в диапазоне от start_days до end_days относительно текущей даты."""
    return (datetime.now() + timedelta(days=random.randint(start_days, end_days))).date()


def create_users(num_users=10):
    """Создаёт заданное количество пользователей с хэшированными паролями."""
    existing_logins = set(User.objects.values_list('login', flat=True))

    for i in range(num_users):
        user_name = f"User {i + 1}"
        email = f"user{i + 1}@example.com"
        login = f"user{i + 1}"

        # Обеспечиваем уникальность логина
        while login in existing_logins:
            login = f"user{i + 1}_{random.randint(1, 1000)}"
        existing_logins.add(login)

        password = f"password{i + 1}"
        phone_number = f"+123456789{random.randint(0, 9)}"
        role = random.choice(['admin', 'user'])

        # Используем create_user для корректного хэширования пароля
        User.objects.create_user(
            login=login,
            password=password,
            user_name=user_name,
            email=email,
            phone_number=phone_number,
            role=role
        )
        print(f"Создан пользователь: {login}")


def create_shipments(num_shipments=10):
    """Создаёт заданное количество поставок."""
    if not User.objects.exists():
        print("Ошибка: Сначала создайте пользователей!")
        return

    for _ in range(num_shipments):
        quantity = random.randint(1, 100)
        date_of_shipment = generate_random_date()
        user = User.objects.order_by('?').first()

        shipment = Shipment(
            quantity=quantity,
            date_of_shipment=date_of_shipment,
            user=user
        )
        shipment.save()
        print(f"Создана поставка: ID {shipment.shipment_id}")


def create_write_offs(num_write_offs=10):
    """Создаёт заданное количество списаний продуктов."""
    if not User.objects.exists():
        print("Ошибка: Сначала создайте пользователей!")
        return

    for _ in range(num_write_offs):
        product_write_off_date = generate_random_date()
        quantity = random.randint(1, 20)
        reason = random.choice(["Просрочено", "Брак", "Повреждение"])
        user = User.objects.order_by('?').first()

        write_off = WriteOffOfProducts(
            product_write_off_date=product_write_off_date,
            quantity=quantity,
            reason=reason,
            user=user
        )
        write_off.save()
        print(f"Создано списание: ID {write_off.id_product_write_off}")


def create_extraditions(num_extraditions=10):
    """Создаёт заданное количество выдач продуктов."""
    if not User.objects.exists():
        print("Ошибка: Сначала создайте пользователей!")
        return

    for _ in range(num_extraditions):
        date_of_extradition = generate_random_date()
        quantity = random.randint(1, 20)
        user = User.objects.order_by('?').first()

        extradition = Extradition(
            date_of_extradition=date_of_extradition,
            quantity=quantity,
            user=user
        )
        extradition.save()
        print(f"Создана выдача: ID {extradition.extradition_id}")


def create_products(num_products=100):
    """Создаёт заданное количество продуктов и их текущие количества."""
    if not Shipment.objects.exists():
        print("Ошибка: Сначала создайте поставки!")
        return

    for i in range(num_products):
        product_name = f"Product {i + 1}"
        expire_date = generate_random_date(end_days=365)  # Срок годности до года
        product_type = random.choice(['Type A', 'Type B', 'Type C'])
        manufacturer = random.choice(['Manufacturer X', 'Manufacturer Y', 'Manufacturer Z'])
        weight = round(random.uniform(0.1, 100.0), 2)

        shipment = Shipment.objects.order_by('?').first()
        # Случайно выбираем, связывать ли с WriteOffOfProducts или Extradition
        write_off = WriteOffOfProducts.objects.order_by('?').first() if random.choice(
            [True, False]) and WriteOffOfProducts.objects.exists() else None
        extradition = Extradition.objects.order_by('?').first() if random.choice(
            [True, False]) and Extradition.objects.exists() else None

        product = Product(
            product_name=product_name,
            expire_date=expire_date,
            product_type=product_type,
            manufacturer=manufacturer,
            weight=weight,
            shipment=shipment,
            write_off_of_products=write_off,
            extradition=extradition
        )
        product.save()

        current_quantity = random.randint(1, 100)
        product_quantity = ProductsCurrentQuantity(
            quantity=current_quantity,
            product=product
        )
        product_quantity.save()
        print(f"Создан продукт: ID {product.product_id}")


def clear_tables():
    """Очищает все таблицы в правильном порядке, учитывая зависимости."""
    ProductsCurrentQuantity.objects.all().delete()
    Product.objects.all().delete()  # Сначала продукты, так как они зависят от других
    WriteOffOfProducts.objects.all().delete()
    Extradition.objects.all().delete()
    Shipment.objects.all().delete()
    User.objects.all().delete()
    print("Все таблицы очищены")


if __name__ == "__main__":
    # Очищаем таблицы перед созданием новых данных
    #clear_tables()

    # Создаём данные в правильной последовательности
    create_users(10)
    create_shipments(10)
    create_write_offs(10)
    create_extraditions(10)
    create_products(100)

    print("Заполнение базы данных завершено!")