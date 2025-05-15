# warehouse/permissions.py
from rest_framework import permissions


class IsAdminOrReadOnly(permissions.BasePermission):
    def has_permission(self, request, view):
        # Аутентифицированные пользователи имеют доступ
        if not request.user.is_authenticated:
            return False

        # GET-запросы разрешены всем аутентифицированным
        if request.method in permissions.SAFE_METHODS:  # SAFE_METHODS = ['GET', 'HEAD', 'OPTIONS']
            return True

        # Для POST, PUT, DELETE нужен role='admin'
        return request.user.role == 'admin'