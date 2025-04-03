FROM nginx:latest

# nginx.conf.template 복사
COPY nginx.conf.template /etc/nginx/nginx.conf.template

# React 빌드 파일을 Nginx 기본 디렉토리에 복사
COPY build/ /usr/share/nginx/html

# 환경변수를 적용한 실제 nginx.conf 생성
CMD envsubst '$$SSH_HOST_PBL2' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && nginx -g 'daemon off;'
