FROM rabbitmq:management

ENV RABBITMQ_DEFAULT_USER=$RABBITMQ_USER \
    RABBITMQ_DEFAULT_PASS=$RABBITMQ_PASS
ENV TZ Asia/Seoul

RUN rabbitmq-plugins enable rabbitmq_management

EXPOSE 5672 15672

CMD ["rabbitmq-server"]