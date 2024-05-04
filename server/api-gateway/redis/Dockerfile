FROM redis:7.2.4-alpine

COPY conf/redis.conf /usr/local/etc/redis/redis.conf

ENV TZ Asia/Seoul

EXPOSE 6381

CMD redis-server /usr/local/etc/redis/redis.conf --requirepass $REDIS_PASSWORD