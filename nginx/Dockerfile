FROM nginx:latest

# nginx.conf.template 복사
COPY ./nginx/nginx.conf.template /etc/nginx/nginx.conf.template

# 환경변수를 적용한 실제 nginx.conf 생성
CMD envsubst '$$SSH_HOST_PBL2' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && nginx -g 'daemon off;'
