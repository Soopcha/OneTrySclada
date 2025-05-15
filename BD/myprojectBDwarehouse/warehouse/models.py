from django.contrib.auth.models import AbstractUser, UserManager
from django.db import models

# Create your models here.
#ForeignKey - ManyToOneField и OneToManyField тк их двоих тут нет
#Django автоматически понимает, что если ты добавляешь ForeignKey, то речь идёт о связи
# "многие к одному" со стороны той модели, где стоит ForeignKey, и "один ко многим" со стороны связанной модели
# бывает что ошибка тогда надо почистить все миграции и удалить таблицу мб прошлую? или не надо

class CustomUserManager(UserManager):
    def _create_user(self, login, password, **extra_fields):
        """
        Переопределяем базовый метод, чтобы использовать login вместо username.
        """
        if not login:
            raise ValueError('The Login field must be set')
        extra_fields.setdefault('username', None)  # Устанавливаем username как None
        user = self.model(login=login, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_user(self, login, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', False)
        extra_fields.setdefault('is_superuser', False)
        return self._create_user(login, password, **extra_fields)

    def create_superuser(self, login, password=None, **extra_fields):
        extra_fields.setdefault('is_staff', True)
        extra_fields.setdefault('is_superuser', True)
        extra_fields.setdefault('role', 'admin')  # Автоматически устанавливаем role=admin для суперпользователя
        if extra_fields.get('is_staff') is not True:
            raise ValueError('Superuser must have is_staff=True.')
        if extra_fields.get('is_superuser') is not True:
            raise ValueError('Superuser must have is_superuser=True.')
        return self._create_user(login, password, **extra_fields)

class User(AbstractUser):
    user_id = models.AutoField(primary_key=True)
    user_name = models.CharField(max_length=100, null=False)
    email = models.EmailField(null=False)
    login = models.CharField(max_length=100, unique=True, null=False)
    phone_number = models.CharField(max_length=15, null=True)
    role = models.CharField(max_length=50, null=False)
    username = models.CharField(max_length=150, unique=True, null=True, blank=True)  # Необязательное поле

    objects = CustomUserManager()  # Используем кастомный менеджер

    USERNAME_FIELD = 'login'
    REQUIRED_FIELDS = ['user_name', 'email', 'role']

    def __str__(self):
        return f"User {self.user_id} - {self.user_name}, Email: {self.email}"

class Shipment(models.Model):  #поставка
    shipment_id = models.AutoField(primary_key=True)  # Первичный ключ
    quantity = models.IntegerField(null=False)  # Количество, NOT NULL
    date_of_shipment = models.DateField(null=False)  # Дата отгрузки, NOT NULL
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=False, blank=False, related_name='shipments')

class WriteOffOfProducts(models.Model):
    id_product_write_off = models.AutoField(primary_key=True)  # Первичный ключ
    product_write_off_date = models.DateField(null=False)  # Дата списания продукта, NOT NULL
    quantity = models.IntegerField(null=False)  # Количество списанного продукта, NOT NULL
    reason = models.CharField(max_length=255, null=False)  # Причина списания, NOT NULL
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=False, blank=False, related_name='write_offs')  # Внешний ключ на User
    #null=False тк WriteOffOfProducts относится к юзеру как 1 и обязательный
    #blank=False: Поле также должно быть заполнено в формах. Это гарантирует, что при создании или редактировании записи пользователь должен выбрать пользователя
    #а  чтобы не каждому юзеру был Write-off of products это не тут делается а потом
    #null=True было бы если WriteOffOfProducts FK user_id к User PK user_id и вроде blank=True,
    # blank=True что при создании или редактировании записи обязательно выбирается пользователь

class Extradition(models.Model):
    extradition_id = models.AutoField(primary_key=True)  # Первичный ключ
    date_of_extradition = models.DateField(null=False)  # Дата выдачи, NOT NULL
    quantity = models.IntegerField(null=False, default=0)  # Количество выданного, NOT NULL
    user = models.ForeignKey(User, on_delete=models.CASCADE, null=False, blank=False, related_name='extraditions')  # Внешний ключ на User


class Product(models.Model):  #1 конкретный продукт
    product_id = models.AutoField(primary_key=True)  # Явное указание первичного ключа
    # если ты объявляешь поле как primary_key=True, то оно автоматически считается NOT NULL.
    product_name = models.CharField(max_length=100, null=False)  # Название продукта, NOT NULL
    expire_date = models.DateField(null=False)  # Дата истечения срока годности, NOT NULL
    product_type = models.CharField(max_length=100, null=False)  # Тип продукта, NOT NULL
    manufacturer = models.CharField(max_length=100, null=False)  # Производитель, NOT NULL
    weight = models.FloatField(null=False)  # Вес, NOT NULL
    shipment = models.ForeignKey(Shipment, on_delete=models.CASCADE, null=False, blank=False, related_name='products')  # Внешний ключ на Product Много продуктов на одну отгрузку
    #те много Product могут быть связаны с одним Shipment
    write_off_of_products = models.ForeignKey(WriteOffOfProducts,on_delete=models.CASCADE, null=True, blank=True, related_name='products')
    extradition = models.ForeignKey(Extradition,on_delete=models.CASCADE, null=True, blank=True, related_name='products')
    #null=True: Поле может содержать NULL в базе данных, то есть оно может быть пустым на уровне базы данных.
    #blank=True: Поле может быть оставлено пустым в формах Django

class ProductsCurrentQuantity(models.Model):
    product_current_quantity_id = models.AutoField(primary_key=True)  # Первичный ключ
    quantity = models.IntegerField(null=False)  # Количество, NOT NUL
    product = models.OneToOneField(Product, on_delete=models.CASCADE,null=False, blank=False, related_name='current_quantity')  # Один к одному с Product
    #OneToOneField уже предполагает, что поле не может быть пустым.
    #Поле product в модели ProductsCurrentQuantity связано с таблицей Product,
    #а именно с полем product_id, которое является первичным ключом в таблице Product.
    # Связь через OneToOneField:
    #
    # Когда ты объявляешь product = models.OneToOneField(Product, on_delete=models.CASCADE, related_name='current_quantity'), это означает, что поле product в модели ProductsCurrentQuantity ссылается на экземпляр модели Product.
    # Django автоматически использует первичный ключ product_id из модели Product для этой связи.
    # Обратная связь:
    #
    # Благодаря related_name='current_quantity', ты можешь получить доступ к связанному объекту ProductsCurrentQuantity через экземпляр Product. Например, если у тебя есть объект product, ты можешь получить текущую запись о количестве через product.current_quantity.
    #
    # Вот как это будет выглядеть в коде:
    #
    # # Предположим, у нас есть объект product
    # product = Product.objects.get(product_id=1)  # Получаем продукт с ID 1
    #
    # # Создаем запись о текущем количестве
    # current_quantity = ProductsCurrentQuantity.objects.create(product=product, quantity=100)
    #
    # # Теперь можем получить текущее количество через product
    # quantity_info = product.current_quantity  # Это вернет объект ProductsCurrentQuantity, связанный с product
    # print(quantity_info.quantity)  # Выведет 100
    #
# myprojectBDwarehouse/
#│
#├──warehouse
#│   ├── __init__.py
#│   ├── admin.py
#│   ├── apps.py
#│   ├── migrations/
#│   ├── models.py
#│   ├── tests.py
#│   ├── views.py
#│   ├── populate_products.py  #  файл для заполнения таблицы
#│
#├── myprojectBDwarehouse/  # Директория проекта
#│   ├── __init__.py
#│   ├── settings.py
#│   ├── urls.py
#│   ├── wsgi.py
#│
#├── manage.py  # Командный файл для управления проектом
    #





