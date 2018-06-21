FROM frolvlad/alpine-oraclejdk8

RUN echo "Asia/Shanghai" > /etc/timezone
COPY entrypoint.sh /root/entrypoint.sh
RUN chmod +x /root/entrypoint.sh
VOLUME /tmp

ENV PROJECT_NAME auth-service
ADD auth-service.war /auth-service.war

EXPOSE 22
CMD ["/root/entrypoint.sh"]
