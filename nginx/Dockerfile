FROM nginx:latest

# nginx.conf.template 복사
COPY ./nginx.conf.template /etc/nginx/nginx.conf.template

# default.conf 삭제 후, 환경변수를 적용한 실제 nginx.conf 생성
CMD rm -f /etc/nginx/conf.d/default.conf && \
    envsubst '$$SSH_HOST_PBL2' < /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf && \
    nginx -g 'daemon off;'
