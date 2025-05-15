# warehouse/login_logout.py
from venv import logger

from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated

from django.contrib.auth import authenticate
from rest_framework.authtoken.models import Token
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status

class LoginView(APIView):
    authentication_classes = []  # Отключаем аутентификацию
    permission_classes = []  # Отключаем проверку разрешений

    def post(self, request):
        login = request.data.get('login')
        password = request.data.get('password')
        user = authenticate(username=login, password=password)  # Аутентификация
        if user:
            token, _ = Token.objects.get_or_create(user=user)
            return Response({'token': token.key}, status=status.HTTP_200_OK)
        return Response({'error': 'Invalid Credentials'}, status=status.HTTP_401_UNAUTHORIZED)

class LogoutView(APIView):
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        request.user.auth_token.delete()  # Удаляем токен
        return Response(status=status.HTTP_204_NO_CONTENT)

class ProfileView(APIView):
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def get(self, request):
        user = request.user  # Получаем текущего пользователя
        try:
            profile_data = {
                'user_id': user.user_id,
                'user_name': user.user_name,
                'email': user.email,
                'login': user.login,
                'phone_number': user.phone_number,
                'role': user.role,
            }
        except Exception as e:
            logger.error(f"Ошибка при сборке профиля: {e}")
            return Response({'error': 'Internal Server Error'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        return Response(profile_data, status=status.HTTP_200_OK)